package it.unibz.inf.ontop.reformulation.tests;

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

import it.unibz.inf.ontop.io.ModelIOManager;
import it.unibz.inf.ontop.model.OBDADataFactory;
import it.unibz.inf.ontop.model.OBDAModel;
import it.unibz.inf.ontop.model.impl.OBDADataFactoryImpl;
import it.unibz.inf.ontop.owlrefplatform.core.QuestConstants;
import it.unibz.inf.ontop.owlrefplatform.core.QuestPreferences;
import it.unibz.inf.ontop.owlrefplatform.owlapi.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Class to test if bind in SPARQL is working properly.
 * Refer in particular to the class {@link it.unibz.inf.ontop.owlrefplatform.core.translator.SparqlAlgebraToDatalogTranslator}
 *
 * It uses the test from http://www.w3.org/TR/sparql11-query/#bind
 */

public class CharacterTest {



    private OBDADataFactory fac;
    private Connection conn;

    private OBDAModel obdaModel;
    private OWLOntology ontology;

    final String owlfile = "src/test/resources/test/unicode.owl";
    final String obdafile = "src/test/resources/test/unicode.obda";

    @Before
    public void setUp() throws Exception {

        // String driver = "org.h2.Driver";
        String url = "jdbc:h2:mem:unicodedb";
        String username = "sa";
        String password = "";

        fac = OBDADataFactoryImpl.getInstance();

        conn = DriverManager.getConnection(url, username, password);
        Statement st = conn.createStatement();

        FileReader reader = new FileReader("src/test/resources/test/unicode.sql");
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

        // Loading the OWL file
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        ontology = manager.loadOntologyFromOntologyDocument((new File(owlfile)));


        // Loading the OBDA data
        obdaModel = fac.getOBDAModel();
        ModelIOManager ioManager = new ModelIOManager(obdaModel);
        ioManager.load(obdafile);
    }


    @After
    public void tearDown() throws Exception{

        if (!conn.isClosed()) {
            java.sql.Statement s = conn.createStatement();
            try {
                s.execute("DROP ALL OBJECTS DELETE FILES");
            } catch (SQLException sqle) {
                System.out.println("Table not found, not dropping");
            } finally {
                s.close();
                conn.close();
            }
        }
    }


    private String runTests(QuestPreferences p, String query) throws Exception {

        // Creating a new instance of the reasoner

        QuestOWLFactory factory = new QuestOWLFactory();
        QuestOWLConfiguration config = QuestOWLConfiguration.builder().obdaModel(obdaModel).preferences(p).build();
        QuestOWL reasoner = factory.createReasoner(ontology, config);


        // Now we are ready for querying
        QuestOWLConnection conn = reasoner.getConnection();
        QuestOWLStatement st = conn.createStatement();


        try {
            QuestOWLResultSet rs = st.executeTuple(query);
            assertTrue(rs.nextRow());

            OWLObject ind2 = rs.getOWLObject("y");

            return ind2.toString();

        }
        finally {
            st.close();
            reasoner.dispose();
        }
    }

    /**
     * querySelect1 return a literal instead of a numeric datatype
     * @throws Exception
     */
    @Test
    public void testSelect() throws Exception {

        QuestPreferences p = new QuestPreferences();
        p.setCurrentValueOf(QuestPreferences.ABOX_MODE, QuestConstants.VIRTUAL);
        p.setCurrentValueOf(QuestPreferences.OPTIMIZE_EQUIVALENCES, "true");

        //simple case
        String querySelect = "PREFIX : <http://www.semanticweb.org/sarah/ontologies/2017/1/untitled-ontology-528#>\n" +
                "SELECT ?y \n" +
                "{ ?x :tieneAño ?y .\n" +
                "}";
        String year = runTests(p, querySelect);

        assertEquals("\"1999\"", year );






    }


}
