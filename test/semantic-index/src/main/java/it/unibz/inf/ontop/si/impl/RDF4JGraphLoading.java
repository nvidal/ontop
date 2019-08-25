package it.unibz.inf.ontop.si.impl;


import com.google.inject.Injector;
import it.unibz.inf.ontop.injection.OntopModelConfiguration;
import it.unibz.inf.ontop.model.atom.TargetAtomFactory;
import it.unibz.inf.ontop.model.term.ObjectConstant;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.term.ValueConstant;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import it.unibz.inf.ontop.model.type.TypeFactory;
import it.unibz.inf.ontop.si.OntopSemanticIndexLoader;
import it.unibz.inf.ontop.si.SemanticIndexException;
import it.unibz.inf.ontop.si.repository.impl.SIRepository;
import it.unibz.inf.ontop.spec.ontology.*;
import it.unibz.inf.ontop.spec.ontology.impl.OntologyBuilderImpl;
import org.apache.commons.rdf.api.RDF;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.query.Dataset;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.*;

public class RDF4JGraphLoading {

    private static final Logger LOG = LoggerFactory.getLogger(RDF4JGraphLoading.class);

    public static OntopSemanticIndexLoader loadRDFGraph(Dataset dataset, Properties properties)
            throws SemanticIndexException {
        // Merge default and named graphs to filter duplicates
        Set<IRI> graphURLs = new HashSet<>();
        graphURLs.addAll(dataset.getDefaultGraphs());
        graphURLs.addAll(dataset.getNamedGraphs());

        OntopModelConfiguration defaultConfiguration = OntopModelConfiguration.defaultBuilder().build();
        Injector injector = defaultConfiguration.getInjector();

        RDF rdfFactory = injector.getInstance(RDF.class);

        CollectRDFVocabulary collectVocabulary = new CollectRDFVocabulary(rdfFactory);
        for (IRI graphURL : graphURLs) {
            processRDF(collectVocabulary, graphURL);
        }
        Ontology vocabulary = collectVocabulary.vb.build();

        SIRepository repo = new SIRepository(vocabulary.tbox(), defaultConfiguration.getTermFactory(),
                defaultConfiguration.getTypeFactory(),
                injector.getInstance(TargetAtomFactory.class));
        Connection connection = repo.createConnection();

        //  Load the data
        SemanticIndexRDFHandler insertData = new SemanticIndexRDFHandler(repo, connection,
                defaultConfiguration.getTypeFactory(), defaultConfiguration.getTermFactory(),
                injector.getInstance(RDF.class));

        for (IRI graphURL : graphURLs) {
            processRDF(insertData, graphURL);
        }
        LOG.info("Inserted {} triples", insertData.count);

        return new OntopSemanticIndexLoaderImpl(repo, connection, properties, Optional.empty() /* no tbox */);
    }


    private static final class CollectRDFVocabulary extends AbstractRDFHandler {
        private final OntologyBuilder vb;
        private final RDF rdfFactory;

        CollectRDFVocabulary(RDF rdfFactory) {
            this.rdfFactory = rdfFactory;
            this.vb = OntologyBuilderImpl.builder(rdfFactory);
        }

        @Override
        public void handleStatement(Statement st) throws RDFHandlerException {
            String predicateName = st.getPredicate().stringValue();
            Value obj = st.getObject();
            if (predicateName.equals(org.eclipse.rdf4j.model.vocabulary.RDF.TYPE.stringValue())) {
                vb.declareClass(rdfFactory.createIRI(obj.stringValue()));
            }
            else if (obj instanceof Literal) {
                vb.declareDataProperty(rdfFactory.createIRI(predicateName));
            }
            else {
                vb.declareObjectProperty(rdfFactory.createIRI(predicateName));
            }
        }
    }

    private static final class SemanticIndexRDFHandler extends AbstractRDFHandler {

        private final SIRepository repository;
        private final Connection connection;
        private final ABoxAssertionSupplier builder;
        private final TypeFactory typeFactory;
        private final TermFactory termFactory;

        private static final int MAX_BUFFER_SIZE = 5000;

        private List<Statement> buffer = new ArrayList<>(MAX_BUFFER_SIZE);
        private int count = 0;
        private final RDF rdfFactory;

        public SemanticIndexRDFHandler(SIRepository repository, Connection connection,
                                       TypeFactory typeFactory, TermFactory termFactory,
                                       RDF rdfFactory) {
            this.repository = repository;
            this.typeFactory = typeFactory;
            this.termFactory = termFactory;
            this.builder = OntologyBuilderImpl.assertionSupplier(rdfFactory);
            this.connection = connection;
            this.rdfFactory = rdfFactory;
        }

