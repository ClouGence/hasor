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
import net.hasor.dataql.InvokerProcessException;
import net.hasor.dataql.ProcessException;
import net.hasor.dataql.domain.compiler.Instruction;
import net.hasor.dataql.result.ObjectModel;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstFilter;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.mem.MemStack;
import net.hasor.dataql.runtime.mem.StackStruts;
import net.hasor.dataql.runtime.struts.ObjectResultStruts;
import net.hasor.utils.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;
/**
 * ASM，指令处理器。用于将结果作为对象进行处理。如果结果是集合，那么按照对象处理。
 *
 * 与 ASM 指令配对的还有一个对应的 ASE，在这一对 ASM -> ASE 范围内的指令。
 *
 * TODO 后续可以考虑优化这个代码，让对象的每个字段的处理做成并发。
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
class ASM implements InsetProcess {
    @Override
    public int getOpcode() {
        return ASM;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, StackStruts local, ProcessContet context) throws ProcessException {
        //
        // .读取返回值并包装成 ResultStruts
        String typeString = sequence.currentInst().getString(0);
        Class<?> objectType = null;
        if (StringUtils.isNotBlank(typeString)) {
            try {
                objectType = context.loadType(typeString);
            } catch (Exception e) {
                throw new InvokerProcessException(getOpcode(), "load type failed -> " + typeString, e);
            }
        } else {
            objectType = ObjectModel.class;
        }
        Object toType = null;
        try {
            toType = objectType.newInstance();
        } catch (Exception e) {
            throw new InvokerProcessException(getOpcode(), "newInstance -> " + objectType.getName(), e);
        }
        Object result = memStack.pop();
        memStack.push(new ObjectResultStruts(toType));
        //
        // .圈定处理结果集的指令集
        final AtomicInteger dogs = new AtomicInteger(0);
        InstSequence subSequence = sequence.findSubSequence(new InstFilter() {
            public boolean isExit(Instruction inst) {
                //
                if (ASM == inst.getInstCode() || ASA == inst.getInstCode()) {
                    dogs.incrementAndGet();
                    return false;
                }
                //
                if (ASE == inst.getInstCode()) {
                    dogs.decrementAndGet();
                    if (dogs.get() == 0) {
                        return true;
                    }
                }
                return false;
            }
        });
        //
        // .对结果集进行迭代处理
        subSequence.reset();    // 重置执行序列
        local.push(result);     // 设置DS
        context.processInset(subSequence, memStack, local);// 执行序列
        local.pop();            // 销毁DS
        //
        // .处理完毕跳到出口
        sequence.jumpTo(subSequence.exitPosition());
    }
}