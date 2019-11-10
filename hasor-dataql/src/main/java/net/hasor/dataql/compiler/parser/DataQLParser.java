// Generated from /Users/yongchun.zyc/Documents/Drive/projects/hasor/hasor.git/hasor-dataql/src/main/java/net/hasor/dataql/compiler/parser/DataQL.g4 by ANTLR 4.7.2
package net.hasor.dataql.compiler.parser;
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
		T__0=1, T__1=2, T__2=3, T__3=4, WS=5, COMMENT1=6, COMMENT2=7, EOL=8, IF=9, 
		ELSE=10, RETURN=11, THROW=12, EXIT=13, VAR=14, OPTION=15, IMPORT=16, TRUE=17, 
		FALSE=18, NULL=19, AS=20, PLUS=21, MINUS=22, MUL=23, DIV=24, DIV2=25, 
		MOD=26, LBT=27, RBT=28, AND=29, OR=30, NOT=31, XOR=32, LSHIFT=33, RSHIFT=34, 
		RSHIFT2=35, GT=36, GE=37, LT=38, LE=39, EQ=40, NE=41, SC_OR=42, SC_AND=43, 
		COMMA=44, COLON=45, ASS=46, DOT=47, LSBT=48, RSBT=49, OCBR=50, CCBR=51, 
		ROU=52, STRING=53, HEX_NUM=54, OCT_NUM=55, BIT_NUM=56, INTEGER_NUM=57, 
		DECIMAL_NUM=58, IDENTIFIER=59;
	public static final int
		RULE_rootInstSet = 0, RULE_optionInst = 1, RULE_importInst = 2, RULE_blockSet = 3, 
		RULE_ifInst = 4, RULE_breakInst = 5, RULE_lambdaDef = 6, RULE_varInst = 7, 
		RULE_anyObject = 8, RULE_routeMapping = 9, RULE_routeNameSet = 10, RULE_routeName = 11, 
		RULE_routeSubscript = 12, RULE_funcCall = 13, RULE_funcCallResult = 14, 
		RULE_objectValue = 15, RULE_objectKeyValue = 16, RULE_listValue = 17, 
		RULE_primitiveValue = 18, RULE_expr = 19, RULE_dyadicExpr = 20, RULE_ternaryExpr = 21, 
		RULE_atomExpr = 22;
	private static String[] makeRuleNames() {
		return new String[] {
			"rootInstSet", "optionInst", "importInst", "blockSet", "ifInst", "breakInst", 
			"lambdaDef", "varInst", "anyObject", "routeMapping", "routeNameSet", 
			"routeName", "routeSubscript", "funcCall", "funcCallResult", "objectValue", 
			"objectKeyValue", "listValue", "primitiveValue", "expr", "dyadicExpr", 
			"ternaryExpr", "atomExpr"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'->'", "'=>'", "'?'", null, null, null, null, "'if'", "'else'", 
			"'return'", "'throw'", "'exit'", "'var'", "'option'", "'import'", "'true'", 
			"'false'", "'null'", "'as'", "'+'", "'-'", "'*'", "'/'", "'\\'", "'%'", 
			"'('", "')'", "'&'", "'|'", "'!'", "'^'", "'<<'", "'>>'", "'>>>'", "'>'", 
			"'>='", "'<'", "'<='", "'=='", "'!='", "'||'", "'&&'", "','", "':'", 
			"'='", "'.'", "'['", "']'", "'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, "WS", "COMMENT1", "COMMENT2", "EOL", "IF", 
			"ELSE", "RETURN", "THROW", "EXIT", "VAR", "OPTION", "IMPORT", "TRUE", 
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

	public static class RootInstSetContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(DataQLParser.EOF, 0); }
		public List<OptionInstContext> optionInst() {
			return getRuleContexts(OptionInstContext.class);
		}
		public OptionInstContext optionInst(int i) {
			return getRuleContext(OptionInstContext.class,i);
		}
		public List<ImportInstContext> importInst() {
			return getRuleContexts(ImportInstContext.class);
		}
		public ImportInstContext importInst(int i) {
			return getRuleContext(ImportInstContext.class,i);
		}
		public List<BlockSetContext> blockSet() {
			return getRuleContexts(BlockSetContext.class);
		}
		public BlockSetContext blockSet(int i) {
			return getRuleContext(BlockSetContext.class,i);
		}
		public RootInstSetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rootInstSet; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitRootInstSet(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RootInstSetContext rootInstSet() throws RecognitionException {
		RootInstSetContext _localctx = new RootInstSetContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_rootInstSet);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(49);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OPTION) {
				{
				{
				setState(46);
				optionInst();
				}
				}
				setState(51);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(55);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IMPORT) {
				{
				{
				setState(52);
				importInst();
				}
				}
				setState(57);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(59); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(58);
				blockSet();
				}
				}
				setState(61); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IF) | (1L << RETURN) | (1L << THROW) | (1L << EXIT) | (1L << VAR) | (1L << OCBR))) != 0) );
			setState(63);
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

	public static class OptionInstContext extends ParserRuleContext {
		public TerminalNode OPTION() { return getToken(DataQLParser.OPTION, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataQLParser.IDENTIFIER, 0); }
		public TerminalNode ASS() { return getToken(DataQLParser.ASS, 0); }
		public PrimitiveValueContext primitiveValue() {
			return getRuleContext(PrimitiveValueContext.class,0);
		}
		public OptionInstContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optionInst; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitOptionInst(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OptionInstContext optionInst() throws RecognitionException {
		OptionInstContext _localctx = new OptionInstContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_optionInst);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(65);
			match(OPTION);
			setState(66);
			match(IDENTIFIER);
			setState(67);
			match(ASS);
			setState(68);
			primitiveValue();
			setState(70);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(69);
				match(T__0);
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

	public static class ImportInstContext extends ParserRuleContext {
		public TerminalNode IMPORT() { return getToken(DataQLParser.IMPORT, 0); }
		public TerminalNode STRING() { return getToken(DataQLParser.STRING, 0); }
		public TerminalNode AS() { return getToken(DataQLParser.AS, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataQLParser.IDENTIFIER, 0); }
		public TerminalNode ROU() { return getToken(DataQLParser.ROU, 0); }
		public ImportInstContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importInst; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitImportInst(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ImportInstContext importInst() throws RecognitionException {
		ImportInstContext _localctx = new ImportInstContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_importInst);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			match(IMPORT);
			setState(74);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ROU) {
				{
				setState(73);
				match(ROU);
				}
			}

			setState(76);
			match(STRING);
			setState(77);
			match(AS);
			setState(78);
			match(IDENTIFIER);
			setState(80);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(79);
				match(T__0);
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

	public static class BlockSetContext extends ParserRuleContext {
		public BlockSetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blockSet; }
	 
		public BlockSetContext() { }
		public void copyFrom(BlockSetContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SingleInstContext extends BlockSetContext {
		public VarInstContext varInst() {
			return getRuleContext(VarInstContext.class,0);
		}
		public IfInstContext ifInst() {
			return getRuleContext(IfInstContext.class,0);
		}
		public BreakInstContext breakInst() {
			return getRuleContext(BreakInstContext.class,0);
		}
		public SingleInstContext(BlockSetContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitSingleInst(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MultipleInstContext extends BlockSetContext {
		public TerminalNode OCBR() { return getToken(DataQLParser.OCBR, 0); }
		public TerminalNode CCBR() { return getToken(DataQLParser.CCBR, 0); }
		public List<VarInstContext> varInst() {
			return getRuleContexts(VarInstContext.class);
		}
		public VarInstContext varInst(int i) {
			return getRuleContext(VarInstContext.class,i);
		}
		public List<IfInstContext> ifInst() {
			return getRuleContexts(IfInstContext.class);
		}
		public IfInstContext ifInst(int i) {
			return getRuleContext(IfInstContext.class,i);
		}
		public List<BreakInstContext> breakInst() {
			return getRuleContexts(BreakInstContext.class);
		}
		public BreakInstContext breakInst(int i) {
			return getRuleContext(BreakInstContext.class,i);
		}
		public MultipleInstContext(BlockSetContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitMultipleInst(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockSetContext blockSet() throws RecognitionException {
		BlockSetContext _localctx = new BlockSetContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_blockSet);
		int _la;
		try {
			setState(105);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OCBR:
				_localctx = new MultipleInstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(82);
				match(OCBR);
				setState(91); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(86);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case VAR:
						{
						setState(83);
						varInst();
						}
						break;
					case IF:
						{
						setState(84);
						ifInst();
						}
						break;
					case RETURN:
					case THROW:
					case EXIT:
						{
						setState(85);
						breakInst();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(89);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==T__0) {
						{
						setState(88);
						match(T__0);
						}
					}

					}
					}
					setState(93); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IF) | (1L << RETURN) | (1L << THROW) | (1L << EXIT) | (1L << VAR))) != 0) );
				setState(95);
				match(CCBR);
				}
				break;
			case IF:
			case RETURN:
			case THROW:
			case EXIT:
			case VAR:
				_localctx = new SingleInstContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(100);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case VAR:
					{
					setState(97);
					varInst();
					}
					break;
				case IF:
					{
					setState(98);
					ifInst();
					}
					break;
				case RETURN:
				case THROW:
				case EXIT:
					{
					setState(99);
					breakInst();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(103);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
				case 1:
					{
					setState(102);
					match(T__0);
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

	public static class IfInstContext extends ParserRuleContext {
		public List<TerminalNode> IF() { return getTokens(DataQLParser.IF); }
		public TerminalNode IF(int i) {
			return getToken(DataQLParser.IF, i);
		}
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
		public List<TerminalNode> ELSE() { return getTokens(DataQLParser.ELSE); }
		public TerminalNode ELSE(int i) {
			return getToken(DataQLParser.ELSE, i);
		}
		public IfInstContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifInst; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitIfInst(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfInstContext ifInst() throws RecognitionException {
		IfInstContext _localctx = new IfInstContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_ifInst);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			match(IF);
			setState(108);
			match(LBT);
			setState(109);
			expr();
			setState(110);
			match(RBT);
			setState(111);
			blockSet();
			setState(121);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(112);
					match(ELSE);
					setState(113);
					match(IF);
					setState(114);
					match(LBT);
					setState(115);
					expr();
					setState(116);
					match(RBT);
					setState(117);
					blockSet();
					}
					} 
				}
				setState(123);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			}
			setState(126);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(124);
				match(ELSE);
				setState(125);
				blockSet();
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

	public static class BreakInstContext extends ParserRuleContext {
		public AnyObjectContext anyObject() {
			return getRuleContext(AnyObjectContext.class,0);
		}
		public TerminalNode RETURN() { return getToken(DataQLParser.RETURN, 0); }
		public TerminalNode THROW() { return getToken(DataQLParser.THROW, 0); }
		public TerminalNode EXIT() { return getToken(DataQLParser.EXIT, 0); }
		public TerminalNode INTEGER_NUM() { return getToken(DataQLParser.INTEGER_NUM, 0); }
		public TerminalNode COMMA() { return getToken(DataQLParser.COMMA, 0); }
		public BreakInstContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_breakInst; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitBreakInst(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BreakInstContext breakInst() throws RecognitionException {
		BreakInstContext _localctx = new BreakInstContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_breakInst);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RETURN) | (1L << THROW) | (1L << EXIT))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(131);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				setState(129);
				match(INTEGER_NUM);
				setState(130);
				match(COMMA);
				}
				break;
			}
			setState(133);
			anyObject();
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitLambdaDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LambdaDefContext lambdaDef() throws RecognitionException {
		LambdaDefContext _localctx = new LambdaDefContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_lambdaDef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(135);
			match(LBT);
			setState(144);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(136);
				match(IDENTIFIER);
				setState(141);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(137);
					match(COMMA);
					setState(138);
					match(IDENTIFIER);
					}
					}
					setState(143);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(146);
			match(RBT);
			setState(147);
			match(T__1);
			setState(148);
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

	public static class VarInstContext extends ParserRuleContext {
		public TerminalNode VAR() { return getToken(DataQLParser.VAR, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataQLParser.IDENTIFIER, 0); }
		public TerminalNode ASS() { return getToken(DataQLParser.ASS, 0); }
		public AnyObjectContext anyObject() {
			return getRuleContext(AnyObjectContext.class,0);
		}
		public VarInstContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varInst; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitVarInst(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarInstContext varInst() throws RecognitionException {
		VarInstContext _localctx = new VarInstContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_varInst);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			match(VAR);
			setState(151);
			match(IDENTIFIER);
			setState(152);
			match(ASS);
			setState(153);
			anyObject();
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

	public static class AnyObjectContext extends ParserRuleContext {
		public LambdaDefContext lambdaDef() {
			return getRuleContext(LambdaDefContext.class,0);
		}
		public PrimitiveValueContext primitiveValue() {
			return getRuleContext(PrimitiveValueContext.class,0);
		}
		public ObjectValueContext objectValue() {
			return getRuleContext(ObjectValueContext.class,0);
		}
		public ListValueContext listValue() {
			return getRuleContext(ListValueContext.class,0);
		}
		public FuncCallContext funcCall() {
			return getRuleContext(FuncCallContext.class,0);
		}
		public RouteMappingContext routeMapping() {
			return getRuleContext(RouteMappingContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public AnyObjectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_anyObject; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitAnyObject(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AnyObjectContext anyObject() throws RecognitionException {
		AnyObjectContext _localctx = new AnyObjectContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_anyObject);
		try {
			setState(162);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(155);
				lambdaDef();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(156);
				primitiveValue();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(157);
				objectValue();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(158);
				listValue();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(159);
				funcCall();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(160);
				routeMapping(0);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(161);
				expr();
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

	public static class RouteMappingContext extends ParserRuleContext {
		public RouteMappingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_routeMapping; }
	 
		public RouteMappingContext() { }
		public void copyFrom(RouteMappingContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SpecialRouteContext extends RouteMappingContext {
		public TerminalNode ROU() { return getToken(DataQLParser.ROU, 0); }
		public TerminalNode OCBR() { return getToken(DataQLParser.OCBR, 0); }
		public TerminalNode CCBR() { return getToken(DataQLParser.CCBR, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataQLParser.IDENTIFIER, 0); }
		public TerminalNode STRING() { return getToken(DataQLParser.STRING, 0); }
		public SpecialRouteContext(RouteMappingContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitSpecialRoute(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NormalRouteContext extends RouteMappingContext {
		public RouteNameSetContext routeNameSet() {
			return getRuleContext(RouteNameSetContext.class,0);
		}
		public NormalRouteContext(RouteMappingContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitNormalRoute(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConvertRouteContext extends RouteMappingContext {
		public RouteMappingContext routeMapping() {
			return getRuleContext(RouteMappingContext.class,0);
		}
		public ObjectValueContext objectValue() {
			return getRuleContext(ObjectValueContext.class,0);
		}
		public ListValueContext listValue() {
			return getRuleContext(ListValueContext.class,0);
		}
		public ConvertRouteContext(RouteMappingContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitConvertRoute(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RouteMappingContext routeMapping() throws RecognitionException {
		return routeMapping(0);
	}

	private RouteMappingContext routeMapping(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		RouteMappingContext _localctx = new RouteMappingContext(_ctx, _parentState);
		RouteMappingContext _prevctx = _localctx;
		int _startState = 18;
		enterRecursionRule(_localctx, 18, RULE_routeMapping, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ROU:
				{
				_localctx = new SpecialRouteContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(165);
				match(ROU);
				setState(166);
				match(OCBR);
				setState(167);
				_la = _input.LA(1);
				if ( !(_la==STRING || _la==IDENTIFIER) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(168);
				match(CCBR);
				}
				break;
			case IDENTIFIER:
				{
				_localctx = new NormalRouteContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(169);
				routeNameSet();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(180);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ConvertRouteContext(new RouteMappingContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_routeMapping);
					setState(172);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(173);
					match(T__2);
					setState(176);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case OCBR:
						{
						setState(174);
						objectValue();
						}
						break;
					case LSBT:
						{
						setState(175);
						listValue();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					}
					} 
				}
				setState(182);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
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

	public static class RouteNameSetContext extends ParserRuleContext {
		public List<RouteNameContext> routeName() {
			return getRuleContexts(RouteNameContext.class);
		}
		public RouteNameContext routeName(int i) {
			return getRuleContext(RouteNameContext.class,i);
		}
		public List<TerminalNode> DOT() { return getTokens(DataQLParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(DataQLParser.DOT, i);
		}
		public RouteNameSetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_routeNameSet; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitRouteNameSet(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RouteNameSetContext routeNameSet() throws RecognitionException {
		RouteNameSetContext _localctx = new RouteNameSetContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_routeNameSet);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(183);
			routeName();
			setState(188);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(184);
					match(DOT);
					setState(185);
					routeName();
					}
					} 
				}
				setState(190);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
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

	public static class RouteNameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(DataQLParser.IDENTIFIER, 0); }
		public List<RouteSubscriptContext> routeSubscript() {
			return getRuleContexts(RouteSubscriptContext.class);
		}
		public RouteSubscriptContext routeSubscript(int i) {
			return getRuleContext(RouteSubscriptContext.class,i);
		}
		public RouteNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_routeName; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitRouteName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RouteNameContext routeName() throws RecognitionException {
		RouteNameContext _localctx = new RouteNameContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_routeName);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(191);
			match(IDENTIFIER);
			setState(195);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(192);
					routeSubscript();
					}
					} 
				}
				setState(197);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
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

	public static class RouteSubscriptContext extends ParserRuleContext {
		public TerminalNode LSBT() { return getToken(DataQLParser.LSBT, 0); }
		public TerminalNode RSBT() { return getToken(DataQLParser.RSBT, 0); }
		public TerminalNode STRING() { return getToken(DataQLParser.STRING, 0); }
		public TerminalNode INTEGER_NUM() { return getToken(DataQLParser.INTEGER_NUM, 0); }
		public RouteSubscriptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_routeSubscript; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitRouteSubscript(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RouteSubscriptContext routeSubscript() throws RecognitionException {
		RouteSubscriptContext _localctx = new RouteSubscriptContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_routeSubscript);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(198);
			match(LSBT);
			setState(199);
			_la = _input.LA(1);
			if ( !(_la==STRING || _la==INTEGER_NUM) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(200);
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

	public static class FuncCallContext extends ParserRuleContext {
		public RouteMappingContext routeMapping() {
			return getRuleContext(RouteMappingContext.class,0);
		}
		public TerminalNode LBT() { return getToken(DataQLParser.LBT, 0); }
		public TerminalNode RBT() { return getToken(DataQLParser.RBT, 0); }
		public List<AnyObjectContext> anyObject() {
			return getRuleContexts(AnyObjectContext.class);
		}
		public AnyObjectContext anyObject(int i) {
			return getRuleContext(AnyObjectContext.class,i);
		}
		public FuncCallResultContext funcCallResult() {
			return getRuleContext(FuncCallResultContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(DataQLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DataQLParser.COMMA, i);
		}
		public FuncCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcCall; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitFuncCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuncCallContext funcCall() throws RecognitionException {
		FuncCallContext _localctx = new FuncCallContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_funcCall);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(202);
			routeMapping(0);
			setState(203);
			match(LBT);
			setState(212);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << PLUS) | (1L << MINUS) | (1L << LBT) | (1L << NOT) | (1L << LSBT) | (1L << OCBR) | (1L << ROU) | (1L << STRING) | (1L << HEX_NUM) | (1L << OCT_NUM) | (1L << BIT_NUM) | (1L << INTEGER_NUM) | (1L << DECIMAL_NUM) | (1L << IDENTIFIER))) != 0)) {
				{
				setState(204);
				anyObject();
				setState(209);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(205);
					match(COMMA);
					setState(206);
					anyObject();
					}
					}
					setState(211);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(214);
			match(RBT);
			setState(216);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
			case 1:
				{
				setState(215);
				funcCallResult();
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

	public static class FuncCallResultContext extends ParserRuleContext {
		public FuncCallResultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcCallResult; }
	 
		public FuncCallResultContext() { }
		public void copyFrom(FuncCallResultContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class FuncCallResult_routeContext extends FuncCallResultContext {
		public TerminalNode DOT() { return getToken(DataQLParser.DOT, 0); }
		public RouteNameSetContext routeNameSet() {
			return getRuleContext(RouteNameSetContext.class,0);
		}
		public FuncCallResultContext funcCallResult() {
			return getRuleContext(FuncCallResultContext.class,0);
		}
		public List<RouteSubscriptContext> routeSubscript() {
			return getRuleContexts(RouteSubscriptContext.class);
		}
		public RouteSubscriptContext routeSubscript(int i) {
			return getRuleContext(RouteSubscriptContext.class,i);
		}
		public FuncCallResult_routeContext(FuncCallResultContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitFuncCallResult_route(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FuncCallResult_convertContext extends FuncCallResultContext {
		public ObjectValueContext objectValue() {
			return getRuleContext(ObjectValueContext.class,0);
		}
		public ListValueContext listValue() {
			return getRuleContext(ListValueContext.class,0);
		}
		public FuncCallResult_convertContext(FuncCallResultContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitFuncCallResult_convert(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FuncCallResult_callContext extends FuncCallResultContext {
		public TerminalNode LBT() { return getToken(DataQLParser.LBT, 0); }
		public TerminalNode RBT() { return getToken(DataQLParser.RBT, 0); }
		public List<AnyObjectContext> anyObject() {
			return getRuleContexts(AnyObjectContext.class);
		}
		public AnyObjectContext anyObject(int i) {
			return getRuleContext(AnyObjectContext.class,i);
		}
		public FuncCallResultContext funcCallResult() {
			return getRuleContext(FuncCallResultContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(DataQLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DataQLParser.COMMA, i);
		}
		public FuncCallResult_callContext(FuncCallResultContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitFuncCallResult_call(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuncCallResultContext funcCallResult() throws RecognitionException {
		FuncCallResultContext _localctx = new FuncCallResultContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_funcCallResult);
		int _la;
		try {
			setState(250);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DOT:
			case LSBT:
				_localctx = new FuncCallResult_routeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(223);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LSBT) {
					{
					setState(219); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(218);
						routeSubscript();
						}
						}
						setState(221); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==LSBT );
					}
				}

				setState(225);
				match(DOT);
				setState(226);
				routeNameSet();
				setState(228);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
				case 1:
					{
					setState(227);
					funcCallResult();
					}
					break;
				}
				}
				break;
			case T__2:
				_localctx = new FuncCallResult_convertContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(230);
				match(T__2);
				setState(233);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case OCBR:
					{
					setState(231);
					objectValue();
					}
					break;
				case LSBT:
					{
					setState(232);
					listValue();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case LBT:
				_localctx = new FuncCallResult_callContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(235);
				match(LBT);
				setState(244);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << PLUS) | (1L << MINUS) | (1L << LBT) | (1L << NOT) | (1L << LSBT) | (1L << OCBR) | (1L << ROU) | (1L << STRING) | (1L << HEX_NUM) | (1L << OCT_NUM) | (1L << BIT_NUM) | (1L << INTEGER_NUM) | (1L << DECIMAL_NUM) | (1L << IDENTIFIER))) != 0)) {
					{
					setState(236);
					anyObject();
					setState(241);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(237);
						match(COMMA);
						setState(238);
						anyObject();
						}
						}
						setState(243);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(246);
				match(RBT);
				setState(248);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
				case 1:
					{
					setState(247);
					funcCallResult();
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

	public static class ObjectValueContext extends ParserRuleContext {
		public TerminalNode OCBR() { return getToken(DataQLParser.OCBR, 0); }
		public TerminalNode CCBR() { return getToken(DataQLParser.CCBR, 0); }
		public List<ObjectKeyValueContext> objectKeyValue() {
			return getRuleContexts(ObjectKeyValueContext.class);
		}
		public ObjectKeyValueContext objectKeyValue(int i) {
			return getRuleContext(ObjectKeyValueContext.class,i);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitObjectValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectValueContext objectValue() throws RecognitionException {
		ObjectValueContext _localctx = new ObjectValueContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_objectValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(252);
			match(OCBR);
			setState(254);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING) {
				{
				setState(253);
				objectKeyValue();
				}
			}

			setState(260);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(256);
				match(COMMA);
				setState(257);
				objectKeyValue();
				}
				}
				setState(262);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(263);
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

	public static class ObjectKeyValueContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(DataQLParser.STRING, 0); }
		public TerminalNode COLON() { return getToken(DataQLParser.COLON, 0); }
		public AnyObjectContext anyObject() {
			return getRuleContext(AnyObjectContext.class,0);
		}
		public ObjectKeyValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objectKeyValue; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitObjectKeyValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectKeyValueContext objectKeyValue() throws RecognitionException {
		ObjectKeyValueContext _localctx = new ObjectKeyValueContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_objectKeyValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(265);
			match(STRING);
			setState(268);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(266);
				match(COLON);
				setState(267);
				anyObject();
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
		public List<AnyObjectContext> anyObject() {
			return getRuleContexts(AnyObjectContext.class);
		}
		public AnyObjectContext anyObject(int i) {
			return getRuleContext(AnyObjectContext.class,i);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitListValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListValueContext listValue() throws RecognitionException {
		ListValueContext _localctx = new ListValueContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_listValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(270);
			match(LSBT);
			setState(272);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << PLUS) | (1L << MINUS) | (1L << LBT) | (1L << NOT) | (1L << LSBT) | (1L << OCBR) | (1L << ROU) | (1L << STRING) | (1L << HEX_NUM) | (1L << OCT_NUM) | (1L << BIT_NUM) | (1L << INTEGER_NUM) | (1L << DECIMAL_NUM) | (1L << IDENTIFIER))) != 0)) {
				{
				setState(271);
				anyObject();
				}
			}

			setState(278);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(274);
				match(COMMA);
				setState(275);
				anyObject();
				}
				}
				setState(280);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(281);
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

	public static class PrimitiveValueContext extends ParserRuleContext {
		public PrimitiveValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primitiveValue; }
	 
		public PrimitiveValueContext() { }
		public void copyFrom(PrimitiveValueContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class StringValueContext extends PrimitiveValueContext {
		public TerminalNode STRING() { return getToken(DataQLParser.STRING, 0); }
		public StringValueContext(PrimitiveValueContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitStringValue(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BooleanValueContext extends PrimitiveValueContext {
		public TerminalNode TRUE() { return getToken(DataQLParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(DataQLParser.FALSE, 0); }
		public BooleanValueContext(PrimitiveValueContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitBooleanValue(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumberValueContext extends PrimitiveValueContext {
		public TerminalNode DECIMAL_NUM() { return getToken(DataQLParser.DECIMAL_NUM, 0); }
		public TerminalNode INTEGER_NUM() { return getToken(DataQLParser.INTEGER_NUM, 0); }
		public TerminalNode HEX_NUM() { return getToken(DataQLParser.HEX_NUM, 0); }
		public TerminalNode OCT_NUM() { return getToken(DataQLParser.OCT_NUM, 0); }
		public TerminalNode BIT_NUM() { return getToken(DataQLParser.BIT_NUM, 0); }
		public NumberValueContext(PrimitiveValueContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitNumberValue(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NullValueContext extends PrimitiveValueContext {
		public TerminalNode NULL() { return getToken(DataQLParser.NULL, 0); }
		public NullValueContext(PrimitiveValueContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitNullValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimitiveValueContext primitiveValue() throws RecognitionException {
		PrimitiveValueContext _localctx = new PrimitiveValueContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_primitiveValue);
		int _la;
		try {
			setState(287);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING:
				_localctx = new StringValueContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(283);
				match(STRING);
				}
				break;
			case NULL:
				_localctx = new NullValueContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(284);
				match(NULL);
				}
				break;
			case TRUE:
			case FALSE:
				_localctx = new BooleanValueContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(285);
				_la = _input.LA(1);
				if ( !(_la==TRUE || _la==FALSE) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case HEX_NUM:
			case OCT_NUM:
			case BIT_NUM:
			case INTEGER_NUM:
			case DECIMAL_NUM:
				_localctx = new NumberValueContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(286);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << HEX_NUM) | (1L << OCT_NUM) | (1L << BIT_NUM) | (1L << INTEGER_NUM) | (1L << DECIMAL_NUM))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
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

	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class UnaryExprContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode PLUS() { return getToken(DataQLParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(DataQLParser.MINUS, 0); }
		public TerminalNode NOT() { return getToken(DataQLParser.NOT, 0); }
		public UnaryExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitUnaryExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PrivilegeExprContext extends ExprContext {
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
		public PrivilegeExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitPrivilegeExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MultipleExprContext extends ExprContext {
		public AtomExprContext atomExpr() {
			return getRuleContext(AtomExprContext.class,0);
		}
		public DyadicExprContext dyadicExpr() {
			return getRuleContext(DyadicExprContext.class,0);
		}
		public TernaryExprContext ternaryExpr() {
			return getRuleContext(TernaryExprContext.class,0);
		}
		public MultipleExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitMultipleExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_expr);
		int _la;
		try {
			setState(303);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PLUS:
			case MINUS:
			case NOT:
				_localctx = new UnaryExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(289);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << NOT))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(290);
				expr();
				}
				break;
			case LBT:
				_localctx = new PrivilegeExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(291);
				match(LBT);
				setState(292);
				expr();
				setState(293);
				match(RBT);
				setState(296);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
				case 1:
					{
					setState(294);
					dyadicExpr();
					}
					break;
				case 2:
					{
					setState(295);
					ternaryExpr();
					}
					break;
				}
				}
				break;
			case TRUE:
			case FALSE:
			case NULL:
			case ROU:
			case STRING:
			case HEX_NUM:
			case OCT_NUM:
			case BIT_NUM:
			case INTEGER_NUM:
			case DECIMAL_NUM:
			case IDENTIFIER:
				_localctx = new MultipleExprContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(298);
				atomExpr();
				setState(301);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
				case 1:
					{
					setState(299);
					dyadicExpr();
					}
					break;
				case 2:
					{
					setState(300);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitDyadicExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DyadicExprContext dyadicExpr() throws RecognitionException {
		DyadicExprContext _localctx = new DyadicExprContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_dyadicExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(305);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << MUL) | (1L << DIV) | (1L << DIV2) | (1L << MOD) | (1L << LBT) | (1L << RBT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << XOR) | (1L << LSHIFT) | (1L << RSHIFT) | (1L << RSHIFT2) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << SC_OR) | (1L << SC_AND))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(306);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitTernaryExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TernaryExprContext ternaryExpr() throws RecognitionException {
		TernaryExprContext _localctx = new TernaryExprContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_ternaryExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(308);
			match(T__3);
			setState(309);
			expr();
			setState(310);
			match(COLON);
			setState(311);
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
		public FuncCallContext funcCall() {
			return getRuleContext(FuncCallContext.class,0);
		}
		public RouteMappingContext routeMapping() {
			return getRuleContext(RouteMappingContext.class,0);
		}
		public AtomExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atomExpr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitAtomExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AtomExprContext atomExpr() throws RecognitionException {
		AtomExprContext _localctx = new AtomExprContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_atomExpr);
		try {
			setState(316);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(313);
				primitiveValue();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(314);
				funcCall();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(315);
				routeMapping(0);
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 9:
			return routeMapping_sempred((RouteMappingContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean routeMapping_sempred(RouteMappingContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3=\u0141\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\3\2\7\2\62"+
		"\n\2\f\2\16\2\65\13\2\3\2\7\28\n\2\f\2\16\2;\13\2\3\2\6\2>\n\2\r\2\16"+
		"\2?\3\2\3\2\3\3\3\3\3\3\3\3\3\3\5\3I\n\3\3\4\3\4\5\4M\n\4\3\4\3\4\3\4"+
		"\3\4\5\4S\n\4\3\5\3\5\3\5\3\5\5\5Y\n\5\3\5\5\5\\\n\5\6\5^\n\5\r\5\16\5"+
		"_\3\5\3\5\3\5\3\5\3\5\5\5g\n\5\3\5\5\5j\n\5\5\5l\n\5\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\7\6z\n\6\f\6\16\6}\13\6\3\6\3\6\5\6\u0081"+
		"\n\6\3\7\3\7\3\7\5\7\u0086\n\7\3\7\3\7\3\b\3\b\3\b\3\b\7\b\u008e\n\b\f"+
		"\b\16\b\u0091\13\b\5\b\u0093\n\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3"+
		"\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u00a5\n\n\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\5\13\u00ad\n\13\3\13\3\13\3\13\3\13\5\13\u00b3\n\13\7\13\u00b5\n\13\f"+
		"\13\16\13\u00b8\13\13\3\f\3\f\3\f\7\f\u00bd\n\f\f\f\16\f\u00c0\13\f\3"+
		"\r\3\r\7\r\u00c4\n\r\f\r\16\r\u00c7\13\r\3\16\3\16\3\16\3\16\3\17\3\17"+
		"\3\17\3\17\3\17\7\17\u00d2\n\17\f\17\16\17\u00d5\13\17\5\17\u00d7\n\17"+
		"\3\17\3\17\5\17\u00db\n\17\3\20\6\20\u00de\n\20\r\20\16\20\u00df\5\20"+
		"\u00e2\n\20\3\20\3\20\3\20\5\20\u00e7\n\20\3\20\3\20\3\20\5\20\u00ec\n"+
		"\20\3\20\3\20\3\20\3\20\7\20\u00f2\n\20\f\20\16\20\u00f5\13\20\5\20\u00f7"+
		"\n\20\3\20\3\20\5\20\u00fb\n\20\5\20\u00fd\n\20\3\21\3\21\5\21\u0101\n"+
		"\21\3\21\3\21\7\21\u0105\n\21\f\21\16\21\u0108\13\21\3\21\3\21\3\22\3"+
		"\22\3\22\5\22\u010f\n\22\3\23\3\23\5\23\u0113\n\23\3\23\3\23\7\23\u0117"+
		"\n\23\f\23\16\23\u011a\13\23\3\23\3\23\3\24\3\24\3\24\3\24\5\24\u0122"+
		"\n\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25\5\25\u012b\n\25\3\25\3\25\3\25"+
		"\5\25\u0130\n\25\5\25\u0132\n\25\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3"+
		"\27\3\30\3\30\3\30\5\30\u013f\n\30\3\30\2\3\24\31\2\4\6\b\n\f\16\20\22"+
		"\24\26\30\32\34\36 \"$&(*,.\2\t\3\2\r\17\4\2\67\67==\4\2\67\67;;\3\2\23"+
		"\24\3\28<\4\2\27\30!!\3\2\27-\2\u0163\2\63\3\2\2\2\4C\3\2\2\2\6J\3\2\2"+
		"\2\bk\3\2\2\2\nm\3\2\2\2\f\u0082\3\2\2\2\16\u0089\3\2\2\2\20\u0098\3\2"+
		"\2\2\22\u00a4\3\2\2\2\24\u00ac\3\2\2\2\26\u00b9\3\2\2\2\30\u00c1\3\2\2"+
		"\2\32\u00c8\3\2\2\2\34\u00cc\3\2\2\2\36\u00fc\3\2\2\2 \u00fe\3\2\2\2\""+
		"\u010b\3\2\2\2$\u0110\3\2\2\2&\u0121\3\2\2\2(\u0131\3\2\2\2*\u0133\3\2"+
		"\2\2,\u0136\3\2\2\2.\u013e\3\2\2\2\60\62\5\4\3\2\61\60\3\2\2\2\62\65\3"+
		"\2\2\2\63\61\3\2\2\2\63\64\3\2\2\2\649\3\2\2\2\65\63\3\2\2\2\668\5\6\4"+
		"\2\67\66\3\2\2\28;\3\2\2\29\67\3\2\2\29:\3\2\2\2:=\3\2\2\2;9\3\2\2\2<"+
		">\5\b\5\2=<\3\2\2\2>?\3\2\2\2?=\3\2\2\2?@\3\2\2\2@A\3\2\2\2AB\7\2\2\3"+
		"B\3\3\2\2\2CD\7\21\2\2DE\7=\2\2EF\7\60\2\2FH\5&\24\2GI\7\3\2\2HG\3\2\2"+
		"\2HI\3\2\2\2I\5\3\2\2\2JL\7\22\2\2KM\7\66\2\2LK\3\2\2\2LM\3\2\2\2MN\3"+
		"\2\2\2NO\7\67\2\2OP\7\26\2\2PR\7=\2\2QS\7\3\2\2RQ\3\2\2\2RS\3\2\2\2S\7"+
		"\3\2\2\2T]\7\64\2\2UY\5\20\t\2VY\5\n\6\2WY\5\f\7\2XU\3\2\2\2XV\3\2\2\2"+
		"XW\3\2\2\2Y[\3\2\2\2Z\\\7\3\2\2[Z\3\2\2\2[\\\3\2\2\2\\^\3\2\2\2]X\3\2"+
		"\2\2^_\3\2\2\2_]\3\2\2\2_`\3\2\2\2`a\3\2\2\2ab\7\65\2\2bl\3\2\2\2cg\5"+
		"\20\t\2dg\5\n\6\2eg\5\f\7\2fc\3\2\2\2fd\3\2\2\2fe\3\2\2\2gi\3\2\2\2hj"+
		"\7\3\2\2ih\3\2\2\2ij\3\2\2\2jl\3\2\2\2kT\3\2\2\2kf\3\2\2\2l\t\3\2\2\2"+
		"mn\7\13\2\2no\7\35\2\2op\5(\25\2pq\7\36\2\2q{\5\b\5\2rs\7\f\2\2st\7\13"+
		"\2\2tu\7\35\2\2uv\5(\25\2vw\7\36\2\2wx\5\b\5\2xz\3\2\2\2yr\3\2\2\2z}\3"+
		"\2\2\2{y\3\2\2\2{|\3\2\2\2|\u0080\3\2\2\2}{\3\2\2\2~\177\7\f\2\2\177\u0081"+
		"\5\b\5\2\u0080~\3\2\2\2\u0080\u0081\3\2\2\2\u0081\13\3\2\2\2\u0082\u0085"+
		"\t\2\2\2\u0083\u0084\7;\2\2\u0084\u0086\7.\2\2\u0085\u0083\3\2\2\2\u0085"+
		"\u0086\3\2\2\2\u0086\u0087\3\2\2\2\u0087\u0088\5\22\n\2\u0088\r\3\2\2"+
		"\2\u0089\u0092\7\35\2\2\u008a\u008f\7=\2\2\u008b\u008c\7.\2\2\u008c\u008e"+
		"\7=\2\2\u008d\u008b\3\2\2\2\u008e\u0091\3\2\2\2\u008f\u008d\3\2\2\2\u008f"+
		"\u0090\3\2\2\2\u0090\u0093\3\2\2\2\u0091\u008f\3\2\2\2\u0092\u008a\3\2"+
		"\2\2\u0092\u0093\3\2\2\2\u0093\u0094\3\2\2\2\u0094\u0095\7\36\2\2\u0095"+
		"\u0096\7\4\2\2\u0096\u0097\5\b\5\2\u0097\17\3\2\2\2\u0098\u0099\7\20\2"+
		"\2\u0099\u009a\7=\2\2\u009a\u009b\7\60\2\2\u009b\u009c\5\22\n\2\u009c"+
		"\21\3\2\2\2\u009d\u00a5\5\16\b\2\u009e\u00a5\5&\24\2\u009f\u00a5\5 \21"+
		"\2\u00a0\u00a5\5$\23\2\u00a1\u00a5\5\34\17\2\u00a2\u00a5\5\24\13\2\u00a3"+
		"\u00a5\5(\25\2\u00a4\u009d\3\2\2\2\u00a4\u009e\3\2\2\2\u00a4\u009f\3\2"+
		"\2\2\u00a4\u00a0\3\2\2\2\u00a4\u00a1\3\2\2\2\u00a4\u00a2\3\2\2\2\u00a4"+
		"\u00a3\3\2\2\2\u00a5\23\3\2\2\2\u00a6\u00a7\b\13\1\2\u00a7\u00a8\7\66"+
		"\2\2\u00a8\u00a9\7\64\2\2\u00a9\u00aa\t\3\2\2\u00aa\u00ad\7\65\2\2\u00ab"+
		"\u00ad\5\26\f\2\u00ac\u00a6\3\2\2\2\u00ac\u00ab\3\2\2\2\u00ad\u00b6\3"+
		"\2\2\2\u00ae\u00af\f\3\2\2\u00af\u00b2\7\5\2\2\u00b0\u00b3\5 \21\2\u00b1"+
		"\u00b3\5$\23\2\u00b2\u00b0\3\2\2\2\u00b2\u00b1\3\2\2\2\u00b3\u00b5\3\2"+
		"\2\2\u00b4\u00ae\3\2\2\2\u00b5\u00b8\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b6"+
		"\u00b7\3\2\2\2\u00b7\25\3\2\2\2\u00b8\u00b6\3\2\2\2\u00b9\u00be\5\30\r"+
		"\2\u00ba\u00bb\7\61\2\2\u00bb\u00bd\5\30\r\2\u00bc\u00ba\3\2\2\2\u00bd"+
		"\u00c0\3\2\2\2\u00be\u00bc\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf\27\3\2\2"+
		"\2\u00c0\u00be\3\2\2\2\u00c1\u00c5\7=\2\2\u00c2\u00c4\5\32\16\2\u00c3"+
		"\u00c2\3\2\2\2\u00c4\u00c7\3\2\2\2\u00c5\u00c3\3\2\2\2\u00c5\u00c6\3\2"+
		"\2\2\u00c6\31\3\2\2\2\u00c7\u00c5\3\2\2\2\u00c8\u00c9\7\62\2\2\u00c9\u00ca"+
		"\t\4\2\2\u00ca\u00cb\7\63\2\2\u00cb\33\3\2\2\2\u00cc\u00cd\5\24\13\2\u00cd"+
		"\u00d6\7\35\2\2\u00ce\u00d3\5\22\n\2\u00cf\u00d0\7.\2\2\u00d0\u00d2\5"+
		"\22\n\2\u00d1\u00cf\3\2\2\2\u00d2\u00d5\3\2\2\2\u00d3\u00d1\3\2\2\2\u00d3"+
		"\u00d4\3\2\2\2\u00d4\u00d7\3\2\2\2\u00d5\u00d3\3\2\2\2\u00d6\u00ce\3\2"+
		"\2\2\u00d6\u00d7\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00da\7\36\2\2\u00d9"+
		"\u00db\5\36\20\2\u00da\u00d9\3\2\2\2\u00da\u00db\3\2\2\2\u00db\35\3\2"+
		"\2\2\u00dc\u00de\5\32\16\2\u00dd\u00dc\3\2\2\2\u00de\u00df\3\2\2\2\u00df"+
		"\u00dd\3\2\2\2\u00df\u00e0\3\2\2\2\u00e0\u00e2\3\2\2\2\u00e1\u00dd\3\2"+
		"\2\2\u00e1\u00e2\3\2\2\2\u00e2\u00e3\3\2\2\2\u00e3\u00e4\7\61\2\2\u00e4"+
		"\u00e6\5\26\f\2\u00e5\u00e7\5\36\20\2\u00e6\u00e5\3\2\2\2\u00e6\u00e7"+
		"\3\2\2\2\u00e7\u00fd\3\2\2\2\u00e8\u00eb\7\5\2\2\u00e9\u00ec\5 \21\2\u00ea"+
		"\u00ec\5$\23\2\u00eb\u00e9\3\2\2\2\u00eb\u00ea\3\2\2\2\u00ec\u00fd\3\2"+
		"\2\2\u00ed\u00f6\7\35\2\2\u00ee\u00f3\5\22\n\2\u00ef\u00f0\7.\2\2\u00f0"+
		"\u00f2\5\22\n\2\u00f1\u00ef\3\2\2\2\u00f2\u00f5\3\2\2\2\u00f3\u00f1\3"+
		"\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\u00f7\3\2\2\2\u00f5\u00f3\3\2\2\2\u00f6"+
		"\u00ee\3\2\2\2\u00f6\u00f7\3\2\2\2\u00f7\u00f8\3\2\2\2\u00f8\u00fa\7\36"+
		"\2\2\u00f9\u00fb\5\36\20\2\u00fa\u00f9\3\2\2\2\u00fa\u00fb\3\2\2\2\u00fb"+
		"\u00fd\3\2\2\2\u00fc\u00e1\3\2\2\2\u00fc\u00e8\3\2\2\2\u00fc\u00ed\3\2"+
		"\2\2\u00fd\37\3\2\2\2\u00fe\u0100\7\64\2\2\u00ff\u0101\5\"\22\2\u0100"+
		"\u00ff\3\2\2\2\u0100\u0101\3\2\2\2\u0101\u0106\3\2\2\2\u0102\u0103\7."+
		"\2\2\u0103\u0105\5\"\22\2\u0104\u0102\3\2\2\2\u0105\u0108\3\2\2\2\u0106"+
		"\u0104\3\2\2\2\u0106\u0107\3\2\2\2\u0107\u0109\3\2\2\2\u0108\u0106\3\2"+
		"\2\2\u0109\u010a\7\65\2\2\u010a!\3\2\2\2\u010b\u010e\7\67\2\2\u010c\u010d"+
		"\7/\2\2\u010d\u010f\5\22\n\2\u010e\u010c\3\2\2\2\u010e\u010f\3\2\2\2\u010f"+
		"#\3\2\2\2\u0110\u0112\7\62\2\2\u0111\u0113\5\22\n\2\u0112\u0111\3\2\2"+
		"\2\u0112\u0113\3\2\2\2\u0113\u0118\3\2\2\2\u0114\u0115\7.\2\2\u0115\u0117"+
		"\5\22\n\2\u0116\u0114\3\2\2\2\u0117\u011a\3\2\2\2\u0118\u0116\3\2\2\2"+
		"\u0118\u0119\3\2\2\2\u0119\u011b\3\2\2\2\u011a\u0118\3\2\2\2\u011b\u011c"+
		"\7\63\2\2\u011c%\3\2\2\2\u011d\u0122\7\67\2\2\u011e\u0122\7\25\2\2\u011f"+
		"\u0122\t\5\2\2\u0120\u0122\t\6\2\2\u0121\u011d\3\2\2\2\u0121\u011e\3\2"+
		"\2\2\u0121\u011f\3\2\2\2\u0121\u0120\3\2\2\2\u0122\'\3\2\2\2\u0123\u0124"+
		"\t\7\2\2\u0124\u0132\5(\25\2\u0125\u0126\7\35\2\2\u0126\u0127\5(\25\2"+
		"\u0127\u012a\7\36\2\2\u0128\u012b\5*\26\2\u0129\u012b\5,\27\2\u012a\u0128"+
		"\3\2\2\2\u012a\u0129\3\2\2\2\u012a\u012b\3\2\2\2\u012b\u0132\3\2\2\2\u012c"+
		"\u012f\5.\30\2\u012d\u0130\5*\26\2\u012e\u0130\5,\27\2\u012f\u012d\3\2"+
		"\2\2\u012f\u012e\3\2\2\2\u012f\u0130\3\2\2\2\u0130\u0132\3\2\2\2\u0131"+
		"\u0123\3\2\2\2\u0131\u0125\3\2\2\2\u0131\u012c\3\2\2\2\u0132)\3\2\2\2"+
		"\u0133\u0134\t\b\2\2\u0134\u0135\5(\25\2\u0135+\3\2\2\2\u0136\u0137\7"+
		"\6\2\2\u0137\u0138\5(\25\2\u0138\u0139\7/\2\2\u0139\u013a\5(\25\2\u013a"+
		"-\3\2\2\2\u013b\u013f\5&\24\2\u013c\u013f\5\34\17\2\u013d\u013f\5\24\13"+
		"\2\u013e\u013b\3\2\2\2\u013e\u013c\3\2\2\2\u013e\u013d\3\2\2\2\u013f/"+
		"\3\2\2\2.\639?HLRX[_fik{\u0080\u0085\u008f\u0092\u00a4\u00ac\u00b2\u00b6"+
		"\u00be\u00c5\u00d3\u00d6\u00da\u00df\u00e1\u00e6\u00eb\u00f3\u00f6\u00fa"+
		"\u00fc\u0100\u0106\u010e\u0112\u0118\u0121\u012a\u012f\u0131\u013e";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}