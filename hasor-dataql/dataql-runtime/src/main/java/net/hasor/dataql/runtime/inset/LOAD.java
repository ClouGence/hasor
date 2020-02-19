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
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InsetProcessContext;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.mem.DataHeap;
import net.hasor.dataql.runtime.mem.DataStack;
import net.hasor.dataql.runtime.mem.EnvStack;

import java.util.function.Supplier;

/**
 * LOAD    // 从指定深度的堆中加载n号元素到栈（例：LOAD 1 ,1 ）
 *         - 参数说明：共2参数；参数1：堆深度；参数2：元素序号；
 *         - 栈行为：消费0，产出1
 *         - 堆行为：取出数据（不删除）
 *
 * @see net.hasor.dataql.runtime.inset.STORE
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class LOAD implements InsetProcess {
    @Override
    public int getOpcode() {
        return LOAD;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) {
        int depth = sequence.currentInst().getInt(0);
        int index = sequence.currentInst().getInt(1);
        Object data = dataHeap.loadData(depth, index);
        if (data instanceof Supplier) {
            data = ((Supplier) data).get();
        }
        dataStack.push(data);
    }
}