/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.dataql.compiler.parser;
import net.hasor.dataql.compiler.ParseException;
import net.hasor.dataql.compiler.ast.Expression;
import net.hasor.dataql.compiler.ast.Location;
import net.hasor.dataql.compiler.ast.Location.CodePosition;
import net.hasor.dataql.compiler.ast.RouteVariable;
import net.hasor.dataql.compiler.ast.Variable;
import net.hasor.dataql.compiler.ast.expr.*;
import net.hasor.dataql.compiler.ast.fmt.ListFormat;
import net.hasor.dataql.compiler.ast.fmt.ObjectFormat;
import net.hasor.dataql.compiler.ast.inst.*;
import net.hasor.dataql.compiler.ast.inst.ImportInst.ImportType;
import net.hasor.dataql.compiler.ast.token.IntegerToken;
import net.hasor.dataql.compiler.ast.token.StringToken;
import net.hasor.dataql.compiler.ast.token.SymbolToken;
import net.hasor.dataql.compiler.ast.value.*;
import net.hasor.dataql.compiler.ast.value.EnterRouteVariable.RouteType;
import net.hasor.dataql.compiler.ast.value.EnterRouteVariable.SpecialType;
import net.hasor.dataql.compiler.ast.value.PrimitiveVariable.ValueType;
import net.hasor.dataql.compiler.parser.DataQLParser.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Stack;

/**
 * This class provides an empty implementation of {@link DataQLParserVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-11-07
 */
public class DefaultDataQLVisitor<T> extends AbstractParseTreeVisitor<T> implements DataQLParserVisitor<T> {
    private final Stack<Object> instStack = new Stack<>();

    public <T extends Location> T code(T location, TerminalNode context) {
        Token symbol = context.getSymbol();
        location.setStartPosition(new CodePosition(symbol.getLine(), symbol.getCharPositionInLine()));
        location.setEndPosition(new CodePosition(symbol.getLine(), symbol.getCharPositionInLine() + symbol.getText().length()));
        return location;
    }

    public <T extends Location> T code(T location, ParserRuleContext context) {
        Token startToken = context.start;
        Token endToken = context.stop;
        int endTokenLength = endToken.getText().length();
        location.setStartPosition(new CodePosition(startToken.getLine(), startToken.getCharPositionInLine()));
        location.setEndPosition(new CodePosition(endToken.getLine(), endToken.getCharPositionInLine() + endTokenLength));
        return location;
    }

    private <T extends Location, V extends Location> T code(T location, V other) {
        location.setStartPosition(other.getStartPosition());
        location.setEndPosition(other.getEndPosition());
        return location;
    }

    private <T extends Location, V extends Location> T code(T location, List<TerminalNode> otherList) {
        Token firstTerm = otherList.get(0).getSymbol();
        Token lastTerm = otherList.get(otherList.size() - 1).getSymbol();
        location.setStartPosition(new CodePosition(firstTerm.getLine(), firstTerm.getCharPositionInLine()));
        location.setEndPosition(new CodePosition(lastTerm.getLine(), lastTerm.getCharPositionInLine() + lastTerm.getText().length()));
        return location;
    }

    private <T extends Location, V extends Location> T code(T location, Token token) {
        int endTokenLength = token.getText().length();
        location.setStartPosition(new CodePosition(token.getLine(), token.getCharPositionInLine()));
        location.setEndPosition(new CodePosition(token.getLine(), token.getCharPositionInLine() + endTokenLength));
        return location;
    }

    @Override
    public T visitRootInstSet(RootInstSetContext ctx) {
        this.instStack.push(code(new RootBlockSet(), ctx));
        List<HintInstContext> optionList = ctx.hintInst();
        List<ImportInstContext> importList = ctx.importInst();
        List<BlockSetContext> blockSetList = ctx.blockSet();
        //
        if (optionList != null) {
            for (HintInstContext option : optionList) {
                option.accept(this);
            }
        }
        if (importList != null) {
            for (ImportInstContext option : importList) {
                option.accept(this);
            }
        }
        if (blockSetList != null) {
            for (BlockSetContext blockSet : blockSetList) {
                blockSet.accept(this);
                InstSet instSet = (InstSet) this.instStack.pop();
                RootBlockSet rootBlockSet = (RootBlockSet) this.instStack.peek();
                if (instSet.isMultipleInst()) {
                    rootBlockSet.add(instSet);
                } else {
                    rootBlockSet.addInstSet(instSet);
                }
            }
        }
        //
        return (T) this.instStack.pop();
    }

