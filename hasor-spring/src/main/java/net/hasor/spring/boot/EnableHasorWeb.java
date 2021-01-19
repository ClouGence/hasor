/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.spring.boot;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用 Hasor Web，这个插件会配置 Hasor 的全局拦截器和监听器。
 * @version : 2020年02月27日
 * @author 赵永春 (zyc@hasor.net)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(value = { WebHasorConfiguration.class })
public @interface EnableHasorWeb {
    /** Hasor 全局拦截器工作的目录 */
    public String path() default "/*";

    /** Hasor 全局拦截器的顺序 */
    public int order() default 0;

    /** Hasor 全局拦截器的工作模式，默认：控制器模式。 */
    public WorkAt at() default WorkAt.Controller;
}
