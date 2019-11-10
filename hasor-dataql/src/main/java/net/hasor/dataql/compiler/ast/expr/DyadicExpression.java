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
import net.hasor.dataql.Option;
import net.hasor.dataql.compiler.ast.AstVisitor;
import net.hasor.dataql.compiler.ast.Expression;
import net.hasor.dataql.compiler.ast.FormatWriter;
import net.hasor.dataql.compiler.ast.InstVisitorContext;

import java.io.IOException;

/**
 * 二元运算表达式
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class DyadicExpression implements Expression {
    private Expression fstExpression;   //第一个表达式
    private String     dyadicSymbol;    //运算符
    private Expression secExpression;   //第二个表达式

    public DyadicExpression(Expression fstExpression, String dyadicSymbol, Expression secExpression) {
        this.fstExpression = fstExpression;
        this.dyadicSymbol = dyadicSymbol;
        this.secExpression = secExpression;
    }

    public Expression getFstExpression() {
        return fstExpression;
    }

    public String getDyadicSymbol() {
        return dyadicSymbol;
    }

    public Expression getSecExpression() {
        return secExpression;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visitInst(new InstVisitorContext(this) {
            @Override
            public void visitChildren(AstVisitor astVisitor) {
                fstExpression.accept(astVisitor);
                secExpression.accept(astVisitor);
            }
        });
    }

    @Override
    public void doFormat(int depth, Option formatOption, FormatWriter writer) throws IOException {
        this.fstExpression.doFormat(depth, formatOption, writer);
        writer.write(" " + dyadicSymbol + " ");
        this.secExpression.doFormat(depth, formatOption, writer);
    }
}