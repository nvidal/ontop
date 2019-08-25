package it.unibz.inf.ontop.iq.transformer.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import it.unibz.inf.ontop.exception.OntopInternalBugException;
import it.unibz.inf.ontop.injection.IntermediateQueryFactory;
import it.unibz.inf.ontop.iq.IQTree;
import it.unibz.inf.ontop.iq.UnaryIQTree;
import it.unibz.inf.ontop.iq.node.*;
import it.unibz.inf.ontop.iq.transform.IQTreeTransformer;
import it.unibz.inf.ontop.iq.transform.impl.ChildTransformer;
import it.unibz.inf.ontop.iq.transform.impl.CompositeIQTreeTransformer;
import it.unibz.inf.ontop.iq.transform.impl.DefaultNonRecursiveIQTreeTransformer;
import it.unibz.inf.ontop.iq.transformer.ExplicitEqualityTransformer;
import it.unibz.inf.ontop.model.atom.AtomFactory;
import it.unibz.inf.ontop.model.atom.AtomPredicate;
import it.unibz.inf.ontop.model.atom.DataAtom;
import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.substitution.ImmutableSubstitution;
import it.unibz.inf.ontop.substitution.InjectiveVar2VarSubstitution;
import it.unibz.inf.ontop.substitution.SubstitutionFactory;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import it.unibz.inf.ontop.utils.VariableGenerator;

import java.util.*;
import java.util.stream.Stream;

import static it.unibz.inf.ontop.model.term.functionsymbol.ExpressionOperation.AND;
import static it.unibz.inf.ontop.model.term.functionsymbol.ExpressionOperation.EQ;

public class ExplicitEqualityTransformerImpl implements ExplicitEqualityTransformer {

    private final IntermediateQueryFactory iqFactory;
    private final AtomFactory atomFactory;
    private final TermFactory termFactory;
    private final VariableGenerator variableGenerator;
    private final SubstitutionFactory substitutionFactory;
    private final CompositeIQTreeTransformer compositeTransformer;

    @AssistedInject
    public ExplicitEqualityTransformerImpl(@Assisted VariableGenerator variableGenerator,
                                           IntermediateQueryFactory iqFactory,
                                           AtomFactory atomFactory,
                                           TermFactory termFactory,
                                           SubstitutionFactory substitutionFactory) {
        this.iqFactory = iqFactory;
        this.atomFactory = atomFactory;
        this.termFactory = termFactory;
        this.variableGenerator = variableGenerator;
        this.substitutionFactory = substitutionFactory;
        ImmutableList<IQTreeTransformer> preTransformers = ImmutableList.of(new LocalExplicitEqualityEnforcer());
        ImmutableList<IQTreeTransformer> postTransformers = ImmutableList.of(new CnLifter(), new FilterChildNormalizer(iqFactory));
        this.compositeTransformer = new CompositeIQTreeTransformer(preTransformers, postTransformers, iqFactory);
    }

    @Override
    public IQTree transform(IQTree tree) {
          return compositeTransformer.transform(tree);
    }

    /**
     * Affects (left) joins and data nodes.
     * - left join: if the same variable is returned by both operands (implicit equality),
     * rename it (with a fresh variable each time) in each branch but the left one,
     * and make the corresponding equalities explicit.
     * - inner join: identical to left join, but renaming is performed in each branch but the first where the variable appears
     * - data node: if the data node contains a ground term, create a variable and make the equality explicit (create a filter).
     *
     * If needed, creates a root projection to ensure that the transformed query has the same signature as the input one
     */
    class LocalExplicitEqualityEnforcer extends DefaultNonRecursiveIQTreeTransformer {

        @Override
        public IQTree transformIntensionalData(IntensionalDataNode dn) {
            return transformDataNode(dn);
        }

        @Override
        public IQTree transformExtensionalData(ExtensionalDataNode dn) {
            return transformDataNode(dn);
        }

