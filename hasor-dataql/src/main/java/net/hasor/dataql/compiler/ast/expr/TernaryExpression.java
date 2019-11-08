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
import net.hasor.dataql.compiler.FormatWriter;
import net.hasor.dataql.compiler.ast.Expression;
import net.hasor.dataql.compiler.qil.CompilerStack;
import net.hasor.dataql.compiler.qil.InstQueue;
import net.hasor.dataql.compiler.qil.Label;

import java.io.IOException;

/**
 * 三元运算表达式
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class TernaryExpression extends Expression {
    private Expression testExpression;  //三元运算符，条件表达式
    private Expression thenExpression;  //第一个表达式
    private Expression elseExpression;  //第二个表达式

    public TernaryExpression(Expression testExp, Expression thenExp, Expression elseExp) {
        this.testExpression = testExp;
        this.thenExpression = thenExp;
        this.elseExpression = elseExp;
    }

    @Override
    public void doFormat(int depth, Option formatOption, FormatWriter writer) throws IOException {
        this.testExpression.doFormat(depth, formatOption, writer);
        writer.write(" ? ");
        this.thenExpression.doFormat(depth, formatOption, writer);
        writer.write(" : ");
        this.elseExpression.doFormat(depth, formatOption, writer);
    }

    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        Label elseEnterIn = queue.labelDef(); //
        //
        // .测试表达式
        this.testExpression.doCompiler(queue, stackTree);
        queue.inst(IF, elseEnterIn);//如果判断失败，跳转到下一个Label
        //
        // .第一个表达式
        this.thenExpression.doCompiler(queue, stackTree);
        //
        // .第二个表达式
        queue.inst(LABEL, elseEnterIn);
        this.elseExpression.doCompiler(queue, stackTree);
    }
}