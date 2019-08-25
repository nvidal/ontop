package it.unibz.inf.ontop.iq.node.impl;

import com.google.common.collect.ImmutableSet;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import it.unibz.inf.ontop.injection.IntermediateQueryFactory;
import it.unibz.inf.ontop.iq.exception.InvalidIntermediateQueryException;
import it.unibz.inf.ontop.iq.impl.IQTreeTools;
import it.unibz.inf.ontop.iq.node.*;
import it.unibz.inf.ontop.iq.exception.QueryNodeTransformationException;
import it.unibz.inf.ontop.iq.transform.IQTreeVisitingTransformer;
import it.unibz.inf.ontop.model.atom.AtomPredicate;
import it.unibz.inf.ontop.model.atom.DataAtom;
import it.unibz.inf.ontop.model.term.Variable;
import it.unibz.inf.ontop.iq.*;
import it.unibz.inf.ontop.iq.transform.node.HeterogeneousQueryNodeTransformer;
import it.unibz.inf.ontop.iq.transform.node.HomogeneousQueryNodeTransformer;


public class IntensionalDataNodeImpl extends DataNodeImpl<AtomPredicate> implements IntensionalDataNode {

    private static final String INTENSIONAL_DATA_NODE_STR = "INTENSIONAL";

    @AssistedInject
    private IntensionalDataNodeImpl(@Assisted DataAtom<AtomPredicate> atom,
                                    IQTreeTools iqTreeTools, IntermediateQueryFactory iqFactory) {
        super(atom, iqTreeTools, iqFactory);
    }

    @Override
    public void acceptVisitor(QueryNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IntensionalDataNode clone() {
        return iqFactory.createIntensionalDataNode(getProjectionAtom());
    }

    @Override
    public IntensionalDataNode acceptNodeTransformer(HomogeneousQueryNodeTransformer transformer)
            throws QueryNodeTransformationException {
        return transformer.transform(this);
    }

    /**
     * We assume all the variables are non-null. Ok for triple patterns.
     * TODO: what about quads and default graphs?
     */
    @Override
    public boolean isVariableNullable(IntermediateQuery query, Variable variable) {
        if (getVariables().contains(variable))
            return false;
        else
            throw new IllegalArgumentException("The variable" + variable + " is not projected by " + this);
    }

    @Override
    public IQTree acceptTransformer(IQTreeVisitingTransformer transformer) {
        return transformer.transformIntensionalData(this);
    }

    @Override
    public VariableNullability getVariableNullability() {
        return VariableNullabilityImpl.empty();
    }

    @Override
    public void validate() throws InvalidIntermediateQueryException {
    }

    @Override
    public boolean isSyntacticallyEquivalentTo(QueryNode node) {
        return (node instanceof IntensionalDataNode)
                && ((IntensionalDataNode) node).getProjectionAtom().equals(this.getProjectionAtom());
    }

    @Override
    public ImmutableSet<Variable> getRequiredVariables(IntermediateQuery query) {
        return getLocallyRequiredVariables();
    }

    @Override
    public boolean isEquivalentTo(QueryNode queryNode) {
        return (queryNode instanceof IntensionalDataNode)
                && getProjectionAtom().equals(((IntensionalDataNode) queryNode).getProjectionAtom());
    }

    @Override
    public NodeTransformationProposal acceptNodeTransformer(HeterogeneousQueryNodeTransformer transformer) {
        return transformer.transform(this);
    }

    @Override
    public String toString() {
        return INTENSIONAL_DATA_NODE_STR + " " + getProjectionAtom();
    }

    @Override
    public IntensionalDataNode newAtom(DataAtom newAtom) {
        return iqFactory.createIntensionalDataNode(newAtom);
    }

}
