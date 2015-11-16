// Generated from obdalib-core/src/main/java/it/unibz/krdb/obda/parser/TurtleOBDA.g4 by ANTLR 4.5.1

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

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TurtleOBDAParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, BASE=2, PREFIX=3, FALSE=4, TRUE=5, REFERENCE=6, LTSIGN=7, RTSIGN=8, 
		SEMI=9, PERIOD=10, COMMA=11, LSQ_BRACKET=12, RSQ_BRACKET=13, LCR_BRACKET=14, 
		RCR_BRACKET=15, LPAREN=16, RPAREN=17, QUESTION=18, DOLLAR=19, QUOTE_DOUBLE=20, 
		QUOTE_SINGLE=21, APOSTROPHE=22, UNDERSCORE=23, MINUS=24, ASTERISK=25, 
		AMPERSAND=26, AT=27, EXCLAMATION=28, HASH=29, PERCENT=30, PLUS=31, EQUALS=32, 
		COLON=33, LESS=34, GREATER=35, SLASH=36, DOUBLE_SLASH=37, BACKSLASH=38, 
		BLANK=39, BLANK_PREFIX=40, TILDE=41, CARET=42, INTEGER=43, DOUBLE=44, 
		DECIMAL=45, INTEGER_POSITIVE=46, INTEGER_NEGATIVE=47, DOUBLE_POSITIVE=48, 
		DOUBLE_NEGATIVE=49, DECIMAL_POSITIVE=50, DECIMAL_NEGATIVE=51, VARNAME=52, 
		NCNAME=53, NCNAME_EXT=54, NAMESPACE=55, PREFIXED_NAME=56, STRING_WITH_QUOTE=57, 
		STRING_WITH_QUOTE_DOUBLE=58, STRING_WITH_BRACKET=59, STRING_WITH_CURLY_BRACKET=60, 
		STRING_URI=61, WS=62;
	public static final int
		RULE_parse = 0, RULE_directiveStatement = 1, RULE_triplesStatement = 2, 
		RULE_directive = 3, RULE_base = 4, RULE_prefixID = 5, RULE_triples = 6, 
		RULE_predicateObjectList = 7, RULE_verb = 8, RULE_objectList = 9, RULE_subject = 10, 
		RULE_predicate = 11, RULE_object = 12, RULE_resource = 13, RULE_uriref = 14, 
		RULE_qname = 15, RULE_blank = 16, RULE_variable = 17, RULE_function = 18, 
		RULE_typedLiteral = 19, RULE_language = 20, RULE_terms = 21, RULE_term = 22, 
		RULE_literal = 23, RULE_stringLiteral = 24, RULE_dataTypeString = 25, 
		RULE_numericLiteral = 26, RULE_nodeID = 27, RULE_relativeURI = 28, RULE_namespace = 29, 
		RULE_defaultNamespace = 30, RULE_name = 31, RULE_languageTag = 32, RULE_booleanLiteral = 33, 
		RULE_numericUnsigned = 34, RULE_numericPositive = 35, RULE_numericNegative = 36;
	public static final String[] ruleNames = {
		"parse", "directiveStatement", "triplesStatement", "directive", "base", 
		"prefixID", "triples", "predicateObjectList", "verb", "objectList", "subject", 
		"predicate", "object", "resource", "uriref", "qname", "blank", "variable", 
		"function", "typedLiteral", "language", "terms", "term", "literal", "stringLiteral", 
		"dataTypeString", "numericLiteral", "nodeID", "relativeURI", "namespace", 
		"defaultNamespace", "name", "languageTag", "booleanLiteral", "numericUnsigned", 
		"numericPositive", "numericNegative"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'a'", null, null, null, null, "'^^'", "'<\"'", "'\">'", "';'", 
		"'.'", "','", "'['", "']'", "'{'", "'}'", "'('", "')'", "'?'", "'$'", 
		"'\"'", "'''", "'`'", "'_'", "'-'", "'*'", "'&'", "'@'", "'!'", "'#'", 
		"'%'", "'+'", "'='", "':'", "'<'", "'>'", "'/'", "'//'", "'\\'", "'[]'", 
		"'_:'", "'~'", "'^'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, "BASE", "PREFIX", "FALSE", "TRUE", "REFERENCE", "LTSIGN", 
		"RTSIGN", "SEMI", "PERIOD", "COMMA", "LSQ_BRACKET", "RSQ_BRACKET", "LCR_BRACKET", 
		"RCR_BRACKET", "LPAREN", "RPAREN", "QUESTION", "DOLLAR", "QUOTE_DOUBLE", 
		"QUOTE_SINGLE", "APOSTROPHE", "UNDERSCORE", "MINUS", "ASTERISK", "AMPERSAND", 
		"AT", "EXCLAMATION", "HASH", "PERCENT", "PLUS", "EQUALS", "COLON", "LESS", 
		"GREATER", "SLASH", "DOUBLE_SLASH", "BACKSLASH", "BLANK", "BLANK_PREFIX", 
		"TILDE", "CARET", "INTEGER", "DOUBLE", "DECIMAL", "INTEGER_POSITIVE", 
		"INTEGER_NEGATIVE", "DOUBLE_POSITIVE", "DOUBLE_NEGATIVE", "DECIMAL_POSITIVE", 
		"DECIMAL_NEGATIVE", "VARNAME", "NCNAME", "NCNAME_EXT", "NAMESPACE", "PREFIXED_NAME", 
		"STRING_WITH_QUOTE", "STRING_WITH_QUOTE_DOUBLE", "STRING_WITH_BRACKET", 
		"STRING_WITH_CURLY_BRACKET", "STRING_URI", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "TurtleOBDA.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


	/** Map of directives */
	private HashMap<String, String> directives = new HashMap<String, String>();

	/** The current subject term */
	private Term currentSubject;

	/** All variables */
	private Set<Term> variableSet = new HashSet<Term>();

	/** A factory to construct the predicates and terms */
	private static final OBDADataFactory dfac = OBDADataFactoryImpl.getInstance();
	private static final DatatypeFactory dtfac = OBDADataFactoryImpl.getInstance().getDatatypeFactory();

	private String error = "";

	public String getError() {
	   return error;
	}


	private String removeBrackets(String text) {
	   return text.substring(1, text.length()-1);
	}

		private Term construct(String text) {
		   Term toReturn = null;
		   final String PLACEHOLDER = "{}";
		   List<Term> terms = new LinkedList<Term>();
		   List<FormatString> tokens = parse(text);
		   int size = tokens.size();
		   if (size == 1) {
		      FormatString token = tokens.get(0);
		      if (token instanceof FixedString) {
		          ValueConstant uriTemplate = dfac.getConstantLiteral(token.toString()); // a single URI template
		          toReturn = dfac.getUriTemplate(uriTemplate);
		      }
		      else if (token instanceof ColumnString) {
		         ValueConstant uriTemplate = dfac.getConstantLiteral(PLACEHOLDER); // a single URI template
		         Variable column = dfac.getVariable(token.toString());
		         terms.add(0, uriTemplate);
		         terms.add(column);
		         toReturn = dfac.getUriTemplate(terms);
		      }
		   }
		   else {
		      StringBuilder sb = new StringBuilder();
		      for(FormatString token : tokens) {
		         if (token instanceof FixedString) { // if part of URI template
		            sb.append(token.toString());
		         }
		         else if (token instanceof ColumnString) {
		            sb.append(PLACEHOLDER);
		            Variable column = dfac.getVariable(token.toString());
		            terms.add(column);
		         }
		      }
		      ValueConstant uriTemplate = dfac.getConstantLiteral(sb.toString()); // complete URI template
		      terms.add(0, uriTemplate);
		      toReturn = dfac.getUriTemplate(terms);
		   }
		   return toReturn;
		}
		
	// Column placeholder pattern
	private static final String formatSpecifier = "\\{([^\\}]+)?\\}";
	private static Pattern chPattern = Pattern.compile(formatSpecifier);

	private List<FormatString> parse(String text) {
	   List<FormatString> toReturn = new ArrayList<FormatString>();
	   Matcher m = chPattern.matcher(text);
	   int i = 0;
	   while (i < text.length()) {
	      if (m.find(i)) {
	         if (m.start() != i) {
	            toReturn.add(new FixedString(text.substring(i, m.start())));
	         }
	         String value = m.group(1);
	         toReturn.add(new ColumnString(value));
	         i = m.end();
	      }
	      else {
	         toReturn.add(new FixedString(text.substring(i)));
	         break;
	      }
	   }
	   return toReturn;
	}

	private interface FormatString {
	   int index();
	   String toString();
	}

	private class FixedString implements FormatString {
	   private String s;
	   FixedString(String s) { this.s = s; }
	   @Override public int index() { return -1; }  // flag code for fixed string
	   @Override public String toString() { return s; }
	}

	private class ColumnString implements FormatString {
	   private String s;
	   ColumnString(String s) { this.s = s; }
	   @Override public int index() { return 0; }  // flag code for column string
	   @Override public String toString() { return s; }
	}

		//this function distinguishes curly bracket with 
		//back slash "\{" from curly bracket "{" 
		private int getIndexOfCurlyB(String str){
		   int i;
		   int j;
		   i = str.indexOf("{");
		   j = str.indexOf("\\{");
		      while((i-1 == j) &&(j != -1)){		
			i = str.indexOf("{",i+1);
			j = str.indexOf("\\{",j+1);		
		      }	
		  return i;
		}
		
		//in case of concat this function parses the literal 
		//and adds parsed constant literals and template literal to terms list
		private ArrayList<Term> addToTermsList(String str){
		   ArrayList<Term> terms = new ArrayList<Term>();
		   int i,j;
		   String st;
		   str = str.substring(1, str.length()-1);
		   while(str.contains("{")){
		      i = getIndexOfCurlyB(str);
		      if (i > 0){
		    	 st = str.substring(0,i);
		    	 st = st.replace("\\\\", "");
		         terms.add(dfac.getConstantLiteral(st));
		         str = str.substring(str.indexOf("{", i), str.length());
		      }else if (i == 0){
		         j = str.indexOf("}");
		         terms.add(dfac.getVariable(str.substring(1,j)));
		         str = str.substring(j+1,str.length());
		      } else {
		    	  break;
		      }
		   }
		   if(!str.equals("")){
		      str = str.replace("\\\\", "");
		      terms.add(dfac.getConstantLiteral(str));
		   }
		   return terms;
		}
		
		//this function returns nested concats 
		//in case of more than two terms need to be concatted
		private Function getNestedConcat(String str){
		   ArrayList<Term> terms = new ArrayList<Term>();
		   terms = addToTermsList(str);
		   Function f = dfac.getFunctionConcat(terms.get(0),terms.get(1));
	           for(int j=2;j<terms.size();j++){
	              f = dfac.getFunctionConcat(f,terms.get(j));
	           }
		   return f;
		}

	/**
	 * This methods construct an atom from a triple 
	 * 
	 * For the input (subject, pred, object), the result is 
	 * <ul>
	 *  <li> object(subject), if pred == rdf:type and subject is grounded ; </li>
	 *  <li> predicate(subject, object), if pred != rdf:type and predicate is grounded ; </li>
	 *  <li> triple(subject, pred, object), otherwise (it is a higher order atom). </li>
	 * </ul>
	 */
		private Function makeAtom(Term subject, Term pred, Term object) {
		     Function atom = null;

		        if (isRDFType(pred)) {
			             if (object instanceof  Function) {
			                  if(QueryUtils.isGrounded(object)) {
			                      ValueConstant c = ((ValueConstant) ((Function) object).getTerm(0));  // it has to be a URI constant
			                      Predicate predicate = dfac.getClassPredicate(c.getValue());
			                      atom = dfac.getFunction(predicate, subject);
			                  } else {
			                       atom = dfac.getTripleAtom(subject, pred, object);
			                  }
			             }
			             else if (object instanceof  Variable){
			                  Term uriOfPred = dfac.getUriTemplate(pred);
			                  Term uriOfObject = dfac.getUriTemplate(object);
			                  atom = dfac.getTripleAtom(subject, uriOfPred,  uriOfObject);
			              }
			             else {
			                  throw new IllegalArgumentException("parser cannot handle object " + object);
			              }
			        } else if( ! QueryUtils.isGrounded(pred) ){
			             atom = dfac.getTripleAtom(subject, pred,  object);
			        } else {
	                			             //Predicate predicate = dfac.getPredicate(pred.toString(), 2); // the data type cannot be determined here!
	                			             Predicate predicate;
	                			             if(pred instanceof Function) {
	                							 ValueConstant pr = (ValueConstant) ((Function) pred).getTerm(0);
	                							 if (object instanceof Variable) {
	                								 predicate = dfac.getPredicate(pr.getValue(), 2);
	                							 } else {
	                								 if (object instanceof Function) {
	                									 if (((Function) object).getFunctionSymbol() instanceof URITemplatePredicate) {

	                										 predicate = dfac.getObjectPropertyPredicate(pr.getValue());
	                									 } else {
	                										 predicate = dfac.getDataPropertyPredicate(pr.getValue());
	                									 }
	                								 }
	                									 else {
	                										 throw new IllegalArgumentException("parser cannot handle object " + object);
	                									 }
	                							 }
	                						 }else {
	                			                  throw new IllegalArgumentException("predicate should be a URI Function");
	                			             }
	                			             atom = dfac.getFunction(predicate, subject, object);
	                			       }
	                			       return atom;
		  }


	private static boolean isRDFType(Term pred) {
	//		if (pred instanceof Constant && ((Constant) pred).getValue().equals(OBDAVocabulary.RDF_TYPE)) {
	//			return true;
	//		}
			if (pred instanceof Function && ((Function) pred).getTerm(0) instanceof Constant ) {
				String c= ((Constant) ((Function) pred).getTerm(0)).getValue();
				return c.equals(OBDAVocabulary.RDF_TYPE);
			}	
			return false;
		}


	public TurtleOBDAParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ParseContext extends ParserRuleContext {
		public List<Function> value;
		public TriplesStatementContext t1;
		public TriplesStatementContext t2;
		public TerminalNode EOF() { return getToken(TurtleOBDAParser.EOF, 0); }
		public List<TriplesStatementContext> triplesStatement() {
			return getRuleContexts(TriplesStatementContext.class);
		}
		public TriplesStatementContext triplesStatement(int i) {
			return getRuleContext(TriplesStatementContext.class,i);
		}
		public List<DirectiveStatementContext> directiveStatement() {
			return getRuleContexts(DirectiveStatementContext.class);
		}
		public DirectiveStatementContext directiveStatement(int i) {
			return getRuleContext(DirectiveStatementContext.class,i);
		}
		public ParseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parse; }
	}

	public final ParseContext parse() throws RecognitionException {
		ParseContext _localctx = new ParseContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_parse);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(77);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(74);
				directiveStatement();
				}
				}
				setState(79);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(80);
			((ParseContext)_localctx).t1 = triplesStatement();

			      ((ParseContext)_localctx).value =   ((ParseContext)_localctx).t1.value;
			    
			setState(87);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PREFIXED_NAME) | (1L << STRING_WITH_BRACKET) | (1L << STRING_WITH_CURLY_BRACKET))) != 0)) {
				{
				{
				setState(82);
				((ParseContext)_localctx).t2 = triplesStatement();

				      List<Function> additionalTriples = ((ParseContext)_localctx).t2.value;
				      if (additionalTriples != null) {
				        // If there are additional triple statements then just add to the existing body
				        List<Function> existingBody = _localctx.value;
				        existingBody.addAll(additionalTriples);
				      }
				    
				}
				}
				setState(89);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(90);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DirectiveStatementContext extends ParserRuleContext {
		public DirectiveContext directive() {
			return getRuleContext(DirectiveContext.class,0);
		}
		public TerminalNode PERIOD() { return getToken(TurtleOBDAParser.PERIOD, 0); }
		public DirectiveStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_directiveStatement; }
	}

	public final DirectiveStatementContext directiveStatement() throws RecognitionException {
		DirectiveStatementContext _localctx = new DirectiveStatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_directiveStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(92);
			directive();
			setState(93);
			match(PERIOD);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TriplesStatementContext extends ParserRuleContext {
		public List<Function> value;
		public TriplesContext triples;
		public TriplesContext triples() {
			return getRuleContext(TriplesContext.class,0);
		}
		public TerminalNode PERIOD() { return getToken(TurtleOBDAParser.PERIOD, 0); }
		public List<TerminalNode> WS() { return getTokens(TurtleOBDAParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(TurtleOBDAParser.WS, i);
		}
		public TriplesStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_triplesStatement; }
	}

	public final TriplesStatementContext triplesStatement() throws RecognitionException {
		TriplesStatementContext _localctx = new TriplesStatementContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_triplesStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			((TriplesStatementContext)_localctx).triples = triples();
			setState(99);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(96);
				match(WS);
				}
				}
				setState(101);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(102);
			match(PERIOD);
			 ((TriplesStatementContext)_localctx).value =  ((TriplesStatementContext)_localctx).triples.value; 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DirectiveContext extends ParserRuleContext {
		public BaseContext base() {
			return getRuleContext(BaseContext.class,0);
		}
		public PrefixIDContext prefixID() {
			return getRuleContext(PrefixIDContext.class,0);
		}
		public DirectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_directive; }
	}

	public final DirectiveContext directive() throws RecognitionException {
		DirectiveContext _localctx = new DirectiveContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_directive);
		try {
			setState(107);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(105);
				base();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(106);
				prefixID();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BaseContext extends ParserRuleContext {
		public TerminalNode AT() { return getToken(TurtleOBDAParser.AT, 0); }
		public TerminalNode BASE() { return getToken(TurtleOBDAParser.BASE, 0); }
		public UrirefContext uriref() {
			return getRuleContext(UrirefContext.class,0);
		}
		public BaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_base; }
	}

	public final BaseContext base() throws RecognitionException {
		BaseContext _localctx = new BaseContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_base);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(109);
			match(AT);
			setState(110);
			match(BASE);
			setState(111);
			uriref();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PrefixIDContext extends ParserRuleContext {
		public NamespaceContext namespace;
		public DefaultNamespaceContext defaultNamespace;
		public UrirefContext uriref;
		public TerminalNode AT() { return getToken(TurtleOBDAParser.AT, 0); }
		public TerminalNode PREFIX() { return getToken(TurtleOBDAParser.PREFIX, 0); }
		public UrirefContext uriref() {
			return getRuleContext(UrirefContext.class,0);
		}
		public NamespaceContext namespace() {
			return getRuleContext(NamespaceContext.class,0);
		}
		public DefaultNamespaceContext defaultNamespace() {
			return getRuleContext(DefaultNamespaceContext.class,0);
		}
		public PrefixIDContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_prefixID; }
	}

	public final PrefixIDContext prefixID() throws RecognitionException {
		PrefixIDContext _localctx = new PrefixIDContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_prefixID);

		  String prefix = "";

		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(113);
			match(AT);
			setState(114);
			match(PREFIX);
			setState(121);
			switch (_input.LA(1)) {
			case NAMESPACE:
				{
				setState(115);
				((PrefixIDContext)_localctx).namespace = namespace();
				 prefix = (((PrefixIDContext)_localctx).namespace!=null?_input.getText(((PrefixIDContext)_localctx).namespace.start,((PrefixIDContext)_localctx).namespace.stop):null); 
				}
				break;
			case COLON:
				{
				setState(118);
				((PrefixIDContext)_localctx).defaultNamespace = defaultNamespace();
				 prefix = (((PrefixIDContext)_localctx).defaultNamespace!=null?_input.getText(((PrefixIDContext)_localctx).defaultNamespace.start,((PrefixIDContext)_localctx).defaultNamespace.stop):null); 
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(123);
			((PrefixIDContext)_localctx).uriref = uriref();

			      String uriref = ((PrefixIDContext)_localctx).uriref.value;
			      directives.put(prefix.substring(0, prefix.length()-1), uriref); // remove the end colon
			    
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TriplesContext extends ParserRuleContext {
		public List<Function> value;
		public SubjectContext subject;
		public PredicateObjectListContext predicateObjectList;
		public SubjectContext subject() {
			return getRuleContext(SubjectContext.class,0);
		}
		public PredicateObjectListContext predicateObjectList() {
			return getRuleContext(PredicateObjectListContext.class,0);
		}
		public TriplesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_triples; }
	}

	public final TriplesContext triples() throws RecognitionException {
		TriplesContext _localctx = new TriplesContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_triples);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
			((TriplesContext)_localctx).subject = subject();
			 currentSubject = ((TriplesContext)_localctx).subject.value; 
			setState(128);
			((TriplesContext)_localctx).predicateObjectList = predicateObjectList();

			      ((TriplesContext)_localctx).value =  ((TriplesContext)_localctx).predicateObjectList.value;
			    
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PredicateObjectListContext extends ParserRuleContext {
		public List<Function> value;
		public VerbContext v1;
		public ObjectListContext l1;
		public VerbContext v2;
		public ObjectListContext l2;
		public List<VerbContext> verb() {
			return getRuleContexts(VerbContext.class);
		}
		public VerbContext verb(int i) {
			return getRuleContext(VerbContext.class,i);
		}
		public List<ObjectListContext> objectList() {
			return getRuleContexts(ObjectListContext.class);
		}
		public ObjectListContext objectList(int i) {
			return getRuleContext(ObjectListContext.class,i);
		}
		public List<TerminalNode> SEMI() { return getTokens(TurtleOBDAParser.SEMI); }
		public TerminalNode SEMI(int i) {
			return getToken(TurtleOBDAParser.SEMI, i);
		}
		public PredicateObjectListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicateObjectList; }
	}

	public final PredicateObjectListContext predicateObjectList() throws RecognitionException {
		PredicateObjectListContext _localctx = new PredicateObjectListContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_predicateObjectList);

		   ((PredicateObjectListContext)_localctx).value =  new LinkedList<Function>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(131);
			((PredicateObjectListContext)_localctx).v1 = verb();
			setState(132);
			((PredicateObjectListContext)_localctx).l1 = objectList();

			      for (Term object : ((PredicateObjectListContext)_localctx).l1.value) {
			        Function atom = makeAtom(currentSubject, ((PredicateObjectListContext)_localctx).v1.value, object);
			        _localctx.value.add(atom);
			      }
			    
			setState(141);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEMI) {
				{
				{
				setState(134);
				match(SEMI);
				setState(135);
				((PredicateObjectListContext)_localctx).v2 = verb();
				setState(136);
				((PredicateObjectListContext)_localctx).l2 = objectList();

				      for (Term object : ((PredicateObjectListContext)_localctx).l2.value) {
				        Function atom = makeAtom(currentSubject, ((PredicateObjectListContext)_localctx).v2.value, object);
				        _localctx.value.add(atom);
				      }
				    
				}
				}
				setState(143);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VerbContext extends ParserRuleContext {
		public Term value;
		public PredicateContext predicate;
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public VerbContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_verb; }
	}

	public final VerbContext verb() throws RecognitionException {
		VerbContext _localctx = new VerbContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_verb);
		try {
			setState(149);
			switch (_input.LA(1)) {
			case PREFIXED_NAME:
			case STRING_WITH_BRACKET:
				enterOuterAlt(_localctx, 1);
				{
				setState(144);
				((VerbContext)_localctx).predicate = predicate();
				 ((VerbContext)_localctx).value =  ((VerbContext)_localctx).predicate.value; 
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 2);
				{
				setState(147);
				match(T__0);

				  Term constant = dfac.getConstantLiteral(OBDAVocabulary.RDF_TYPE);
				  ((VerbContext)_localctx).value =  dfac.getUriTemplate(constant);
				  
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ObjectListContext extends ParserRuleContext {
		public List<Term> value;
		public ObjectContext o1;
		public ObjectContext o2;
		public List<ObjectContext> object() {
			return getRuleContexts(ObjectContext.class);
		}
		public ObjectContext object(int i) {
			return getRuleContext(ObjectContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(TurtleOBDAParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(TurtleOBDAParser.COMMA, i);
		}
		public ObjectListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objectList; }
	}

	public final ObjectListContext objectList() throws RecognitionException {
		ObjectListContext _localctx = new ObjectListContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_objectList);

		  ((ObjectListContext)_localctx).value =  new ArrayList<Term>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(151);
			((ObjectListContext)_localctx).o1 = object();
			 _localctx.value.add(((ObjectListContext)_localctx).o1.value); 
			setState(159);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(153);
				match(COMMA);
				setState(154);
				((ObjectListContext)_localctx).o2 = object();
				 _localctx.value.add(((ObjectListContext)_localctx).o2.value); 
				}
				}
				setState(161);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SubjectContext extends ParserRuleContext {
		public Term value;
		public ResourceContext resource;
		public VariableContext variable;
		public ResourceContext resource() {
			return getRuleContext(ResourceContext.class,0);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public SubjectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subject; }
	}

	public final SubjectContext subject() throws RecognitionException {
		SubjectContext _localctx = new SubjectContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_subject);
		try {
			setState(168);
			switch (_input.LA(1)) {
			case PREFIXED_NAME:
			case STRING_WITH_BRACKET:
				enterOuterAlt(_localctx, 1);
				{
				setState(162);
				((SubjectContext)_localctx).resource = resource();
				 ((SubjectContext)_localctx).value =  ((SubjectContext)_localctx).resource.value; 
				}
				break;
			case STRING_WITH_CURLY_BRACKET:
				enterOuterAlt(_localctx, 2);
				{
				setState(165);
				((SubjectContext)_localctx).variable = variable();
				 ((SubjectContext)_localctx).value =  ((SubjectContext)_localctx).variable.value; 
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PredicateContext extends ParserRuleContext {
		public Term value;
		public ResourceContext resource;
		public ResourceContext resource() {
			return getRuleContext(ResourceContext.class,0);
		}
		public PredicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicate; }
	}

	public final PredicateContext predicate() throws RecognitionException {
		PredicateContext _localctx = new PredicateContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			((PredicateContext)_localctx).resource = resource();

			  	((PredicateContext)_localctx).value =  ((PredicateContext)_localctx).resource.value; 
			//      Term nl = ((PredicateContext)_localctx).resource.value;
			//      if (nl instanceof URIConstant) {
			//        URIConstant c = (URIConstant) nl;
			//        ((PredicateContext)_localctx).value =  c.getValue();
			//      } else {
			//        throw new RuntimeException("Unsupported predicate syntax: " + nl.toString());
			//      }
			    
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ObjectContext extends ParserRuleContext {
		public Term value;
		public ResourceContext resource;
		public LiteralContext literal;
		public TypedLiteralContext typedLiteral;
		public VariableContext variable;
		public ResourceContext resource() {
			return getRuleContext(ResourceContext.class,0);
		}
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public TypedLiteralContext typedLiteral() {
			return getRuleContext(TypedLiteralContext.class,0);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public ObjectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_object; }
	}

	public final ObjectContext object() throws RecognitionException {
		ObjectContext _localctx = new ObjectContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_object);
		try {
			setState(185);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(173);
				((ObjectContext)_localctx).resource = resource();
				 ((ObjectContext)_localctx).value =  ((ObjectContext)_localctx).resource.value; 
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(176);
				((ObjectContext)_localctx).literal = literal();
				 ((ObjectContext)_localctx).value =  ((ObjectContext)_localctx).literal.value; 
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(179);
				((ObjectContext)_localctx).typedLiteral = typedLiteral();
				 ((ObjectContext)_localctx).value =  ((ObjectContext)_localctx).typedLiteral.value; 
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(182);
				((ObjectContext)_localctx).variable = variable();
				 ((ObjectContext)_localctx).value =  ((ObjectContext)_localctx).variable.value; 
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ResourceContext extends ParserRuleContext {
		public Term value;
		public UrirefContext uriref;
		public QnameContext qname;
		public UrirefContext uriref() {
			return getRuleContext(UrirefContext.class,0);
		}
		public QnameContext qname() {
			return getRuleContext(QnameContext.class,0);
		}
		public ResourceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resource; }
	}

	public final ResourceContext resource() throws RecognitionException {
		ResourceContext _localctx = new ResourceContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_resource);
		try {
			setState(193);
			switch (_input.LA(1)) {
			case STRING_WITH_BRACKET:
				enterOuterAlt(_localctx, 1);
				{
				setState(187);
				((ResourceContext)_localctx).uriref = uriref();
				 ((ResourceContext)_localctx).value =  construct(((ResourceContext)_localctx).uriref.value); 
				}
				break;
			case PREFIXED_NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(190);
				((ResourceContext)_localctx).qname = qname();
				 ((ResourceContext)_localctx).value =  construct(((ResourceContext)_localctx).qname.value); 
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UrirefContext extends ParserRuleContext {
		public String value;
		public Token STRING_WITH_BRACKET;
		public TerminalNode STRING_WITH_BRACKET() { return getToken(TurtleOBDAParser.STRING_WITH_BRACKET, 0); }
		public UrirefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_uriref; }
	}

	public final UrirefContext uriref() throws RecognitionException {
		UrirefContext _localctx = new UrirefContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_uriref);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(195);
			((UrirefContext)_localctx).STRING_WITH_BRACKET = match(STRING_WITH_BRACKET);
			 ((UrirefContext)_localctx).value =  removeBrackets((((UrirefContext)_localctx).STRING_WITH_BRACKET!=null?((UrirefContext)_localctx).STRING_WITH_BRACKET.getText():null)); 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QnameContext extends ParserRuleContext {
		public String value;
		public Token PREFIXED_NAME;
		public TerminalNode PREFIXED_NAME() { return getToken(TurtleOBDAParser.PREFIXED_NAME, 0); }
		public QnameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qname; }
	}

	public final QnameContext qname() throws RecognitionException {
		QnameContext _localctx = new QnameContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_qname);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(198);
			((QnameContext)_localctx).PREFIXED_NAME = match(PREFIXED_NAME);

			      String[] tokens = (((QnameContext)_localctx).PREFIXED_NAME!=null?((QnameContext)_localctx).PREFIXED_NAME.getText():null).split(":", 2);
			      String uri = directives.get(tokens[0]);  // the first token is the prefix
			      ((QnameContext)_localctx).value =  uri + tokens[1];  // the second token is the local name
			    
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlankContext extends ParserRuleContext {
		public NodeIDContext nodeID() {
			return getRuleContext(NodeIDContext.class,0);
		}
		public TerminalNode BLANK() { return getToken(TurtleOBDAParser.BLANK, 0); }
		public BlankContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blank; }
	}

	public final BlankContext blank() throws RecognitionException {
		BlankContext _localctx = new BlankContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_blank);
		try {
			setState(203);
			switch (_input.LA(1)) {
			case BLANK_PREFIX:
				enterOuterAlt(_localctx, 1);
				{
				setState(201);
				nodeID();
				}
				break;
			case BLANK:
				enterOuterAlt(_localctx, 2);
				{
				setState(202);
				match(BLANK);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableContext extends ParserRuleContext {
		public Variable value;
		public Token STRING_WITH_CURLY_BRACKET;
		public TerminalNode STRING_WITH_CURLY_BRACKET() { return getToken(TurtleOBDAParser.STRING_WITH_CURLY_BRACKET, 0); }
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(205);
			((VariableContext)_localctx).STRING_WITH_CURLY_BRACKET = match(STRING_WITH_CURLY_BRACKET);

			      ((VariableContext)_localctx).value =  dfac.getVariable(removeBrackets((((VariableContext)_localctx).STRING_WITH_CURLY_BRACKET!=null?((VariableContext)_localctx).STRING_WITH_CURLY_BRACKET.getText():null)));
			      variableSet.add(_localctx.value);
			    
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionContext extends ParserRuleContext {
		public Function value;
		public ResourceContext resource;
		public TermsContext terms;
		public ResourceContext resource() {
			return getRuleContext(ResourceContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(TurtleOBDAParser.LPAREN, 0); }
		public TermsContext terms() {
			return getRuleContext(TermsContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(TurtleOBDAParser.RPAREN, 0); }
		public FunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function; }
	}

	public final FunctionContext function() throws RecognitionException {
		FunctionContext _localctx = new FunctionContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(208);
			((FunctionContext)_localctx).resource = resource();
			setState(209);
			match(LPAREN);
			setState(210);
			((FunctionContext)_localctx).terms = terms();
			setState(211);
			match(RPAREN);

			      String functionName = ((FunctionContext)_localctx).resource.value.toString();
			      int arity = ((FunctionContext)_localctx).terms.value.size();
			      Predicate functionSymbol = dfac.getPredicate(functionName, arity);
			      ((FunctionContext)_localctx).value =  dfac.getFunction(functionSymbol, ((FunctionContext)_localctx).terms.value);
			    
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypedLiteralContext extends ParserRuleContext {
		public Function value;
		public VariableContext variable;
		public LanguageContext language;
		public ResourceContext resource;
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public TerminalNode AT() { return getToken(TurtleOBDAParser.AT, 0); }
		public LanguageContext language() {
			return getRuleContext(LanguageContext.class,0);
		}
		public TerminalNode REFERENCE() { return getToken(TurtleOBDAParser.REFERENCE, 0); }
		public ResourceContext resource() {
			return getRuleContext(ResourceContext.class,0);
		}
		public TypedLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typedLiteral; }
	}

	public final TypedLiteralContext typedLiteral() throws RecognitionException {
		TypedLiteralContext _localctx = new TypedLiteralContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_typedLiteral);
		try {
			setState(224);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(214);
				((TypedLiteralContext)_localctx).variable = variable();
				setState(215);
				match(AT);
				setState(216);
				((TypedLiteralContext)_localctx).language = language();

				      Variable var = ((TypedLiteralContext)_localctx).variable.value;
				      Term lang = ((TypedLiteralContext)_localctx).language.value;   
				      ((TypedLiteralContext)_localctx).value =  dfac.getTypedTerm(var, lang);

				    
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(219);
				((TypedLiteralContext)_localctx).variable = variable();
				setState(220);
				match(REFERENCE);
				setState(221);
				((TypedLiteralContext)_localctx).resource = resource();

				      Variable var = ((TypedLiteralContext)_localctx).variable.value;
				      //String functionName = ((TypedLiteralContext)_localctx).resource.value.toString();
				      // ((TypedLiteralContext)_localctx).resource.value must be a URIConstant
				    String functionName = null;
				    if (((TypedLiteralContext)_localctx).resource.value instanceof Function){
				       functionName = ((ValueConstant) ((Function)((TypedLiteralContext)_localctx).resource.value).getTerm(0)).getValue();
				    } else {
				        throw new IllegalArgumentException("((TypedLiteralContext)_localctx).resource.value should be an URI");
				    }
				    Predicate.COL_TYPE type = dtfac.getDatatype(functionName);
				    if (type == null)  
				 	  throw new RuntimeException("ERROR. A mapping involves an unsupported datatype. \nOffending datatype:" + functionName);
				    
				      ((TypedLiteralContext)_localctx).value =  dfac.getTypedTerm(var, type);

					
				     
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LanguageContext extends ParserRuleContext {
		public Term value;
		public LanguageTagContext languageTag;
		public VariableContext variable;
		public LanguageTagContext languageTag() {
			return getRuleContext(LanguageTagContext.class,0);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public LanguageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_language; }
	}

	public final LanguageContext language() throws RecognitionException {
		LanguageContext _localctx = new LanguageContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_language);
		try {
			setState(232);
			switch (_input.LA(1)) {
			case VARNAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(226);
				((LanguageContext)_localctx).languageTag = languageTag();

				    	((LanguageContext)_localctx).value =  dfac.getConstantLiteral((((LanguageContext)_localctx).languageTag!=null?_input.getText(((LanguageContext)_localctx).languageTag.start,((LanguageContext)_localctx).languageTag.stop):null).toLowerCase(), COL_TYPE.STRING);
				    
				}
				break;
			case STRING_WITH_CURLY_BRACKET:
				enterOuterAlt(_localctx, 2);
				{
				setState(229);
				((LanguageContext)_localctx).variable = variable();

				    	((LanguageContext)_localctx).value =  ((LanguageContext)_localctx).variable.value;
				    
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TermsContext extends ParserRuleContext {
		public Vector<Term> value;
		public TermContext t1;
		public TermContext t2;
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(TurtleOBDAParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(TurtleOBDAParser.COMMA, i);
		}
		public TermsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_terms; }
	}

	public final TermsContext terms() throws RecognitionException {
		TermsContext _localctx = new TermsContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_terms);

		  ((TermsContext)_localctx).value =  new Vector<Term>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(234);
			((TermsContext)_localctx).t1 = term();
			 _localctx.value.add(((TermsContext)_localctx).t1.value); 
			setState(242);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(236);
				match(COMMA);
				setState(237);
				((TermsContext)_localctx).t2 = term();
				 _localctx.value.add(((TermsContext)_localctx).t2.value); 
				}
				}
				setState(244);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TermContext extends ParserRuleContext {
		public Term value;
		public FunctionContext function;
		public VariableContext variable;
		public LiteralContext literal;
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_term);
		try {
			setState(254);
			switch (_input.LA(1)) {
			case PREFIXED_NAME:
			case STRING_WITH_BRACKET:
				enterOuterAlt(_localctx, 1);
				{
				setState(245);
				((TermContext)_localctx).function = function();
				 ((TermContext)_localctx).value =  ((TermContext)_localctx).function.value; 
				}
				break;
			case STRING_WITH_CURLY_BRACKET:
				enterOuterAlt(_localctx, 2);
				{
				setState(248);
				((TermContext)_localctx).variable = variable();
				 ((TermContext)_localctx).value =  ((TermContext)_localctx).variable.value; 
				}
				break;
			case FALSE:
			case TRUE:
			case INTEGER:
			case DOUBLE:
			case DECIMAL:
			case INTEGER_POSITIVE:
			case INTEGER_NEGATIVE:
			case DOUBLE_POSITIVE:
			case DOUBLE_NEGATIVE:
			case DECIMAL_POSITIVE:
			case DECIMAL_NEGATIVE:
			case STRING_WITH_QUOTE_DOUBLE:
				enterOuterAlt(_localctx, 3);
				{
				setState(251);
				((TermContext)_localctx).literal = literal();
				 ((TermContext)_localctx).value =  ((TermContext)_localctx).literal.value; 
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LiteralContext extends ParserRuleContext {
		public Term value;
		public StringLiteralContext stringLiteral;
		public LanguageContext language;
		public DataTypeStringContext dataTypeString;
		public NumericLiteralContext numericLiteral;
		public BooleanLiteralContext booleanLiteral;
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public TerminalNode AT() { return getToken(TurtleOBDAParser.AT, 0); }
		public LanguageContext language() {
			return getRuleContext(LanguageContext.class,0);
		}
		public DataTypeStringContext dataTypeString() {
			return getRuleContext(DataTypeStringContext.class,0);
		}
		public NumericLiteralContext numericLiteral() {
			return getRuleContext(NumericLiteralContext.class,0);
		}
		public BooleanLiteralContext booleanLiteral() {
			return getRuleContext(BooleanLiteralContext.class,0);
		}
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_literal);
		int _la;
		try {
			setState(272);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(256);
				((LiteralContext)_localctx).stringLiteral = stringLiteral();
				setState(259);
				_la = _input.LA(1);
				if (_la==AT) {
					{
					setState(257);
					match(AT);
					setState(258);
					((LiteralContext)_localctx).language = language();
					}
				}


				       Term lang = ((LiteralContext)_localctx).language.value;
				       if ((((LiteralContext)_localctx).stringLiteral.value) instanceof Function){
				          Function f = (Function)((LiteralContext)_localctx).stringLiteral.value;
				          if (lang != null){
				             ((LiteralContext)_localctx).value =  dfac.getTypedTerm(f,lang);
				          }else{
				             ((LiteralContext)_localctx).value =  dfac.getTypedTerm(f, COL_TYPE.LITERAL);
				          }       
				       }else{
				          ValueConstant constant = (ValueConstant)((LiteralContext)_localctx).stringLiteral.value;
				          if (lang != null) {
					         ((LiteralContext)_localctx).value =  dfac.getTypedTerm(constant, lang);
				          } else {
				      	     ((LiteralContext)_localctx).value =  dfac.getTypedTerm(constant, COL_TYPE.LITERAL);
				          }
				       }
				    
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(263);
				((LiteralContext)_localctx).dataTypeString = dataTypeString();
				 ((LiteralContext)_localctx).value =  ((LiteralContext)_localctx).dataTypeString.value; 
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(266);
				((LiteralContext)_localctx).numericLiteral = numericLiteral();
				 ((LiteralContext)_localctx).value =  ((LiteralContext)_localctx).numericLiteral.value; 
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(269);
				((LiteralContext)_localctx).booleanLiteral = booleanLiteral();
				 ((LiteralContext)_localctx).value =  ((LiteralContext)_localctx).booleanLiteral.value; 
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StringLiteralContext extends ParserRuleContext {
		public Term value;
		public Token STRING_WITH_QUOTE_DOUBLE;
		public TerminalNode STRING_WITH_QUOTE_DOUBLE() { return getToken(TurtleOBDAParser.STRING_WITH_QUOTE_DOUBLE, 0); }
		public StringLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringLiteral; }
	}

	public final StringLiteralContext stringLiteral() throws RecognitionException {
		StringLiteralContext _localctx = new StringLiteralContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_stringLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(274);
			((StringLiteralContext)_localctx).STRING_WITH_QUOTE_DOUBLE = match(STRING_WITH_QUOTE_DOUBLE);

			      String str = (((StringLiteralContext)_localctx).STRING_WITH_QUOTE_DOUBLE!=null?((StringLiteralContext)_localctx).STRING_WITH_QUOTE_DOUBLE.getText():null);
			      if (str.contains("{")){
			      	((StringLiteralContext)_localctx).value =  getNestedConcat(str);
			      }else{
			      	((StringLiteralContext)_localctx).value =  dfac.getConstantLiteral(str.substring(1, str.length()-1), COL_TYPE.LITERAL); // without the double quotes
			      }
			    
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DataTypeStringContext extends ParserRuleContext {
		public Term value;
		public StringLiteralContext stringLiteral;
		public ResourceContext resource;
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public TerminalNode REFERENCE() { return getToken(TurtleOBDAParser.REFERENCE, 0); }
		public ResourceContext resource() {
			return getRuleContext(ResourceContext.class,0);
		}
		public DataTypeStringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dataTypeString; }
	}

	public final DataTypeStringContext dataTypeString() throws RecognitionException {
		DataTypeStringContext _localctx = new DataTypeStringContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_dataTypeString);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(277);
			((DataTypeStringContext)_localctx).stringLiteral = stringLiteral();
			setState(278);
			match(REFERENCE);
			setState(279);
			((DataTypeStringContext)_localctx).resource = resource();

			      if ((((DataTypeStringContext)_localctx).stringLiteral.value) instanceof Function){
			          Function f = (Function)((DataTypeStringContext)_localctx).stringLiteral.value;
			          ((DataTypeStringContext)_localctx).value =  dfac.getTypedTerm(f, COL_TYPE.LITERAL);
			      }else{
			          ValueConstant constant = (ValueConstant)((DataTypeStringContext)_localctx).stringLiteral.value;
			          String functionName = ((DataTypeStringContext)_localctx).resource.value.toString();
			          Predicate functionSymbol = null;
			          if (((DataTypeStringContext)_localctx).resource.value instanceof Function){
				    functionName = ( (ValueConstant) ((Function)((DataTypeStringContext)_localctx).resource.value).getTerm(0) ).getValue();
			          }
			          Predicate.COL_TYPE type = dtfac.getDatatype(functionName);
			          if (type == null) {
			            throw new RuntimeException("Unsupported datatype: " + functionName);
			          }
			          ((DataTypeStringContext)_localctx).value =  dfac.getTypedTerm(constant, type);
			      }
			  
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumericLiteralContext extends ParserRuleContext {
		public Term value;
		public NumericUnsignedContext numericUnsigned;
		public NumericPositiveContext numericPositive;
		public NumericNegativeContext numericNegative;
		public NumericUnsignedContext numericUnsigned() {
			return getRuleContext(NumericUnsignedContext.class,0);
		}
		public NumericPositiveContext numericPositive() {
			return getRuleContext(NumericPositiveContext.class,0);
		}
		public NumericNegativeContext numericNegative() {
			return getRuleContext(NumericNegativeContext.class,0);
		}
		public NumericLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numericLiteral; }
	}

	public final NumericLiteralContext numericLiteral() throws RecognitionException {
		NumericLiteralContext _localctx = new NumericLiteralContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_numericLiteral);
		try {
			setState(291);
			switch (_input.LA(1)) {
			case INTEGER:
			case DOUBLE:
			case DECIMAL:
				enterOuterAlt(_localctx, 1);
				{
				setState(282);
				((NumericLiteralContext)_localctx).numericUnsigned = numericUnsigned();
				 ((NumericLiteralContext)_localctx).value =  ((NumericLiteralContext)_localctx).numericUnsigned.value; 
				}
				break;
			case INTEGER_POSITIVE:
			case DOUBLE_POSITIVE:
			case DECIMAL_POSITIVE:
				enterOuterAlt(_localctx, 2);
				{
				setState(285);
				((NumericLiteralContext)_localctx).numericPositive = numericPositive();
				 ((NumericLiteralContext)_localctx).value =  ((NumericLiteralContext)_localctx).numericPositive.value; 
				}
				break;
			case INTEGER_NEGATIVE:
			case DOUBLE_NEGATIVE:
			case DECIMAL_NEGATIVE:
				enterOuterAlt(_localctx, 3);
				{
				setState(288);
				((NumericLiteralContext)_localctx).numericNegative = numericNegative();
				 ((NumericLiteralContext)_localctx).value =  ((NumericLiteralContext)_localctx).numericNegative.value; 
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NodeIDContext extends ParserRuleContext {
		public TerminalNode BLANK_PREFIX() { return getToken(TurtleOBDAParser.BLANK_PREFIX, 0); }
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public NodeIDContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nodeID; }
	}

	public final NodeIDContext nodeID() throws RecognitionException {
		NodeIDContext _localctx = new NodeIDContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_nodeID);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(293);
			match(BLANK_PREFIX);
			setState(294);
			name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RelativeURIContext extends ParserRuleContext {
		public TerminalNode STRING_URI() { return getToken(TurtleOBDAParser.STRING_URI, 0); }
		public RelativeURIContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relativeURI; }
	}

	public final RelativeURIContext relativeURI() throws RecognitionException {
		RelativeURIContext _localctx = new RelativeURIContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_relativeURI);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(296);
			match(STRING_URI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NamespaceContext extends ParserRuleContext {
		public TerminalNode NAMESPACE() { return getToken(TurtleOBDAParser.NAMESPACE, 0); }
		public NamespaceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_namespace; }
	}

	public final NamespaceContext namespace() throws RecognitionException {
		NamespaceContext _localctx = new NamespaceContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_namespace);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(298);
			match(NAMESPACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DefaultNamespaceContext extends ParserRuleContext {
		public TerminalNode COLON() { return getToken(TurtleOBDAParser.COLON, 0); }
		public DefaultNamespaceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defaultNamespace; }
	}

	public final DefaultNamespaceContext defaultNamespace() throws RecognitionException {
		DefaultNamespaceContext _localctx = new DefaultNamespaceContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_defaultNamespace);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(300);
			match(COLON);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NameContext extends ParserRuleContext {
		public TerminalNode VARNAME() { return getToken(TurtleOBDAParser.VARNAME, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(302);
			match(VARNAME);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LanguageTagContext extends ParserRuleContext {
		public TerminalNode VARNAME() { return getToken(TurtleOBDAParser.VARNAME, 0); }
		public LanguageTagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_languageTag; }
	}

	public final LanguageTagContext languageTag() throws RecognitionException {
		LanguageTagContext _localctx = new LanguageTagContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_languageTag);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(304);
			match(VARNAME);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BooleanLiteralContext extends ParserRuleContext {
		public Term value;
		public Token TRUE;
		public Token FALSE;
		public TerminalNode TRUE() { return getToken(TurtleOBDAParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(TurtleOBDAParser.FALSE, 0); }
		public BooleanLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_booleanLiteral; }
	}

	public final BooleanLiteralContext booleanLiteral() throws RecognitionException {
		BooleanLiteralContext _localctx = new BooleanLiteralContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_booleanLiteral);
		try {
			setState(310);
			switch (_input.LA(1)) {
			case TRUE:
				enterOuterAlt(_localctx, 1);
				{
				setState(306);
				((BooleanLiteralContext)_localctx).TRUE = match(TRUE);

				  ValueConstant trueConstant = dfac.getConstantLiteral((((BooleanLiteralContext)_localctx).TRUE!=null?((BooleanLiteralContext)_localctx).TRUE.getText():null), COL_TYPE.LITERAL);
				  ((BooleanLiteralContext)_localctx).value =  dfac.getTypedTerm(trueConstant, COL_TYPE.BOOLEAN); 
				}
				break;
			case FALSE:
				enterOuterAlt(_localctx, 2);
				{
				setState(308);
				((BooleanLiteralContext)_localctx).FALSE = match(FALSE);

				  ValueConstant falseConstant = dfac.getConstantLiteral((((BooleanLiteralContext)_localctx).FALSE!=null?((BooleanLiteralContext)_localctx).FALSE.getText():null), COL_TYPE.LITERAL);
				  ((BooleanLiteralContext)_localctx).value =  dfac.getTypedTerm(falseConstant, COL_TYPE.BOOLEAN);
				  
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumericUnsignedContext extends ParserRuleContext {
		public Term value;
		public Token INTEGER;
		public Token DOUBLE;
		public Token DECIMAL;
		public TerminalNode INTEGER() { return getToken(TurtleOBDAParser.INTEGER, 0); }
		public TerminalNode DOUBLE() { return getToken(TurtleOBDAParser.DOUBLE, 0); }
		public TerminalNode DECIMAL() { return getToken(TurtleOBDAParser.DECIMAL, 0); }
		public NumericUnsignedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numericUnsigned; }
	}

	public final NumericUnsignedContext numericUnsigned() throws RecognitionException {
		NumericUnsignedContext _localctx = new NumericUnsignedContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_numericUnsigned);
		try {
			setState(318);
			switch (_input.LA(1)) {
			case INTEGER:
				enterOuterAlt(_localctx, 1);
				{
				setState(312);
				((NumericUnsignedContext)_localctx).INTEGER = match(INTEGER);

				  ValueConstant integerConstant = dfac.getConstantLiteral((((NumericUnsignedContext)_localctx).INTEGER!=null?((NumericUnsignedContext)_localctx).INTEGER.getText():null), COL_TYPE.LITERAL);
				  ((NumericUnsignedContext)_localctx).value =  dfac.getTypedTerm(integerConstant, COL_TYPE.INTEGER);
				  
				}
				break;
			case DOUBLE:
				enterOuterAlt(_localctx, 2);
				{
				setState(314);
				((NumericUnsignedContext)_localctx).DOUBLE = match(DOUBLE);

				  ValueConstant doubleConstant = dfac.getConstantLiteral((((NumericUnsignedContext)_localctx).DOUBLE!=null?((NumericUnsignedContext)_localctx).DOUBLE.getText():null), COL_TYPE.LITERAL);
				  ((NumericUnsignedContext)_localctx).value =  dfac.getTypedTerm(doubleConstant, COL_TYPE.DOUBLE);
				  
				}
				break;
			case DECIMAL:
				enterOuterAlt(_localctx, 3);
				{
				setState(316);
				((NumericUnsignedContext)_localctx).DECIMAL = match(DECIMAL);

				  ValueConstant decimalConstant = dfac.getConstantLiteral((((NumericUnsignedContext)_localctx).DECIMAL!=null?((NumericUnsignedContext)_localctx).DECIMAL.getText():null), COL_TYPE.LITERAL);
				  ((NumericUnsignedContext)_localctx).value =  dfac.getTypedTerm(decimalConstant, COL_TYPE.DECIMAL);
				   
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumericPositiveContext extends ParserRuleContext {
		public Term value;
		public Token INTEGER_POSITIVE;
		public Token DOUBLE_POSITIVE;
		public Token DECIMAL_POSITIVE;
		public TerminalNode INTEGER_POSITIVE() { return getToken(TurtleOBDAParser.INTEGER_POSITIVE, 0); }
		public TerminalNode DOUBLE_POSITIVE() { return getToken(TurtleOBDAParser.DOUBLE_POSITIVE, 0); }
		public TerminalNode DECIMAL_POSITIVE() { return getToken(TurtleOBDAParser.DECIMAL_POSITIVE, 0); }
		public NumericPositiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numericPositive; }
	}

	public final NumericPositiveContext numericPositive() throws RecognitionException {
		NumericPositiveContext _localctx = new NumericPositiveContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_numericPositive);
		try {
			setState(326);
			switch (_input.LA(1)) {
			case INTEGER_POSITIVE:
				enterOuterAlt(_localctx, 1);
				{
				setState(320);
				((NumericPositiveContext)_localctx).INTEGER_POSITIVE = match(INTEGER_POSITIVE);

				   ValueConstant integerConstant = dfac.getConstantLiteral((((NumericPositiveContext)_localctx).INTEGER_POSITIVE!=null?((NumericPositiveContext)_localctx).INTEGER_POSITIVE.getText():null), COL_TYPE.LITERAL);
				   ((NumericPositiveContext)_localctx).value =  dfac.getTypedTerm(integerConstant, COL_TYPE.INTEGER);
				  
				}
				break;
			case DOUBLE_POSITIVE:
				enterOuterAlt(_localctx, 2);
				{
				setState(322);
				((NumericPositiveContext)_localctx).DOUBLE_POSITIVE = match(DOUBLE_POSITIVE);

				  ValueConstant doubleConstant = dfac.getConstantLiteral((((NumericPositiveContext)_localctx).DOUBLE_POSITIVE!=null?((NumericPositiveContext)_localctx).DOUBLE_POSITIVE.getText():null), COL_TYPE.LITERAL);
				  ((NumericPositiveContext)_localctx).value =  dfac.getTypedTerm(doubleConstant, COL_TYPE.DOUBLE);
				  
				}
				break;
			case DECIMAL_POSITIVE:
				enterOuterAlt(_localctx, 3);
				{
				setState(324);
				((NumericPositiveContext)_localctx).DECIMAL_POSITIVE = match(DECIMAL_POSITIVE);

				  ValueConstant decimalConstant = dfac.getConstantLiteral((((NumericPositiveContext)_localctx).DECIMAL_POSITIVE!=null?((NumericPositiveContext)_localctx).DECIMAL_POSITIVE.getText():null), COL_TYPE.LITERAL);
				  ((NumericPositiveContext)_localctx).value =  dfac.getTypedTerm(decimalConstant, COL_TYPE.DECIMAL);
				   
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumericNegativeContext extends ParserRuleContext {
		public Term value;
		public Token INTEGER_NEGATIVE;
		public Token DOUBLE_NEGATIVE;
		public Token DECIMAL_NEGATIVE;
		public TerminalNode INTEGER_NEGATIVE() { return getToken(TurtleOBDAParser.INTEGER_NEGATIVE, 0); }
		public TerminalNode DOUBLE_NEGATIVE() { return getToken(TurtleOBDAParser.DOUBLE_NEGATIVE, 0); }
		public TerminalNode DECIMAL_NEGATIVE() { return getToken(TurtleOBDAParser.DECIMAL_NEGATIVE, 0); }
		public NumericNegativeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numericNegative; }
	}

	public final NumericNegativeContext numericNegative() throws RecognitionException {
		NumericNegativeContext _localctx = new NumericNegativeContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_numericNegative);
		try {
			setState(334);
			switch (_input.LA(1)) {
			case INTEGER_NEGATIVE:
				enterOuterAlt(_localctx, 1);
				{
				setState(328);
				((NumericNegativeContext)_localctx).INTEGER_NEGATIVE = match(INTEGER_NEGATIVE);

				  ValueConstant integerConstant = dfac.getConstantLiteral((((NumericNegativeContext)_localctx).INTEGER_NEGATIVE!=null?((NumericNegativeContext)_localctx).INTEGER_NEGATIVE.getText():null), COL_TYPE.LITERAL);
				  ((NumericNegativeContext)_localctx).value =  dfac.getTypedTerm(integerConstant, COL_TYPE.INTEGER);
				  
				}
				break;
			case DOUBLE_NEGATIVE:
				enterOuterAlt(_localctx, 2);
				{
				setState(330);
				((NumericNegativeContext)_localctx).DOUBLE_NEGATIVE = match(DOUBLE_NEGATIVE);

				   ValueConstant doubleConstant = dfac.getConstantLiteral((((NumericNegativeContext)_localctx).DOUBLE_NEGATIVE!=null?((NumericNegativeContext)_localctx).DOUBLE_NEGATIVE.getText():null), COL_TYPE.LITERAL);
				   ((NumericNegativeContext)_localctx).value =  dfac.getTypedTerm(doubleConstant, COL_TYPE.DOUBLE);
				  
				}
				break;
			case DECIMAL_NEGATIVE:
				enterOuterAlt(_localctx, 3);
				{
				setState(332);
				((NumericNegativeContext)_localctx).DECIMAL_NEGATIVE = match(DECIMAL_NEGATIVE);

				  ValueConstant decimalConstant = dfac.getConstantLiteral((((NumericNegativeContext)_localctx).DECIMAL_NEGATIVE!=null?((NumericNegativeContext)_localctx).DECIMAL_NEGATIVE.getText():null), COL_TYPE.LITERAL);
				  ((NumericNegativeContext)_localctx).value =  dfac.getTypedTerm(decimalConstant, COL_TYPE.DECIMAL);
				  
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3@\u0153\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\3\2\7\2N\n\2\f\2\16\2Q\13\2\3\2\3"+
		"\2\3\2\3\2\3\2\7\2X\n\2\f\2\16\2[\13\2\3\2\3\2\3\3\3\3\3\3\3\4\3\4\7\4"+
		"d\n\4\f\4\16\4g\13\4\3\4\3\4\3\4\3\5\3\5\5\5n\n\5\3\6\3\6\3\6\3\6\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7|\n\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\7\t\u008e\n\t\f\t\16\t\u0091\13\t\3\n"+
		"\3\n\3\n\3\n\3\n\5\n\u0098\n\n\3\13\3\13\3\13\3\13\3\13\3\13\7\13\u00a0"+
		"\n\13\f\13\16\13\u00a3\13\13\3\f\3\f\3\f\3\f\3\f\3\f\5\f\u00ab\n\f\3\r"+
		"\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\5"+
		"\16\u00bc\n\16\3\17\3\17\3\17\3\17\3\17\3\17\5\17\u00c4\n\17\3\20\3\20"+
		"\3\20\3\21\3\21\3\21\3\22\3\22\5\22\u00ce\n\22\3\23\3\23\3\23\3\24\3\24"+
		"\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25"+
		"\5\25\u00e3\n\25\3\26\3\26\3\26\3\26\3\26\3\26\5\26\u00eb\n\26\3\27\3"+
		"\27\3\27\3\27\3\27\3\27\7\27\u00f3\n\27\f\27\16\27\u00f6\13\27\3\30\3"+
		"\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\5\30\u0101\n\30\3\31\3\31\3\31"+
		"\5\31\u0106\n\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31"+
		"\5\31\u0113\n\31\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3\34"+
		"\3\34\3\34\3\34\3\34\3\34\3\34\5\34\u0126\n\34\3\35\3\35\3\35\3\36\3\36"+
		"\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3#\3#\5#\u0139\n#\3$\3$\3$\3$\3$"+
		"\3$\5$\u0141\n$\3%\3%\3%\3%\3%\3%\5%\u0149\n%\3&\3&\3&\3&\3&\3&\5&\u0151"+
		"\n&\3&\2\2\'\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64"+
		"\668:<>@BDFHJ\2\2\u014d\2O\3\2\2\2\4^\3\2\2\2\6a\3\2\2\2\bm\3\2\2\2\n"+
		"o\3\2\2\2\fs\3\2\2\2\16\u0080\3\2\2\2\20\u0085\3\2\2\2\22\u0097\3\2\2"+
		"\2\24\u0099\3\2\2\2\26\u00aa\3\2\2\2\30\u00ac\3\2\2\2\32\u00bb\3\2\2\2"+
		"\34\u00c3\3\2\2\2\36\u00c5\3\2\2\2 \u00c8\3\2\2\2\"\u00cd\3\2\2\2$\u00cf"+
		"\3\2\2\2&\u00d2\3\2\2\2(\u00e2\3\2\2\2*\u00ea\3\2\2\2,\u00ec\3\2\2\2."+
		"\u0100\3\2\2\2\60\u0112\3\2\2\2\62\u0114\3\2\2\2\64\u0117\3\2\2\2\66\u0125"+
		"\3\2\2\28\u0127\3\2\2\2:\u012a\3\2\2\2<\u012c\3\2\2\2>\u012e\3\2\2\2@"+
		"\u0130\3\2\2\2B\u0132\3\2\2\2D\u0138\3\2\2\2F\u0140\3\2\2\2H\u0148\3\2"+
		"\2\2J\u0150\3\2\2\2LN\5\4\3\2ML\3\2\2\2NQ\3\2\2\2OM\3\2\2\2OP\3\2\2\2"+
		"PR\3\2\2\2QO\3\2\2\2RS\5\6\4\2SY\b\2\1\2TU\5\6\4\2UV\b\2\1\2VX\3\2\2\2"+
		"WT\3\2\2\2X[\3\2\2\2YW\3\2\2\2YZ\3\2\2\2Z\\\3\2\2\2[Y\3\2\2\2\\]\7\2\2"+
		"\3]\3\3\2\2\2^_\5\b\5\2_`\7\f\2\2`\5\3\2\2\2ae\5\16\b\2bd\7@\2\2cb\3\2"+
		"\2\2dg\3\2\2\2ec\3\2\2\2ef\3\2\2\2fh\3\2\2\2ge\3\2\2\2hi\7\f\2\2ij\b\4"+
		"\1\2j\7\3\2\2\2kn\5\n\6\2ln\5\f\7\2mk\3\2\2\2ml\3\2\2\2n\t\3\2\2\2op\7"+
		"\35\2\2pq\7\4\2\2qr\5\36\20\2r\13\3\2\2\2st\7\35\2\2t{\7\5\2\2uv\5<\37"+
		"\2vw\b\7\1\2w|\3\2\2\2xy\5> \2yz\b\7\1\2z|\3\2\2\2{u\3\2\2\2{x\3\2\2\2"+
		"|}\3\2\2\2}~\5\36\20\2~\177\b\7\1\2\177\r\3\2\2\2\u0080\u0081\5\26\f\2"+
		"\u0081\u0082\b\b\1\2\u0082\u0083\5\20\t\2\u0083\u0084\b\b\1\2\u0084\17"+
		"\3\2\2\2\u0085\u0086\5\22\n\2\u0086\u0087\5\24\13\2\u0087\u008f\b\t\1"+
		"\2\u0088\u0089\7\13\2\2\u0089\u008a\5\22\n\2\u008a\u008b\5\24\13\2\u008b"+
		"\u008c\b\t\1\2\u008c\u008e\3\2\2\2\u008d\u0088\3\2\2\2\u008e\u0091\3\2"+
		"\2\2\u008f\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090\21\3\2\2\2\u0091\u008f"+
		"\3\2\2\2\u0092\u0093\5\30\r\2\u0093\u0094\b\n\1\2\u0094\u0098\3\2\2\2"+
		"\u0095\u0096\7\3\2\2\u0096\u0098\b\n\1\2\u0097\u0092\3\2\2\2\u0097\u0095"+
		"\3\2\2\2\u0098\23\3\2\2\2\u0099\u009a\5\32\16\2\u009a\u00a1\b\13\1\2\u009b"+
		"\u009c\7\r\2\2\u009c\u009d\5\32\16\2\u009d\u009e\b\13\1\2\u009e\u00a0"+
		"\3\2\2\2\u009f\u009b\3\2\2\2\u00a0\u00a3\3\2\2\2\u00a1\u009f\3\2\2\2\u00a1"+
		"\u00a2\3\2\2\2\u00a2\25\3\2\2\2\u00a3\u00a1\3\2\2\2\u00a4\u00a5\5\34\17"+
		"\2\u00a5\u00a6\b\f\1\2\u00a6\u00ab\3\2\2\2\u00a7\u00a8\5$\23\2\u00a8\u00a9"+
		"\b\f\1\2\u00a9\u00ab\3\2\2\2\u00aa\u00a4\3\2\2\2\u00aa\u00a7\3\2\2\2\u00ab"+
		"\27\3\2\2\2\u00ac\u00ad\5\34\17\2\u00ad\u00ae\b\r\1\2\u00ae\31\3\2\2\2"+
		"\u00af\u00b0\5\34\17\2\u00b0\u00b1\b\16\1\2\u00b1\u00bc\3\2\2\2\u00b2"+
		"\u00b3\5\60\31\2\u00b3\u00b4\b\16\1\2\u00b4\u00bc\3\2\2\2\u00b5\u00b6"+
		"\5(\25\2\u00b6\u00b7\b\16\1\2\u00b7\u00bc\3\2\2\2\u00b8\u00b9\5$\23\2"+
		"\u00b9\u00ba\b\16\1\2\u00ba\u00bc\3\2\2\2\u00bb\u00af\3\2\2\2\u00bb\u00b2"+
		"\3\2\2\2\u00bb\u00b5\3\2\2\2\u00bb\u00b8\3\2\2\2\u00bc\33\3\2\2\2\u00bd"+
		"\u00be\5\36\20\2\u00be\u00bf\b\17\1\2\u00bf\u00c4\3\2\2\2\u00c0\u00c1"+
		"\5 \21\2\u00c1\u00c2\b\17\1\2\u00c2\u00c4\3\2\2\2\u00c3\u00bd\3\2\2\2"+
		"\u00c3\u00c0\3\2\2\2\u00c4\35\3\2\2\2\u00c5\u00c6\7=\2\2\u00c6\u00c7\b"+
		"\20\1\2\u00c7\37\3\2\2\2\u00c8\u00c9\7:\2\2\u00c9\u00ca\b\21\1\2\u00ca"+
		"!\3\2\2\2\u00cb\u00ce\58\35\2\u00cc\u00ce\7)\2\2\u00cd\u00cb\3\2\2\2\u00cd"+
		"\u00cc\3\2\2\2\u00ce#\3\2\2\2\u00cf\u00d0\7>\2\2\u00d0\u00d1\b\23\1\2"+
		"\u00d1%\3\2\2\2\u00d2\u00d3\5\34\17\2\u00d3\u00d4\7\22\2\2\u00d4\u00d5"+
		"\5,\27\2\u00d5\u00d6\7\23\2\2\u00d6\u00d7\b\24\1\2\u00d7\'\3\2\2\2\u00d8"+
		"\u00d9\5$\23\2\u00d9\u00da\7\35\2\2\u00da\u00db\5*\26\2\u00db\u00dc\b"+
		"\25\1\2\u00dc\u00e3\3\2\2\2\u00dd\u00de\5$\23\2\u00de\u00df\7\b\2\2\u00df"+
		"\u00e0\5\34\17\2\u00e0\u00e1\b\25\1\2\u00e1\u00e3\3\2\2\2\u00e2\u00d8"+
		"\3\2\2\2\u00e2\u00dd\3\2\2\2\u00e3)\3\2\2\2\u00e4\u00e5\5B\"\2\u00e5\u00e6"+
		"\b\26\1\2\u00e6\u00eb\3\2\2\2\u00e7\u00e8\5$\23\2\u00e8\u00e9\b\26\1\2"+
		"\u00e9\u00eb\3\2\2\2\u00ea\u00e4\3\2\2\2\u00ea\u00e7\3\2\2\2\u00eb+\3"+
		"\2\2\2\u00ec\u00ed\5.\30\2\u00ed\u00f4\b\27\1\2\u00ee\u00ef\7\r\2\2\u00ef"+
		"\u00f0\5.\30\2\u00f0\u00f1\b\27\1\2\u00f1\u00f3\3\2\2\2\u00f2\u00ee\3"+
		"\2\2\2\u00f3\u00f6\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f4\u00f5\3\2\2\2\u00f5"+
		"-\3\2\2\2\u00f6\u00f4\3\2\2\2\u00f7\u00f8\5&\24\2\u00f8\u00f9\b\30\1\2"+
		"\u00f9\u0101\3\2\2\2\u00fa\u00fb\5$\23\2\u00fb\u00fc\b\30\1\2\u00fc\u0101"+
		"\3\2\2\2\u00fd\u00fe\5\60\31\2\u00fe\u00ff\b\30\1\2\u00ff\u0101\3\2\2"+
		"\2\u0100\u00f7\3\2\2\2\u0100\u00fa\3\2\2\2\u0100\u00fd\3\2\2\2\u0101/"+
		"\3\2\2\2\u0102\u0105\5\62\32\2\u0103\u0104\7\35\2\2\u0104\u0106\5*\26"+
		"\2\u0105\u0103\3\2\2\2\u0105\u0106\3\2\2\2\u0106\u0107\3\2\2\2\u0107\u0108"+
		"\b\31\1\2\u0108\u0113\3\2\2\2\u0109\u010a\5\64\33\2\u010a\u010b\b\31\1"+
		"\2\u010b\u0113\3\2\2\2\u010c\u010d\5\66\34\2\u010d\u010e\b\31\1\2\u010e"+
		"\u0113\3\2\2\2\u010f\u0110\5D#\2\u0110\u0111\b\31\1\2\u0111\u0113\3\2"+
		"\2\2\u0112\u0102\3\2\2\2\u0112\u0109\3\2\2\2\u0112\u010c\3\2\2\2\u0112"+
		"\u010f\3\2\2\2\u0113\61\3\2\2\2\u0114\u0115\7<\2\2\u0115\u0116\b\32\1"+
		"\2\u0116\63\3\2\2\2\u0117\u0118\5\62\32\2\u0118\u0119\7\b\2\2\u0119\u011a"+
		"\5\34\17\2\u011a\u011b\b\33\1\2\u011b\65\3\2\2\2\u011c\u011d\5F$\2\u011d"+
		"\u011e\b\34\1\2\u011e\u0126\3\2\2\2\u011f\u0120\5H%\2\u0120\u0121\b\34"+
		"\1\2\u0121\u0126\3\2\2\2\u0122\u0123\5J&\2\u0123\u0124\b\34\1\2\u0124"+
		"\u0126\3\2\2\2\u0125\u011c\3\2\2\2\u0125\u011f\3\2\2\2\u0125\u0122\3\2"+
		"\2\2\u0126\67\3\2\2\2\u0127\u0128\7*\2\2\u0128\u0129\5@!\2\u01299\3\2"+
		"\2\2\u012a\u012b\7?\2\2\u012b;\3\2\2\2\u012c\u012d\79\2\2\u012d=\3\2\2"+
		"\2\u012e\u012f\7#\2\2\u012f?\3\2\2\2\u0130\u0131\7\66\2\2\u0131A\3\2\2"+
		"\2\u0132\u0133\7\66\2\2\u0133C\3\2\2\2\u0134\u0135\7\7\2\2\u0135\u0139"+
		"\b#\1\2\u0136\u0137\7\6\2\2\u0137\u0139\b#\1\2\u0138\u0134\3\2\2\2\u0138"+
		"\u0136\3\2\2\2\u0139E\3\2\2\2\u013a\u013b\7-\2\2\u013b\u0141\b$\1\2\u013c"+
		"\u013d\7.\2\2\u013d\u0141\b$\1\2\u013e\u013f\7/\2\2\u013f\u0141\b$\1\2"+
		"\u0140\u013a\3\2\2\2\u0140\u013c\3\2\2\2\u0140\u013e\3\2\2\2\u0141G\3"+
		"\2\2\2\u0142\u0143\7\60\2\2\u0143\u0149\b%\1\2\u0144\u0145\7\62\2\2\u0145"+
		"\u0149\b%\1\2\u0146\u0147\7\64\2\2\u0147\u0149\b%\1\2\u0148\u0142\3\2"+
		"\2\2\u0148\u0144\3\2\2\2\u0148\u0146\3\2\2\2\u0149I\3\2\2\2\u014a\u014b"+
		"\7\61\2\2\u014b\u0151\b&\1\2\u014c\u014d\7\63\2\2\u014d\u0151\b&\1\2\u014e"+
		"\u014f\7\65\2\2\u014f\u0151\b&\1\2\u0150\u014a\3\2\2\2\u0150\u014c\3\2"+
		"\2\2\u0150\u014e\3\2\2\2\u0151K\3\2\2\2\31OYem{\u008f\u0097\u00a1\u00aa"+
		"\u00bb\u00c3\u00cd\u00e2\u00ea\u00f4\u0100\u0105\u0112\u0125\u0138\u0140"+
		"\u0148\u0150";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}