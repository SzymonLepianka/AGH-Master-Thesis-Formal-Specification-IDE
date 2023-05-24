package tl_to_spass;

// Generated from TL.g4 by ANTLR 4.7
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TLLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, VAR_ID=2, PRED_ID=3, IND_ID=4, FUNC_ID=5, WORD=6, LPAREN=7, RPAREN=8, 
		EQUAL=9, NOT=10, ALWAYS=11, EVENTUALLY=12, CONJ=13, DISJ=14, IMPL=15, 
		BICOND=16, ENDLINE=17, WHITESPACE=18;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "VAR_ID", "PRED_ID", "IND_ID", "FUNC_ID", "WORD", "LPAREN", "RPAREN", 
		"EQUAL", "NOT", "ALWAYS", "EVENTUALLY", "CHARACTER", "CONJ", "DISJ", "IMPL", 
		"BICOND", "ENDLINE", "WHITESPACE"
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
        TLLexer(){
            System.out.println(_ATN);
        }
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


	public TLLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "TL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\24Z\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\6\7\65"+
		"\n\7\r\7\16\7\66\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16"+
		"\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\6\23P\n\23\r\23\16"+
		"\23Q\3\24\6\24U\n\24\r\24\16\24V\3\24\3\24\2\2\25\3\3\5\4\7\5\t\6\13\7"+
		"\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\2\35\17\37\20!\21#\22%\23\'\24"+
		"\3\2\5\5\2\62;C\\c|\4\2\f\f\17\17\4\2\13\13\"\"\2[\2\3\3\2\2\2\2\5\3\2"+
		"\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21"+
		"\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\35\3\2"+
		"\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\3)\3\2"+
		"\2\2\5+\3\2\2\2\7-\3\2\2\2\t/\3\2\2\2\13\61\3\2\2\2\r\64\3\2\2\2\178\3"+
		"\2\2\2\21:\3\2\2\2\23<\3\2\2\2\25>\3\2\2\2\27@\3\2\2\2\31B\3\2\2\2\33"+
		"D\3\2\2\2\35F\3\2\2\2\37H\3\2\2\2!J\3\2\2\2#L\3\2\2\2%O\3\2\2\2\'T\3\2"+
		"\2\2)*\7.\2\2*\4\3\2\2\2+,\7A\2\2,\6\3\2\2\2-.\7a\2\2.\b\3\2\2\2/\60\7"+
		"%\2\2\60\n\3\2\2\2\61\62\7\60\2\2\62\f\3\2\2\2\63\65\5\33\16\2\64\63\3"+
		"\2\2\2\65\66\3\2\2\2\66\64\3\2\2\2\66\67\3\2\2\2\67\16\3\2\2\289\7*\2"+
		"\29\20\3\2\2\2:;\7+\2\2;\22\3\2\2\2<=\7?\2\2=\24\3\2\2\2>?\7\u00ae\2\2"+
		"?\26\3\2\2\2@A\7\u25a3\2\2A\30\3\2\2\2BC\7\u25c9\2\2C\32\3\2\2\2DE\t\2"+
		"\2\2E\34\3\2\2\2FG\7\u2229\2\2G\36\3\2\2\2HI\7\u222a\2\2I \3\2\2\2JK\7"+
		"\u2194\2\2K\"\3\2\2\2LM\7\u2196\2\2M$\3\2\2\2NP\t\3\2\2ON\3\2\2\2PQ\3"+
		"\2\2\2QO\3\2\2\2QR\3\2\2\2R&\3\2\2\2SU\t\4\2\2TS\3\2\2\2UV\3\2\2\2VT\3"+
		"\2\2\2VW\3\2\2\2WX\3\2\2\2XY\b\24\2\2Y(\3\2\2\2\6\2\66QV\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}