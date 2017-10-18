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
package net.hasor.dataql;
/**
 * UDF查找方式
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-14
 */
public enum LoadType {
    /**根据 name 查找 UDF。*/
    ByName,//
    /**根据 javaClass 查找 UDF。*/
    ByType,//
    /**根据 资源地址加载一个 UDF。*/
    ByResource//
}