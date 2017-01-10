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
package net.hasor.web;
/**
 * 请求过滤器，相当于 {@link javax.servlet.Filter} 同等作用。
 * @version : 2016-12-26
 * @author 赵永春 (zyc@hasor.net)
 */
public interface InvokerFilter {
    /**
     * 初始化过滤器
     * @param config 配置信息
     * @throws Throwable 初始化过程中发生异常。
     */
    public void init(InvokerConfig config) throws Throwable;

    /**
     * 指定过滤器
     * @param invoker 当前请求信息
     * @param chain 过滤器链
     * @throws Throwable 执行过滤器中发生的异常。
     */
    public void doInvoke(Invoker invoker, InvokerChain chain) throws Throwable;

    /** 销毁过滤器。 */
    public void destroy();
}