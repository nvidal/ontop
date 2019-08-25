package it.unibz.inf.ontop.iq.executor.join;

import com.google.common.collect.*;
import com.google.inject.Inject;
import it.unibz.inf.ontop.dbschema.*;
import it.unibz.inf.ontop.injection.IntermediateQueryFactory;
import it.unibz.inf.ontop.iq.exception.EmptyQueryException;
import it.unibz.inf.ontop.iq.node.*;
import it.unibz.inf.ontop.iq.tools.VariableOccurrenceAnalyzer;
import it.unibz.inf.ontop.model.term.Variable;
import it.unibz.inf.ontop.model.term.VariableOrGroundTerm;
import it.unibz.inf.ontop.iq.*;
import it.unibz.inf.ontop.iq.tools.impl.NaiveVariableOccurrenceAnalyzerImpl;
import it.unibz.inf.ontop.iq.impl.QueryTreeComponent;
import it.unibz.inf.ontop.iq.proposal.InnerJoinOptimizationProposal;
import it.unibz.inf.ontop.iq.exception.InvalidQueryOptimizationProposalException;
import it.unibz.inf.ontop.iq.proposal.NodeCentricOptimizationResults;
import it.unibz.inf.ontop.iq.proposal.impl.NodeCentricOptimizationResultsImpl;
import it.unibz.inf.ontop.utils.ImmutableCollectors;

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
        ImmutableMultimap<RelationDefinition, ExtensionalDataNode> dataNodeMap = extractDataNodeMap(query, joinNode);

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

        /*
         * First removes all the redundant nodes
         */
        nodesToRemove
                .forEach(treeComponent::removeSubTree);

        /*
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
                    /*
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
    private ImmutableMultimap<RelationDefinition, ExtensionalDataNode> extractDataNodeMap(IntermediateQuery query,
                                                                                          InnerJoinNode joinNode) {
        return query.getChildren(joinNode).stream()
                .filter(c -> c instanceof ExtensionalDataNode)
                .map(c -> (ExtensionalDataNode) c)
                .map(c -> Maps.immutableEntry(c.getProjectionAtom().getPredicate().getRelationDefinition(), c))
                .collect(ImmutableCollectors.toMultimap());
    }

    private ImmutableSet<DataNode> findRedundantNodes(IntermediateQuery query, InnerJoinNode joinNode,
                                                      ImmutableMultimap<RelationDefinition, ExtensionalDataNode> dataNodeMap) {
        return dataNodeMap.keySet().stream()
                .flatMap(r -> r.getForeignKeys().stream()
                        .flatMap(c -> selectRedundantNodesForConstraint(r, c, query, joinNode, dataNodeMap)))
                .collect(ImmutableCollectors.toSet());
    }

    /**
     * TODO: explain
     */
    private Stream<ExtensionalDataNode> selectRedundantNodesForConstraint(RelationDefinition sourceRelation,
                                                               ForeignKeyConstraint constraint,
                                                               IntermediateQuery query,
                                                               InnerJoinNode joinNode,
                                                               ImmutableMultimap<RelationDefinition, ExtensionalDataNode> dataNodeMap) {
        /**
         * "Target" data nodes === "referenced" data nodes
         */
        ImmutableCollection<ExtensionalDataNode> targetDataNodes = dataNodeMap.get(constraint.getReferencedRelation());

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
    private boolean areMatching(ExtensionalDataNode sourceDataNode, ExtensionalDataNode targetDataNode, ForeignKeyConstraint constraint) {

        ImmutableList<? extends VariableOrGroundTerm> sourceArguments = sourceDataNode.getProjectionAtom().getArguments();
        ImmutableList<? extends VariableOrGroundTerm> targetArguments = targetDataNode.getProjectionAtom().getArguments();

        return constraint.getComponents().stream()
                .allMatch(c -> sourceArguments.get(c.getAttribute().getIndex() - 1)
                        .equals(targetArguments.get(c.getReference().getIndex() - 1)));
    }

    /**
     * TODO: explain
     */
    private boolean areNonFKColumnsUnused(ExtensionalDataNode targetDataNode, IntermediateQuery query,
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
                .noneMatch(v -> analyzer.isVariableUsedSomewhereElse(query, targetDataNode, v));
    }
}
