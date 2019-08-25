package it.unibz.inf.ontop.utils;

import com.google.common.collect.ImmutableSet;
import it.unibz.inf.ontop.model.term.Variable;

import java.util.Collection;

/**
 * Generates new variables that are guaranteed to not conflict with
 * already variables in a given scope.
 *
 * The typical scope for variables is the body of a rule.
 *
 * Do not expect implementations to be thread-safe!
 *
 */
public interface VariableGenerator {

    /**
     * Declares additional variables as known.
     */
    void registerAdditionalVariables(Collection<Variable> additionalVariables);

    /**
     * Registers the previous variable and creates a new non-conflicting one by reusing (part of) its name
     */
    Variable generateNewVariableFromVar(Variable previousVariable);

    /**
     * Generates a new variable if a conflict is detected.
     */
    Variable generateNewVariableIfConflicting(Variable previousVariable);

    /**
     * Generates a new non-conflicting variable.
     */
    Variable generateNewVariable();

    ImmutableSet<Variable> getKnownVariables();

    /**
     * Instant snapshot of variable it knows.
     */
    VariableGenerator createSnapshot();
}
