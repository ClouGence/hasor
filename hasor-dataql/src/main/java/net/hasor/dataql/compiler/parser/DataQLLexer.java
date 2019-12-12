// Generated from /Users/yongchun.zyc/Documents/Drive/projects/hasor/hasor.git/hasor-dataql/src/main/java/net/hasor/dataql/compiler/parser/DataQL.g4 by ANTLR 4.7.2
package net.hasor.dataql.compiler.parser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DataQLLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, WS=5, COMMENT1=6, COMMENT2=7, EOL=8, IF=9, 
		ELSE=10, RETURN=11, THROW=12, EXIT=13, VAR=14, RUN=15, HINT=16, IMPORT=17, 
		TRUE=18, FALSE=19, NULL=20, AS=21, PLUS=22, MINUS=23, MUL=24, DIV=25, 
		DIV2=26, MOD=27, LBT=28, RBT=29, AND=30, OR=31, NOT=32, XOR=33, LSHIFT=34, 
		RSHIFT=35, RSHIFT2=36, GT=37, GE=38, LT=39, LE=40, EQ=41, NE=42, SC_OR=43, 
		SC_AND=44, COMMA=45, COLON=46, ASS=47, DOT=48, LSBT=49, RSBT=50, OCBR=51, 
		CCBR=52, ROU=53, STRING=54, HEX_NUM=55, OCT_NUM=56, BIT_NUM=57, INTEGER_NUM=58, 
		DECIMAL_NUM=59, IDENTIFIER=60;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "WS", "COMMENT1", "COMMENT2", "EOL", 
			"IF", "ELSE", "RETURN", "THROW", "EXIT", "VAR", "RUN", "HINT", "IMPORT", 
			"TRUE", "FALSE", "NULL", "AS", "PLUS", "MINUS", "MUL", "DIV", "DIV2", 
			"MOD", "LBT", "RBT", "AND", "OR", "NOT", "XOR", "LSHIFT", "RSHIFT", "RSHIFT2", 
			"GT", "GE", "LT", "LE", "EQ", "NE", "SC_OR", "SC_AND", "COMMA", "COLON", 
			"ASS", "DOT", "LSBT", "RSBT", "OCBR", "CCBR", "ROU", "STRING", "TRANS", 
			"UNICODE", "HEX", "HEX_NUM", "OCT_NUM", "BIT_NUM", "INTEGER_NUM", "DECIMAL_NUM", 
			"IDENTIFIER"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'->'", "'=>'", "'?'", null, null, null, null, "'if'", "'else'", 
			"'return'", "'throw'", "'exit'", "'var'", "'run'", "'hint'", "'import'", 
			"'true'", "'false'", "'null'", "'as'", "'+'", "'-'", "'*'", "'/'", "'\\'", 
			"'%'", "'('", "')'", "'&'", "'|'", "'!'", "'^'", "'<<'", "'>>'", "'>>>'", 
			"'>'", "'>='", "'<'", "'<='", "'=='", "'!='", "'||'", "'&&'", "','", 
			"':'", "'='", "'.'", "'['", "']'", "'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, "WS", "COMMENT1", "COMMENT2", "EOL", "IF", 
			"ELSE", "RETURN", "THROW", "EXIT", "VAR", "RUN", "HINT", "IMPORT", "TRUE", 
			"FALSE", "NULL", "AS", "PLUS", "MINUS", "MUL", "DIV", "DIV2", "MOD", 
			"LBT", "RBT", "AND", "OR", "NOT", "XOR", "LSHIFT", "RSHIFT", "RSHIFT2", 
			"GT", "GE", "LT", "LE", "EQ", "NE", "SC_OR", "SC_AND", "COMMA", "COLON", 
			"ASS", "DOT", "LSBT", "RSBT", "OCBR", "CCBR", "ROU", "STRING", "HEX_NUM", 
			"OCT_NUM", "BIT_NUM", "INTEGER_NUM", "DECIMAL_NUM", "IDENTIFIER"
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


	public DataQLLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "DataQL.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2>\u01a7\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\6\6\6\u008d"+
		"\n\6\r\6\16\6\u008e\3\6\3\6\3\7\3\7\3\7\3\7\7\7\u0097\n\7\f\7\16\7\u009a"+
		"\13\7\3\7\5\7\u009d\n\7\3\7\3\7\3\b\3\b\3\b\3\b\7\b\u00a5\n\b\f\b\16\b"+
		"\u00a8\13\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3\13\3\13"+
		"\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3"+
		"\16\3\16\3\16\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3"+
		"\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3"+
		"\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3"+
		"\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3"+
		"\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3#\3$\3$\3$\3%\3%\3%\3%\3"+
		"&\3&\3\'\3\'\3\'\3(\3(\3)\3)\3)\3*\3*\3*\3+\3+\3+\3,\3,\3,\3-\3-\3-\3"+
		".\3.\3/\3/\3\60\3\60\3\61\3\61\3\62\3\62\3\63\3\63\3\64\3\64\3\65\3\65"+
		"\3\66\3\66\3\67\3\67\3\67\3\67\3\67\7\67\u0141\n\67\f\67\16\67\u0144\13"+
		"\67\3\67\3\67\3\67\3\67\3\67\3\67\7\67\u014c\n\67\f\67\16\67\u014f\13"+
		"\67\3\67\5\67\u0152\n\67\38\38\38\58\u0157\n8\39\39\39\39\39\39\3:\3:"+
		"\3;\3;\3;\6;\u0164\n;\r;\16;\u0165\3<\3<\3<\6<\u016b\n<\r<\16<\u016c\3"+
		"=\3=\3=\6=\u0172\n=\r=\16=\u0173\3>\5>\u0177\n>\3>\6>\u017a\n>\r>\16>"+
		"\u017b\3?\5?\u017f\n?\3?\7?\u0182\n?\f?\16?\u0185\13?\3?\3?\6?\u0189\n"+
		"?\r?\16?\u018a\3?\6?\u018e\n?\r?\16?\u018f\5?\u0192\n?\3?\3?\5?\u0196"+
		"\n?\3?\3?\7?\u019a\n?\f?\16?\u019d\13?\5?\u019f\n?\3@\3@\7@\u01a3\n@\f"+
		"@\16@\u01a6\13@\3\u00a6\2A\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25"+
		"\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32"+
		"\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a"+
		"\62c\63e\64g\65i\66k\67m8o\2q\2s\2u9w:y;{<}=\177>\3\2\25\5\2\13\f\16\17"+
		"\"\"\4\2\f\f\17\17\4\2\f\f\16\17\4\2%&BB\5\2\f\f\17\17$$\5\2\f\f\17\17"+
		"))\13\2$$))\61\61^^ddhhppttvv\5\2\62;CHch\4\2ZZzz\4\2QQqq\3\2\629\4\2"+
		"DDdd\3\2\62\63\3\2\62;\3\2\63;\4\2GGgg\4\2--//\5\2C\\aac|\6\2\62;C\\a"+
		"ac|\2\u01bd\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2"+
		"\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27"+
		"\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2"+
		"\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2"+
		"\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2"+
		"\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2"+
		"\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S"+
		"\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2"+
		"\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2"+
		"\2m\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177"+
		"\3\2\2\2\3\u0081\3\2\2\2\5\u0083\3\2\2\2\7\u0086\3\2\2\2\t\u0089\3\2\2"+
		"\2\13\u008c\3\2\2\2\r\u0092\3\2\2\2\17\u00a0\3\2\2\2\21\u00ae\3\2\2\2"+
		"\23\u00b0\3\2\2\2\25\u00b3\3\2\2\2\27\u00b8\3\2\2\2\31\u00bf\3\2\2\2\33"+
		"\u00c5\3\2\2\2\35\u00ca\3\2\2\2\37\u00ce\3\2\2\2!\u00d2\3\2\2\2#\u00d7"+
		"\3\2\2\2%\u00de\3\2\2\2\'\u00e3\3\2\2\2)\u00e9\3\2\2\2+\u00ee\3\2\2\2"+
		"-\u00f1\3\2\2\2/\u00f3\3\2\2\2\61\u00f5\3\2\2\2\63\u00f7\3\2\2\2\65\u00f9"+
		"\3\2\2\2\67\u00fb\3\2\2\29\u00fd\3\2\2\2;\u00ff\3\2\2\2=\u0101\3\2\2\2"+
		"?\u0103\3\2\2\2A\u0105\3\2\2\2C\u0107\3\2\2\2E\u0109\3\2\2\2G\u010c\3"+
		"\2\2\2I\u010f\3\2\2\2K\u0113\3\2\2\2M\u0115\3\2\2\2O\u0118\3\2\2\2Q\u011a"+
		"\3\2\2\2S\u011d\3\2\2\2U\u0120\3\2\2\2W\u0123\3\2\2\2Y\u0126\3\2\2\2["+
		"\u0129\3\2\2\2]\u012b\3\2\2\2_\u012d\3\2\2\2a\u012f\3\2\2\2c\u0131\3\2"+
		"\2\2e\u0133\3\2\2\2g\u0135\3\2\2\2i\u0137\3\2\2\2k\u0139\3\2\2\2m\u0151"+
		"\3\2\2\2o\u0153\3\2\2\2q\u0158\3\2\2\2s\u015e\3\2\2\2u\u0160\3\2\2\2w"+
		"\u0167\3\2\2\2y\u016e\3\2\2\2{\u0176\3\2\2\2}\u017e\3\2\2\2\177\u01a0"+
		"\3\2\2\2\u0081\u0082\7=\2\2\u0082\4\3\2\2\2\u0083\u0084\7/\2\2\u0084\u0085"+
		"\7@\2\2\u0085\6\3\2\2\2\u0086\u0087\7?\2\2\u0087\u0088\7@\2\2\u0088\b"+
		"\3\2\2\2\u0089\u008a\7A\2\2\u008a\n\3\2\2\2\u008b\u008d\t\2\2\2\u008c"+
		"\u008b\3\2\2\2\u008d\u008e\3\2\2\2\u008e\u008c\3\2\2\2\u008e\u008f\3\2"+
		"\2\2\u008f\u0090\3\2\2\2\u0090\u0091\b\6\2\2\u0091\f\3\2\2\2\u0092\u0093"+
		"\7\61\2\2\u0093\u0094\7\61\2\2\u0094\u0098\3\2\2\2\u0095\u0097\n\3\2\2"+
		"\u0096\u0095\3\2\2\2\u0097\u009a\3\2\2\2\u0098\u0096\3\2\2\2\u0098\u0099"+
		"\3\2\2\2\u0099\u009c\3\2\2\2\u009a\u0098\3\2\2\2\u009b\u009d\5\21\t\2"+
		"\u009c\u009b\3\2\2\2\u009c\u009d\3\2\2\2\u009d\u009e\3\2\2\2\u009e\u009f"+
		"\b\7\2\2\u009f\16\3\2\2\2\u00a0\u00a1\7\61\2\2\u00a1\u00a2\7,\2\2\u00a2"+
		"\u00a6\3\2\2\2\u00a3\u00a5\13\2\2\2\u00a4\u00a3\3\2\2\2\u00a5\u00a8\3"+
		"\2\2\2\u00a6\u00a7\3\2\2\2\u00a6\u00a4\3\2\2\2\u00a7\u00a9\3\2\2\2\u00a8"+
		"\u00a6\3\2\2\2\u00a9\u00aa\7,\2\2\u00aa\u00ab\7\61\2\2\u00ab\u00ac\3\2"+
		"\2\2\u00ac\u00ad\b\b\2\2\u00ad\20\3\2\2\2\u00ae\u00af\t\4\2\2\u00af\22"+
		"\3\2\2\2\u00b0\u00b1\7k\2\2\u00b1\u00b2\7h\2\2\u00b2\24\3\2\2\2\u00b3"+
		"\u00b4\7g\2\2\u00b4\u00b5\7n\2\2\u00b5\u00b6\7u\2\2\u00b6\u00b7\7g\2\2"+
		"\u00b7\26\3\2\2\2\u00b8\u00b9\7t\2\2\u00b9\u00ba\7g\2\2\u00ba\u00bb\7"+
		"v\2\2\u00bb\u00bc\7w\2\2\u00bc\u00bd\7t\2\2\u00bd\u00be\7p\2\2\u00be\30"+
		"\3\2\2\2\u00bf\u00c0\7v\2\2\u00c0\u00c1\7j\2\2\u00c1\u00c2\7t\2\2\u00c2"+
		"\u00c3\7q\2\2\u00c3\u00c4\7y\2\2\u00c4\32\3\2\2\2\u00c5\u00c6\7g\2\2\u00c6"+
		"\u00c7\7z\2\2\u00c7\u00c8\7k\2\2\u00c8\u00c9\7v\2\2\u00c9\34\3\2\2\2\u00ca"+
		"\u00cb\7x\2\2\u00cb\u00cc\7c\2\2\u00cc\u00cd\7t\2\2\u00cd\36\3\2\2\2\u00ce"+
		"\u00cf\7t\2\2\u00cf\u00d0\7w\2\2\u00d0\u00d1\7p\2\2\u00d1 \3\2\2\2\u00d2"+
		"\u00d3\7j\2\2\u00d3\u00d4\7k\2\2\u00d4\u00d5\7p\2\2\u00d5\u00d6\7v\2\2"+
		"\u00d6\"\3\2\2\2\u00d7\u00d8\7k\2\2\u00d8\u00d9\7o\2\2\u00d9\u00da\7r"+
		"\2\2\u00da\u00db\7q\2\2\u00db\u00dc\7t\2\2\u00dc\u00dd\7v\2\2\u00dd$\3"+
		"\2\2\2\u00de\u00df\7v\2\2\u00df\u00e0\7t\2\2\u00e0\u00e1\7w\2\2\u00e1"+
		"\u00e2\7g\2\2\u00e2&\3\2\2\2\u00e3\u00e4\7h\2\2\u00e4\u00e5\7c\2\2\u00e5"+
		"\u00e6\7n\2\2\u00e6\u00e7\7u\2\2\u00e7\u00e8\7g\2\2\u00e8(\3\2\2\2\u00e9"+
		"\u00ea\7p\2\2\u00ea\u00eb\7w\2\2\u00eb\u00ec\7n\2\2\u00ec\u00ed\7n\2\2"+
		"\u00ed*\3\2\2\2\u00ee\u00ef\7c\2\2\u00ef\u00f0\7u\2\2\u00f0,\3\2\2\2\u00f1"+
		"\u00f2\7-\2\2\u00f2.\3\2\2\2\u00f3\u00f4\7/\2\2\u00f4\60\3\2\2\2\u00f5"+
		"\u00f6\7,\2\2\u00f6\62\3\2\2\2\u00f7\u00f8\7\61\2\2\u00f8\64\3\2\2\2\u00f9"+
		"\u00fa\7^\2\2\u00fa\66\3\2\2\2\u00fb\u00fc\7\'\2\2\u00fc8\3\2\2\2\u00fd"+
		"\u00fe\7*\2\2\u00fe:\3\2\2\2\u00ff\u0100\7+\2\2\u0100<\3\2\2\2\u0101\u0102"+
		"\7(\2\2\u0102>\3\2\2\2\u0103\u0104\7~\2\2\u0104@\3\2\2\2\u0105\u0106\7"+
		"#\2\2\u0106B\3\2\2\2\u0107\u0108\7`\2\2\u0108D\3\2\2\2\u0109\u010a\7>"+
		"\2\2\u010a\u010b\7>\2\2\u010bF\3\2\2\2\u010c\u010d\7@\2\2\u010d\u010e"+
		"\7@\2\2\u010eH\3\2\2\2\u010f\u0110\7@\2\2\u0110\u0111\7@\2\2\u0111\u0112"+
		"\7@\2\2\u0112J\3\2\2\2\u0113\u0114\7@\2\2\u0114L\3\2\2\2\u0115\u0116\7"+
		"@\2\2\u0116\u0117\7?\2\2\u0117N\3\2\2\2\u0118\u0119\7>\2\2\u0119P\3\2"+
		"\2\2\u011a\u011b\7>\2\2\u011b\u011c\7?\2\2\u011cR\3\2\2\2\u011d\u011e"+
		"\7?\2\2\u011e\u011f\7?\2\2\u011fT\3\2\2\2\u0120\u0121\7#\2\2\u0121\u0122"+
		"\7?\2\2\u0122V\3\2\2\2\u0123\u0124\7~\2\2\u0124\u0125\7~\2\2\u0125X\3"+
		"\2\2\2\u0126\u0127\7(\2\2\u0127\u0128\7(\2\2\u0128Z\3\2\2\2\u0129\u012a"+
		"\7.\2\2\u012a\\\3\2\2\2\u012b\u012c\7<\2\2\u012c^\3\2\2\2\u012d\u012e"+
		"\7?\2\2\u012e`\3\2\2\2\u012f\u0130\7\60\2\2\u0130b\3\2\2\2\u0131\u0132"+
		"\7]\2\2\u0132d\3\2\2\2\u0133\u0134\7_\2\2\u0134f\3\2\2\2\u0135\u0136\7"+
		"}\2\2\u0136h\3\2\2\2\u0137\u0138\7\177\2\2\u0138j\3\2\2\2\u0139\u013a"+
		"\t\5\2\2\u013al\3\2\2\2\u013b\u0142\7$\2\2\u013c\u0141\n\6\2\2\u013d\u013e"+
		"\7$\2\2\u013e\u0141\7$\2\2\u013f\u0141\5o8\2\u0140\u013c\3\2\2\2\u0140"+
		"\u013d\3\2\2\2\u0140\u013f\3\2\2\2\u0141\u0144\3\2\2\2\u0142\u0140\3\2"+
		"\2\2\u0142\u0143\3\2\2\2\u0143\u0145\3\2\2\2\u0144\u0142\3\2\2\2\u0145"+
		"\u0152\7$\2\2\u0146\u014d\7)\2\2\u0147\u014c\n\7\2\2\u0148\u0149\7)\2"+
		"\2\u0149\u014c\7)\2\2\u014a\u014c\5o8\2\u014b\u0147\3\2\2\2\u014b\u0148"+
		"\3\2\2\2\u014b\u014a\3\2\2\2\u014c\u014f\3\2\2\2\u014d\u014b\3\2\2\2\u014d"+
		"\u014e\3\2\2\2\u014e\u0150\3\2\2\2\u014f\u014d\3\2\2\2\u0150\u0152\7)"+
		"\2\2\u0151\u013b\3\2\2\2\u0151\u0146\3\2\2\2\u0152n\3\2\2\2\u0153\u0156"+
		"\7^\2\2\u0154\u0157\t\b\2\2\u0155\u0157\5q9\2\u0156\u0154\3\2\2\2\u0156"+
		"\u0155\3\2\2\2\u0157p\3\2\2\2\u0158\u0159\7w\2\2\u0159\u015a\5s:\2\u015a"+
		"\u015b\5s:\2\u015b\u015c\5s:\2\u015c\u015d\5s:\2\u015dr\3\2\2\2\u015e"+
		"\u015f\t\t\2\2\u015ft\3\2\2\2\u0160\u0161\7\62\2\2\u0161\u0163\t\n\2\2"+
		"\u0162\u0164\t\t\2\2\u0163\u0162\3\2\2\2\u0164\u0165\3\2\2\2\u0165\u0163"+
		"\3\2\2\2\u0165\u0166\3\2\2\2\u0166v\3\2\2\2\u0167\u0168\7\62\2\2\u0168"+
		"\u016a\t\13\2\2\u0169\u016b\t\f\2\2\u016a\u0169\3\2\2\2\u016b\u016c\3"+
		"\2\2\2\u016c\u016a\3\2\2\2\u016c\u016d\3\2\2\2\u016dx\3\2\2\2\u016e\u016f"+
		"\7\62\2\2\u016f\u0171\t\r\2\2\u0170\u0172\t\16\2\2\u0171\u0170\3\2\2\2"+
		"\u0172\u0173\3\2\2\2\u0173\u0171\3\2\2\2\u0173\u0174\3\2\2\2\u0174z\3"+
		"\2\2\2\u0175\u0177\7/\2\2\u0176\u0175\3\2\2\2\u0176\u0177\3\2\2\2\u0177"+
		"\u0179\3\2\2\2\u0178\u017a\t\17\2\2\u0179\u0178\3\2\2\2\u017a\u017b\3"+
		"\2\2\2\u017b\u0179\3\2\2\2\u017b\u017c\3\2\2\2\u017c|\3\2\2\2\u017d\u017f"+
		"\7/\2\2\u017e\u017d\3\2\2\2\u017e\u017f\3\2\2\2\u017f\u0191\3\2\2\2\u0180"+
		"\u0182\t\17\2\2\u0181\u0180\3\2\2\2\u0182\u0185\3\2\2\2\u0183\u0181\3"+
		"\2\2\2\u0183\u0184\3\2\2\2\u0184\u0186\3\2\2\2\u0185\u0183\3\2\2\2\u0186"+
		"\u0188\7\60\2\2\u0187\u0189\t\17\2\2\u0188\u0187\3\2\2\2\u0189\u018a\3"+
		"\2\2\2\u018a\u0188\3\2\2\2\u018a\u018b\3\2\2\2\u018b\u0192\3\2\2\2\u018c"+
		"\u018e\t\20\2\2\u018d\u018c\3\2\2\2\u018e\u018f\3\2\2\2\u018f\u018d\3"+
		"\2\2\2\u018f\u0190\3\2\2\2\u0190\u0192\3\2\2\2\u0191\u0183\3\2\2\2\u0191"+
		"\u018d\3\2\2\2\u0192\u019e\3\2\2\2\u0193\u0195\t\21\2\2\u0194\u0196\t"+
		"\22\2\2\u0195\u0194\3\2\2\2\u0195\u0196\3\2\2\2\u0196\u0197\3\2\2\2\u0197"+
		"\u019b\t\20\2\2\u0198\u019a\t\17\2\2\u0199\u0198\3\2\2\2\u019a\u019d\3"+
		"\2\2\2\u019b\u0199\3\2\2\2\u019b\u019c\3\2\2\2\u019c\u019f\3\2\2\2\u019d"+
		"\u019b\3\2\2\2\u019e\u0193\3\2\2\2\u019e\u019f\3\2\2\2\u019f~\3\2\2\2"+
		"\u01a0\u01a4\t\23\2\2\u01a1\u01a3\t\24\2\2\u01a2\u01a1\3\2\2\2\u01a3\u01a6"+
		"\3\2\2\2\u01a4\u01a2\3\2\2\2\u01a4\u01a5\3\2\2\2\u01a5\u0080\3\2\2\2\u01a6"+
		"\u01a4\3\2\2\2\33\2\u008e\u0098\u009c\u00a6\u0140\u0142\u014b\u014d\u0151"+
		"\u0156\u0165\u016c\u0173\u0176\u017b\u017e\u0183\u018a\u018f\u0191\u0195"+
		"\u019b\u019e\u01a4\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}