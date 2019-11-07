// Generated from /Users/yongchun.zyc/Documents/Drive/projects/hasor/hasor.git/hasor-dataql/src/main/java/net/hasor/dataql/domain/parser/DataQL.g4 by ANTLR 4.7.2
package net.hasor.dataql.domain.parser;
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
		ELSEIF=10, ELSE=11, END=12, RETURN=13, THROW=14, EXIT=15, VAR=16, OPTION=17, 
		IMPORT=18, TRUE=19, FALSE=20, NULL=21, AS=22, PLUS=23, MINUS=24, MUL=25, 
		DIV=26, DIV2=27, MOD=28, LBT=29, RBT=30, AND=31, OR=32, NOT=33, XOR=34, 
		LSHIFT=35, RSHIFT=36, RSHIFT2=37, GT=38, GE=39, LT=40, LE=41, EQ=42, NE=43, 
		SC_OR=44, SC_AND=45, COMMA=46, COLON=47, ASS=48, DOT=49, LSBT=50, RSBT=51, 
		OCBR=52, CCBR=53, ROU=54, STRING=55, HEX_NUM=56, OCT_NUM=57, BIT_NUM=58, 
		INTEGER_NUM=59, DECIMAL_NUM=60, IDENTIFIER=61;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "WS", "COMMENT1", "COMMENT2", "EOL", 
			"IF", "ELSEIF", "ELSE", "END", "RETURN", "THROW", "EXIT", "VAR", "OPTION", 
			"IMPORT", "TRUE", "FALSE", "NULL", "AS", "PLUS", "MINUS", "MUL", "DIV", 
			"DIV2", "MOD", "LBT", "RBT", "AND", "OR", "NOT", "XOR", "LSHIFT", "RSHIFT", 
			"RSHIFT2", "GT", "GE", "LT", "LE", "EQ", "NE", "SC_OR", "SC_AND", "COMMA", 
			"COLON", "ASS", "DOT", "LSBT", "RSBT", "OCBR", "CCBR", "ROU", "STRING", 
			"TRANS", "UNICODE", "HEX", "HEX_NUM", "OCT_NUM", "BIT_NUM", "INTEGER_NUM", 
			"DECIMAL_NUM", "IDENTIFIER"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'->'", "'=>'", "'?'", null, null, null, null, "'if'", "'elseif'", 
			"'else'", "'end'", "'return'", "'throw'", "'exit'", "'var'", "'option'", 
			"'import'", "'true'", "'false'", "'null'", "'as'", "'+'", "'-'", "'*'", 
			"'/'", "'\\'", "'%'", "'('", "')'", "'&'", "'|'", "'!'", "'^'", "'<<'", 
			"'>>'", "'>>>'", "'>'", "'>='", "'<'", "'<='", "'=='", "'!='", "'||'", 
			"'&&'", "','", "':'", "'='", "'.'", "'['", "']'", "'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, "WS", "COMMENT1", "COMMENT2", "EOL", "IF", 
			"ELSEIF", "ELSE", "END", "RETURN", "THROW", "EXIT", "VAR", "OPTION", 
			"IMPORT", "TRUE", "FALSE", "NULL", "AS", "PLUS", "MINUS", "MUL", "DIV", 
			"DIV2", "MOD", "LBT", "RBT", "AND", "OR", "NOT", "XOR", "LSHIFT", "RSHIFT", 
			"RSHIFT2", "GT", "GE", "LT", "LE", "EQ", "NE", "SC_OR", "SC_AND", "COMMA", 
			"COLON", "ASS", "DOT", "LSBT", "RSBT", "OCBR", "CCBR", "ROU", "STRING", 
			"HEX_NUM", "OCT_NUM", "BIT_NUM", "INTEGER_NUM", "DECIMAL_NUM", "IDENTIFIER"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2?\u01b2\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\6\6"+
		"\6\u008f\n\6\r\6\16\6\u0090\3\6\3\6\3\7\3\7\3\7\3\7\7\7\u0099\n\7\f\7"+
		"\16\7\u009c\13\7\3\7\5\7\u009f\n\7\3\7\3\7\3\b\3\b\3\b\3\b\7\b\u00a7\n"+
		"\b\f\b\16\b\u00aa\13\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\13\3"+
		"\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20"+
		"\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\30\3\30"+
		"\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37"+
		"\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3$\3%\3%\3%\3&\3&\3&\3&\3\'\3\'\3(\3"+
		"(\3(\3)\3)\3*\3*\3*\3+\3+\3+\3,\3,\3,\3-\3-\3-\3.\3.\3.\3/\3/\3\60\3\60"+
		"\3\61\3\61\3\62\3\62\3\63\3\63\3\64\3\64\3\65\3\65\3\66\3\66\3\67\3\67"+
		"\38\38\38\38\38\78\u014c\n8\f8\168\u014f\138\38\38\38\38\38\38\78\u0157"+
		"\n8\f8\168\u015a\138\38\58\u015d\n8\39\39\39\59\u0162\n9\3:\3:\3:\3:\3"+
		":\3:\3;\3;\3<\3<\3<\6<\u016f\n<\r<\16<\u0170\3=\3=\3=\6=\u0176\n=\r=\16"+
		"=\u0177\3>\3>\3>\6>\u017d\n>\r>\16>\u017e\3?\5?\u0182\n?\3?\6?\u0185\n"+
		"?\r?\16?\u0186\3@\5@\u018a\n@\3@\7@\u018d\n@\f@\16@\u0190\13@\3@\3@\6"+
		"@\u0194\n@\r@\16@\u0195\3@\6@\u0199\n@\r@\16@\u019a\5@\u019d\n@\3@\3@"+
		"\5@\u01a1\n@\3@\3@\7@\u01a5\n@\f@\16@\u01a8\13@\5@\u01aa\n@\3A\3A\7A\u01ae"+
		"\nA\fA\16A\u01b1\13A\2\2B\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25"+
		"\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32"+
		"\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a"+
		"\62c\63e\64g\65i\66k\67m8o9q\2s\2u\2w:y;{<}=\177>\u0081?\3\2\25\5\2\13"+
		"\f\16\17\"\"\4\2\f\f\17\17\4\2\f\f\16\17\4\2%&BB\5\2\f\f\17\17$$\5\2\f"+
		"\f\17\17))\13\2$$))\61\61^^ddhhppttvv\5\2\62;CHch\4\2ZZzz\4\2QQqq\3\2"+
		"\629\4\2DDdd\3\2\62\63\3\2\62;\3\2\63;\4\2GGgg\4\2--//\5\2C\\aac|\6\2"+
		"\62;C\\aac|\2\u01c8\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2"+
		"\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3"+
		"\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2"+
		"\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2"+
		"\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2"+
		"\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2"+
		"\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q"+
		"\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2"+
		"\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2"+
		"\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}"+
		"\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\3\u0083\3\2\2\2\5\u0085\3\2\2\2"+
		"\7\u0088\3\2\2\2\t\u008b\3\2\2\2\13\u008e\3\2\2\2\r\u0094\3\2\2\2\17\u00a2"+
		"\3\2\2\2\21\u00b0\3\2\2\2\23\u00b2\3\2\2\2\25\u00b5\3\2\2\2\27\u00bc\3"+
		"\2\2\2\31\u00c1\3\2\2\2\33\u00c5\3\2\2\2\35\u00cc\3\2\2\2\37\u00d2\3\2"+
		"\2\2!\u00d7\3\2\2\2#\u00db\3\2\2\2%\u00e2\3\2\2\2\'\u00e9\3\2\2\2)\u00ee"+
		"\3\2\2\2+\u00f4\3\2\2\2-\u00f9\3\2\2\2/\u00fc\3\2\2\2\61\u00fe\3\2\2\2"+
		"\63\u0100\3\2\2\2\65\u0102\3\2\2\2\67\u0104\3\2\2\29\u0106\3\2\2\2;\u0108"+
		"\3\2\2\2=\u010a\3\2\2\2?\u010c\3\2\2\2A\u010e\3\2\2\2C\u0110\3\2\2\2E"+
		"\u0112\3\2\2\2G\u0114\3\2\2\2I\u0117\3\2\2\2K\u011a\3\2\2\2M\u011e\3\2"+
		"\2\2O\u0120\3\2\2\2Q\u0123\3\2\2\2S\u0125\3\2\2\2U\u0128\3\2\2\2W\u012b"+
		"\3\2\2\2Y\u012e\3\2\2\2[\u0131\3\2\2\2]\u0134\3\2\2\2_\u0136\3\2\2\2a"+
		"\u0138\3\2\2\2c\u013a\3\2\2\2e\u013c\3\2\2\2g\u013e\3\2\2\2i\u0140\3\2"+
		"\2\2k\u0142\3\2\2\2m\u0144\3\2\2\2o\u015c\3\2\2\2q\u015e\3\2\2\2s\u0163"+
		"\3\2\2\2u\u0169\3\2\2\2w\u016b\3\2\2\2y\u0172\3\2\2\2{\u0179\3\2\2\2}"+
		"\u0181\3\2\2\2\177\u0189\3\2\2\2\u0081\u01ab\3\2\2\2\u0083\u0084\7=\2"+
		"\2\u0084\4\3\2\2\2\u0085\u0086\7/\2\2\u0086\u0087\7@\2\2\u0087\6\3\2\2"+
		"\2\u0088\u0089\7?\2\2\u0089\u008a\7@\2\2\u008a\b\3\2\2\2\u008b\u008c\7"+
		"A\2\2\u008c\n\3\2\2\2\u008d\u008f\t\2\2\2\u008e\u008d\3\2\2\2\u008f\u0090"+
		"\3\2\2\2\u0090\u008e\3\2\2\2\u0090\u0091\3\2\2\2\u0091\u0092\3\2\2\2\u0092"+
		"\u0093\b\6\2\2\u0093\f\3\2\2\2\u0094\u0095\7\61\2\2\u0095\u0096\7\61\2"+
		"\2\u0096\u009a\3\2\2\2\u0097\u0099\n\3\2\2\u0098\u0097\3\2\2\2\u0099\u009c"+
		"\3\2\2\2\u009a\u0098\3\2\2\2\u009a\u009b\3\2\2\2\u009b\u009e\3\2\2\2\u009c"+
		"\u009a\3\2\2\2\u009d\u009f\5\21\t\2\u009e\u009d\3\2\2\2\u009e\u009f\3"+
		"\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a1\b\7\2\2\u00a1\16\3\2\2\2\u00a2"+
		"\u00a3\7\61\2\2\u00a3\u00a4\7,\2\2\u00a4\u00a8\3\2\2\2\u00a5\u00a7\3\2"+
		"\2\2\u00a6\u00a5\3\2\2\2\u00a7\u00aa\3\2\2\2\u00a8\u00a6\3\2\2\2\u00a8"+
		"\u00a9\3\2\2\2\u00a9\u00ab\3\2\2\2\u00aa\u00a8\3\2\2\2\u00ab\u00ac\7,"+
		"\2\2\u00ac\u00ad\7\61\2\2\u00ad\u00ae\3\2\2\2\u00ae\u00af\b\b\2\2\u00af"+
		"\20\3\2\2\2\u00b0\u00b1\t\4\2\2\u00b1\22\3\2\2\2\u00b2\u00b3\7k\2\2\u00b3"+
		"\u00b4\7h\2\2\u00b4\24\3\2\2\2\u00b5\u00b6\7g\2\2\u00b6\u00b7\7n\2\2\u00b7"+
		"\u00b8\7u\2\2\u00b8\u00b9\7g\2\2\u00b9\u00ba\7k\2\2\u00ba\u00bb\7h\2\2"+
		"\u00bb\26\3\2\2\2\u00bc\u00bd\7g\2\2\u00bd\u00be\7n\2\2\u00be\u00bf\7"+
		"u\2\2\u00bf\u00c0\7g\2\2\u00c0\30\3\2\2\2\u00c1\u00c2\7g\2\2\u00c2\u00c3"+
		"\7p\2\2\u00c3\u00c4\7f\2\2\u00c4\32\3\2\2\2\u00c5\u00c6\7t\2\2\u00c6\u00c7"+
		"\7g\2\2\u00c7\u00c8\7v\2\2\u00c8\u00c9\7w\2\2\u00c9\u00ca\7t\2\2\u00ca"+
		"\u00cb\7p\2\2\u00cb\34\3\2\2\2\u00cc\u00cd\7v\2\2\u00cd\u00ce\7j\2\2\u00ce"+
		"\u00cf\7t\2\2\u00cf\u00d0\7q\2\2\u00d0\u00d1\7y\2\2\u00d1\36\3\2\2\2\u00d2"+
		"\u00d3\7g\2\2\u00d3\u00d4\7z\2\2\u00d4\u00d5\7k\2\2\u00d5\u00d6\7v\2\2"+
		"\u00d6 \3\2\2\2\u00d7\u00d8\7x\2\2\u00d8\u00d9\7c\2\2\u00d9\u00da\7t\2"+
		"\2\u00da\"\3\2\2\2\u00db\u00dc\7q\2\2\u00dc\u00dd\7r\2\2\u00dd\u00de\7"+
		"v\2\2\u00de\u00df\7k\2\2\u00df\u00e0\7q\2\2\u00e0\u00e1\7p\2\2\u00e1$"+
		"\3\2\2\2\u00e2\u00e3\7k\2\2\u00e3\u00e4\7o\2\2\u00e4\u00e5\7r\2\2\u00e5"+
		"\u00e6\7q\2\2\u00e6\u00e7\7t\2\2\u00e7\u00e8\7v\2\2\u00e8&\3\2\2\2\u00e9"+
		"\u00ea\7v\2\2\u00ea\u00eb\7t\2\2\u00eb\u00ec\7w\2\2\u00ec\u00ed\7g\2\2"+
		"\u00ed(\3\2\2\2\u00ee\u00ef\7h\2\2\u00ef\u00f0\7c\2\2\u00f0\u00f1\7n\2"+
		"\2\u00f1\u00f2\7u\2\2\u00f2\u00f3\7g\2\2\u00f3*\3\2\2\2\u00f4\u00f5\7"+
		"p\2\2\u00f5\u00f6\7w\2\2\u00f6\u00f7\7n\2\2\u00f7\u00f8\7n\2\2\u00f8,"+
		"\3\2\2\2\u00f9\u00fa\7c\2\2\u00fa\u00fb\7u\2\2\u00fb.\3\2\2\2\u00fc\u00fd"+
		"\7-\2\2\u00fd\60\3\2\2\2\u00fe\u00ff\7/\2\2\u00ff\62\3\2\2\2\u0100\u0101"+
		"\7,\2\2\u0101\64\3\2\2\2\u0102\u0103\7\61\2\2\u0103\66\3\2\2\2\u0104\u0105"+
		"\7^\2\2\u01058\3\2\2\2\u0106\u0107\7\'\2\2\u0107:\3\2\2\2\u0108\u0109"+
		"\7*\2\2\u0109<\3\2\2\2\u010a\u010b\7+\2\2\u010b>\3\2\2\2\u010c\u010d\7"+
		"(\2\2\u010d@\3\2\2\2\u010e\u010f\7~\2\2\u010fB\3\2\2\2\u0110\u0111\7#"+
		"\2\2\u0111D\3\2\2\2\u0112\u0113\7`\2\2\u0113F\3\2\2\2\u0114\u0115\7>\2"+
		"\2\u0115\u0116\7>\2\2\u0116H\3\2\2\2\u0117\u0118\7@\2\2\u0118\u0119\7"+
		"@\2\2\u0119J\3\2\2\2\u011a\u011b\7@\2\2\u011b\u011c\7@\2\2\u011c\u011d"+
		"\7@\2\2\u011dL\3\2\2\2\u011e\u011f\7@\2\2\u011fN\3\2\2\2\u0120\u0121\7"+
		"@\2\2\u0121\u0122\7?\2\2\u0122P\3\2\2\2\u0123\u0124\7>\2\2\u0124R\3\2"+
		"\2\2\u0125\u0126\7>\2\2\u0126\u0127\7?\2\2\u0127T\3\2\2\2\u0128\u0129"+
		"\7?\2\2\u0129\u012a\7?\2\2\u012aV\3\2\2\2\u012b\u012c\7#\2\2\u012c\u012d"+
		"\7?\2\2\u012dX\3\2\2\2\u012e\u012f\7~\2\2\u012f\u0130\7~\2\2\u0130Z\3"+
		"\2\2\2\u0131\u0132\7(\2\2\u0132\u0133\7(\2\2\u0133\\\3\2\2\2\u0134\u0135"+
		"\7.\2\2\u0135^\3\2\2\2\u0136\u0137\7<\2\2\u0137`\3\2\2\2\u0138\u0139\7"+
		"?\2\2\u0139b\3\2\2\2\u013a\u013b\7\60\2\2\u013bd\3\2\2\2\u013c\u013d\7"+
		"]\2\2\u013df\3\2\2\2\u013e\u013f\7_\2\2\u013fh\3\2\2\2\u0140\u0141\7}"+
		"\2\2\u0141j\3\2\2\2\u0142\u0143\7\177\2\2\u0143l\3\2\2\2\u0144\u0145\t"+
		"\5\2\2\u0145n\3\2\2\2\u0146\u014d\7$\2\2\u0147\u014c\n\6\2\2\u0148\u0149"+
		"\7$\2\2\u0149\u014c\7$\2\2\u014a\u014c\5q9\2\u014b\u0147\3\2\2\2\u014b"+
		"\u0148\3\2\2\2\u014b\u014a\3\2\2\2\u014c\u014f\3\2\2\2\u014d\u014b\3\2"+
		"\2\2\u014d\u014e\3\2\2\2\u014e\u0150\3\2\2\2\u014f\u014d\3\2\2\2\u0150"+
		"\u015d\7$\2\2\u0151\u0158\7)\2\2\u0152\u0157\n\7\2\2\u0153\u0154\7)\2"+
		"\2\u0154\u0157\7)\2\2\u0155\u0157\5q9\2\u0156\u0152\3\2\2\2\u0156\u0153"+
		"\3\2\2\2\u0156\u0155\3\2\2\2\u0157\u015a\3\2\2\2\u0158\u0156\3\2\2\2\u0158"+
		"\u0159\3\2\2\2\u0159\u015b\3\2\2\2\u015a\u0158\3\2\2\2\u015b\u015d\7)"+
		"\2\2\u015c\u0146\3\2\2\2\u015c\u0151\3\2\2\2\u015dp\3\2\2\2\u015e\u0161"+
		"\7^\2\2\u015f\u0162\t\b\2\2\u0160\u0162\5s:\2\u0161\u015f\3\2\2\2\u0161"+
		"\u0160\3\2\2\2\u0162r\3\2\2\2\u0163\u0164\7w\2\2\u0164\u0165\5u;\2\u0165"+
		"\u0166\5u;\2\u0166\u0167\5u;\2\u0167\u0168\5u;\2\u0168t\3\2\2\2\u0169"+
		"\u016a\t\t\2\2\u016av\3\2\2\2\u016b\u016c\7\62\2\2\u016c\u016e\t\n\2\2"+
		"\u016d\u016f\t\t\2\2\u016e\u016d\3\2\2\2\u016f\u0170\3\2\2\2\u0170\u016e"+
		"\3\2\2\2\u0170\u0171\3\2\2\2\u0171x\3\2\2\2\u0172\u0173\7\62\2\2\u0173"+
		"\u0175\t\13\2\2\u0174\u0176\t\f\2\2\u0175\u0174\3\2\2\2\u0176\u0177\3"+
		"\2\2\2\u0177\u0175\3\2\2\2\u0177\u0178\3\2\2\2\u0178z\3\2\2\2\u0179\u017a"+
		"\7\62\2\2\u017a\u017c\t\r\2\2\u017b\u017d\t\16\2\2\u017c\u017b\3\2\2\2"+
		"\u017d\u017e\3\2\2\2\u017e\u017c\3\2\2\2\u017e\u017f\3\2\2\2\u017f|\3"+
		"\2\2\2\u0180\u0182\7/\2\2\u0181\u0180\3\2\2\2\u0181\u0182\3\2\2\2\u0182"+
		"\u0184\3\2\2\2\u0183\u0185\t\17\2\2\u0184\u0183\3\2\2\2\u0185\u0186\3"+
		"\2\2\2\u0186\u0184\3\2\2\2\u0186\u0187\3\2\2\2\u0187~\3\2\2\2\u0188\u018a"+
		"\7/\2\2\u0189\u0188\3\2\2\2\u0189\u018a\3\2\2\2\u018a\u019c\3\2\2\2\u018b"+
		"\u018d\t\17\2\2\u018c\u018b\3\2\2\2\u018d\u0190\3\2\2\2\u018e\u018c\3"+
		"\2\2\2\u018e\u018f\3\2\2\2\u018f\u0191\3\2\2\2\u0190\u018e\3\2\2\2\u0191"+
		"\u0193\7\60\2\2\u0192\u0194\t\17\2\2\u0193\u0192\3\2\2\2\u0194\u0195\3"+
		"\2\2\2\u0195\u0193\3\2\2\2\u0195\u0196\3\2\2\2\u0196\u019d\3\2\2\2\u0197"+
		"\u0199\t\20\2\2\u0198\u0197\3\2\2\2\u0199\u019a\3\2\2\2\u019a\u0198\3"+
		"\2\2\2\u019a\u019b\3\2\2\2\u019b\u019d\3\2\2\2\u019c\u018e\3\2\2\2\u019c"+
		"\u0198\3\2\2\2\u019d\u01a9\3\2\2\2\u019e\u01a0\t\21\2\2\u019f\u01a1\t"+
		"\22\2\2\u01a0\u019f\3\2\2\2\u01a0\u01a1\3\2\2\2\u01a1\u01a2\3\2\2\2\u01a2"+
		"\u01a6\t\20\2\2\u01a3\u01a5\t\17\2\2\u01a4\u01a3\3\2\2\2\u01a5\u01a8\3"+
		"\2\2\2\u01a6\u01a4\3\2\2\2\u01a6\u01a7\3\2\2\2\u01a7\u01aa\3\2\2\2\u01a8"+
		"\u01a6\3\2\2\2\u01a9\u019e\3\2\2\2\u01a9\u01aa\3\2\2\2\u01aa\u0080\3\2"+
		"\2\2\u01ab\u01af\t\23\2\2\u01ac\u01ae\t\24\2\2\u01ad\u01ac\3\2\2\2\u01ae"+
		"\u01b1\3\2\2\2\u01af\u01ad\3\2\2\2\u01af\u01b0\3\2\2\2\u01b0\u0082\3\2"+
		"\2\2\u01b1\u01af\3\2\2\2\33\2\u0090\u009a\u009e\u00a8\u014b\u014d\u0156"+
		"\u0158\u015c\u0161\u0170\u0177\u017e\u0181\u0186\u0189\u018e\u0195\u019a"+
		"\u019c\u01a0\u01a6\u01a9\u01af\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}