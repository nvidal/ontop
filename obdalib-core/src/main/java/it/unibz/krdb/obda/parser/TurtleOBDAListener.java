// Generated from TurtleOBDA.g4 by ANTLR 4.5.1

package it.unibz.krdb.obda.parser;

import it.unibz.krdb.obda.model.CQIE;
import it.unibz.krdb.obda.model.Constant;
import it.unibz.krdb.obda.model.Function;
import it.unibz.krdb.obda.model.Term;
import it.unibz.krdb.obda.model.OBDADataFactory;
import it.unibz.krdb.obda.model.DatatypeFactory;
import it.unibz.krdb.obda.model.OBDALibConstants;
import it.unibz.krdb.obda.model.Predicate;
import it.unibz.krdb.obda.model.URIConstant;
import it.unibz.krdb.obda.model.ValueConstant;
import it.unibz.krdb.obda.model.Variable;
import it.unibz.krdb.obda.model.Predicate.COL_TYPE;
import it.unibz.krdb.obda.model.impl.OBDADataFactoryImpl;
import it.unibz.krdb.obda.model.impl.OBDAVocabulary;
import it.unibz.krdb.obda.utils.QueryUtils;
import it.unibz.krdb.obda.model.URITemplatePredicate;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;


import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TurtleOBDAParser}.
 */
