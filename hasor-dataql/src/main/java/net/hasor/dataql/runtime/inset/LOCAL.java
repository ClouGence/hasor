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
import net.hasor.dataql.ProcessException;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.mem.MemStack;
import net.hasor.dataql.runtime.mem.StackStruts;
import net.hasor.dataql.runtime.struts.LambdaCall;
/**
 * LOCAL，紧跟在 METHOD 指令后面可以有多个组成。
 * 作用是 将 METHOD 指令中纠正的参数数组存储到堆内存中。它的工作性质有点类似 STORE。
 * @see net.hasor.dataql.runtime.inset.METHOD
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
class LOCAL implements InsetProcess {
    @Override
    public int getOpcode() {
        return LOCAL;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, StackStruts local, ProcessContet context) throws ProcessException {
        LambdaCall callInfo = (LambdaCall) memStack.peek();
        int storeIndex = sequence.currentInst().getInt(0);
        //
        Object data = callInfo.getArrays()[storeIndex];
        memStack.storeData(storeIndex, data);
    }
}