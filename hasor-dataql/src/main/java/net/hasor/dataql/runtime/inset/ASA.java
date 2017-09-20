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
import net.hasor.dataql.runtime.struts.ListResultStruts;
import net.hasor.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * ASA，指令处理器。用于将结果作为 集合进行处理。如果结果是单条而非集合，那么结果会被先转换为 只有一个元素的 List 在进行处理。
 *
 * 与 ASA 指令配对的还有一个对应的 ASE，在这一对 ASA -> ASE 范围内的指令。会在每一个数据元素上都执行一遍。
 *
 * TODO 后续可以考虑优化这个代码做成并发处理。
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
class ASA implements InsetProcess {
    @Override
    public int getOpcode() {
        return ASA;
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
        memStack.push(new ListResultStruts(toType));
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
        Collection<Object> dataSet = toCollection(result);
        for (Object obj : dataSet) {
            subSequence.reset();    // 重置执行序列
            local.push(obj);        // 设置DS数据源
            context.processInset(subSequence, memStack, local);// 执行序列
            local.pop();            // 销毁DS数据源
        }
        //
        // .处理完毕跳到出口
        sequence.jumpTo(subSequence.exitPosition());
    }
    private Collection<Object> toCollection(Object curData) {
        Collection<Object> listData = null;
        if (curData == null) {
            listData = new ArrayList<Object>();
        } else {
            if (!(curData instanceof Collection)) {
                if (curData.getClass().isArray()) {
                    listData = new ArrayList<Object>();
                    for (Object obj : (Object[]) curData) {
                        listData.add(obj);
                    }
                } else {
                    listData = Arrays.asList(curData);
                }
            } else {
                listData = (Collection<Object>) curData;
            }
        }
        //
        return listData;
    }
}