        @Override
        public IQTree transformInnerJoin(IQTree tree, InnerJoinNode rootNode, ImmutableList<IQTree> children) {
            ImmutableList<InjectiveVar2VarSubstitution> substitutions = computeSubstitutions(children);
            if (substitutions.stream().allMatch(ImmutableSubstitution::isEmpty))
                return tree;

            ImmutableList<IQTree> updatedChildren = updateJoinChildren(substitutions, children);
            return iqFactory.createUnaryIQTree(
                    iqFactory.createConstructionNode(tree.getVariables()),
                    iqFactory.createNaryIQTree(
                            iqFactory.createInnerJoinNode(
                                    Optional.of(updateJoinCondition(
                                            rootNode.getOptionalFilterCondition(),
                                            substitutions ))),
                            updatedChildren
                    ));
        }

        @Override
        public IQTree transformLeftJoin(IQTree tree, LeftJoinNode rootNode, IQTree leftChild, IQTree rightChild) {
            ImmutableList<IQTree> children = ImmutableList.of(leftChild, rightChild);
            ImmutableList<InjectiveVar2VarSubstitution> substitutions = computeSubstitutions(children);
            if (substitutions.stream().allMatch(ImmutableSubstitution::isEmpty))
                return tree;

           ImmutableList<IQTree> updatedChildren = updateJoinChildren(substitutions, children);


           return iqFactory.createUnaryIQTree(
                   iqFactory.createConstructionNode(tree.getVariables()),
                   iqFactory.createBinaryNonCommutativeIQTree(
                           iqFactory.createLeftJoinNode(
                                   Optional.of(
                                           updateJoinCondition(
                                                   rootNode.getOptionalFilterCondition(),
                                                   substitutions
                                           ))),
                           updatedChildren.get(0),
                           updatedChildren.get(1)
                   ));
        }

        private ImmutableList<InjectiveVar2VarSubstitution> computeSubstitutions(ImmutableList<IQTree> children) {
            if (children.size() < 2) {
                throw new ExplicitEqualityTransformerInternalException("At least 2 children are expected");
            }
            ImmutableSet<Variable> repeatedVariables = children.stream()
                    .flatMap(t -> t.getVariables().stream())
                    .collect(ImmutableCollectors.toMultiset()).entrySet().stream()
                    .filter(e -> e.getCount() > 1)
                    .map(Multiset.Entry::getElement)
                    .collect(ImmutableCollectors.toSet());

            return children.stream().sequential()
                    .map(t -> computeSubstitution(
                            repeatedVariables,
                            children,
                            t
                    ))
                    .collect(ImmutableCollectors.toList());
        }

        private ImmutableList<IQTree> updateJoinChildren(ImmutableList<InjectiveVar2VarSubstitution> substitutions, ImmutableList<IQTree> children) {
            Iterator<IQTree> it = children.iterator();
            return substitutions.stream().sequential()
                    .map(s -> it.next().applyDescendingSubstitutionWithoutOptimizing(s))
                    .collect(ImmutableCollectors.toList());
        }

        private InjectiveVar2VarSubstitution computeSubstitution(ImmutableSet<Variable> repeatedVars, ImmutableList<IQTree> children, IQTree tree) {
            return substitutionFactory.getInjectiveVar2VarSubstitution(
                    tree.getVariables().stream()
                            .filter(repeatedVars::contains)
                            .filter(v -> !isFirstOcc(v, children, tree))
                            .collect(ImmutableCollectors.toMap(
                                    v -> v,
                                    variableGenerator::generateNewVariableFromVar
                            )));
        }

        private boolean isFirstOcc(Variable variable, ImmutableList<IQTree> children, IQTree tree) {
            return children.stream().sequential()
                    .filter(t -> t.getVariables().contains(variable))
                    .findFirst().get()
                    .equals(tree);
        }

        private ImmutableExpression updateJoinCondition(Optional<ImmutableExpression> optionalFilterCondition, ImmutableList<InjectiveVar2VarSubstitution> substitutions) {
            Stream<ImmutableExpression> varEqualities = extractEqualities(substitutions);
            return getConjunction(optionalFilterCondition, varEqualities);
        }

