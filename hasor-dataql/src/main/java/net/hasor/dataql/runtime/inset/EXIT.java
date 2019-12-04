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
import net.hasor.dataql.domain.DomainHelper;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InsetProcessContext;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.mem.DataHeap;
import net.hasor.dataql.runtime.mem.DataStack;
import net.hasor.dataql.runtime.mem.EnvStack;
import net.hasor.dataql.runtime.mem.ExitType;

/**
 * EXIT    // 结束所有指令序列的执行并返回数据和状态
 *         - 参数说明：共1参数；参数1：退出码
 *         - 栈行为：消费1，产出0
 *         - 堆行为：无
 * 提示：区别于 RETURN 指令的是，EXIT 指令将会终结整个查询的执行。
 * 而 RETURN 指令只会终止当前指令序列的执行。同时有别于 THROW 指令的是，EXIT 执行不会得到异常抛出。
 * @see net.hasor.dataql.runtime.inset.THROW
 * @see net.hasor.dataql.runtime.inset.EXIT
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class EXIT implements InsetProcess {
    @Override
    public int getOpcode() {
        return EXIT;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) {
        int resultCode = sequence.currentInst().getInt(0);
        Object result = dataStack.pop();
        dataStack.setResultCode(resultCode);
        dataStack.setResult(DomainHelper.convertTo(result));
        dataStack.setExitType(ExitType.Exit);
        sequence.jumpTo(sequence.exitPosition());
    }
}