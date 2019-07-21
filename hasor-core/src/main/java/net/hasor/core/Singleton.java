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
import net.hasor.core.ApiBinder.ScopedBindingBuilder;

import javax.inject.Scope;
import java.lang.annotation.*;

/**
 * 标记类型为单例模式，与 {@link Prototype} 为互斥关系，代码配置优先于注解。
 * 当 {@link Singleton} 和 {@link ImplBy} 组合使用时，标记在接口上的 Singleton 注解会覆盖 ImplBy 指定的那个实现。
 * @see ScopedBindingBuilder#asEagerSingleton()
 * @see javax.inject.Singleton
 * @version : 2015年7月28日
 * @author 赵永春 (zyc@hasor.net)
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Deprecated
public @interface Singleton {
}