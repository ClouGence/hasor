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
package net.hasor.core;
/**
 * Hasor模块，任何扩展功能都是通过 Module 接口进行，这是 Hasor 开发的主要入口。
 * @version : 2013-3-20
 * @author 赵永春 (zyc@hasor.net)
 */
@FunctionalInterface
public interface Module {
    /** 表示放弃后续 onStart/onStop 的执行 */
    public static final class IgnoreModuleException extends RuntimeException {
    }

    /**
     * 初始化过程，注意：apiBinder 参数只能在 loadModule 阶段中使用。
     * 如果只要不抛错，后续 onStart/onStop 都会被调用
     * @param apiBinder
     * @throws Throwable init异常抛出
     * @throws IgnoreModuleException 如果抛出该类型异常则表示放弃后续 onStart/onStop 的执行，module 的加载仍然继续
     */
    public void loadModule(ApiBinder apiBinder) throws Throwable;

    /**
     * 启动过程。
     * @param appContext appContext
     * @throws Throwable init异常抛出
     */
    public default void onStart(AppContext appContext) throws Throwable {
    }

    /**
     * 终止过程。
     * @param appContext appContext
     * @throws Throwable init异常抛出
     */
    public default void onStop(AppContext appContext) throws Throwable {
    }
}
