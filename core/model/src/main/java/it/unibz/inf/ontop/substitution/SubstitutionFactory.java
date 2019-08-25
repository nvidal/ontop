package it.unibz.inf.ontop.substitution;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.Variable;
import it.unibz.inf.ontop.utils.VariableGenerator;

import java.util.Map;

public interface SubstitutionFactory {

    <T extends ImmutableTerm> ImmutableSubstitution<T> getSubstitution(ImmutableMap<Variable, T> newSubstitutionMap);
    <T extends ImmutableTerm> ImmutableSubstitution<T> getSubstitution(Variable k1, T v1);
    <T extends ImmutableTerm> ImmutableSubstitution<T> getSubstitution(Variable k1, T v1, Variable k2, T v2);
    <T extends ImmutableTerm> ImmutableSubstitution<T> getSubstitution(Variable k1, T v1, Variable k2, T v2,
                                                                       Variable k3, T v3);
    <T extends ImmutableTerm> ImmutableSubstitution<T> getSubstitution(Variable k1, T v1, Variable k2, T v2,
                                                                       Variable k3, T v3, Variable k4, T v4);
    <T extends ImmutableTerm> ImmutableSubstitution<T> getSubstitution();

    Var2VarSubstitution getVar2VarSubstitution(ImmutableMap<Variable, Variable> substitutionMap);
    InjectiveVar2VarSubstitution getInjectiveVar2VarSubstitution(Map<Variable, Variable> substitutionMap);

    InjectiveVar2VarSubstitution generateNotConflictingRenaming(VariableGenerator variableGenerator,
                                                                ImmutableSet<Variable> variables);
}
