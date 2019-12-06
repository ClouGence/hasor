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

/**
 * OPT     // 环境配置，影响执行引擎的参数选项。
 *         - 参数说明：共2参数；参数1：选项Key；参数2：选项Value
 *         - 栈行为：消费2，产出0
 *         - 堆行为：无
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class OPT implements InsetProcess {
    @Override
    public int getOpcode() {
        return OPT;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) {
        Object value = dataStack.pop();
        String key = (String) dataStack.pop();
        //
        if (value == null) {
            context.removeOption(key);
        } else if (value instanceof Boolean) {
            context.setOption(key, (Boolean) value);
        } else if (value instanceof Number) {
            context.setOption(key, (Number) value);
        } else {
            context.setOption(key, value.toString());
        }
    }
}