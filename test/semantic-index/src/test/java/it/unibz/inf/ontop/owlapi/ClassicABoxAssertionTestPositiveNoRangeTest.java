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

import it.unibz.inf.ontop.owlapi.OntopOWLFactory;
import it.unibz.inf.ontop.owlapi.OntopOWLReasoner;
import it.unibz.inf.ontop.owlapi.connection.OWLConnection;
import it.unibz.inf.ontop.owlapi.connection.OWLStatement;
import it.unibz.inf.ontop.owlapi.resultset.OWLBindingSet;
import it.unibz.inf.ontop.owlapi.resultset.TupleOWLResultSet;
import it.unibz.inf.ontop.si.OntopSemanticIndexLoader;
import junit.framework.TestCase;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLObject;

import java.util.Properties;

/**
 * This test check proper handling of ABox assertions, including handling of the
 * supported data types. We check that each ABox assertion is inserted in the
 * database and the data is taken into account in relevant queries. Typing is
 * important in that although all data will be entered, not all data
 * participates in all queries.
 */
public class ClassicABoxAssertionTestPositiveNoRangeTest extends TestCase {

	private OWLConnection conn;
	private OWLStatement st;

	public ClassicABoxAssertionTestPositiveNoRangeTest() throws Exception {
		Properties p = new Properties();

		String owlfile = "src/test/resources/test/owl-types-simple-split.owl";

		OntopOWLReasoner reasoner;
		try(OntopSemanticIndexLoader siLoader = OntopSemanticIndexLoader.loadOntologyIndividuals(owlfile, p)) {
			OntopOWLFactory factory = OntopOWLFactory.defaultFactory();
			reasoner = factory.createReasoner(siLoader.getConfiguration());
		}

		conn = reasoner.getConnection();
		st = conn.createStatement();
	}

	private int executeQuery(String q) throws OWLException {
		String prefix = "PREFIX : <http://it.unibz.inf/obda/ontologies/quest-typing-test.owl#> \n PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";
		String query = prefix + " " + q;

		TupleOWLResultSet res = st.executeSelectQuery(query);
		int count = 0;
		int columns = res.getColumnCount();
		while (res.hasNext()) {
            final OWLBindingSet bindingSet = res.next();
            for (int i = 0; i < columns; i++) {
				OWLObject o = bindingSet.getOWLObject(i+1);
				System.out.println(o.toString());
			}
			count += 1;
		}
		res.close();
		return count;
	}

	public void testClassAssertions() throws OWLException {
		String query = "SELECT ?x WHERE {?x a :class}";
		int count = executeQuery(query);
		assertEquals(1, count);
	}

	public void testObjectPropertyAssertions() throws OWLException{
		String query = "SELECT ?x ?y WHERE {?x :oproperty ?y}";
		int count = executeQuery(query);
		assertEquals(1, count);
	}

	public void testDataPropertyAssertionsLiteral() throws OWLException{
		String query = "SELECT ?x WHERE {?x :uliteral ?y}";
		int count = executeQuery(query);
		assertEquals(2, count);
	}

	public void testDataPropertyAssertionsBoolean() throws OWLException{
		String query = "SELECT ?x WHERE {?x :uboolean ?y}";
		int count = executeQuery(query);
		// asserting 0 and false will result in only one axiom 
		// same for 1 and true
		// the result is a set of axioms
		// hence we expect only 2
		assertEquals(2, count);
	}

	public void testDataPropertyAssertionsDatetime() throws OWLException{
		String query = "SELECT ?x WHERE {?x :udateTime ?y}";
		int count = executeQuery(query);
		assertEquals(5, count);
	}

	public void testDataPropertyAssertionsDecimal() throws OWLException{
		String query = "SELECT ?x WHERE {?x :udecimal ?y}";
		int count = executeQuery(query);
		assertEquals(8, count);
	}

	public void testDataPropertyAssertionsDouble() throws OWLException{
		String query = "SELECT ?y WHERE {?x :udouble ?y}";
		int count = executeQuery(query);
		// values 0 and -0 produce equivalent axioms
		assertEquals(6, count);
	}

	public void testDataPropertyAssertionsFloat() throws OWLException{
		String query = "SELECT ?x WHERE {?x :ufloat ?y}";
		int count = executeQuery(query);
		// values 0 and -0 produce equivalent axioms
		assertEquals(6, count);
	}

	public void testDataPropertyAssertionsInt() throws OWLException{
		String query = "SELECT ?x ?y WHERE {?x :uint ?y}";
		int count = executeQuery(query);
		assertEquals(6, count);
		
		query = "SELECT ?x ?y WHERE {?x :uint ?y FILTER (?y > 0)}";
		count = executeQuery(query);
		assertEquals(3, count);
	}

	public void testDataPropertyAssertionsInteger()throws OWLException {
		String query = "SELECT ?y WHERE {?x :uinteger ?y}";
		int count = executeQuery(query);
		assertEquals(4, count);
	}

	public void testDataPropertyAssertionsLong() throws OWLException{
		String query = "SELECT ?x WHERE {?x :ulong ?y}";
		int count = executeQuery(query);
		assertEquals(6, count);
	}
}
