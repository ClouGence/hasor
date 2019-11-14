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
import net.hasor.dataql.compiler.ast.inst.InstSet;
import net.hasor.dataql.compiler.ast.inst.SwitchInst;
import net.hasor.dataql.compiler.ast.inst.SwitchInst.SwitchExpression;
import net.hasor.dataql.compiler.qil.CompilerContext;
import net.hasor.dataql.compiler.qil.InstCompiler;
import net.hasor.dataql.compiler.qil.InstQueue;
import net.hasor.dataql.compiler.qil.Label;

import java.util.List;

/**
 * if指令
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class SwitchInstCompiler implements InstCompiler<SwitchInst> {
    @Override
    public void doCompiler(SwitchInst astInst, InstQueue queue, CompilerContext compilerContext) {
        List<SwitchExpression> testBlockSet = astInst.getTestBlockSet();
        InstSet elseBlockSet = astInst.getElseBlockSet();
        //
        if (testBlockSet.isEmpty()) {
            throw new RuntimeException("if testBlockSet is empty.");
        }
        /*
            if (a == b)
                var a = b
            elseif (a == null)
                var b = a
            else
                var c = a
            end

            -----               // if (a == b)
            LABEL   "label_0"
            LOAD    1
            LOAD    2
            DO      "eq"
            IF      "label_1"
            ...
            GOTO    "label_4"
            -----               // elseif (a == null)
            LABEL   "label_1"
            LOAD    1
            LDC_N
            DO      "eq"
            IF      "label_2"
            ...
            GOTO    "label_4"
            -----               // else
            GOTO    "label_2"
            ...
            -----
            GOTO    "label_4"
        */
        //
        // .if 和 elseif
        //
        Label finalLabel = queue.labelDef();    // if 的总出口 Label
        for (SwitchExpression switchExp : testBlockSet) {
            //
            // .产生新的入口，流给下一个if分支使用
            Label lastEnterIn = queue.labelDef();
            //
            // .条件判断
            Expression testExpression = switchExp.testExpression;
            compilerContext.findInstCompilerByInst(testExpression).doCompiler(queue);
            queue.inst(IF, lastEnterIn);//如果判断失败，跳转到下一个Label
            //
            // .if的body
            InstSet instBlockSet = switchExp.instBlockSet;
            compilerContext.newFrame();
            compilerContext.findInstCompilerByInst(instBlockSet).doCompiler(queue);
            compilerContext.dropFrame();
            queue.inst(GOTO, finalLabel);//执行完毕，跳转到总出口
            queue.inst(LABEL, lastEnterIn);
        }
        // .else
        if (elseBlockSet != null) {
            compilerContext.newFrame();
            compilerContext.findInstCompilerByInst(elseBlockSet).doCompiler(queue);
            compilerContext.dropFrame();
            queue.inst(GOTO, finalLabel);
        }
        // .if 的结束点
        queue.inst(LABEL, finalLabel);
    }
}