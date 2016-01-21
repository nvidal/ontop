package org.semanticweb.ontop.cli.util;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.help.cli.bash.BashCompletionGenerator;
import com.github.rvesse.airline.help.markdown.MarkdownGlobalUsageGenerator;
import com.github.rvesse.airline.model.GlobalMetadata;
import org.semanticweb.ontop.cli.Ontop;
import org.semanticweb.ontop.cli.OntopCommand;

import java.io.FileOutputStream;
import java.io.IOException;

public class OntopMarkdownHelpGenerator {


    /**
     * Generates ontop-completion.sh for bash-completion
     *
     */
    public static void main(String[] args) throws IOException {


        GlobalMetadata<OntopCommand> metadata = new Cli<OntopCommand>(Ontop.class).getMetadata();

        MarkdownGlobalUsageGenerator<OntopCommand> generator = new MarkdownGlobalUsageGenerator<>();

        //generator.usage(metadata, System.out);

        generator.usage(metadata, new FileOutputStream("src/main/resources/USAGE.md"));
    }
}
