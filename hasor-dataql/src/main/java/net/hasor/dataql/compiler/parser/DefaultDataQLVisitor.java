package net.hasor.dataql.compiler.parser;
import net.hasor.dataql.compiler.ast.Expression;
import net.hasor.dataql.compiler.ast.RouteVariable;
import net.hasor.dataql.compiler.ast.Variable;
import net.hasor.dataql.compiler.ast.expr.*;
import net.hasor.dataql.compiler.ast.format.ListFormat;
import net.hasor.dataql.compiler.ast.format.ObjectFormat;
import net.hasor.dataql.compiler.ast.inst.*;
import net.hasor.dataql.compiler.ast.inst.ImportInst.ImportType;
import net.hasor.dataql.compiler.ast.value.*;
import net.hasor.dataql.compiler.ast.value.EnterRouteVariable.RouteType;
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
 * This class provides an empty implementation of {@link DataQLVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public class DefaultDataQLVisitor<T> extends AbstractParseTreeVisitor<T> implements DataQLVisitor<T> {
    private Stack<Object> instStack = new Stack<>();

    @Override
    public T visitRootInstSet(RootInstSetContext ctx) {
        this.instStack.push(new RootBlockSet());
        List<OptionInstContext> optionList = ctx.optionInst();
        List<ImportInstContext> importList = ctx.importInst();
        List<BlockSetContext> blockSetList = ctx.blockSet();
        //
        if (optionList != null) {
            for (OptionInstContext option : optionList) {
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
    public T visitOptionInst(OptionInstContext ctx) {
        this.instStack.push(ctx.IDENTIFIER().getText());
        visitChildren(ctx);
        //
        PrimitiveVariable optValue = (PrimitiveVariable) this.instStack.pop();
        String optKey = (String) this.instStack.pop();
        OptionInst optionInst = new OptionInst(optKey, optValue);
        ((RootBlockSet) this.instStack.peek()).addOptionInst(optionInst);
        return null;
    }

    @Override
    public T visitImportInst(ImportInstContext ctx) {
        visitChildren(ctx);
        //
        ImportType importType = ImportType.ClassType;
        String importResource = ctx.STRING().getText();
        String asName = ctx.IDENTIFIER().getText();
        TerminalNode rouNode = ctx.ROU();
        if (rouNode != null) {
            if ("@".equals(rouNode.getText())) {
                importType = ImportType.Resource;
            } else {
                throw new RuntimeException("paser failed -> visitImportInst.");
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
        PolymericObjectContext polymeric = ctx.polymericObject();
        LambdaDefContext lambda = ctx.lambdaDef();
        if (lambda != null) {
            lambda.accept(this);
        } else {
            polymeric.accept(this);
        }
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
        throw new RuntimeException("paser failed -> visitBreakInst.");
    }

    @Override
    public T visitLambdaDef(LambdaDefContext ctx) {
        this.instStack.push(new LambdaVariable());
        LambdaDefParametersContext defParameters = ctx.lambdaDefParameters();
        if (defParameters != null) {
            defParameters.accept(this);
        }
        //
        ctx.blockSet().accept(this);
        InstSet instSet = (InstSet) this.instStack.pop();
        ((LambdaVariable) this.instStack.peek()).addInstSet(instSet);
        return null;
    }

    @Override
    public T visitLambdaDefParameters(LambdaDefParametersContext ctx) {
        List<TerminalNode> identifierList = ctx.IDENTIFIER();
        if (identifierList != null) {
            for (TerminalNode terminalNode : identifierList) {
                ((LambdaVariable) this.instStack.peek()).addParam(terminalNode.getText());
            }
        }
        return null;
    }

    @Override
    public T visitConvertObject(ConvertObjectContext ctx) {
        FuncCallContext funcCall = ctx.funcCall();
        RouteCallContext routeCall = ctx.routeCall();
        if (funcCall != null) {
            funcCall.accept(this);
        } else {
            routeCall.accept(this);
        }
        //
        ListValueContext listValue = ctx.listValue();
        ObjectValueContext objectValue = ctx.objectValue();
        //
        if (listValue != null) {
            listValue.accept(this);
            ListVariable listVariable = (ListVariable) this.instStack.pop();
            RouteVariable routeVariable = (RouteVariable) this.instStack.pop();
            this.instStack.push(new ListFormat(routeVariable, listVariable));
        } else {
            objectValue.accept(this);
            ObjectVariable objectVariable = (ObjectVariable) this.instStack.pop();
            RouteVariable routeVariable = (RouteVariable) this.instStack.pop();
            this.instStack.push(new ObjectFormat(routeVariable, objectVariable));
        }
        return null;
    }

    @Override
    public T visitConvertRaw(ConvertRawContext ctx) {
        return visitChildren(ctx);
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
        String fieldKey = ctx.STRING().getText();
        PolymericObjectContext polymericObject = ctx.polymericObject();
        if (polymericObject != null) {
            polymericObject.accept(this);
            Variable valueExp = (Variable) this.instStack.pop();
            objectVariable.addField(fieldKey, valueExp);
        } else {
            EnterRouteVariable enterRoute = new EnterRouteVariable(RouteType.Context, null);
            NameRouteVariable nameRoute = new NameRouteVariable(enterRoute, fieldKey);
            objectVariable.addField(fieldKey, nameRoute);
        }
        return null;
    }

    @Override
    public T visitListValue(ListValueContext ctx) {
        ListVariable listVariable = new ListVariable();
        List<PolymericObjectContext> polymericList = ctx.polymericObject();
        if (polymericList != null) {
            for (PolymericObjectContext polymeric : polymericList) {
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
        String text = ctx.STRING().getText();
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
                this.instStack.push(new PrimitiveVariable(bigInt.byteValue(), ValueType.Number));
                return null;
            }
            if (bitLength < 16) {
                this.instStack.push(new PrimitiveVariable(bigInt.shortValue(), ValueType.Number));
                return null;
            }
            if (bitLength < 32) {
                this.instStack.push(new PrimitiveVariable(bigInt.intValue(), ValueType.Number));
                return null;
            }
            if (bitLength < 64) {
                this.instStack.push(new PrimitiveVariable(bigInt.longValue(), ValueType.Number));
                return null;
            }
            this.instStack.push(new PrimitiveVariable(bigInt, ValueType.Number));
            return null;
        } else {
            BigDecimal bigDec = new BigDecimal(decimalNode.getText());
            int precisionLength = bigDec.precision();
            if (precisionLength < 8 && !Float.isInfinite(bigDec.floatValue())) {
                this.instStack.push(new PrimitiveVariable(bigDec.floatValue(), ValueType.Number));
                return null;
            }
            if (precisionLength < 16 && !Double.isInfinite(bigDec.doubleValue())) {
                this.instStack.push(new PrimitiveVariable(bigDec.doubleValue(), ValueType.Number));
                return null;
            }
            this.instStack.push(new PrimitiveVariable(bigDec, ValueType.Number));
            return null;
        }
    }

    @Override
    public T visitFuncCall(FuncCallContext ctx) {
        ctx.routeCall().accept(this);
        //
        RouteVariable routeVariable = (RouteVariable) this.instStack.pop();
        FunCallRouteVariable funCall = new FunCallRouteVariable(routeVariable);
        List<PolymericObjectContext> paramLists = ctx.polymericObject();
        if (paramLists != null) {
            for (PolymericObjectContext param : paramLists) {
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
    public T visitFuncCallResult(FuncCallResultContext ctx) {
        NormalRouteCopyContext normalRoute = ctx.normalRouteCopy();
        List<RouteSubscriptContext> subscriptList = ctx.routeSubscript();
        //
        FunCallRouteVariable funCallRoute = (FunCallRouteVariable) this.instStack.pop();
        this.instStack.push(new EnterRouteVariable(RouteType.Enter, funCallRoute));
        //
        if (normalRoute != null) {
            normalRoute.accept(this);
            return null;
        }
        if (subscriptList != null) {
            for (RouteSubscriptContext context : subscriptList) {
                context.accept(this);
            }
            return null;
        }
        throw new RuntimeException("paser failed -> visitFuncCallResult.");
    }

    @Override
    public T visitSpecialRoute(SpecialRouteContext ctx) {
        String rouType = ctx.ROU().getText();
        RouteType routeType = RouteType.Context;
        if ("#".equals(rouType)) {
            routeType = RouteType.Special_A;
        }
        if ("$".equals(rouType)) {
            routeType = RouteType.Special_B;
        }
        if ("@".equals(rouType)) {
            routeType = RouteType.Special_C;
        }
        //
        this.instStack.push(new EnterRouteVariable(routeType, null));
        ctx.normalRouteCopy().accept(this);
        return null;
    }

    @Override
    public T visitNormalRoute(NormalRouteContext ctx) {
        this.instStack.push(new EnterRouteVariable(RouteType.Context, null));
        return visitChildren(ctx);
    }

    @Override
    public T visitNormalRouteCopy(NormalRouteCopyContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public T visitRouteItem(RouteItemContext ctx) {
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
            this.instStack.push(new SubscriptRouteVariable(SubType.String, atNode, stringNode.getText()));
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

    private TerminalNode operSwitch(TerminalNode first, TerminalNode second) {
        return first != null ? first : second;
    }

    @Override
    public T visitTernaryExpr(TernaryExprContext ctx) {
        ctx.accept(this);
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
}