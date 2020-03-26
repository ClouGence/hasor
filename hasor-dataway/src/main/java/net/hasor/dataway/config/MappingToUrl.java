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
package net.hasor.dataway.config;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 控制器映射的地址，没有直接使用 MappingTo 的原因是想和 MappingTo 隔离开。
 * @version : 2013-3-26
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MappingToUrl {
    /**请求地址*/
    public String value();
}
