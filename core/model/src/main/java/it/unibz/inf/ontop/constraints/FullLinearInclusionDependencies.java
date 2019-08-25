package it.unibz.inf.ontop.constraints;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.atom.AtomFactory;
import it.unibz.inf.ontop.model.atom.AtomPredicate;
import it.unibz.inf.ontop.model.atom.DataAtom;
import it.unibz.inf.ontop.utils.CoreUtilsFactory;

/**
 *    these dependencies are full (that is, contain no existentially quantified variables)
 * @param <P>
 */

public class FullLinearInclusionDependencies<P extends AtomPredicate> extends LinearInclusionDependencies<P> {

    private FullLinearInclusionDependencies(CoreUtilsFactory coreUtilsFactory,
                                        AtomFactory atomFactory,
                                        ImmutableList<LinearInclusionDependency<P>> dependencies) {
        super(coreUtilsFactory, atomFactory, dependencies);
    }

    public static Builder builder(CoreUtilsFactory coreUtilsFactory, AtomFactory atomFactory) {
        return new Builder(coreUtilsFactory, atomFactory);
    }

    @Override
    protected ImmutableHomomorphism extendWithLabelledNulls(LinearInclusionDependency<P> id, ImmutableHomomorphism h) {
        return h;
    }

    @Override
    protected void registerVariables(DataAtom<P> atom) {
        // NO-OP
    }

    @Override
    protected void registerVariables(ImmutableCollection<DataAtom<P>> atoms) {
        // NO-OP
    }


    public static class Builder<P extends AtomPredicate> extends LinearInclusionDependencies.Builder<P> {

        protected Builder(CoreUtilsFactory coreUtilsFactory,
                          AtomFactory atomFactory) {
            super(coreUtilsFactory, atomFactory);
        }

        public Builder add(DataAtom<P> head, DataAtom<P> body) {
            if (!body.getVariables().containsAll(head.getVariables()))
                throw new IllegalArgumentException();
            super.add(head, body);
            return this;
        }

        public FullLinearInclusionDependencies build() {
            return new FullLinearInclusionDependencies(coreUtilsFactory, atomFactory, builder.build());
        }
    }
}