    @Override
    public T visitHintInst(HintInstContext ctx) {
        TerminalNode identifier = ctx.IDENTIFIER();
        StringToken stringToken = code(new StringToken(fixIdentifier(identifier)), identifier);
        this.instStack.push(stringToken);
        visitChildren(ctx);
        //
        PrimitiveVariable optValue = (PrimitiveVariable) this.instStack.pop();
        StringToken optKey = (StringToken) this.instStack.pop();
        HintInst hintInst = code(new HintInst(optKey, optValue), ctx);
        //
        ((InstSet) this.instStack.peek()).addOptionInst(hintInst);
        return null;
    }

    @Override
    public T visitImportInst(ImportInstContext ctx) {
        visitChildren(ctx);
        //
        TerminalNode importResourceTerm = ctx.STRING();
        TerminalNode asNameTerm = ctx.IDENTIFIER();
        StringToken importResourceToken = code(new StringToken(fixString(importResourceTerm)), importResourceTerm);
        StringToken asNameToken = code(new StringToken(fixIdentifier(asNameTerm)), asNameTerm);
        //
        ImportType importType = ImportType.ClassType;
        TerminalNode rouNode = ctx.ROU();
        if (rouNode != null) {
            if ("@".equals(rouNode.getText())) {
                importType = ImportType.Resource;
            } else {
                throw newParseException(rouNode.getSymbol(), "parser failed -> visitImportInst.");
            }
        }
        //
        ImportInst importInst = code(new ImportInst(importType, importResourceToken, asNameToken), ctx);
        ((RootBlockSet) this.instStack.peek()).addImportInst(importInst);
        return null;
    }

    @Override
    public T visitMultipleInst(MultipleInstContext ctx) {
        this.instStack.push(code(new InstSet(true), ctx));
        visitChildren(ctx);
        return null;
    }

    @Override
    public T visitSingleInst(SingleInstContext ctx) {
        this.instStack.push(code(new InstSet(false), ctx));
        visitChildren(ctx);
        return null;
    }

    @Override
    public T visitVarInst(VarInstContext ctx) {
        visitChildren(ctx);
        //
        TerminalNode identifier = ctx.IDENTIFIER();
        StringToken stringToken = code(new StringToken(fixIdentifier(identifier)), identifier);
        VarInst varInst = code(new VarInst(stringToken, (Variable) this.instStack.pop()), ctx);
        ((InstSet) this.instStack.peek()).addInst(varInst);
        return null;
    }

    @Override
    public T visitRunInst(RunInstContext ctx) {
        visitChildren(ctx);
        //
        RunInst varInst = code(new RunInst((Variable) this.instStack.pop()), ctx);
        ((InstSet) this.instStack.peek()).addInst(varInst);
        return null;
    }

