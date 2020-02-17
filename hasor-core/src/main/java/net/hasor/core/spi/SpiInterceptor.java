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
package net.hasor.core.spi;
/**
 * SPI 调用拦截器。
 * @version : 2013-11-8
 * @author 赵永春 (zyc@hasor.net)
 */
public interface SpiInterceptor {
    public interface SpiChainInvocation {
        /** 上一个 Spi 的结果。当作为 SpiChain 环中第一被执行时候，该方法将会返回 default 默认值。
         * @see SpiTrigger#callResultSpi(java.lang.Class, net.hasor.core.spi.SpiCaller, java.lang.Object) */
        public Object defaultValue();

        /** 调用 SPI 得到结果。 */
        public Object doSpi() throws Throwable;
    }

    public Object doSpi(SpiChainInvocation invocation) throws Throwable;
}