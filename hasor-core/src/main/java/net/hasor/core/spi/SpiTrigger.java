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
import net.hasor.core.spi.SpiCaller.SpiCallerWithoutResult;

import java.util.EventListener;

/**
 * SPI 触发器：SPI 的本真意图是在应用流程执行的过程中，安插一些扩展点。
 *  - 让应用有机会在正常的流程中对外提供接口扩展能力。
 *  - 通过 SPI 接口达到获得流程内部状态 或 干预程序流程中变量值的目的。
 * @version : 2013-11-8
 * @author 赵永春 (zyc@hasor.net)
 */
public interface SpiTrigger {
    /**
     * 通知型 SPI：保证所有 SPI 都会被触发，每一个SPI监听器 都可以拿到最初的值。
     *  - 也可以理解是异步工作思想
     * @param spiType SPI 接口类型
     * @param spiCaller spiCaller
     */
    public default <T extends EventListener> void notifySpiWithoutResult(Class<T> spiType, SpiCallerWithoutResult<T> spiCaller) {
        notifySpi(spiType, spiCaller, null);
    }

    /**
     * 通知型 SPI：保证所有 SPI 都会被触发，每一个SPI监听器 都可以拿到最初的值。
     *  - 也可以理解是异步工作思想
     * @param spiType SPI 接口类型
     * @param spiCaller spiCaller
     */
    public <R, T extends EventListener> R notifySpi(Class<T> spiType, SpiCaller<T, R> spiCaller, R defaultResult);

    /**
     * 链型 SPI： 监听器工作模式类似Aop拦截器，下一个 SPI监听器 可以获取上一个 SPI监听器的值。
     *  - 也可以理解是同步工作思想
     * @param spiType SPI 接口类型
     * @param spiCaller spiCaller
     */
    public default <R, T extends EventListener> R chainSpi(Class<T> spiType, SpiCaller<T, R> spiCaller) {
        return chainSpi(spiType, spiCaller, null);
    }

    /**
     * 链型 SPI： 监听器工作模式类似Aop拦截器，下一个 SPI监听器 可以获取上一个 SPI监听器的值。
     *  - 也可以理解是同步工作思想
     * @param spiType SPI 接口类型
     * @param spiCaller spiCaller
     * @param defaultResult 默认值
     */
    public <R, T extends EventListener> R chainSpi(Class<T> spiType, SpiCaller<T, R> spiCaller, R defaultResult);

    /** 判断某类 SPI 是否有注册 */
    public boolean hasSpi(Class<? extends EventListener> spiType);

    /** 判断某类 SPI 是否有注册了仲裁 */
    public boolean hasJudge(Class<? extends SpiJudge> spiJudge);
}