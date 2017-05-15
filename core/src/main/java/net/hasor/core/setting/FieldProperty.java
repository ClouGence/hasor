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
package net.hasor.core.setting;
/**
 * 该接口表示一个自定义属性对象。
 * @version : 2011-9-30
 * @author 赵永春 (zyc@hasor.net)
 */
public interface FieldProperty {
    /** 读取属性*/
    public <T> T getValue(Class<T> toType, T defaultValue);
}