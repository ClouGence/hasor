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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.more.NoDefinitionException;
import org.more.RepeateException;
import org.more.core.xml.XmlParserKit;
import org.more.core.xml.XmlParserKitManager;
import org.more.core.xml.stream.XmlReader;
import org.more.hypha.AbstractEventManager;
import org.more.hypha.DefineResource;
import org.more.hypha.DefineResourcePlugin;
import org.more.hypha.EventManager;
import org.more.hypha.beans.AbstractBeanDefine;
import org.more.hypha.beans.support.QuickPropertyParser;
import org.more.hypha.beans.support.TagBeans_Beans;
import org.more.hypha.event.AddBeanDefineEvent;
import org.more.hypha.event.AddPluginEvent;
import org.more.hypha.event.BeginInitEvent;
import org.more.hypha.event.DestroyEvent;
import org.more.hypha.event.EndInitEvent;
import org.more.hypha.event.ReloadEvent;
import org.more.util.ClassPathUtil;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * xml解析器，该类已经完成了xml解析所需要的所有功能子类需要根据特定要求注册相应的命名空间解析器。
 * 以及属性值元信息解析器即可。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class XmlConfiguration implements DefineResource {
    /**  */
    private static final long                 serialVersionUID = -2907262416329013610L;
    //
    private static final String               ResourcePath     = "/META-INF/resource/hypha/register.xml";  //
    //
    private String                            sourceName       = null;                                     //资源名
    private URI                               sourceURI        = null;                                     //xml URI描述
    private InputStream                       sourceStream     = null;                                     //xml 输入流
    private Map<String, DefineResourcePlugin> pluginList       = null;                                     //插件集合
    private Map<String, AbstractBeanDefine>   defineMap        = new HashMap<String, AbstractBeanDefine>(); //bean定义Map
    private EventManager                      eventManager     = new AbstractEventManager() {};            //事件管理器
    private IAttribute                        attributeManager = null;                                     //属性管理器
    //
    private ArrayList<QuickPropertyParser>    quickParser      = new ArrayList<QuickPropertyParser>();     //属性快速解析器定义
    private XmlParserKitManager               manager          = new XmlParserKitManager();                //xml解析器
    //========================================================================================构造方法
    /**创建{@link XmlConfiguration}对象，init过程需要手动进行。*/
    public XmlConfiguration() throws IOException, XMLStreamException {
        this.initRegedit(this);
    }
    /**创建{@link XmlConfiguration}对象，sourceFile是要装载的配置文件。*/
    public XmlConfiguration(String sourceFile) throws IOException, XMLStreamException {
        this(new File(sourceFile));
    }
    /**创建{@link XmlConfiguration}对象，sourceURI是要装载的配置文件。*/
    public XmlConfiguration(URI sourceURI) throws IOException, XMLStreamException {
        this.initRegedit(this);
        this.sourceURI = sourceURI;
        this.init();
    }
    /**创建{@link XmlConfiguration}对象，sourceFile是要装载的配置文件。*/
    public XmlConfiguration(File sourceFile) throws IOException, XMLStreamException {
        this.initRegedit(this);
        this.sourceURI = sourceFile.toURI();
        this.init();
    }
    /**创建{@link XmlConfiguration}对象，sourceStream是要装载的配置文件流。*/
    public XmlConfiguration(InputStream sourceStream) throws IOException, XMLStreamException {
        this.initRegedit(this);
        this.sourceStream = sourceStream;
        this.init();
    }
    /**执行初始化注册。 */
    private void initRegedit(XmlConfiguration config) throws IOException, XMLStreamException {
        List<InputStream> ins = ClassPathUtil.getResource(ResourcePath);
        NameSpaceConfiguration ns = new NameSpaceConfiguration(config);
        for (InputStream is : ins)
            new XmlReader(is).reader(ns, null);
    }
    //========================================================================================DefineResourcePluginSet接口
    /**返回扩展Define配置描述。*/
    public DefineResourcePlugin getPlugin(String name) {
        if (this.pluginList == null)
            return null;
        return this.pluginList.get(name);
    };
    /**设置一个插件，如果插件重名则替换重名的插件注册。*/
    public void setPlugin(String name, DefineResourcePlugin plugin) {
        if (this.pluginList == null)
            this.pluginList = new HashMap<String, DefineResourcePlugin>();
        this.getEventManager().pushEvent(new AddPluginEvent(this, plugin));//TODO 新插件
        this.pluginList.put(name, plugin);
    };
    /**删除一个已有的插件注册。*/
    public void removePlugin(String name) {
        this.pluginList.remove(name);
    };
    //========================================================================================
    /**获取一个{@link AbstractBeanDefine}定义。*/
    public AbstractBeanDefine getBeanDefine(String name) throws NoDefinitionException {
        if (this.defineMap.containsKey(name) == false)
            throw new NoDefinitionException("不存在名称为[" + name + "]的Bean定义。");
        return this.defineMap.get(name);
    };
    /**测试某个名称的bean定义是否存在。*/
    public boolean containsBeanDefine(String name) {
        return this.defineMap.containsKey(name);
    }
    /**添加一个Bean定义，被添加的Bean定义会被执行检测。*/
    public void addBeanDefine(AbstractBeanDefine define) {
        this.getEventManager().pushEvent(new AddBeanDefineEvent(this, define));//TODO 新Bean定义
        if (this.defineMap.containsKey(define.getName()) == true)
            throw new RepeateException("[" + define.getName() + "]Bean定义重复。");
        this.defineMap.put(define.getID(), define);
    };
    /**使用指定的输入流解析*/
    private XmlConfiguration passerXml(InputStream in) throws XMLStreamException {
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
    /**注册一个快速属性值解析器。*/
    public void regeditQuickParser(QuickPropertyParser parser) {
        if (parser == null)
            throw new NullPointerException("QuickPropertyParser类型参数不能为空。");
        if (this.quickParser.contains(parser) == false)
            this.quickParser.add(parser);
    }
    /**取消一个快速属性值解析器的注册。*/
    public void unRegeditQuickParser(QuickPropertyParser parser) {
        if (parser == null)
            throw new NullPointerException("QuickPropertyParser类型参数不能为空。");
        if (this.quickParser.contains(parser) == true)
            this.quickParser.remove(parser);
    }
    /**获取注册的{@link QuickPropertyParser}集合*/
    public List<QuickPropertyParser> getQuickList() {
        return this.quickParser;
    }
    /**注册一个标签解析工具集。*/
    public void regeditXmlParserKit(String namespace, XmlParserKit kit) {
        this.manager.regeditKit(namespace, kit);
    }
    /**取消一个标签解析工具集的注册。*/
    public void unRegeditXmlParserKit(String namespace, XmlParserKit kit) {
        this.manager.unRegeditKit(namespace, kit);
    }
    /**重载{@link XmlConfiguration}如果创建{@link XmlConfiguration}时使用的是流方式那么流需要支持reset否则会引发异常。*/
    public void reload() throws XMLStreamException, MalformedURLException, IOException {
        this.getEventManager().pushEvent(new ReloadEvent(this));//TODO 重载
        this.destroy();
        this.init();
    }
    /**重置输入流，并且重新初始化{@link XmlConfiguration}对象。*/
    public void init() throws XMLStreamException, MalformedURLException, IOException {
        if (this.sourceStream == null)
            this.sourceStream = this.sourceURI.toURL().openStream();
        try {
            this.sourceStream.reset();
        } catch (Exception e) {}
        this.getEventManager().pushEvent(new BeginInitEvent(this));//TODO 开始初始化
        this.getEventManager().popEvent(BeginInitEvent.class);
        this.passerXml(this.sourceStream);
        this.getEventManager().pushEvent(new EndInitEvent(this));//TODO 结束初始化
        this.getEventManager().popEvent(EndInitEvent.class);
        //
        this.getEventManager().popEvent();
    };
    /**清空所有注册的Bean定义*/
    public void destroy() {
        this.getEventManager().pushEvent(new DestroyEvent(this));//TODO 销毁
        this.defineMap.clear();
    }
    /**设置资源名*/
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
    public String getSourceName() {
        return this.sourceName;
    }
    public IAttribute getAttribute() {
        if (this.attributeManager == null)
            this.attributeManager = new AttBase();
        return this.attributeManager;
    }
    public EventManager getEventManager() {
        return this.eventManager;
    }
    public URI getSourceURI() {
        return this.sourceURI;
    }
    public List<String> getBeanDefineNames() {
        return new ArrayList<String>(this.defineMap.keySet());
    }
    public boolean isPrototype(String name) throws NoDefinitionException {
        AbstractBeanDefine define = this.getBeanDefine(name);
        return (define.factoryName() == null) ? false : true;
    }
    public boolean isSingleton(String name) throws NoDefinitionException {
        AbstractBeanDefine define = this.getBeanDefine(name);
        return define.isSingleton();
    }
    public boolean isFactory(String name) throws NoDefinitionException {
        AbstractBeanDefine define = this.getBeanDefine(name);
        return (define.factoryName() == null) ? false : true;
    }
}