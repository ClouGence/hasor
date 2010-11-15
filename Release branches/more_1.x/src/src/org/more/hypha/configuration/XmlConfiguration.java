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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.more.core.xml.XmlParserKit;
import org.more.core.xml.XmlParserKitManager;
import org.more.core.xml.stream.XmlReader;
import org.more.hypha.ApplicationContext;
import org.more.hypha.DefineResource;
import org.more.hypha.beans.TypeManager;
import org.more.hypha.beans.support.TagBeans_Beans;
import org.more.hypha.event.Config_BeginBuildEvent;
import org.more.hypha.event.Config_EndBuildEvent;
import org.more.hypha.event.Config_LoadedXmlEvent;
import org.more.hypha.event.Config_LoadingXmlEvent;
import org.more.util.ClassPathUtil;
/**
 * 该类是用于生成{@link DefineResource}接口的类。该类的职责是收集任何可以解析的配置数据源，
 * 当调用build方法时进入构建，该类会根据不同的配置格式来将其转换为流在读取。
 * 以及属性值元信息解析器即可。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class XmlConfiguration {
    /**  */
    private static final long   serialVersionUID = -2907262416329013610L;
    private static final String ResourcePath     = "/META-INF/resource/hypha/register.xml";
    private ArrayList<Object>   sourceArray      = new ArrayList<Object>();
    private TypeManager         typeManager      = new TypeManager();                      //类型解析
    private XmlParserKitManager manager          = new XmlParserKitManager();              //xml解析器
    //========================================================================================构造方法
    /**创建{@link XmlConfiguration}对象。*/
    public XmlConfiguration() {}
    /**创建{@link XmlConfiguration}对象，sourceFile是要装载的配置文件，该路径应当相当于classpath。*/
    public XmlConfiguration(String sourceFile) {
        this.addSource(sourceFile);
    }
    /**创建{@link XmlConfiguration}对象，sourceFiles是要装载的配置文件，该路径应当相当于classpath。*/
    public XmlConfiguration(String[] sourceFiles) {
        for (String sf : sourceFiles)
            this.addSource(sf);
    }
    /**创建{@link XmlConfiguration}对象，sourceURL是要装载的配置文件。*/
    public XmlConfiguration(URL sourceURL) {
        this.addSource(sourceURL);
    }
    /**创建{@link XmlConfiguration}对象，sourceURLs是要装载的配置文件。*/
    public XmlConfiguration(URL[] sourceURLs) {
        for (URL url : sourceURLs)
            this.addSource(url);
    }
    /**创建{@link XmlConfiguration}对象，sourceURI是要装载的配置文件。*/
    public XmlConfiguration(URI sourceURI) {
        this.addSource(sourceURI);
    }
    /**创建{@link XmlConfiguration}对象，sourceURIs是要装载的配置文件。*/
    public XmlConfiguration(URI[] sourceURIs) {
        for (URI uri : sourceURIs)
            this.addSource(uri);
    }
    /**创建{@link XmlConfiguration}对象，sourceFile是要装载的配置文件。*/
    public XmlConfiguration(File sourceFile) {
        this.addSource(sourceFile);
    }
    /**创建{@link XmlConfiguration}对象，sourceFiles是要装载的配置文件。*/
    public XmlConfiguration(File[] sourceFiles) {
        for (File file : sourceFiles)
            this.addSource(file);
    }
    /**创建{@link XmlConfiguration}对象，sourceStream是要装载的配置文件流。*/
    public XmlConfiguration(InputStream sourceStream) {
        this.addSource(sourceStream);
    }
    /**创建{@link XmlConfiguration}对象，sourceStream是要装载的配置文件流。*/
    public XmlConfiguration(InputStream[] sourceStreams) {
        for (InputStream is : sourceStreams)
            this.addSource(is);
    }
    //========================================================================================
    private void addSourceArray(Object source) {
        if (source == null)
            throw new NullPointerException("参数为空");
        if (this.sourceArray.contains(source) == false)
            this.sourceArray.add(source);
    }
    /**添加资源。*/
    public void addSource(InputStream stream) {
        this.addSourceArray(stream);
    }
    /**添加资源。*/
    public void addSource(URI uri) {
        this.addSourceArray(uri);
    }
    /**添加资源。*/
    public void addSource(URL url) {
        this.addSourceArray(url);
    }
    /**添加资源。*/
    public void addSource(File file) {
        this.addSourceArray(file);
    }
    /**添加资源，该资源的存放路径是相对于classpath。*/
    public void addSource(String source) {
        this.addSourceArray(source);
    }
    //========================================================================================
    /**解析配置文件流。*/
    protected void passerXml(InputStream in, DefineResource conf) throws XMLStreamException {
        XmlReader reader = new XmlReader(in);
        this.manager.getContext().setAttribute(TagBeans_Beans.BeanDefineManager, this);
        reader.reader(this.manager, null);
    };
    /**获取{@link XmlParserKitManager}*/
    protected XmlParserKitManager getManager() {
        return this.manager;
    }
    /**注册一个标签解析工具集。*/
    public void regeditXmlParserKit(String namespace, XmlParserKit kit) {
        this.manager.regeditKit(namespace, kit);
    }
    /**取消一个标签解析工具集的注册。*/
    public void unRegeditXmlParserKit(String namespace, XmlParserKit kit) {
        this.manager.unRegeditKit(namespace, kit);
    }
    //========================================================================================
    public ApplicationContext buildApp(Object context) throws Throwable {
        return null;//TODO
    }
    /**生成{@link DefineResourceImpl}对象。注意：构建过程包含了配置文件装载。*/
    public DefineResource build(String sourceName, ClassLoader loader) throws Throwable {
        //1.创建
        DefineResourceImpl conf = new DefineResourceImpl(this);
        conf.getEventManager().doEvent(new Config_BeginBuildEvent(this, conf));//开始构建
        if (loader == null)
            loader = ClassLoader.getSystemClassLoader();
        conf.setClassLoader(loader);
        conf.setSourceName(sourceName);
        //2.初始化
        List<InputStream> ins = ClassPathUtil.getResource(ResourcePath);
        NameSpaceConfiguration ns = new NameSpaceConfiguration();
        for (InputStream is : ins)
            new XmlReader(is).reader(ns, null);
        List<NameSpaceRegister> registers = ns.getRegister();
        for (NameSpaceRegister reg : registers)
            /**第一个参数会在{@link NameSpaceRegisterPropxy}对象中得到*/
            reg.initRegister(null, this, conf);
        //
        conf.loadDefine();
        conf.getEventManager().doEvent(new Config_EndBuildEvent(this, conf));//结束构建
        return conf;
    }
    /**使用当前的配置信息装载{@link DefineResource}对象。*/
    public synchronized DefineResource loadConfig(DefineResource conf) throws IOException, XMLStreamException {
        conf.getEventManager().doEvent(new Config_LoadingXmlEvent(this, conf));//开始装载Beans
        for (Object obj : this.sourceArray)
            if (obj instanceof InputStream) {
                InputStream is = (InputStream) obj;
                try {
                    //注意这里有一个试图重置输入流的尝试
                    is.reset();
                } catch (Exception e) {}
                this.passerXml(is, conf);
            } else if (obj instanceof URL) {
                InputStream is = ((URL) obj).openStream();
                this.passerXml(is, conf);
                is.close();
            } else if (obj instanceof URI) {
                InputStream is = ((URI) obj).toURL().openStream();
                this.passerXml(is, conf);
                is.close();
            } else if (obj instanceof File) {
                FileInputStream is = new FileInputStream((File) obj);
                this.passerXml(is, conf);
                is.close();
            } else if (obj instanceof String) {
                List<InputStream> xmlINS = ClassPathUtil.getResource((String) obj);
                for (InputStream is : xmlINS) {
                    this.passerXml(is, conf);
                    is.close();
                }
            }
        conf.getEventManager().doEvent(new Config_LoadedXmlEvent(this, conf));//装载Beans结束
        return conf;
    }
    /**获取类型解析器*/
    public TypeManager getTypeManager() {
        return this.typeManager;
    }
}