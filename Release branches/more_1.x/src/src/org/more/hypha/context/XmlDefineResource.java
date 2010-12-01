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
import org.more.hypha.DefineResource;
import org.more.hypha.beans.TypeManager;
import org.more.hypha.beans.support.TagBeans_Beans;
import org.more.hypha.event.Config_LoadedXmlEvent;
import org.more.hypha.event.Config_LoadingXmlEvent;
import org.more.hypha.event.ReloadDefineEvent;
import org.more.util.ClassPathUtil;
/**
 * 
 * @version 2010-11-30
 * @author 赵永春 (zyc@byshell.org)
 */
public class XmlDefineResource extends ArrayDefineResource {
    private static final String               ResourcePath = "/META-INF/resource/hypha/register.xml";
    private ArrayList<Object>                 sourceArray  = new ArrayList<Object>();
    private TypeManager                       typeManager  = new TypeManager();                      //类型解析
    private XmlParserKitManager               manager      = new XmlParserKitManager();              //xml解析器
    //
    //========================================================================================静态方法
    private static List<XmlNameSpaceRegister> registers    = null;
    private static synchronized void r_s_init(XmlDefineResource resource) throws Throwable {
        XmlDefineResource.registers = null;
        XmlDefineResource.s_init(resource);
    }
    private static synchronized void s_init(XmlDefineResource resource) throws Throwable {
        //关键-初始化
        if (XmlDefineResource.registers == null) {
            List<InputStream> ins = ClassPathUtil.getResource(ResourcePath);
            _NameSpaceConfiguration ns = new _NameSpaceConfiguration();
            for (InputStream is : ins)
                new XmlReader(is).reader(ns, null);
            XmlDefineResource.registers = ns.getRegister();
        }
        for (XmlNameSpaceRegister reg : registers)
            /**第一个参数会在{@link NameSpaceRegisterPropxy}对象中得到*/
            reg.initRegister(null, resource);
    }
    /**创建{@link XmlDefineResource}对象，该方法将不会重新扫描ClassPath中的命名空间注册。*/
    public static XmlDefineResource newInstance() throws Throwable {
        return new XmlDefineResource(false);
    };
    /**创建{@link XmlDefineResource}对象，该方法将导致重新扫描ClassPath中的命名空间注册。*/
    public static XmlDefineResource newInstanceByNew() throws Throwable {
        return new XmlDefineResource(true);
    };
    //========================================================================================构造方法
    /**创建{@link XmlDefineResource}对象，参数sinit表明是否重新装载命名空间解析。*/
    private XmlDefineResource(boolean sinit) throws Throwable {
        if (sinit == true)
            r_s_init(this);
        else
            s_init(this);
    }
    /**创建{@link XmlDefineResource}对象。*/
    public XmlDefineResource() throws Throwable {
        this(false);//不重新装载命名空间注册。
    }
    /**创建{@link XmlDefineResource}对象，sourceFile是要装载的配置文件，该路径应当相当于classpath。*/
    public XmlDefineResource(String sourceFile) throws Throwable {
        this();
        this.addSource(sourceFile);
    }
    /**创建{@link XmlDefineResource}对象，sourceFiles是要装载的配置文件，该路径应当相当于classpath。*/
    public XmlDefineResource(String[] sourceFiles) throws Throwable {
        this();
        for (String sf : sourceFiles)
            this.addSource(sf);
    }
    /**创建{@link XmlDefineResource}对象，sourceURL是要装载的配置文件。*/
    public XmlDefineResource(URL sourceURL) throws Throwable {
        this();
        this.addSource(sourceURL);
    }
    /**创建{@link XmlDefineResource}对象，sourceURLs是要装载的配置文件。*/
    public XmlDefineResource(URL[] sourceURLs) throws Throwable {
        this();
        for (URL url : sourceURLs)
            this.addSource(url);
    }
    /**创建{@link XmlDefineResource}对象，sourceURI是要装载的配置文件。*/
    public XmlDefineResource(URI sourceURI) throws Throwable {
        this();
        this.addSource(sourceURI);
    }
    /**创建{@link XmlDefineResource}对象，sourceURIs是要装载的配置文件。*/
    public XmlDefineResource(URI[] sourceURIs) throws Throwable {
        this();
        for (URI uri : sourceURIs)
            this.addSource(uri);
    }
    /**创建{@link XmlDefineResource}对象，sourceFile是要装载的配置文件。*/
    public XmlDefineResource(File sourceFile) throws Throwable {
        this();
        this.addSource(sourceFile);
    }
    /**创建{@link XmlDefineResource}对象，sourceFiles是要装载的配置文件。*/
    public XmlDefineResource(File[] sourceFiles) throws Throwable {
        this();
        for (File file : sourceFiles)
            this.addSource(file);
    }
    /**创建{@link XmlDefineResource}对象，sourceStream是要装载的配置文件流。*/
    public XmlDefineResource(InputStream sourceStream) throws Throwable {
        this();
        this.addSource(sourceStream);
    }
    /**创建{@link XmlDefineResource}对象，sourceStream是要装载的配置文件流。*/
    public XmlDefineResource(InputStream[] sourceStreams) throws Throwable {
        this();
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
    /**获取{@link XmlParserKitManager}*/
    protected XmlParserKitManager getManager() {
        return this.manager;
    };
    /**获取类型解析器*/
    public TypeManager getTypeManager() {
        return this.typeManager;
    };
    /**注册一个标签解析工具集。*/
    public synchronized void regeditXmlParserKit(String namespace, XmlParserKit kit) {
        this.manager.regeditKit(namespace, kit);
    };
    /**取消一个标签解析工具集的注册。*/
    public synchronized void unRegeditXmlParserKit(String namespace, XmlParserKit kit) {
        this.manager.unRegeditKit(namespace, kit);
    };
    /**解析配置文件流。*/
    protected synchronized void passerXml(InputStream in, DefineResource conf) throws XMLStreamException {
        XmlReader reader = new XmlReader(in);
        this.manager.getContext().setAttribute(TagBeans_Beans.BeanDefineManager, this);
        reader.reader(this.manager, null);
    };
    /**手动执行配置装载动作，如果重复装载可能产生异常。该动作将会引发{@link Config_LoadingXmlEvent}事件*/
    public synchronized void loadDefine() throws IOException, XMLStreamException {
        this.getEventManager().doEvent(new Config_LoadingXmlEvent(this, this));//开始装载Beans
        for (Object obj : this.sourceArray)
            if (obj instanceof InputStream) {
                InputStream is = (InputStream) obj;
                try {
                    //注意这里有一个试图重置输入流的尝试
                    is.reset();
                } catch (Exception e) {}
                this.passerXml(is, this);
            } else if (obj instanceof URL) {
                InputStream is = ((URL) obj).openStream();
                this.passerXml(is, this);
                is.close();
            } else if (obj instanceof URI) {
                InputStream is = ((URI) obj).toURL().openStream();
                this.passerXml(is, this);
                is.close();
            } else if (obj instanceof File) {
                FileInputStream is = new FileInputStream((File) obj);
                this.passerXml(is, this);
                is.close();
            } else if (obj instanceof String) {
                List<InputStream> xmlINS = ClassPathUtil.getResource((String) obj);
                for (InputStream is : xmlINS) {
                    this.passerXml(is, this);
                    is.close();
                }
            }
        this.getEventManager().doEvent(new Config_LoadedXmlEvent(this, this));//装载Beans结束
    };
    /**重新装载配置，该方法会首先执行clearDefine()方法其次在执行loadDefine()。在执行之前该方法会引发{@link ReloadDefineEvent}事件。*/
    public synchronized void reloadDefine() throws IOException, XMLStreamException {
        this.getEventManager().doEvent(new ReloadDefineEvent(this));//重载Beans
        this.clearDefine();
        this.clearPlugin();
        this.loadDefine();
    }
}