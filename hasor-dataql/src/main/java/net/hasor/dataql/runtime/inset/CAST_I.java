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
import net.hasor.dataql.runtime.mem.DataHeap;
import net.hasor.dataql.runtime.mem.DataIterator;
import net.hasor.dataql.runtime.mem.DataStack;
import net.hasor.dataql.runtime.mem.EnvStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * CAST_I  // 将栈顶元素转换为迭代器，作为迭代器有三个特殊操作：data(数据)、next(移动到下一个，如果成功返回true)
 *         - 参数说明：共0参数
 *         - 栈行为：消费1，产出1
 *         - 堆行为：无
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class CAST_I implements InsetProcess {
    @Override
    public int getOpcode() {
        return CAST_I;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) {
        Object data = dataStack.pop();
        Iterator iterator = null;
        //
        if (data == null) {
            iterator = Collections.EMPTY_LIST.iterator();
        } else if (data instanceof ValueModel && ((ValueModel) data).isNull()) {
            iterator = Collections.EMPTY_LIST.iterator();
        } else if (data instanceof ListModel) {
            iterator = ((ListModel) data).asOri().iterator();
        } else if (data instanceof Collection) {
            iterator = ((Collection) data).iterator();
        } else if (data.getClass().isArray()) {
            iterator = Arrays.asList((Object[]) data).iterator();
        } else {
            iterator = Collections.singletonList(data).iterator();
        }
        //
        dataStack.push(new DataIterator(iterator));
    }
}