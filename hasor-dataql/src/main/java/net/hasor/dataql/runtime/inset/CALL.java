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
import net.hasor.dataql.UDF;
import net.hasor.dataql.domain.compiler.Instruction;
import net.hasor.dataql.runtime.*;
import net.hasor.dataql.runtime.mem.MemStack;
import net.hasor.dataql.runtime.mem.StackStruts;
import net.hasor.dataql.runtime.struts.LambdaCallStruts;
/**
 * CALL，指令是用于发起对 UDF 的调用。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
class CALL implements InsetProcess {
    @Override
    public int getOpcode() {
        return CALL;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, StackStruts local, ProcessContet context) throws ProcessException {
        Instruction instruction = sequence.currentInst();
        String udfName = instruction.getString(0);
        int paramCount = instruction.getInt(1);
        //
        Object[] paramArrays = new Object[paramCount];
        for (int i = 0; i < paramCount; i++) {
            int paramIndex = paramCount - 1 - i;
            Object paramObj = memStack.pop();
            //
            if (paramObj instanceof LambdaCallStruts) {
                int callAddress = ((LambdaCallStruts) paramObj).getMethod();
                InstSequence methodSeq = sequence.methodSet(callAddress);
                paramArrays[paramIndex] = new LambdaCallProxy(methodSeq, memStack, local, context);
                continue;
            }
            //
            paramArrays[paramIndex] = paramObj;
        }
        //
        UDF udf = context.findUDF(udfName);
        if (udf == null) {
            throw new InvokerProcessException(getOpcode(), "CALL -> udf '" + udfName + "' is not found");
        }
        //
        try {
            Object result = udf.call(paramArrays, new OptionReadOnly(context));
            memStack.push(result);
        } catch (Throwable e) {
            throw new InvokerProcessException(getOpcode(), "call '" + udfName + "' error.", e);
        }
    }
}