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
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.AbstractEventManager;
import org.more.hypha.AbstractExpandPointManager;
import org.more.hypha.ApplicationContext;
import org.more.hypha.DefineResource;
import org.more.hypha.Plugin;
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
    private String                              sourceName         = null;                                     //资源名
    private ArrayList<String>                   pluginNames        = new ArrayList<String>();                  //插件名称集合
    private Map<String, Plugin<DefineResource>> pluginList         = null;                                     //插件集合
    private ArrayList<String>                   defineNames        = new ArrayList<String>();                  //bean定义名称集合
    private Map<String, AbstractBeanDefine>     defineMap          = new HashMap<String, AbstractBeanDefine>(); //bean定义Map
    //
    //以下字段都可以通过重写相应方法达到重写的目的。
    private AbstractEventManager                eventManager       = null;                                     //事件管理器
    private AbstractExpandPointManager          expandPointManager = null;                                     //扩展点管理器
    private IAttribute                          attributeManager   = null;                                     //属性管理器
    /**全局闪存，通过重写受保护的方法createFlash来达到植入的目的。*/
    private IAttribute                          flashContext       = null;
    //========================================================================================DefineResourcePluginSet接口
    /**根据扩展名获取扩展目标对象。*/
    public Plugin<DefineResource> getPlugin(String name) {
        if (this.pluginList == null)
            return null;
        if (this.pluginNames.contains(name) == false)
            return null;
        return this.pluginList.get(name);
    };
    /**设置一个插件，如果插件重名则替换重名的插件注册。*/
    public synchronized void setPlugin(String name, Plugin<DefineResource> plugin) {
        if (this.pluginList == null)
            this.pluginList = new HashMap<String, Plugin<DefineResource>>();
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
    /**获取DefineResource的属性访问接口。子类可以通过重写该方法来改变属性管理器。*/
    public IAttribute getAttribute() {
        if (this.attributeManager == null)
            this.attributeManager = new AttBase();
        return this.attributeManager;
    }
    /**获取事件管理器，通过该管理器可以发送事件，事件的监听也是通过这个接口对象完成的。子类可以通过重写该方法来改变事件管理器。*/
    public AbstractEventManager getEventManager() {
        if (this.eventManager == null)
            this.eventManager = new AbstractEventManager() {};
        return this.eventManager;
    }
    /**获取扩展点管理器，通过扩展点管理器可以检索、注册或者解除注册扩展点。有关扩展点的功能请参见{@link ExpandPoint}。子类可以通过重写该方法来改变扩展点管理器。*/
    public AbstractExpandPointManager getExpandPointManager() {
        if (this.expandPointManager == null)
            this.expandPointManager = new AbstractExpandPointManager() {};
        return this.expandPointManager;
    }
    /**获取一个状态该状态表述是否已经准备好，{@link ArrayDefineResource}类型中该方法始终返回true。*/
    public boolean isReady() {
        return true;
    };
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
    public synchronized List<String> getBeanDefinitionIDs() {
        return Collections.unmodifiableList((List<String>) this.defineNames);
    }
    public boolean isPrototype(String id) throws NoDefinitionException {
        AbstractBeanDefine define = this.getBeanDefine(id);
        if (define.factoryMethod() == null && define.isSingleton() == false)
            return true;
        else
            return false;
    }
    public boolean isSingleton(String id) throws NoDefinitionException {
        AbstractBeanDefine define = this.getBeanDefine(id);
        return define.isSingleton();
    }
    public boolean isFactory(String id) throws NoDefinitionException {
        AbstractBeanDefine define = this.getBeanDefine(id);
        return (define.factoryMethod() == null) ? false : true;
    }
    public synchronized void clearDefine() {
        this.getEventManager().doEvent(new ClearDefineEvent(this));//销毁
        this.defineNames.clear();
        this.defineMap.clear();
    }
    /**获取Flash，这个flash是一个内部信息携带体。它可以贯穿整个hypha的所有阶段。得到flash有两种办法一种是主动获取。另外一种是在特定的位置由hypha提供。*/
    protected final IAttribute getFlash() {
        synchronized (this) {
            if (this.flashContext == null)
                this.flashContext = this.createFlash();
            if (this.flashContext == null)
                this.flashContext = new AttBase();
        }
        return this.flashContext;
    };
    /**创建一个用于贯穿整个hypha的flash。getFlash方法会企图从该方法中创建，本方法没有成功创建flash将会采用默认的方式创建flash。*/
    protected IAttribute createFlash() {
        return null;
    };
    public final synchronized ApplicationContext buildApp(Object context) throws Throwable {
        ApplicationContext appContext = this.createApplicationContext(context);
        appContext.init();
        return appContext;
    };
    /**该方法是由buildApp方法直接调用。用于确定子类使用何种类型的ApplicationContext实现。*/
    protected ApplicationContext createApplicationContext(final Object context) {
        return new HyphaApplicationContext(this, context) {
            protected IAttribute getFlash() {
                return ArrayDefineResource.this.getFlash();/*统一FLASH缓存*/
            }
            protected IAttribute getAttribute() {
                return ArrayDefineResource.this.getAttribute();/*统一属性管理器*/
            }
        };
    };
};