        private Stream<ImmutableExpression> extractEqualities(ImmutableList<InjectiveVar2VarSubstitution> substitutions) {
            return substitutions.stream()
                    .flatMap(s -> s.getImmutableMap().entrySet().stream())
                    .map(e -> termFactory.getImmutableExpression(
                            EQ,
                            e.getKey(),
                            e.getValue()
                    ));
        }

        private IQTree transformDataNode(DataNode dn) {
            ImmutableList<Optional<Variable>> replacementVars = getArgumentReplacement(dn);

            if (empt(replacementVars))
                return dn;

            FilterNode filter = createFilter(dn.getProjectionAtom(), replacementVars);
            DataAtom atom = replaceVars(dn.getProjectionAtom(), replacementVars);
            return iqFactory.createUnaryIQTree(
                    iqFactory.createConstructionNode(dn.getVariables()),
                    iqFactory.createUnaryIQTree(
                            filter,
                            dn.newAtom(atom))
            );
        }

        private boolean empt(ImmutableList<Optional<Variable>> replacementVars) {
            return replacementVars.stream()
                    .noneMatch(Optional::isPresent);
        }

        private <P extends AtomPredicate> DataAtom<P> replaceVars(DataAtom<P> projectionAtom, ImmutableList<Optional<Variable>> replacements) {
            Iterator<Optional<Variable>> it = replacements.iterator();
            return atomFactory.getDataAtom(
                    projectionAtom.getPredicate(),
                    projectionAtom.getArguments().stream()
                            .map(a -> {
                                Optional<Variable> r = it.next();
                                return r.isPresent() ?
                                        r.get() :
                                        a;
                            })
                            .collect(ImmutableCollectors.toList())
            );
        }

        private ImmutableList<Optional<Variable>> getArgumentReplacement(DataNode dn) {
            Set<Variable> vars = new HashSet<>();
            List<Optional<Variable>> replacements = new ArrayList<>();
            for (VariableOrGroundTerm term: (ImmutableList<VariableOrGroundTerm>) dn.getProjectionAtom().getArguments()) {
                if (term instanceof GroundTerm) {
                    replacements.add(Optional.of(variableGenerator.generateNewVariable()));
                } else if (term instanceof Variable) {
                    Variable var = (Variable) term;
                    if (vars.contains(var)) {
                        replacements.add(Optional.of(variableGenerator.generateNewVariableFromVar(var)));
                    } else {
                        replacements.add(Optional.empty());
                        vars.add(var);
                    }
                }
            }
            return ImmutableList.copyOf(replacements);
        }

        private FilterNode createFilter(DataAtom da, ImmutableList<Optional<Variable>> replacementVars) {
            Iterator<Optional<Variable>> it = replacementVars.iterator();
            return iqFactory.createFilterNode(
                    getConjunction(da.getArguments().stream()
                            .map(a -> getEquality((VariableOrGroundTerm) a, it.next()))
                            .filter(e -> ((Optional) e).isPresent())
                            .map(e -> ((Optional) e).get())
                    ));
        }

        private Optional<ImmutableExpression> getEquality(VariableOrGroundTerm t, Optional<Variable> replacement) {
            return replacement
                    .map(variable -> termFactory.getImmutableExpression(
                            EQ,
                            t,
                            variable
                    ));
        }
    }


    /**
     * Affects each outermost filter or (left) join n in the tree.
     * For each child of n, deletes its root if it is a filter node.
     * Then:
     * - if n is a join or filter: merge the boolean expressions
     * - if n is a left join: merge boolean expressions coming from the right, and lift the ones coming from the left.
     * This lift is only performed for optimization purposes: may save a subquery during SQL generation.
     */
    class FilterChildNormalizer extends DefaultNonRecursiveIQTreeTransformer {

        private final ChildTransformer childTransformer;

        public FilterChildNormalizer(IntermediateQueryFactory iqFactory) {
            this.childTransformer = new ChildTransformer(iqFactory, this);
        }

