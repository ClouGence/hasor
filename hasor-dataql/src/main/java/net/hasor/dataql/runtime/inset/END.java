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
 * END，正常结束指令，当执行该指令时，会将栈顶的元素作为 result。
 * 并且将执行指针设置到执行序列的末尾。
 * @see net.hasor.dataql.runtime.inset.ERR
 * @see net.hasor.dataql.runtime.inset.EXIT
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
class END extends AbstractReturn implements InsetProcess {
    @Override
    public int getOpcode() {
        return END;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, StackStruts local, ProcessContet context) throws ProcessException {
        Object result = memStack.pop();
        //
        result = specialProcess(sequence, memStack, local, context, result);
        memStack.setResult(result);
        sequence.jumpTo(sequence.exitPosition());
    }
}