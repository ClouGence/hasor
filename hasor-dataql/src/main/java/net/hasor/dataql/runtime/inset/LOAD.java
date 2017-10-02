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
/**
 * LOAD，从堆中装载一个数据到栈。与其对应的指令为 STORE
 * @see net.hasor.dataql.runtime.inset.STORE
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
class LOAD implements InsetProcess {
    @Override
    public int getOpcode() {
        return LOAD;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, StackStruts local, ProcessContet context) throws ProcessException {
        // depth 小于 0 表示的是当前堆栈，常规编译模式下处理 lambda_25 用例时发现，在加载入参参数时。要加载的 depth 位置和实际堆栈位置有偏差。
        //      因此通过 -1 形式来表示这个参数的 load 是方法变量表中的参数。同时作为特殊处理， -1 只加载栈顶以修复偏差。
        int depth = sequence.currentInst().getInt(0);
        int index = sequence.currentInst().getInt(1);
        Object data = null;
        if (depth < 0) {
            data = memStack.loadData(memStack.getDepth(), index);
        } else {
            data = memStack.loadData(depth, index);
        }
        memStack.push(data);
    }
}