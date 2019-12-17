// Generated from /Users/yongchun.zyc/Documents/Drive/projects/hasor/hasor.git/hasor-dataql/src/main/java/net/hasor/dataql/compiler/parser/DataQLParser.g4 by ANTLR 4.7.2
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
		WS=1, COMMENT1=2, COMMENT2=3, EOL=4, AT=5, OPEN_TAG=6, IF=7, ELSE=8, RETURN=9, 
		THROW=10, EXIT=11, VAR=12, RUN=13, HINT=14, IMPORT=15, TRUE=16, FALSE=17, 
		NULL=18, AS=19, PLUS=20, MINUS=21, MUL=22, DIV=23, DIV2=24, MOD=25, LBT=26, 
		RBT=27, AND=28, OR=29, NOT=30, XOR=31, LSHIFT=32, RSHIFT=33, RSHIFT2=34, 
		GT=35, GE=36, LT=37, LE=38, EQ=39, NE=40, SC_OR=41, SC_AND=42, COMMA=43, 
		COLON=44, ASS=45, DOT=46, LSBT=47, RSBT=48, OCBR=49, CCBR=50, ROU=51, 
		QUE=52, SEM=53, CONVER=54, LAMBDA=55, STRING=56, HEX_NUM=57, OCT_NUM=58, 
		BIT_NUM=59, INTEGER_NUM=60, DECIMAL_NUM=61, IDENTIFIER=62, CLOS_TAG=63, 
		CHAR=64;
	public static final int
		RULE_rootInstSet = 0, RULE_hintInst = 1, RULE_importInst = 2, RULE_blockSet = 3, 
		RULE_ifInst = 4, RULE_breakInst = 5, RULE_lambdaDef = 6, RULE_varInst = 7, 
		RULE_runInst = 8, RULE_anyObject = 9, RULE_routeMapping = 10, RULE_routeNameSet = 11, 
		RULE_routeName = 12, RULE_routeSubscript = 13, RULE_funcCall = 14, RULE_funcCallResult = 15, 
		RULE_objectValue = 16, RULE_objectKeyValue = 17, RULE_listValue = 18, 
		RULE_primitiveValue = 19, RULE_expr = 20, RULE_dyadicExpr = 21, RULE_ternaryExpr = 22, 
		RULE_atomExpr = 23, RULE_extBlock = 24, RULE_extParams = 25;
	private static String[] makeRuleNames() {
		return new String[] {
			"rootInstSet", "hintInst", "importInst", "blockSet", "ifInst", "breakInst", 
			"lambdaDef", "varInst", "runInst", "anyObject", "routeMapping", "routeNameSet", 
			"routeName", "routeSubscript", "funcCall", "funcCallResult", "objectValue", 
			"objectKeyValue", "listValue", "primitiveValue", "expr", "dyadicExpr", 
			"ternaryExpr", "atomExpr", "extBlock", "extParams"
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
			"IDENTIFIER", "CLOS_TAG", "CHAR"
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
	public String getGrammarFileName() { return "DataQLParser.g4"; }

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
		public List<HintInstContext> hintInst() {
			return getRuleContexts(HintInstContext.class);
		}
		public HintInstContext hintInst(int i) {
			return getRuleContext(HintInstContext.class,i);
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitRootInstSet(this);
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
			setState(55);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==HINT) {
				{
				{
				setState(52);
				hintInst();
				}
				}
				setState(57);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(61);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IMPORT) {
				{
				{
				setState(58);
				importInst();
				}
				}
				setState(63);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(65); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(64);
				blockSet();
				}
				}
				setState(67); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IF) | (1L << RETURN) | (1L << THROW) | (1L << EXIT) | (1L << VAR) | (1L << RUN) | (1L << OCBR))) != 0) );
			setState(69);
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

	public static class HintInstContext extends ParserRuleContext {
		public TerminalNode HINT() { return getToken(DataQLParser.HINT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataQLParser.IDENTIFIER, 0); }
		public TerminalNode ASS() { return getToken(DataQLParser.ASS, 0); }
		public PrimitiveValueContext primitiveValue() {
			return getRuleContext(PrimitiveValueContext.class,0);
		}
		public TerminalNode SEM() { return getToken(DataQLParser.SEM, 0); }
		public HintInstContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hintInst; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitHintInst(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HintInstContext hintInst() throws RecognitionException {
		HintInstContext _localctx = new HintInstContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_hintInst);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(71);
			match(HINT);
			setState(72);
			match(IDENTIFIER);
			setState(73);
			match(ASS);
			setState(74);
			primitiveValue();
			setState(76);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEM) {
				{
				setState(75);
				match(SEM);
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
		public TerminalNode SEM() { return getToken(DataQLParser.SEM, 0); }
		public ImportInstContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importInst; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitImportInst(this);
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
			setState(78);
			match(IMPORT);
			setState(80);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ROU) {
				{
				setState(79);
				match(ROU);
				}
			}

			setState(82);
			match(STRING);
			setState(83);
			match(AS);
			setState(84);
			match(IDENTIFIER);
			setState(86);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEM) {
				{
				setState(85);
				match(SEM);
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
		public RunInstContext runInst() {
			return getRuleContext(RunInstContext.class,0);
		}
		public VarInstContext varInst() {
			return getRuleContext(VarInstContext.class,0);
		}
		public IfInstContext ifInst() {
			return getRuleContext(IfInstContext.class,0);
		}
		public BreakInstContext breakInst() {
			return getRuleContext(BreakInstContext.class,0);
		}
		public TerminalNode SEM() { return getToken(DataQLParser.SEM, 0); }
		public SingleInstContext(BlockSetContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitSingleInst(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MultipleInstContext extends BlockSetContext {
		public TerminalNode OCBR() { return getToken(DataQLParser.OCBR, 0); }
		public TerminalNode CCBR() { return getToken(DataQLParser.CCBR, 0); }
		public List<RunInstContext> runInst() {
			return getRuleContexts(RunInstContext.class);
		}
		public RunInstContext runInst(int i) {
			return getRuleContext(RunInstContext.class,i);
		}
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
		public List<TerminalNode> SEM() { return getTokens(DataQLParser.SEM); }
		public TerminalNode SEM(int i) {
			return getToken(DataQLParser.SEM, i);
		}
		public MultipleInstContext(BlockSetContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitMultipleInst(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockSetContext blockSet() throws RecognitionException {
		BlockSetContext _localctx = new BlockSetContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_blockSet);
		int _la;
		try {
			setState(113);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OCBR:
				_localctx = new MultipleInstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(88);
				match(OCBR);
				setState(98); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(93);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case RUN:
						{
						setState(89);
						runInst();
						}
						break;
					case VAR:
						{
						setState(90);
						varInst();
						}
						break;
					case IF:
						{
						setState(91);
						ifInst();
						}
						break;
					case RETURN:
					case THROW:
					case EXIT:
						{
						setState(92);
						breakInst();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(96);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==SEM) {
						{
						setState(95);
						match(SEM);
						}
					}

					}
					}
					setState(100); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IF) | (1L << RETURN) | (1L << THROW) | (1L << EXIT) | (1L << VAR) | (1L << RUN))) != 0) );
				setState(102);
				match(CCBR);
				}
				break;
			case IF:
			case RETURN:
			case THROW:
			case EXIT:
			case VAR:
			case RUN:
				_localctx = new SingleInstContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(108);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case RUN:
					{
					setState(104);
					runInst();
					}
					break;
				case VAR:
					{
					setState(105);
					varInst();
					}
					break;
				case IF:
					{
					setState(106);
					ifInst();
					}
					break;
				case RETURN:
				case THROW:
				case EXIT:
					{
					setState(107);
					breakInst();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(111);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
				case 1:
					{
					setState(110);
					match(SEM);
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitIfInst(this);
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
			setState(115);
			match(IF);
			setState(116);
			match(LBT);
			setState(117);
			expr();
			setState(118);
			match(RBT);
			setState(119);
			blockSet();
			setState(129);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(120);
					match(ELSE);
					setState(121);
					match(IF);
					setState(122);
					match(LBT);
					setState(123);
					expr();
					setState(124);
					match(RBT);
					setState(125);
					blockSet();
					}
					} 
				}
				setState(131);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			}
			setState(134);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(132);
				match(ELSE);
				setState(133);
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitBreakInst(this);
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
			setState(136);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RETURN) | (1L << THROW) | (1L << EXIT))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(139);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				setState(137);
				match(INTEGER_NUM);
				setState(138);
				match(COMMA);
				}
				break;
			}
			setState(141);
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
		public TerminalNode LAMBDA() { return getToken(DataQLParser.LAMBDA, 0); }
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitLambdaDef(this);
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
			setState(143);
			match(LBT);
			setState(152);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(144);
				match(IDENTIFIER);
				setState(149);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(145);
					match(COMMA);
					setState(146);
					match(IDENTIFIER);
					}
					}
					setState(151);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(154);
			match(RBT);
			setState(155);
			match(LAMBDA);
			setState(156);
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitVarInst(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarInstContext varInst() throws RecognitionException {
		VarInstContext _localctx = new VarInstContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_varInst);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(158);
			match(VAR);
			setState(159);
			match(IDENTIFIER);
			setState(160);
			match(ASS);
			setState(161);
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

	public static class RunInstContext extends ParserRuleContext {
		public TerminalNode RUN() { return getToken(DataQLParser.RUN, 0); }
		public AnyObjectContext anyObject() {
			return getRuleContext(AnyObjectContext.class,0);
		}
		public RunInstContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_runInst; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitRunInst(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RunInstContext runInst() throws RecognitionException {
		RunInstContext _localctx = new RunInstContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_runInst);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(163);
			match(RUN);
			setState(164);
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
		public ExtBlockContext extBlock() {
			return getRuleContext(ExtBlockContext.class,0);
		}
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitAnyObject(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AnyObjectContext anyObject() throws RecognitionException {
		AnyObjectContext _localctx = new AnyObjectContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_anyObject);
		try {
			setState(174);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(166);
				extBlock();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(167);
				lambdaDef();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(168);
				primitiveValue();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(169);
				objectValue();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(170);
				listValue();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(171);
				funcCall();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(172);
				routeMapping(0);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(173);
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
		public RouteSubscriptContext routeSubscript() {
			return getRuleContext(RouteSubscriptContext.class,0);
		}
		public TerminalNode DOT() { return getToken(DataQLParser.DOT, 0); }
		public RouteNameSetContext routeNameSet() {
			return getRuleContext(RouteNameSetContext.class,0);
		}
		public SpecialRouteContext(RouteMappingContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitSpecialRoute(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NormalRouteContext extends RouteMappingContext {
		public RouteNameSetContext routeNameSet() {
			return getRuleContext(RouteNameSetContext.class,0);
		}
		public TerminalNode ROU() { return getToken(DataQLParser.ROU, 0); }
		public TerminalNode DOT() { return getToken(DataQLParser.DOT, 0); }
		public NormalRouteContext(RouteMappingContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitNormalRoute(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConvertRouteContext extends RouteMappingContext {
		public RouteMappingContext routeMapping() {
			return getRuleContext(RouteMappingContext.class,0);
		}
		public TerminalNode CONVER() { return getToken(DataQLParser.CONVER, 0); }
		public ObjectValueContext objectValue() {
			return getRuleContext(ObjectValueContext.class,0);
		}
		public ListValueContext listValue() {
			return getRuleContext(ListValueContext.class,0);
		}
		public ConvertRouteContext(RouteMappingContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitConvertRoute(this);
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
		int _startState = 20;
		enterRecursionRule(_localctx, 20, RULE_routeMapping, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(199);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				{
				_localctx = new SpecialRouteContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(177);
				match(ROU);
				setState(178);
				match(OCBR);
				setState(179);
				_la = _input.LA(1);
				if ( !(_la==STRING || _la==IDENTIFIER) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(180);
				match(CCBR);
				setState(182);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
				case 1:
					{
					setState(181);
					routeSubscript();
					}
					break;
				}
				setState(186);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
				case 1:
					{
					setState(184);
					match(DOT);
					setState(185);
					routeNameSet();
					}
					break;
				}
				}
				break;
			case 2:
				{
				_localctx = new NormalRouteContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(197);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
				case 1:
					{
					{
					setState(189);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ROU) {
						{
						setState(188);
						match(ROU);
						}
					}

					setState(191);
					routeNameSet();
					}
					}
					break;
				case 2:
					{
					{
					setState(192);
					match(ROU);
					setState(195);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
					case 1:
						{
						setState(193);
						match(DOT);
						setState(194);
						routeNameSet();
						}
						break;
					}
					}
					}
					break;
				}
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(209);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ConvertRouteContext(new RouteMappingContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_routeMapping);
					setState(201);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(202);
					match(CONVER);
					setState(205);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case OCBR:
						{
						setState(203);
						objectValue();
						}
						break;
					case LSBT:
						{
						setState(204);
						listValue();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					}
					} 
				}
				setState(211);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitRouteNameSet(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RouteNameSetContext routeNameSet() throws RecognitionException {
		RouteNameSetContext _localctx = new RouteNameSetContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_routeNameSet);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(212);
			routeName();
			setState(217);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(213);
					match(DOT);
					setState(214);
					routeName();
					}
					} 
				}
				setState(219);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitRouteName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RouteNameContext routeName() throws RecognitionException {
		RouteNameContext _localctx = new RouteNameContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_routeName);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(220);
			match(IDENTIFIER);
			setState(224);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(221);
					routeSubscript();
					}
					} 
				}
				setState(226);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitRouteSubscript(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RouteSubscriptContext routeSubscript() throws RecognitionException {
		RouteSubscriptContext _localctx = new RouteSubscriptContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_routeSubscript);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(227);
			match(LSBT);
			setState(228);
			_la = _input.LA(1);
			if ( !(_la==STRING || _la==INTEGER_NUM) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(229);
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitFuncCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuncCallContext funcCall() throws RecognitionException {
		FuncCallContext _localctx = new FuncCallContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_funcCall);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(231);
			routeMapping(0);
			setState(232);
			match(LBT);
			setState(241);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AT) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << PLUS) | (1L << MINUS) | (1L << LBT) | (1L << NOT) | (1L << LSBT) | (1L << OCBR) | (1L << ROU) | (1L << STRING) | (1L << HEX_NUM) | (1L << OCT_NUM) | (1L << BIT_NUM) | (1L << INTEGER_NUM) | (1L << DECIMAL_NUM) | (1L << IDENTIFIER))) != 0)) {
				{
				setState(233);
				anyObject();
				setState(238);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(234);
					match(COMMA);
					setState(235);
					anyObject();
					}
					}
					setState(240);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(243);
			match(RBT);
			setState(245);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				{
				setState(244);
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
	public static class FuncCallResult_route2Context extends FuncCallResultContext {
		public List<RouteSubscriptContext> routeSubscript() {
			return getRuleContexts(RouteSubscriptContext.class);
		}
		public RouteSubscriptContext routeSubscript(int i) {
			return getRuleContext(RouteSubscriptContext.class,i);
		}
		public TerminalNode DOT() { return getToken(DataQLParser.DOT, 0); }
		public RouteNameSetContext routeNameSet() {
			return getRuleContext(RouteNameSetContext.class,0);
		}
		public FuncCallResultContext funcCallResult() {
			return getRuleContext(FuncCallResultContext.class,0);
		}
		public FuncCallResult_route2Context(FuncCallResultContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitFuncCallResult_route2(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FuncCallResult_convertContext extends FuncCallResultContext {
		public TerminalNode CONVER() { return getToken(DataQLParser.CONVER, 0); }
		public ObjectValueContext objectValue() {
			return getRuleContext(ObjectValueContext.class,0);
		}
		public ListValueContext listValue() {
			return getRuleContext(ListValueContext.class,0);
		}
		public FuncCallResult_convertContext(FuncCallResultContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitFuncCallResult_convert(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FuncCallResult_route1Context extends FuncCallResultContext {
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
		public FuncCallResult_route1Context(FuncCallResultContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitFuncCallResult_route1(this);
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitFuncCallResult_call(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuncCallResultContext funcCallResult() throws RecognitionException {
		FuncCallResultContext _localctx = new FuncCallResultContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_funcCallResult);
		int _la;
		try {
			int _alt;
			setState(291);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
			case 1:
				_localctx = new FuncCallResult_route1Context(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(252);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LSBT) {
					{
					setState(248); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(247);
						routeSubscript();
						}
						}
						setState(250); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==LSBT );
					}
				}

				setState(254);
				match(DOT);
				setState(255);
				routeNameSet();
				setState(257);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
				case 1:
					{
					setState(256);
					funcCallResult();
					}
					break;
				}
				}
				break;
			case 2:
				_localctx = new FuncCallResult_route2Context(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(260); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(259);
						routeSubscript();
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(262); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(266);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
				case 1:
					{
					setState(264);
					match(DOT);
					setState(265);
					routeNameSet();
					}
					break;
				}
				setState(269);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
				case 1:
					{
					setState(268);
					funcCallResult();
					}
					break;
				}
				}
				break;
			case 3:
				_localctx = new FuncCallResult_convertContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(271);
				match(CONVER);
				setState(274);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case OCBR:
					{
					setState(272);
					objectValue();
					}
					break;
				case LSBT:
					{
					setState(273);
					listValue();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case 4:
				_localctx = new FuncCallResult_callContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(276);
				match(LBT);
				setState(285);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AT) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << PLUS) | (1L << MINUS) | (1L << LBT) | (1L << NOT) | (1L << LSBT) | (1L << OCBR) | (1L << ROU) | (1L << STRING) | (1L << HEX_NUM) | (1L << OCT_NUM) | (1L << BIT_NUM) | (1L << INTEGER_NUM) | (1L << DECIMAL_NUM) | (1L << IDENTIFIER))) != 0)) {
					{
					setState(277);
					anyObject();
					setState(282);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(278);
						match(COMMA);
						setState(279);
						anyObject();
						}
						}
						setState(284);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(287);
				match(RBT);
				setState(289);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
				case 1:
					{
					setState(288);
					funcCallResult();
					}
					break;
				}
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitObjectValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectValueContext objectValue() throws RecognitionException {
		ObjectValueContext _localctx = new ObjectValueContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_objectValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(293);
			match(OCBR);
			setState(295);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING) {
				{
				setState(294);
				objectKeyValue();
				}
			}

			setState(301);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(297);
				match(COMMA);
				setState(298);
				objectKeyValue();
				}
				}
				setState(303);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(304);
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitObjectKeyValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectKeyValueContext objectKeyValue() throws RecognitionException {
		ObjectKeyValueContext _localctx = new ObjectKeyValueContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_objectKeyValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(306);
			match(STRING);
			setState(309);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(307);
				match(COLON);
				setState(308);
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitListValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListValueContext listValue() throws RecognitionException {
		ListValueContext _localctx = new ListValueContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_listValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(311);
			match(LSBT);
			setState(313);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AT) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << PLUS) | (1L << MINUS) | (1L << LBT) | (1L << NOT) | (1L << LSBT) | (1L << OCBR) | (1L << ROU) | (1L << STRING) | (1L << HEX_NUM) | (1L << OCT_NUM) | (1L << BIT_NUM) | (1L << INTEGER_NUM) | (1L << DECIMAL_NUM) | (1L << IDENTIFIER))) != 0)) {
				{
				setState(312);
				anyObject();
				}
			}

			setState(319);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(315);
				match(COMMA);
				setState(316);
				anyObject();
				}
				}
				setState(321);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(322);
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitStringValue(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BooleanValueContext extends PrimitiveValueContext {
		public TerminalNode TRUE() { return getToken(DataQLParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(DataQLParser.FALSE, 0); }
		public BooleanValueContext(PrimitiveValueContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitBooleanValue(this);
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitNumberValue(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NullValueContext extends PrimitiveValueContext {
		public TerminalNode NULL() { return getToken(DataQLParser.NULL, 0); }
		public NullValueContext(PrimitiveValueContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitNullValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimitiveValueContext primitiveValue() throws RecognitionException {
		PrimitiveValueContext _localctx = new PrimitiveValueContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_primitiveValue);
		int _la;
		try {
			setState(328);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING:
				_localctx = new StringValueContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(324);
				match(STRING);
				}
				break;
			case NULL:
				_localctx = new NullValueContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(325);
				match(NULL);
				}
				break;
			case TRUE:
			case FALSE:
				_localctx = new BooleanValueContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(326);
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
				setState(327);
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitUnaryExpr(this);
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitPrivilegeExpr(this);
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitMultipleExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_expr);
		int _la;
		try {
			setState(344);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PLUS:
			case MINUS:
			case NOT:
				_localctx = new UnaryExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(330);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << NOT))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(331);
				expr();
				}
				break;
			case LBT:
				_localctx = new PrivilegeExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(332);
				match(LBT);
				setState(333);
				expr();
				setState(334);
				match(RBT);
				setState(337);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
				case 1:
					{
					setState(335);
					dyadicExpr();
					}
					break;
				case 2:
					{
					setState(336);
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
				setState(339);
				atomExpr();
				setState(342);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
				case 1:
					{
					setState(340);
					dyadicExpr();
					}
					break;
				case 2:
					{
					setState(341);
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitDyadicExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DyadicExprContext dyadicExpr() throws RecognitionException {
		DyadicExprContext _localctx = new DyadicExprContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_dyadicExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(346);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << MUL) | (1L << DIV) | (1L << DIV2) | (1L << MOD) | (1L << LBT) | (1L << RBT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << XOR) | (1L << LSHIFT) | (1L << RSHIFT) | (1L << RSHIFT2) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << SC_OR) | (1L << SC_AND))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(347);
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
		public TerminalNode QUE() { return getToken(DataQLParser.QUE, 0); }
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitTernaryExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TernaryExprContext ternaryExpr() throws RecognitionException {
		TernaryExprContext _localctx = new TernaryExprContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_ternaryExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(349);
			match(QUE);
			setState(350);
			expr();
			setState(351);
			match(COLON);
			setState(352);
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
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitAtomExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AtomExprContext atomExpr() throws RecognitionException {
		AtomExprContext _localctx = new AtomExprContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_atomExpr);
		try {
			setState(357);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(354);
				primitiveValue();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(355);
				funcCall();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(356);
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

	public static class ExtBlockContext extends ParserRuleContext {
		public TerminalNode AT() { return getToken(DataQLParser.AT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataQLParser.IDENTIFIER, 0); }
		public TerminalNode LBT() { return getToken(DataQLParser.LBT, 0); }
		public TerminalNode RBT() { return getToken(DataQLParser.RBT, 0); }
		public TerminalNode OPEN_TAG() { return getToken(DataQLParser.OPEN_TAG, 0); }
		public TerminalNode CLOS_TAG() { return getToken(DataQLParser.CLOS_TAG, 0); }
		public ExtParamsContext extParams() {
			return getRuleContext(ExtParamsContext.class,0);
		}
		public List<TerminalNode> CHAR() { return getTokens(DataQLParser.CHAR); }
		public TerminalNode CHAR(int i) {
			return getToken(DataQLParser.CHAR, i);
		}
		public ExtBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extBlock; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitExtBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExtBlockContext extBlock() throws RecognitionException {
		ExtBlockContext _localctx = new ExtBlockContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_extBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(359);
			match(AT);
			setState(360);
			match(IDENTIFIER);
			setState(361);
			match(LBT);
			setState(363);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(362);
				extParams();
				}
			}

			setState(365);
			match(RBT);
			setState(366);
			match(OPEN_TAG);
			setState(370);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CHAR) {
				{
				{
				setState(367);
				match(CHAR);
				}
				}
				setState(372);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(373);
			match(CLOS_TAG);
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

	public static class ExtParamsContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(DataQLParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(DataQLParser.IDENTIFIER, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(DataQLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DataQLParser.COMMA, i);
		}
		public ExtParamsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extParams; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLParserVisitor ) return ((DataQLParserVisitor<? extends T>)visitor).visitExtParams(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExtParamsContext extParams() throws RecognitionException {
		ExtParamsContext _localctx = new ExtParamsContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_extParams);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(375);
			match(IDENTIFIER);
			setState(380);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(376);
				match(COMMA);
				setState(377);
				match(IDENTIFIER);
				}
				}
				setState(382);
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 10:
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3B\u0182\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\3\2\7\28\n\2\f\2\16\2;\13\2\3\2\7\2>\n\2\f\2\16\2"+
		"A\13\2\3\2\6\2D\n\2\r\2\16\2E\3\2\3\2\3\3\3\3\3\3\3\3\3\3\5\3O\n\3\3\4"+
		"\3\4\5\4S\n\4\3\4\3\4\3\4\3\4\5\4Y\n\4\3\5\3\5\3\5\3\5\3\5\5\5`\n\5\3"+
		"\5\5\5c\n\5\6\5e\n\5\r\5\16\5f\3\5\3\5\3\5\3\5\3\5\3\5\5\5o\n\5\3\5\5"+
		"\5r\n\5\5\5t\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\7\6\u0082"+
		"\n\6\f\6\16\6\u0085\13\6\3\6\3\6\5\6\u0089\n\6\3\7\3\7\3\7\5\7\u008e\n"+
		"\7\3\7\3\7\3\b\3\b\3\b\3\b\7\b\u0096\n\b\f\b\16\b\u0099\13\b\5\b\u009b"+
		"\n\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3\13\3"+
		"\13\3\13\3\13\3\13\3\13\5\13\u00b1\n\13\3\f\3\f\3\f\3\f\3\f\3\f\5\f\u00b9"+
		"\n\f\3\f\3\f\5\f\u00bd\n\f\3\f\5\f\u00c0\n\f\3\f\3\f\3\f\3\f\5\f\u00c6"+
		"\n\f\5\f\u00c8\n\f\5\f\u00ca\n\f\3\f\3\f\3\f\3\f\5\f\u00d0\n\f\7\f\u00d2"+
		"\n\f\f\f\16\f\u00d5\13\f\3\r\3\r\3\r\7\r\u00da\n\r\f\r\16\r\u00dd\13\r"+
		"\3\16\3\16\7\16\u00e1\n\16\f\16\16\16\u00e4\13\16\3\17\3\17\3\17\3\17"+
		"\3\20\3\20\3\20\3\20\3\20\7\20\u00ef\n\20\f\20\16\20\u00f2\13\20\5\20"+
		"\u00f4\n\20\3\20\3\20\5\20\u00f8\n\20\3\21\6\21\u00fb\n\21\r\21\16\21"+
		"\u00fc\5\21\u00ff\n\21\3\21\3\21\3\21\5\21\u0104\n\21\3\21\6\21\u0107"+
		"\n\21\r\21\16\21\u0108\3\21\3\21\5\21\u010d\n\21\3\21\5\21\u0110\n\21"+
		"\3\21\3\21\3\21\5\21\u0115\n\21\3\21\3\21\3\21\3\21\7\21\u011b\n\21\f"+
		"\21\16\21\u011e\13\21\5\21\u0120\n\21\3\21\3\21\5\21\u0124\n\21\5\21\u0126"+
		"\n\21\3\22\3\22\5\22\u012a\n\22\3\22\3\22\7\22\u012e\n\22\f\22\16\22\u0131"+
		"\13\22\3\22\3\22\3\23\3\23\3\23\5\23\u0138\n\23\3\24\3\24\5\24\u013c\n"+
		"\24\3\24\3\24\7\24\u0140\n\24\f\24\16\24\u0143\13\24\3\24\3\24\3\25\3"+
		"\25\3\25\3\25\5\25\u014b\n\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\5\26"+
		"\u0154\n\26\3\26\3\26\3\26\5\26\u0159\n\26\5\26\u015b\n\26\3\27\3\27\3"+
		"\27\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\5\31\u0168\n\31\3\32\3\32"+
		"\3\32\3\32\5\32\u016e\n\32\3\32\3\32\3\32\7\32\u0173\n\32\f\32\16\32\u0176"+
		"\13\32\3\32\3\32\3\33\3\33\3\33\7\33\u017d\n\33\f\33\16\33\u0180\13\33"+
		"\3\33\2\3\26\34\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62"+
		"\64\2\t\3\2\13\r\4\2::@@\4\2::>>\3\2\22\23\3\2;?\4\2\26\27  \3\2\26,\2"+
		"\u01b0\29\3\2\2\2\4I\3\2\2\2\6P\3\2\2\2\bs\3\2\2\2\nu\3\2\2\2\f\u008a"+
		"\3\2\2\2\16\u0091\3\2\2\2\20\u00a0\3\2\2\2\22\u00a5\3\2\2\2\24\u00b0\3"+
		"\2\2\2\26\u00c9\3\2\2\2\30\u00d6\3\2\2\2\32\u00de\3\2\2\2\34\u00e5\3\2"+
		"\2\2\36\u00e9\3\2\2\2 \u0125\3\2\2\2\"\u0127\3\2\2\2$\u0134\3\2\2\2&\u0139"+
		"\3\2\2\2(\u014a\3\2\2\2*\u015a\3\2\2\2,\u015c\3\2\2\2.\u015f\3\2\2\2\60"+
		"\u0167\3\2\2\2\62\u0169\3\2\2\2\64\u0179\3\2\2\2\668\5\4\3\2\67\66\3\2"+
		"\2\28;\3\2\2\29\67\3\2\2\29:\3\2\2\2:?\3\2\2\2;9\3\2\2\2<>\5\6\4\2=<\3"+
		"\2\2\2>A\3\2\2\2?=\3\2\2\2?@\3\2\2\2@C\3\2\2\2A?\3\2\2\2BD\5\b\5\2CB\3"+
		"\2\2\2DE\3\2\2\2EC\3\2\2\2EF\3\2\2\2FG\3\2\2\2GH\7\2\2\3H\3\3\2\2\2IJ"+
		"\7\20\2\2JK\7@\2\2KL\7/\2\2LN\5(\25\2MO\7\67\2\2NM\3\2\2\2NO\3\2\2\2O"+
		"\5\3\2\2\2PR\7\21\2\2QS\7\65\2\2RQ\3\2\2\2RS\3\2\2\2ST\3\2\2\2TU\7:\2"+
		"\2UV\7\25\2\2VX\7@\2\2WY\7\67\2\2XW\3\2\2\2XY\3\2\2\2Y\7\3\2\2\2Zd\7\63"+
		"\2\2[`\5\22\n\2\\`\5\20\t\2]`\5\n\6\2^`\5\f\7\2_[\3\2\2\2_\\\3\2\2\2_"+
		"]\3\2\2\2_^\3\2\2\2`b\3\2\2\2ac\7\67\2\2ba\3\2\2\2bc\3\2\2\2ce\3\2\2\2"+
		"d_\3\2\2\2ef\3\2\2\2fd\3\2\2\2fg\3\2\2\2gh\3\2\2\2hi\7\64\2\2it\3\2\2"+
		"\2jo\5\22\n\2ko\5\20\t\2lo\5\n\6\2mo\5\f\7\2nj\3\2\2\2nk\3\2\2\2nl\3\2"+
		"\2\2nm\3\2\2\2oq\3\2\2\2pr\7\67\2\2qp\3\2\2\2qr\3\2\2\2rt\3\2\2\2sZ\3"+
		"\2\2\2sn\3\2\2\2t\t\3\2\2\2uv\7\t\2\2vw\7\34\2\2wx\5*\26\2xy\7\35\2\2"+
		"y\u0083\5\b\5\2z{\7\n\2\2{|\7\t\2\2|}\7\34\2\2}~\5*\26\2~\177\7\35\2\2"+
		"\177\u0080\5\b\5\2\u0080\u0082\3\2\2\2\u0081z\3\2\2\2\u0082\u0085\3\2"+
		"\2\2\u0083\u0081\3\2\2\2\u0083\u0084\3\2\2\2\u0084\u0088\3\2\2\2\u0085"+
		"\u0083\3\2\2\2\u0086\u0087\7\n\2\2\u0087\u0089\5\b\5\2\u0088\u0086\3\2"+
		"\2\2\u0088\u0089\3\2\2\2\u0089\13\3\2\2\2\u008a\u008d\t\2\2\2\u008b\u008c"+
		"\7>\2\2\u008c\u008e\7-\2\2\u008d\u008b\3\2\2\2\u008d\u008e\3\2\2\2\u008e"+
		"\u008f\3\2\2\2\u008f\u0090\5\24\13\2\u0090\r\3\2\2\2\u0091\u009a\7\34"+
		"\2\2\u0092\u0097\7@\2\2\u0093\u0094\7-\2\2\u0094\u0096\7@\2\2\u0095\u0093"+
		"\3\2\2\2\u0096\u0099\3\2\2\2\u0097\u0095\3\2\2\2\u0097\u0098\3\2\2\2\u0098"+
		"\u009b\3\2\2\2\u0099\u0097\3\2\2\2\u009a\u0092\3\2\2\2\u009a\u009b\3\2"+
		"\2\2\u009b\u009c\3\2\2\2\u009c\u009d\7\35\2\2\u009d\u009e\79\2\2\u009e"+
		"\u009f\5\b\5\2\u009f\17\3\2\2\2\u00a0\u00a1\7\16\2\2\u00a1\u00a2\7@\2"+
		"\2\u00a2\u00a3\7/\2\2\u00a3\u00a4\5\24\13\2\u00a4\21\3\2\2\2\u00a5\u00a6"+
		"\7\17\2\2\u00a6\u00a7\5\24\13\2\u00a7\23\3\2\2\2\u00a8\u00b1\5\62\32\2"+
		"\u00a9\u00b1\5\16\b\2\u00aa\u00b1\5(\25\2\u00ab\u00b1\5\"\22\2\u00ac\u00b1"+
		"\5&\24\2\u00ad\u00b1\5\36\20\2\u00ae\u00b1\5\26\f\2\u00af\u00b1\5*\26"+
		"\2\u00b0\u00a8\3\2\2\2\u00b0\u00a9\3\2\2\2\u00b0\u00aa\3\2\2\2\u00b0\u00ab"+
		"\3\2\2\2\u00b0\u00ac\3\2\2\2\u00b0\u00ad\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b0"+
		"\u00af\3\2\2\2\u00b1\25\3\2\2\2\u00b2\u00b3\b\f\1\2\u00b3\u00b4\7\65\2"+
		"\2\u00b4\u00b5\7\63\2\2\u00b5\u00b6\t\3\2\2\u00b6\u00b8\7\64\2\2\u00b7"+
		"\u00b9\5\34\17\2\u00b8\u00b7\3\2\2\2\u00b8\u00b9\3\2\2\2\u00b9\u00bc\3"+
		"\2\2\2\u00ba\u00bb\7\60\2\2\u00bb\u00bd\5\30\r\2\u00bc\u00ba\3\2\2\2\u00bc"+
		"\u00bd\3\2\2\2\u00bd\u00ca\3\2\2\2\u00be\u00c0\7\65\2\2\u00bf\u00be\3"+
		"\2\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00c1\3\2\2\2\u00c1\u00c8\5\30\r\2\u00c2"+
		"\u00c5\7\65\2\2\u00c3\u00c4\7\60\2\2\u00c4\u00c6\5\30\r\2\u00c5\u00c3"+
		"\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6\u00c8\3\2\2\2\u00c7\u00bf\3\2\2\2\u00c7"+
		"\u00c2\3\2\2\2\u00c8\u00ca\3\2\2\2\u00c9\u00b2\3\2\2\2\u00c9\u00c7\3\2"+
		"\2\2\u00ca\u00d3\3\2\2\2\u00cb\u00cc\f\3\2\2\u00cc\u00cf\78\2\2\u00cd"+
		"\u00d0\5\"\22\2\u00ce\u00d0\5&\24\2\u00cf\u00cd\3\2\2\2\u00cf\u00ce\3"+
		"\2\2\2\u00d0\u00d2\3\2\2\2\u00d1\u00cb\3\2\2\2\u00d2\u00d5\3\2\2\2\u00d3"+
		"\u00d1\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4\27\3\2\2\2\u00d5\u00d3\3\2\2"+
		"\2\u00d6\u00db\5\32\16\2\u00d7\u00d8\7\60\2\2\u00d8\u00da\5\32\16\2\u00d9"+
		"\u00d7\3\2\2\2\u00da\u00dd\3\2\2\2\u00db\u00d9\3\2\2\2\u00db\u00dc\3\2"+
		"\2\2\u00dc\31\3\2\2\2\u00dd\u00db\3\2\2\2\u00de\u00e2\7@\2\2\u00df\u00e1"+
		"\5\34\17\2\u00e0\u00df\3\2\2\2\u00e1\u00e4\3\2\2\2\u00e2\u00e0\3\2\2\2"+
		"\u00e2\u00e3\3\2\2\2\u00e3\33\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e5\u00e6"+
		"\7\61\2\2\u00e6\u00e7\t\4\2\2\u00e7\u00e8\7\62\2\2\u00e8\35\3\2\2\2\u00e9"+
		"\u00ea\5\26\f\2\u00ea\u00f3\7\34\2\2\u00eb\u00f0\5\24\13\2\u00ec\u00ed"+
		"\7-\2\2\u00ed\u00ef\5\24\13\2\u00ee\u00ec\3\2\2\2\u00ef\u00f2\3\2\2\2"+
		"\u00f0\u00ee\3\2\2\2\u00f0\u00f1\3\2\2\2\u00f1\u00f4\3\2\2\2\u00f2\u00f0"+
		"\3\2\2\2\u00f3\u00eb\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\u00f5\3\2\2\2\u00f5"+
		"\u00f7\7\35\2\2\u00f6\u00f8\5 \21\2\u00f7\u00f6\3\2\2\2\u00f7\u00f8\3"+
		"\2\2\2\u00f8\37\3\2\2\2\u00f9\u00fb\5\34\17\2\u00fa\u00f9\3\2\2\2\u00fb"+
		"\u00fc\3\2\2\2\u00fc\u00fa\3\2\2\2\u00fc\u00fd\3\2\2\2\u00fd\u00ff\3\2"+
		"\2\2\u00fe\u00fa\3\2\2\2\u00fe\u00ff\3\2\2\2\u00ff\u0100\3\2\2\2\u0100"+
		"\u0101\7\60\2\2\u0101\u0103\5\30\r\2\u0102\u0104\5 \21\2\u0103\u0102\3"+
		"\2\2\2\u0103\u0104\3\2\2\2\u0104\u0126\3\2\2\2\u0105\u0107\5\34\17\2\u0106"+
		"\u0105\3\2\2\2\u0107\u0108\3\2\2\2\u0108\u0106\3\2\2\2\u0108\u0109\3\2"+
		"\2\2\u0109\u010c\3\2\2\2\u010a\u010b\7\60\2\2\u010b\u010d\5\30\r\2\u010c"+
		"\u010a\3\2\2\2\u010c\u010d\3\2\2\2\u010d\u010f\3\2\2\2\u010e\u0110\5 "+
		"\21\2\u010f\u010e\3\2\2\2\u010f\u0110\3\2\2\2\u0110\u0126\3\2\2\2\u0111"+
		"\u0114\78\2\2\u0112\u0115\5\"\22\2\u0113\u0115\5&\24\2\u0114\u0112\3\2"+
		"\2\2\u0114\u0113\3\2\2\2\u0115\u0126\3\2\2\2\u0116\u011f\7\34\2\2\u0117"+
		"\u011c\5\24\13\2\u0118\u0119\7-\2\2\u0119\u011b\5\24\13\2\u011a\u0118"+
		"\3\2\2\2\u011b\u011e\3\2\2\2\u011c\u011a\3\2\2\2\u011c\u011d\3\2\2\2\u011d"+
		"\u0120\3\2\2\2\u011e\u011c\3\2\2\2\u011f\u0117\3\2\2\2\u011f\u0120\3\2"+
		"\2\2\u0120\u0121\3\2\2\2\u0121\u0123\7\35\2\2\u0122\u0124\5 \21\2\u0123"+
		"\u0122\3\2\2\2\u0123\u0124\3\2\2\2\u0124\u0126\3\2\2\2\u0125\u00fe\3\2"+
		"\2\2\u0125\u0106\3\2\2\2\u0125\u0111\3\2\2\2\u0125\u0116\3\2\2\2\u0126"+
		"!\3\2\2\2\u0127\u0129\7\63\2\2\u0128\u012a\5$\23\2\u0129\u0128\3\2\2\2"+
		"\u0129\u012a\3\2\2\2\u012a\u012f\3\2\2\2\u012b\u012c\7-\2\2\u012c\u012e"+
		"\5$\23\2\u012d\u012b\3\2\2\2\u012e\u0131\3\2\2\2\u012f\u012d\3\2\2\2\u012f"+
		"\u0130\3\2\2\2\u0130\u0132\3\2\2\2\u0131\u012f\3\2\2\2\u0132\u0133\7\64"+
		"\2\2\u0133#\3\2\2\2\u0134\u0137\7:\2\2\u0135\u0136\7.\2\2\u0136\u0138"+
		"\5\24\13\2\u0137\u0135\3\2\2\2\u0137\u0138\3\2\2\2\u0138%\3\2\2\2\u0139"+
		"\u013b\7\61\2\2\u013a\u013c\5\24\13\2\u013b\u013a\3\2\2\2\u013b\u013c"+
		"\3\2\2\2\u013c\u0141\3\2\2\2\u013d\u013e\7-\2\2\u013e\u0140\5\24\13\2"+
		"\u013f\u013d\3\2\2\2\u0140\u0143\3\2\2\2\u0141\u013f\3\2\2\2\u0141\u0142"+
		"\3\2\2\2\u0142\u0144\3\2\2\2\u0143\u0141\3\2\2\2\u0144\u0145\7\62\2\2"+
		"\u0145\'\3\2\2\2\u0146\u014b\7:\2\2\u0147\u014b\7\24\2\2\u0148\u014b\t"+
		"\5\2\2\u0149\u014b\t\6\2\2\u014a\u0146\3\2\2\2\u014a\u0147\3\2\2\2\u014a"+
		"\u0148\3\2\2\2\u014a\u0149\3\2\2\2\u014b)\3\2\2\2\u014c\u014d\t\7\2\2"+
		"\u014d\u015b\5*\26\2\u014e\u014f\7\34\2\2\u014f\u0150\5*\26\2\u0150\u0153"+
		"\7\35\2\2\u0151\u0154\5,\27\2\u0152\u0154\5.\30\2\u0153\u0151\3\2\2\2"+
		"\u0153\u0152\3\2\2\2\u0153\u0154\3\2\2\2\u0154\u015b\3\2\2\2\u0155\u0158"+
		"\5\60\31\2\u0156\u0159\5,\27\2\u0157\u0159\5.\30\2\u0158\u0156\3\2\2\2"+
		"\u0158\u0157\3\2\2\2\u0158\u0159\3\2\2\2\u0159\u015b\3\2\2\2\u015a\u014c"+
		"\3\2\2\2\u015a\u014e\3\2\2\2\u015a\u0155\3\2\2\2\u015b+\3\2\2\2\u015c"+
		"\u015d\t\b\2\2\u015d\u015e\5*\26\2\u015e-\3\2\2\2\u015f\u0160\7\66\2\2"+
		"\u0160\u0161\5*\26\2\u0161\u0162\7.\2\2\u0162\u0163\5*\26\2\u0163/\3\2"+
		"\2\2\u0164\u0168\5(\25\2\u0165\u0168\5\36\20\2\u0166\u0168\5\26\f\2\u0167"+
		"\u0164\3\2\2\2\u0167\u0165\3\2\2\2\u0167\u0166\3\2\2\2\u0168\61\3\2\2"+
		"\2\u0169\u016a\7\7\2\2\u016a\u016b\7@\2\2\u016b\u016d\7\34\2\2\u016c\u016e"+
		"\5\64\33\2\u016d\u016c\3\2\2\2\u016d\u016e\3\2\2\2\u016e\u016f\3\2\2\2"+
		"\u016f\u0170\7\35\2\2\u0170\u0174\7\b\2\2\u0171\u0173\7B\2\2\u0172\u0171"+
		"\3\2\2\2\u0173\u0176\3\2\2\2\u0174\u0172\3\2\2\2\u0174\u0175\3\2\2\2\u0175"+
		"\u0177\3\2\2\2\u0176\u0174\3\2\2\2\u0177\u0178\7A\2\2\u0178\63\3\2\2\2"+
		"\u0179\u017e\7@\2\2\u017a\u017b\7-\2\2\u017b\u017d\7@\2\2\u017c\u017a"+
		"\3\2\2\2\u017d\u0180\3\2\2\2\u017e\u017c\3\2\2\2\u017e\u017f\3\2\2\2\u017f"+
		"\65\3\2\2\2\u0180\u017e\3\2\2\299?ENRX_bfnqs\u0083\u0088\u008d\u0097\u009a"+
		"\u00b0\u00b8\u00bc\u00bf\u00c5\u00c7\u00c9\u00cf\u00d3\u00db\u00e2\u00f0"+
		"\u00f3\u00f7\u00fc\u00fe\u0103\u0108\u010c\u010f\u0114\u011c\u011f\u0123"+
		"\u0125\u0129\u012f\u0137\u013b\u0141\u014a\u0153\u0158\u015a\u0167\u016d"+
		"\u0174\u017e";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}