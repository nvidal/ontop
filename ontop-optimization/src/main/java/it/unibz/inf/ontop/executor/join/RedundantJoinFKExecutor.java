package it.unibz.inf.ontop.executor.join;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import it.unibz.inf.ontop.injection.IntermediateQueryFactory;
import it.unibz.inf.ontop.model.AtomPredicate;
import it.unibz.inf.ontop.model.DBMetadata;
import it.unibz.inf.ontop.model.Variable;
import it.unibz.inf.ontop.model.VariableOrGroundTerm;
import it.unibz.inf.ontop.pivotalrepr.*;
import it.unibz.inf.ontop.pivotalrepr.impl.NaiveVariableOccurrenceAnalyzerImpl;
import it.unibz.inf.ontop.pivotalrepr.impl.QueryTreeComponent;
import it.unibz.inf.ontop.pivotalrepr.proposal.InnerJoinOptimizationProposal;
import it.unibz.inf.ontop.pivotalrepr.proposal.InvalidQueryOptimizationProposalException;
import it.unibz.inf.ontop.pivotalrepr.proposal.NodeCentricOptimizationResults;
import it.unibz.inf.ontop.pivotalrepr.proposal.impl.NodeCentricOptimizationResultsImpl;
import it.unibz.inf.ontop.sql.*;
import it.unibz.inf.ontop.utils.ImmutableCollectors;

import java.util.AbstractMap.SimpleEntry;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Use foreign keys to remove some redundant inner joins
 *
 * Normalization assumption: variables are reused between data nodes (no explicit equality between variables)
 *
 */
public class RedundantJoinFKExecutor implements InnerJoinExecutor {

    private final IntermediateQueryFactory iqFactory;

    @Inject
    private RedundantJoinFKExecutor(IntermediateQueryFactory iqFactory) {
        this.iqFactory = iqFactory;
    }

    @Override
    public NodeCentricOptimizationResults<InnerJoinNode> apply(InnerJoinOptimizationProposal proposal,
                                                               IntermediateQuery query,
                                                               QueryTreeComponent treeComponent)
            throws InvalidQueryOptimizationProposalException, EmptyQueryException {

        InnerJoinNode joinNode = proposal.getFocusNode();
        ImmutableMultimap<DatabaseRelationDefinition, DataNode> dataNodeMap = extractDataNodeMap(query, joinNode);

        ImmutableSet<DataNode> nodesToRemove = findRedundantNodes(query, joinNode, dataNodeMap);

        if (!nodesToRemove.isEmpty()) {

            NodeCentricOptimizationResults<InnerJoinNode> result = applyOptimization(query, treeComponent,
                    joinNode, nodesToRemove);

            return result;
        }
        /**
         * No change
         */
        else {
            return new NodeCentricOptimizationResultsImpl<>(query, joinNode);
        }
    }

    private NodeCentricOptimizationResults<InnerJoinNode> applyOptimization(IntermediateQuery query,
                                                                            QueryTreeComponent treeComponent,
                                                                            InnerJoinNode joinNode,
                                                                            ImmutableSet<DataNode> nodesToRemove) {

        /**
         * First removes all the redundant nodes
         */
        nodesToRemove.stream()
                .forEach(treeComponent::removeSubTree);

        /**
         * Then replaces the join node if needed
         */
        switch (query.getChildren(joinNode).size()) {
            case 0:
                throw new IllegalStateException("Redundant join elimination should not eliminate all the children");
            case 1:
                QueryNode replacingChild = query.getFirstChild(joinNode).get();

                if (joinNode.getOptionalFilterCondition().isPresent()) {
                    FilterNode newFilterNode = iqFactory.createFilterNode(joinNode.getOptionalFilterCondition().get());
                    treeComponent.replaceNode(joinNode, newFilterNode);
                    /**
                     * NB: the filter node is not declared as the replacing node but the child is.
                     * Why? Because a JOIN with a filtering condition could decomposed into two different nodes.
                     */
                }
                else {
                    treeComponent.replaceNodeByChild(joinNode, Optional.empty());
                }
                return new NodeCentricOptimizationResultsImpl<>(query, Optional.of(replacingChild));

            default:
                return new NodeCentricOptimizationResultsImpl<>(query, joinNode);
        }
    }

