package it.unibz.inf.ontop.iq.node;

import it.unibz.inf.ontop.iq.exception.QueryNodeTransformationException;
import it.unibz.inf.ontop.model.term.ImmutableExpression;
import it.unibz.inf.ontop.iq.transform.node.HomogeneousQueryNodeTransformer;

/**
 * TODO: explain
 */
public interface FilterNode extends CommutativeJoinOrFilterNode, UnaryOperatorNode {

    @Override
    FilterNode clone();

    @Override
    FilterNode acceptNodeTransformer(HomogeneousQueryNodeTransformer transformer) throws QueryNodeTransformationException;

    /**
     * Not optional for a FilterNode.
     */
    ImmutableExpression getFilterCondition();

    /**
     * Returns a new FilterNode (immutable).
     */
    FilterNode changeFilterCondition(ImmutableExpression newFilterCondition);
}
