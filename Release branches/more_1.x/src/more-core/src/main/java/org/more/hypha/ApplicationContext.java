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
import java.util.List;
import org.more.hypha.define.BeanDefine;
/**
* 这个接口是Bean容器的核心接口，{@link ApplicationContext}的特定接口实现类可以用于某些情形。<br/><br/>
* 在{@link ApplicationContext}中所有Bean都有唯一的一个名称。该工厂将返回一个包含对象的一个独立实例(原型设计模式)，或单个
* 共享实例(Singleton设计模式，该实例是在当前工厂中的一个单态）。返回哪种类型的实例取决于bean的配置。<br/><br/>
* 处于工厂中的bean通常是存在于XML文件中。但不排除bean的来源于DBMS或者LDAP，这都取决于{@link ApplicationContext}中bean数据源接口是如何提供数据的。
* @version 2011-2-25
* @author 赵永春 (zyc@byshell.org)
*/
public interface ApplicationContext {
    /**获取应用上下文ID值。*/
    public String getID();
    /**
     * 获取{@link ApplicationContext}中可以索引到的所有bean定义名称集合，如果获取不到任何名称则需要返回一个空集合。
     * @return 返回获取到的所有bean定义名称集合。
     */
    public List<String> getBeanDefinitionIDs();
    /**
     * 获取bean的定义，在Bean的定义中只包括属于当前bean的元信息。
     * 如果当前bean的属性注入需要依赖其他bean则获取其他bean的定义需要重新调用getBeanDefinition方法进行获取。
     * @param id 要获取bean定义的bean名称。
     * @return 返回bean定义，如果获取不到指定的bean定义则返回null。
     */
    public BeanDefine getBeanDefinition(String id);
    /**
    * 获取一个类装载器，org.more.hypha中的类装载均是通过这个类装载器进行装载的。
    * @return 返回一个类装载器，org.more.hypha中的类装载均是通过这个类装载器进行装载的。
    */
    public ClassLoader getClassLoader();
    /**
     * 检测是否存在某个名称的Bean。
     * @param id 要获取的Bean id。
     * @return 返回检测结果。如果存在返回true否则返回false。
     */
    public boolean containsBean(String id);
    /**
     * 获取某个Bean的实例对象，该实例被创建时会根据其配置决定其创建是原型模式还是单态模式。 
     * 如果这个bean有属性的依赖注入则依赖注入也会在创建时进行。
     * @param id 要获取的bean实例id。
     * @return 返回或者返回创建的新实例。
     */
    public <T> T getBean(String id);
    /**
     * 获取某个Bean的实例对象，该实例被创建时会根据其配置决定其创建是原型模式还是单态模式。 
     * 如果这个bean有属性的依赖注入则依赖注入也会在创建时进行。
     * @param id 要获取的bean实例id。
     * @param objects 在获取bean实例时可能会传递的参数信息。
     * @return 返回或者返回创建的新实例。
     */
    public <T> T getBean(String id, Object... objects);
    /**
     * 根据Bean名称获取其bean类型，该方法将返回在bean定义中配置的bean类型。
     * 那么getBeanType方法将返回生成的新类类型对象。
     * @param id 要获取的Bean id。
     * @param objects 在获取bean类型时可能会传递的参数信息。
     * @return 返回要获取的bean类型对象，如果企图获取不存在的bean类型则返回 null。
     */
    public Class<?> getBeanType(String id, Object... objects);
    /**
     * 测试某名称Bean是否为原型模式创建，如果目标bean不存在则返回false。
     * @param id 要测试的Bean id。
     * @return 返回测试结果，如果是以原型模式创建则返回true,否则返回false。
     * @throws NoDefineBeanException 如果要测试的目标bean不存在则引发该异常。
     */
    public boolean isPrototype(String id) throws NoDefineBeanException;
    /**
     * 测试某名称Bean是否为单态模式创建，如果目标bean不存在则返回false。
     * @param id 要测试的Bean id。
     * @return 返回测试结果，如果是以原型模式创建则返回true,否则返回false。
     * @throws NoDefineBeanException 如果要测试的目标bean不存在则引发该异常。
     */
    public boolean isSingleton(String id) throws NoDefineBeanException;
    /**
     * 测试某名称Bean是否为工厂模式创建，如果目标bean不存在则返回false。
     * @param id 要测试的Bean id。
     * @return 返回测试结果，如果是以原型模式创建则返回true,否则返回false。
     * @throws NoDefineBeanException 如果要测试的目标bean不存在则引发该异常。
     */
    public boolean isFactory(String id) throws NoDefineBeanException;
    /**
     * 测试指定id的bean类型是否可以转换成为targetType参数表示的类型。如果可以则返回true，否则返回false。
     * @param id 要测试的Bean id。
     * @param targetType 要测试的类型名。
     * @return 返回测试结果，如果指定的类型是被测试的bean的父类则返回true,否则返回false。
     */
    public boolean isTypeMatch(String id, Class<?> targetType);
    /**初始化{@link ApplicationContext}接口，该方法一定要在destroy之前执行。 */
    public void init();
    /**销毁{@link ApplicationContext}接口。*/
    public void destroy();
    /*------------------------------------------------------------------------------*/
    /**获取应用的上下文环境对象。*/
    public Object getContextObject();
    /**设置应用的上下文环境对象。*/
    public void setContextObject(Object contextObject);
};