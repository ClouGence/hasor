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
package org.more.convert;
/**
 * 类型转换处理类型转换的辅助类基类。
 * @version 2009-5-23
 * @author 赵永春 (zyc@byshell.org)
 */
public interface Converter {
    /**
     * 转换对象目标类型。返回转换结果。
     * @param object 要被转换的类型。
     * @return 转换对象目标类型。返回转换结果。
     */
    public Object convert(Class<?> toType, Object object);
}