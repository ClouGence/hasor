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
import net.hasor.dataql.domain.ValueModel;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InsetProcessContext;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.mem.DataHeap;
import net.hasor.dataql.runtime.mem.DataStack;
import net.hasor.dataql.runtime.mem.EnvStack;

/**
 * IF      // if 条件判断，如果条件判断失败那么 GOTO 到指定位置，否则继续往下执行
 *         - 参数说明：共1参数；参数1：GOTO 的位置
 *         - 栈行为：消费1，产出0
 *         - 堆行为：无
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class IF implements InsetProcess {
    @Override
    public int getOpcode() {
        return IF;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) {
        Object test = dataStack.pop();
        if (test instanceof ValueModel) {
            test = ((ValueModel) test).asOri();
        }
        //
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