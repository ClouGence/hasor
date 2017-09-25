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
import net.hasor.dataql.result.LambdaModel;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.LambdaCallProxy;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.mem.MemStack;
import net.hasor.dataql.runtime.mem.StackStruts;
import net.hasor.dataql.runtime.struts.LambdaCallStruts;
/**
 * 特殊处理结果，兼容 return 、throw、exit 时返回一个 lambda 的情况。
 * @see net.hasor.dataql.runtime.inset.ERR
 * @see net.hasor.dataql.runtime.inset.EXIT
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
abstract class AbstractReturn implements InsetProcess {
    protected Object specialProcess(InstSequence sequence, MemStack memStack, StackStruts local, ProcessContet context, Object result) {
        if (result instanceof LambdaCallStruts) {
            int callAddress = ((LambdaCallStruts) result).getMethod();
            InstSequence methodSeq = sequence.methodSet(callAddress);
            result = new LambdaModel(new LambdaCallProxy(methodSeq, memStack, local, context));
        }
        return result;
    }
}