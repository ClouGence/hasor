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
import net.hasor.dataql.compiler.qil.Instruction;
import net.hasor.dataql.runtime.*;
import net.hasor.dataql.runtime.mem.DataHeap;
import net.hasor.dataql.runtime.mem.DataStack;
import net.hasor.dataql.runtime.mem.EnvStack;
import net.hasor.dataql.runtime.mem.RefCall;

/**
 * CALL    // 发起服务调用（例：CALL,2）
 *         - 参数说明：共1参数；参数1：发起调用时需要用到的调用参数个数 n
 *         - 栈行为：消费：n + 1（n是参数，1是函数入口），产出1
 *         - 堆行为：无
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class CALL implements InsetProcess {
    @Override
    public int getOpcode() {
        return CALL;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) throws InstructRuntimeException {
        Instruction instruction = sequence.currentInst();
        int paramCount = instruction.getInt(0);
        //
        Object[] paramArrays = new Object[paramCount];
        for (int i = 0; i < paramCount; i++) {
            int paramIndex = paramCount - 1 - i;
            Object paramObj = dataStack.pop();
            paramArrays[paramIndex] = paramObj;
        }
        //
        Object refCallObj = dataStack.pop();
        if (!(refCallObj instanceof RefCall)) {
            throw new InstructRuntimeException("target is not RefCall.");
        }
        //
        RefCall refCall = (RefCall) refCallObj;
        Object result = refCall.invokeMethod(paramArrays, new OptionReadOnly(context));
        dataStack.push(result);
    }
}