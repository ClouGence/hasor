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
import net.hasor.dataql.runtime.InstructRuntimeException;
import net.hasor.dataql.runtime.mem.DataHeap;
import net.hasor.dataql.runtime.mem.DataIterator;
import net.hasor.dataql.runtime.mem.DataStack;
import net.hasor.dataql.runtime.mem.EnvStack;

/**
 * E_LOAD  // 加载环境栈顶的数据到数据栈
 *         - 参数说明：共1参数；参数1：操作符号@#$
 *         - 栈行为：消费0，产出1
 *         - 环境栈行为：消费0，产出0
 *         - 堆行为：无
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class E_LOAD implements InsetProcess {
    @Override
    public int getOpcode() {
        return E_LOAD;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) throws InstructRuntimeException {
        String symbol = sequence.currentInst().getString(0);
        if (envStack.isEmpty()) {
            dataStack.push(null);
            return;
        }
        //
        if ("#".equalsIgnoreCase(symbol)) {
            // # 表示环境栈顶
            dataStack.push(envStack.peek());
        } else if ("$".equalsIgnoreCase(symbol)) {
            // $ 表示环境栈根
            Object first = envStack.firstElement();
            if (first instanceof DataIterator) {
                first = ((DataIterator) first).getOriData();
            }
            dataStack.push(first);
        } else if ("@".equalsIgnoreCase(symbol)) {
            // @ 表示整个环境栈(数组形态)
            Object[] objects = envStack.toArray();
            for (int i = 0; i < objects.length; i++) {
                if (objects[i] instanceof DataIterator) {
                    objects[i] = ((DataIterator) objects[i]).getOriData();
                }
            }
            dataStack.push(objects);
        } else {
            throw new InstructRuntimeException(sequence.programLocation(), "symbol '" + symbol + "' is not define.");
        }
    }
}