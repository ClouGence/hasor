// Generated from /Users/yongchun.zyc/Documents/Drive/projects/hasor/hasor.git/hasor-dataql/src/main/java/net/hasor/dataql/domain/parser/DataQL.g4 by ANTLR 4.7.2
package net.hasor.dataql.domain.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link DataQLParser}.
 */
public interface DataQLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link DataQLParser#rootBlockSet}.
	 * @param ctx the parse tree
	 */
	void enterRootBlockSet(DataQLParser.RootBlockSetContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#rootBlockSet}.
	 * @param ctx the parse tree
	 */
	void exitRootBlockSet(DataQLParser.RootBlockSetContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#optionBlock}.
	 * @param ctx the parse tree
	 */
	void enterOptionBlock(DataQLParser.OptionBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#optionBlock}.
	 * @param ctx the parse tree
	 */
	void exitOptionBlock(DataQLParser.OptionBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#importBlock}.
	 * @param ctx the parse tree
	 */
	void enterImportBlock(DataQLParser.ImportBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#importBlock}.
	 * @param ctx the parse tree
	 */
	void exitImportBlock(DataQLParser.ImportBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#blockSet}.
	 * @param ctx the parse tree
	 */
	void enterBlockSet(DataQLParser.BlockSetContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#blockSet}.
	 * @param ctx the parse tree
	 */
	void exitBlockSet(DataQLParser.BlockSetContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#blockItem}.
	 * @param ctx the parse tree
	 */
	void enterBlockItem(DataQLParser.BlockItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#blockItem}.
	 * @param ctx the parse tree
	 */
	void exitBlockItem(DataQLParser.BlockItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#varBlock}.
	 * @param ctx the parse tree
	 */
	void enterVarBlock(DataQLParser.VarBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#varBlock}.
	 * @param ctx the parse tree
	 */
	void exitVarBlock(DataQLParser.VarBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#ifBlock}.
	 * @param ctx the parse tree
	 */
	void enterIfBlock(DataQLParser.IfBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#ifBlock}.
	 * @param ctx the parse tree
	 */
	void exitIfBlock(DataQLParser.IfBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#breakBlock}.
	 * @param ctx the parse tree
	 */
	void enterBreakBlock(DataQLParser.BreakBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#breakBlock}.
	 * @param ctx the parse tree
	 */
	void exitBreakBlock(DataQLParser.BreakBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#polymericObject}.
	 * @param ctx the parse tree
	 */
	void enterPolymericObject(DataQLParser.PolymericObjectContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#polymericObject}.
	 * @param ctx the parse tree
	 */
	void exitPolymericObject(DataQLParser.PolymericObjectContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#lambdaDef}.
	 * @param ctx the parse tree
	 */
	void enterLambdaDef(DataQLParser.LambdaDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#lambdaDef}.
	 * @param ctx the parse tree
	 */
	void exitLambdaDef(DataQLParser.LambdaDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#objectValue}.
	 * @param ctx the parse tree
	 */
	void enterObjectValue(DataQLParser.ObjectValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#objectValue}.
	 * @param ctx the parse tree
	 */
	void exitObjectValue(DataQLParser.ObjectValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#objectValueItem}.
	 * @param ctx the parse tree
	 */
	void enterObjectValueItem(DataQLParser.ObjectValueItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#objectValueItem}.
	 * @param ctx the parse tree
	 */
	void exitObjectValueItem(DataQLParser.ObjectValueItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#listValue}.
	 * @param ctx the parse tree
	 */
	void enterListValue(DataQLParser.ListValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#listValue}.
	 * @param ctx the parse tree
	 */
	void exitListValue(DataQLParser.ListValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#functionCallConvertValue}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCallConvertValue(DataQLParser.FunctionCallConvertValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#functionCallConvertValue}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCallConvertValue(DataQLParser.FunctionCallConvertValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#routeConvertValue}.
	 * @param ctx the parse tree
	 */
	void enterRouteConvertValue(DataQLParser.RouteConvertValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#routeConvertValue}.
	 * @param ctx the parse tree
	 */
	void exitRouteConvertValue(DataQLParser.RouteConvertValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(DataQLParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(DataQLParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#routeMapping}.
	 * @param ctx the parse tree
	 */
	void enterRouteMapping(DataQLParser.RouteMappingContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#routeMapping}.
	 * @param ctx the parse tree
	 */
	void exitRouteMapping(DataQLParser.RouteMappingContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#routeMappingItem}.
	 * @param ctx the parse tree
	 */
	void enterRouteMappingItem(DataQLParser.RouteMappingItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#routeMappingItem}.
	 * @param ctx the parse tree
	 */
	void exitRouteMappingItem(DataQLParser.RouteMappingItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#subscriptMapping}.
	 * @param ctx the parse tree
	 */
	void enterSubscriptMapping(DataQLParser.SubscriptMappingContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#subscriptMapping}.
	 * @param ctx the parse tree
	 */
	void exitSubscriptMapping(DataQLParser.SubscriptMappingContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(DataQLParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(DataQLParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#unaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpr(DataQLParser.UnaryExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#unaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpr(DataQLParser.UnaryExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#dyadicExpr}.
	 * @param ctx the parse tree
	 */
	void enterDyadicExpr(DataQLParser.DyadicExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#dyadicExpr}.
	 * @param ctx the parse tree
	 */
	void exitDyadicExpr(DataQLParser.DyadicExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#ternaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterTernaryExpr(DataQLParser.TernaryExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#ternaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitTernaryExpr(DataQLParser.TernaryExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#atomExpr}.
	 * @param ctx the parse tree
	 */
	void enterAtomExpr(DataQLParser.AtomExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#atomExpr}.
	 * @param ctx the parse tree
	 */
	void exitAtomExpr(DataQLParser.AtomExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link DataQLParser#primitiveValue}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveValue(DataQLParser.PrimitiveValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link DataQLParser#primitiveValue}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveValue(DataQLParser.PrimitiveValueContext ctx);
}