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
package org.more.submit;
/**
 * 过滤器工厂对象。通过该接口可以获取相应的Action过滤器。
 * Date : 2009-6-29
 * @author 赵永春
 */
public interface FilterFactory {
    /**
     * 查找指定名称的过滤器，如果找不到指定名称的过滤器则返回null。
     * @param name 过滤器名。
     * @return 返回查找到的过滤器对象，如果找不到指定名称的过滤器则返回null。
     */
    public ActionFilter findFilter(String name);
    /**
     * 获取全局过滤器名称。
     * @return 返回全局过滤器名称。
     */
    public String[] findPublicFilterNames();
    /**
     * 获取所有过滤器名称。
     * @return 返回所有过滤器名称。
     */
    public String[] findFilterNames();
    /**
     * 查找并返回指定filter的某一个属性，当找不到时候返回null。
     * @param name 要查找的filter名
     * @param propName 要查找的属性名
     * @return 返回查找并返回指定filter的某一个属性，当找不到时候返回null。
     */
    public Object findFilterProp(String name, String propName);
}