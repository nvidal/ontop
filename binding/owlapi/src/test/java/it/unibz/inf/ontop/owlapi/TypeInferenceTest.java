package it.unibz.inf.ontop.owlapi;

import com.google.common.collect.ImmutableSet;
import it.unibz.inf.ontop.answering.reformulation.generation.impl.TypeExtractor;
import it.unibz.inf.ontop.injection.OntopSQLOWLAPIConfiguration;
import it.unibz.inf.ontop.owlapi.OntopOWLFactory;
import it.unibz.inf.ontop.owlapi.OntopOWLReasoner;
import it.unibz.inf.ontop.owlapi.connection.OWLConnection;
import it.unibz.inf.ontop.owlapi.connection.OWLStatement;
import it.unibz.inf.ontop.owlapi.resultset.OWLBindingSet;
import it.unibz.inf.ontop.owlapi.resultset.TupleOWLResultSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertTrue;

/**
 * test Type inference in the SQL Generator with the use of metadata when the value is unknown.
 * @see TypeExtractor
 */
public class TypeInferenceTest {

    private Connection conn;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String ONTOLOGY_FILE = "src/test/resources/test/typeinference/types.owl";
    private static final String OBDA_FILE = "src/test/resources/test/typeinference/types.obda";
    private static final String CREATE_DB_FILE = "src/test/resources/test/typeinference/types-create-db.sql";
    private static final String DROP_DB_FILE = "src/test/resources/test/typeinference/types-drop-db.sql";

    private static final String URL = "jdbc:h2:mem:types";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    @Before
    public void setUp() throws Exception {

        conn = DriverManager.getConnection(URL, USER, PASSWORD);


        Statement st = conn.createStatement();

        FileReader reader = new FileReader(CREATE_DB_FILE);
        BufferedReader in = new BufferedReader(reader);
        StringBuilder bf = new StringBuilder();
        String line = in.readLine();
        while (line != null) {
            bf.append(line);
            line = in.readLine();
        }
        in.close();

        st.executeUpdate(bf.toString());
        conn.commit();
    }

    @After
    public void tearDown() throws Exception {

        dropTables();
        conn.close();

    }

    private void dropTables() throws SQLException, IOException {

        Statement st = conn.createStatement();

        FileReader reader = new FileReader(DROP_DB_FILE);
        BufferedReader in = new BufferedReader(reader);
        StringBuilder bf = new StringBuilder();
        String line = in.readLine();
        while (line != null) {
            bf.append(line);
            line = in.readLine();
        }
        in.close();

        st.executeUpdate(bf.toString());
        st.close();
        conn.commit();
    }

    @Test
    public void testType() throws Exception {
        String queryBind = "PREFIX : <http://example.org/types/voc#>\n" +
                "\n" +
                "SELECT ?r \n" +
                "WHERE {\n" +
                "?x a :Asian_Company ; :hasCompanyLocation ?r .  "+
                "}";

        ImmutableSet<String> expectedValues = ImmutableSet.of(
                "<http://example.org/types/voc#Philippines>",
                "<http://example.org/types/voc#China>"

        );
        checkReturnedValues(queryBind, expectedValues);
    }

    private void checkReturnedValues(String query, ImmutableSet<String> expectedValues) throws Exception {

        OntopOWLFactory factory = OntopOWLFactory.defaultFactory();
        OntopSQLOWLAPIConfiguration config = OntopSQLOWLAPIConfiguration.defaultBuilder()
                .nativeOntopMappingFile(OBDA_FILE)
                .ontologyFile(ONTOLOGY_FILE)
                .jdbcUrl(URL)
                .jdbcUser(USER)
                .jdbcPassword(PASSWORD)
                .enableTestMode()
                .build();
        OntopOWLReasoner reasoner = factory.createReasoner(config);

        // Now we are ready for querying
        OWLConnection conn = reasoner.getConnection();
        OWLStatement st = conn.createStatement();

        int i = 0;
        ImmutableSet.Builder<String> returnedValueBuilder = ImmutableSet.builder();
        try {
            TupleOWLResultSet rs = st.executeSelectQuery(query);
            while (rs.hasNext()) {
                final OWLBindingSet bindingSet = rs.next();
                OWLObject ind1 = bindingSet.getOWLObject("r");
                log.debug(ind1.toString());
                returnedValueBuilder.add(ind1.toString());
                i++;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            conn.close();
            reasoner.dispose();
        }
        ImmutableSet<String> returnedValues = returnedValueBuilder.build();

        assertTrue(String.format("%s instead of \n %s", returnedValues.toString(), expectedValues.toString()),
                returnedValues.equals(expectedValues));
        assertTrue(String.format("Wrong size: %d (expected %d)", i, expectedValues.size()), expectedValues.size() == i);

    }
}
