// Generated from java-escape by ANTLR 4.11.1
package sl.fside.services.code_generator1.gen;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class PythonLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.11.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, CharArray=16;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "CharArray"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'Seq'", "'('", "','", "')'", "'Branch'", "'BranchRe'", "'Concur'", 
			"'ConcurRe'", "'Cond'", "'Para'", "'Loop'", "'Choice'", "'SeqSeq'", "'Repeat'", 
			"'#'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, "CharArray"
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


	public PythonLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Python.g4"; }

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
		"\u0004\u0000\u0010|\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002"+
		"\u000f\u0007\u000f\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r"+
		"\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e"+
		"\u0001\u000f\u0004\u000fs\b\u000f\u000b\u000f\f\u000ft\u0001\u000f\u0005"+
		"\u000fx\b\u000f\n\u000f\f\u000f{\t\u000f\u0000\u0000\u0010\u0001\u0001"+
		"\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f"+
		"\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e\u001d\u000f"+
		"\u001f\u0010\u0001\u0000\u0002\u0004\u000009AZ__az\b\u0000 !%%++09<?A"+
		"Z__az}\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000"+
		"\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000"+
		"\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000"+
		"\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000"+
		"\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000"+
		"\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000"+
		"\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000"+
		"\u001d\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0001"+
		"!\u0001\u0000\u0000\u0000\u0003%\u0001\u0000\u0000\u0000\u0005\'\u0001"+
		"\u0000\u0000\u0000\u0007)\u0001\u0000\u0000\u0000\t+\u0001\u0000\u0000"+
		"\u0000\u000b2\u0001\u0000\u0000\u0000\r;\u0001\u0000\u0000\u0000\u000f"+
		"B\u0001\u0000\u0000\u0000\u0011K\u0001\u0000\u0000\u0000\u0013P\u0001"+
		"\u0000\u0000\u0000\u0015U\u0001\u0000\u0000\u0000\u0017Z\u0001\u0000\u0000"+
		"\u0000\u0019a\u0001\u0000\u0000\u0000\u001bh\u0001\u0000\u0000\u0000\u001d"+
		"o\u0001\u0000\u0000\u0000\u001fr\u0001\u0000\u0000\u0000!\"\u0005S\u0000"+
		"\u0000\"#\u0005e\u0000\u0000#$\u0005q\u0000\u0000$\u0002\u0001\u0000\u0000"+
		"\u0000%&\u0005(\u0000\u0000&\u0004\u0001\u0000\u0000\u0000\'(\u0005,\u0000"+
		"\u0000(\u0006\u0001\u0000\u0000\u0000)*\u0005)\u0000\u0000*\b\u0001\u0000"+
		"\u0000\u0000+,\u0005B\u0000\u0000,-\u0005r\u0000\u0000-.\u0005a\u0000"+
		"\u0000./\u0005n\u0000\u0000/0\u0005c\u0000\u000001\u0005h\u0000\u0000"+
		"1\n\u0001\u0000\u0000\u000023\u0005B\u0000\u000034\u0005r\u0000\u0000"+
		"45\u0005a\u0000\u000056\u0005n\u0000\u000067\u0005c\u0000\u000078\u0005"+
		"h\u0000\u000089\u0005R\u0000\u00009:\u0005e\u0000\u0000:\f\u0001\u0000"+
		"\u0000\u0000;<\u0005C\u0000\u0000<=\u0005o\u0000\u0000=>\u0005n\u0000"+
		"\u0000>?\u0005c\u0000\u0000?@\u0005u\u0000\u0000@A\u0005r\u0000\u0000"+
		"A\u000e\u0001\u0000\u0000\u0000BC\u0005C\u0000\u0000CD\u0005o\u0000\u0000"+
		"DE\u0005n\u0000\u0000EF\u0005c\u0000\u0000FG\u0005u\u0000\u0000GH\u0005"+
		"r\u0000\u0000HI\u0005R\u0000\u0000IJ\u0005e\u0000\u0000J\u0010\u0001\u0000"+
		"\u0000\u0000KL\u0005C\u0000\u0000LM\u0005o\u0000\u0000MN\u0005n\u0000"+
		"\u0000NO\u0005d\u0000\u0000O\u0012\u0001\u0000\u0000\u0000PQ\u0005P\u0000"+
		"\u0000QR\u0005a\u0000\u0000RS\u0005r\u0000\u0000ST\u0005a\u0000\u0000"+
		"T\u0014\u0001\u0000\u0000\u0000UV\u0005L\u0000\u0000VW\u0005o\u0000\u0000"+
		"WX\u0005o\u0000\u0000XY\u0005p\u0000\u0000Y\u0016\u0001\u0000\u0000\u0000"+
		"Z[\u0005C\u0000\u0000[\\\u0005h\u0000\u0000\\]\u0005o\u0000\u0000]^\u0005"+
		"i\u0000\u0000^_\u0005c\u0000\u0000_`\u0005e\u0000\u0000`\u0018\u0001\u0000"+
		"\u0000\u0000ab\u0005S\u0000\u0000bc\u0005e\u0000\u0000cd\u0005q\u0000"+
		"\u0000de\u0005S\u0000\u0000ef\u0005e\u0000\u0000fg\u0005q\u0000\u0000"+
		"g\u001a\u0001\u0000\u0000\u0000hi\u0005R\u0000\u0000ij\u0005e\u0000\u0000"+
		"jk\u0005p\u0000\u0000kl\u0005e\u0000\u0000lm\u0005a\u0000\u0000mn\u0005"+
		"t\u0000\u0000n\u001c\u0001\u0000\u0000\u0000op\u0005#\u0000\u0000p\u001e"+
		"\u0001\u0000\u0000\u0000qs\u0007\u0000\u0000\u0000rq\u0001\u0000\u0000"+
		"\u0000st\u0001\u0000\u0000\u0000tr\u0001\u0000\u0000\u0000tu\u0001\u0000"+
		"\u0000\u0000uy\u0001\u0000\u0000\u0000vx\u0007\u0001\u0000\u0000wv\u0001"+
		"\u0000\u0000\u0000x{\u0001\u0000\u0000\u0000yw\u0001\u0000\u0000\u0000"+
		"yz\u0001\u0000\u0000\u0000z \u0001\u0000\u0000\u0000{y\u0001\u0000\u0000"+
		"\u0000\u0003\u0000ty\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}