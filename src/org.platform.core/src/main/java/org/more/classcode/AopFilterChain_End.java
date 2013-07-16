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
package org.more.classcode;
/**
 * 过滤器链的最终一层，该类负责在aop调用链的最后环节执行方法调用。如果在执行最终调用时method参数无法定位其方法那么将会导致NoSuchMethodException异常
 * @version 2010-9-2
 * @author 赵永春 (zyc@byshell.org)
 */
class AopFilterChain_End implements AopFilterChain {
    public Object doInvokeFilter(Object target, Method method, Object[] args) throws Throwable {
        if (method == null)
            throw new LostException("在Aop链最终环节丢失方法。");
        return method.getTargetMeyhod().invoke(target, args);
    }
}