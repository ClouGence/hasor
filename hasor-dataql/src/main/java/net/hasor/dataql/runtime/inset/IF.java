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
 * IF，当前栈顶的表达式如果为 false，则跳转到 IF 指令上指定的位置上去。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
class IF implements InsetProcess {
    @Override
    public int getOpcode() {
        return IF;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, StackStruts local, ProcessContet context) throws ProcessException {
        Object test = memStack.pop();
        int jumpLabel = sequence.currentInst().getInt(0);
        //
        boolean testFailed = (test == null || Boolean.FALSE.equals(test));
        if (!testFailed) {
            String testStr = test.toString();
            testFailed = ("false".equalsIgnoreCase(testStr) || "off".equalsIgnoreCase(testStr) || "0".equalsIgnoreCase(testStr));
        }
        //
        if (testFailed) {
            sequence.jumpTo(jumpLabel);
        }
    }
}