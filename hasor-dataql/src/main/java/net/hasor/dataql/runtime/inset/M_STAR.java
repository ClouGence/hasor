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
import net.hasor.dataql.runtime.InsetProcessContext;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.mem.DataHeap;
import net.hasor.dataql.runtime.mem.DataStack;
import net.hasor.dataql.runtime.mem.EnvStack;
import net.hasor.dataql.runtime.mem.RefLambdaCallStruts;

/**
 * METHOD，位于定义 lambda 函数的执行序列头部。该指令的目的是对发起调用的入参参数个数进行规整化。
 * 例：<pre>
 *  var foo = lambda : (a,b,c) -> {
 *       return a+b+c;
 *   }
 *   return foo(1,2,3,4,5)~;
 * <pre/>
 * 例子中，foo 方法定义了 3个参数，但是当发起调用 foo 时实际传入了 5 个参数。METHOD，指令的目的就是为了纠正入参个数的不一致。
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class M_STAR implements InsetProcess {
    @Override
    public int getOpcode() {
        return M_STAR;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) throws ProcessException {
        RefLambdaCallStruts lambdaCall = (RefLambdaCallStruts) dataStack.peek();
        int paramCount = sequence.currentInst().getInt(0);
        Object[] callParams = lambdaCall.getParams();
        for (int i = 0; i < paramCount; i++) {
            if (i > callParams.length) {
                break;
            }
            dataHeap.saveData(i, callParams[i]);
        }
    }
}