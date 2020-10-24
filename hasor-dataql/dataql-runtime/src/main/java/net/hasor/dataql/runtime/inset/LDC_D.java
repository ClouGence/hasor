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
import net.hasor.dataql.runtime.operator.OperatorUtils;

import static net.hasor.dataql.HintValue.MIN_DECIMAL_WIDTH;
import static net.hasor.dataql.HintValue.MIN_INTEGER_WIDTH;

/**
 * LDC_D   // 将数字压入栈（例：LDC_D 12345）
 *         - 参数说明：共1参数；参数1：数据；
 *         - 栈行为：消费0，产出1
 *         - 堆行为：无
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class LDC_D implements InsetProcess {
    @Override
    public int getOpcode() {
        return LDC_D;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) {
        Number number = sequence.currentInst().getNumber(0);
        String decimalWidth = (String) context.currentHints().getHint(MIN_DECIMAL_WIDTH);
        String integerWidth = (String) context.currentHints().getHint(MIN_INTEGER_WIDTH);
        dataStack.push(OperatorUtils.fixNumberWidth(number, decimalWidth, integerWidth));
    }
}