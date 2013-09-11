/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.core.binder;
/**
 * Bean的注册信息。
 * @version : 2013-5-6
 * @author 赵永春 (zyc@hasor.net)
 */
public interface BeanInfo {
    /**获取bean的名称*/
    public String getName();
    /**获取bean的别名称*/
    public String[] getAliasName();
    /**获取bean的类型*/
    public Class<?> getBeanType();
}