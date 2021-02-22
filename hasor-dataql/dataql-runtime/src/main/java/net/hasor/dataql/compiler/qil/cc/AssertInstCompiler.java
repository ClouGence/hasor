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
import net.hasor.dataql.compiler.ast.Variable;
import net.hasor.dataql.compiler.ast.inst.AssertInst;
import net.hasor.dataql.compiler.qil.CompilerContext;
import net.hasor.dataql.compiler.qil.InstCompiler;
import net.hasor.dataql.compiler.qil.InstQueue;
import net.hasor.dataql.compiler.qil.Label;
import net.hasor.dataql.domain.TypeOfEnum;

/**
 * Assert
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class AssertInstCompiler implements InstCompiler<AssertInst> {
    @Override
    public void doCompiler(AssertInst astInst, InstQueue queue, CompilerContext compilerContext) {
        Variable varValue = astInst.getValue();
        compilerContext.findInstCompilerByInst(varValue).doCompiler(queue);
        instLocation(queue, astInst);
        Label finalLabel = queue.labelDef();// 总出口 Label
        //
        // .数据类型判断
        Label nextLabel = queue.labelDef();
        queue.inst(COPY);   // 表达式值Copy 一份用来计算 typeof
        queue.inst(TYPEOF);
        queue.inst(LDC_S, TypeOfEnum.Boolean.typeCode());
        queue.inst(DO, "!=");
        queue.inst(IF, nextLabel);
        queue.inst(POP);
        queue.inst(LDC_S, "assert expression value is not 'boolean' type.");
        queue.inst(THROW, 500);
        queue.inst(GOTO, finalLabel);
        queue.inst(LABEL, nextLabel);
        //
        // .判断断言
        nextLabel = queue.labelDef();
        queue.inst(IF, nextLabel);
        queue.inst(GOTO, finalLabel);
        queue.inst(LABEL, nextLabel);
        queue.inst(LDC_S, "assert test failed.");
        queue.inst(THROW, 500);
        //
        // .if 的结束点
        queue.inst(LABEL, finalLabel);
    }
}
