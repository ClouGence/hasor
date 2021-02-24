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
import net.hasor.dataql.UdfSource;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InsetProcessContext;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.QueryRuntimeException;
import net.hasor.dataql.runtime.mem.DataHeap;
import net.hasor.dataql.runtime.mem.DataStack;
import net.hasor.dataql.runtime.mem.EnvStack;

/**
 * M_TYP   // 加载一个类型对象到栈顶.
 *         - 参数说明：共1参数；参数为要加载的Bean名
 *         - 栈行为：消费0，产出1
 *         - 堆行为：无
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
class M_TYP implements InsetProcess {
    @Override
    public int getOpcode() {
        return M_TYP;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) throws QueryRuntimeException {
        String udfType = sequence.currentInst().getString(0);
        Object loadObject = null;
        try {
            loadObject = context.loadObject(udfType);
        } catch (ClassNotFoundException e) {
            throw new QueryRuntimeException(sequence.programLocation(), udfType + " ClassNotFoundException.", e);
        }
        if (loadObject == null) {
            throw new QueryRuntimeException(sequence.programLocation(), "loadObject is null.");
        }
        //
        if (loadObject instanceof UdfSource) {
            loadObject = ((UdfSource) loadObject).getUdfResource(context.getFinder()).get();
        }
        //
        dataStack.push(loadObject);
    }
}