        @Override
        public void endRDF() throws RDFHandlerException {
            loadBuffer();
        }

        @Override
        public void handleStatement(Statement st) throws RDFHandlerException {
            // Add statement to buffer
            buffer.add(st);
            if (buffer.size() == MAX_BUFFER_SIZE) {
                loadBuffer();
            }
        }

        private void loadBuffer() throws RDFHandlerException {
            try {
                Iterator<Assertion> assertionIterator = buffer.stream()
                        .map(st -> constructAssertion(st, builder))
                        .iterator();
                count += repository.insertData(connection, assertionIterator);
                buffer.clear();
            }
            catch (Exception e) {
                throw new RDFHandlerException(e);
            }
        }

        /***
         * Constructs an ABox assertion with the data from the current result set.
         * This can be a Class, Object or Data Property assertion. It is a class
         * assertion if the predicate is rdf:type. Its an Object property if the
         * predicate is not type and the object is URI or BNode. Its a data property
         * if the predicate is not rdf:type and the object is a Literal.
         */
        private Assertion constructAssertion(Statement st, ABoxAssertionSupplier builder) {

            Resource subject = st.getSubject();
            final ObjectConstant c;
            if (subject instanceof IRI) {
                c = termFactory.getConstantIRI(rdfFactory.createIRI(subject.stringValue()));
            }
            else if (subject instanceof BNode) {
                c = termFactory.getConstantBNode(subject.stringValue());
            }
            else {
                throw new RuntimeException("Unsupported subject found in triple: "	+ st + " (Required URI or BNode)");
            }

            String predicateName = st.getPredicate().stringValue();
            Value object = st.getObject();

            // Create the assertion
            try {
                if (predicateName.equals(org.eclipse.rdf4j.model.vocabulary.RDF.TYPE.stringValue() )) {
                    return builder.createClassAssertion(object.stringValue(), c);
                }
                else if (object instanceof IRI) {
                    ObjectConstant c2 = termFactory.getConstantIRI(rdfFactory.createIRI(object.stringValue()));
                    return builder.createObjectPropertyAssertion(predicateName, c, c2);
                }
                else if (object instanceof BNode) {
                    ObjectConstant c2 = termFactory.getConstantBNode(object.stringValue());
                    return builder.createObjectPropertyAssertion(predicateName, c, c2);
                }
                else if (object instanceof Literal) {
                    Literal l = (Literal) object;
                    Optional<String> lang = l.getLanguage();
                    final ValueConstant c2;
                    if (!lang.isPresent()) {
                        IRI datatype = l.getDatatype();
                        RDFDatatype type = (datatype == null)
                                ? typeFactory.getXsdStringDatatype()
                                : typeFactory.getDatatype(rdfFactory.createIRI(datatype.stringValue()));
                        c2 = termFactory.getConstantLiteral(l.getLabel(), type);
                    }
                    else {
                        c2 = termFactory.getConstantLiteral(l.getLabel(), lang.get());
                    }
                    return builder.createDataPropertyAssertion(predicateName, c, c2);
                }

                throw new RuntimeException("Unsupported object found in triple: " + st + " (Required URI, BNode or Literal)");
            }
            catch (InconsistentOntologyException e) {
                throw new RuntimeException("InconsistentOntologyException: " + st);
            }
        }
    }

    private static void processRDF(AbstractRDFHandler rdfHandler,  IRI graphURL) throws SemanticIndexException {

        RDFFormat rdfFormat = Rio.getParserFormatForFileName(graphURL.toString()).get();
        RDFParser rdfParser = Rio.createParser(rdfFormat);

        ParserConfig config = rdfParser.getParserConfig();
        // To emulate DatatypeHandling.IGNORE
        config.addNonFatalError(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES);
        config.addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);
        config.addNonFatalError(BasicParserSettings.NORMALIZE_DATATYPE_VALUES);

        rdfParser.setRDFHandler(rdfHandler);

        InputStream in = null;
        try {
            URL url = new URL(graphURL.toString());
            in = url.openStream();
            rdfParser.parse(in, graphURL.toString());
        }
        catch (IOException e) {
            throw new SemanticIndexException(e.getMessage());
        }
        finally {
            try {
                if (in != null)
                    in.close();
            }
            catch (IOException e) {
                throw new SemanticIndexException(e.getMessage());
            }
        }
    }

}
