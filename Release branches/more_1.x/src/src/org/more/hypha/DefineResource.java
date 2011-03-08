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
import org.more.NoDefinitionException;
import org.more.hypha.beans.AbstractBeanDefine;
import org.more.hypha.event.AddBeanDefineEvent;
import org.more.hypha.event.ClearDefineEvent;
import org.more.util.attribute.IAttribute;
/**
 * 这个接口是more.hypha组建的基本接口之一，该接口用于提供{@link AbstractBeanDefine}的索引获取功能。
 * 接口实现类决定bean数据是以什么形式存在（DBMS、LDAP、XML这些都是数据提供形式）。
 * @version 2010-9-24
 * @author 赵永春 (zyc@byshell.org)
 */
public interface DefineResource extends PluginSet<DefineResource> {
    /**获取一个状态该状态表述是否已经准备好。*/
    public boolean isReady();
    /**获取{@link DefineResource}的属性访问接口。*/
    public IAttribute getAttribute();
    /**获取资源名。*/
    public String getSourceName();
    /**
     * 获取bean的定义，在Bean的定义中只包括属于当前bean的元信息。
     * @param id 要获取bean定义的id。
     * @return 返回bean定义，如果获取不到指定的bean定义则返回null。
     */
    public AbstractBeanDefine getBeanDefine(String id) throws NoDefinitionException;
    /**
     * 添加一个bean定义。使用该方法添加的定义在{@link DefineResource}接口重载时会丢失。
     * 请注意该方法将会引发{@link AddBeanDefineEvent}事件。
     * @param define 要添加的bean定义。
     */
    public void addBeanDefine(AbstractBeanDefine define) throws NoDefinitionException;
    /**
     * 检测是否存在某个名称的Bean定义，如果存在目标bean定义则返回true，否则返回false。
     * @param id 要检测的Bean定义id。
     * @return 返回检测结果。如果存在返回true否则返回false。
     */
    public boolean containsBeanDefine(String id);
    /**
     * 获取{@link DefineResource}中可以索引到的所有bean定义名称集合，
     * 如果获取不到任何名称则需要返回一个空集合。返回的集合是一个只读集合。
     * @return 返回获取到的所有bean定义名称集合。
     */
    public List<String> getBeanDefinitionIDs();
    /**
     * 测试某名称Bean是否为原型模式创建，原型模式是指bean即不属于工厂方式创建也没有配置单态特性。
     * @param id 要测试的Bean id。
     * @return 返回测试结果，如果是以原型模式创建则返回true,否则返回false。
     */
    public boolean isPrototype(String id) throws NoDefinitionException;
    /**
     * 测试某名称Bean是否为单态模式创建，如果目标bean不存在则会引发{@link NoDefinitionException}异常。
     * @param id 要测试的Bean id。
     * @return 返回测试结果，如果是以原型模式创建则返回true,否则返回false。
     */
    public boolean isSingleton(String id) throws NoDefinitionException;
    /**
     * 测试某名称Bean是否为工厂模式创建，如果目标bean不存在则会引发{@link NoDefinitionException}异常。
     * @param id 要测试的Bean id。
     * @return 返回测试结果，如果是以原型模式创建则返回true,否则返回false。
     */
    public boolean isFactory(String id) throws NoDefinitionException;
    /**清空所有装载的Bean定义对象，该方法将会引发{@link ClearDefineEvent}事件。*/
    public void clearDefine();
    /**获取事件管理器，通过该管理器可以发送事件，事件的监听也是通过这个接口对象完成的。*/
    public AbstractEventManager getEventManager();
    /**获取扩展点管理器，通过扩展点管理器可以检索、注册或者解除注册扩展点。有关扩展点的功能请参见{@link ExpandPoint}*/
    public AbstractExpandPointManager getExpandPointManager();
};