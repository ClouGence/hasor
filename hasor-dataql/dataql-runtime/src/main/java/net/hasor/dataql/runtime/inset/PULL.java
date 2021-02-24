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
import net.hasor.dataql.domain.ListModel;
import net.hasor.dataql.domain.ValueModel;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InsetProcessContext;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.QueryRuntimeException;
import net.hasor.dataql.runtime.mem.DataHeap;
import net.hasor.dataql.runtime.mem.DataStack;
import net.hasor.dataql.runtime.mem.EnvStack;
import net.hasor.utils.convert.ConverterUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static net.hasor.dataql.HintNames.INDEX_OVERFLOW;
import static net.hasor.dataql.HintValue.*;

/**
 * PULL    // 栈顶元素是一个集合类型，获取集合的指定索引元素。（例：PULL 123）
 *         - 参数说明：共1参数；参数1：元素位置(负数表示从后向前，正数表示从前向后)
 *         - 栈行为：消费1，产出1
 *         - 堆行为：无
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class PULL implements InsetProcess {
    @Override
    public int getOpcode() {
        return PULL;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) throws QueryRuntimeException {
        int point = 0;
        if (sequence.currentInst().getArrays().length > 0) {
            point = sequence.currentInst().getInt(0);
        } else {
            Object pointData = dataStack.pop();
            if (pointData instanceof ValueModel) {
                point = ((ValueModel) pointData).asInt();
            } else if (pointData instanceof Number) {
                point = ((Number) pointData).intValue();
            } else {
                point = (int) ConverterUtils.convert(Integer.TYPE, pointData);
            }
        }
        Object data = dataStack.pop();
        //
        if (data == null) {
            dataStack.push(null);
            return;
        } else if (data instanceof ListModel) {
            data = ((ListModel) data).asOri();
        } else if (data.getClass().isArray()) {
            data = Arrays.asList((Object[]) data);
        }
        //
        String indexOverflow = context.currentHints().getOrMap(INDEX_OVERFLOW.name(), val -> {
            return (val == null) ? INDEX_OVERFLOW_NEAR : val.toString();
        });
        //
        if (!(data instanceof Collection)) {
            throw new QueryRuntimeException(sequence.programLocation(), "output data error, target type must be Collection.");
        }
        int size = ((Collection) data).size();
        if (point < 0) {
            point = size + point;
            if (point <= 0) {
                if (INDEX_OVERFLOW_THROW.equalsIgnoreCase(indexOverflow)) {
                    throw new ArrayIndexOutOfBoundsException(point + " out of " + size);
                }
                if (INDEX_OVERFLOW_NULL.equalsIgnoreCase(indexOverflow)) {
                    dataStack.push(null);// 不使用溢出能力，以 null 代替
                    return;
                } else {
                    point = 0;//反向溢出，索引位置归零
                }
            }
        } else if (point >= size) {
            if (INDEX_OVERFLOW_THROW.equalsIgnoreCase(indexOverflow)) {
                throw new ArrayIndexOutOfBoundsException(point + " out of " + size);
            }
            if (INDEX_OVERFLOW_NULL.equalsIgnoreCase(indexOverflow)) {
                dataStack.push(null);// 不使用溢出能力，以 null 代替
                return;
            } else {
                point = size - 1;//正向溢出，索引位置归到最大
            }
        }
        //
        Object pullData = null;
        if (data instanceof List) {
            pullData = ((List) data).get(point);
        } else {
            pullData = ((Collection) data).toArray()[point];
        }
        //
        dataStack.push(pullData);
    }
}
