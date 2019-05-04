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
package net.hasor.web.annotation;
import net.hasor.web.MimeType;
import net.hasor.web.RenderInvoker;

import java.lang.annotation.*;
/**
 * 标记在方法上用来设置一个 MimeType name。在 Controller 调用之前 RenderWebPlugin 会先得到这个值，
 * 然后通过 {@link MimeType#getMimeType(String)} 方式得到 ContentType ，之后设置到 response 上。
 * 最后才会调用用户的 Controller。
 * 注意：该注解只有预设置作用。在 Controller 执行过程中，可以通过 {@link RenderInvoker#renderTo(String, String)}
 * 和 {@link RenderInvoker#viewType(String)} 两个方法来改变 Produces 注解的行为。
 * @version : 2013-8-14
 * @author 赵永春 (zyc@hasor.net)
 */
@Inherited
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Produces {
    /**响应的类型*/
    public String value();
}