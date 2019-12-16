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
import net.hasor.dataql.FragmentProcess;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InsetProcessContext;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.InstructRuntimeException;
import net.hasor.dataql.runtime.mem.*;

/**
 * M_FRAG  // 加载一个 代码执行片段的执行器。
 *         - 参数说明：共1参数；参数1：片段类型
 *         - 栈行为：消费0，产出1
 *         - 堆行为：无
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-14
 */
class M_FRAG implements InsetProcess {
    @Override
    public int getOpcode() {
        return M_FRAG;
    }

    @Override
    public void doWork(InstSequence sequence, DataHeap dataHeap, DataStack dataStack, EnvStack envStack, InsetProcessContext context) {
        String fragmentType = sequence.currentInst().getString(0);
        FragmentProcess loadObject = context.findFragmentProcess(fragmentType);
        if (loadObject == null) {
            throw new InstructRuntimeException(fragmentType + " fragment undefine.");
        }
        dataStack.push(new RefCall(true, new RefFragmentCall(loadObject)));
    }
}