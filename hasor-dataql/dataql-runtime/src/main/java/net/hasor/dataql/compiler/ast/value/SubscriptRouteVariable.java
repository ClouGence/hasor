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
package net.hasor.dataql.compiler.ast.value;
import net.hasor.dataql.Hints;
import net.hasor.dataql.compiler.ast.*;
import net.hasor.dataql.compiler.ast.expr.AtomExpression;
import net.hasor.dataql.compiler.ast.expr.PrivilegeExpression;
import net.hasor.dataql.compiler.ast.token.IntegerToken;
import net.hasor.dataql.compiler.ast.token.StringToken;
import net.hasor.dataql.compiler.ast.value.EnterRouteVariable.RouteType;
import net.hasor.dataql.compiler.ast.value.EnterRouteVariable.SpecialType;

import java.io.IOException;

/**
 * 对 RouteVariable 的下标操作
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class SubscriptRouteVariable extends AstBasic implements Variable, RouteVariable {
    public static enum SubType {
        String, Integer, Expr
    }

    private final RouteVariable parent;
    private final SubType       subType;
    private       StringToken   subValue;
    private       Expression    exprValue;

    public SubscriptRouteVariable(RouteVariable parent, IntegerToken subValue) {
        this.subType = SubType.Integer;
        this.parent = parent;
        this.subValue = subValue.toStringToken();
    }

    public SubscriptRouteVariable(RouteVariable parent, StringToken subValue) {
        this.subType = SubType.String;
        this.parent = parent;
        this.subValue = subValue;
    }

    public SubscriptRouteVariable(RouteVariable parent, Expression exprValue) {
        this.subType = SubType.Expr;
        this.parent = parent;
        this.exprValue = exprValue;
    }

    @Override
    public RouteVariable getParent() {
        return this.parent;
    }

    public SubType getSubType() {
        return subType;
    }

    public StringToken getSubValue() {
        return subValue;
    }

    public Expression getExprValue() {
        return exprValue;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        if (this.parent != null) {
            this.parent.accept(astVisitor);
        }
        astVisitor.visitInst(new InstVisitorContext(this) {
            @Override
            public void visitChildren(AstVisitor astVisitor) {
            }
        });
    }

    @Override
    public void doFormat(int depth, Hints formatOption, FormatWriter writer) throws IOException {
        this.parent.doFormat(depth, formatOption, writer);
        if (this.parent instanceof EnterRouteVariable) {
            RouteType routeType = ((EnterRouteVariable) parent).getRouteType();
            SpecialType specialType = ((EnterRouteVariable) parent).getSpecialType();
            if (specialType == null) {
                specialType = EnterRouteVariable.SpecialType.Special_A;
            }
            writer.write(specialType.getCode());
        }
        //
        if (subType == SubType.String) {
            String newValue = subValue.getValue().replace(String.valueOf(quoteChar), "\\" + quoteChar);
            writer.write("[" + quoteChar + newValue + quoteChar + "]");
        } else if (subType == SubType.Integer) {
            writer.write("[" + subValue.getValue() + "]");
        } else {
            writer.write("[");
            Variable varExp = this.exprValue;
            while (true) {
                if (varExp instanceof PrivilegeExpression) {
                    varExp = ((PrivilegeExpression) varExp).getExpression();
                    continue;
                }
                break;
            }
            if (varExp instanceof AtomExpression) {
                varExp = ((AtomExpression) varExp).getVariableExpression();
            }
            if (varExp instanceof EnterRouteVariable) {
                writer.write(((EnterRouteVariable) varExp).getSpecialType().getCode());
            } else {
                varExp.doFormat(depth, formatOption, writer);
            }
            writer.write("]");
        }
    }
}