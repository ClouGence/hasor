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
import net.hasor.dataql.runtime.mem.ExitType;

/**
 * RETURN  // 结束当前指令序列的执行，并返回数据和状态给上一个指令序列。如果没有上一个指令序列那么结束整个查询
 *         - 参数说明：共1参数；参数1：返回码
 *         - 栈行为：消费1，产出0
 *         - 堆行为：无
 *
 * @see net.hasor.dataql.runtime.inset.THROW
 * @see net.hasor.dataql.runtime.inset.EXIT
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class RETURN implements InsetProcess {
    @Override
    public int getOpcode() {
        return RETURN;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) throws ProcessException {
        int resultCode = sequence.currentInst().getInt(0);
        Object result = dataStack.pop();
        dataStack.setResultCode(resultCode);
        dataStack.setResult(result);
        dataStack.setExitType(ExitType.Return);
        sequence.jumpTo(sequence.exitPosition());
    }
}