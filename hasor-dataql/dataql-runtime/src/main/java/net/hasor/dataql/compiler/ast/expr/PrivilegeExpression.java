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
package net.hasor.dataql.compiler.ast.expr;
import net.hasor.dataql.Hints;
import net.hasor.dataql.compiler.ast.AstVisitor;
import net.hasor.dataql.compiler.ast.CodeLocation.CodeLocationInfo;
import net.hasor.dataql.compiler.ast.Expression;
import net.hasor.dataql.compiler.ast.FormatWriter;
import net.hasor.dataql.compiler.ast.InstVisitorContext;

import java.io.IOException;

/**
 * 权限提升，用于表示表达式中的括号
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class PrivilegeExpression extends CodeLocationInfo implements Expression {
    private final Expression expression;

    public PrivilegeExpression(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return "( " + this.expression.toString() + " )";
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visitInst(new InstVisitorContext(this) {
            @Override
            public void visitChildren(AstVisitor astVisitor) {
                expression.accept(astVisitor);
            }
        });
    }

    @Override
    public void doFormat(int depth, Hints formatOption, FormatWriter writer) throws IOException {
        if (this.expression instanceof PrivilegeExpression) {
            this.expression.doFormat(depth, formatOption, writer);
        } else {
            writer.write("(");
            this.expression.doFormat(depth, formatOption, writer);
            writer.write(")");
        }
    }
}