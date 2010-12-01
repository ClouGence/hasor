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
package org.more.hypha.context;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.NoDefinitionException;
import org.more.RepeateException;
import org.more.hypha.AbstractEventManager;
import org.more.hypha.ApplicationContext;
import org.more.hypha.DefineResource;
import org.more.hypha.DefineResourcePlugin;
import org.more.hypha.EventManager;
import org.more.hypha.beans.AbstractBeanDefine;
import org.more.hypha.event.AddBeanDefineEvent;
import org.more.hypha.event.AddPluginEvent;
import org.more.hypha.event.ClearDefineEvent;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 数组{@link DefineResource}接口实现类，{@link ArrayDefineResource}类将所有Bean定义数据都存储在内存中。
 * @version 2010-11-30
 * @author 赵永春 (zyc@byshell.org)
 */
public class ArrayDefineResource implements DefineResource {
    private String                            sourceName       = null;                                     //资源名
    private ArrayList<String>                 pluginNames      = new ArrayList<String>();                  //插件名称集合
    private Map<String, DefineResourcePlugin> pluginList       = null;                                     //插件集合
    private ArrayList<String>                 defineNames      = new ArrayList<String>();                  //bean定义名称集合
    private Map<String, AbstractBeanDefine>   defineMap        = new HashMap<String, AbstractBeanDefine>(); //bean定义Map
    //
    private EventManager                      eventManager     = new AbstractEventManager() {};            //事件管理器
    private IAttribute                        attributeManager = null;                                     //属性管理器
    private ClassLoader                       classLoader      = null;
    //========================================================================================DefineResourcePluginSet接口
    /**根据扩展名获取扩展目标对象。*/
    public DefineResourcePlugin getPlugin(String name) {
        if (this.pluginList == null)
            return null;
        if (this.pluginNames.contains(name) == false)
            return null;
        return this.pluginList.get(name);
    };
    /**设置一个插件，如果插件重名则替换重名的插件注册。*/
    public synchronized void setPlugin(String name, DefineResourcePlugin plugin) {
        if (this.pluginList == null)
            this.pluginList = new HashMap<String, DefineResourcePlugin>();
        this.getEventManager().doEvent(new AddPluginEvent(this, plugin));//新插件
        this.pluginNames.add(name);
        this.pluginList.put(name, plugin);
    };
    /**删除一个已有的插件注册。*/
    public synchronized void removePlugin(String name) {
        this.pluginNames.remove(name);
        this.pluginList.remove(name);
    };
    /**获取已注册插件的名称集合。*/
    public List<String> getPluginNames() {
        return Collections.unmodifiableList((List<String>) this.pluginNames);
    };
    /**清理掉所有插件的注册。*/
    protected synchronized void clearPlugin() {
        this.pluginList.clear();
        this.pluginNames.clear();
    }
    //========================================================================================
    /**获取一个状态该状态表述是否已经准备好，{@link ArrayDefineResource}类型中该方法始终返回true。*/
    public boolean isReady() {
        return true;
    };
    public ClassLoader getClassLoader() {
        if (this.classLoader == null)
            return ClassLoader.getSystemClassLoader();
        return this.classLoader;
    };
    /**设置ClassLoader，通常在初始化之前进行设置。*/
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    public IAttribute getAttribute() {
        if (this.attributeManager == null)
            this.attributeManager = new AttBase();
        return this.attributeManager;
    }
    public EventManager getEventManager() {
        return this.eventManager;
    }
    /**设置资源名。*/
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
    public String getSourceName() {
        return this.sourceName;
    }
    public AbstractBeanDefine getBeanDefine(String id) throws NoDefinitionException {
        if (this.defineNames.contains(id) == false)
            throw new NoDefinitionException("不存在id为[" + id + "]的Bean定义。");
        return this.defineMap.get(id);
    };
    public synchronized void addBeanDefine(AbstractBeanDefine define) throws NoDefinitionException {
        if (this.defineNames.contains(define.getID()) == true)
            throw new RepeateException("[" + define.getID() + "]Bean定义重复。");
        this.getEventManager().doEvent(new AddBeanDefineEvent(this, define));//新Bean定义，使用队列形式。
        this.defineNames.add(define.getID());
        this.defineMap.put(define.getID(), define);
    };
    public boolean containsBeanDefine(String id) {
        return this.defineNames.contains(id);
    };
    public synchronized List<String> getBeanDefineNames() {
        return Collections.unmodifiableList((List<String>) this.defineNames);
    }
    public boolean isPrototype(String id) throws NoDefinitionException {
        AbstractBeanDefine define = this.getBeanDefine(id);
        return (define.factoryName() == null) ? false : true;
    }
    public boolean isSingleton(String id) throws NoDefinitionException {
        AbstractBeanDefine define = this.getBeanDefine(id);
        return define.isSingleton();
    }
    public boolean isFactory(String id) throws NoDefinitionException {
        AbstractBeanDefine define = this.getBeanDefine(id);
        return (define.factoryName() == null) ? false : true;
    }
    public synchronized void clearDefine() {
        this.getEventManager().doEvent(new ClearDefineEvent(this));//销毁
        this.defineNames.clear();
        this.defineMap.clear();
    }
    public ApplicationContext buildApp(Object context) {
        return new HyphaApplicationContext(this, context);
    };
}