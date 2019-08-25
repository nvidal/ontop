package it.unibz.inf.ontop.rdf4j.repository;

import it.unibz.inf.ontop.injection.OntopSQLOWLAPIConfiguration;
import org.eclipse.rdf4j.query.Query;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

/**
 * Tests that user-applied constraints can be provided through
 * sesameWrapper.OntopVirtualRepository
 * with manually instantiated metadata.
 *
 * This is quite similar to the setting in the optique platform
 * 
 * Some stuff copied from ExampleManualMetadata 
 * 
 * @author dhovl
 *
 */
public class RDF4JTimeoutTest {
	static String owlfile = "src/test/resources/userconstraints/uc.owl";
	static String obdafile = "src/test/resources/userconstraints/uc.obda";
	static String r2rmlfile = "src/test/resources/userconstraints/uc.ttl";

	static String uc_keyfile = "src/test/resources/userconstraints/keys.lst";
	static String uc_create = "src/test/resources/userconstraints/create.sql";

	private Connection sqlConnection;
	private RepositoryConnection conn;

	private static final String URL = "jdbc:h2:mem:countries";
	private static final String USER = "sa";
	private static final String PASSWORD = "";
	

	@Before
	public void init()  throws Exception {

		sqlConnection= DriverManager.getConnection(URL, USER, PASSWORD);
		java.sql.Statement s = sqlConnection.createStatement();

		try {
			Scanner sqlFile = new Scanner(new File(uc_create));
			String text = sqlFile.useDelimiter("\\A").next();
			sqlFile.close();
			
			s.execute(text);
			for(int i = 1; i <= 10000; i++){
				s.execute("INSERT INTO TABLE1 VALUES (" + i + "," + i + ");");
			}

		} catch(SQLException sqle) {
			System.out.println("Exception in creating db from script");
		}

		s.close();

		OntopSQLOWLAPIConfiguration configuration = OntopSQLOWLAPIConfiguration.defaultBuilder()
				.r2rmlMappingFile(r2rmlfile)
				.ontologyFile(owlfile)
				.jdbcUrl(URL)
				.jdbcUser(USER)
				.jdbcPassword(PASSWORD)
				.enableTestMode()
				.build();

		OntopRepository repo = OntopRepository.defaultRepository(configuration);
		repo.initialize();
		/*
		 * Prepare the data connection for querying.
		 */
		conn = repo.getConnection();
	}


	@After
	public void tearDown() throws Exception{
		if (!sqlConnection.isClosed()) {
			java.sql.Statement s = sqlConnection.createStatement();
			try {
				s.execute("DROP ALL OBJECTS DELETE FILES");
			} catch (SQLException sqle) {
				System.out.println("Table not found, not dropping");
			} finally {
				s.close();
				sqlConnection.close();
			}
		}
	}



	@Test
	public void testTimeout() throws Exception {
		String queryString = "PREFIX : <http://www.semanticweb.org/ontologies/2013/7/untitled-ontology-150#> SELECT * WHERE {?x :hasVal1 ?v1; :hasVal2 ?v2.}";
        
        // execute query
        Query query = conn.prepareQuery(QueryLanguage.SPARQL, queryString);

        TupleQuery tq = (TupleQuery) query;
        tq.setMaxQueryTime(1);
        boolean exceptionThrown = false;
        long start = System.currentTimeMillis();
        try {
        	TupleQueryResult result = tq.evaluate();
        	result.close();
        } catch (Exception e) {
        	long end = System.currentTimeMillis();
        	assertTrue(e.toString().indexOf("OntopTupleQuery timed out. More than 1 seconds passed") >= 0);
        	assertTrue(end - start >= 1000);
        	exceptionThrown = true;
        } 
		assertTrue(exceptionThrown);
		
	}
	
	@Test
	public void testNoTimeout() throws Exception {
		String queryString = "PREFIX : <http://www.semanticweb.org/ontologies/2013/7/untitled-ontology-150#> SELECT * WHERE {?x :hasVal1 ?v1; :hasVal2 ?v2.}";
        
        // execute query
        Query query = conn.prepareQuery(QueryLanguage.SPARQL, queryString);

        TupleQuery tq = (TupleQuery) query;
        TupleQueryResult result = tq.evaluate();
		assertTrue(result.hasNext());
		result.close();
	
	}
}
