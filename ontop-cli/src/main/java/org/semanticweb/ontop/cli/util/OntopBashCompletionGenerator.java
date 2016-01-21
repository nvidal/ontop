package org.semanticweb.ontop.cli.util;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.help.cli.bash.BashCompletionGenerator;
import com.github.rvesse.airline.model.GlobalMetadata;
import org.semanticweb.ontop.cli.Ontop;
import org.semanticweb.ontop.cli.OntopCommand;

import java.io.IOException;

public class OntopBashCompletionGenerator {


    /**
     * Generates ontop-completion.sh for bash-completion
     *
     */
    public static void main(String[] args) throws IOException {


        GlobalMetadata<OntopCommand> metadata = new Cli<OntopCommand>(Ontop.class).getMetadata();

        BashCompletionGenerator<OntopCommand> generator = new BashCompletionGenerator<>();

        generator.usage(metadata, System.out);
    }
}
