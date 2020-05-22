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
package net.hasor.dataway.schema.types;
/**
 * 类型定义
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-05-21
 */
public enum TypeEnum {
    String,     // 字符串
    Boolean,    // Boolean
    Number,     // 数字
    Array,      // 数组或集合
    Struts,     // 结构体
    Ref,        // 引用其它结构
    Map         // Key-Value 映射对
}