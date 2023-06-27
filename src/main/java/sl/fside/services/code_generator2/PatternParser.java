// Generated from pl\edu\agh\kis\Pattern.g4 by ANTLR 4.12.0
package sl.fside.services.code_generator2;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class PatternParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.12.0", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		PATTERN_NAME_LEX=1, DELIMITER=2, LEFT_BRACKET=3, RIGHT_BRACKET=4, COLON=5, 
		BOOLEAN=6, EMPTY=7, ATOM=8, INTEGER=9, FLOATING=10, STRING=11, WHITESPACE=12;
	public static final int
		RULE_pattern = 0, RULE_patternName = 1, RULE_method = 2, RULE_parameter = 3, 
		RULE_typedExpr = 4;
	private static String[] makeRuleNames() {
		return new String[] {
			"pattern", "patternName", "method", "parameter", "typedExpr"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, "','", "'('", "')'", "':'", null, "'empty'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "PATTERN_NAME_LEX", "DELIMITER", "LEFT_BRACKET", "RIGHT_BRACKET", 
			"COLON", "BOOLEAN", "EMPTY", "ATOM", "INTEGER", "FLOATING", "STRING", 
			"WHITESPACE"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
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
	public String getGrammarFileName() { return "Pattern.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public PatternParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PatternContext extends ParserRuleContext {
		public PatternContext pattern;
		public List<PatternContext> args = new ArrayList<PatternContext>();
		public PatternNameContext patternName() {
			return getRuleContext(PatternNameContext.class,0);
		}
		public TerminalNode LEFT_BRACKET() { return getToken(PatternParser.LEFT_BRACKET, 0); }
		public TerminalNode RIGHT_BRACKET() { return getToken(PatternParser.RIGHT_BRACKET, 0); }
		public List<PatternContext> pattern() {
			return getRuleContexts(PatternContext.class);
		}
		public PatternContext pattern(int i) {
			return getRuleContext(PatternContext.class,i);
		}
		public List<TerminalNode> DELIMITER() { return getTokens(PatternParser.DELIMITER); }
		public TerminalNode DELIMITER(int i) {
			return getToken(PatternParser.DELIMITER, i);
		}
		public MethodContext method() {
			return getRuleContext(MethodContext.class,0);
		}
		public TerminalNode STRING() { return getToken(PatternParser.STRING, 0); }
		public TerminalNode EMPTY() { return getToken(PatternParser.EMPTY, 0); }
		public PatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pattern; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PatternVisitor ) return ((PatternVisitor<? extends T>)visitor).visitPattern(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PatternContext pattern() throws RecognitionException {
		PatternContext _localctx = new PatternContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_pattern);
		int _la;
		try {
			setState(27);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PATTERN_NAME_LEX:
				enterOuterAlt(_localctx, 1);
				{
				setState(10);
				patternName();
				setState(11);
				match(LEFT_BRACKET);
				setState(20);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2434L) != 0)) {
					{
					setState(12);
					((PatternContext)_localctx).pattern = pattern();
					((PatternContext)_localctx).args.add(((PatternContext)_localctx).pattern);
					setState(17);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==DELIMITER) {
						{
						{
						setState(13);
						match(DELIMITER);
						setState(14);
						((PatternContext)_localctx).pattern = pattern();
						((PatternContext)_localctx).args.add(((PatternContext)_localctx).pattern);
						}
						}
						setState(19);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(22);
				match(RIGHT_BRACKET);
				}
				break;
			case ATOM:
				enterOuterAlt(_localctx, 2);
				{
				setState(24);
				method();
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 3);
				{
				setState(25);
				match(STRING);
				}
				break;
			case EMPTY:
				enterOuterAlt(_localctx, 4);
				{
				setState(26);
				match(EMPTY);
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

	@SuppressWarnings("CheckReturnValue")
	public static class PatternNameContext extends ParserRuleContext {
		public TerminalNode PATTERN_NAME_LEX() { return getToken(PatternParser.PATTERN_NAME_LEX, 0); }
		public PatternNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_patternName; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PatternVisitor ) return ((PatternVisitor<? extends T>)visitor).visitPatternName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PatternNameContext patternName() throws RecognitionException {
		PatternNameContext _localctx = new PatternNameContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_patternName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(29);
			match(PATTERN_NAME_LEX);
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

	@SuppressWarnings("CheckReturnValue")
	public static class MethodContext extends ParserRuleContext {
		public ParameterContext parameter;
		public List<ParameterContext> args = new ArrayList<ParameterContext>();
		public Token returnType;
		public TerminalNode ATOM() { return getToken(PatternParser.ATOM, 0); }
		public TerminalNode LEFT_BRACKET() { return getToken(PatternParser.LEFT_BRACKET, 0); }
		public TerminalNode RIGHT_BRACKET() { return getToken(PatternParser.RIGHT_BRACKET, 0); }
		public TerminalNode COLON() { return getToken(PatternParser.COLON, 0); }
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}
		public TerminalNode STRING() { return getToken(PatternParser.STRING, 0); }
		public List<TerminalNode> DELIMITER() { return getTokens(PatternParser.DELIMITER); }
		public TerminalNode DELIMITER(int i) {
			return getToken(PatternParser.DELIMITER, i);
		}
		public MethodContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_method; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PatternVisitor ) return ((PatternVisitor<? extends T>)visitor).visitMethod(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MethodContext method() throws RecognitionException {
		MethodContext _localctx = new MethodContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_method);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(31);
			match(ATOM);
			setState(32);
			match(LEFT_BRACKET);
			setState(41);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 3904L) != 0)) {
				{
				setState(33);
				((MethodContext)_localctx).parameter = parameter();
				((MethodContext)_localctx).args.add(((MethodContext)_localctx).parameter);
				setState(38);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==DELIMITER) {
					{
					{
					setState(34);
					match(DELIMITER);
					setState(35);
					((MethodContext)_localctx).parameter = parameter();
					((MethodContext)_localctx).args.add(((MethodContext)_localctx).parameter);
					}
					}
					setState(40);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(43);
			match(RIGHT_BRACKET);
			setState(46);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(44);
				match(COLON);
				setState(45);
				((MethodContext)_localctx).returnType = match(STRING);
				}
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

	@SuppressWarnings("CheckReturnValue")
	public static class ParameterContext extends ParserRuleContext {
		public TerminalNode INTEGER() { return getToken(PatternParser.INTEGER, 0); }
		public TerminalNode FLOATING() { return getToken(PatternParser.FLOATING, 0); }
		public TerminalNode BOOLEAN() { return getToken(PatternParser.BOOLEAN, 0); }
		public TerminalNode STRING() { return getToken(PatternParser.STRING, 0); }
		public MethodContext method() {
			return getRuleContext(MethodContext.class,0);
		}
		public TypedExprContext typedExpr() {
			return getRuleContext(TypedExprContext.class,0);
		}
		public ParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PatternVisitor ) return ((PatternVisitor<? extends T>)visitor).visitParameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterContext parameter() throws RecognitionException {
		ParameterContext _localctx = new ParameterContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_parameter);
		try {
			setState(54);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(48);
				match(INTEGER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(49);
				match(FLOATING);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(50);
				match(BOOLEAN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(51);
				match(STRING);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(52);
				method();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(53);
				typedExpr();
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

	@SuppressWarnings("CheckReturnValue")
	public static class TypedExprContext extends ParserRuleContext {
		public Token expr;
		public Token type;
		public TerminalNode COLON() { return getToken(PatternParser.COLON, 0); }
		public List<TerminalNode> STRING() { return getTokens(PatternParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(PatternParser.STRING, i);
		}
		public TypedExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typedExpr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PatternVisitor ) return ((PatternVisitor<? extends T>)visitor).visitTypedExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypedExprContext typedExpr() throws RecognitionException {
		TypedExprContext _localctx = new TypedExprContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_typedExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			((TypedExprContext)_localctx).expr = match(STRING);
			setState(57);
			match(COLON);
			setState(58);
			((TypedExprContext)_localctx).type = match(STRING);
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
		"\u0004\u0001\f=\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0005\u0000\u0010"+
		"\b\u0000\n\u0000\f\u0000\u0013\t\u0000\u0003\u0000\u0015\b\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0003\u0000\u001c"+
		"\b\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0005\u0002%\b\u0002\n\u0002\f\u0002(\t\u0002\u0003"+
		"\u0002*\b\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002/\b\u0002"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0003\u00037\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0000\u0000\u0005\u0000\u0002\u0004\u0006\b\u0000\u0000D"+
		"\u0000\u001b\u0001\u0000\u0000\u0000\u0002\u001d\u0001\u0000\u0000\u0000"+
		"\u0004\u001f\u0001\u0000\u0000\u0000\u00066\u0001\u0000\u0000\u0000\b"+
		"8\u0001\u0000\u0000\u0000\n\u000b\u0003\u0002\u0001\u0000\u000b\u0014"+
		"\u0005\u0003\u0000\u0000\f\u0011\u0003\u0000\u0000\u0000\r\u000e\u0005"+
		"\u0002\u0000\u0000\u000e\u0010\u0003\u0000\u0000\u0000\u000f\r\u0001\u0000"+
		"\u0000\u0000\u0010\u0013\u0001\u0000\u0000\u0000\u0011\u000f\u0001\u0000"+
		"\u0000\u0000\u0011\u0012\u0001\u0000\u0000\u0000\u0012\u0015\u0001\u0000"+
		"\u0000\u0000\u0013\u0011\u0001\u0000\u0000\u0000\u0014\f\u0001\u0000\u0000"+
		"\u0000\u0014\u0015\u0001\u0000\u0000\u0000\u0015\u0016\u0001\u0000\u0000"+
		"\u0000\u0016\u0017\u0005\u0004\u0000\u0000\u0017\u001c\u0001\u0000\u0000"+
		"\u0000\u0018\u001c\u0003\u0004\u0002\u0000\u0019\u001c\u0005\u000b\u0000"+
		"\u0000\u001a\u001c\u0005\u0007\u0000\u0000\u001b\n\u0001\u0000\u0000\u0000"+
		"\u001b\u0018\u0001\u0000\u0000\u0000\u001b\u0019\u0001\u0000\u0000\u0000"+
		"\u001b\u001a\u0001\u0000\u0000\u0000\u001c\u0001\u0001\u0000\u0000\u0000"+
		"\u001d\u001e\u0005\u0001\u0000\u0000\u001e\u0003\u0001\u0000\u0000\u0000"+
		"\u001f \u0005\b\u0000\u0000 )\u0005\u0003\u0000\u0000!&\u0003\u0006\u0003"+
		"\u0000\"#\u0005\u0002\u0000\u0000#%\u0003\u0006\u0003\u0000$\"\u0001\u0000"+
		"\u0000\u0000%(\u0001\u0000\u0000\u0000&$\u0001\u0000\u0000\u0000&\'\u0001"+
		"\u0000\u0000\u0000\'*\u0001\u0000\u0000\u0000(&\u0001\u0000\u0000\u0000"+
		")!\u0001\u0000\u0000\u0000)*\u0001\u0000\u0000\u0000*+\u0001\u0000\u0000"+
		"\u0000+.\u0005\u0004\u0000\u0000,-\u0005\u0005\u0000\u0000-/\u0005\u000b"+
		"\u0000\u0000.,\u0001\u0000\u0000\u0000./\u0001\u0000\u0000\u0000/\u0005"+
		"\u0001\u0000\u0000\u000007\u0005\t\u0000\u000017\u0005\n\u0000\u00002"+
		"7\u0005\u0006\u0000\u000037\u0005\u000b\u0000\u000047\u0003\u0004\u0002"+
		"\u000057\u0003\b\u0004\u000060\u0001\u0000\u0000\u000061\u0001\u0000\u0000"+
		"\u000062\u0001\u0000\u0000\u000063\u0001\u0000\u0000\u000064\u0001\u0000"+
		"\u0000\u000065\u0001\u0000\u0000\u00007\u0007\u0001\u0000\u0000\u0000"+
		"89\u0005\u000b\u0000\u00009:\u0005\u0005\u0000\u0000:;\u0005\u000b\u0000"+
		"\u0000;\t\u0001\u0000\u0000\u0000\u0007\u0011\u0014\u001b&).6";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}