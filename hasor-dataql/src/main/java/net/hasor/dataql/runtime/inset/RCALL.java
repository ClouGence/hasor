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
import net.hasor.dataql.runtime.LambdaCallProxy;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.mem.MemStack;
import net.hasor.dataql.runtime.mem.StackStruts;

/**
 * RCALL，发起一个 lambda 的调用，调用会在一个全新的堆栈上运行。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class RCALL implements InsetProcess {
    @Override
    public int getOpcode() {
        return RCALL;
    }

    @Override
    public void doWork(InstSequence sequence, MemStack memStack, StackStruts local, ProcessContet context) throws ProcessException {
        //
        LambdaCallProxy callStruts = (LambdaCallProxy) memStack.pop();
        int paramCount = sequence.currentInst().getInt(0);
        //
        // .参数准备
        Object[] paramArrays = new Object[paramCount];
        for (int i = 0; i < paramCount; i++) {
            paramArrays[paramCount - 1 - i] = memStack.pop();
        }
        // .返回值处理
        try {
            Object result = callStruts.call(paramArrays, context);
            memStack.push(result);
        } catch (ProcessException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvokerProcessException(getOpcode(), e.getMessage(), e);
        }
    }
}