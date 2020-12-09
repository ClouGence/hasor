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
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InsetProcessContext;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.InstructRuntimeException;
import net.hasor.dataql.runtime.mem.DataHeap;
import net.hasor.dataql.runtime.mem.DataStack;
import net.hasor.dataql.runtime.mem.EnvStack;
import net.hasor.dataql.runtime.operator.OperatorProcess;

/**
 * UO      // 一元运算
 *         - 参数说明：共1参数；参数1：一元操作符
 *         - 栈行为：消费1，产出1
 *         - 堆行为：无
 *
 * 开发者可以通过实现 OperatorProcess 接口，覆盖某个运算符实现 运算符重载功能。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class UO implements InsetProcess {
    @Override
    public int getOpcode() {
        return UO;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) throws InstructRuntimeException {
        String dyadicSymbol = sequence.currentInst().getString(0);
        Object expData = dataStack.pop();
        //
        if (expData instanceof DataModel) {
            expData = ((DataModel) expData).asOri();
        }
        //
        Class<?> expType = (expData == null) ? Void.class : expData.getClass();
        OperatorProcess process = context.findUnaryOperator(dyadicSymbol, expType);
        //
        if (process == null) {
            throw new InstructRuntimeException(sequence.programLocation(), "UO -> " + dyadicSymbol + " OperatorProcess is Undefined");
        }
        //
        Object result = process.doProcess(sequence.programLocation(), dyadicSymbol, new Object[] { expData }, context.currentHints());
        dataStack.push(result);
    }
}