package org.semanticweb.ontop.cli.util;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.model.GlobalMetadata;
import org.semanticweb.ontop.cli.Ontop;
import org.semanticweb.ontop.cli.OntopCommand;

import com.github.rvesse.airline.help.html.HtmlCommandUsageGenerator;

/**
 * @author xiao
 */
public class OntopHtmlHelpGenerator {
    public static void main(String[] args) {
        GlobalMetadata<OntopCommand> metadata = new Cli<OntopCommand>(Ontop.class).getMetadata();

        HtmlCommandUsageGenerator generator = new HtmlCommandUsageGenerator();

        //generator.usage("ontop", new String[]{"mapping"},);

        //   generator.usage(metadata, System.out);
    }

}