    @Override
    public T visitAnyObject(AnyObjectContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public T visitIfInst(IfInstContext ctx) {
        List<ExprContext> exprList = ctx.expr();
        List<BlockSetContext> ifBlocks = ctx.blockSet();
        SwitchInst switchInst = code(new SwitchInst(), ctx);
        for (int i = 0; i < ifBlocks.size(); i++) {
            if (i < exprList.size()) {
                // if and elseif
                exprList.get(i).accept(this);
                ifBlocks.get(i).accept(this);
                InstSet instSet = (InstSet) this.instStack.pop();
                Variable expr = (Variable) this.instStack.pop();
                if (expr instanceof Expression) {
                    switchInst.addElseif((Expression) expr, instSet);
                } else {
                    switchInst.addElseif(code(new AtomExpression(expr), expr), instSet);
                }
            } else {
                // else
                ifBlocks.get(i).accept(this);
                InstSet instSet = (InstSet) this.instStack.pop();
                switchInst.setElseBlockSet(instSet);
                break;
            }
        }
        ((InstSet) this.instStack.peek()).addInst(switchInst);
        return null;
    }

    @Override
    public T visitBreakInst(BreakInstContext ctx) {
        //
        TerminalNode breakCodeNode = ctx.INTEGER_NUM();
        IntegerToken breakCodeToken = null;
        if (breakCodeNode != null) {
            int breakCode = Integer.parseInt(breakCodeNode.getText());
            breakCodeToken = code(new IntegerToken(breakCode), breakCodeNode);
        } else {
            breakCodeToken = code(new IntegerToken(0), ctx.start);
        }
        //
        AnyObjectContext anyObject = ctx.anyObject();
        anyObject.accept(this);
        Variable variable = (Variable) this.instStack.pop();
        //
        if (ctx.EXIT() != null) {
            ((InstSet) this.instStack.peek()).addInst(code(new ExitInst(breakCodeToken, variable), ctx));
            return null;
        }
        if (ctx.THROW() != null) {
            ((InstSet) this.instStack.peek()).addInst(code(new ThrowInst(breakCodeToken, variable), ctx));
            return null;
        }
        if (ctx.RETURN() != null) {
            ((InstSet) this.instStack.peek()).addInst(code(new ReturnInst(breakCodeToken, variable), ctx));
            return null;
        }
        throw newParseException(ctx.start, "missing exit statement.");
    }

    @Override
    public T visitLambdaDef(LambdaDefContext ctx) {
        LambdaVariable lambdaVariable = code(new LambdaVariable(), ctx);
        List<TerminalNode> identifierList = ctx.IDENTIFIER();
        if (identifierList != null) {
            for (TerminalNode terminalNode : identifierList) {
                String paramName = fixIdentifier(terminalNode);
                StringToken paramToken = code(new StringToken(paramName), terminalNode);
                lambdaVariable.addParam(paramToken);
            }
        }
        //
        this.instStack.push(lambdaVariable);
        //
        ctx.blockSet().accept(this);
        InstSet instSet = (InstSet) this.instStack.pop();
        ((LambdaVariable) this.instStack.peek()).setMultipleInst(instSet.isMultipleInst());
        ((LambdaVariable) this.instStack.peek()).addInstSet(instSet);
        return null;
    }

    @Override
    public T visitObjectValue(ObjectValueContext ctx) {
        ObjectVariable objectVariable = code(new ObjectVariable(), ctx);
        this.instStack.push(objectVariable);
        return visitChildren(ctx);
    }

    @Override
    public T visitObjectKeyValue(ObjectKeyValueContext ctx) {
        TerminalNode fieldKeyTerm = ctx.STRING();
        String fieldKey = fixString(fieldKeyTerm);
        StringToken fieldKeyToken = code(new StringToken(fieldKey), fieldKeyTerm);
        //
        ObjectVariable objectVariable = (ObjectVariable) this.instStack.peek();
        AnyObjectContext polymericObject = ctx.anyObject();
        if (polymericObject != null) {
            polymericObject.accept(this);
            Variable valueExp = (Variable) this.instStack.pop();
            objectVariable.addField(fieldKeyToken, valueExp);
        } else {
            EnterRouteVariable enterRoute = code(new EnterRouteVariable(RouteType.Expr, null), fieldKeyTerm);
            NameRouteVariable nameRoute = code(new NameRouteVariable(enterRoute, fieldKeyToken), fieldKeyTerm);
            objectVariable.addField(fieldKeyToken, nameRoute);
        }
        return null;
    }

    @Override
    public T visitListValue(ListValueContext ctx) {
        ListVariable listVariable = code(new ListVariable(), ctx);
        List<AnyObjectContext> polymericList = ctx.anyObject();
        if (polymericList != null) {
            for (AnyObjectContext polymeric : polymericList) {
                polymeric.accept(this);
                Variable variable = (Variable) this.instStack.pop();
                listVariable.addItem(variable);
            }
        }
        this.instStack.push(listVariable);
        return null;
    }

    @Override
    public T visitStringValue(StringValueContext ctx) {
        String text = fixString(ctx.STRING());
        this.instStack.push(code(new PrimitiveVariable(text, ValueType.String), ctx));
        return null;
    }

    @Override
    public T visitNullValue(NullValueContext ctx) {
        this.instStack.push(code(new PrimitiveVariable(null, ValueType.Null), ctx));
        return null;
    }

    @Override
    public T visitBooleanValue(BooleanValueContext ctx) {
        boolean boolValue = ctx.TRUE() != null;
        this.instStack.push(code(new PrimitiveVariable(boolValue, ValueType.Boolean), ctx));
        return null;
    }

    @Override
    public T visitNumberValue(NumberValueContext ctx) {
        TerminalNode bitNode = ctx.BIT_NUM();
        TerminalNode octNode = ctx.OCT_NUM();
        TerminalNode intNode = ctx.INTEGER_NUM();
        TerminalNode hexNode = ctx.HEX_NUM();
        TerminalNode decimalNode = ctx.DECIMAL_NUM();
        //
        int radix = 10;
        String radixNumber = null;
        TerminalNode atTerm = null;
        if (bitNode != null) {
            radix = 2;
            radixNumber = bitNode.getText();
            radixNumber = (radixNumber.charAt(0) == '-') ? ("-" + radixNumber.substring(3)) : radixNumber.substring(2);
            atTerm = bitNode;
        }
        if (octNode != null) {
            radix = 8;
            radixNumber = octNode.getText();
            radixNumber = (radixNumber.charAt(0) == '-') ? ("-" + radixNumber.substring(3)) : radixNumber.substring(2);
            atTerm = octNode;
        }
        if (intNode != null) {
            radix = 10;
            radixNumber = intNode.getText();
            atTerm = intNode;
        }
        if (hexNode != null) {
            radix = 16;
            radixNumber = hexNode.getText();
            radixNumber = (radixNumber.charAt(0) == '-') ? ("-" + radixNumber.substring(3)) : radixNumber.substring(2);
            atTerm = hexNode;
        }
        if (radixNumber != null) {
            BigInteger bigInt = new BigInteger(radixNumber, radix);
            int bitLength = bigInt.bitLength();
            if (bitLength < 8) {
                this.instStack.push(code(new PrimitiveVariable(bigInt.byteValue(), ValueType.Number, radix), atTerm));
                return null;
            }
            if (bitLength < 16) {
                this.instStack.push(code(new PrimitiveVariable(bigInt.shortValue(), ValueType.Number, radix), atTerm));
                return null;
            }
            if (bitLength < 32) {
                this.instStack.push(code(new PrimitiveVariable(bigInt.intValue(), ValueType.Number, radix), atTerm));
                return null;
            }
            if (bitLength < 64) {
                this.instStack.push(code(new PrimitiveVariable(bigInt.longValue(), ValueType.Number, radix), atTerm));
                return null;
            }
            this.instStack.push(code(new PrimitiveVariable(bigInt, ValueType.Number, radix), atTerm));
            return null;
        } else {
            BigDecimal bigDec = new BigDecimal(decimalNode.getText());
            atTerm = decimalNode;
            int precisionLength = bigDec.precision();
            if (precisionLength < 8 && !Float.isInfinite(bigDec.floatValue())) {
                this.instStack.push(code(new PrimitiveVariable(bigDec.floatValue(), ValueType.Number, radix), atTerm));
                return null;
            }
            if (precisionLength < 16 && !Double.isInfinite(bigDec.doubleValue())) {
                this.instStack.push(code(new PrimitiveVariable(bigDec.doubleValue(), ValueType.Number, radix), atTerm));
                return null;
            }
            this.instStack.push(code(new PrimitiveVariable(bigDec, ValueType.Number, radix), atTerm));
            return null;
        }
    }

    @Override
    public T visitFuncCall(FuncCallContext ctx) {
        ctx.routeMapping().accept(this);
        //
        RouteVariable routeVariable = (RouteVariable) this.instStack.pop();
        FunCallRouteVariable funCall = code(new FunCallRouteVariable(routeVariable), ctx);
        List<AnyObjectContext> paramLists = ctx.anyObject();
        if (paramLists != null) {
            for (AnyObjectContext param : paramLists) {
                param.accept(this);
                Variable paramVar = (Variable) this.instStack.pop();
                funCall.addParam(paramVar);
            }
        }
        //
        this.instStack.push(funCall);
        FuncCallResultContext funcCallResult = ctx.funcCallResult();
        if (funcCallResult != null) {
            funcCallResult.accept(this);
        }
        return null;
    }

    @Override
    public T visitFuncCallResult_route1(FuncCallResult_route1Context ctx) {
        List<RouteSubscriptContext> subscriptList = ctx.routeSubscript();
        if (subscriptList != null) {
            for (RouteSubscriptContext context : subscriptList) {
                context.accept(this);
            }
        }
        //
        ctx.routeNameSet().accept(this);
        //
        FuncCallResultContext funcCallResult = ctx.funcCallResult();
        if (funcCallResult != null) {
            funcCallResult.accept(this);
        }
        return null;
    }

    @Override
    public T visitFuncCallResult_route2(FuncCallResult_route2Context ctx) {
        List<RouteSubscriptContext> subscriptList = ctx.routeSubscript();
        for (RouteSubscriptContext context : subscriptList) {
            context.accept(this);
        }
        //
        RouteNameSetContext routeNameSet = ctx.routeNameSet();
        if (routeNameSet != null) {
            ctx.routeNameSet().accept(this);
        }
        //
        FuncCallResultContext funcCallResult = ctx.funcCallResult();
        if (funcCallResult != null) {
            funcCallResult.accept(this);
        }
        return null;
    }

    @Override
    public T visitFuncCallResult_convert(FuncCallResult_convertContext ctx) {
        RouteVariable routeVariable = (RouteVariable) this.instStack.pop();
        //
        ListValueContext listValue = ctx.listValue();
        ObjectValueContext objectValue = ctx.objectValue();
        if (listValue != null) {
            listValue.accept(this);
            ListVariable listVariable = (ListVariable) this.instStack.pop();
            this.instStack.push(code(new ListFormat(routeVariable, listVariable), listValue));
        } else {
            objectValue.accept(this);
            ObjectVariable objectVariable = (ObjectVariable) this.instStack.pop();
            this.instStack.push(code(new ObjectFormat(routeVariable, objectVariable), objectValue));
        }
        return null;
    }

    @Override
    public T visitFuncCallResult_call(FuncCallResult_callContext ctx) {
        RouteVariable routeVariable = (RouteVariable) this.instStack.pop();
        FunCallRouteVariable funCall = code(new FunCallRouteVariable(routeVariable), ctx);
        List<AnyObjectContext> paramLists = ctx.anyObject();
        if (paramLists != null) {
            for (AnyObjectContext param : paramLists) {
                param.accept(this);
                Variable paramVar = (Variable) this.instStack.pop();
                funCall.addParam(paramVar);
            }
        }
        //
        this.instStack.push(funCall);
        FuncCallResultContext funcCallResult = ctx.funcCallResult();
        if (funcCallResult != null) {
            funcCallResult.accept(this);
        }
        return null;
    }

    @Override
    public T visitParamRoute(ParamRouteContext ctx) {
        // .根
        TerminalNode rouTerm = ctx.ROU();
        SpecialType special = specialType(rouTerm, SpecialType.Special_B);
        TerminalNode identifier = ctx.IDENTIFIER();
        TerminalNode string = ctx.STRING();
        StringToken rouNameToken = null;
        //
        Token enterToken = rouTerm != null ? rouTerm.getSymbol() : ctx.start;
        EnterRouteVariable enter = code(new EnterRouteVariable(RouteType.Params, special), enterToken);
        if (identifier != null) {
            rouNameToken = code(new StringToken(fixIdentifier(identifier)), identifier);
        } else {
            rouNameToken = code(new StringToken(string.getText()), string);
        }
        this.instStack.push(code(new NameRouteVariable(enter, rouNameToken), rouNameToken));
        //
        // .x{} 后面的继续路由
        RouteSubscriptContext routeSubscript = ctx.routeSubscript();
        if (routeSubscript != null) {
            routeSubscript.accept(this);
        }
        RouteNameSetContext routeNameSet = ctx.routeNameSet();
        if (routeNameSet != null) {
            routeNameSet.accept(this);
        }
        return null;
    }

    @Override
    public T visitSubExprRoute(SubExprRouteContext ctx) {
        TerminalNode rouTerm = ctx.ROU();
        SpecialType special = specialType(rouTerm, SpecialType.Special_B);
        //
        Token enterToken = rouTerm != null ? rouTerm.getSymbol() : ctx.start;
        EnterRouteVariable enter = code(new EnterRouteVariable(RouteType.Expr, special), enterToken);
        this.instStack.push(enter);
        //
        List<RouteSubscriptContext> subscriptContexts = ctx.routeSubscript();
        for (RouteSubscriptContext subContext : subscriptContexts) {
            subContext.accept(this);
        }
        //
        RouteNameSetContext routeNameSet = ctx.routeNameSet();
        if (routeNameSet != null) {
            routeNameSet.accept(this);
        }
        return null;
    }

    @Override
    public T visitNameExprRoute(NameExprRouteContext ctx) {
        TerminalNode rouTerm = ctx.ROU();
        SpecialType special = specialType(rouTerm, SpecialType.Special_B);
        //
        Token enterToken = rouTerm != null ? rouTerm.getSymbol() : ctx.start;
        EnterRouteVariable enter = code(new EnterRouteVariable(RouteType.Expr, special), enterToken);
        if (ctx.DOT() != null) {
            StringToken stringToken = code(new StringToken(""), rouTerm);
            this.instStack.push(code(new NameRouteVariable(enter, stringToken), ctx));
        } else {
            this.instStack.push(enter);
        }
        //
        RouteNameSetContext routeNameSet = ctx.routeNameSet();
        if (routeNameSet != null) {
            routeNameSet.accept(this);
        }
        return null;
    }

    @Override
    public T visitExprRoute(ExprRouteContext ctx) {
        TerminalNode rouTerm = ctx.ROU();
        SpecialType specialType = specialType(rouTerm, SpecialType.Special_A);
        //
        Token enterToken = rouTerm != null ? rouTerm.getSymbol() : ctx.start;
        EnterRouteVariable enter = code(new EnterRouteVariable(RouteType.Expr, specialType), enterToken);
        this.instStack.push(enter);
        //
        RouteNameSetContext routeNameSet = ctx.routeNameSet();
        if (routeNameSet != null) {
            routeNameSet.accept(this);
        }
        return null;
    }

    @Override
    public T visitExprFmtRoute(ExprFmtRouteContext ctx) {
        ctx.routeMapping().accept(this);
        RouteVariable routeVariable = (RouteVariable) this.instStack.pop();
        //
        ListValueContext listValue = ctx.listValue();
        ObjectValueContext objectValue = ctx.objectValue();
        //
        if (listValue != null) {
            listValue.accept(this);
            ListVariable listVariable = (ListVariable) this.instStack.pop();
            this.instStack.push(code(new ListFormat(routeVariable, listVariable), listValue));
        } else {
            objectValue.accept(this);
            ObjectVariable objectVariable = (ObjectVariable) this.instStack.pop();
            this.instStack.push(code(new ObjectFormat(routeVariable, objectVariable), objectValue));
        }
        return null;
    }

    @Override
    public T visitRouteNameSet(RouteNameSetContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public T visitRouteName(RouteNameContext ctx) {
        Object peek = this.instStack.peek();
        RouteVariable parent = null;
        if (peek instanceof RouteVariable) {
            this.instStack.pop();
            parent = (RouteVariable) peek;
        }
        TerminalNode itemNameTerm = ctx.IDENTIFIER();
        StringToken itemNameToken = code(new StringToken(fixIdentifier(itemNameTerm)), itemNameTerm);
        this.instStack.push(code(new NameRouteVariable(parent, itemNameToken), itemNameToken));
        //
        List<RouteSubscriptContext> subList = ctx.routeSubscript();
        if (subList != null) {
            for (RouteSubscriptContext subItem : subList) {
                subItem.accept(this);
            }
        }
        return null;
    }

    @Override
    public T visitRouteSubscript(RouteSubscriptContext ctx) {
        RouteVariable atNode = (RouteVariable) this.instStack.pop();
        TerminalNode intNode = ctx.INTEGER_NUM();
        TerminalNode stringNode = ctx.STRING();
        ExprContext exprContext = ctx.expr();
        //
        if (intNode != null) {
            IntegerToken subscriptToken = code(new IntegerToken(Integer.parseInt(intNode.getText())), intNode);
            this.instStack.push(code(new SubscriptRouteVariable(atNode, subscriptToken), intNode));
            return null;
        }
        if (stringNode != null) {
            StringToken subscriptToken = code(new StringToken(fixString(stringNode)), stringNode);
            this.instStack.push(code(new SubscriptRouteVariable(atNode, subscriptToken), stringNode));
            return null;
        }
        if (exprContext != null) {
            exprContext.accept(this);
            Expression expr = (Expression) this.instStack.pop();
            this.instStack.push(code(new SubscriptRouteVariable(atNode, expr), exprContext));
            return null;
        }
        throw newParseException(ctx.start, "parser failed -> visitRouteSubscript.");
    }

    @Override
    public T visitPrivilegeExpr(PrivilegeExprContext ctx) {
        //
        // .先处理优先级
        ExprContext expr = ctx.expr();
        expr.accept(this);
        this.instStack.push(code(new PrivilegeExpression((Expression) this.instStack.pop()), ctx));
        //
        // .后处理可能存在的多元计算
        DyadicExprContext dyadicExpr = ctx.dyadicExpr();
        TernaryExprContext ternaryExpr = ctx.ternaryExpr();
        if (dyadicExpr != null) {
            dyadicExpr.accept(this);
            return null;
        }
        if (ternaryExpr != null) {
            ternaryExpr.accept(this);
            return null;
        }
        return null;
    }

    @Override
    public T visitUnaryExpr(UnaryExprContext ctx) {
        TerminalNode unaryOper = null;
        unaryOper = operSwitch(unaryOper, ctx.NOT());
        unaryOper = operSwitch(unaryOper, ctx.MINUS());
        unaryOper = operSwitch(unaryOper, ctx.PLUS());
        //
        ctx.expr().accept(this);
        Expression expr = (Expression) this.instStack.pop();
        SymbolToken symbolToken = code(new SymbolToken(unaryOper.getText()), unaryOper);
        this.instStack.push(code(new UnaryExpression(expr, symbolToken), ctx));
        return null;
    }

    @Override
    public T visitMultipleExpr(MultipleExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public T visitDyadicExpr(DyadicExprContext ctx) {
        TerminalNode dyadicOper = null;
        dyadicOper = operSwitch(dyadicOper, ctx.PLUS());
        dyadicOper = operSwitch(dyadicOper, ctx.MINUS());
        dyadicOper = operSwitch(dyadicOper, ctx.MUL());
        dyadicOper = operSwitch(dyadicOper, ctx.DIV());
        dyadicOper = operSwitch(dyadicOper, ctx.DIV2());
        dyadicOper = operSwitch(dyadicOper, ctx.MOD());
        dyadicOper = operSwitch(dyadicOper, ctx.LBT());
        dyadicOper = operSwitch(dyadicOper, ctx.RBT());
        dyadicOper = operSwitch(dyadicOper, ctx.AND());
        dyadicOper = operSwitch(dyadicOper, ctx.OR());
        dyadicOper = operSwitch(dyadicOper, ctx.NOT());
        dyadicOper = operSwitch(dyadicOper, ctx.XOR());
        dyadicOper = operSwitch(dyadicOper, ctx.LSHIFT());
        dyadicOper = operSwitch(dyadicOper, ctx.RSHIFT());
        dyadicOper = operSwitch(dyadicOper, ctx.RSHIFT2());
        dyadicOper = operSwitch(dyadicOper, ctx.GT());
        dyadicOper = operSwitch(dyadicOper, ctx.GE());
        dyadicOper = operSwitch(dyadicOper, ctx.LT());
        dyadicOper = operSwitch(dyadicOper, ctx.LE());
        dyadicOper = operSwitch(dyadicOper, ctx.EQ());
        dyadicOper = operSwitch(dyadicOper, ctx.NE());
        dyadicOper = operSwitch(dyadicOper, ctx.SC_OR());
        dyadicOper = operSwitch(dyadicOper, ctx.SC_AND());
        //
        ctx.expr().accept(this);
        Expression expr2 = (Expression) this.instStack.pop();
        Expression expr1 = (Expression) this.instStack.pop();
        SymbolToken symbolToken = code(new SymbolToken(dyadicOper.getText()), dyadicOper);
        this.instStack.push(code(new DyadicExpression(expr1, symbolToken, expr2), ctx));
        return null;
    }

    @Override
    public T visitTernaryExpr(TernaryExprContext ctx) {
        visitChildren(ctx);
        Expression expr3 = (Expression) this.instStack.pop();
        Expression expr2 = (Expression) this.instStack.pop();
        Expression expr1 = (Expression) this.instStack.pop();
        this.instStack.push(code(new TernaryExpression(expr1, expr2, expr3), ctx));
        return null;
    }

    @Override
    public T visitAtomExpr(AtomExprContext ctx) {
        visitChildren(ctx);
        if (this.instStack.peek() instanceof Expression) {
            return null;
        } else {
            Variable var = (Variable) this.instStack.pop();
            this.instStack.push(code(new AtomExpression(var), ctx));
            return null;
        }
    }

    @Override
    public T visitExtBlock(ExtBlockContext ctx) {
        TerminalNode fragmentNameToke = ctx.IDENTIFIER();
        String fragmentName = fixIdentifier(fragmentNameToke);
        StringBuilder fragmentString = new StringBuilder();
        List<TerminalNode> chars = ctx.CHAR();
        chars.forEach(terminalNode -> {
            fragmentString.append(terminalNode.getText());
        });
        //
        boolean isBatch = ctx.LSBT() != null;
        StringToken fragmentNameToken = code(new StringToken(fragmentName), fragmentNameToke);
        StringToken fragmentStringToken = code(new StringToken(fragmentString.toString()), chars);
        FragmentVariable fragmentVariable = code(new FragmentVariable(fragmentNameToken, fragmentStringToken, isBatch), ctx);
        ExtParamsContext paramsContext = ctx.extParams();
        if (paramsContext != null) {
            for (TerminalNode terminalNode : paramsContext.IDENTIFIER()) {
                StringToken paramNameToken = code(new StringToken(fixIdentifier(terminalNode)), terminalNode);
                fragmentVariable.getParamList().add(paramNameToken);
            }
        }
        this.instStack.push(fragmentVariable);
        return null;
    }

    @Override
    public T visitExtParams(ExtParamsContext ctx) {
        return null;
    }

    private SpecialType specialType(TerminalNode rou, SpecialType defaultType) {
        if (rou == null) {
            return defaultType;
        }
        String rouType = rou.getText();
        if ("#".equals(rouType)) {
            return SpecialType.Special_A;
        }
        if ("$".equals(rouType)) {
            return SpecialType.Special_B;
        }
        if ("@".equals(rouType)) {
            return SpecialType.Special_C;
        }
        throw newParseException(rou.getSymbol(), "rouType '" + rouType + "' is not supported");
    }

    private TerminalNode operSwitch(TerminalNode first, TerminalNode second) {
        return first != null ? first : second;
    }

    private ParseException newParseException(Token token, String errorMessage) {
        return new ParseException(token.getLine(), token.getStartIndex(), errorMessage);
    }

    private String fixIdentifier(TerminalNode identifierNode) {
        String string = identifierNode.getText();
        if (string.charAt(0) == '`') {
            string = string.substring(1, string.length() - 1);
        }
        return string;
    }

    private String fixString(TerminalNode stringNode) {
        String nodeText = stringNode.getText();
        return nodeText.substring(1, nodeText.length() - 1);
    }
}