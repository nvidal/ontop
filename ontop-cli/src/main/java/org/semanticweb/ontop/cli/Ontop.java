package org.semanticweb.ontop.cli;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.annotations.Group;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.parser.errors.ParseArgumentsUnexpectedException;
import com.github.rvesse.airline.parser.errors.ParseCommandMissingException;
import com.github.rvesse.airline.parser.errors.ParseCommandUnrecognizedException;
import com.github.rvesse.airline.parser.errors.ParseOptionMissingException;


@com.github.rvesse.airline.annotations.Cli(name = "ontop",
        description = "Ontop system for Ontology based Data Access",
        defaultCommand = OntopHelp.class,
        commands = {OntopVersion.class,
                OntopHelp.class,
                OntopQuery.class,
                OntopMaterialize.class,
                OntopBootstrap.class,
                OntopValidate.class,
                OntopCompile.class},
        groups = {@Group(name = "mapping",
                commands = {OntopOBDAToR2RML.class,
                        OntopR2RMLToOBDA.class,
                        OntopR2RMLPrettify.class})})
public class Ontop {

    public static void main(String... args) {
        // Cli<OntopCommand> ontopCommandCLI = getOntopCommandCLI();
        Cli<OntopCommand> ontopCommandCLI = new Cli<>(Ontop.class);

        OntopCommand command;

        try {
            command = ontopCommandCLI.parse(args);
            command.run();
        } catch (ParseCommandMissingException e) {
            main("help");
        } catch (ParseCommandUnrecognizedException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Run `ontop help` to see the help");
        } catch (ParseArgumentsUnexpectedException | ParseOptionMissingException e) {
            System.err.println("Error: " + e.getMessage());
            String commandName = args[0];
            System.err.format("Run `ontop help %s` to see the help for the command `%s`\n", commandName, commandName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
