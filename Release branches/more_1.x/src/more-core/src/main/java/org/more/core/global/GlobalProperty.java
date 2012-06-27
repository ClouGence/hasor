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
package org.more.core.global;
/**
 * 该接口表示一个自定义读写方式的属性对象，内置对象会解析该类。global也会执行解析该类。
 * @version : 2011-9-30
 * @author 赵永春 (zyc@byshell.org)
 */
public interface GlobalProperty {
    /** 读取属性*/
    public <T> T getValue(AbstractGlobal global, Class<T> toType);
    /** 写入属性*/
    public void setValue(Object newValue, AbstractGlobal global);
}