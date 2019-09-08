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
import java.lang.annotation.*;

/**
 * 标记在方法上用来设置 response 使用的 ContentType。如果没有配置该注解，那么 Hasor-web 会采用请求资源的后缀名，然后在 mime 中进行匹配。
 * @version : 2013-8-14
 * @author 赵永春 (zyc@hasor.net)
 */
@Inherited
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Produces {
    /** 指定的内容响应类型 */
    public String value();
}