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
 * 实现AOP的方法过滤器，通过过滤器中{@link AOPFilterChain}对象的方法可以执行过滤器节点的下一个节点。
 * 当过滤器执行到最后一个节点时正式执行方法调用。如果过滤器想立即返回方法调用结果可以直接使用
 * callMethod对象的invoke函数直接调用目标方法产生方法返回值然后进行其他操作。
 * 利用多个{@link AOPInvokeFilter}可以组成过滤器链。
 * @version 2009-10-18
 * @author 赵永春 (zyc@byshell.org)
 */
public interface AOPInvokeFilter {
    /**
     * 当调用受AOP拦截的方法时执行该方法，chain参数对象可以用来继续执行过滤器链。如果直接调用
     * callMethod对象的invoke函数则可以跳过过滤器链直接执行目标方法，并获取返回值。
     * @param target 目标对象。
     * @param methods 被调用的方法该对象中包含了被调用的代理方法和原始方法。
     * @param args 调用AOP拦截方法时的方法参数。
     * @param chain AOP过滤器链对象。
     * @return 返回方法返回值。交付上一个调用过滤器或者直接返回方法回调。
     * @throws Throwable 如果在执行时发生异常。
     */
    public Object doFilter(Object target, AOPMethods methods, Object[] args, AOPFilterChain chain) throws Throwable;
}