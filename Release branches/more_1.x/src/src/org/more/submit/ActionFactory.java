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
 * Action工厂接口，该接口的实现类应当负责对action对象的创建，以及负责action对象的filter装载。该类在submit中还是一个action容器。
 * @version 2009-6-25
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ActionFactory {
    /**
     * 查找并返回指定名称的Action对象，当找不到指定名称的Action对象则返回null。
     * @param name 要查找的action名
     * @return 返回找到的Action对象，如果找不到指定名称的Action对象则返回null。
     */
    public Object findAction(String name);
    /**
     * 查找并返回指定action的某一个属性，当找不到时候返回null。
     * @param name 要查找的action名
     * @param propName 要查找的属性名
     * @return 返回查找并返回指定action的某一个属性，当找不到时候返回null。
     */
    public Object findActionProp(String name, String propName);
    /**
     * 查找并返回指定名称的Action对象，当找不到指定名称的Action对象则返回flase，否则返回true。
     * @param name 要查找的action名
     * @return 返回查找Action的结果，如果找不到指定名称的Action对象返回false否则返回true。
     */
    public boolean containsAction(String name);
    /**
     * 根据指定的action名获取这个action上的过滤器名集合，过滤器名已经按照过滤器链的顺序进行处理过。
     * @param actionName action名
     * @return 返回根据指定的action名获取这个action上的过滤器名集合，过滤器名已经按照过滤器链的顺序进行处理过。
     */
    public String[] getActionFilterNames(String actionName);
    /**
     * 获取Factory中所有Action的名称集合。
     * @return 返回Factory中所有Action的名称集合。
     */
    public String[] getActionNames();
    /**
     * 获取Bean的Class类型。
     * @param name 要获取的Bean名。
     * @return 返回Bean的Class类型。
     */
    public Class<?> getType(String name);
    /**
     * 测试Bean是否为prototype。如果目标Action是prototype则返回true否则返回false。
     * @param name 要测试的Bean名
     * @return 返回测试结果测试Bean是否为prototype。如果目标Action是prototype则返回true否则返回false。
     */
    public boolean isPrototype(String name);
    /**
     * 测试Bean是否为singleton。如果目标Action是singleton则返回true否则返回false。
     * @param name 要测试的Bean名
     * @return 返回测试结果测试Bean是否为singleton。如果目标Action是singleton则返回true否则返回false。
     */
    public boolean isSingleton(String name);
}