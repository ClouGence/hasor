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
/**
* 代表过滤器链的一个环节接口，当{@link AopInvokeFilter}接口的doInvokeFilter方法被执行时{@link AopFilterChain}类型参数对象
* 表示这个过滤器所处过滤器链的下一个过滤器资源。本接口的doInvokeFilter方法表示执行过滤器资源，
* 下一个过滤器资源可能是过滤器链的下一个过滤器元素也可能是真实的资源方法。对于classcode最终资源就是目标方法。
* @version 2009-10-30
* @author 赵永春 (zyc@byshell.org)
*/
public interface AopFilterChain {
    /**
     * 执行过滤器下一个过滤器资源的方法，通过该方法继续调用过滤器链的动作。
     * @param target 执行方法的对象。
     * @param method 被调用的方法该对象中包含了被调用的代理方法和原始方法。
     * @param args 调用方法所传递的参数。
     * @return 返回执行结果。
     * @throws Throwable 执行过滤器资源时发生异常。
     */
    public Object doInvokeFilter(Object target, Method method, Object[] args) throws Throwable;
}