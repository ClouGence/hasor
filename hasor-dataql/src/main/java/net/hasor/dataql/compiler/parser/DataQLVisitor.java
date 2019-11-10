// Generated from /Users/yongchun.zyc/Documents/Drive/projects/hasor/hasor.git/hasor-dataql/src/main/java/net/hasor/dataql/compiler/parser/DataQL.g4 by ANTLR 4.7.2
package net.hasor.dataql.compiler.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link DataQLParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface DataQLVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link DataQLParser#rootInstSet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRootInstSet(DataQLParser.RootInstSetContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataQLParser#optionInst}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOptionInst(DataQLParser.OptionInstContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataQLParser#importInst}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportInst(DataQLParser.ImportInstContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multipleInst}
	 * labeled alternative in {@link DataQLParser#blockSet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultipleInst(DataQLParser.MultipleInstContext ctx);
	/**
	 * Visit a parse tree produced by the {@code singleInst}
	 * labeled alternative in {@link DataQLParser#blockSet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleInst(DataQLParser.SingleInstContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataQLParser#ifInst}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfInst(DataQLParser.IfInstContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataQLParser#breakInst}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakInst(DataQLParser.BreakInstContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataQLParser#lambdaDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLambdaDef(DataQLParser.LambdaDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataQLParser#varInst}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarInst(DataQLParser.VarInstContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataQLParser#anyObject}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnyObject(DataQLParser.AnyObjectContext ctx);
	/**
	 * Visit a parse tree produced by the {@code specialRoute}
	 * labeled alternative in {@link DataQLParser#routeMapping}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecialRoute(DataQLParser.SpecialRouteContext ctx);
	/**
	 * Visit a parse tree produced by the {@code normalRoute}
	 * labeled alternative in {@link DataQLParser#routeMapping}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNormalRoute(DataQLParser.NormalRouteContext ctx);
	/**
	 * Visit a parse tree produced by the {@code convertRoute}
	 * labeled alternative in {@link DataQLParser#routeMapping}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConvertRoute(DataQLParser.ConvertRouteContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataQLParser#routeNameSet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRouteNameSet(DataQLParser.RouteNameSetContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataQLParser#routeName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRouteName(DataQLParser.RouteNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataQLParser#routeSubscript}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRouteSubscript(DataQLParser.RouteSubscriptContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataQLParser#funcCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncCall(DataQLParser.FuncCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code funcCallResult_route}
	 * labeled alternative in {@link DataQLParser#funcCallResult}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncCallResult_route(DataQLParser.FuncCallResult_routeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code funcCallResult_convert}
	 * labeled alternative in {@link DataQLParser#funcCallResult}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncCallResult_convert(DataQLParser.FuncCallResult_convertContext ctx);
	/**
	 * Visit a parse tree produced by the {@code funcCallResult_call}
	 * labeled alternative in {@link DataQLParser#funcCallResult}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncCallResult_call(DataQLParser.FuncCallResult_callContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataQLParser#objectValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectValue(DataQLParser.ObjectValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataQLParser#objectKeyValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectKeyValue(DataQLParser.ObjectKeyValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataQLParser#listValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListValue(DataQLParser.ListValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stringValue}
	 * labeled alternative in {@link DataQLParser#primitiveValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringValue(DataQLParser.StringValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nullValue}
	 * labeled alternative in {@link DataQLParser#primitiveValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullValue(DataQLParser.NullValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code booleanValue}
	 * labeled alternative in {@link DataQLParser#primitiveValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanValue(DataQLParser.BooleanValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numberValue}
	 * labeled alternative in {@link DataQLParser#primitiveValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberValue(DataQLParser.NumberValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code unaryExpr}
	 * labeled alternative in {@link DataQLParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpr(DataQLParser.UnaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code privilegeExpr}
	 * labeled alternative in {@link DataQLParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrivilegeExpr(DataQLParser.PrivilegeExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multipleExpr}
	 * labeled alternative in {@link DataQLParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultipleExpr(DataQLParser.MultipleExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataQLParser#dyadicExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDyadicExpr(DataQLParser.DyadicExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataQLParser#ternaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTernaryExpr(DataQLParser.TernaryExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link DataQLParser#atomExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtomExpr(DataQLParser.AtomExprContext ctx);
}