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
package net.hasor.web.invoker;
import net.hasor.web.Invoker;
import net.hasor.web.MappingData;

import java.lang.reflect.Method;
/**
 * @version : 2016-12-26
 * @author 赵永春 (zyc@hasor.net)
 */
interface InMapping extends MappingData {
    /**
     * 首先测试路径是否匹配，然后判断Restful实例是否支持这个 请求方法。
     * @return 返回测试结果。
     */
    public boolean matchingMapping(Invoker invoker);

    public String getMappingToMatches();

    /**
     * 调用目标
     * @throws Throwable 异常抛出
     */
    public Method findMethod(final Invoker invoker);

    public boolean isAsync(Invoker invoker);

    /**创建对象*/
    public Object newInstance(Invoker invoker) throws Throwable;
}