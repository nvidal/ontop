package it.unibz.inf.ontop.cli;

import com.github.rvesse.airline.Cli;
import org.junit.Test;

import static it.unibz.inf.ontop.cli.Ontop.getOntopCommandCLI;

public class OntopR2RMLToOBDATest {


    //@Ignore("too expensive to run")
    @Test
    public void testOntopHelp (){
        String[] argv = {"help", "mapping", "to-obda"};
        runCommand(argv);
    }

    //@Ignore("too expensive to run")
    @Test
    public void testOntopR2RMLToOBDA (){
        String[] argv = {"mapping", "to-obda",
                "-i", "src/test/resources/books/exampleBooks.ttl",
                "-o", "src/test/resources/output/converted-exampleBooks.obda"
        };
        runCommand(argv);
    }

    @Test
    public void testOntopR2RMLToOBDA2 (){
        String[] argv = {"mapping", "to-obda",
                "-i", "src/test/resources/mapping.ttl",
                "-o", "src/test/resources/output/mapping-booktutorial.obda"
        };
        runCommand(argv);
    }

    private void runCommand(String[] args) {
        Cli<OntopCommand> ontopCommandCLI = getOntopCommandCLI();

        OntopCommand command = ontopCommandCLI.parse(args);
        command.run();
    }



}
