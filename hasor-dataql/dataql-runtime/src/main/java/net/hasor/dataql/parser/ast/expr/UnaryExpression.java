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
import net.hasor.dataql.parser.ast.token.SymbolToken;
import net.hasor.dataql.parser.location.BlockLocation;

import java.io.IOException;

/**
 * 一元运算表达式
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class UnaryExpression extends BlockLocation implements Expression {
    private final Expression  target;      //表达式
    private final SymbolToken symbolToken;//操作符

    public UnaryExpression(Expression target, SymbolToken symbolToken) {
        this.target = target;
        this.symbolToken = symbolToken;
    }

    public Expression getTarget() {
        return target;
    }

    public SymbolToken getDyadicSymbol() {
        return symbolToken;
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
        writer.write(this.symbolToken.getSymbol());
        this.target.doFormat(depth, formatOption, writer);
    }
}
