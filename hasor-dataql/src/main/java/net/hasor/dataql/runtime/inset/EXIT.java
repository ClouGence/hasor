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
import net.hasor.dataql.BreakProcessException;
import net.hasor.dataql.ProcessException;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.mem.MemStack;
import net.hasor.dataql.runtime.mem.StackStruts;
/**
 * EXIT，退出指令，当执行该指令时，会将栈顶的两个元素作为整个查询退出的返回值。
 * 退出的实现机制和 ERR 指令相似，不同的是开发者不会得到异常抛出。
 * 提示：
 * 区别于 END 指令的是，EXIT 指令将会终结整个查询的执行。而 END 指令只会终止当前指令序列的执行。同时有别于 ERR 指令的是，开发者不会得到异常抛出。
 * @see net.hasor.dataql.runtime.inset.ERR
 * @see net.hasor.dataql.runtime.inset.EXIT
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
class EXIT extends AbstractReturn implements InsetProcess {
    @Override
    public int getOpcode() {
        return EXIT;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, StackStruts local, ProcessContet context) throws ProcessException {
        Object exitData = memStack.pop();
        int exitCode = (Integer) memStack.pop();
        //
        exitData = specialProcess(sequence, memStack, local, context, exitData);
        throw new BreakProcessException(this.getOpcode(), exitCode, exitData);
    }
}