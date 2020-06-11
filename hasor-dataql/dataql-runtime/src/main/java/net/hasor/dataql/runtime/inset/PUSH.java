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
import net.hasor.dataql.domain.ListModel;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InsetProcessContext;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.InstructRuntimeException;
import net.hasor.dataql.runtime.mem.DataHeap;
import net.hasor.dataql.runtime.mem.DataStack;
import net.hasor.dataql.runtime.mem.EnvStack;

/**
 * PUSH    // 将栈顶元素压入集合（例：PUSH）
 *         - 参数说明：共0参数；
 *         - 栈行为：消费1，产出0
 *         - 堆行为：无
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class PUSH implements InsetProcess {
    @Override
    public int getOpcode() {
        return PUSH;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) throws InstructRuntimeException {
        Object data = dataStack.pop();
        Object ors = dataStack.peek();
        //
        if (ors instanceof ListModel) {
            ((ListModel) ors).add(data);
            return;
        }
        throw new InstructRuntimeException(sequence.programLocation(), "output data error, target type must be ListModel.");
    }
}