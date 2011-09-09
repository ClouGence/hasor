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
package org.more.util.config;
import java.util.Enumeration;
/**
 * 配置接口,该接口作为工具接口而存在以便于在整个more中各个组建可以重复使用它
 * Date : 2010-7-26
 * @author 赵永春(zyc@byshell.org)
 */
public interface Config<T> {
    /**
     * 获取上下文对象。
     * @return 返回上下文对象。
     */
    public T getContext();
    /**
     * 根据参数名获取参数。如果不存在这个参数则返回null。
     * @param name 要获取的参数名。
     * @return 根据参数名获取参数。如果不存在这个参数则返回null。
     */
    public Object getInitParameter(String name);
    /**
     * 获取配置属性名称集合。
     * @return 返回配置属性名称集合。
     */
    public Enumeration<String> getInitParameterNames();
}