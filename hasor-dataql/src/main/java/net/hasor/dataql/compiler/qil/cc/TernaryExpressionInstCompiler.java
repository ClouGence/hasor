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
package net.hasor.dataql.compiler.qil.cc;
import net.hasor.dataql.compiler.ast.Expression;
import net.hasor.dataql.compiler.ast.expr.TernaryExpression;
import net.hasor.dataql.compiler.qil.CompilerStack;
import net.hasor.dataql.compiler.qil.InstCompiler;
import net.hasor.dataql.compiler.qil.InstQueue;
import net.hasor.dataql.compiler.qil.Label;

/**
 * 三元运算表达式
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class TernaryExpressionInstCompiler extends InstCompiler<TernaryExpression> {
    @Override
    public void doCompiler(TernaryExpression inst, InstQueue queue, CompilerStack stackTree) {
        Expression testExpr = inst.getTestExpression();
        Expression thenExpr = inst.getThenExpression();
        Expression elseExpr = inst.getElseExpression();
        Label elseLabel = queue.labelDef();
        //
        // .测试表达式
        findInstCompilerByInst(testExpr).doCompiler(testExpr, queue, stackTree);
        queue.inst(IF, elseLabel);//如果判断失败，跳转到下一个Label
        //
        // .第一个表达式
        findInstCompilerByInst(thenExpr).doCompiler(thenExpr, queue, stackTree);
        //
        // .第二个表达式
        queue.inst(LABEL, elseLabel);
        findInstCompilerByInst(elseExpr).doCompiler(elseExpr, queue, stackTree);
    }
}