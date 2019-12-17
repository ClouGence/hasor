package net.hasor.dataql.compiler.parser;
import net.hasor.dataql.compiler.ast.Expression;
import net.hasor.dataql.compiler.ast.RouteVariable;
import net.hasor.dataql.compiler.ast.Variable;
import net.hasor.dataql.compiler.ast.expr.*;
import net.hasor.dataql.compiler.ast.fmt.ListFormat;
import net.hasor.dataql.compiler.ast.fmt.ObjectFormat;
import net.hasor.dataql.compiler.ast.inst.*;
import net.hasor.dataql.compiler.ast.inst.ImportInst.ImportType;
import net.hasor.dataql.compiler.ast.value.*;
import net.hasor.dataql.compiler.ast.value.EnterRouteVariable.RouteType;
import net.hasor.dataql.compiler.ast.value.EnterRouteVariable.SpecialType;
import net.hasor.dataql.compiler.ast.value.PrimitiveVariable.ValueType;
import net.hasor.dataql.compiler.ast.value.SubscriptRouteVariable.SubType;
import net.hasor.dataql.compiler.parser.DataQLParser.*;
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
 */
public class DefaultDataQLVisitor<T> extends AbstractParseTreeVisitor<T> implements DataQLParserVisitor<T> {
    private Stack<Object> instStack = new Stack<>();