        @Override
        public IQTree transformLeftJoin(IQTree tree, LeftJoinNode rootNode, IQTree leftChild, IQTree rightChild) {

            Optional<ImmutableExpression> leftChildChildExpression = getOptionalChildExpression(leftChild);
            Optional<ImmutableExpression> rightChildExpression = getOptionalChildExpression(rightChild);

            if(leftChildChildExpression.isPresent() || rightChildExpression.isPresent()) {
                IQTree leftJoinTree = iqFactory.createBinaryNonCommutativeIQTree(
                        rightChildExpression.isPresent() ?
                                iqFactory.createLeftJoinNode(
                                        Optional.of(
                                                updateJoinCondition(
                                                        rootNode.getOptionalFilterCondition(),
                                                        ImmutableList.of(rightChildExpression.get())
                                                ))) :
                                rootNode,
                        trimRootFilter(leftChild),
                        trimRootFilter(rightChild)
                );
                return leftChildChildExpression.isPresent() ?
                        iqFactory.createUnaryIQTree(iqFactory.createFilterNode(leftChildChildExpression.get()), leftJoinTree) :
                        leftJoinTree;
            }
            return tree;
        }

        @Override
        public IQTree transformInnerJoin(IQTree tree, InnerJoinNode rootNode, ImmutableList<IQTree> children) {
            ImmutableList<ImmutableExpression> filterChildExpressions = getChildExpressions(children);
            if (filterChildExpressions.isEmpty())
                return tree;

            return iqFactory.createNaryIQTree(
                    iqFactory.createInnerJoinNode(
                            Optional.of(
                                    updateJoinCondition(
                                            rootNode.getOptionalFilterCondition(),
                                            filterChildExpressions
                                    ))),
                    children.stream()
                            .map(this::trimRootFilter)
                            .collect(ImmutableCollectors.toList())
            );
        }

        @Override
        public IQTree transformFilter(IQTree tree, FilterNode rootNode, IQTree child) {
            ImmutableList<ImmutableExpression> filterChildExpressions = getChildExpressions(ImmutableList.of(child));
            if (filterChildExpressions.isEmpty())
                return tree;
            return iqFactory.createUnaryIQTree(
                    iqFactory.createFilterNode(
                            updateJoinCondition(
                                    Optional.of(rootNode.getFilterCondition()),
                                    filterChildExpressions
                            )),
                    trimRootFilter(child)
            );
        }

        private ImmutableList<ImmutableExpression> getChildExpressions(ImmutableList<IQTree> children) {
            return children.stream()
                    .filter(t -> t.getRootNode() instanceof FilterNode)
                    .map(t -> ((FilterNode) t.getRootNode()).getFilterCondition())
                    .collect(ImmutableCollectors.toList());
        }

        private Optional<ImmutableExpression> getOptionalChildExpression(IQTree child) {
            QueryNode root = child.getRootNode();
            return root instanceof FilterNode?
                    Optional.of(((FilterNode) root).getFilterCondition()):
                    Optional.empty();

        }

        private IQTree trimRootFilter(IQTree tree) {
            return tree.getRootNode() instanceof FilterNode ?
                    ((UnaryIQTree) tree).getChild() :
                    tree;
        }

        private ImmutableExpression updateJoinCondition(Optional<ImmutableExpression> joinCondition, ImmutableList<ImmutableExpression> additionalConditions) {
            if (additionalConditions.isEmpty())
                throw new ExplicitEqualityTransformerInternalException("Nonempty list of filters expected");
            return getConjunction(joinCondition, additionalConditions.stream());
        }

        protected IQTree transformUnaryNode(IQTree tree, UnaryOperatorNode rootNode, IQTree child) {
            return childTransformer.transform(tree);
        }

        protected IQTree transformNaryCommutativeNode(IQTree tree, NaryOperatorNode rootNode, ImmutableList<IQTree> children) {
            return childTransformer.transform(tree);
        }

        protected IQTree transformBinaryNonCommutativeNode(IQTree tree, BinaryNonCommutativeOperatorNode rootNode, IQTree leftChild, IQTree rightChild) {
            return childTransformer.transform(tree);
        }
    }

