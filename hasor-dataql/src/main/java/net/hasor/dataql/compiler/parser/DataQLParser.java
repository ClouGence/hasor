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
		RULE_varInst = 4, RULE_ifInst = 5, RULE_breakInst = 6, RULE_lambdaDef = 7, 
		RULE_lambdaDefParameters = 8, RULE_polymericObject = 9, RULE_objectValue = 10, 
		RULE_objectKeyValue = 11, RULE_listValue = 12, RULE_primitiveValue = 13, 
		RULE_funcCall = 14, RULE_funcCallResult = 15, RULE_routeCall = 16, RULE_normalRouteCopy = 17, 
		RULE_routeItem = 18, RULE_routeSubscript = 19, RULE_expr = 20, RULE_dyadicExpr = 21, 
		RULE_ternaryExpr = 22, RULE_atomExpr = 23;
	private static String[] makeRuleNames() {
		return new String[] {
			"rootInstSet", "optionInst", "importInst", "blockSet", "varInst", "ifInst", 
			"breakInst", "lambdaDef", "lambdaDefParameters", "polymericObject", "objectValue", 
			"objectKeyValue", "listValue", "primitiveValue", "funcCall", "funcCallResult", 
			"routeCall", "normalRouteCopy", "routeItem", "routeSubscript", "expr", 
			"dyadicExpr", "ternaryExpr", "atomExpr"
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
			setState(51);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OPTION) {
				{
				{
				setState(48);
				optionInst();
				}
				}
				setState(53);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(57);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IMPORT) {
				{
				{
				setState(54);
				importInst();
				}
				}
				setState(59);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(61); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(60);
				blockSet();
				}
				}
				setState(63); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IF) | (1L << RETURN) | (1L << THROW) | (1L << EXIT) | (1L << VAR) | (1L << OCBR))) != 0) );
			setState(65);
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
			setState(67);
			match(OPTION);
			setState(68);
			match(IDENTIFIER);
			setState(69);
			match(ASS);
			setState(70);
			primitiveValue();
			setState(72);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(71);
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
			setState(74);
			match(IMPORT);
			setState(76);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ROU) {
				{
				setState(75);
				match(ROU);
				}
			}

			setState(78);
			match(STRING);
			setState(79);
			match(AS);
			setState(80);
			match(IDENTIFIER);
			setState(82);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(81);
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
			setState(107);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OCBR:
				_localctx = new MultipleInstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(84);
				match(OCBR);
				setState(93); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(88);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case VAR:
						{
						setState(85);
						varInst();
						}
						break;
					case IF:
						{
						setState(86);
						ifInst();
						}
						break;
					case RETURN:
					case THROW:
					case EXIT:
						{
						setState(87);
						breakInst();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(91);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==T__0) {
						{
						setState(90);
						match(T__0);
						}
					}

					}
					}
					setState(95); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IF) | (1L << RETURN) | (1L << THROW) | (1L << EXIT) | (1L << VAR))) != 0) );
				setState(97);
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
				setState(102);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case VAR:
					{
					setState(99);
					varInst();
					}
					break;
				case IF:
					{
					setState(100);
					ifInst();
					}
					break;
				case RETURN:
				case THROW:
				case EXIT:
					{
					setState(101);
					breakInst();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(105);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
				case 1:
					{
					setState(104);
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

	public static class VarInstContext extends ParserRuleContext {
		public TerminalNode VAR() { return getToken(DataQLParser.VAR, 0); }
		public TerminalNode IDENTIFIER() { return getToken(DataQLParser.IDENTIFIER, 0); }
		public TerminalNode ASS() { return getToken(DataQLParser.ASS, 0); }
		public PolymericObjectContext polymericObject() {
			return getRuleContext(PolymericObjectContext.class,0);
		}
		public LambdaDefContext lambdaDef() {
			return getRuleContext(LambdaDefContext.class,0);
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
		enterRule(_localctx, 8, RULE_varInst);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(109);
			match(VAR);
			setState(110);
			match(IDENTIFIER);
			setState(111);
			match(ASS);
			setState(114);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				setState(112);
				polymericObject();
				}
				break;
			case 2:
				{
				setState(113);
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
		enterRule(_localctx, 10, RULE_ifInst);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(116);
			match(IF);
			setState(117);
			match(LBT);
			setState(118);
			expr();
			setState(119);
			match(RBT);
			setState(120);
			blockSet();
			setState(130);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(121);
					match(ELSE);
					setState(122);
					match(IF);
					setState(123);
					match(LBT);
					setState(124);
					expr();
					setState(125);
					match(RBT);
					setState(126);
					blockSet();
					}
					} 
				}
				setState(132);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			}
			setState(135);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				setState(133);
				match(ELSE);
				setState(134);
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
		enterRule(_localctx, 12, RULE_breakInst);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(137);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RETURN) | (1L << THROW) | (1L << EXIT))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(140);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				{
				setState(138);
				match(INTEGER_NUM);
				setState(139);
				match(COMMA);
				}
				break;
			}
			setState(144);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				{
				setState(142);
				polymericObject();
				}
				break;
			case 2:
				{
				setState(143);
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

	public static class LambdaDefContext extends ParserRuleContext {
		public TerminalNode LBT() { return getToken(DataQLParser.LBT, 0); }
		public TerminalNode RBT() { return getToken(DataQLParser.RBT, 0); }
		public BlockSetContext blockSet() {
			return getRuleContext(BlockSetContext.class,0);
		}
		public LambdaDefParametersContext lambdaDefParameters() {
			return getRuleContext(LambdaDefParametersContext.class,0);
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
		enterRule(_localctx, 14, RULE_lambdaDef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(146);
			match(LBT);
			setState(148);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(147);
				lambdaDefParameters();
				}
			}

			setState(150);
			match(RBT);
			setState(151);
			match(T__1);
			setState(152);
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

	public static class LambdaDefParametersContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(DataQLParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(DataQLParser.IDENTIFIER, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(DataQLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DataQLParser.COMMA, i);
		}
		public LambdaDefParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lambdaDefParameters; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitLambdaDefParameters(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LambdaDefParametersContext lambdaDefParameters() throws RecognitionException {
		LambdaDefParametersContext _localctx = new LambdaDefParametersContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_lambdaDefParameters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(154);
			match(IDENTIFIER);
			setState(159);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(155);
				match(COMMA);
				setState(156);
				match(IDENTIFIER);
				}
				}
				setState(161);
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

	public static class PolymericObjectContext extends ParserRuleContext {
		public PolymericObjectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_polymericObject; }
	 
		public PolymericObjectContext() { }
		public void copyFrom(PolymericObjectContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ConvertRawContext extends PolymericObjectContext {
		public PrimitiveValueContext primitiveValue() {
			return getRuleContext(PrimitiveValueContext.class,0);
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
		public LambdaDefContext lambdaDef() {
			return getRuleContext(LambdaDefContext.class,0);
		}
		public ConvertRawContext(PolymericObjectContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitConvertRaw(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConvertObjectContext extends PolymericObjectContext {
		public FuncCallContext funcCall() {
			return getRuleContext(FuncCallContext.class,0);
		}
		public RouteCallContext routeCall() {
			return getRuleContext(RouteCallContext.class,0);
		}
		public ObjectValueContext objectValue() {
			return getRuleContext(ObjectValueContext.class,0);
		}
		public ListValueContext listValue() {
			return getRuleContext(ListValueContext.class,0);
		}
		public ConvertObjectContext(PolymericObjectContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitConvertObject(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PolymericObjectContext polymericObject() throws RecognitionException {
		PolymericObjectContext _localctx = new PolymericObjectContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_polymericObject);
		try {
			setState(178);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				_localctx = new ConvertObjectContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(164);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
				case 1:
					{
					setState(162);
					funcCall();
					}
					break;
				case 2:
					{
					setState(163);
					routeCall();
					}
					break;
				}
				setState(166);
				match(T__2);
				setState(169);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case OCBR:
					{
					setState(167);
					objectValue();
					}
					break;
				case LSBT:
					{
					setState(168);
					listValue();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				}
				break;
			case 2:
				_localctx = new ConvertRawContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(176);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
				case 1:
					{
					setState(171);
					primitiveValue();
					}
					break;
				case 2:
					{
					setState(172);
					objectValue();
					}
					break;
				case 3:
					{
					setState(173);
					listValue();
					}
					break;
				case 4:
					{
					setState(174);
					expr();
					}
					break;
				case 5:
					{
					setState(175);
					lambdaDef();
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
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitObjectValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectValueContext objectValue() throws RecognitionException {
		ObjectValueContext _localctx = new ObjectValueContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_objectValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(180);
			match(OCBR);
			setState(182);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING) {
				{
				setState(181);
				objectKeyValue();
				}
			}

			setState(188);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(184);
				match(COMMA);
				setState(185);
				objectKeyValue();
				}
				}
				setState(190);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(191);
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
		public PolymericObjectContext polymericObject() {
			return getRuleContext(PolymericObjectContext.class,0);
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
		enterRule(_localctx, 22, RULE_objectKeyValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(193);
			match(STRING);
			setState(196);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(194);
				match(COLON);
				setState(195);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitListValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListValueContext listValue() throws RecognitionException {
		ListValueContext _localctx = new ListValueContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_listValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(198);
			match(LSBT);
			setState(200);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << PLUS) | (1L << MINUS) | (1L << LBT) | (1L << NOT) | (1L << LSBT) | (1L << OCBR) | (1L << ROU) | (1L << STRING) | (1L << HEX_NUM) | (1L << OCT_NUM) | (1L << BIT_NUM) | (1L << INTEGER_NUM) | (1L << DECIMAL_NUM) | (1L << IDENTIFIER))) != 0)) {
				{
				setState(199);
				polymericObject();
				}
			}

			setState(206);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(202);
				match(COMMA);
				setState(203);
				polymericObject();
				}
				}
				setState(208);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(209);
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
		enterRule(_localctx, 26, RULE_primitiveValue);
		int _la;
		try {
			setState(215);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING:
				_localctx = new StringValueContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(211);
				match(STRING);
				}
				break;
			case NULL:
				_localctx = new NullValueContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(212);
				match(NULL);
				}
				break;
			case TRUE:
			case FALSE:
				_localctx = new BooleanValueContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(213);
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
				setState(214);
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

	public static class FuncCallContext extends ParserRuleContext {
		public RouteCallContext routeCall() {
			return getRuleContext(RouteCallContext.class,0);
		}
		public TerminalNode LBT() { return getToken(DataQLParser.LBT, 0); }
		public TerminalNode RBT() { return getToken(DataQLParser.RBT, 0); }
		public List<PolymericObjectContext> polymericObject() {
			return getRuleContexts(PolymericObjectContext.class);
		}
		public PolymericObjectContext polymericObject(int i) {
			return getRuleContext(PolymericObjectContext.class,i);
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
		enterRule(_localctx, 28, RULE_funcCall);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(217);
			routeCall();
			setState(218);
			match(LBT);
			setState(227);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << PLUS) | (1L << MINUS) | (1L << LBT) | (1L << NOT) | (1L << LSBT) | (1L << OCBR) | (1L << ROU) | (1L << STRING) | (1L << HEX_NUM) | (1L << OCT_NUM) | (1L << BIT_NUM) | (1L << INTEGER_NUM) | (1L << DECIMAL_NUM) | (1L << IDENTIFIER))) != 0)) {
				{
				setState(219);
				polymericObject();
				setState(224);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(220);
					match(COMMA);
					setState(221);
					polymericObject();
					}
					}
					setState(226);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(229);
			match(RBT);
			setState(231);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DOT || _la==LSBT) {
				{
				setState(230);
				funcCallResult();
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

	public static class FuncCallResultContext extends ParserRuleContext {
		public TerminalNode DOT() { return getToken(DataQLParser.DOT, 0); }
		public NormalRouteCopyContext normalRouteCopy() {
			return getRuleContext(NormalRouteCopyContext.class,0);
		}
		public List<RouteSubscriptContext> routeSubscript() {
			return getRuleContexts(RouteSubscriptContext.class);
		}
		public RouteSubscriptContext routeSubscript(int i) {
			return getRuleContext(RouteSubscriptContext.class,i);
		}
		public FuncCallResultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcCallResult; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitFuncCallResult(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuncCallResultContext funcCallResult() throws RecognitionException {
		FuncCallResultContext _localctx = new FuncCallResultContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_funcCallResult);
		int _la;
		try {
			setState(240);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DOT:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(233);
				match(DOT);
				setState(234);
				normalRouteCopy();
				}
				}
				break;
			case LSBT:
				enterOuterAlt(_localctx, 2);
				{
				setState(236); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(235);
					routeSubscript();
					}
					}
					setState(238); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==LSBT );
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

	public static class RouteCallContext extends ParserRuleContext {
		public RouteCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_routeCall; }
	 
		public RouteCallContext() { }
		public void copyFrom(RouteCallContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SpecialRouteContext extends RouteCallContext {
		public TerminalNode ROU() { return getToken(DataQLParser.ROU, 0); }
		public TerminalNode OCBR() { return getToken(DataQLParser.OCBR, 0); }
		public NormalRouteCopyContext normalRouteCopy() {
			return getRuleContext(NormalRouteCopyContext.class,0);
		}
		public TerminalNode CCBR() { return getToken(DataQLParser.CCBR, 0); }
		public SpecialRouteContext(RouteCallContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitSpecialRoute(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NormalRouteContext extends RouteCallContext {
		public NormalRouteCopyContext normalRouteCopy() {
			return getRuleContext(NormalRouteCopyContext.class,0);
		}
		public NormalRouteContext(RouteCallContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitNormalRoute(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RouteCallContext routeCall() throws RecognitionException {
		RouteCallContext _localctx = new RouteCallContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_routeCall);
		try {
			setState(248);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ROU:
				_localctx = new SpecialRouteContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(242);
				match(ROU);
				setState(243);
				match(OCBR);
				setState(244);
				normalRouteCopy();
				setState(245);
				match(CCBR);
				}
				break;
			case IDENTIFIER:
				_localctx = new NormalRouteContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(247);
				normalRouteCopy();
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

	public static class NormalRouteCopyContext extends ParserRuleContext {
		public List<RouteItemContext> routeItem() {
			return getRuleContexts(RouteItemContext.class);
		}
		public RouteItemContext routeItem(int i) {
			return getRuleContext(RouteItemContext.class,i);
		}
		public List<TerminalNode> DOT() { return getTokens(DataQLParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(DataQLParser.DOT, i);
		}
		public NormalRouteCopyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_normalRouteCopy; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitNormalRouteCopy(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NormalRouteCopyContext normalRouteCopy() throws RecognitionException {
		NormalRouteCopyContext _localctx = new NormalRouteCopyContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_normalRouteCopy);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(250);
			routeItem();
			setState(255);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(251);
				match(DOT);
				setState(252);
				routeItem();
				}
				}
				setState(257);
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

	public static class RouteItemContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(DataQLParser.IDENTIFIER, 0); }
		public List<RouteSubscriptContext> routeSubscript() {
			return getRuleContexts(RouteSubscriptContext.class);
		}
		public RouteSubscriptContext routeSubscript(int i) {
			return getRuleContext(RouteSubscriptContext.class,i);
		}
		public RouteItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_routeItem; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DataQLVisitor ) return ((DataQLVisitor<? extends T>)visitor).visitRouteItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RouteItemContext routeItem() throws RecognitionException {
		RouteItemContext _localctx = new RouteItemContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_routeItem);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(258);
			match(IDENTIFIER);
			setState(262);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LSBT) {
				{
				{
				setState(259);
				routeSubscript();
				}
				}
				setState(264);
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
		enterRule(_localctx, 38, RULE_routeSubscript);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(265);
			match(LSBT);
			setState(266);
			_la = _input.LA(1);
			if ( !(_la==STRING || _la==INTEGER_NUM) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(267);
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
		enterRule(_localctx, 40, RULE_expr);
		int _la;
		try {
			setState(283);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LBT:
				_localctx = new PrivilegeExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(269);
				match(LBT);
				setState(270);
				expr();
				setState(271);
				match(RBT);
				setState(274);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
				case 1:
					{
					setState(272);
					dyadicExpr();
					}
					break;
				case 2:
					{
					setState(273);
					ternaryExpr();
					}
					break;
				}
				}
				break;
			case PLUS:
			case MINUS:
			case NOT:
				_localctx = new UnaryExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(276);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << NOT))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(277);
				expr();
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
				setState(278);
				atomExpr();
				setState(281);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
				case 1:
					{
					setState(279);
					dyadicExpr();
					}
					break;
				case 2:
					{
					setState(280);
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
		enterRule(_localctx, 42, RULE_dyadicExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(285);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << MUL) | (1L << DIV) | (1L << DIV2) | (1L << MOD) | (1L << LBT) | (1L << RBT) | (1L << AND) | (1L << OR) | (1L << NOT) | (1L << XOR) | (1L << LSHIFT) | (1L << RSHIFT) | (1L << RSHIFT2) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ) | (1L << NE) | (1L << SC_OR) | (1L << SC_AND))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(286);
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
		enterRule(_localctx, 44, RULE_ternaryExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(288);
			match(T__3);
			setState(289);
			expr();
			setState(290);
			match(COLON);
			setState(291);
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
		public RouteCallContext routeCall() {
			return getRuleContext(RouteCallContext.class,0);
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
		enterRule(_localctx, 46, RULE_atomExpr);
		try {
			setState(296);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(293);
				primitiveValue();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(294);
				funcCall();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(295);
				routeCall();
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3=\u012d\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\3\2\7\2\64\n\2\f\2\16\2\67\13\2\3\2\7\2:\n\2\f\2\16\2=\13\2\3\2\6\2@"+
		"\n\2\r\2\16\2A\3\2\3\2\3\3\3\3\3\3\3\3\3\3\5\3K\n\3\3\4\3\4\5\4O\n\4\3"+
		"\4\3\4\3\4\3\4\5\4U\n\4\3\5\3\5\3\5\3\5\5\5[\n\5\3\5\5\5^\n\5\6\5`\n\5"+
		"\r\5\16\5a\3\5\3\5\3\5\3\5\3\5\5\5i\n\5\3\5\5\5l\n\5\5\5n\n\5\3\6\3\6"+
		"\3\6\3\6\3\6\5\6u\n\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\7\7\u0083\n\7\f\7\16\7\u0086\13\7\3\7\3\7\5\7\u008a\n\7\3\b\3\b\3\b\5"+
		"\b\u008f\n\b\3\b\3\b\5\b\u0093\n\b\3\t\3\t\5\t\u0097\n\t\3\t\3\t\3\t\3"+
		"\t\3\n\3\n\3\n\7\n\u00a0\n\n\f\n\16\n\u00a3\13\n\3\13\3\13\5\13\u00a7"+
		"\n\13\3\13\3\13\3\13\5\13\u00ac\n\13\3\13\3\13\3\13\3\13\3\13\5\13\u00b3"+
		"\n\13\5\13\u00b5\n\13\3\f\3\f\5\f\u00b9\n\f\3\f\3\f\7\f\u00bd\n\f\f\f"+
		"\16\f\u00c0\13\f\3\f\3\f\3\r\3\r\3\r\5\r\u00c7\n\r\3\16\3\16\5\16\u00cb"+
		"\n\16\3\16\3\16\7\16\u00cf\n\16\f\16\16\16\u00d2\13\16\3\16\3\16\3\17"+
		"\3\17\3\17\3\17\5\17\u00da\n\17\3\20\3\20\3\20\3\20\3\20\7\20\u00e1\n"+
		"\20\f\20\16\20\u00e4\13\20\5\20\u00e6\n\20\3\20\3\20\5\20\u00ea\n\20\3"+
		"\21\3\21\3\21\6\21\u00ef\n\21\r\21\16\21\u00f0\5\21\u00f3\n\21\3\22\3"+
		"\22\3\22\3\22\3\22\3\22\5\22\u00fb\n\22\3\23\3\23\3\23\7\23\u0100\n\23"+
		"\f\23\16\23\u0103\13\23\3\24\3\24\7\24\u0107\n\24\f\24\16\24\u010a\13"+
		"\24\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\5\26\u0115\n\26\3\26"+
		"\3\26\3\26\3\26\3\26\5\26\u011c\n\26\5\26\u011e\n\26\3\27\3\27\3\27\3"+
		"\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\5\31\u012b\n\31\3\31\2\2\32\2\4"+
		"\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\2\b\3\2\r\17\3\2\23\24"+
		"\3\28<\4\2\67\67;;\4\2\27\30!!\3\2\27-\2\u0148\2\65\3\2\2\2\4E\3\2\2\2"+
		"\6L\3\2\2\2\bm\3\2\2\2\no\3\2\2\2\fv\3\2\2\2\16\u008b\3\2\2\2\20\u0094"+
		"\3\2\2\2\22\u009c\3\2\2\2\24\u00b4\3\2\2\2\26\u00b6\3\2\2\2\30\u00c3\3"+
		"\2\2\2\32\u00c8\3\2\2\2\34\u00d9\3\2\2\2\36\u00db\3\2\2\2 \u00f2\3\2\2"+
		"\2\"\u00fa\3\2\2\2$\u00fc\3\2\2\2&\u0104\3\2\2\2(\u010b\3\2\2\2*\u011d"+
		"\3\2\2\2,\u011f\3\2\2\2.\u0122\3\2\2\2\60\u012a\3\2\2\2\62\64\5\4\3\2"+
		"\63\62\3\2\2\2\64\67\3\2\2\2\65\63\3\2\2\2\65\66\3\2\2\2\66;\3\2\2\2\67"+
		"\65\3\2\2\28:\5\6\4\298\3\2\2\2:=\3\2\2\2;9\3\2\2\2;<\3\2\2\2<?\3\2\2"+
		"\2=;\3\2\2\2>@\5\b\5\2?>\3\2\2\2@A\3\2\2\2A?\3\2\2\2AB\3\2\2\2BC\3\2\2"+
		"\2CD\7\2\2\3D\3\3\2\2\2EF\7\21\2\2FG\7=\2\2GH\7\60\2\2HJ\5\34\17\2IK\7"+
		"\3\2\2JI\3\2\2\2JK\3\2\2\2K\5\3\2\2\2LN\7\22\2\2MO\7\66\2\2NM\3\2\2\2"+
		"NO\3\2\2\2OP\3\2\2\2PQ\7\67\2\2QR\7\26\2\2RT\7=\2\2SU\7\3\2\2TS\3\2\2"+
		"\2TU\3\2\2\2U\7\3\2\2\2V_\7\64\2\2W[\5\n\6\2X[\5\f\7\2Y[\5\16\b\2ZW\3"+
		"\2\2\2ZX\3\2\2\2ZY\3\2\2\2[]\3\2\2\2\\^\7\3\2\2]\\\3\2\2\2]^\3\2\2\2^"+
		"`\3\2\2\2_Z\3\2\2\2`a\3\2\2\2a_\3\2\2\2ab\3\2\2\2bc\3\2\2\2cd\7\65\2\2"+
		"dn\3\2\2\2ei\5\n\6\2fi\5\f\7\2gi\5\16\b\2he\3\2\2\2hf\3\2\2\2hg\3\2\2"+
		"\2ik\3\2\2\2jl\7\3\2\2kj\3\2\2\2kl\3\2\2\2ln\3\2\2\2mV\3\2\2\2mh\3\2\2"+
		"\2n\t\3\2\2\2op\7\20\2\2pq\7=\2\2qt\7\60\2\2ru\5\24\13\2su\5\20\t\2tr"+
		"\3\2\2\2ts\3\2\2\2u\13\3\2\2\2vw\7\13\2\2wx\7\35\2\2xy\5*\26\2yz\7\36"+
		"\2\2z\u0084\5\b\5\2{|\7\f\2\2|}\7\13\2\2}~\7\35\2\2~\177\5*\26\2\177\u0080"+
		"\7\36\2\2\u0080\u0081\5\b\5\2\u0081\u0083\3\2\2\2\u0082{\3\2\2\2\u0083"+
		"\u0086\3\2\2\2\u0084\u0082\3\2\2\2\u0084\u0085\3\2\2\2\u0085\u0089\3\2"+
		"\2\2\u0086\u0084\3\2\2\2\u0087\u0088\7\f\2\2\u0088\u008a\5\b\5\2\u0089"+
		"\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a\r\3\2\2\2\u008b\u008e\t\2\2\2"+
		"\u008c\u008d\7;\2\2\u008d\u008f\7.\2\2\u008e\u008c\3\2\2\2\u008e\u008f"+
		"\3\2\2\2\u008f\u0092\3\2\2\2\u0090\u0093\5\24\13\2\u0091\u0093\5\20\t"+
		"\2\u0092\u0090\3\2\2\2\u0092\u0091\3\2\2\2\u0093\17\3\2\2\2\u0094\u0096"+
		"\7\35\2\2\u0095\u0097\5\22\n\2\u0096\u0095\3\2\2\2\u0096\u0097\3\2\2\2"+
		"\u0097\u0098\3\2\2\2\u0098\u0099\7\36\2\2\u0099\u009a\7\4\2\2\u009a\u009b"+
		"\5\b\5\2\u009b\21\3\2\2\2\u009c\u00a1\7=\2\2\u009d\u009e\7.\2\2\u009e"+
		"\u00a0\7=\2\2\u009f\u009d\3\2\2\2\u00a0\u00a3\3\2\2\2\u00a1\u009f\3\2"+
		"\2\2\u00a1\u00a2\3\2\2\2\u00a2\23\3\2\2\2\u00a3\u00a1\3\2\2\2\u00a4\u00a7"+
		"\5\36\20\2\u00a5\u00a7\5\"\22\2\u00a6\u00a4\3\2\2\2\u00a6\u00a5\3\2\2"+
		"\2\u00a7\u00a8\3\2\2\2\u00a8\u00ab\7\5\2\2\u00a9\u00ac\5\26\f\2\u00aa"+
		"\u00ac\5\32\16\2\u00ab\u00a9\3\2\2\2\u00ab\u00aa\3\2\2\2\u00ac\u00b5\3"+
		"\2\2\2\u00ad\u00b3\5\34\17\2\u00ae\u00b3\5\26\f\2\u00af\u00b3\5\32\16"+
		"\2\u00b0\u00b3\5*\26\2\u00b1\u00b3\5\20\t\2\u00b2\u00ad\3\2\2\2\u00b2"+
		"\u00ae\3\2\2\2\u00b2\u00af\3\2\2\2\u00b2\u00b0\3\2\2\2\u00b2\u00b1\3\2"+
		"\2\2\u00b3\u00b5\3\2\2\2\u00b4\u00a6\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b5"+
		"\25\3\2\2\2\u00b6\u00b8\7\64\2\2\u00b7\u00b9\5\30\r\2\u00b8\u00b7\3\2"+
		"\2\2\u00b8\u00b9\3\2\2\2\u00b9\u00be\3\2\2\2\u00ba\u00bb\7.\2\2\u00bb"+
		"\u00bd\5\30\r\2\u00bc\u00ba\3\2\2\2\u00bd\u00c0\3\2\2\2\u00be\u00bc\3"+
		"\2\2\2\u00be\u00bf\3\2\2\2\u00bf\u00c1\3\2\2\2\u00c0\u00be\3\2\2\2\u00c1"+
		"\u00c2\7\65\2\2\u00c2\27\3\2\2\2\u00c3\u00c6\7\67\2\2\u00c4\u00c5\7/\2"+
		"\2\u00c5\u00c7\5\24\13\2\u00c6\u00c4\3\2\2\2\u00c6\u00c7\3\2\2\2\u00c7"+
		"\31\3\2\2\2\u00c8\u00ca\7\62\2\2\u00c9\u00cb\5\24\13\2\u00ca\u00c9\3\2"+
		"\2\2\u00ca\u00cb\3\2\2\2\u00cb\u00d0\3\2\2\2\u00cc\u00cd\7.\2\2\u00cd"+
		"\u00cf\5\24\13\2\u00ce\u00cc\3\2\2\2\u00cf\u00d2\3\2\2\2\u00d0\u00ce\3"+
		"\2\2\2\u00d0\u00d1\3\2\2\2\u00d1\u00d3\3\2\2\2\u00d2\u00d0\3\2\2\2\u00d3"+
		"\u00d4\7\63\2\2\u00d4\33\3\2\2\2\u00d5\u00da\7\67\2\2\u00d6\u00da\7\25"+
		"\2\2\u00d7\u00da\t\3\2\2\u00d8\u00da\t\4\2\2\u00d9\u00d5\3\2\2\2\u00d9"+
		"\u00d6\3\2\2\2\u00d9\u00d7\3\2\2\2\u00d9\u00d8\3\2\2\2\u00da\35\3\2\2"+
		"\2\u00db\u00dc\5\"\22\2\u00dc\u00e5\7\35\2\2\u00dd\u00e2\5\24\13\2\u00de"+
		"\u00df\7.\2\2\u00df\u00e1\5\24\13\2\u00e0\u00de\3\2\2\2\u00e1\u00e4\3"+
		"\2\2\2\u00e2\u00e0\3\2\2\2\u00e2\u00e3\3\2\2\2\u00e3\u00e6\3\2\2\2\u00e4"+
		"\u00e2\3\2\2\2\u00e5\u00dd\3\2\2\2\u00e5\u00e6\3\2\2\2\u00e6\u00e7\3\2"+
		"\2\2\u00e7\u00e9\7\36\2\2\u00e8\u00ea\5 \21\2\u00e9\u00e8\3\2\2\2\u00e9"+
		"\u00ea\3\2\2\2\u00ea\37\3\2\2\2\u00eb\u00ec\7\61\2\2\u00ec\u00f3\5$\23"+
		"\2\u00ed\u00ef\5(\25\2\u00ee\u00ed\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0\u00ee"+
		"\3\2\2\2\u00f0\u00f1\3\2\2\2\u00f1\u00f3\3\2\2\2\u00f2\u00eb\3\2\2\2\u00f2"+
		"\u00ee\3\2\2\2\u00f3!\3\2\2\2\u00f4\u00f5\7\66\2\2\u00f5\u00f6\7\64\2"+
		"\2\u00f6\u00f7\5$\23\2\u00f7\u00f8\7\65\2\2\u00f8\u00fb\3\2\2\2\u00f9"+
		"\u00fb\5$\23\2\u00fa\u00f4\3\2\2\2\u00fa\u00f9\3\2\2\2\u00fb#\3\2\2\2"+
		"\u00fc\u0101\5&\24\2\u00fd\u00fe\7\61\2\2\u00fe\u0100\5&\24\2\u00ff\u00fd"+
		"\3\2\2\2\u0100\u0103\3\2\2\2\u0101\u00ff\3\2\2\2\u0101\u0102\3\2\2\2\u0102"+
		"%\3\2\2\2\u0103\u0101\3\2\2\2\u0104\u0108\7=\2\2\u0105\u0107\5(\25\2\u0106"+
		"\u0105\3\2\2\2\u0107\u010a\3\2\2\2\u0108\u0106\3\2\2\2\u0108\u0109\3\2"+
		"\2\2\u0109\'\3\2\2\2\u010a\u0108\3\2\2\2\u010b\u010c\7\62\2\2\u010c\u010d"+
		"\t\5\2\2\u010d\u010e\7\63\2\2\u010e)\3\2\2\2\u010f\u0110\7\35\2\2\u0110"+
		"\u0111\5*\26\2\u0111\u0114\7\36\2\2\u0112\u0115\5,\27\2\u0113\u0115\5"+
		".\30\2\u0114\u0112\3\2\2\2\u0114\u0113\3\2\2\2\u0114\u0115\3\2\2\2\u0115"+
		"\u011e\3\2\2\2\u0116\u0117\t\6\2\2\u0117\u011e\5*\26\2\u0118\u011b\5\60"+
		"\31\2\u0119\u011c\5,\27\2\u011a\u011c\5.\30\2\u011b\u0119\3\2\2\2\u011b"+
		"\u011a\3\2\2\2\u011b\u011c\3\2\2\2\u011c\u011e\3\2\2\2\u011d\u010f\3\2"+
		"\2\2\u011d\u0116\3\2\2\2\u011d\u0118\3\2\2\2\u011e+\3\2\2\2\u011f\u0120"+
		"\t\7\2\2\u0120\u0121\5*\26\2\u0121-\3\2\2\2\u0122\u0123\7\6\2\2\u0123"+
		"\u0124\5*\26\2\u0124\u0125\7/\2\2\u0125\u0126\5*\26\2\u0126/\3\2\2\2\u0127"+
		"\u012b\5\34\17\2\u0128\u012b\5\36\20\2\u0129\u012b\5\"\22\2\u012a\u0127"+
		"\3\2\2\2\u012a\u0128\3\2\2\2\u012a\u0129\3\2\2\2\u012b\61\3\2\2\2+\65"+
		";AJNTZ]ahkmt\u0084\u0089\u008e\u0092\u0096\u00a1\u00a6\u00ab\u00b2\u00b4"+
		"\u00b8\u00be\u00c6\u00ca\u00d0\u00d9\u00e2\u00e5\u00e9\u00f0\u00f2\u00fa"+
		"\u0101\u0108\u0114\u011b\u011d\u012a";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}