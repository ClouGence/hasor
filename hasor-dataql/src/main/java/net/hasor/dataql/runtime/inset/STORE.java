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
import net.hasor.dataql.runtime.mem.DataHeap;
import net.hasor.dataql.runtime.mem.DataStack;
import net.hasor.dataql.runtime.mem.EnvStack;

/**
 * STORE   // 栈顶数据存储到堆（例：STORE，2）
 *         - 参数说明：共1参数；参数1：存入堆的位置；
 *         - 栈行为：消费1，产出0
 *         - 堆行为：存入数据
 *
 * @see net.hasor.dataql.runtime.inset.LOAD
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class STORE implements InsetProcess {
    @Override
    public int getOpcode() {
        return STORE;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, ProcessContet context) throws ProcessException {
        int index = sequence.currentInst().getInt(0);
        Object data = dataStack.pop();
        dataHeap.saveData(index, data);
    }
}