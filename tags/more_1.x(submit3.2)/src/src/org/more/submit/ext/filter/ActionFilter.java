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
package org.more.submit.ext.filter;
import org.more.submit.ActionStack;
/**
 * submit的action执行过滤器，同时这个过滤器也叫做action拦截器。
 * @version 2009-11-28
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ActionFilter {
    /**
     * 当action请求被拦截之后拦截器的doActionFilter方法会被自动调用。如果向继续执行拦截器那么就调用chain方法的execute方法既可。
     * @param stack 当调用目标方法时希望给目标方法传递的事件对象。
     * @param chain 当执行过滤器时该参数可以用于决定是否继续执行过滤器。
     * @return 返回执行过滤器之后的执行结果。
     * @throws Throwable 当执行时发生异常
     */
    public Object doActionFilter(ActionStack stack, FilterChain chain) throws Throwable;
};