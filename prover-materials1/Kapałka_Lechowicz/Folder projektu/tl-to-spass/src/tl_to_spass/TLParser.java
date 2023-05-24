package tl_to_spass;

// Generated from TL.g4 by ANTLR 4.7
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, VAR_ID=2, PRED_ID=3, IND_ID=4, FUNC_ID=5, WORD=6, LPAREN=7, RPAREN=8, 
		EQUAL=9, NOT=10, ALWAYS=11, EVENTUALLY=12, CONJ=13, DISJ=14, IMPL=15, 
		BICOND=16, ENDLINE=17, WHITESPACE=18;
	public static final int
		RULE_condition = 0, RULE_formula = 1, RULE_term = 2, RULE_bin_connective = 3, 
		RULE_variable = 4, RULE_pred_constant = 5, RULE_ind_constant = 6, RULE_func_constant = 7, 
		RULE_separator = 8;
	public static final String[] ruleNames = {
		"condition", "formula", "term", "bin_connective", "variable", "pred_constant", 
		"ind_constant", "func_constant", "separator"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "','", "'?'", "'_'", "'#'", "'.'", null, "'('", "')'", "'='", "'\u00AC'", 
		"'\u25A1'", "'\u25C7'", "'\u2227'", "'\u2228'", "'\u2192'", "'\u2194'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, "VAR_ID", "PRED_ID", "IND_ID", "FUNC_ID", "WORD", "LPAREN", 
		"RPAREN", "EQUAL", "NOT", "ALWAYS", "EVENTUALLY", "CONJ", "DISJ", "IMPL", 
		"BICOND", "ENDLINE", "WHITESPACE"
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
	public String getGrammarFileName() { return "TL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ConditionContext extends ParserRuleContext {
		public List<FormulaContext> formula() {
			return getRuleContexts(FormulaContext.class);
		}
		public FormulaContext formula(int i) {
			return getRuleContext(FormulaContext.class,i);
		}
		public TerminalNode EOF() { return getToken(TLParser.EOF, 0); }
		public List<TerminalNode> ENDLINE() { return getTokens(TLParser.ENDLINE); }
		public TerminalNode ENDLINE(int i) {
			return getToken(TLParser.ENDLINE, i);
		}
		public ConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLListener ) ((TLListener)listener).enterCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLListener ) ((TLListener)listener).exitCondition(this);
		}
	}

	public final ConditionContext condition() throws RecognitionException {
		ConditionContext _localctx = new ConditionContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_condition);
		int _la;
		try {
			int _alt;
			setState(35);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VAR_ID:
			case PRED_ID:
			case IND_ID:
			case FUNC_ID:
			case LPAREN:
			case NOT:
			case ALWAYS:
			case EVENTUALLY:
				enterOuterAlt(_localctx, 1);
				{
				setState(18);
				formula(0);
				setState(23);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(19);
						match(ENDLINE);
						setState(20);
						formula(0);
						}
						} 
					}
					setState(25);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
				}
				setState(29);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==ENDLINE) {
					{
					{
					setState(26);
					match(ENDLINE);
					}
					}
					setState(31);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(32);
				match(EOF);
				}
				break;
			case EOF:
				enterOuterAlt(_localctx, 2);
				{
				setState(34);
				match(EOF);
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

	public static class FormulaContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(TLParser.LPAREN, 0); }
		public List<FormulaContext> formula() {
			return getRuleContexts(FormulaContext.class);
		}
		public FormulaContext formula(int i) {
			return getRuleContext(FormulaContext.class,i);
		}
		public Bin_connectiveContext bin_connective() {
			return getRuleContext(Bin_connectiveContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(TLParser.RPAREN, 0); }
		public TerminalNode NOT() { return getToken(TLParser.NOT, 0); }
		public TerminalNode ALWAYS() { return getToken(TLParser.ALWAYS, 0); }
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public List<SeparatorContext> separator() {
			return getRuleContexts(SeparatorContext.class);
		}
		public SeparatorContext separator(int i) {
			return getRuleContext(SeparatorContext.class,i);
		}
		public TerminalNode EVENTUALLY() { return getToken(TLParser.EVENTUALLY, 0); }
		public Pred_constantContext pred_constant() {
			return getRuleContext(Pred_constantContext.class,0);
		}
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public TerminalNode EQUAL() { return getToken(TLParser.EQUAL, 0); }
		public FormulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLListener ) ((TLListener)listener).enterFormula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLListener ) ((TLListener)listener).exitFormula(this);
		}
	}

	public final FormulaContext formula() throws RecognitionException {
		return formula(0);
	}

	private FormulaContext formula(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		FormulaContext _localctx = new FormulaContext(_ctx, _parentState);
		FormulaContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_formula, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(77);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LPAREN:
				{
				setState(38);
				match(LPAREN);
				setState(39);
				formula(0);
				setState(40);
				bin_connective();
				setState(41);
				formula(0);
				setState(42);
				match(RPAREN);
				}
				break;
			case NOT:
				{
				setState(44);
				match(NOT);
				setState(45);
				formula(6);
				}
				break;
			case ALWAYS:
				{
				setState(46);
				match(ALWAYS);
				setState(47);
				match(LPAREN);
				setState(48);
				variable();
				setState(49);
				separator();
				setState(50);
				formula(0);
				setState(51);
				match(RPAREN);
				}
				break;
			case EVENTUALLY:
				{
				setState(53);
				match(EVENTUALLY);
				setState(54);
				match(LPAREN);
				setState(55);
				variable();
				setState(56);
				separator();
				setState(57);
				formula(0);
				setState(58);
				match(RPAREN);
				}
				break;
			case PRED_ID:
				{
				setState(60);
				pred_constant();
				setState(61);
				match(LPAREN);
				setState(62);
				term();
				setState(68);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(63);
					separator();
					setState(64);
					term();
					}
					}
					setState(70);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(71);
				match(RPAREN);
				}
				break;
			case VAR_ID:
			case IND_ID:
			case FUNC_ID:
				{
				setState(73);
				term();
				setState(74);
				match(EQUAL);
				setState(75);
				term();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(83);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new FormulaContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_formula);
					setState(79);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(80);
					match(VAR_ID);
					}
					} 
				}
				setState(85);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class TermContext extends ParserRuleContext {
		public Ind_constantContext ind_constant() {
			return getRuleContext(Ind_constantContext.class,0);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public Func_constantContext func_constant() {
			return getRuleContext(Func_constantContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(TLParser.LPAREN, 0); }
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(TLParser.RPAREN, 0); }
		public List<SeparatorContext> separator() {
			return getRuleContexts(SeparatorContext.class);
		}
		public SeparatorContext separator(int i) {
			return getRuleContext(SeparatorContext.class,i);
		}
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLListener ) ((TLListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLListener ) ((TLListener)listener).exitTerm(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_term);
		int _la;
		try {
			setState(101);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IND_ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(86);
				ind_constant();
				}
				break;
			case VAR_ID:
				enterOuterAlt(_localctx, 2);
				{
				setState(87);
				variable();
				}
				break;
			case FUNC_ID:
				enterOuterAlt(_localctx, 3);
				{
				setState(88);
				func_constant();
				setState(89);
				match(LPAREN);
				setState(90);
				term();
				setState(96);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(91);
					separator();
					setState(92);
					term();
					}
					}
					setState(98);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(99);
				match(RPAREN);
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

	public static class Bin_connectiveContext extends ParserRuleContext {
		public TerminalNode CONJ() { return getToken(TLParser.CONJ, 0); }
		public TerminalNode DISJ() { return getToken(TLParser.DISJ, 0); }
		public TerminalNode IMPL() { return getToken(TLParser.IMPL, 0); }
		public TerminalNode BICOND() { return getToken(TLParser.BICOND, 0); }
		public Bin_connectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bin_connective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLListener ) ((TLListener)listener).enterBin_connective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLListener ) ((TLListener)listener).exitBin_connective(this);
		}
	}

	public final Bin_connectiveContext bin_connective() throws RecognitionException {
		Bin_connectiveContext _localctx = new Bin_connectiveContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_bin_connective);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(103);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CONJ) | (1L << DISJ) | (1L << IMPL) | (1L << BICOND))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	public static class VariableContext extends ParserRuleContext {
		public TerminalNode VAR_ID() { return getToken(TLParser.VAR_ID, 0); }
		public TerminalNode WORD() { return getToken(TLParser.WORD, 0); }
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLListener ) ((TLListener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLListener ) ((TLListener)listener).exitVariable(this);
		}
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(105);
			match(VAR_ID);
			setState(106);
			match(WORD);
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

	public static class Pred_constantContext extends ParserRuleContext {
		public TerminalNode PRED_ID() { return getToken(TLParser.PRED_ID, 0); }
		public TerminalNode WORD() { return getToken(TLParser.WORD, 0); }
		public Pred_constantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pred_constant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLListener ) ((TLListener)listener).enterPred_constant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLListener ) ((TLListener)listener).exitPred_constant(this);
		}
	}

	public final Pred_constantContext pred_constant() throws RecognitionException {
		Pred_constantContext _localctx = new Pred_constantContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_pred_constant);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108);
			match(PRED_ID);
			setState(109);
			match(WORD);
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

	public static class Ind_constantContext extends ParserRuleContext {
		public TerminalNode IND_ID() { return getToken(TLParser.IND_ID, 0); }
		public TerminalNode WORD() { return getToken(TLParser.WORD, 0); }
		public Ind_constantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ind_constant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLListener ) ((TLListener)listener).enterInd_constant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLListener ) ((TLListener)listener).exitInd_constant(this);
		}
	}

	public final Ind_constantContext ind_constant() throws RecognitionException {
		Ind_constantContext _localctx = new Ind_constantContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_ind_constant);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(111);
			match(IND_ID);
			setState(112);
			match(WORD);
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

	public static class Func_constantContext extends ParserRuleContext {
		public TerminalNode FUNC_ID() { return getToken(TLParser.FUNC_ID, 0); }
		public TerminalNode WORD() { return getToken(TLParser.WORD, 0); }
		public Func_constantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_func_constant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLListener ) ((TLListener)listener).enterFunc_constant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLListener ) ((TLListener)listener).exitFunc_constant(this);
		}
	}

	public final Func_constantContext func_constant() throws RecognitionException {
		Func_constantContext _localctx = new Func_constantContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_func_constant);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(114);
			match(FUNC_ID);
			setState(115);
			match(WORD);
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

	public static class SeparatorContext extends ParserRuleContext {
		public SeparatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_separator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLListener ) ((TLListener)listener).enterSeparator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLListener ) ((TLListener)listener).exitSeparator(this);
		}
	}

	public final SeparatorContext separator() throws RecognitionException {
		SeparatorContext _localctx = new SeparatorContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_separator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(117);
			match(T__0);
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1:
			return formula_sempred((FormulaContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean formula_sempred(FormulaContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\24z\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3\2\3\2"+
		"\7\2\30\n\2\f\2\16\2\33\13\2\3\2\7\2\36\n\2\f\2\16\2!\13\2\3\2\3\2\3\2"+
		"\5\2&\n\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3E\n\3\f\3"+
		"\16\3H\13\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3P\n\3\3\3\3\3\7\3T\n\3\f\3\16\3"+
		"W\13\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\7\4a\n\4\f\4\16\4d\13\4\3\4\3\4"+
		"\5\4h\n\4\3\5\3\5\3\6\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3\t\3\n"+
		"\3\n\3\n\2\3\4\13\2\4\6\b\n\f\16\20\22\2\3\3\2\17\22\2}\2%\3\2\2\2\4O"+
		"\3\2\2\2\6g\3\2\2\2\bi\3\2\2\2\nk\3\2\2\2\fn\3\2\2\2\16q\3\2\2\2\20t\3"+
		"\2\2\2\22w\3\2\2\2\24\31\5\4\3\2\25\26\7\23\2\2\26\30\5\4\3\2\27\25\3"+
		"\2\2\2\30\33\3\2\2\2\31\27\3\2\2\2\31\32\3\2\2\2\32\37\3\2\2\2\33\31\3"+
		"\2\2\2\34\36\7\23\2\2\35\34\3\2\2\2\36!\3\2\2\2\37\35\3\2\2\2\37 \3\2"+
		"\2\2 \"\3\2\2\2!\37\3\2\2\2\"#\7\2\2\3#&\3\2\2\2$&\7\2\2\3%\24\3\2\2\2"+
		"%$\3\2\2\2&\3\3\2\2\2\'(\b\3\1\2()\7\t\2\2)*\5\4\3\2*+\5\b\5\2+,\5\4\3"+
		"\2,-\7\n\2\2-P\3\2\2\2./\7\f\2\2/P\5\4\3\b\60\61\7\r\2\2\61\62\7\t\2\2"+
		"\62\63\5\n\6\2\63\64\5\22\n\2\64\65\5\4\3\2\65\66\7\n\2\2\66P\3\2\2\2"+
		"\678\7\16\2\289\7\t\2\29:\5\n\6\2:;\5\22\n\2;<\5\4\3\2<=\7\n\2\2=P\3\2"+
		"\2\2>?\5\f\7\2?@\7\t\2\2@F\5\6\4\2AB\5\22\n\2BC\5\6\4\2CE\3\2\2\2DA\3"+
		"\2\2\2EH\3\2\2\2FD\3\2\2\2FG\3\2\2\2GI\3\2\2\2HF\3\2\2\2IJ\7\n\2\2JP\3"+
		"\2\2\2KL\5\6\4\2LM\7\13\2\2MN\5\6\4\2NP\3\2\2\2O\'\3\2\2\2O.\3\2\2\2O"+
		"\60\3\2\2\2O\67\3\2\2\2O>\3\2\2\2OK\3\2\2\2PU\3\2\2\2QR\f\3\2\2RT\7\4"+
		"\2\2SQ\3\2\2\2TW\3\2\2\2US\3\2\2\2UV\3\2\2\2V\5\3\2\2\2WU\3\2\2\2Xh\5"+
		"\16\b\2Yh\5\n\6\2Z[\5\20\t\2[\\\7\t\2\2\\b\5\6\4\2]^\5\22\n\2^_\5\6\4"+
		"\2_a\3\2\2\2`]\3\2\2\2ad\3\2\2\2b`\3\2\2\2bc\3\2\2\2ce\3\2\2\2db\3\2\2"+
		"\2ef\7\n\2\2fh\3\2\2\2gX\3\2\2\2gY\3\2\2\2gZ\3\2\2\2h\7\3\2\2\2ij\t\2"+
		"\2\2j\t\3\2\2\2kl\7\4\2\2lm\7\b\2\2m\13\3\2\2\2no\7\5\2\2op\7\b\2\2p\r"+
		"\3\2\2\2qr\7\6\2\2rs\7\b\2\2s\17\3\2\2\2tu\7\7\2\2uv\7\b\2\2v\21\3\2\2"+
		"\2wx\7\3\2\2x\23\3\2\2\2\n\31\37%FOUbg";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}