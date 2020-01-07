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
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Scope;

import java.util.function.Supplier;

/**
 * 查找目标使用的作用域
 * @version : 2020-01-06
 * @author 赵永春 (zyc@hasor.net)
 */
public interface CollectScopeListener extends java.util.EventListener {
    /**
     * 查找目标使用的作用域。
     * @param bindInfo 正在被创建的 BindInfo
     * @param appContext 容器对象
     * @param suppliers 已经找到了的作用域
     */
    public Supplier<Scope>[] collectScope(BindInfo<?> bindInfo, AppContext appContext, Supplier<Scope>[] suppliers) throws Throwable;

    /**
     * 查找目标使用的作用域。
     * @param targetType 正在被创建的 类型
     * @param appContext 容器对象
     * @param suppliers 已经找到了的作用域
     */
    public Supplier<Scope>[] collectScope(Class<?> targetType, AppContext appContext, Supplier<Scope>[] suppliers) throws Throwable;
}