public interface TurtleOBDAListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#parse}.
	 * @param ctx the parse tree
	 */
	void enterParse(TurtleOBDAParser.ParseContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#parse}.
	 * @param ctx the parse tree
	 */
	void exitParse(TurtleOBDAParser.ParseContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#directiveStatement}.
	 * @param ctx the parse tree
	 */
	void enterDirectiveStatement(TurtleOBDAParser.DirectiveStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#directiveStatement}.
	 * @param ctx the parse tree
	 */
	void exitDirectiveStatement(TurtleOBDAParser.DirectiveStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#triplesStatement}.
	 * @param ctx the parse tree
	 */
	void enterTriplesStatement(TurtleOBDAParser.TriplesStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#triplesStatement}.
	 * @param ctx the parse tree
	 */
	void exitTriplesStatement(TurtleOBDAParser.TriplesStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#directive}.
	 * @param ctx the parse tree
	 */
	void enterDirective(TurtleOBDAParser.DirectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#directive}.
	 * @param ctx the parse tree
	 */
	void exitDirective(TurtleOBDAParser.DirectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#base}.
	 * @param ctx the parse tree
	 */
	void enterBase(TurtleOBDAParser.BaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#base}.
	 * @param ctx the parse tree
	 */
	void exitBase(TurtleOBDAParser.BaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#prefixID}.
	 * @param ctx the parse tree
	 */
	void enterPrefixID(TurtleOBDAParser.PrefixIDContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#prefixID}.
	 * @param ctx the parse tree
	 */
	void exitPrefixID(TurtleOBDAParser.PrefixIDContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#triples}.
	 * @param ctx the parse tree
	 */
	void enterTriples(TurtleOBDAParser.TriplesContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#triples}.
	 * @param ctx the parse tree
	 */
	void exitTriples(TurtleOBDAParser.TriplesContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#predicateObjectList}.
	 * @param ctx the parse tree
	 */
	void enterPredicateObjectList(TurtleOBDAParser.PredicateObjectListContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#predicateObjectList}.
	 * @param ctx the parse tree
	 */
	void exitPredicateObjectList(TurtleOBDAParser.PredicateObjectListContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#verb}.
	 * @param ctx the parse tree
	 */
	void enterVerb(TurtleOBDAParser.VerbContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#verb}.
	 * @param ctx the parse tree
	 */
	void exitVerb(TurtleOBDAParser.VerbContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#objectList}.
	 * @param ctx the parse tree
	 */
	void enterObjectList(TurtleOBDAParser.ObjectListContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#objectList}.
	 * @param ctx the parse tree
	 */
	void exitObjectList(TurtleOBDAParser.ObjectListContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#subject}.
	 * @param ctx the parse tree
	 */
	void enterSubject(TurtleOBDAParser.SubjectContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#subject}.
	 * @param ctx the parse tree
	 */
	void exitSubject(TurtleOBDAParser.SubjectContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterPredicate(TurtleOBDAParser.PredicateContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitPredicate(TurtleOBDAParser.PredicateContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#object}.
	 * @param ctx the parse tree
	 */
	void enterObject(TurtleOBDAParser.ObjectContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#object}.
	 * @param ctx the parse tree
	 */
	void exitObject(TurtleOBDAParser.ObjectContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#resource}.
	 * @param ctx the parse tree
	 */
	void enterResource(TurtleOBDAParser.ResourceContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#resource}.
	 * @param ctx the parse tree
	 */
	void exitResource(TurtleOBDAParser.ResourceContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#uriref}.
	 * @param ctx the parse tree
	 */
	void enterUriref(TurtleOBDAParser.UrirefContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#uriref}.
	 * @param ctx the parse tree
	 */
	void exitUriref(TurtleOBDAParser.UrirefContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#qname}.
	 * @param ctx the parse tree
	 */
	void enterQname(TurtleOBDAParser.QnameContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#qname}.
	 * @param ctx the parse tree
	 */
	void exitQname(TurtleOBDAParser.QnameContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#blank}.
	 * @param ctx the parse tree
	 */
	void enterBlank(TurtleOBDAParser.BlankContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#blank}.
	 * @param ctx the parse tree
	 */
	void exitBlank(TurtleOBDAParser.BlankContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(TurtleOBDAParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(TurtleOBDAParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(TurtleOBDAParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(TurtleOBDAParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#typedLiteral}.
	 * @param ctx the parse tree
	 */
	void enterTypedLiteral(TurtleOBDAParser.TypedLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#typedLiteral}.
	 * @param ctx the parse tree
	 */
	void exitTypedLiteral(TurtleOBDAParser.TypedLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#language}.
	 * @param ctx the parse tree
	 */
	void enterLanguage(TurtleOBDAParser.LanguageContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#language}.
	 * @param ctx the parse tree
	 */
	void exitLanguage(TurtleOBDAParser.LanguageContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#terms}.
	 * @param ctx the parse tree
	 */
	void enterTerms(TurtleOBDAParser.TermsContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#terms}.
	 * @param ctx the parse tree
	 */
	void exitTerms(TurtleOBDAParser.TermsContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(TurtleOBDAParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(TurtleOBDAParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(TurtleOBDAParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(TurtleOBDAParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#stringLiteral}.
	 * @param ctx the parse tree
	 */
	void enterStringLiteral(TurtleOBDAParser.StringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#stringLiteral}.
	 * @param ctx the parse tree
	 */
	void exitStringLiteral(TurtleOBDAParser.StringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#dataTypeString}.
	 * @param ctx the parse tree
	 */
	void enterDataTypeString(TurtleOBDAParser.DataTypeStringContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#dataTypeString}.
	 * @param ctx the parse tree
	 */
	void exitDataTypeString(TurtleOBDAParser.DataTypeStringContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#numericLiteral}.
	 * @param ctx the parse tree
	 */
	void enterNumericLiteral(TurtleOBDAParser.NumericLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#numericLiteral}.
	 * @param ctx the parse tree
	 */
	void exitNumericLiteral(TurtleOBDAParser.NumericLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#nodeID}.
	 * @param ctx the parse tree
	 */
	void enterNodeID(TurtleOBDAParser.NodeIDContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#nodeID}.
	 * @param ctx the parse tree
	 */
	void exitNodeID(TurtleOBDAParser.NodeIDContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#relativeURI}.
	 * @param ctx the parse tree
	 */
	void enterRelativeURI(TurtleOBDAParser.RelativeURIContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#relativeURI}.
	 * @param ctx the parse tree
	 */
	void exitRelativeURI(TurtleOBDAParser.RelativeURIContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#namespace}.
	 * @param ctx the parse tree
	 */
	void enterNamespace(TurtleOBDAParser.NamespaceContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#namespace}.
	 * @param ctx the parse tree
	 */
	void exitNamespace(TurtleOBDAParser.NamespaceContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#defaultNamespace}.
	 * @param ctx the parse tree
	 */
	void enterDefaultNamespace(TurtleOBDAParser.DefaultNamespaceContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#defaultNamespace}.
	 * @param ctx the parse tree
	 */
	void exitDefaultNamespace(TurtleOBDAParser.DefaultNamespaceContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(TurtleOBDAParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(TurtleOBDAParser.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#languageTag}.
	 * @param ctx the parse tree
	 */
	void enterLanguageTag(TurtleOBDAParser.LanguageTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#languageTag}.
	 * @param ctx the parse tree
	 */
	void exitLanguageTag(TurtleOBDAParser.LanguageTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#booleanLiteral}.
	 * @param ctx the parse tree
	 */
	void enterBooleanLiteral(TurtleOBDAParser.BooleanLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#booleanLiteral}.
	 * @param ctx the parse tree
	 */
	void exitBooleanLiteral(TurtleOBDAParser.BooleanLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#numericUnsigned}.
	 * @param ctx the parse tree
	 */
	void enterNumericUnsigned(TurtleOBDAParser.NumericUnsignedContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#numericUnsigned}.
	 * @param ctx the parse tree
	 */
	void exitNumericUnsigned(TurtleOBDAParser.NumericUnsignedContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#numericPositive}.
	 * @param ctx the parse tree
	 */
	void enterNumericPositive(TurtleOBDAParser.NumericPositiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#numericPositive}.
	 * @param ctx the parse tree
	 */
	void exitNumericPositive(TurtleOBDAParser.NumericPositiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link TurtleOBDAParser#numericNegative}.
	 * @param ctx the parse tree
	 */
	void enterNumericNegative(TurtleOBDAParser.NumericNegativeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TurtleOBDAParser#numericNegative}.
	 * @param ctx the parse tree
	 */
	void exitNumericNegative(TurtleOBDAParser.NumericNegativeContext ctx);
}