    /**
     * Predicates not having a DatabaseRelationDefinition are ignored
     */
    private ImmutableMultimap<DatabaseRelationDefinition, DataNode> extractDataNodeMap(IntermediateQuery query,
                                                                                       InnerJoinNode joinNode) {

        DBMetadata dbMetadata = query.getDBMetadata();

        return query.getChildren(joinNode).stream()
                .filter(c -> c instanceof DataNode)
                .map(c -> (DataNode) c)
                .map(c -> getDatabaseRelationByName(dbMetadata, c.getProjectionAtom().getPredicate())
                        .map(r -> new SimpleEntry<DatabaseRelationDefinition, DataNode>(r, c)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(ImmutableCollectors.toMultimap());
    }

    private ImmutableSet<DataNode> findRedundantNodes(IntermediateQuery query, InnerJoinNode joinNode,
                                                      ImmutableMultimap<DatabaseRelationDefinition, DataNode> dataNodeMap) {
        return dataNodeMap.keySet().stream()
                .flatMap(r -> r.getForeignKeys().stream()
                        .flatMap(c -> selectRedundantNodesForConstraint(r, c, query, joinNode, dataNodeMap)))
                .collect(ImmutableCollectors.toSet());
    }

    /**
     * TODO: explain
     */
    private Stream<DataNode> selectRedundantNodesForConstraint(DatabaseRelationDefinition sourceRelation,
                                                               ForeignKeyConstraint constraint,
                                                               IntermediateQuery query,
                                                               InnerJoinNode joinNode,
                                                               ImmutableMultimap<DatabaseRelationDefinition, DataNode> dataNodeMap) {
        /**
         * "Target" data nodes === "referenced" data nodes
         */
        ImmutableCollection<DataNode> targetDataNodes = dataNodeMap.get(constraint.getReferencedRelation());

        /**
         * No optimization possible
         */
        if (targetDataNodes.isEmpty()) {
            return Stream.empty();
        }

        return dataNodeMap.get(sourceRelation).stream()
                .flatMap(s -> targetDataNodes.stream()
                        .filter(t -> areMatching(s,t,constraint)))
                .distinct()
                .filter(t -> areNonFKColumnsUnused(t, query, constraint));
    }

    /**
     *
     * TODO: explain
     */
    private boolean areMatching(DataNode sourceDataNode, DataNode targetDataNode, ForeignKeyConstraint constraint) {

        ImmutableList<? extends VariableOrGroundTerm> sourceArguments = sourceDataNode.getProjectionAtom().getArguments();
        ImmutableList<? extends VariableOrGroundTerm> targetArguments = targetDataNode.getProjectionAtom().getArguments();

        return constraint.getComponents().stream()
                .allMatch(c -> sourceArguments.get(c.getAttribute().getIndex() - 1)
                        .equals(targetArguments.get(c.getReference().getIndex() - 1)));
    }

    /**
     * TODO: explain
     */
    private boolean areNonFKColumnsUnused(DataNode targetDataNode, IntermediateQuery query,
                                          ForeignKeyConstraint constraint) {

        ImmutableList<? extends VariableOrGroundTerm> targetArguments = targetDataNode.getProjectionAtom().getArguments();

        ImmutableSet<Integer> fkTargetIndexes = constraint.getComponents().stream()
                .map(c -> c.getReference().getIndex() - 1)
                .collect(ImmutableCollectors.toSet());

        /**
         * Terms appearing in non-FK positions
         */
        ImmutableList<VariableOrGroundTerm> remainingTerms = IntStream.range(0, targetArguments.size())
                .filter(i -> !fkTargetIndexes.contains(i))
                .boxed()
                .map(targetArguments::get)
                .collect(ImmutableCollectors.toList());

        /**
         * Check usage in the data atom.
         *
         * 1 - They should all variables
         * 2 - They should be no duplicate
         * 3 - They must be distinct from the FK target terms
         */
        if ((!remainingTerms.stream().allMatch(t -> t instanceof Variable))
            || (ImmutableSet.copyOf(remainingTerms).size() < remainingTerms.size())
            || fkTargetIndexes.stream()
                .map(targetArguments::get)
                .anyMatch(remainingTerms::contains))
            return false;

        /**
         * Check that the remaining variables are not used anywhere else
         */
        // TODO: use a more efficient implementation
        VariableOccurrenceAnalyzer analyzer = new NaiveVariableOccurrenceAnalyzerImpl();

        return remainingTerms.stream()
                .map(v -> (Variable) v)
                .allMatch(v -> ! analyzer.isVariableUsedSomewhereElse(query, targetDataNode, v));
    }

    private Optional<DatabaseRelationDefinition> getDatabaseRelationByName(DBMetadata dbMetadata, AtomPredicate predicate) {

        RelationID relationId = Relation2DatalogPredicate.createRelationFromPredicateName(dbMetadata.getQuotedIDFactory(),
                predicate);

        return Optional.ofNullable(dbMetadata.getRelation(relationId))
                /**
                 * Here we only consider DB relations
                 */
                .filter(r -> r instanceof DatabaseRelationDefinition)
                .map(r -> (DatabaseRelationDefinition) r);
    }
}