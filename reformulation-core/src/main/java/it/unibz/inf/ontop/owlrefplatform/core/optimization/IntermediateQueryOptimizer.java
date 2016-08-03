package it.unibz.inf.ontop.owlrefplatform.core.optimization;

import it.unibz.inf.ontop.pivotalrepr.EmptyQueryException;
import it.unibz.inf.ontop.pivotalrepr.IntermediateQuery;

/**
 * TODO: explain
 *
 * TODO: should we create two sub-interfaces: GeneralOptimizer and GoalOrientedOptimizer?
 * For the moment, we expect the Optimizer to be general, not goal-oriented.
 */
public interface IntermediateQueryOptimizer {

    /**
     * TODO: explain
     */
    IntermediateQuery optimize(IntermediateQuery query) throws EmptyQueryException;
}