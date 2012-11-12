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
package org.more.hypha.define;
/**
 * 该枚举中定义了{@link Simple_ValueMetaData}类可以表示的基本类型。
 * @version 2010-11-11
 * @author 赵永春 (zyc@byshell.org)
 */
public enum PropertyType {
    /**null数据。*/
    Null,
    /**布尔类型。*/
    Boolean,
    /**字节类型。*/
    Byte,
    /**短整数类型。*/
    Short,
    /**整数类型。*/
    Int,
    /**长整数类型。*/
    Long,
    /**单精度浮点数类型。*/
    Float,
    /**双精度浮点数类型。*/
    Double,
    /**字符类型。*/
    Char,
    /**字符串类型。*/
    String,
}