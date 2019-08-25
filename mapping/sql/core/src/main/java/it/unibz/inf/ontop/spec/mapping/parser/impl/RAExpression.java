package it.unibz.inf.ontop.spec.mapping.parser.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import it.unibz.inf.ontop.model.atom.DataAtom;
import it.unibz.inf.ontop.model.atom.RelationPredicate;
import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.dbschema.QualifiedAttributeID;
import it.unibz.inf.ontop.dbschema.QuotedID;
import it.unibz.inf.ontop.dbschema.RelationID;
import it.unibz.inf.ontop.model.term.functionsymbol.ExpressionOperation;
import it.unibz.inf.ontop.spec.mapping.parser.exception.IllegalJoinException;
import it.unibz.inf.ontop.utils.ImmutableCollectors;

/**
 * Created by Roman Kontchakov on 01/11/2016.
 *
 */
public class RAExpression {

    private ImmutableList<DataAtom<RelationPredicate>> dataAtoms;
    private ImmutableList<ImmutableExpression> filterAtoms;
    private RAExpressionAttributes attributes;

    /**
     * constructs a relation expression
     *  @param dataAtoms            an {@link ImmutableList}<{@link Function}>
     * @param filterAtoms          an {@link ImmutableList}<{@link Function}>
     * @param attributes           an {@link RAExpressionAttributes}
     */
    public RAExpression(ImmutableList<DataAtom<RelationPredicate>> dataAtoms,
                        ImmutableList<ImmutableExpression> filterAtoms,
                        RAExpressionAttributes attributes) {
        this.dataAtoms = dataAtoms;
        this.filterAtoms = filterAtoms;
        this.attributes = attributes;
    }


    public ImmutableList<DataAtom<RelationPredicate>> getDataAtoms() {
        return dataAtoms;
    }

    public ImmutableList<ImmutableExpression> getFilterAtoms() {
        return filterAtoms;
    }

    public ImmutableMap<QualifiedAttributeID, ImmutableTerm> getAttributes() {
        return attributes.getAttributes();
    }

    /**
     * CROSS JOIN (also denoted by , in SQL)
     *
     * @param re1 a {@link RAExpression}
     * @param re2 a {@link RAExpression}
     * @return a {@link RAExpression}
     * @throws IllegalJoinException if the same alias occurs in both arguments
     */
    public static RAExpression crossJoin(RAExpression re1, RAExpression re2) throws IllegalJoinException {

        RAExpressionAttributes attributes =
                RAExpressionAttributes.crossJoin(re1.attributes, re2.attributes);

        return new RAExpression(union(re1.dataAtoms, re2.dataAtoms),
                union(re1.filterAtoms, re2.filterAtoms), attributes);
    }


    /**
     * JOIN ON
     *
     * @param re1 a {@link RAExpression}
     * @param re2 a {@link RAExpression}
     * @param getAtomOnExpression
     * @return a {@link RAExpression}
     * @throws IllegalJoinException if the same alias occurs in both arguments
     */
    public static RAExpression joinOn(RAExpression re1, RAExpression re2,
                                      java.util.function.Function<ImmutableMap<QualifiedAttributeID, ImmutableTerm>, ImmutableList<ImmutableExpression>> getAtomOnExpression) throws IllegalJoinException {

        RAExpressionAttributes attributes =
                RAExpressionAttributes.crossJoin(re1.attributes, re2.attributes);

        return new RAExpression(union(re1.dataAtoms, re2.dataAtoms),
                union(re1.filterAtoms, re2.filterAtoms,
                        getAtomOnExpression.apply(attributes.getAttributes())), attributes);
    }

    /**
     * NATURAL JOIN
     *
     * @param re1 a {@link RAExpression}
     * @param re2 a {@link RAExpression}
     * @return a {@link RAExpression}
     * @throws IllegalJoinException if the same alias occurs in both arguments
     *          or one of the shared attributes is ambiguous
     */

