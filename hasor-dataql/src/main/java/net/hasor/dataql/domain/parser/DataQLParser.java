// Generated from /Users/yongchun.zyc/Documents/Drive/projects/hasor/hasor.git/hasor-dataql/src/main/java/net/hasor/dataql/domain/parser/DataQL.g4 by ANTLR 4.7.2
package net.hasor.dataql.domain.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DataQLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, WS=6, COMMENT1=7, COMMENT2=8, 
		EOL=9, IF=10, ELSEIF=11, ELSE=12, END=13, RETURN=14, THROW=15, EXIT=16, 
		VAR=17, OPTION=18, IMPORT=19, TRUE=20, FALSE=21, NULL=22, AS=23, PLUS=24, 
		MINUS=25, MUL=26, DIV=27, DIV2=28, MOD=29, LBT=30, RBT=31, AND=32, OR=33, 
		NOT=34, XOR=35, LSHIFT=36, RSHIFT=37, RSHIFT2=38, GT=39, GE=40, LT=41, 
		LE=42, EQ=43, NE=44, SC_OR=45, SC_AND=46, COMMA=47, COLON=48, ASS=49, 
		DOT=50, LSBT=51, RSBT=52, OCBR=53, CCBR=54, ROU=55, STRING=56, DECIMAL_NUM=57, 
		INTEGER_NUM=58, HEX_NUM=59, OCT_NUM=60, BIT_NUM=61, IDENTIFIER=62;
	public static final int
		RULE_rootBlockSet = 0, RULE_optionBlock = 1, RULE_importBlock = 2, RULE_blockSet = 3, 
		RULE_blockItem = 4, RULE_varBlock = 5, RULE_ifBlock = 6, RULE_breakBlock = 7, 
		RULE_polymericObject = 8, RULE_lambdaDef = 9, RULE_objectValue = 10, RULE_objectValueItem = 11, 
		RULE_listValue = 12, RULE_functionCallConvertValue = 13, RULE_routeConvertValue = 14, 
		RULE_functionCall = 15, RULE_routeMapping = 16, RULE_routeMappingItem = 17, 
		RULE_subscriptMapping = 18, RULE_expr = 19, RULE_unaryExpr = 20, RULE_dyadicExpr = 21, 
		RULE_ternaryExpr = 22, RULE_atomExpr = 23, RULE_primitiveValue = 24;
	private static String[] makeRuleNames() {
		return new String[] {
			"rootBlockSet", "optionBlock", "importBlock", "blockSet", "blockItem", 
			"varBlock", "ifBlock", "breakBlock", "polymericObject", "lambdaDef", 
			"objectValue", "objectValueItem", "listValue", "functionCallConvertValue", 
			"routeConvertValue", "functionCall", "routeMapping", "routeMappingItem", 
			"subscriptMapping", "expr", "unaryExpr", "dyadicExpr", "ternaryExpr", 
			"atomExpr", "primitiveValue"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'@'", "';'", "'->'", "'=>'", "'?'", null, null, null, null, "'if'", 
			"'elseif'", "'else'", "'end'", "'return'", "'throw'", "'exit'", "'var'", 
			"'option'", "'import'", "'true'", "'false'", "'null'", "'as'", "'+'", 
			"'-'", "'*'", "'/'", "'\\'", "'%'", "'('", "')'", "'&'", "'|'", "'!'", 
			"'^'", "'<<'", "'>>'", "'>>>'", "'>'", "'>='", "'<'", "'<='", "'=='", 
			"'!='", "'||'", "'&&'", "','", "':'", "'='", "'.'", "'['", "']'", "'{'", 
			"'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, "WS", "COMMENT1", "COMMENT2", "EOL", 
			"IF", "ELSEIF", "ELSE", "END", "RETURN", "THROW", "EXIT", "VAR", "OPTION", 
			"IMPORT", "TRUE", "FALSE", "NULL", "AS", "PLUS", "MINUS", "MUL", "DIV", 
			"DIV2", "MOD", "LBT", "RBT", "AND", "OR", "NOT", "XOR", "LSHIFT", "RSHIFT", 
			"RSHIFT2", "GT", "GE", "LT", "LE", "EQ", "NE", "SC_OR", "SC_AND", "COMMA", 
			"COLON", "ASS", "DOT", "LSBT", "RSBT", "OCBR", "CCBR", "ROU", "STRING", 
			"DECIMAL_NUM", "INTEGER_NUM", "HEX_NUM", "OCT_NUM", "BIT_NUM", "IDENTIFIER"
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
	public String getGrammarFileName() { return "DataQL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public DataQLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class RootBlockSetContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(DataQLParser.EOF, 0); }
		public List<OptionBlockContext> optionBlock() {
			return getRuleContexts(OptionBlockContext.class);
		}
		public OptionBlockContext optionBlock(int i) {
			return getRuleContext(OptionBlockContext.class,i);
		}
		public List<ImportBlockContext> importBlock() {
			return getRuleContexts(ImportBlockContext.class);
		}
		public ImportBlockContext importBlock(int i) {
			return getRuleContext(ImportBlockContext.class,i);
		}
		public List<BlockSetContext> blockSet() {
			return getRuleContexts(BlockSetContext.class);
		}
		public BlockSetContext blockSet(int i) {
			return getRuleContext(BlockSetContext.class,i);
		}
		public RootBlockSetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rootBlockSet; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterRootBlockSet(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitRootBlockSet(this);
		}
	}

	public final RootBlockSetContext rootBlockSet() throws RecognitionException {
		RootBlockSetContext _localctx = new RootBlockSetContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_rootBlockSet);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OPTION) {
				{
				{
				setState(50);
				optionBlock();
				}
				}
				setState(55);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IMPORT) {
				{
				{
				setState(56);
				importBlock();
				}
				}
				setState(61);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(63); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(62);
				blockSet();
				}
				}
				setState(65); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IF) | (1L << RETURN) | (1L << THROW) | (1L << EXIT) | (1L << VAR) | (1L << OCBR))) != 0) );
			setState(67);
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

	public static class OptionBlockContext extends ParserRuleContext {
		public TerminalNode OPTION() { return getToken(DataQLParser.OPTION, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataQLParser.IDENTIFIER, 0); }
		public TerminalNode ASS() { return getToken(DataQLParser.ASS, 0); }
		public PrimitiveValueContext primitiveValue() {
			return getRuleContext(PrimitiveValueContext.class,0);
		}
		public OptionBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optionBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterOptionBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitOptionBlock(this);
		}
	}

	public final OptionBlockContext optionBlock() throws RecognitionException {
		OptionBlockContext _localctx = new OptionBlockContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_optionBlock);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69);
			match(OPTION);
			setState(70);
			match(IDENTIFIER);
			setState(71);
			match(ASS);
			setState(72);
			primitiveValue();
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

	public static class ImportBlockContext extends ParserRuleContext {
		public TerminalNode IMPORT() { return getToken(DataQLParser.IMPORT, 0); }
		public TerminalNode STRING() { return getToken(DataQLParser.STRING, 0); }
		public TerminalNode AS() { return getToken(DataQLParser.AS, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataQLParser.IDENTIFIER, 0); }
		public ImportBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterImportBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitImportBlock(this);
		}
	}

	public final ImportBlockContext importBlock() throws RecognitionException {
		ImportBlockContext _localctx = new ImportBlockContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_importBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			match(IMPORT);
			setState(76);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(75);
				match(T__0);
				}
			}

			setState(78);
			match(STRING);
			setState(79);
			match(AS);
			setState(80);
			match(IDENTIFIER);
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

	public static class BlockSetContext extends ParserRuleContext {
		public TerminalNode OCBR() { return getToken(DataQLParser.OCBR, 0); }
		public TerminalNode CCBR() { return getToken(DataQLParser.CCBR, 0); }
		public List<BlockItemContext> blockItem() {
			return getRuleContexts(BlockItemContext.class);
		}
		public BlockItemContext blockItem(int i) {
			return getRuleContext(BlockItemContext.class,i);
		}
		public BlockSetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blockSet; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterBlockSet(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitBlockSet(this);
		}
	}

	public final BlockSetContext blockSet() throws RecognitionException {
		BlockSetContext _localctx = new BlockSetContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_blockSet);
		int _la;
		try {
			setState(97);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OCBR:
				enterOuterAlt(_localctx, 1);
				{
				setState(82);
				match(OCBR);
				setState(87); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(83);
					blockItem();
					setState(85);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==T__1) {
						{
						setState(84);
						match(T__1);
						}
					}

					}
					}
					setState(89); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IF) | (1L << RETURN) | (1L << THROW) | (1L << EXIT) | (1L << VAR))) != 0) );
				setState(91);
				match(CCBR);
				}
				break;
			case IF:
			case RETURN:
			case THROW:
			case EXIT:
			case VAR:
				enterOuterAlt(_localctx, 2);
				{
				setState(93);
				blockItem();
				setState(95);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
				case 1:
					{
					setState(94);
					match(T__1);
					}
					break;
				}
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

	public static class BlockItemContext extends ParserRuleContext {
		public VarBlockContext varBlock() {
			return getRuleContext(VarBlockContext.class,0);
		}
		public IfBlockContext ifBlock() {
			return getRuleContext(IfBlockContext.class,0);
		}
		public BreakBlockContext breakBlock() {
			return getRuleContext(BreakBlockContext.class,0);
		}
		public BlockItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blockItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterBlockItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitBlockItem(this);
		}
	}

	public final BlockItemContext blockItem() throws RecognitionException {
		BlockItemContext _localctx = new BlockItemContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_blockItem);
		try {
			setState(102);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VAR:
				enterOuterAlt(_localctx, 1);
				{
				setState(99);
				varBlock();
				}
				break;
			case IF:
				enterOuterAlt(_localctx, 2);
				{
				setState(100);
				ifBlock();
				}
				break;
			case RETURN:
			case THROW:
			case EXIT:
				enterOuterAlt(_localctx, 3);
				{
				setState(101);
				breakBlock();
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

	public static class VarBlockContext extends ParserRuleContext {
		public TerminalNode VAR() { return getToken(DataQLParser.VAR, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataQLParser.IDENTIFIER, 0); }
		public TerminalNode ASS() { return getToken(DataQLParser.ASS, 0); }
		public PolymericObjectContext polymericObject() {
			return getRuleContext(PolymericObjectContext.class,0);
		}
		public LambdaDefContext lambdaDef() {
			return getRuleContext(LambdaDefContext.class,0);
		}
		public VarBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterVarBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitVarBlock(this);
		}
	}

	public final VarBlockContext varBlock() throws RecognitionException {
		VarBlockContext _localctx = new VarBlockContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_varBlock);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(104);
			match(VAR);
			setState(105);
			match(IDENTIFIER);
			setState(106);
			match(ASS);
			setState(109);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(107);
				polymericObject();
				}
				break;
			case 2:
				{
				setState(108);
				lambdaDef();
				}
				break;
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

	public static class IfBlockContext extends ParserRuleContext {
		public TerminalNode IF() { return getToken(DataQLParser.IF, 0); }
		public List<TerminalNode> LBT() { return getTokens(DataQLParser.LBT); }
		public TerminalNode LBT(int i) {
			return getToken(DataQLParser.LBT, i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> RBT() { return getTokens(DataQLParser.RBT); }
		public TerminalNode RBT(int i) {
			return getToken(DataQLParser.RBT, i);
		}
		public List<BlockSetContext> blockSet() {
			return getRuleContexts(BlockSetContext.class);
		}
		public BlockSetContext blockSet(int i) {
			return getRuleContext(BlockSetContext.class,i);
		}
		public TerminalNode END() { return getToken(DataQLParser.END, 0); }
		public List<TerminalNode> ELSEIF() { return getTokens(DataQLParser.ELSEIF); }
		public TerminalNode ELSEIF(int i) {
			return getToken(DataQLParser.ELSEIF, i);
		}
		public TerminalNode ELSE() { return getToken(DataQLParser.ELSE, 0); }
		public IfBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterIfBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitIfBlock(this);
		}
	}

	public final IfBlockContext ifBlock() throws RecognitionException {
		IfBlockContext _localctx = new IfBlockContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_ifBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(111);
			match(IF);
			setState(112);
			match(LBT);
			setState(113);
			expr();
			setState(114);
			match(RBT);
			setState(115);
			blockSet();
			setState(124);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ELSEIF) {
				{
				{
				setState(116);
				match(ELSEIF);
				setState(117);
				match(LBT);
				setState(118);
				expr();
				setState(119);
				match(RBT);
				setState(120);
				blockSet();
				}
				}
				setState(126);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(129);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(127);
				match(ELSE);
				setState(128);
				blockSet();
				}
			}

			setState(131);
			match(END);
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

	public static class BreakBlockContext extends ParserRuleContext {
		public TerminalNode RETURN() { return getToken(DataQLParser.RETURN, 0); }
		public TerminalNode THROW() { return getToken(DataQLParser.THROW, 0); }
		public TerminalNode EXIT() { return getToken(DataQLParser.EXIT, 0); }
		public PolymericObjectContext polymericObject() {
			return getRuleContext(PolymericObjectContext.class,0);
		}
		public LambdaDefContext lambdaDef() {
			return getRuleContext(LambdaDefContext.class,0);
		}
		public TerminalNode INTEGER_NUM() { return getToken(DataQLParser.INTEGER_NUM, 0); }
		public TerminalNode COMMA() { return getToken(DataQLParser.COMMA, 0); }
		public BreakBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_breakBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterBreakBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitBreakBlock(this);
		}
	}

	public final BreakBlockContext breakBlock() throws RecognitionException {
		BreakBlockContext _localctx = new BreakBlockContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_breakBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(133);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RETURN) | (1L << THROW) | (1L << EXIT))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(136);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				setState(134);
				match(INTEGER_NUM);
				setState(135);
				match(COMMA);
				}
				break;
			}
			setState(140);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(138);
				polymericObject();
				}
				break;
			case 2:
				{
				setState(139);
				lambdaDef();
				}
				break;
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

	public static class PolymericObjectContext extends ParserRuleContext {
		public FunctionCallConvertValueContext functionCallConvertValue() {
			return getRuleContext(FunctionCallConvertValueContext.class,0);
		}
		public RouteConvertValueContext routeConvertValue() {
			return getRuleContext(RouteConvertValueContext.class,0);
		}
		public ObjectValueContext objectValue() {
			return getRuleContext(ObjectValueContext.class,0);
		}
		public ListValueContext listValue() {
			return getRuleContext(ListValueContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public PrimitiveValueContext primitiveValue() {
			return getRuleContext(PrimitiveValueContext.class,0);
		}
		public PolymericObjectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_polymericObject; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterPolymericObject(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitPolymericObject(this);
		}
	}

	public final PolymericObjectContext polymericObject() throws RecognitionException {
		PolymericObjectContext _localctx = new PolymericObjectContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_polymericObject);
		try {
			setState(148);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(142);
				functionCallConvertValue();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(143);
				routeConvertValue();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(144);
				objectValue();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(145);
				listValue();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(146);
				expr();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(147);
				primitiveValue();
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

	public static class LambdaDefContext extends ParserRuleContext {
		public TerminalNode LBT() { return getToken(DataQLParser.LBT, 0); }
		public TerminalNode RBT() { return getToken(DataQLParser.RBT, 0); }
		public BlockSetContext blockSet() {
			return getRuleContext(BlockSetContext.class,0);
		}
		public List<TerminalNode> IDENTIFIER() { return getTokens(DataQLParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(DataQLParser.IDENTIFIER, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(DataQLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DataQLParser.COMMA, i);
		}
		public LambdaDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lambdaDef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterLambdaDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitLambdaDef(this);
		}
	}

	public final LambdaDefContext lambdaDef() throws RecognitionException {
		LambdaDefContext _localctx = new LambdaDefContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_lambdaDef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			match(LBT);
			setState(159);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(151);
				match(IDENTIFIER);
				setState(156);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(152);
					match(COMMA);
					setState(153);
					match(IDENTIFIER);
					}
					}
					setState(158);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(161);
			match(RBT);
			setState(162);
			match(T__2);
			setState(163);
			blockSet();
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

	public static class ObjectValueContext extends ParserRuleContext {
		public TerminalNode OCBR() { return getToken(DataQLParser.OCBR, 0); }
		public TerminalNode CCBR() { return getToken(DataQLParser.CCBR, 0); }
		public List<ObjectValueItemContext> objectValueItem() {
			return getRuleContexts(ObjectValueItemContext.class);
		}
		public ObjectValueItemContext objectValueItem(int i) {
			return getRuleContext(ObjectValueItemContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(DataQLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DataQLParser.COMMA, i);
		}
		public ObjectValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objectValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterObjectValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitObjectValue(this);
		}
	}

	public final ObjectValueContext objectValue() throws RecognitionException {
		ObjectValueContext _localctx = new ObjectValueContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_objectValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(165);
			match(OCBR);
			setState(167);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING) {
				{
				setState(166);
				objectValueItem();
				}
			}

			setState(173);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(169);
				match(COMMA);
				setState(170);
				objectValueItem();
				}
				}
				setState(175);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(176);
			match(CCBR);
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

	public static class ObjectValueItemContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(DataQLParser.STRING, 0); }
		public TerminalNode COLON() { return getToken(DataQLParser.COLON, 0); }
		public PolymericObjectContext polymericObject() {
			return getRuleContext(PolymericObjectContext.class,0);
		}
		public ObjectValueItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objectValueItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterObjectValueItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitObjectValueItem(this);
		}
	}

	public final ObjectValueItemContext objectValueItem() throws RecognitionException {
		ObjectValueItemContext _localctx = new ObjectValueItemContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_objectValueItem);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
			match(STRING);
			setState(181);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(179);
				match(COLON);
				setState(180);
				polymericObject();
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

	public static class ListValueContext extends ParserRuleContext {
		public TerminalNode LSBT() { return getToken(DataQLParser.LSBT, 0); }
		public TerminalNode RSBT() { return getToken(DataQLParser.RSBT, 0); }
		public List<PolymericObjectContext> polymericObject() {
			return getRuleContexts(PolymericObjectContext.class);
		}
		public PolymericObjectContext polymericObject(int i) {
			return getRuleContext(PolymericObjectContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(DataQLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DataQLParser.COMMA, i);
		}
		public ListValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterListValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitListValue(this);
		}
	}

	public final ListValueContext listValue() throws RecognitionException {
		ListValueContext _localctx = new ListValueContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_listValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(183);
			match(LSBT);
			setState(185);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << PLUS) | (1L << MINUS) | (1L << LBT) | (1L << NOT) | (1L << LSBT) | (1L << OCBR) | (1L << ROU) | (1L << STRING) | (1L << DECIMAL_NUM) | (1L << INTEGER_NUM) | (1L << HEX_NUM) | (1L << OCT_NUM) | (1L << BIT_NUM) | (1L << IDENTIFIER))) != 0)) {
				{
				setState(184);
				polymericObject();
				}
			}

			setState(191);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(187);
				match(COMMA);
				setState(188);
				polymericObject();
				}
				}
				setState(193);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(194);
			match(RSBT);
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

	public static class FunctionCallConvertValueContext extends ParserRuleContext {
		public FunctionCallContext functionCall() {
			return getRuleContext(FunctionCallContext.class,0);
		}
		public ObjectValueContext objectValue() {
			return getRuleContext(ObjectValueContext.class,0);
		}
		public ListValueContext listValue() {
			return getRuleContext(ListValueContext.class,0);
		}
		public FunctionCallConvertValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionCallConvertValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterFunctionCallConvertValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitFunctionCallConvertValue(this);
		}
	}

	public final FunctionCallConvertValueContext functionCallConvertValue() throws RecognitionException {
		FunctionCallConvertValueContext _localctx = new FunctionCallConvertValueContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_functionCallConvertValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(196);
			functionCall();
			setState(197);
			match(T__3);
			setState(200);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OCBR:
				{
				setState(198);
				objectValue();
				}
				break;
			case LSBT:
				{
				setState(199);
				listValue();
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class RouteConvertValueContext extends ParserRuleContext {
		public RouteMappingContext routeMapping() {
			return getRuleContext(RouteMappingContext.class,0);
		}
		public ObjectValueContext objectValue() {
			return getRuleContext(ObjectValueContext.class,0);
		}
		public ListValueContext listValue() {
			return getRuleContext(ListValueContext.class,0);
		}
		public RouteConvertValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_routeConvertValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterRouteConvertValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitRouteConvertValue(this);
		}
	}

	public final RouteConvertValueContext routeConvertValue() throws RecognitionException {
		RouteConvertValueContext _localctx = new RouteConvertValueContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_routeConvertValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(202);
			routeMapping();
			setState(203);
			match(T__3);
			setState(206);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OCBR:
				{
				setState(204);
				objectValue();
				}
				break;
			case LSBT:
				{
				setState(205);
				listValue();
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class FunctionCallContext extends ParserRuleContext {
		public List<RouteMappingContext> routeMapping() {
			return getRuleContexts(RouteMappingContext.class);
		}
		public RouteMappingContext routeMapping(int i) {
			return getRuleContext(RouteMappingContext.class,i);
		}
		public TerminalNode LBT() { return getToken(DataQLParser.LBT, 0); }
		public TerminalNode RBT() { return getToken(DataQLParser.RBT, 0); }
		public List<PolymericObjectContext> polymericObject() {
			return getRuleContexts(PolymericObjectContext.class);
		}
		public PolymericObjectContext polymericObject(int i) {
			return getRuleContext(PolymericObjectContext.class,i);
		}
		public SubscriptMappingContext subscriptMapping() {
			return getRuleContext(SubscriptMappingContext.class,0);
		}
		public TerminalNode DOT() { return getToken(DataQLParser.DOT, 0); }
		public List<TerminalNode> COMMA() { return getTokens(DataQLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DataQLParser.COMMA, i);
		}
		public FunctionCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionCall; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterFunctionCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitFunctionCall(this);
		}
	}

	public final FunctionCallContext functionCall() throws RecognitionException {
		FunctionCallContext _localctx = new FunctionCallContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_functionCall);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(208);
			routeMapping();
			setState(209);
			match(LBT);
			setState(218);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << PLUS) | (1L << MINUS) | (1L << LBT) | (1L << NOT) | (1L << LSBT) | (1L << OCBR) | (1L << ROU) | (1L << STRING) | (1L << DECIMAL_NUM) | (1L << INTEGER_NUM) | (1L << HEX_NUM) | (1L << OCT_NUM) | (1L << BIT_NUM) | (1L << IDENTIFIER))) != 0)) {
				{
				setState(210);
				polymericObject();
				setState(215);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(211);
					match(COMMA);
					setState(212);
					polymericObject();
					}
					}
					setState(217);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(220);
			match(RBT);
			setState(224);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DOT:
				{
				{
				setState(221);
				match(DOT);
				setState(222);
				routeMapping();
				}
				}
				break;
			case LSBT:
				{
				setState(223);
				subscriptMapping();
				}
				break;
			case EOF:
			case T__1:
			case T__3:
			case T__4:
			case IF:
			case ELSEIF:
			case ELSE:
			case END:
			case RETURN:
			case THROW:
			case EXIT:
			case VAR:
			case PLUS:
			case MINUS:
			case MUL:
			case DIV:
			case DIV2:
			case MOD:
			case LBT:
			case RBT:
			case AND:
			case OR:
			case NOT:
			case XOR:
			case LSHIFT:
			case RSHIFT:
			case RSHIFT2:
			case GT:
			case GE:
			case LT:
			case LE:
			case EQ:
			case NE:
			case SC_OR:
			case SC_AND:
			case COMMA:
			case COLON:
			case RSBT:
			case OCBR:
			case CCBR:
				break;
			default:
				break;
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

	public static class RouteMappingContext extends ParserRuleContext {
		public TerminalNode ROU() { return getToken(DataQLParser.ROU, 0); }
		public TerminalNode OCBR() { return getToken(DataQLParser.OCBR, 0); }
		public List<RouteMappingItemContext> routeMappingItem() {
			return getRuleContexts(RouteMappingItemContext.class);
		}
		public RouteMappingItemContext routeMappingItem(int i) {
			return getRuleContext(RouteMappingItemContext.class,i);
		}
		public TerminalNode CCBR() { return getToken(DataQLParser.CCBR, 0); }
		public List<TerminalNode> DOT() { return getTokens(DataQLParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(DataQLParser.DOT, i);
		}
		public RouteMappingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_routeMapping; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterRouteMapping(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitRouteMapping(this);
		}
	}

	public final RouteMappingContext routeMapping() throws RecognitionException {
		RouteMappingContext _localctx = new RouteMappingContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_routeMapping);
		int _la;
		try {
			setState(246);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ROU:
				enterOuterAlt(_localctx, 1);
				{
				setState(226);
				match(ROU);
				setState(227);
				match(OCBR);
				setState(228);
				routeMappingItem();
				setState(233);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==DOT) {
					{
					{
					setState(229);
					match(DOT);
					setState(230);
					routeMappingItem();
					}
					}
					setState(235);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(236);
				match(CCBR);
				}
				break;
			case IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(238);
				routeMappingItem();
				setState(243);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==DOT) {
					{
					{
					setState(239);
					match(DOT);
					setState(240);
					routeMappingItem();
					}
					}
					setState(245);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
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

	public static class RouteMappingItemContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(DataQLParser.IDENTIFIER, 0); }
		public List<SubscriptMappingContext> subscriptMapping() {
			return getRuleContexts(SubscriptMappingContext.class);
		}
		public SubscriptMappingContext subscriptMapping(int i) {
			return getRuleContext(SubscriptMappingContext.class,i);
		}
		public RouteMappingItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_routeMappingItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterRouteMappingItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitRouteMappingItem(this);
		}
	}

	public final RouteMappingItemContext routeMappingItem() throws RecognitionException {
		RouteMappingItemContext _localctx = new RouteMappingItemContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_routeMappingItem);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(248);
			match(IDENTIFIER);
			setState(252);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LSBT) {
				{
				{
				setState(249);
				subscriptMapping();
				}
				}
				setState(254);
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

	public static class SubscriptMappingContext extends ParserRuleContext {
		public TerminalNode LSBT() { return getToken(DataQLParser.LSBT, 0); }
		public TerminalNode RSBT() { return getToken(DataQLParser.RSBT, 0); }
		public TerminalNode STRING() { return getToken(DataQLParser.STRING, 0); }
		public TerminalNode INTEGER_NUM() { return getToken(DataQLParser.INTEGER_NUM, 0); }
		public SubscriptMappingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subscriptMapping; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterSubscriptMapping(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitSubscriptMapping(this);
		}
	}

	public final SubscriptMappingContext subscriptMapping() throws RecognitionException {
		SubscriptMappingContext _localctx = new SubscriptMappingContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_subscriptMapping);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(255);
			match(LSBT);
			setState(256);
			_la = _input.LA(1);
			if ( !(_la==STRING || _la==INTEGER_NUM) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(257);
			match(RSBT);
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

	public static class ExprContext extends ParserRuleContext {
		public TerminalNode LBT() { return getToken(DataQLParser.LBT, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode RBT() { return getToken(DataQLParser.RBT, 0); }
		public DyadicExprContext dyadicExpr() {
			return getRuleContext(DyadicExprContext.class,0);
		}
		public TernaryExprContext ternaryExpr() {
			return getRuleContext(TernaryExprContext.class,0);
		}
		public UnaryExprContext unaryExpr() {
			return getRuleContext(UnaryExprContext.class,0);
		}
		public AtomExprContext atomExpr() {
			return getRuleContext(AtomExprContext.class,0);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitExpr(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_expr);
		try {
			setState(272);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LBT:
				enterOuterAlt(_localctx, 1);
				{
				setState(259);
				match(LBT);
				setState(260);
				expr();
				setState(261);
				match(RBT);
				setState(264);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
				case 1:
					{
					setState(262);
					dyadicExpr();
					}
					break;
				case 2:
					{
					setState(263);
					ternaryExpr();
					}
					break;
				}
				}
				break;
			case PLUS:
			case MINUS:
			case NOT:
				enterOuterAlt(_localctx, 2);
				{
				setState(266);
				unaryExpr();
				}
				break;
			case TRUE:
			case FALSE:
			case NULL:
			case ROU:
			case STRING:
			case DECIMAL_NUM:
			case INTEGER_NUM:
			case HEX_NUM:
			case OCT_NUM:
			case BIT_NUM:
			case IDENTIFIER:
				enterOuterAlt(_localctx, 3);
				{
				setState(267);
				atomExpr();
				setState(270);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
				case 1:
					{
					setState(268);
					dyadicExpr();
					}
					break;
				case 2:
					{
					setState(269);
					ternaryExpr();
					}
					break;
				}
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

	public static class UnaryExprContext extends ParserRuleContext {
		public AtomExprContext atomExpr() {
			return getRuleContext(AtomExprContext.class,0);
		}
		public TerminalNode PLUS() { return getToken(DataQLParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(DataQLParser.MINUS, 0); }
		public TerminalNode NOT() { return getToken(DataQLParser.NOT, 0); }
		public UnaryExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterUnaryExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitUnaryExpr(this);
		}
	}

	public final UnaryExprContext unaryExpr() throws RecognitionException {
		UnaryExprContext _localctx = new UnaryExprContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_unaryExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(274);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << NOT))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(275);
			atomExpr();
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

	public static class DyadicExprContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode PLUS() { return getToken(DataQLParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(DataQLParser.MINUS, 0); }
		public TerminalNode MUL() { return getToken(DataQLParser.MUL, 0); }
		public TerminalNode DIV() { return getToken(DataQLParser.DIV, 0); }
		public TerminalNode DIV2() { return getToken(DataQLParser.DIV2, 0); }
		public TerminalNode MOD() { return getToken(DataQLParser.MOD, 0); }
		public TerminalNode LBT() { return getToken(DataQLParser.LBT, 0); }
		public TerminalNode RBT() { return getToken(DataQLParser.RBT, 0); }
		public TerminalNode AND() { return getToken(DataQLParser.AND, 0); }
		public TerminalNode OR() { return getToken(DataQLParser.OR, 0); }
		public TerminalNode NOT() { return getToken(DataQLParser.NOT, 0); }
		public TerminalNode XOR() { return getToken(DataQLParser.XOR, 0); }
		public TerminalNode LSHIFT() { return getToken(DataQLParser.LSHIFT, 0); }
		public TerminalNode RSHIFT() { return getToken(DataQLParser.RSHIFT, 0); }
		public TerminalNode RSHIFT2() { return getToken(DataQLParser.RSHIFT2, 0); }
		public TerminalNode GT() { return getToken(DataQLParser.GT, 0); }
		public TerminalNode GE() { return getToken(DataQLParser.GE, 0); }
		public TerminalNode LT() { return getToken(DataQLParser.LT, 0); }
		public TerminalNode LE() { return getToken(DataQLParser.LE, 0); }
		public TerminalNode EQ() { return getToken(DataQLParser.EQ, 0); }
		public TerminalNode NE() { return getToken(DataQLParser.NE, 0); }
		public TerminalNode SC_OR() { return getToken(DataQLParser.SC_OR, 0); }
		public TerminalNode SC_AND() { return getToken(DataQLParser.SC_AND, 0); }
		public DyadicExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dyadicExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterDyadicExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitDyadicExpr(this);
		}
	}

	public final DyadicExprContext dyadicExpr() throws RecognitionException {
		DyadicExprContext _localctx = new DyadicExprContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_dyadicExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(277);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << MUL) | (1L << DIV) | (1L << DIV2) | (1L << MOD) | (1L << LBT) | (1L << RBT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << XOR) | (1L << LSHIFT) | (1L << RSHIFT) | (1L << RSHIFT2) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << SC_OR) | (1L << SC_AND))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(278);
			expr();
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

	public static class TernaryExprContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode COLON() { return getToken(DataQLParser.COLON, 0); }
		public TernaryExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ternaryExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterTernaryExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitTernaryExpr(this);
		}
	}

	public final TernaryExprContext ternaryExpr() throws RecognitionException {
		TernaryExprContext _localctx = new TernaryExprContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_ternaryExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(280);
			match(T__4);
			setState(281);
			expr();
			setState(282);
			match(COLON);
			setState(283);
			expr();
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

	public static class AtomExprContext extends ParserRuleContext {
		public PrimitiveValueContext primitiveValue() {
			return getRuleContext(PrimitiveValueContext.class,0);
		}
		public FunctionCallContext functionCall() {
			return getRuleContext(FunctionCallContext.class,0);
		}
		public RouteMappingContext routeMapping() {
			return getRuleContext(RouteMappingContext.class,0);
		}
		public AtomExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atomExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterAtomExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitAtomExpr(this);
		}
	}

	public final AtomExprContext atomExpr() throws RecognitionException {
		AtomExprContext _localctx = new AtomExprContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_atomExpr);
		try {
			setState(288);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(285);
				primitiveValue();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(286);
				functionCall();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(287);
				routeMapping();
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

	public static class PrimitiveValueContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(DataQLParser.STRING, 0); }
		public TerminalNode NULL() { return getToken(DataQLParser.NULL, 0); }
		public TerminalNode TRUE() { return getToken(DataQLParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(DataQLParser.FALSE, 0); }
		public TerminalNode DECIMAL_NUM() { return getToken(DataQLParser.DECIMAL_NUM, 0); }
		public TerminalNode INTEGER_NUM() { return getToken(DataQLParser.INTEGER_NUM, 0); }
		public TerminalNode HEX_NUM() { return getToken(DataQLParser.HEX_NUM, 0); }
		public TerminalNode OCT_NUM() { return getToken(DataQLParser.OCT_NUM, 0); }
		public TerminalNode BIT_NUM() { return getToken(DataQLParser.BIT_NUM, 0); }
		public PrimitiveValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primitiveValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).enterPrimitiveValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DataQLListener ) ((DataQLListener)listener).exitPrimitiveValue(this);
		}
	}

	public final PrimitiveValueContext primitiveValue() throws RecognitionException {
		PrimitiveValueContext _localctx = new PrimitiveValueContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_primitiveValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(290);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << STRING) | (1L << DECIMAL_NUM) | (1L << INTEGER_NUM) | (1L << HEX_NUM) | (1L << OCT_NUM) | (1L << BIT_NUM))) != 0)) ) {
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3@\u0127\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\3\2\7\2\66\n\2\f\2\16\29\13\2\3\2\7\2<\n\2\f\2\16\2?\13\2\3"+
		"\2\6\2B\n\2\r\2\16\2C\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\4\3\4\5\4O\n\4\3\4"+
		"\3\4\3\4\3\4\3\5\3\5\3\5\5\5X\n\5\6\5Z\n\5\r\5\16\5[\3\5\3\5\3\5\3\5\5"+
		"\5b\n\5\5\5d\n\5\3\6\3\6\3\6\5\6i\n\6\3\7\3\7\3\7\3\7\3\7\5\7p\n\7\3\b"+
		"\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\7\b}\n\b\f\b\16\b\u0080\13\b"+
		"\3\b\3\b\5\b\u0084\n\b\3\b\3\b\3\t\3\t\3\t\5\t\u008b\n\t\3\t\3\t\5\t\u008f"+
		"\n\t\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u0097\n\n\3\13\3\13\3\13\3\13\7\13\u009d"+
		"\n\13\f\13\16\13\u00a0\13\13\5\13\u00a2\n\13\3\13\3\13\3\13\3\13\3\f\3"+
		"\f\5\f\u00aa\n\f\3\f\3\f\7\f\u00ae\n\f\f\f\16\f\u00b1\13\f\3\f\3\f\3\r"+
		"\3\r\3\r\5\r\u00b8\n\r\3\16\3\16\5\16\u00bc\n\16\3\16\3\16\7\16\u00c0"+
		"\n\16\f\16\16\16\u00c3\13\16\3\16\3\16\3\17\3\17\3\17\3\17\5\17\u00cb"+
		"\n\17\3\20\3\20\3\20\3\20\5\20\u00d1\n\20\3\21\3\21\3\21\3\21\3\21\7\21"+
		"\u00d8\n\21\f\21\16\21\u00db\13\21\5\21\u00dd\n\21\3\21\3\21\3\21\3\21"+
		"\5\21\u00e3\n\21\3\22\3\22\3\22\3\22\3\22\7\22\u00ea\n\22\f\22\16\22\u00ed"+
		"\13\22\3\22\3\22\3\22\3\22\3\22\7\22\u00f4\n\22\f\22\16\22\u00f7\13\22"+
		"\5\22\u00f9\n\22\3\23\3\23\7\23\u00fd\n\23\f\23\16\23\u0100\13\23\3\24"+
		"\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\5\25\u010b\n\25\3\25\3\25\3\25"+
		"\3\25\5\25\u0111\n\25\5\25\u0113\n\25\3\26\3\26\3\26\3\27\3\27\3\27\3"+
		"\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\5\31\u0123\n\31\3\32\3\32\3\32"+
		"\2\2\33\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\2\7\3\2"+
		"\20\22\4\2::<<\4\2\32\33$$\3\2\32\60\4\2\26\30:?\2\u013a\2\67\3\2\2\2"+
		"\4G\3\2\2\2\6L\3\2\2\2\bc\3\2\2\2\nh\3\2\2\2\fj\3\2\2\2\16q\3\2\2\2\20"+
		"\u0087\3\2\2\2\22\u0096\3\2\2\2\24\u0098\3\2\2\2\26\u00a7\3\2\2\2\30\u00b4"+
		"\3\2\2\2\32\u00b9\3\2\2\2\34\u00c6\3\2\2\2\36\u00cc\3\2\2\2 \u00d2\3\2"+
		"\2\2\"\u00f8\3\2\2\2$\u00fa\3\2\2\2&\u0101\3\2\2\2(\u0112\3\2\2\2*\u0114"+
		"\3\2\2\2,\u0117\3\2\2\2.\u011a\3\2\2\2\60\u0122\3\2\2\2\62\u0124\3\2\2"+
		"\2\64\66\5\4\3\2\65\64\3\2\2\2\669\3\2\2\2\67\65\3\2\2\2\678\3\2\2\28"+
		"=\3\2\2\29\67\3\2\2\2:<\5\6\4\2;:\3\2\2\2<?\3\2\2\2=;\3\2\2\2=>\3\2\2"+
		"\2>A\3\2\2\2?=\3\2\2\2@B\5\b\5\2A@\3\2\2\2BC\3\2\2\2CA\3\2\2\2CD\3\2\2"+
		"\2DE\3\2\2\2EF\7\2\2\3F\3\3\2\2\2GH\7\24\2\2HI\7@\2\2IJ\7\63\2\2JK\5\62"+
		"\32\2K\5\3\2\2\2LN\7\25\2\2MO\7\3\2\2NM\3\2\2\2NO\3\2\2\2OP\3\2\2\2PQ"+
		"\7:\2\2QR\7\31\2\2RS\7@\2\2S\7\3\2\2\2TY\7\67\2\2UW\5\n\6\2VX\7\4\2\2"+
		"WV\3\2\2\2WX\3\2\2\2XZ\3\2\2\2YU\3\2\2\2Z[\3\2\2\2[Y\3\2\2\2[\\\3\2\2"+
		"\2\\]\3\2\2\2]^\78\2\2^d\3\2\2\2_a\5\n\6\2`b\7\4\2\2a`\3\2\2\2ab\3\2\2"+
		"\2bd\3\2\2\2cT\3\2\2\2c_\3\2\2\2d\t\3\2\2\2ei\5\f\7\2fi\5\16\b\2gi\5\20"+
		"\t\2he\3\2\2\2hf\3\2\2\2hg\3\2\2\2i\13\3\2\2\2jk\7\23\2\2kl\7@\2\2lo\7"+
		"\63\2\2mp\5\22\n\2np\5\24\13\2om\3\2\2\2on\3\2\2\2p\r\3\2\2\2qr\7\f\2"+
		"\2rs\7 \2\2st\5(\25\2tu\7!\2\2u~\5\b\5\2vw\7\r\2\2wx\7 \2\2xy\5(\25\2"+
		"yz\7!\2\2z{\5\b\5\2{}\3\2\2\2|v\3\2\2\2}\u0080\3\2\2\2~|\3\2\2\2~\177"+
		"\3\2\2\2\177\u0083\3\2\2\2\u0080~\3\2\2\2\u0081\u0082\7\16\2\2\u0082\u0084"+
		"\5\b\5\2\u0083\u0081\3\2\2\2\u0083\u0084\3\2\2\2\u0084\u0085\3\2\2\2\u0085"+
		"\u0086\7\17\2\2\u0086\17\3\2\2\2\u0087\u008a\t\2\2\2\u0088\u0089\7<\2"+
		"\2\u0089\u008b\7\61\2\2\u008a\u0088\3\2\2\2\u008a\u008b\3\2\2\2\u008b"+
		"\u008e\3\2\2\2\u008c\u008f\5\22\n\2\u008d\u008f\5\24\13\2\u008e\u008c"+
		"\3\2\2\2\u008e\u008d\3\2\2\2\u008f\21\3\2\2\2\u0090\u0097\5\34\17\2\u0091"+
		"\u0097\5\36\20\2\u0092\u0097\5\26\f\2\u0093\u0097\5\32\16\2\u0094\u0097"+
		"\5(\25\2\u0095\u0097\5\62\32\2\u0096\u0090\3\2\2\2\u0096\u0091\3\2\2\2"+
		"\u0096\u0092\3\2\2\2\u0096\u0093\3\2\2\2\u0096\u0094\3\2\2\2\u0096\u0095"+
		"\3\2\2\2\u0097\23\3\2\2\2\u0098\u00a1\7 \2\2\u0099\u009e\7@\2\2\u009a"+
		"\u009b\7\61\2\2\u009b\u009d\7@\2\2\u009c\u009a\3\2\2\2\u009d\u00a0\3\2"+
		"\2\2\u009e\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a2\3\2\2\2\u00a0"+
		"\u009e\3\2\2\2\u00a1\u0099\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a3\3\2"+
		"\2\2\u00a3\u00a4\7!\2\2\u00a4\u00a5\7\5\2\2\u00a5\u00a6\5\b\5\2\u00a6"+
		"\25\3\2\2\2\u00a7\u00a9\7\67\2\2\u00a8\u00aa\5\30\r\2\u00a9\u00a8\3\2"+
		"\2\2\u00a9\u00aa\3\2\2\2\u00aa\u00af\3\2\2\2\u00ab\u00ac\7\61\2\2\u00ac"+
		"\u00ae\5\30\r\2\u00ad\u00ab\3\2\2\2\u00ae\u00b1\3\2\2\2\u00af\u00ad\3"+
		"\2\2\2\u00af\u00b0\3\2\2\2\u00b0\u00b2\3\2\2\2\u00b1\u00af\3\2\2\2\u00b2"+
		"\u00b3\78\2\2\u00b3\27\3\2\2\2\u00b4\u00b7\7:\2\2\u00b5\u00b6\7\62\2\2"+
		"\u00b6\u00b8\5\22\n\2\u00b7\u00b5\3\2\2\2\u00b7\u00b8\3\2\2\2\u00b8\31"+
		"\3\2\2\2\u00b9\u00bb\7\65\2\2\u00ba\u00bc\5\22\n\2\u00bb\u00ba\3\2\2\2"+
		"\u00bb\u00bc\3\2\2\2\u00bc\u00c1\3\2\2\2\u00bd\u00be\7\61\2\2\u00be\u00c0"+
		"\5\22\n\2\u00bf\u00bd\3\2\2\2\u00c0\u00c3\3\2\2\2\u00c1\u00bf\3\2\2\2"+
		"\u00c1\u00c2\3\2\2\2\u00c2\u00c4\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c4\u00c5"+
		"\7\66\2\2\u00c5\33\3\2\2\2\u00c6\u00c7\5 \21\2\u00c7\u00ca\7\6\2\2\u00c8"+
		"\u00cb\5\26\f\2\u00c9\u00cb\5\32\16\2\u00ca\u00c8\3\2\2\2\u00ca\u00c9"+
		"\3\2\2\2\u00cb\35\3\2\2\2\u00cc\u00cd\5\"\22\2\u00cd\u00d0\7\6\2\2\u00ce"+
		"\u00d1\5\26\f\2\u00cf\u00d1\5\32\16\2\u00d0\u00ce\3\2\2\2\u00d0\u00cf"+
		"\3\2\2\2\u00d1\37\3\2\2\2\u00d2\u00d3\5\"\22\2\u00d3\u00dc\7 \2\2\u00d4"+
		"\u00d9\5\22\n\2\u00d5\u00d6\7\61\2\2\u00d6\u00d8\5\22\n\2\u00d7\u00d5"+
		"\3\2\2\2\u00d8\u00db\3\2\2\2\u00d9\u00d7\3\2\2\2\u00d9\u00da\3\2\2\2\u00da"+
		"\u00dd\3\2\2\2\u00db\u00d9\3\2\2\2\u00dc\u00d4\3\2\2\2\u00dc\u00dd\3\2"+
		"\2\2\u00dd\u00de\3\2\2\2\u00de\u00e2\7!\2\2\u00df\u00e0\7\64\2\2\u00e0"+
		"\u00e3\5\"\22\2\u00e1\u00e3\5&\24\2\u00e2\u00df\3\2\2\2\u00e2\u00e1\3"+
		"\2\2\2\u00e2\u00e3\3\2\2\2\u00e3!\3\2\2\2\u00e4\u00e5\79\2\2\u00e5\u00e6"+
		"\7\67\2\2\u00e6\u00eb\5$\23\2\u00e7\u00e8\7\64\2\2\u00e8\u00ea\5$\23\2"+
		"\u00e9\u00e7\3\2\2\2\u00ea\u00ed\3\2\2\2\u00eb\u00e9\3\2\2\2\u00eb\u00ec"+
		"\3\2\2\2\u00ec\u00ee\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ee\u00ef\78\2\2\u00ef"+
		"\u00f9\3\2\2\2\u00f0\u00f5\5$\23\2\u00f1\u00f2\7\64\2\2\u00f2\u00f4\5"+
		"$\23\2\u00f3\u00f1\3\2\2\2\u00f4\u00f7\3\2\2\2\u00f5\u00f3\3\2\2\2\u00f5"+
		"\u00f6\3\2\2\2\u00f6\u00f9\3\2\2\2\u00f7\u00f5\3\2\2\2\u00f8\u00e4\3\2"+
		"\2\2\u00f8\u00f0\3\2\2\2\u00f9#\3\2\2\2\u00fa\u00fe\7@\2\2\u00fb\u00fd"+
		"\5&\24\2\u00fc\u00fb\3\2\2\2\u00fd\u0100\3\2\2\2\u00fe\u00fc\3\2\2\2\u00fe"+
		"\u00ff\3\2\2\2\u00ff%\3\2\2\2\u0100\u00fe\3\2\2\2\u0101\u0102\7\65\2\2"+
		"\u0102\u0103\t\3\2\2\u0103\u0104\7\66\2\2\u0104\'\3\2\2\2\u0105\u0106"+
		"\7 \2\2\u0106\u0107\5(\25\2\u0107\u010a\7!\2\2\u0108\u010b\5,\27\2\u0109"+
		"\u010b\5.\30\2\u010a\u0108\3\2\2\2\u010a\u0109\3\2\2\2\u010a\u010b\3\2"+
		"\2\2\u010b\u0113\3\2\2\2\u010c\u0113\5*\26\2\u010d\u0110\5\60\31\2\u010e"+
		"\u0111\5,\27\2\u010f\u0111\5.\30\2\u0110\u010e\3\2\2\2\u0110\u010f\3\2"+
		"\2\2\u0110\u0111\3\2\2\2\u0111\u0113\3\2\2\2\u0112\u0105\3\2\2\2\u0112"+
		"\u010c\3\2\2\2\u0112\u010d\3\2\2\2\u0113)\3\2\2\2\u0114\u0115\t\4\2\2"+
		"\u0115\u0116\5\60\31\2\u0116+\3\2\2\2\u0117\u0118\t\5\2\2\u0118\u0119"+
		"\5(\25\2\u0119-\3\2\2\2\u011a\u011b\7\7\2\2\u011b\u011c\5(\25\2\u011c"+
		"\u011d\7\62\2\2\u011d\u011e\5(\25\2\u011e/\3\2\2\2\u011f\u0123\5\62\32"+
		"\2\u0120\u0123\5 \21\2\u0121\u0123\5\"\22\2\u0122\u011f\3\2\2\2\u0122"+
		"\u0120\3\2\2\2\u0122\u0121\3\2\2\2\u0123\61\3\2\2\2\u0124\u0125\t\6\2"+
		"\2\u0125\63\3\2\2\2%\67=CNW[acho~\u0083\u008a\u008e\u0096\u009e\u00a1"+
		"\u00a9\u00af\u00b7\u00bb\u00c1\u00ca\u00d0\u00d9\u00dc\u00e2\u00eb\u00f5"+
		"\u00f8\u00fe\u010a\u0110\u0112\u0122";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}