    @Override
    public T visitRootInstSet(RootInstSetContext ctx) {
        this.instStack.push(new RootBlockSet());
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
                rootBlockSet.addInstSet(instSet);
            }
        }
        //
        return (T) this.instStack.pop();
    }

    @Override
    public T visitHintInst(HintInstContext ctx) {
        this.instStack.push(ctx.IDENTIFIER().getText());
        visitChildren(ctx);
        //
        PrimitiveVariable optValue = (PrimitiveVariable) this.instStack.pop();
        String optKey = (String) this.instStack.pop();
        HintInst hintInst = new HintInst(optKey, optValue);
        ((RootBlockSet) this.instStack.peek()).addOptionInst(hintInst);
        return null;
    }

    @Override
    public T visitImportInst(ImportInstContext ctx) {
        visitChildren(ctx);
        //
        ImportType importType = ImportType.ClassType;
        String importResource = fixString(ctx.STRING());
        String asName = ctx.IDENTIFIER().getText();
        TerminalNode rouNode = ctx.ROU();
        if (rouNode != null) {
            if ("@".equals(rouNode.getText())) {
                importType = ImportType.Resource;
            } else {
                throw new RuntimeException("parser failed -> visitImportInst.");
            }
        }
        //
        ImportInst importInst = new ImportInst(importType, importResource, asName);
        ((RootBlockSet) this.instStack.peek()).addImportInst(importInst);
        return null;
    }

    @Override
    public T visitMultipleInst(MultipleInstContext ctx) {
        this.instStack.push(new InstSet());
        visitChildren(ctx);
        return null;
    }

    @Override
    public T visitSingleInst(SingleInstContext ctx) {
        this.instStack.push(new InstSet());
        visitChildren(ctx);
        return null;
    }

    @Override
    public T visitVarInst(VarInstContext ctx) {
        visitChildren(ctx);
        //
        VarInst varInst = new VarInst(ctx.IDENTIFIER().getText(), (Variable) this.instStack.pop());
        ((InstSet) this.instStack.peek()).addInst(varInst);
        return null;
    }

    @Override
    public T visitRunInst(RunInstContext ctx) {
        visitChildren(ctx);
        //
        RunInst varInst = new RunInst((Variable) this.instStack.pop());
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
        SwitchInst switchInst = new SwitchInst();
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
                    switchInst.addElseif(new AtomExpression(expr), instSet);
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
        int breakCode = 0;
        if (breakCodeNode != null) {
            breakCode = Integer.parseInt(breakCodeNode.getText());
        }
        //
        AnyObjectContext anyObject = ctx.anyObject();
        anyObject.accept(this);
        Variable variable = (Variable) this.instStack.pop();
        //
        if (ctx.EXIT() != null) {
            ((InstSet) this.instStack.peek()).addInst(new ExitInst(breakCode, variable));
            return null;
        }
        if (ctx.THROW() != null) {
            ((InstSet) this.instStack.peek()).addInst(new ThrowInst(breakCode, variable));
            return null;
        }
        if (ctx.RETURN() != null) {
            ((InstSet) this.instStack.peek()).addInst(new ReturnInst(breakCode, variable));
            return null;
        }
        throw new RuntimeException("parser failed -> visitBreakInst.");
    }

    @Override
    public T visitLambdaDef(LambdaDefContext ctx) {
        LambdaVariable lambdaVariable = new LambdaVariable();
        List<TerminalNode> identifierList = ctx.IDENTIFIER();
        if (identifierList != null) {
            for (TerminalNode terminalNode : identifierList) {
                lambdaVariable.addParam(terminalNode.getText());
            }
        }
        //
        this.instStack.push(lambdaVariable);
        //
        ctx.blockSet().accept(this);
        InstSet instSet = (InstSet) this.instStack.pop();
        ((LambdaVariable) this.instStack.peek()).addInstSet(instSet);
        return null;
    }

    @Override
    public T visitObjectValue(ObjectValueContext ctx) {
        ObjectVariable objectVariable = new ObjectVariable();
        this.instStack.push(objectVariable);
        return visitChildren(ctx);
    }

    @Override
    public T visitObjectKeyValue(ObjectKeyValueContext ctx) {
        ObjectVariable objectVariable = (ObjectVariable) this.instStack.peek();
        String fieldKey = fixString(ctx.STRING());
        AnyObjectContext polymericObject = ctx.anyObject();
        if (polymericObject != null) {
            polymericObject.accept(this);
            Variable valueExp = (Variable) this.instStack.pop();
            objectVariable.addField(fieldKey, valueExp);
        } else {
            EnterRouteVariable enterRoute = new EnterRouteVariable(RouteType.Normal, null);
            NameRouteVariable nameRoute = new NameRouteVariable(enterRoute, fieldKey);
            objectVariable.addField(fieldKey, nameRoute);
        }
        return null;
    }

    @Override
    public T visitListValue(ListValueContext ctx) {
        ListVariable listVariable = new ListVariable();
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
        this.instStack.push(new PrimitiveVariable(text, ValueType.String));
        return null;
    }

    @Override
    public T visitNullValue(NullValueContext ctx) {
        this.instStack.push(new PrimitiveVariable(null, ValueType.Null));
        return null;
    }

    @Override
    public T visitBooleanValue(BooleanValueContext ctx) {
        boolean boolValue = ctx.TRUE() != null;
        this.instStack.push(new PrimitiveVariable(boolValue, ValueType.Boolean));
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
        if (bitNode != null) {
            radix = 2;
            radixNumber = bitNode.getText();
            radixNumber = (radixNumber.charAt(0) == '-') ? ("-" + radixNumber.substring(3)) : radixNumber.substring(2);
        }
        if (octNode != null) {
            radix = 8;
            radixNumber = octNode.getText();
            radixNumber = (radixNumber.charAt(0) == '-') ? ("-" + radixNumber.substring(3)) : radixNumber.substring(2);
        }
        if (intNode != null) {
            radix = 10;
            radixNumber = intNode.getText();
        }
        if (hexNode != null) {
            radix = 16;
            radixNumber = hexNode.getText();
            radixNumber = (radixNumber.charAt(0) == '-') ? ("-" + radixNumber.substring(3)) : radixNumber.substring(2);
        }
        if (radixNumber != null) {
            BigInteger bigInt = new BigInteger(radixNumber, radix);
            int bitLength = bigInt.bitLength();
            if (bitLength < 8) {
                this.instStack.push(new PrimitiveVariable(bigInt.byteValue(), ValueType.Number, radix));
                return null;
            }
            if (bitLength < 16) {
                this.instStack.push(new PrimitiveVariable(bigInt.shortValue(), ValueType.Number, radix));
                return null;
            }
            if (bitLength < 32) {
                this.instStack.push(new PrimitiveVariable(bigInt.intValue(), ValueType.Number, radix));
                return null;
            }
            if (bitLength < 64) {
                this.instStack.push(new PrimitiveVariable(bigInt.longValue(), ValueType.Number, radix));
                return null;
            }
            this.instStack.push(new PrimitiveVariable(bigInt, ValueType.Number, radix));
            return null;
        } else {
            BigDecimal bigDec = new BigDecimal(decimalNode.getText());
            int precisionLength = bigDec.precision();
            if (precisionLength < 8 && !Float.isInfinite(bigDec.floatValue())) {
                this.instStack.push(new PrimitiveVariable(bigDec.floatValue(), ValueType.Number, radix));
                return null;
            }
            if (precisionLength < 16 && !Double.isInfinite(bigDec.doubleValue())) {
                this.instStack.push(new PrimitiveVariable(bigDec.doubleValue(), ValueType.Number, radix));
                return null;
            }
            this.instStack.push(new PrimitiveVariable(bigDec, ValueType.Number, radix));
            return null;
        }
    }

    @Override
    public T visitFuncCall(FuncCallContext ctx) {
        ctx.routeMapping().accept(this);
        //
        RouteVariable routeVariable = (RouteVariable) this.instStack.pop();
        FunCallRouteVariable funCall = new FunCallRouteVariable(routeVariable);
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
            this.instStack.push(new ListFormat(routeVariable, listVariable));
        } else {
            objectValue.accept(this);
            ObjectVariable objectVariable = (ObjectVariable) this.instStack.pop();
            this.instStack.push(new ObjectFormat(routeVariable, objectVariable));
        }
        return null;
    }

    @Override
    public T visitFuncCallResult_call(FuncCallResult_callContext ctx) {
        RouteVariable routeVariable = (RouteVariable) this.instStack.pop();
        FunCallRouteVariable funCall = new FunCallRouteVariable(routeVariable);
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
    public T visitSpecialRoute(SpecialRouteContext ctx) {
        String rouType = ctx.ROU().getText();
        SpecialType specialType = null;
        if ("#".equals(rouType)) {
            specialType = SpecialType.Special_A;
        }
        if ("$".equals(rouType)) {
            specialType = SpecialType.Special_B;
        }
        if ("@".equals(rouType)) {
            specialType = SpecialType.Special_C;
        }
        // .根
        TerminalNode identifier = ctx.IDENTIFIER();
        TerminalNode string = ctx.STRING();
        String rouName = null;
        EnterRouteVariable enter = new EnterRouteVariable(RouteType.Special, specialType);
        if (identifier != null) {
            rouName = identifier.getText();
        } else {
            rouName = string.getText();
        }
        this.instStack.push(new NameRouteVariable(enter, rouName));
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
    public T visitNormalRoute(NormalRouteContext ctx) {
        SpecialType specialType = SpecialType.Special_A;
        if (ctx.ROU() != null) {
            String rouType = ctx.ROU().getText();
            if ("#".equals(rouType)) {
                specialType = SpecialType.Special_A;
            }
            if ("$".equals(rouType)) {
                specialType = SpecialType.Special_B;
            }
            if ("@".equals(rouType)) {
                specialType = SpecialType.Special_C;
            }
        }
        //
        EnterRouteVariable enter = new EnterRouteVariable(RouteType.Normal, specialType);
        this.instStack.push(enter);
        //
        RouteNameSetContext routeNameSet = ctx.routeNameSet();
        if (routeNameSet != null) {
            routeNameSet.accept(this);
        }
        return null;
    }

    @Override
    public T visitConvertRoute(ConvertRouteContext ctx) {
        ctx.routeMapping().accept(this);
        RouteVariable routeVariable = (RouteVariable) this.instStack.pop();
        //
        ListValueContext listValue = ctx.listValue();
        ObjectValueContext objectValue = ctx.objectValue();
        if (listValue != null) {
            listValue.accept(this);
            ListVariable listVariable = (ListVariable) this.instStack.pop();
            this.instStack.push(new ListFormat(routeVariable, listVariable));
        } else {
            objectValue.accept(this);
            ObjectVariable objectVariable = (ObjectVariable) this.instStack.pop();
            this.instStack.push(new ObjectFormat(routeVariable, objectVariable));
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
        String itemName = ctx.IDENTIFIER().getText();
        this.instStack.push(new NameRouteVariable(parent, itemName));
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
        //
        if (intNode != null) {
            this.instStack.push(new SubscriptRouteVariable(SubType.Integer, atNode, intNode.getText()));
            return null;
        } else {
            this.instStack.push(new SubscriptRouteVariable(SubType.String, atNode, fixString(stringNode)));
            return null;
        }
    }

    @Override
    public T visitPrivilegeExpr(PrivilegeExprContext ctx) {
        //
        // .先处理优先级
        ExprContext expr = ctx.expr();
        expr.accept(this);
        this.instStack.push(new PrivilegeExpression((Expression) this.instStack.pop()));
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
        this.instStack.push(new UnaryExpression(expr, unaryOper.getText()));
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
        this.instStack.push(new DyadicExpression(expr1, dyadicOper.getText(), expr2));
        return null;
    }

    @Override
    public T visitTernaryExpr(TernaryExprContext ctx) {
        visitChildren(ctx);
        Expression expr3 = (Expression) this.instStack.pop();
        Expression expr2 = (Expression) this.instStack.pop();
        Expression expr1 = (Expression) this.instStack.pop();
        this.instStack.push(new TernaryExpression(expr1, expr2, expr3));
        return null;
    }

    @Override
    public T visitAtomExpr(AtomExprContext ctx) {
        visitChildren(ctx);
        if (this.instStack.peek() instanceof Expression) {
            return null;
        } else {
            Variable var = (Variable) this.instStack.pop();
            this.instStack.push(new AtomExpression(var));
            return null;
        }
    }

    @Override
    public T visitExtBlock(ExtBlockContext ctx) {
        String fragmentName = ctx.IDENTIFIER().getText();
        StringBuilder fragmentString = new StringBuilder();
        List<TerminalNode> chars = ctx.CHAR();
        if (chars != null) {
            chars.forEach(terminalNode -> {
                fragmentString.append(terminalNode.getText());
            });
        }
        //
        FragmentVariable fragmentVariable = new FragmentVariable(fragmentName, fragmentString.toString());
        ExtParamsContext paramsContext = ctx.extParams();
        if (paramsContext != null) {
            for (TerminalNode terminalNode : paramsContext.IDENTIFIER()) {
                fragmentVariable.getParamList().add(terminalNode.getText());
            }
        }
        this.instStack.push(fragmentVariable);
        return null;
    }

    @Override
    public T visitExtParams(ExtParamsContext ctx) {
        return null;
    }

    private TerminalNode operSwitch(TerminalNode first, TerminalNode second) {
        return first != null ? first : second;
    }

    private String fixString(TerminalNode stringNode) {
        String nodeText = stringNode.getText();
        return nodeText.substring(1, nodeText.length() - 1);
    }
}