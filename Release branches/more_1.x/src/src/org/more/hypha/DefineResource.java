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
package org.more.hypha;
import java.net.URI;
import java.util.List;
import org.more.NoDefinitionException;
import org.more.hypha.beans.AbstractBeanDefine;
import org.more.util.attribute.IAttribute;
/**
 * 这个接口是more.beans组建的基本接口之一，该接口用于提供{@link AbstractBeanDefine}的索引获取功能。
 * 接口实现类决定bean数据是以什么形式存在（DBMS、LDAP、XML这些都是数据提供形式）。
 * @version 2010-9-24
 * @author 赵永春 (zyc@byshell.org)
 */
public interface DefineResource extends DefineResourcePluginSet {
    ///**根据当前配置信息生成一个{@link ApplicationContext}服务接口。*/
    //public ApplicationContext buildApplication();
    /**获取{@link DefineResource}的属性访问接口。*/
    public IAttribute getAttribute();
    /**获取事件管理器。*/
    public EventManager getEventManager();
    /**获取资源名。*/
    public String getSourceName();
    /**获取资源的URI表述形式，如果资源不支持该表述形式则返回null。*/
    public URI getSourceURI();
    /**
     * 获取bean的定义，在Bean的定义中只包括属于当前bean的元信息。
     * @param name 要获取bean定义的bean名称。
     * @return 返回bean定义，如果获取不到指定的bean定义则返回null。
     */
    public AbstractBeanDefine getBeanDefine(String name) throws NoDefinitionException;
    /**
     * 检测是否存在某个名称的Bean定义，如果存在目标bean定义则返回true，否则返回false。
     * @param name 要检测的Bean定义名称。
     * @return 返回检测结果。如果存在返回true否则返回false。
     */
    public boolean containsBeanDefine(String name);
    /**
     * 获取{@link DefineResource}中可以索引到的所有bean定义名称集合，如果获取不到任何名称则需要返回一个空集合。
     * @return 返回获取到的所有bean定义名称集合。
     */
    public List<String> getBeanDefineNames();
    /**
     * 测试某名称Bean是否为原型模式创建，如果目标bean不存在则会引发{@link NoDefinitionException}异常。
     * @param name 要测试的Bean名称。
     * @return 返回测试结果，如果是以原型模式创建则返回true,否则返回false。
     */
    public boolean isPrototype(String name) throws NoDefinitionException;
    /**
     * 测试某名称Bean是否为单态模式创建，如果目标bean不存在则会引发{@link NoDefinitionException}异常。
     * @param name 要测试的Bean名称。
     * @return 返回测试结果，如果是以原型模式创建则返回true,否则返回false。
     */
    public boolean isSingleton(String name) throws NoDefinitionException;
    /**
     * 测试某名称Bean是否为工厂模式创建，如果目标bean不存在则会引发{@link NoDefinitionException}异常。
     * @param name 要测试的Bean名称。
     * @return 返回测试结果，如果是以原型模式创建则返回true,否则返回false。
     */
    public boolean isFactory(String name) throws NoDefinitionException;
    /**初始化{@link DefineResource}接口，如果重复调用init可能引发一些意外异常。*/
    public void init() throws Exception;
    /**销毁BeanResource接口。*/
    public void destroy() throws Exception;
    /** 如果已经初始化则执行销毁在执行初始化。*/
    public void reload() throws Exception;
};