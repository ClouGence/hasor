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
import net.hasor.dataql.Udf;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InsetProcessContext;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.InstructRuntimeException;
import net.hasor.dataql.runtime.Location.RuntimeLocation;
import net.hasor.dataql.runtime.mem.*;

/**
 * M_DEF   // 函数定义，将栈顶元素转换为 UDF
 *         - 参数说明：共0参数；
 *         - 栈行为：消费1，产出1
 *         - 堆行为：无
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class M_DEF implements InsetProcess {
    @Override
    public int getOpcode() {
        return M_DEF;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) throws InstructRuntimeException {
        RuntimeLocation location = sequence.programLocation();
        Object refCall = dataStack.pop();
        if (refCall == null) {
            throw new InstructRuntimeException(location, "target is null.");
        }
        if (!(refCall instanceof Udf)) {
            throw new InstructRuntimeException(location, "target or Property is not UDF.");
        }
        boolean innerUDF = refCall instanceof RefFragmentCall || refCall instanceof RefLambdaCall;
        refCall = new RefCall(location, !innerUDF, (Udf) refCall);
        dataStack.push(refCall);
    }
}