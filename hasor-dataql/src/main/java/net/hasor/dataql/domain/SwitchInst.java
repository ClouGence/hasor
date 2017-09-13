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
package net.hasor.dataql.domain;
import net.hasor.dataql.domain.compiler.CompilerStack;
import net.hasor.dataql.domain.compiler.InstQueue;
import net.hasor.dataql.domain.compiler.Label;

import java.util.ArrayList;
import java.util.List;
/**
 * if指令
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class SwitchInst extends Inst {
    public static class SwitchExpression {
        private Expression testExpression;
        private BlockSet   instBlockSet;
    }
    private List<SwitchExpression> testBlockSet;
    private BlockSet               elseBlockSet;
    public SwitchInst() {
        this.testBlockSet = new ArrayList<SwitchExpression>();
    }
    //
    /** 添加条件分支 */
    public void addBlockSet(Expression testExp, BlockSet instBlockSet) {
        SwitchExpression se = new SwitchExpression();
        se.testExpression = testExp;
        se.instBlockSet = instBlockSet;
        this.testBlockSet.add(se);
    }
    /** 设置默认条件分支 */
    public void setElseBlockSet(BlockSet instBlockSet) {
        this.elseBlockSet = instBlockSet;
    }
    //
    //
    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        if (this.testBlockSet.isEmpty()) {
            queue.inst(LDC_N, 0);
            queue.inst(LDC_S, "inst if -> error.");
            queue.inst(ERR);
            return;
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
        Label finalLabel = queue.labelDef();  // else出口 Label
        Label lastEnterIn = queue.labelDef(); // 最后一个入口 Label（随着if分支发生变化）
        //
        for (SwitchExpression switchExp : this.testBlockSet) {
            //
            // .标记if分支入口
            queue.inst(LABEL, lastEnterIn);
            //
            // .产生新的入口，流给下一个if分支使用
            lastEnterIn = queue.labelDef();
            //
            // .条件判断
            Expression testExpression = switchExp.testExpression;
            testExpression.doCompiler(queue, stackTree);
            queue.inst(IF, lastEnterIn);//如果判断失败，跳转到下一个Label
            //
            // .if的body
            BlockSet instBlockSet = switchExp.instBlockSet;
            instBlockSet.doCompiler(queue, stackTree);
            queue.inst(GOTO, finalLabel);//执行完毕，跳转到总出口
            //
        }
        // .else
        queue.inst(LABEL, lastEnterIn);
        if (this.elseBlockSet != null) {
            this.elseBlockSet.doCompiler(queue, stackTree);
            queue.inst(GOTO, finalLabel);
        }
        // .if 的结束点
        queue.inst(LABEL, finalLabel);
    }
}