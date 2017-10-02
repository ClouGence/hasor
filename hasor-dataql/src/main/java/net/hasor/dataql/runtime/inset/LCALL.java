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
import net.hasor.dataql.InvokerProcessException;
import net.hasor.dataql.ProcessException;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.mem.MemStack;
import net.hasor.dataql.runtime.mem.StackStruts;
import net.hasor.dataql.runtime.struts.LambdaCall;
import net.hasor.dataql.runtime.struts.LambdaCallStruts;
/**
 * LCALL，发起一个 lambda 的调用，调用会在一个全新的堆栈上运行。
 * 当执行该指令时，栈顶必须是一个 LambdaCallStruts ，而 LambdaCallStruts 是通过 M_REF 定义的。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
class LCALL implements InsetProcess {
    @Override
    public int getOpcode() {
        return LCALL;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, StackStruts local, ProcessContet context) throws ProcessException {
        //
        LambdaCallStruts callStruts = (LambdaCallStruts) memStack.pop();
        int address = callStruts.getMethod();
        int paramCount = sequence.currentInst().getInt(0);
        //
        // .参数准备
        Object[] paramArrays = new Object[paramCount];
        for (int i = 0; i < paramCount; i++) {
            paramArrays[paramCount - 1 - i] = memStack.pop();
        }
        LambdaCall callInfo = new LambdaCall(address, paramArrays);
        //
        // .查找方法指令序列
        InstSequence methodSeq = sequence.methodSet(address);
        if (methodSeq == null) {
            throw new InvokerProcessException(getOpcode(), "LCALL -> InstSequence '" + address + "' is not found.");
        }
        // .执行调用，调用前把所有入参打包成一个 Array，交给 METHOD 指令去处理。
        {
            MemStack sub = memStack.create(address);
            sub.push(callInfo);
            context.processInset(methodSeq, sub, local);
            callInfo.setResult(sub.getResult());
        }
        // .返回值处理
        Object result = callInfo.getResult();
        memStack.push(result);
    }
}