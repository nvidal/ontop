package it.unibz.inf.ontop.answering.reformulation;


import com.google.common.collect.ImmutableSet;
import it.unibz.inf.ontop.answering.OntopQueryEngine;
import it.unibz.inf.ontop.answering.connection.OntopConnection;
import it.unibz.inf.ontop.answering.connection.OntopStatement;
import it.unibz.inf.ontop.answering.reformulation.impl.SQLExecutableQuery;
import it.unibz.inf.ontop.answering.reformulation.input.InputQueryFactory;
import it.unibz.inf.ontop.answering.reformulation.input.SelectQuery;
import it.unibz.inf.ontop.answering.resultset.OntopBinding;
import it.unibz.inf.ontop.answering.resultset.OntopBindingSet;
import it.unibz.inf.ontop.answering.resultset.TupleResultSet;
import it.unibz.inf.ontop.exception.*;
import it.unibz.inf.ontop.injection.OntopMappingSQLAllConfiguration;
import it.unibz.inf.ontop.injection.OntopReformulationSQLConfiguration;
import it.unibz.inf.ontop.injection.OntopSystemSQLConfiguration;
import it.unibz.inf.ontop.spec.OBDASpecification;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

/**
 * Examples showing how to separate the offline (OBDA specification extraction) from the online stage
 * (query reformulation or full query answering)
 */
public class OfflineOnlineMarriageTest {

    private static final String OBDA_FILE = "src/test/resources/marriage/marriage.obda";
    private static final String CREATE_DB_FILE = "src/test/resources/marriage/create-db.sql";
    private static final String JDBC_URL = "jdbc:h2:mem:questjunitdb";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASSWORD = "";
    private static final Logger LOGGER = LoggerFactory.getLogger(OfflineOnlineMarriageTest.class);
    private static final String PERSON_QUERY_STRING = "PREFIX : <http://example.org/marriage/voc#>\n" +
            "\n" +
            "SELECT DISTINCT ?x \n" +
            "WHERE {\n" +
            "  ?x a :Person .\n" +
            "}";
    private static final String VAR = "x";
    private static final String PERSON_PREFIX = "http://example.com/person/";

    /*
     * DB connection (keeps it alive)
     */
    private static Connection CONN;

    @BeforeClass
    public static void setUp() throws Exception {

        CONN = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

        Statement st = CONN.createStatement();

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
        CONN.commit();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        CONN.close();
    }

    @Test
    public void testQueryReformulator() throws OBDASpecificationException, OntopReformulationException {
        QueryReformulator queryReformulator = createReformulator();
        InputQueryFactory inputQueryFactory = queryReformulator.getInputQueryFactory();

        SelectQuery query = inputQueryFactory.createSelectQuery(PERSON_QUERY_STRING);

        SQLExecutableQuery executableQuery = (SQLExecutableQuery) queryReformulator.reformulateIntoNativeQuery(query);
        String sqlQuery = executableQuery.getSQL();

        assertFalse(sqlQuery.isEmpty());
        LOGGER.info(sqlQuery);
    }

    /**
     * Instantiation of the query reformulator
     */
    private static QueryReformulator createReformulator() throws OBDASpecificationException {

        OBDASpecification obdaSpecification = loadOBDASpecification();

        OntopReformulationSQLConfiguration reformulationConfiguration = OntopReformulationSQLConfiguration.defaultBuilder()
                .obdaSpecification(obdaSpecification)
                .jdbcUrl(JDBC_URL)
                .enableTestMode()
                .build();

        return reformulationConfiguration.loadQueryReformulator();
    }

    private static OBDASpecification loadOBDASpecification() throws OBDASpecificationException {
        OntopMappingSQLAllConfiguration mappingConfiguration = OntopMappingSQLAllConfiguration.defaultBuilder()
                .nativeOntopMappingFile(OBDA_FILE)
                .jdbcUser(JDBC_USER)
                .jdbcPassword(JDBC_PASSWORD)
                .jdbcUrl(JDBC_URL)
                .enableTestMode()
                .build();

        return mappingConfiguration.loadSpecification();
    }

    @Test
    public void testQueryEngine() throws OBDASpecificationException, OntopConnectionException,
            OntopReformulationException, OntopResultConversionException, OntopQueryEvaluationException {
        try (OntopQueryEngine queryEngine = createQueryEngine()) {
            queryEngine.connect();

            try (OntopConnection connection = queryEngine.getConnection();
                 OntopStatement statement = connection.createStatement()) {
                InputQueryFactory inputQueryFactory = connection.getInputQueryFactory();

                SelectQuery query = inputQueryFactory.createSelectQuery(PERSON_QUERY_STRING);
                TupleResultSet resultSet = statement.execute(query);

                ImmutableSet.Builder<String> answerBuilder = ImmutableSet.builder();
                while (resultSet.hasNext()) {
                    OntopBindingSet bindingSet = resultSet.next();
                    OntopBinding binding = bindingSet.getBinding(VAR);
                    if (binding != null)
                        answerBuilder.add(binding.getValue().getValue());
                }

                assertEquals(
                        ImmutableSet.of(PERSON_PREFIX + 1, PERSON_PREFIX + 2, PERSON_PREFIX + 3),
                        answerBuilder.build());
            }
        }
    }

    /**
     * Instantiation of the query engine
     */
    private static OntopQueryEngine createQueryEngine() throws OBDASpecificationException {
        OBDASpecification obdaSpecification = loadOBDASpecification();

        OntopSystemSQLConfiguration systemConfiguration = OntopSystemSQLConfiguration.defaultBuilder()
                .obdaSpecification(obdaSpecification)
                .jdbcUser(JDBC_USER)
                .jdbcPassword(JDBC_PASSWORD)
                .jdbcUrl(JDBC_URL)
                .enableTestMode()
                .build();

        return systemConfiguration.loadQueryEngine();
    }
}
