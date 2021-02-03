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
/**
 * Hasor 的请求拦截器在 springwebmvc 中工作方式。
 * @version : 2020年02月27日
 * @author 赵永春 (zyc@hasor.net)
 */
public enum WorkAt {
    /** 过滤器模式，以 web filter 的方式进行集成 */
    Filter,
    /** 拦截器模式，以 springwebmvc 的拦截器方式进行集成 */
    Interceptor,
    /** 控制器模式，以 springwebmvc 的 Controller 方式进行集成 */
    Controller
}