    /**
     * - Default behavior: for each child, deletes its root if it is a substitution-free construction node (i.e. a simple projection),
     * and lift the projection if needed
     * - Distinct or slice nodes: does not apply
     */
    class CnLifter extends DefaultNonRecursiveIQTreeTransformer {

        @Override
        public IQTree transformDistinct(IQTree tree, DistinctNode rootNode, IQTree child) {
            return tree;
        }

        @Override
        public IQTree transformSlice(IQTree tree, SliceNode rootNode, IQTree child) {
            return tree;
        }

        @Override
        public IQTree transformUnaryNode(IQTree tree, UnaryOperatorNode rootNode, IQTree child) {
            ImmutableList<ConstructionNode> idleCns = getIdleCns(Stream.of(child));
            return idleCns.isEmpty() ?
                    tree :
                    getProjection(
                            tree.getVariables(),
                            iqFactory.createUnaryIQTree(
                                    rootNode,
                                    trimIdleCn(child)
                    ));
        }

        @Override
        protected IQTree transformNaryCommutativeNode(IQTree tree, NaryOperatorNode rootNode, ImmutableList<IQTree> children) {
            ImmutableList<ConstructionNode> idleCns = getIdleCns(children.stream());
            return idleCns.isEmpty() ?
                    tree :
                    getProjection(
                            tree.getVariables(),
                            iqFactory.createNaryIQTree(
                                    rootNode,
                                    children.stream()
                                            .map(this::trimIdleCn)
                                            .collect(ImmutableCollectors.toList())
                            ));
        }

        @Override
        protected IQTree transformBinaryNonCommutativeNode(IQTree tree, BinaryNonCommutativeOperatorNode rootNode, IQTree leftChild, IQTree rightChild) {
            ImmutableList<ConstructionNode> idleCns = getIdleCns(Stream.of(leftChild, rightChild));
            return idleCns.isEmpty() ?
                    tree :
                    getProjection(
                            tree.getVariables(),
                            iqFactory.createBinaryNonCommutativeIQTree(
                                    rootNode,
                                    trimIdleCn(leftChild),
                                    trimIdleCn(rightChild)
                            ));
        }

        private ImmutableList<ConstructionNode> getIdleCns(Stream<IQTree> trees) {
            return trees
                    .map(this::getIdleCn)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(ImmutableCollectors.toList());
        }

        private Optional<ConstructionNode> getIdleCn(IQTree tree) {
            QueryNode root = tree.getRootNode();
            if (root instanceof ConstructionNode) {
                ConstructionNode cn = ((ConstructionNode) root);
                if (cn.getSubstitution().isEmpty()) {
                    return Optional.of(cn);
                }
            }
            return Optional.empty();
        }

        private IQTree trimIdleCn(IQTree tree) {
            return getIdleCn(tree).isPresent() ?
                    ((UnaryIQTree) tree).getChild() :
                    tree;
        }

        private IQTree getProjection(ImmutableSet<Variable> signature, IQTree tree) {
            QueryNode root = tree.getRootNode();
            if (root instanceof ConstructionNode){
                return iqFactory.createUnaryIQTree(
                        iqFactory.createConstructionNode(signature, ((ConstructionNode)root).getSubstitution()),
                        ((UnaryIQTree)tree).getChild()
                );
            }
            return iqFactory.createUnaryIQTree(
                    iqFactory.createConstructionNode(signature),
                    tree
            );
        }
    }

    private ImmutableExpression getConjunction(Stream<ImmutableExpression> expressions) {
        return expressions
                .reduce(null,
                        (a, b) -> (a == null) ?
                                b :
                                termFactory.getImmutableExpression(
                                        AND,
                                        a,
                                        b
                                ));
    }

    private ImmutableExpression getConjunction(Optional<ImmutableExpression> optExpression, Stream<ImmutableExpression> expressions) {
        return getConjunction(optExpression.isPresent() ?
                Stream.concat(
                        Stream.of(optExpression.get()),
                        expressions
                ) :
                expressions
        );
    }

    private class ExplicitEqualityTransformerInternalException extends OntopInternalBugException {
        ExplicitEqualityTransformerInternalException(String message) {
            super(message);
        }
    }
}
