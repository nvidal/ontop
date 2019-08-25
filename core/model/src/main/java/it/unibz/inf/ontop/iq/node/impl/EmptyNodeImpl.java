package it.unibz.inf.ontop.iq.node.impl;

import com.google.common.collect.ImmutableSet;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import it.unibz.inf.ontop.injection.IntermediateQueryFactory;
import it.unibz.inf.ontop.iq.exception.InvalidIntermediateQueryException;
import it.unibz.inf.ontop.iq.exception.QueryNodeTransformationException;
import it.unibz.inf.ontop.iq.impl.IQTreeTools;
import it.unibz.inf.ontop.iq.node.*;
import it.unibz.inf.ontop.iq.transform.IQTreeVisitingTransformer;
import it.unibz.inf.ontop.model.term.VariableOrGroundTerm;
import it.unibz.inf.ontop.substitution.ImmutableSubstitution;
import it.unibz.inf.ontop.model.term.Variable;
import it.unibz.inf.ontop.iq.*;
import it.unibz.inf.ontop.iq.transform.node.HeterogeneousQueryNodeTransformer;
import it.unibz.inf.ontop.iq.transform.node.HomogeneousQueryNodeTransformer;


public class EmptyNodeImpl extends LeafIQTreeImpl implements EmptyNode {

    private static final String PREFIX = "EMPTY ";
    private final ImmutableSet<Variable> projectedVariables;
    private final ConstructionNodeTools constructionNodeTools;

    @AssistedInject
    private EmptyNodeImpl(@Assisted ImmutableSet<Variable> projectedVariables,
                          IQTreeTools iqTreeTools, ConstructionNodeTools constructionNodeTools,
                          IntermediateQueryFactory iqFactory) {
        super(iqTreeTools, iqFactory);
        this.projectedVariables = projectedVariables;
        this.constructionNodeTools = constructionNodeTools;
    }

    @Override
    public void acceptVisitor(QueryNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public EmptyNode acceptNodeTransformer(HomogeneousQueryNodeTransformer transformer)
            throws QueryNodeTransformationException {
        return transformer.transform(this);
    }

    @Override
    public NodeTransformationProposal acceptNodeTransformer(HeterogeneousQueryNodeTransformer transformer) {
        return transformer.transform(this);
    }

    @Override
    public ImmutableSet<Variable> getLocalVariables() {
        return projectedVariables;
    }

    @Override
    public boolean isVariableNullable(IntermediateQuery query, Variable variable) {
        if (getVariables().contains(variable))
            return true;
        else
            throw new IllegalArgumentException("The variable " + variable + " is not projected by " + this);
    }

    @Override
    public boolean isSyntacticallyEquivalentTo(QueryNode node) {
        if (node instanceof EmptyNode) {
            return projectedVariables.equals(((EmptyNode) node).getVariables());
        }
        return false;
    }

    @Override
    public EmptyNode clone() {
        return iqFactory.createEmptyNode(projectedVariables);
    }

    @Override
    public String toString() {
        return PREFIX + projectedVariables;
    }

    @Override
    public ImmutableSet<Variable> getVariables() {
        return projectedVariables;
    }

    @Override
    public IQTree acceptTransformer(IQTreeVisitingTransformer transformer) {
        return transformer.transformEmpty(this);
    }

    @Override
    public IQTree applyDescendingSubstitutionWithoutOptimizing(
            ImmutableSubstitution<? extends VariableOrGroundTerm> descendingSubstitution) {
        return iqFactory.createEmptyNode(
                constructionNodeTools.computeNewProjectedVariables(descendingSubstitution, projectedVariables));
    }

    @Override
    public ImmutableSet<Variable> getKnownVariables() {
        return projectedVariables;
    }

    @Override
    public boolean isDeclaredAsEmpty() {
        return true;
    }

    @Override
    public VariableNullability getVariableNullability() {
        return VariableNullabilityImpl.empty();
    }

    @Override
    public void validate() throws InvalidIntermediateQueryException {
    }

    @Override
    public ImmutableSet<Variable> getLocallyRequiredVariables() {
        return ImmutableSet.of();
    }

    @Override
    public ImmutableSet<Variable> getRequiredVariables(IntermediateQuery query) {
        return getLocallyRequiredVariables();
    }

    @Override
    public ImmutableSet<Variable> getLocallyDefinedVariables() {
        return ImmutableSet.of();
    }

    @Override
    public boolean isEquivalentTo(QueryNode queryNode) {
        if (!(queryNode instanceof EmptyNode))
            return false;
        return projectedVariables.equals(((EmptyNode) queryNode).getVariables());
    }
}
