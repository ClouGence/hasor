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
import net.hasor.core.ApiBinder.InjectConstructorBindingBuilder;

import java.lang.annotation.*;

/**
 * 如果通过{@link InjectConstructorBindingBuilder}接口配置会覆盖注解配置。
 * 如果在该类上出现多个 {@link ConstructorBy} 注解配置，那么将会按照 构造方法参数个数排序取参数数量最少的那个构造方法。
 * @see InjectConstructorBindingBuilder
 * @see javax.inject.Inject
 * @version : 2018年9月21日
 * @author 赵永春 (zyc@hasor.net)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.CONSTRUCTOR })
@Documented
@Deprecated
public @interface ConstructorBy {
}