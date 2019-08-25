package it.unibz.inf.ontop.owlapi;

/*
 * #%L
 * ontop-quest-owlapi
 * %%
 * Copyright (C) 2009 - 2014 Free University of Bozen-Bolzano
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import it.unibz.inf.ontop.injection.OntopSQLOWLAPIConfiguration;
import it.unibz.inf.ontop.owlapi.OntopOWLFactory;
import it.unibz.inf.ontop.owlapi.OntopOWLReasoner;
import it.unibz.inf.ontop.owlapi.connection.OWLConnection;
import it.unibz.inf.ontop.owlapi.connection.OWLStatement;
import it.unibz.inf.ontop.owlapi.resultset.OWLBindingSet;
import it.unibz.inf.ontop.owlapi.resultset.TupleOWLResultSet;
import junit.framework.TestCase;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class PropertyCharacteristicTest extends TestCase {
	
	private OWLConnection conn = null;
	private OWLStatement stmt = null;
	private OntopOWLReasoner reasoner = null;
	
	private Connection jdbcconn = null;
	private Logger log = LoggerFactory.getLogger(this.getClass());

	private static final String url = "jdbc:h2:mem:questjunitdb";
	private static final String username = "sa";
	private static final String password = "";
	
	@Override
	public void setUp() throws Exception {
		createTables();
	}
	
	private String readSQLFile(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder bf = new StringBuilder();
		String line = reader.readLine();
		while (line != null) {
			bf.append(line + "\n");
			line = reader.readLine();
		}
		return bf.toString();
	}
	
	private void createTables() throws IOException, SQLException {
		String createDDL = readSQLFile("src/test/resources/property-characteristics/sqlcreate.sql");
		
		// Initializing and H2 database with the stock exchange data
		jdbcconn = DriverManager.getConnection(url, username, password);
		Statement st = jdbcconn.createStatement();

		st.executeUpdate(createDDL);
		jdbcconn.commit();
	}

	@Override
	public void tearDown() throws Exception {
	
			dropTables();
			conn.close();
			jdbcconn.close();
		
	}

	private void dropTables() throws SQLException, IOException {
		String dropDDL = readSQLFile("src/test/resources/property-characteristics/drop.sql");
		Statement st = jdbcconn.createStatement();
		st.executeUpdate(dropDDL);
		st.close();
		jdbcconn.commit();
	}
	
	public void testNoProperty() throws Exception {
		final File owlFile = new File("src/test/resources/property-characteristics/noproperty.owl");
		final File obdaFile = new File("src/test/resources/property-characteristics/noproperty.obda");
		
		setupReasoner(owlFile, obdaFile);
		TupleOWLResultSet rs = executeSelectQuery("" +
				"PREFIX : <http://www.semanticweb.org/johardi/ontologies/2013/3/Ontology1365668829973.owl#> \n" +
				"SELECT ?x ?y \n" +
				"WHERE { ?x :knows ?y . }"
				);
		final int count = countResult(rs, true);
		assertEquals(3, count);
	}
	
	public void testSymmetricProperty() throws Exception {
		final File owlFile = new File("src/test/resources/property-characteristics/symmetric.owl");
		final File obdaFile = new File("src/test/resources/property-characteristics/symmetric.obda");
		
		setupReasoner(owlFile, obdaFile);
		TupleOWLResultSet  rs = executeSelectQuery("" +
				"PREFIX : <http://www.semanticweb.org/johardi/ontologies/2013/3/Ontology1365668829973.owl#> \n" +
				"SELECT ?x ?y \n" +
				"WHERE { ?x :knows ?y . }"
				);
		final int count = countResult(rs, true);
		assertEquals(6, count);
	}
	
	private void setupReasoner(File owlFile, File obdaFile) throws Exception {
		OntopOWLFactory factory = OntopOWLFactory.defaultFactory();
        OntopSQLOWLAPIConfiguration config = OntopSQLOWLAPIConfiguration.defaultBuilder()
				.ontologyFile(owlFile)
				.nativeOntopMappingFile(obdaFile)
				.jdbcUrl(url)
				.jdbcUser(username)
				.jdbcPassword(password)
				.enableTestMode()
				.build();
        reasoner = factory.createReasoner(config);
	}
	
	private TupleOWLResultSet  executeSelectQuery(String sparql) throws Exception {
			conn = reasoner.getConnection();
			stmt = conn.createStatement();
			return stmt.executeSelectQuery(sparql);
	}
	
	private int countResult(TupleOWLResultSet  rs, boolean stdout) throws OWLException {
		int counter = 0;
		while (rs.hasNext()) {
            final OWLBindingSet bindingSet = rs.next();
            counter++;
			if (stdout) {
				for (int column = 1; column <= rs.getColumnCount(); column++) {
					OWLObject binding = bindingSet.getOWLObject(column);
					log.debug(binding.toString() + ", ");
				}

			}
		}
		return counter;
	}
}
