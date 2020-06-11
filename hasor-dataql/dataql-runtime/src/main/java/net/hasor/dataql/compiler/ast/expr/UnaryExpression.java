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
import net.hasor.dataql.compiler.ast.*;

import java.io.IOException;

/**
 * 一元运算表达式
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class UnaryExpression extends AstBasic implements Expression {
    private final Expression target;      //表达式
    private final String     dyadicSymbol;//操作符

    public UnaryExpression(Expression target, String dyadicSymbol) {
        this.target = target;
        this.dyadicSymbol = dyadicSymbol;
    }

    public Expression getTarget() {
        return target;
    }

    public String getDyadicSymbol() {
        return dyadicSymbol;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visitInst(new InstVisitorContext(this) {
            @Override
            public void visitChildren(AstVisitor astVisitor) {
                target.accept(astVisitor);
            }
        });
    }

    @Override
    public void doFormat(int depth, Hints formatOption, FormatWriter writer) throws IOException {
        writer.write(this.dyadicSymbol);
        this.target.doFormat(depth, formatOption, writer);
    }
}