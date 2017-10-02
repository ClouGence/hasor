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
/**
 * var指令
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class VariableInst extends Inst {
    private String   varName; //变量名
    private Variable value;   //变量表达式
    public VariableInst(String varName, Variable value) {
        super();
        this.varName = varName;
        this.value = value;
    }
    //
    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        // .表达式指令
        this.value.doCompiler(queue, stackTree);
        //
        // .如果当前堆栈中存在该变量的定义，那么直接覆盖
        int index = stackTree.containsWithCurrent(this.varName);
        if (index >= 0) {
            queue.inst(STORE, index);
        } else {
            int storeIndex = stackTree.push(this.varName);
            queue.inst(STORE, storeIndex);
        }
    }
}