    public static RAExpression naturalJoin(RAExpression re1, RAExpression re2, TermFactory termFactory) throws IllegalJoinException {

        ImmutableSet<QuotedID> shared =
                RAExpressionAttributes.getShared(re1.attributes, re2.attributes);

        RAExpressionAttributes attributes =
                RAExpressionAttributes.joinUsing(re1.attributes, re2.attributes, shared);

        return new RAExpression(union(re1.dataAtoms, re2.dataAtoms),
                union(re1.filterAtoms, re2.filterAtoms,
                        getJoinOnFilter(re1.attributes, re2.attributes, shared, termFactory)),
                attributes);
    }

    /**
     * JOIN USING
     *
     * @param re1 a {@link RAExpression}
     * @param re2 a {@link RAExpression}
     * @param using a {@link ImmutableSet}<{@link QuotedID}>
     * @return a {@link RAExpression}
     * @throws IllegalJoinException if the same alias occurs in both arguments
     *          or one of the `using' attributes is ambiguous or absent
     */

    public static RAExpression joinUsing(RAExpression re1, RAExpression re2,
                                         ImmutableSet<QuotedID> using, TermFactory termFactory) throws IllegalJoinException {

        RAExpressionAttributes attributes =
                RAExpressionAttributes.joinUsing(re1.attributes, re2.attributes, using);

        return new RAExpression(union(re1.dataAtoms, re2.dataAtoms),
                union(re1.filterAtoms, re2.filterAtoms,
                        getJoinOnFilter(re1.attributes, re2.attributes, using, termFactory)),
                attributes);
    }

    /**
     * internal implementation of JOIN USING and NATURAL JOIN
     *
     * @param re1 a {@link RAExpressionAttributes}
     * @param re2 a {@link RAExpressionAttributes}
     * @param using a {@link ImmutableSet}<{@link QuotedID}>
     * @return a {@Link ImmutableList}<{@link Function}>
     */
    private static ImmutableList<ImmutableExpression> getJoinOnFilter(RAExpressionAttributes re1,
                                                                               RAExpressionAttributes re2,
                                                                               ImmutableSet<QuotedID> using,
                                                                               TermFactory termFactory) {

        return using.stream()
                .map(id -> new QualifiedAttributeID(null, id))
                .map(id -> {
                    // TODO: this will be removed later, when OBDA factory will start checking non-nulls
                    ImmutableTerm v1 = re1.getAttributes().get(id);
                    if (v1 == null)
                        throw new IllegalArgumentException("Term " + id + " not found in " + re1);
                    ImmutableTerm v2 = re2.getAttributes().get(id);
                    if (v2 == null)
                        throw new IllegalArgumentException("Term " + id + " not found in " + re2);
                    return termFactory.getImmutableExpression(ExpressionOperation.EQ, v1, v2);
                })
                .collect(ImmutableCollectors.toList());
    }


    /**
     * (relational expression) AS A
     *
     * @param re a {@link RAExpression}
     * @param alias a {@link QuotedID}
     * @return a {@link RAExpression}
     */

    public static RAExpression alias(RAExpression re, RelationID alias) {
        return new RAExpression(re.dataAtoms, re.filterAtoms,
                RAExpressionAttributes.alias(re.attributes, alias));
    }



    private static <T> ImmutableList<T> union(ImmutableList<T> atoms1, ImmutableList<T> atoms2) {
        return ImmutableList.<T>builder().addAll(atoms1).addAll(atoms2).build();
    }

    private static <T> ImmutableList<T> union(ImmutableList<T> atoms1, ImmutableList<T> atoms2, ImmutableList<T> atoms3) {
        return ImmutableList.<T>builder().addAll(atoms1).addAll(atoms2).addAll(atoms3).build();
    }


    @Override
    public String toString() {
        return "RAExpression : " + dataAtoms + " FILTER " + filterAtoms + " with " + attributes;
    }



}
