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
import java.lang.annotation.*;

/**
 * 标记接口或者包上，用于忽略 Hasor 的动态代理功能。当标记到包上时表示整个包都忽略动态代理。
 * 该功能可以有效的防止泛滥的全局动态代理。优先级顺序为：类->父类->包->父包
 * @version : 2016年12月22日
 * @author 赵永春 (zyc@hasor.net)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.PACKAGE })
@Documented
public @interface IgnoreProxy {
    /** 当前注解的配置生效范围是否传递到子包或子类中。如果设置为 false 表示配置只有在当前包或类有效，不会传播到子包或子类中（默认为 true）*/
    public boolean propagate() default true;

    /** 是否忽略Aop配置（默认为 false） */
    public boolean ignore() default true;
}