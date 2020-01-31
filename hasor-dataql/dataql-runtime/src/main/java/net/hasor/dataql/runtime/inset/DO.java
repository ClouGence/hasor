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
 * DO      // 二元运算，堆栈【第一个操作数，第二个操作数】  第一操作数 * 第二操作数
 *         - 参数说明：共1参数；参数1：二元操作符
 *         - 栈行为：消费2，产出1
 *         - 堆行为：无
 *
 * 开发者可以通过实现 OperatorProcess 接口，覆盖某个运算符实现 运算符重载功能。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class DO implements InsetProcess {
    @Override
    public int getOpcode() {
        return DO;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) throws InstructRuntimeException {
        String dyadicSymbol = sequence.currentInst().getString(0);
        Object secExpData = dataStack.pop();
        Object fstExpData = dataStack.pop();
        //
        if (fstExpData instanceof DataModel) {
            fstExpData = ((DataModel) fstExpData).asOri();
        }
        if (secExpData instanceof DataModel) {
            secExpData = ((DataModel) secExpData).asOri();
        }
        //
        Class<?> fstType = (fstExpData == null) ? Void.class : fstExpData.getClass();
        Class<?> secType = (secExpData == null) ? Void.class : secExpData.getClass();
        OperatorProcess process = context.findDyadicOperator(dyadicSymbol, fstType, secType);
        //
        if (process == null) {
            throw new InstructRuntimeException("DO -> type '" + fstType + "' and type '" + fstType + "' operation '" + dyadicSymbol + "' is not supported.");
        }
        //
        Object result = process.doProcess(dyadicSymbol, new Object[] { fstExpData, secExpData }, context);
        dataStack.push(result);
    }
}