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
package org.hasor.binder;
import com.google.inject.Key;
/**
 * 表示一个注册的Bean信息。
 * @version : 2013-5-6
 * @author 赵永春 (zyc@byshell.org)
 */
public interface BeanInfo {
    /**获取bean的名称*/
    public String getName();
    /**获取bean的别名称*/
    public String[] getAliasName();
    /**获取bean的类型*/
    public Class<?> getBeanType();
    /**获取Key*/
    public Key<?> getKey();
}