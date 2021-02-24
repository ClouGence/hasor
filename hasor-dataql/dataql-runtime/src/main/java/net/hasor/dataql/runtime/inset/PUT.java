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
import net.hasor.dataql.domain.ObjectModel;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InsetProcessContext;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.QueryRuntimeException;
import net.hasor.dataql.runtime.mem.DataHeap;
import net.hasor.dataql.runtime.mem.DataStack;
import net.hasor.dataql.runtime.mem.EnvStack;

/**
 * PUT     // 将栈顶对象元素放入对象元素中（例：PUT,"xxxx"）
 *         - 参数说明：共1参数；参数1：属性名称（Map的Key 或 对象的属性名）
 *         - 栈行为：消费1，产出0
 *         - 堆行为：无
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class PUT implements InsetProcess {
    @Override
    public int getOpcode() {
        return PUT;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) throws QueryRuntimeException {
        String fieldName = sequence.currentInst().getString(0);
        Object useData = dataStack.pop();
        Object containerData = dataStack.peek();
        //
        if (containerData instanceof ObjectModel) {
            ((ObjectModel) containerData).put(fieldName, useData);
            return;
        }
        throw new QueryRuntimeException(sequence.programLocation(), "output data error, target type must be ObjectModel.");
    }
}
