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
package org.more.core.classcode;
import java.lang.reflect.Method;
import org.more.InvokeException;
/**
* 过滤器链的最终一层，负责在生成的aop类中调用。
 * @version 2009-10-30
 * @author 赵永春 (zyc@byshell.org)
 */
class AopFilterChain_End implements AopFilterChain {
    public Object doInvokeFilter(Object target, Method method, Object[] args) throws Throwable {
        try {
            if (method == null)
                throw new InvokeException("在Aop链最终环节丢失方法。");
            return method.invoke(target, args);
        } catch (Throwable e) {
            if (e instanceof RuntimeException == true)
                throw (RuntimeException) e;
            throw new InvokeException(e);
        }
    }
}