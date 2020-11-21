package it.unibz.inf.ontop.spec.mapping.parser.impl;

/*
 * #%L
 * ontop-obdalib-core
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

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.exception.TargetQueryParserException;
import it.unibz.inf.ontop.spec.mapping.TargetAtom;
import it.unibz.inf.ontop.spec.mapping.parser.TargetQueryParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;


public abstract class AbstractTurtleOBDAParser implements TargetQueryParser {

	private static final Logger log = LoggerFactory.getLogger(AbstractTurtleOBDAParser.class);
	private final Supplier<TurtleOBDAVisitor> visitorSupplier;

	/**
	 * Constructs the parser object with prefixes. These prefixes will
	 * help to generate the query header that contains the prefix definitions
	 * (i.e., the directives @base and @prefix).
	 *
	 */
	public AbstractTurtleOBDAParser(Supplier<TurtleOBDAVisitor> visitorSupplier) {
		this.visitorSupplier = visitorSupplier;
	}

	/**
	 * Returns the list of TargetAtom objects from the input string.
	 * If the input prefix manager is empty then no directive header will be appended.
	 * 
	 * @param input A target query string written in Turtle syntax.
	 * @return a list of TargetAtom objects.
	 */
	@Override
	public ImmutableList<TargetAtom> parse(String input) throws TargetQueryParserException {
		try {
			CharStream inputStream = CharStreams.fromString(input);
			TurtleOBDALexer lexer = new TurtleOBDALexer(inputStream);
			//substitute the standard ConsoleErrorListener (simply print out the error) with ThrowingErrorListener
            lexer.removeErrorListeners();
			lexer.addErrorListener(new ThrowingErrorListener());

			CommonTokenStream tokenStream = new CommonTokenStream(lexer);
			TurtleOBDAParser parser = new TurtleOBDAParser(tokenStream);
            //substitute the standard ConsoleErrorListener (simply print out the error) with ThrowingErrorListener
			parser.removeErrorListeners();
			parser.addErrorListener(new ThrowingErrorListener());

			return (ImmutableList<TargetAtom>)visitorSupplier.get().visitParse(parser.parse());
		}
		catch (RuntimeException e) {
			throw new TargetQueryParserException(e.getMessage(), e);
		}
	}

	private static class ThrowingErrorListener extends BaseErrorListener {
		@Override
		public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
				throws ParseCancellationException {
			log.debug("Syntax error location: column " + charPositionInLine + "\n" + msg);
			throw new ParseCancellationException("Syntax error location: column " + charPositionInLine + "\n" + msg);
		}
	}
}
