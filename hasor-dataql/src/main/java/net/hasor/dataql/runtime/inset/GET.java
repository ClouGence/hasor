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
import net.hasor.dataql.runtime.InsetProcessContext;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.mem.DataHeap;
import net.hasor.dataql.runtime.mem.DataStack;
import net.hasor.dataql.runtime.mem.EnvStack;
import net.hasor.utils.BeanUtils;

import java.util.Map;

/**
 * GET     // 获取栈顶对象元素的属性（例：GET,"xxxx"）
 *         - 参数说明：共1参数；参数1：属性名称（Map的Key 或 对象的属性名）
 *         - 栈行为：消费1，产出1
 *         - 堆行为：无
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class GET implements InsetProcess {
    @Override
    public int getOpcode() {
        return GET;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) throws ProcessException {
        String nodeName = sequence.currentInst().getString(0);
        Object useData = dataStack.pop();
        useData = readProperty(useData, nodeName);
        dataStack.push(useData);
    }

    private static Object readProperty(Object object, String fieldName) {
        if (object == null) {
            return null;
        }
        if (object instanceof Map) {
            return ((Map) object).get(fieldName);
        }
        //
        return BeanUtils.readPropertyOrField(object, fieldName);
    }
}