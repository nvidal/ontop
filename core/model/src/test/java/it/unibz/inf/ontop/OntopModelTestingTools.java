package it.unibz.inf.ontop;

import com.google.inject.Injector;
import it.unibz.inf.ontop.datalog.DatalogFactory;
import it.unibz.inf.ontop.dbschema.BasicDBMetadata;
import it.unibz.inf.ontop.dbschema.DummyBasicDBMetadata;
import it.unibz.inf.ontop.injection.OntopModelConfiguration;
import it.unibz.inf.ontop.injection.IntermediateQueryFactory;
import it.unibz.inf.ontop.iq.tools.IQConverter;
import it.unibz.inf.ontop.model.atom.AtomFactory;
import it.unibz.inf.ontop.iq.transform.NoNullValueEnforcer;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.iq.tools.ExecutorRegistry;
import it.unibz.inf.ontop.model.type.TypeFactory;
import it.unibz.inf.ontop.substitution.SubstitutionFactory;
import it.unibz.inf.ontop.substitution.impl.UnifierUtilities;
import it.unibz.inf.ontop.utils.CoreUtilsFactory;
import org.apache.commons.rdf.api.RDF;

/**
 *
 */
public class OntopModelTestingTools {

    public static final IntermediateQueryFactory IQ_FACTORY;
    public static final ExecutorRegistry EXECUTOR_REGISTRY;
    public static final TermFactory TERM_FACTORY;
    public static final AtomFactory ATOM_FACTORY;
    public static final SubstitutionFactory SUBSTITUTION_FACTORY;
    public static final DatalogFactory DATALOG_FACTORY;
    public static final TypeFactory TYPE_FACTORY;
    public static final UnifierUtilities UNIFIER_UTILITIES;
    public static final NoNullValueEnforcer NO_NULL_VALUE_ENFORCER;
    public static final IQConverter IQ_CONVERTER;
    public static final RDF RDF_FACTORY;
    public static final CoreUtilsFactory CORE_UTILS_FACTORY;

    private static final DummyBasicDBMetadata DEFAULT_DUMMY_DB_METADATA;

    static {
        OntopModelConfiguration defaultConfiguration = OntopModelConfiguration.defaultBuilder()
                .enableTestMode()
                .build();
        Injector injector = defaultConfiguration.getInjector();

        IQ_FACTORY = injector.getInstance(IntermediateQueryFactory.class);
        ATOM_FACTORY = injector.getInstance(AtomFactory.class);
        SUBSTITUTION_FACTORY = injector.getInstance(SubstitutionFactory.class);
        DATALOG_FACTORY = injector.getInstance(DatalogFactory.class);
        TERM_FACTORY = injector.getInstance(TermFactory.class);
        TYPE_FACTORY = injector.getInstance(TypeFactory.class);
        DEFAULT_DUMMY_DB_METADATA = injector.getInstance(DummyBasicDBMetadata.class);
        UNIFIER_UTILITIES = injector.getInstance(UnifierUtilities.class);
        IQ_CONVERTER = injector.getInstance(IQConverter.class);
        RDF_FACTORY = injector.getInstance(RDF.class);
        CORE_UTILS_FACTORY = injector.getInstance(CoreUtilsFactory.class);

        EXECUTOR_REGISTRY = defaultConfiguration.getExecutorRegistry();

        NO_NULL_VALUE_ENFORCER = injector.getInstance(NoNullValueEnforcer.class);
    }

    public static BasicDBMetadata createDummyMetadata() {
        return DEFAULT_DUMMY_DB_METADATA.clone();
    }

}
