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
import net.hasor.core.utils.StringUtils;
import net.hasor.dataql.InvokerProcessException;
import net.hasor.dataql.ProcessException;
import net.hasor.dataql.result.ObjectModel;
import net.hasor.dataql.runtime.InsetProcess;
import net.hasor.dataql.runtime.InstSequence;
import net.hasor.dataql.runtime.ProcessContet;
import net.hasor.dataql.runtime.mem.LocalData;
import net.hasor.dataql.runtime.mem.MemStack;
/**
 * NO，创建一个对象 or Map。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
class NO implements InsetProcess {
    @Override
    public int getOpcode() {
        return NO;
    }
    @Override
    public void doWork(InstSequence sequence, MemStack memStack, LocalData local, ProcessContet context) throws ProcessException {
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
        //
        try {
            memStack.push(objectType.newInstance());
        } catch (Exception e) {
            throw new InvokerProcessException(getOpcode(), "NO -> " + e.getMessage(), e);
        }
    }
}