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
package net.hasor.graphql;
import java.lang.annotation.*;
/**
 * 声明为一个 UDF，但该类同时还需要实现 UDF 接口才有效。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
public @interface GraphUDF {
    /**UDF 名称，如果没有设置，那么使用完整类名作为 UDF 名称。*/
    public String value() default "";
}