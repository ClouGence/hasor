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
package net.hasor.dataql.parser.ast.expr;
import net.hasor.dataql.Hints;
import net.hasor.dataql.parser.ast.AstVisitor;
import net.hasor.dataql.parser.ast.Expression;
import net.hasor.dataql.parser.ast.FormatWriter;
import net.hasor.dataql.parser.ast.InstVisitorContext;
import net.hasor.dataql.parser.location.BlockLocation;

import java.io.IOException;

/**
 * 三元运算表达式
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class TernaryExpression extends BlockLocation implements Expression {
    private final Expression testExpression;  //三元运算符，条件表达式
    private final Expression thenExpression;  //第一个表达式
    private final Expression elseExpression;  //第二个表达式

    public TernaryExpression(Expression testExp, Expression thenExp, Expression elseExp) {
        this.testExpression = testExp;
        this.thenExpression = thenExp;
        this.elseExpression = elseExp;
    }

    public Expression getTestExpression() {
        return testExpression;
    }

    public Expression getThenExpression() {
        return thenExpression;
    }

    public Expression getElseExpression() {
        return elseExpression;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visitInst(new InstVisitorContext(this) {
            @Override
            public void visitChildren(AstVisitor astVisitor) {
                testExpression.accept(astVisitor);
                thenExpression.accept(astVisitor);
                elseExpression.accept(astVisitor);
            }
        });
    }

    @Override
    public void doFormat(int depth, Hints formatOption, FormatWriter writer) throws IOException {
        this.testExpression.doFormat(depth, formatOption, writer);
        writer.write(" ? ");
        this.thenExpression.doFormat(depth, formatOption, writer);
        writer.write(" : ");
        this.elseExpression.doFormat(depth, formatOption, writer);
    }
}
