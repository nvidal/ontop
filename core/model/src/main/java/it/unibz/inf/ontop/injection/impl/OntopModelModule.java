package it.unibz.inf.ontop.injection.impl;

import com.google.common.collect.ImmutableList;
import com.google.inject.Module;
import it.unibz.inf.ontop.datalog.DatalogFactory;
import it.unibz.inf.ontop.evaluator.ExpressionNormalizer;
import it.unibz.inf.ontop.evaluator.TermNullabilityEvaluator;
import it.unibz.inf.ontop.injection.OntopModelConfiguration;
import it.unibz.inf.ontop.injection.IntermediateQueryFactory;
import it.unibz.inf.ontop.injection.QueryTransformerFactory;
import it.unibz.inf.ontop.iq.node.*;
import it.unibz.inf.ontop.iq.tools.IQConverter;
import it.unibz.inf.ontop.model.atom.AtomFactory;
import it.unibz.inf.ontop.iq.transform.NoNullValueEnforcer;
import it.unibz.inf.ontop.model.atom.TargetAtomFactory;
import it.unibz.inf.ontop.model.type.TypeFactory;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.iq.*;
import it.unibz.inf.ontop.iq.transform.FilterNullableVariableQueryTransformer;
import it.unibz.inf.ontop.iq.transform.QueryRenamer;
import it.unibz.inf.ontop.iq.validation.IntermediateQueryValidator;
import it.unibz.inf.ontop.substitution.SubstitutionFactory;
import it.unibz.inf.ontop.utils.CoreUtilsFactory;
import it.unibz.inf.ontop.utils.VariableGenerator;
import org.apache.commons.rdf.api.RDF;


public class OntopModelModule extends OntopAbstractModule {

    protected OntopModelModule(OntopModelConfiguration configuration) {
        super(configuration.getSettings());
    }

    @Override
    protected void configure() {
        configureCoreConfiguration();

        // Core factories: Too central to be overloaded from the properties
        bindFromSettings(TypeFactory.class);
        bindFromSettings(TermFactory.class);
        bindFromSettings(AtomFactory.class);
        bindFromSettings(SubstitutionFactory.class);
        bindFromSettings(DatalogFactory.class);
        bindFromSettings(TargetAtomFactory.class);

        bindFromSettings(IntermediateQueryValidator.class);
        bindFromSettings(TermNullabilityEvaluator.class);
        bindFromSettings(FilterNullableVariableQueryTransformer.class);
        bindFromSettings(NoNullValueEnforcer.class);
        bindFromSettings(ExpressionNormalizer.class);
        bindFromSettings(IQConverter.class);
        bindFromSettings(RDF.class);

        Module utilsModule = buildFactory(
                ImmutableList.of(
                        VariableGenerator.class
                ),
                CoreUtilsFactory.class);
        install(utilsModule);

        Module iqFactoryModule = buildFactory(ImmutableList.of(
                IntermediateQueryBuilder.class,
                ConstructionNode.class,
                UnionNode.class,
                InnerJoinNode.class,
                LeftJoinNode.class,
                FilterNode.class,
                ExtensionalDataNode.class,
                IntensionalDataNode.class,
                EmptyNode.class,
                TrueNode.class,
                DistinctNode.class,
                SliceNode.class,
                OrderByNode.class,
                OrderByNode.OrderComparator.class,
                UnaryIQTree.class,
                BinaryNonCommutativeIQTree.class,
                NaryIQTree.class,
                IQ.class,
                IQProperties.class
                ),
                IntermediateQueryFactory.class);
        install(iqFactoryModule);

        Module queryTransformerModule = buildFactory(ImmutableList.of(
                QueryRenamer.class),
                QueryTransformerFactory.class);
        install(queryTransformerModule);
    }
}
