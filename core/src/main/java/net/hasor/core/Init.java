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
import net.hasor.core.ApiBinder.LifeBindingBuilder;

import java.lang.annotation.*;
/**
 * 标记方法为初始化方法，如果{@link LifeBindingBuilder#initMethod(String)}方法也定义了一个初始化方法则，注解方式优先于配置。
 * @see LifeBindingBuilder#initMethod(String)
 * @version : 2015年7月28日
 * @author 赵永春(zyc@hasor.net)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Documented
public @interface Init {
}