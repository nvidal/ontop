package it.unibz.inf.ontop.docker.postgres;

import it.unibz.inf.ontop.docker.AbstractVirtualModeTest;
import org.junit.Test;

/**
 * Test case for the IMDB database see wiki Example_MovieOntology
 * Created by Sarah on 30/07/14.
 */


public class ImdbPostgresTest extends AbstractVirtualModeTest {

    private static final String owlFile = "/pgsql/imdb/movieontology.owl";
    private static final String obdaFile = "/pgsql/imdb/movieontology.obda";
    private static final String propertyFile = "/pgsql/imdb/movieontology.properties";

    public ImdbPostgresTest() {
        super(owlFile, obdaFile, propertyFile);
    }

    @Test
    public void testIMDBSeries() throws Exception {
        runQueries("src/test/resources/imdb/movieontology.q");
    }

    @Test
    public void testOneQuery() throws Exception {

        String query = "PREFIX : <http://www.movieontology.org/2009/11/09/movieontology.owl#>\n" +
                "PREFIX mo: <http://www.movieontology.org/2009/10/01/movieontology.owl#>\n" +
                "PREFIX dbpedia: <http://dbpedia.org/ontology/>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "SELECT $x $title $company_name\n" +
                "WHERE { \n" +
                "   $m a mo:Movie; mo:title ?title; mo:hasActor ?x; mo:hasDirector ?x; mo:isProducedBy $y; mo:belongsToGenre $z .\n" +
                "   $x dbpedia:birthName $actor_name .\n" +
                "   $y :companyName $company_name; :hasCompanyLocation [ a mo:Eastern_Asia ] .\n" +
                "   $z a mo:Love .\n" +
                "}\n";


        String query2 = "PREFIX : <http://www.movieontology.org/2009/11/09/movieontology.owl#>\n" +
                "PREFIX mo: <http://www.movieontology.org/2009/10/01/movieontology.owl#>\n" +
                "PREFIX dbpedia: <http://dbpedia.org/ontology/>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "SELECT  *\n" +
                "WHERE { \n" +
                "   $m a mo:Movie; mo:isProducedBy $y .\n" +
                "   $y :hasCompanyLocation [ a mo:Eastern_Asia ] .\n" +
                "}\n";

        countResults(query2, 15175);
    }

    @Test
    public void testCompanyLocationQuery() throws Exception {

        String query = "PREFIX : <http://www.movieontology.org/2009/11/09/movieontology.owl#>\n" +
                "PREFIX mo: <http://www.movieontology.org/2009/10/01/movieontology.owl#>\n" +
                "PREFIX dbpedia: <http://dbpedia.org/ontology/>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "SELECT $company_name\n" +
                "WHERE { \n" +
                "   $y :companyName $company_name; :hasCompanyLocation [ a mo:Eastern_Asia ] .\n" +
                "}\n";


        countResults(query, 7738);
    }

    @Test
    public void testIndividuals() throws Exception {
        String query = "PREFIX mo: <http://www.movieontology.org/2009/10/01/movieontology.owl#>\n" +
                "SELECT DISTINCT $z \n" +
                "WHERE { \n" +
                "   $z a mo:Love .\n" +
                "}\n";

        countResults(query, 29405);
    }
}

