// Generated from /Users/yongchun.zyc/Documents/Drive/projects/hasor/hasor.git/hasor-dataql/src/main/java/net/hasor/dataql/compiler/parser/DataQLLexer.g4 by ANTLR 4.7.2
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
		WS=1, COMMENT1=2, COMMENT2=3, EOL=4, AT=5, OPEN_TAG=6, IF=7, ELSE=8, RETURN=9, 
		THROW=10, EXIT=11, VAR=12, RUN=13, HINT=14, IMPORT=15, TRUE=16, FALSE=17, 
		NULL=18, AS=19, PLUS=20, MINUS=21, MUL=22, DIV=23, DIV2=24, MOD=25, LBT=26, 
		RBT=27, AND=28, OR=29, NOT=30, XOR=31, LSHIFT=32, RSHIFT=33, RSHIFT2=34, 
		GT=35, GE=36, LT=37, LE=38, EQ=39, NE=40, SC_OR=41, SC_AND=42, COMMA=43, 
		COLON=44, ASS=45, DOT=46, LSBT=47, RSBT=48, OCBR=49, CCBR=50, ROU=51, 
		QUE=52, SEM=53, CONVER=54, LAMBDA=55, STRING=56, HEX_NUM=57, OCT_NUM=58, 
		BIT_NUM=59, INTEGER_NUM=60, DECIMAL_NUM=61, IDENTIFIER=62, CLOS_TAG=63, 
		EXT_ANY=64;
	public static final int
		EXT_INSTR=1;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "EXT_INSTR"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"WS", "COMMENT1", "COMMENT2", "EOL", "AT", "OPEN_TAG", "IF", "ELSE", 
			"RETURN", "THROW", "EXIT", "VAR", "RUN", "HINT", "IMPORT", "TRUE", "FALSE", 
			"NULL", "AS", "PLUS", "MINUS", "MUL", "DIV", "DIV2", "MOD", "LBT", "RBT", 
			"AND", "OR", "NOT", "XOR", "LSHIFT", "RSHIFT", "RSHIFT2", "GT", "GE", 
			"LT", "LE", "EQ", "NE", "SC_OR", "SC_AND", "COMMA", "COLON", "ASS", "DOT", 
			"LSBT", "RSBT", "OCBR", "CCBR", "ROU", "QUE", "SEM", "CONVER", "LAMBDA", 
			"STRING", "TRANS", "UNICODE", "HEX", "HEX_NUM", "OCT_NUM", "BIT_NUM", 
			"INTEGER_NUM", "DECIMAL_NUM", "IDENTIFIER", "CLOS_TAG", "EXT_ANY"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, "'@@'", "'<%'", "'if'", "'else'", "'return'", 
			"'throw'", "'exit'", "'var'", "'run'", "'hint'", "'import'", "'true'", 
			"'false'", "'null'", "'as'", "'+'", "'-'", "'*'", "'/'", "'\\'", "'%'", 
			"'('", "')'", "'&'", "'|'", "'!'", "'^'", "'<<'", "'>>'", "'>>>'", "'>'", 
			"'>='", "'<'", "'<='", "'=='", "'!='", "'||'", "'&&'", "','", "':'", 
			"'='", "'.'", "'['", "']'", "'{'", "'}'", null, "'?'", "';'", "'=>'", 
			"'->'", null, null, null, null, null, null, null, "'%>'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "WS", "COMMENT1", "COMMENT2", "EOL", "AT", "OPEN_TAG", "IF", "ELSE", 
			"RETURN", "THROW", "EXIT", "VAR", "RUN", "HINT", "IMPORT", "TRUE", "FALSE", 
			"NULL", "AS", "PLUS", "MINUS", "MUL", "DIV", "DIV2", "MOD", "LBT", "RBT", 
			"AND", "OR", "NOT", "XOR", "LSHIFT", "RSHIFT", "RSHIFT2", "GT", "GE", 
			"LT", "LE", "EQ", "NE", "SC_OR", "SC_AND", "COMMA", "COLON", "ASS", "DOT", 
			"LSBT", "RSBT", "OCBR", "CCBR", "ROU", "QUE", "SEM", "CONVER", "LAMBDA", 
			"STRING", "HEX_NUM", "OCT_NUM", "BIT_NUM", "INTEGER_NUM", "DECIMAL_NUM", 
			"IDENTIFIER", "CLOS_TAG", "EXT_ANY"
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
	public String getGrammarFileName() { return "DataQLLexer.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2B\u01c0\b\1\b\1\4"+
		"\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n"+
		"\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t"+
		"=\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\3\2\6\2\u008c\n\2\r\2\16\2"+
		"\u008d\3\2\3\2\3\3\3\3\3\3\3\3\7\3\u0096\n\3\f\3\16\3\u0099\13\3\3\3\5"+
		"\3\u009c\n\3\3\3\3\3\3\4\3\4\3\4\3\4\7\4\u00a4\n\4\f\4\16\4\u00a7\13\4"+
		"\3\4\3\4\3\4\3\5\3\5\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\t\3"+
		"\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3"+
		"\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\17\3\17"+
		"\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21"+
		"\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\24\3\24"+
		"\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33"+
		"\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3!\3\"\3\"\3"+
		"\"\3#\3#\3#\3#\3$\3$\3%\3%\3%\3&\3&\3\'\3\'\3\'\3(\3(\3(\3)\3)\3)\3*\3"+
		"*\3*\3+\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3\60\3\61\3\61\3\62\3\62\3"+
		"\63\3\63\3\64\3\64\3\65\3\65\3\66\3\66\3\67\3\67\3\67\38\38\38\39\39\3"+
		"9\39\39\79\u0150\n9\f9\169\u0153\139\39\39\39\39\39\39\79\u015b\n9\f9"+
		"\169\u015e\139\39\59\u0161\n9\3:\3:\3:\5:\u0166\n:\3;\3;\3;\3;\3;\3;\3"+
		"<\3<\3=\3=\3=\6=\u0173\n=\r=\16=\u0174\3>\3>\3>\6>\u017a\n>\r>\16>\u017b"+
		"\3?\3?\3?\6?\u0181\n?\r?\16?\u0182\3@\5@\u0186\n@\3@\6@\u0189\n@\r@\16"+
		"@\u018a\3A\5A\u018e\nA\3A\7A\u0191\nA\fA\16A\u0194\13A\3A\3A\6A\u0198"+
		"\nA\rA\16A\u0199\3A\6A\u019d\nA\rA\16A\u019e\5A\u01a1\nA\3A\3A\5A\u01a5"+
		"\nA\3A\3A\7A\u01a9\nA\fA\16A\u01ac\13A\5A\u01ae\nA\3B\3B\7B\u01b2\nB\f"+
		"B\16B\u01b5\13B\3C\3C\3C\3C\3C\3D\6D\u01bd\nD\rD\16D\u01be\3\u00a5\2E"+
		"\4\3\6\4\b\5\n\6\f\7\16\b\20\t\22\n\24\13\26\f\30\r\32\16\34\17\36\20"+
		" \21\"\22$\23&\24(\25*\26,\27.\30\60\31\62\32\64\33\66\348\35:\36<\37"+
		"> @!B\"D#F$H%J&L\'N(P)R*T+V,X-Z.\\/^\60`\61b\62d\63f\64h\65j\66l\67n8"+
		"p9r:t\2v\2x\2z;|<~=\u0080>\u0082?\u0084@\u0086A\u0088B\4\2\3\26\5\2\13"+
		"\f\16\17\"\"\4\2\f\f\17\17\4\2\f\f\16\17\4\2%&BB\5\2\f\f\17\17$$\5\2\f"+
		"\f\17\17))\13\2$$))\61\61^^ddhhppttvv\5\2\62;CHch\4\2ZZzz\4\2QQqq\3\2"+
		"\629\4\2DDdd\3\2\62\63\3\2\62;\3\2\63;\4\2GGgg\4\2--//\5\2C\\aac|\6\2"+
		"\62;C\\aac|\6\2\'\'@@}}\177\177\2\u01d6\2\4\3\2\2\2\2\6\3\2\2\2\2\b\3"+
		"\2\2\2\2\n\3\2\2\2\2\f\3\2\2\2\2\16\3\2\2\2\2\20\3\2\2\2\2\22\3\2\2\2"+
		"\2\24\3\2\2\2\2\26\3\2\2\2\2\30\3\2\2\2\2\32\3\2\2\2\2\34\3\2\2\2\2\36"+
		"\3\2\2\2\2 \3\2\2\2\2\"\3\2\2\2\2$\3\2\2\2\2&\3\2\2\2\2(\3\2\2\2\2*\3"+
		"\2\2\2\2,\3\2\2\2\2.\3\2\2\2\2\60\3\2\2\2\2\62\3\2\2\2\2\64\3\2\2\2\2"+
		"\66\3\2\2\2\28\3\2\2\2\2:\3\2\2\2\2<\3\2\2\2\2>\3\2\2\2\2@\3\2\2\2\2B"+
		"\3\2\2\2\2D\3\2\2\2\2F\3\2\2\2\2H\3\2\2\2\2J\3\2\2\2\2L\3\2\2\2\2N\3\2"+
		"\2\2\2P\3\2\2\2\2R\3\2\2\2\2T\3\2\2\2\2V\3\2\2\2\2X\3\2\2\2\2Z\3\2\2\2"+
		"\2\\\3\2\2\2\2^\3\2\2\2\2`\3\2\2\2\2b\3\2\2\2\2d\3\2\2\2\2f\3\2\2\2\2"+
		"h\3\2\2\2\2j\3\2\2\2\2l\3\2\2\2\2n\3\2\2\2\2p\3\2\2\2\2r\3\2\2\2\2z\3"+
		"\2\2\2\2|\3\2\2\2\2~\3\2\2\2\2\u0080\3\2\2\2\2\u0082\3\2\2\2\2\u0084\3"+
		"\2\2\2\3\u0086\3\2\2\2\3\u0088\3\2\2\2\4\u008b\3\2\2\2\6\u0091\3\2\2\2"+
		"\b\u009f\3\2\2\2\n\u00ab\3\2\2\2\f\u00ad\3\2\2\2\16\u00b0\3\2\2\2\20\u00b5"+
		"\3\2\2\2\22\u00b8\3\2\2\2\24\u00bd\3\2\2\2\26\u00c4\3\2\2\2\30\u00ca\3"+
		"\2\2\2\32\u00cf\3\2\2\2\34\u00d3\3\2\2\2\36\u00d7\3\2\2\2 \u00dc\3\2\2"+
		"\2\"\u00e3\3\2\2\2$\u00e8\3\2\2\2&\u00ee\3\2\2\2(\u00f3\3\2\2\2*\u00f6"+
		"\3\2\2\2,\u00f8\3\2\2\2.\u00fa\3\2\2\2\60\u00fc\3\2\2\2\62\u00fe\3\2\2"+
		"\2\64\u0100\3\2\2\2\66\u0102\3\2\2\28\u0104\3\2\2\2:\u0106\3\2\2\2<\u0108"+
		"\3\2\2\2>\u010a\3\2\2\2@\u010c\3\2\2\2B\u010e\3\2\2\2D\u0111\3\2\2\2F"+
		"\u0114\3\2\2\2H\u0118\3\2\2\2J\u011a\3\2\2\2L\u011d\3\2\2\2N\u011f\3\2"+
		"\2\2P\u0122\3\2\2\2R\u0125\3\2\2\2T\u0128\3\2\2\2V\u012b\3\2\2\2X\u012e"+
		"\3\2\2\2Z\u0130\3\2\2\2\\\u0132\3\2\2\2^\u0134\3\2\2\2`\u0136\3\2\2\2"+
		"b\u0138\3\2\2\2d\u013a\3\2\2\2f\u013c\3\2\2\2h\u013e\3\2\2\2j\u0140\3"+
		"\2\2\2l\u0142\3\2\2\2n\u0144\3\2\2\2p\u0147\3\2\2\2r\u0160\3\2\2\2t\u0162"+
		"\3\2\2\2v\u0167\3\2\2\2x\u016d\3\2\2\2z\u016f\3\2\2\2|\u0176\3\2\2\2~"+
		"\u017d\3\2\2\2\u0080\u0185\3\2\2\2\u0082\u018d\3\2\2\2\u0084\u01af\3\2"+
		"\2\2\u0086\u01b6\3\2\2\2\u0088\u01bc\3\2\2\2\u008a\u008c\t\2\2\2\u008b"+
		"\u008a\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u008b\3\2\2\2\u008d\u008e\3\2"+
		"\2\2\u008e\u008f\3\2\2\2\u008f\u0090\b\2\2\2\u0090\5\3\2\2\2\u0091\u0092"+
		"\7\61\2\2\u0092\u0093\7\61\2\2\u0093\u0097\3\2\2\2\u0094\u0096\n\3\2\2"+
		"\u0095\u0094\3\2\2\2\u0096\u0099\3\2\2\2\u0097\u0095\3\2\2\2\u0097\u0098"+
		"\3\2\2\2\u0098\u009b\3\2\2\2\u0099\u0097\3\2\2\2\u009a\u009c\5\n\5\2\u009b"+
		"\u009a\3\2\2\2\u009b\u009c\3\2\2\2\u009c\u009d\3\2\2\2\u009d\u009e\b\3"+
		"\2\2\u009e\7\3\2\2\2\u009f\u00a0\7\61\2\2\u00a0\u00a1\7,\2\2\u00a1\u00a5"+
		"\3\2\2\2\u00a2\u00a4\13\2\2\2\u00a3\u00a2\3\2\2\2\u00a4\u00a7\3\2\2\2"+
		"\u00a5\u00a6\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a6\u00a8\3\2\2\2\u00a7\u00a5"+
		"\3\2\2\2\u00a8\u00a9\7,\2\2\u00a9\u00aa\7\61\2\2\u00aa\t\3\2\2\2\u00ab"+
		"\u00ac\t\4\2\2\u00ac\13\3\2\2\2\u00ad\u00ae\7B\2\2\u00ae\u00af\7B\2\2"+
		"\u00af\r\3\2\2\2\u00b0\u00b1\7>\2\2\u00b1\u00b2\7\'\2\2\u00b2\u00b3\3"+
		"\2\2\2\u00b3\u00b4\b\7\3\2\u00b4\17\3\2\2\2\u00b5\u00b6\7k\2\2\u00b6\u00b7"+
		"\7h\2\2\u00b7\21\3\2\2\2\u00b8\u00b9\7g\2\2\u00b9\u00ba\7n\2\2\u00ba\u00bb"+
		"\7u\2\2\u00bb\u00bc\7g\2\2\u00bc\23\3\2\2\2\u00bd\u00be\7t\2\2\u00be\u00bf"+
		"\7g\2\2\u00bf\u00c0\7v\2\2\u00c0\u00c1\7w\2\2\u00c1\u00c2\7t\2\2\u00c2"+
		"\u00c3\7p\2\2\u00c3\25\3\2\2\2\u00c4\u00c5\7v\2\2\u00c5\u00c6\7j\2\2\u00c6"+
		"\u00c7\7t\2\2\u00c7\u00c8\7q\2\2\u00c8\u00c9\7y\2\2\u00c9\27\3\2\2\2\u00ca"+
		"\u00cb\7g\2\2\u00cb\u00cc\7z\2\2\u00cc\u00cd\7k\2\2\u00cd\u00ce\7v\2\2"+
		"\u00ce\31\3\2\2\2\u00cf\u00d0\7x\2\2\u00d0\u00d1\7c\2\2\u00d1\u00d2\7"+
		"t\2\2\u00d2\33\3\2\2\2\u00d3\u00d4\7t\2\2\u00d4\u00d5\7w\2\2\u00d5\u00d6"+
		"\7p\2\2\u00d6\35\3\2\2\2\u00d7\u00d8\7j\2\2\u00d8\u00d9\7k\2\2\u00d9\u00da"+
		"\7p\2\2\u00da\u00db\7v\2\2\u00db\37\3\2\2\2\u00dc\u00dd\7k\2\2\u00dd\u00de"+
		"\7o\2\2\u00de\u00df\7r\2\2\u00df\u00e0\7q\2\2\u00e0\u00e1\7t\2\2\u00e1"+
		"\u00e2\7v\2\2\u00e2!\3\2\2\2\u00e3\u00e4\7v\2\2\u00e4\u00e5\7t\2\2\u00e5"+
		"\u00e6\7w\2\2\u00e6\u00e7\7g\2\2\u00e7#\3\2\2\2\u00e8\u00e9\7h\2\2\u00e9"+
		"\u00ea\7c\2\2\u00ea\u00eb\7n\2\2\u00eb\u00ec\7u\2\2\u00ec\u00ed\7g\2\2"+
		"\u00ed%\3\2\2\2\u00ee\u00ef\7p\2\2\u00ef\u00f0\7w\2\2\u00f0\u00f1\7n\2"+
		"\2\u00f1\u00f2\7n\2\2\u00f2\'\3\2\2\2\u00f3\u00f4\7c\2\2\u00f4\u00f5\7"+
		"u\2\2\u00f5)\3\2\2\2\u00f6\u00f7\7-\2\2\u00f7+\3\2\2\2\u00f8\u00f9\7/"+
		"\2\2\u00f9-\3\2\2\2\u00fa\u00fb\7,\2\2\u00fb/\3\2\2\2\u00fc\u00fd\7\61"+
		"\2\2\u00fd\61\3\2\2\2\u00fe\u00ff\7^\2\2\u00ff\63\3\2\2\2\u0100\u0101"+
		"\7\'\2\2\u0101\65\3\2\2\2\u0102\u0103\7*\2\2\u0103\67\3\2\2\2\u0104\u0105"+
		"\7+\2\2\u01059\3\2\2\2\u0106\u0107\7(\2\2\u0107;\3\2\2\2\u0108\u0109\7"+
		"~\2\2\u0109=\3\2\2\2\u010a\u010b\7#\2\2\u010b?\3\2\2\2\u010c\u010d\7`"+
		"\2\2\u010dA\3\2\2\2\u010e\u010f\7>\2\2\u010f\u0110\7>\2\2\u0110C\3\2\2"+
		"\2\u0111\u0112\7@\2\2\u0112\u0113\7@\2\2\u0113E\3\2\2\2\u0114\u0115\7"+
		"@\2\2\u0115\u0116\7@\2\2\u0116\u0117\7@\2\2\u0117G\3\2\2\2\u0118\u0119"+
		"\7@\2\2\u0119I\3\2\2\2\u011a\u011b\7@\2\2\u011b\u011c\7?\2\2\u011cK\3"+
		"\2\2\2\u011d\u011e\7>\2\2\u011eM\3\2\2\2\u011f\u0120\7>\2\2\u0120\u0121"+
		"\7?\2\2\u0121O\3\2\2\2\u0122\u0123\7?\2\2\u0123\u0124\7?\2\2\u0124Q\3"+
		"\2\2\2\u0125\u0126\7#\2\2\u0126\u0127\7?\2\2\u0127S\3\2\2\2\u0128\u0129"+
		"\7~\2\2\u0129\u012a\7~\2\2\u012aU\3\2\2\2\u012b\u012c\7(\2\2\u012c\u012d"+
		"\7(\2\2\u012dW\3\2\2\2\u012e\u012f\7.\2\2\u012fY\3\2\2\2\u0130\u0131\7"+
		"<\2\2\u0131[\3\2\2\2\u0132\u0133\7?\2\2\u0133]\3\2\2\2\u0134\u0135\7\60"+
		"\2\2\u0135_\3\2\2\2\u0136\u0137\7]\2\2\u0137a\3\2\2\2\u0138\u0139\7_\2"+
		"\2\u0139c\3\2\2\2\u013a\u013b\7}\2\2\u013be\3\2\2\2\u013c\u013d\7\177"+
		"\2\2\u013dg\3\2\2\2\u013e\u013f\t\5\2\2\u013fi\3\2\2\2\u0140\u0141\7A"+
		"\2\2\u0141k\3\2\2\2\u0142\u0143\7=\2\2\u0143m\3\2\2\2\u0144\u0145\7?\2"+
		"\2\u0145\u0146\7@\2\2\u0146o\3\2\2\2\u0147\u0148\7/\2\2\u0148\u0149\7"+
		"@\2\2\u0149q\3\2\2\2\u014a\u0151\7$\2\2\u014b\u0150\n\6\2\2\u014c\u014d"+
		"\7$\2\2\u014d\u0150\7$\2\2\u014e\u0150\5t:\2\u014f\u014b\3\2\2\2\u014f"+
		"\u014c\3\2\2\2\u014f\u014e\3\2\2\2\u0150\u0153\3\2\2\2\u0151\u014f\3\2"+
		"\2\2\u0151\u0152\3\2\2\2\u0152\u0154\3\2\2\2\u0153\u0151\3\2\2\2\u0154"+
		"\u0161\7$\2\2\u0155\u015c\7)\2\2\u0156\u015b\n\7\2\2\u0157\u0158\7)\2"+
		"\2\u0158\u015b\7)\2\2\u0159\u015b\5t:\2\u015a\u0156\3\2\2\2\u015a\u0157"+
		"\3\2\2\2\u015a\u0159\3\2\2\2\u015b\u015e\3\2\2\2\u015c\u015a\3\2\2\2\u015c"+
		"\u015d\3\2\2\2\u015d\u015f\3\2\2\2\u015e\u015c\3\2\2\2\u015f\u0161\7)"+
		"\2\2\u0160\u014a\3\2\2\2\u0160\u0155\3\2\2\2\u0161s\3\2\2\2\u0162\u0165"+
		"\7^\2\2\u0163\u0166\t\b\2\2\u0164\u0166\5v;\2\u0165\u0163\3\2\2\2\u0165"+
		"\u0164\3\2\2\2\u0166u\3\2\2\2\u0167\u0168\7w\2\2\u0168\u0169\5x<\2\u0169"+
		"\u016a\5x<\2\u016a\u016b\5x<\2\u016b\u016c\5x<\2\u016cw\3\2\2\2\u016d"+
		"\u016e\t\t\2\2\u016ey\3\2\2\2\u016f\u0170\7\62\2\2\u0170\u0172\t\n\2\2"+
		"\u0171\u0173\t\t\2\2\u0172\u0171\3\2\2\2\u0173\u0174\3\2\2\2\u0174\u0172"+
		"\3\2\2\2\u0174\u0175\3\2\2\2\u0175{\3\2\2\2\u0176\u0177\7\62\2\2\u0177"+
		"\u0179\t\13\2\2\u0178\u017a\t\f\2\2\u0179\u0178\3\2\2\2\u017a\u017b\3"+
		"\2\2\2\u017b\u0179\3\2\2\2\u017b\u017c\3\2\2\2\u017c}\3\2\2\2\u017d\u017e"+
		"\7\62\2\2\u017e\u0180\t\r\2\2\u017f\u0181\t\16\2\2\u0180\u017f\3\2\2\2"+
		"\u0181\u0182\3\2\2\2\u0182\u0180\3\2\2\2\u0182\u0183\3\2\2\2\u0183\177"+
		"\3\2\2\2\u0184\u0186\7/\2\2\u0185\u0184\3\2\2\2\u0185\u0186\3\2\2\2\u0186"+
		"\u0188\3\2\2\2\u0187\u0189\t\17\2\2\u0188\u0187\3\2\2\2\u0189\u018a\3"+
		"\2\2\2\u018a\u0188\3\2\2\2\u018a\u018b\3\2\2\2\u018b\u0081\3\2\2\2\u018c"+
		"\u018e\7/\2\2\u018d\u018c\3\2\2\2\u018d\u018e\3\2\2\2\u018e\u01a0\3\2"+
		"\2\2\u018f\u0191\t\17\2\2\u0190\u018f\3\2\2\2\u0191\u0194\3\2\2\2\u0192"+
		"\u0190\3\2\2\2\u0192\u0193\3\2\2\2\u0193\u0195\3\2\2\2\u0194\u0192\3\2"+
		"\2\2\u0195\u0197\7\60\2\2\u0196\u0198\t\17\2\2\u0197\u0196\3\2\2\2\u0198"+
		"\u0199\3\2\2\2\u0199\u0197\3\2\2\2\u0199\u019a\3\2\2\2\u019a\u01a1\3\2"+
		"\2\2\u019b\u019d\t\20\2\2\u019c\u019b\3\2\2\2\u019d\u019e\3\2\2\2\u019e"+
		"\u019c\3\2\2\2\u019e\u019f\3\2\2\2\u019f\u01a1\3\2\2\2\u01a0\u0192\3\2"+
		"\2\2\u01a0\u019c\3\2\2\2\u01a1\u01ad\3\2\2\2\u01a2\u01a4\t\21\2\2\u01a3"+
		"\u01a5\t\22\2\2\u01a4\u01a3\3\2\2\2\u01a4\u01a5\3\2\2\2\u01a5\u01a6\3"+
		"\2\2\2\u01a6\u01aa\t\20\2\2\u01a7\u01a9\t\17\2\2\u01a8\u01a7\3\2\2\2\u01a9"+
		"\u01ac\3\2\2\2\u01aa\u01a8\3\2\2\2\u01aa\u01ab\3\2\2\2\u01ab\u01ae\3\2"+
		"\2\2\u01ac\u01aa\3\2\2\2\u01ad\u01a2\3\2\2\2\u01ad\u01ae\3\2\2\2\u01ae"+
		"\u0083\3\2\2\2\u01af\u01b3\t\23\2\2\u01b0\u01b2\t\24\2\2\u01b1\u01b0\3"+
		"\2\2\2\u01b2\u01b5\3\2\2\2\u01b3\u01b1\3\2\2\2\u01b3\u01b4\3\2\2\2\u01b4"+
		"\u0085\3\2\2\2\u01b5\u01b3\3\2\2\2\u01b6\u01b7\7\'\2\2\u01b7\u01b8\7@"+
		"\2\2\u01b8\u01b9\3\2\2\2\u01b9\u01ba\bC\4\2\u01ba\u0087\3\2\2\2\u01bb"+
		"\u01bd\n\25\2\2\u01bc\u01bb\3\2\2\2\u01bd\u01be\3\2\2\2\u01be\u01bc\3"+
		"\2\2\2\u01be\u01bf\3\2\2\2\u01bf\u0089\3\2\2\2\35\2\3\u008d\u0097\u009b"+
		"\u00a5\u014f\u0151\u015a\u015c\u0160\u0165\u0174\u017b\u0182\u0185\u018a"+
		"\u018d\u0192\u0199\u019e\u01a0\u01a4\u01aa\u01ad\u01b3\u01be\5\b\2\2\7"+
		"\3\2\6\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}