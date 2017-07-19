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
package net.hasor.dataql.runtime.process;
import net.hasor.dataql.result.ListModel;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.ProcessException;
import net.hasor.dataql.runtime.struts.ListResultStruts;
import net.hasor.dataql.runtime.struts.LocalData;
import net.hasor.dataql.runtime.struts.MemStack;

import java.util.Collection;
/**
 * PUSH，将栈顶的数据 put 到结果集中。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
class PUSH implements InsetProcess {
    @Override
    public int getOpcode() {
        return PUSH;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, LocalData local, ProcessContet context) throws ProcessException {
        Object data = memStack.pop();
        Object ors = memStack.peek();
        //
        if (ors instanceof ListResultStruts) {
            ((ListResultStruts) ors).addResult(data);
            return;
        }
        if (ors instanceof ListModel) {
            ((ListModel) ors).add(data);
            return;
        }
        if (ors instanceof Collection) {
            ((Collection) ors).add(data);
            return;
        }
        throw new ProcessException("output data eror, target type must be 'ListResultStruts or ListModel or Collection.'");
    }
}