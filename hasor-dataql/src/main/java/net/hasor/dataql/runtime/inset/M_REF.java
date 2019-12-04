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
package net.hasor.dataql.runtime.inset;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InsetProcessContext;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.mem.DataHeap;
import net.hasor.dataql.runtime.mem.DataStack;
import net.hasor.dataql.runtime.mem.EnvStack;
import net.hasor.dataql.runtime.mem.RefLambdaCall;

/**
 * M_REF   // 引用另一处的指令序列地址，并将其作为 UDF 形态存放到栈顶
 *         - 参数说明：共1参数；参数1：内置lambda函数的入口地址
 *         - 栈行为：消费0，产出1
 *         - 堆行为：无
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class M_REF implements InsetProcess {
    @Override
    public int getOpcode() {
        return M_REF;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) {
        int callAddress = sequence.currentInst().getInt(0);
        InstSequence methodSeq = sequence.methodSet(callAddress);
        RefLambdaCall refLambdaCall = new RefLambdaCall(//
                methodSeq,  //
                dataHeap,   //
                dataStack,  //
                envStack,   //
                context     //
        );
        dataStack.push(refLambdaCall);
    }
}