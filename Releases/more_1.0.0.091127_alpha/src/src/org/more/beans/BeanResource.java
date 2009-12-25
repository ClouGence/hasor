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
package org.more.beans;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;
import org.more.DoesSupportException;
import org.more.beans.info.BeanDefinition;
/**
 * 这个接口是more.beans组建的基本接口之一，该接口用于提供{@link BeanDefinition}的索引获取功能。
 * 接口实现类决定bean数据是以什么形式存在（DBMS、LDAP、XML这些都是数据提供形式）。
 * 更甚者可以使用Spring的getMergedBeanDefinition方法使用Spring作为bean数据提供者。
 * Date : 2009-11-3
 * @author 赵永春
 */
public interface BeanResource {
    /**获取资源名。*/
    public String getSourceName();
    /**获取资源的URI表述形式，如果资源不支持该表述形式则返回null。*/
    public URI getSourceURI();
    /**获取资源的URL表述形式，如果资源不支持该表述形式则返回null。*/
    public URL getSourceURL();
    /**获取资源的File表述形式，如果资源不支持该表述形式则返回null。*/
    public File getSourceFile();
    /**
     * 获取bean的定义，在Bean的定义中只包括属于当前bean的元信息。
     * 如果当前bean的属性注入需要依赖其他bean则获取其他bean的定义需要重新调用getBeanDefinition方法进行获取。
     * @param name 要获取bean定义的bean名称。
     * @return 返回bean定义，如果获取不到指定的bean定义则返回null。
     */
    public BeanDefinition getBeanDefinition(String name);
    /**
     * 检测是否存在某个名称的Bean定义，如果存在目标bean定义则返回true，否则返回false。
     * @param name 要检测的Bean定义名称。
     * @return 返回检测结果。如果存在返回true否则返回false。
     */
    public boolean containsBeanDefinition(String name);
    /**
     * 获取{@link BeanResource}中可以索引到的所有bean定义名称集合，如果获取不到任何名称则需要返回一个空集合。
     * @return 返回获取到的所有bean定义名称集合。
     */
    public List<String> getBeanDefinitionNames();
    /**
     * 获取一个bean定义名称子集合，所有符合的bean均是要求其初始化的bean定义。
     * @return 返回一个bean定义名称子集合，所有符合的bean均是要求其初始化的bean定义。
     */
    public List<String> getStrartInitBeanDefinitionNames();
    /**
     * 获取整个资源系统环境中的属性信息，该属性作用于整个资源系统。如果接口对象不支持该方法则会引发DoesSupportException异常。
     * @param key 获取的属性KEY。
     * @return 返回获取BeanResource的属性信息。
     * @throws DoesSupportException 接口对象不支持该方法。
     */
    public Object getAttribute(String key) throws DoesSupportException;
    /**
     * 获取{@link BeanResource}接口对Bean资源的描述。
     * @return 获取{@link BeanResource}接口对Bean资源的描述。
     */
    public String getResourceDescription();
    /**
     * 获取一个boolean该值表示{@link BeanResource}对象是否对Bean的定义信息做缓存。如果实现类提供了缓存功能则返回true，否则返回false。
     * @return 获取一个boolean该值表示{@link BeanResource}对象是否对Bean的定义信息做缓存。
     */
    public boolean isCacheBeanMetadata();
    /** 
     * 清空所有缓存的Bean定义信息，如果接口实现对象不支持该方法则会引发DoesSupportException异常。
     * @throws DoesSupportException 接口对象不支持该方法。
     */
    public void clearCache() throws DoesSupportException;
    /**
     * 测试某名称Bean是否为原型模式创建，如果目标bean不存在则返回false。
     * @param name 要测试的Bean名称。
     * @return 返回测试结果，如果是以原型模式创建则返回true,否则返回false。
     */
    public boolean isPrototype(String name);
    /**
     * 测试某名称Bean是否为单态模式创建，如果目标bean不存在则返回false。
     * @param name 要测试的Bean名称。
     * @return 返回测试结果，如果是以原型模式创建则返回true,否则返回false。
     */
    public boolean isSingleton(String name);
    /**
     * 测试某名称Bean是否为工厂模式创建，如果目标bean不存在则返回false。
     * @param name 要测试的Bean名称。
     * @return 返回测试结果，如果是以原型模式创建则返回true,否则返回false。
     */
    public boolean isFactory(String name);
}
