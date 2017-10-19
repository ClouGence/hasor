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
import net.hasor.dataql.domain.compiler.Instruction;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.LambdaCallProxy;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.mem.MemStack;
import net.hasor.dataql.runtime.mem.StackStruts;
/**
 * M_REF，定义一个 lambda 函数指针。（产生一个LambdaCallStruts）
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
class M_REF implements InsetProcess {
    @Override
    public int getOpcode() {
        return M_REF;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, StackStruts local, ProcessContet context) throws ProcessException {
        //
        Instruction inst = sequence.currentInst();
        int callAddress = inst.getInt(0);
        //
        // .前把函数入口定义，打包成一个 LambdaCallStruts
        try {
            InstSequence methodSeq = sequence.methodSet(callAddress);
            LambdaCallProxy callProxy = new LambdaCallProxy(callAddress, methodSeq, memStack, local, context);
            memStack.push(callProxy);
        } catch (ProcessException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvokerProcessException(getOpcode(), e.getMessage(), e);
        }
    }
}