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
package org.more.beans.resource;
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
import org.more.beans.AbstractBeanDefine;
import org.more.beans.DefineResource;
import org.more.beans.resource.namespace.NameSpaceConfiguration;
import org.more.beans.resource.namespace.beans.TagBeans_Beans;
import org.more.core.xml.XmlParserKit;
import org.more.core.xml.XmlParserKitManager;
import org.more.core.xml.stream.XmlReader;
import org.more.util.ClassPathUtil;
import org.more.util.attribute.AttBase;
/**
 * xml解析器，该类已经完成了xml解析所需要的所有功能子类需要根据特定要求注册相应的命名空间解析器。
 * 以及属性值元信息解析器即可。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class XmlConfiguration extends AttBase implements DefineResource {
    /**  */
    private static final long               serialVersionUID = -2907262416329013610L;
    private String                          sourceName       = null;
    private URI                             sourceURI        = null;
    private InputStream                     sourceStream     = null;
    //
    private ArrayList<String>               defineNames      = new ArrayList<String>();
    private Map<String, AbstractBeanDefine> defineMap        = new HashMap<String, AbstractBeanDefine>();
    private ArrayList<QuickPropertyParser>  quickParser      = new ArrayList<QuickPropertyParser>();
    private XmlParserKitManager             manager          = new XmlParserKitManager();
    //========================================================================================
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
    //========================================================================================
    /**获取一个{@link AbstractBeanDefine}定义。*/
    public AbstractBeanDefine getBeanDefine(String name) {
        return this.defineMap.get(name);
    };
    /**测试某个名称的bean定义是否存在。*/
    public boolean containsBeanDefine(String name) {
        return this.defineMap.containsKey(name);
    }
    /**添加一个Bean定义，被添加的Bean定义会被执行检测。*/
    public void addBeanDefine(AbstractBeanDefine define) {
        if (this.defineMap.containsKey(define.getName()) == true)
            throw new RepeateException("Bean定义名称[" + define.getName() + "]重复");
        this.defineMap.put(define.getName(), define);
        this.defineNames.add(define.getName());
    };
    /**使用指定的输入流解析 [jx'xi]*/
    public XmlConfiguration passerXml(InputStream in) throws XMLStreamException {
        //1.启动扫描，进行第一次解析。
        XmlReader reader = new XmlReader(in);
        this.manager.getContext().setAttribute(TagBeans_Beans.BeanDefineManager, this);
        reader.reader(this.manager, null);
        return this;
    };
    /**获取{@link XmlParserKitManager}*/
    protected XmlParserKitManager getManager() {
        return this.manager;
    }
    /**执行初始化注册。 */
    private void initRegedit(XmlConfiguration config) throws IOException, XMLStreamException {
        String resourcePath = "/META-INF/resource/beans/regedit.xml";
        List<InputStream> ins = ClassPathUtil.getResource(resourcePath);
        NameSpaceConfiguration ns = new NameSpaceConfiguration(config);
        for (InputStream is : ins)
            new XmlReader(is).reader(ns, null);
    }
    //========================================================================================
    /**注册一个快速属性值解析器。*/
    public void regeditQuickParser(QuickPropertyParser parser) {
        if (parser == null)
            throw new NullPointerException("参数不能为空.");
        if (this.quickParser.contains(parser) == false)
            this.quickParser.add(parser);
    }
    /**取消一个快速属性值解析器的注册。*/
    public void unRegeditQuickParser(QuickPropertyParser parser) {
        if (parser == null)
            throw new NullPointerException("参数不能为空.");
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
        this.passerXml(this.sourceStream);
    };
    /**清空所有注册的Bean定义*/
    public void destroy() {
        this.defineNames.clear();
        this.defineMap.clear();
    }
    //========================================================================================
    public String getSourceName() {
        return this.sourceName;
    }
    /**设置资源名*/
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
    public URI getSourceURI() {
        return this.sourceURI;
    }
    public List<String> getBeanDefineNames() {
        return this.defineNames;
    }
    public boolean isPrototype(String name) {
        if (this.containsBeanDefine(name) == false)
            throw new NoDefinitionException("找不到名称为[" + name + "]的Bean定义。");
        AbstractBeanDefine define = this.getBeanDefine(name);
        return (define.factoryName() == null) ? false : true;
    }
    public boolean isSingleton(String name) {
        if (this.containsBeanDefine(name) == false)
            throw new NoDefinitionException("找不到名称为[" + name + "]的Bean定义。");
        AbstractBeanDefine define = this.getBeanDefine(name);
        return define.isSingleton();
    }
    public boolean isFactory(String name) {
        if (this.containsBeanDefine(name) == false)
            throw new NoDefinitionException("找不到名称为[" + name + "]的Bean定义。");
        AbstractBeanDefine define = this.getBeanDefine(name);
        return (define.factoryName() == null) ? false : true;
    }
}