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
package org.more.hypha.configuration;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.more.NoDefinitionException;
import org.more.RepeateException;
import org.more.StateException;
import org.more.core.xml.XmlParserKit;
import org.more.core.xml.XmlParserKitManager;
import org.more.core.xml.stream.XmlReader;
import org.more.hypha.DefineResource;
import org.more.hypha.DefineResourcePlugin;
import org.more.hypha.EventManager;
import org.more.hypha.beans.AbstractBeanDefine;
import org.more.hypha.beans.TypeManager;
import org.more.hypha.beans.support.TagBeans_Beans;
import org.more.hypha.event.AddBeanDefineEvent;
import org.more.hypha.event.AddPluginEvent;
import org.more.hypha.event.ClearDefineEvent;
import org.more.hypha.event.LoadingDefineEvent;
import org.more.hypha.event.ReloadDefineEvent;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 该类是{@link DefineResource}的一个实现类。
 * 以及属性值元信息解析器即可。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class DefineResourceImpl implements DefineResource {
    /**  */
    private static final long                 serialVersionUID = -2907262416329013610L;
    //
    private XmlConfiguration                  configuration    = null;
    private boolean                           inited           = false;                                    //是否初始化
    private String                            sourceName       = null;                                     //资源名
    private ArrayList<String>                 pluginNames      = new ArrayList<String>();                  //插件名称集合
    private Map<String, DefineResourcePlugin> pluginList       = null;                                     //插件集合
    private ArrayList<String>                 defineNames      = new ArrayList<String>();                  //bean定义名称集合
    private Map<String, AbstractBeanDefine>   defineMap        = new HashMap<String, AbstractBeanDefine>(); //bean定义Map
    private EventManager                      eventManager     = null;                                     //事件管理器
    private IAttribute                        attributeManager = null;                                     //属性管理器
    //
    private TypeManager                       typeManager      = new TypeManager();                        //类型解析
    private XmlParserKitManager               manager          = new XmlParserKitManager();                //xml解析器
    private ClassLoader                       classLoader      = null;
    //========================================================================================构造方法
    /**私有化*/
    DefineResourceImpl(XmlConfiguration configuration) {
        this.configuration = configuration;
        this.eventManager = configuration.getEventManager();
    }
    //========================================================================================DefineResourcePluginSet接口
    /**返回扩展Define配置描述。*/
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
        this.getEventManager().doEvent(new AddPluginEvent(this, plugin));//TODO 新插件
        this.pluginNames.add(name);
        this.pluginList.put(name, plugin);
    };
    /**删除一个已有的插件注册。*/
    public synchronized void removePlugin(String name) {
        this.pluginNames.remove(name);
        this.pluginList.remove(name);
    };
    //========================================================================================
    /**获取一个{@link AbstractBeanDefine}定义。*/
    public AbstractBeanDefine getBeanDefine(String id) throws NoDefinitionException {
        if (this.defineNames.contains(id) == false)
            throw new NoDefinitionException("不存在id为[" + id + "]的Bean定义。");
        return this.defineMap.get(id);
    };
    /**测试某个id的bean定义是否存在。*/
    public boolean containsBeanDefine(String id) {
        return this.defineNames.contains(id);
    }
    /**添加一个Bean定义，被添加的Bean定义会被执行检测。*/
    public synchronized void addBeanDefine(AbstractBeanDefine define) {
        if (this.defineNames.contains(define.getID()) == true)
            throw new RepeateException("[" + define.getID() + "]Bean定义重复。");
        this.getEventManager().doEvent(new AddBeanDefineEvent(this, define));//TODO 新Bean定义，使用队列形式。
        this.defineNames.add(define.getID());
        this.defineMap.put(define.getID(), define);
    };
    /**解析配置文件流。*/
    protected DefineResourceImpl passerXml(InputStream in) throws XMLStreamException {
        XmlReader reader = new XmlReader(in);
        this.manager.getContext().setAttribute(TagBeans_Beans.BeanDefineManager, this);
        reader.reader(this.manager, null);
        return this;
    };
    /**获取{@link XmlParserKitManager}*/
    protected XmlParserKitManager getManager() {
        return this.manager;
    }
    //========================================================================================
    /**获取类型解析器*/
    public TypeManager getTypeManager() {
        return this.typeManager;
    }
    /**注册一个标签解析工具集。*/
    public void regeditXmlParserKit(String namespace, XmlParserKit kit) {
        this.manager.regeditKit(namespace, kit);
    }
    /**取消一个标签解析工具集的注册。*/
    public void unRegeditXmlParserKit(String namespace, XmlParserKit kit) {
        this.manager.unRegeditKit(namespace, kit);
    }
    /**手动执行配置装载动作，如果重复装载可能产生异常。该动作将会引发{@link LoadingDefineEvent}事件*/
    public synchronized void loadDefine() throws IOException, XMLStreamException {
        if (this.inited == true)
            throw new StateException(this.sourceName + "不能重复初始化!");
        this.configuration.loadConfig(this);
        this.inited = true;
    };
    /**清空所有装载的Bean定义对象，该方法将会引发{@link ClearDefineEvent}事件。*/
    public synchronized void clearDefine() {
        this.getEventManager().doEvent(new ClearDefineEvent(this));//TODO 销毁
        this.defineNames.clear();
        this.defineMap.clear();
        this.pluginNames.clear();
        this.pluginList.clear();
        this.inited = false;
    }
    /**重新装载配置，该方法会首先执行clearDefine()方法其次在执行loadDefine()。在执行之前该方法会引发{@link ReloadDefineEvent}事件。*/
    public synchronized void reloadDefine() throws IOException, XMLStreamException {
        this.getEventManager().doEvent(new ReloadDefineEvent(this));//TODO 重载
        this.clearDefine();
        this.loadDefine();
    }
    /**设置资源名*/
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
    public String getSourceName() {
        return this.sourceName;
    }
    /**获取{@link DefineResource}接口使用的类装载器。*/
    public ClassLoader getClassLoader() {
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
    public List<String> getBeanDefineNames() {
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
}