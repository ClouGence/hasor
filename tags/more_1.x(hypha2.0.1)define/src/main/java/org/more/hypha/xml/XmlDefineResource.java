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
package org.more.hypha.xml;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.more.core.error.LoadException;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.core.xml.XmlParserKit;
import org.more.core.xml.register.XmlRegister;
import org.more.core.xml.stream.XmlReader;
import org.more.hypha.context.array.ArrayDefineResource;
import org.more.hypha.xml._NameSpaceConfiguration.RegisterBean;
import org.more.util.ResourcesUtil;
import org.more.webui.event.Event;
/**
 * 该类是继承自{@link ArrayDefineResource}类，通过该类可以读取存在于配置文件中的类定义信息。
 * @version 2010-11-30
 * @author 赵永春 (zyc@byshell.org)
 */
public class XmlDefineResource extends ArrayDefineResource {
    private static Log          log                 = LogFactory.getLog(XmlDefineResource.class);
    private static final String ResourcePath        = "META-INF/resource/hypha/register.xml";            //标签注册器
    private static final String DefaultResourcePath = "META-INF/resource/hypha/default-hypha-config.xml"; //默认配置文件
    private ArrayList<Object>   sourceArray         = new ArrayList<Object>();
    private XmlRegister         xmlRegister         = new XmlRegister();                                 //xml解析器
    private boolean             loadMark            = false;                                             //是否已经执行过装载.
    /*------------------------------------------------------------*/
    /*------------------------------------------------------------*/
    /**创建{@link XmlDefineResource}对象，参数sinit表明是否重新装载命名空间解析。*/
    private XmlDefineResource(boolean sinit) throws IOException, XMLStreamException, LoadException {
        if (sinit == true) {
            log.info("create XmlDefineResource yes reload 'register.xml'");
            r_s_init(this);
        } else {
            log.info("create XmlDefineResource no reload 'register.xml'");
            s_init(this);
        }
    }
    /**创建{@link XmlDefineResource}对象。*/
    public XmlDefineResource() throws IOException, XMLStreamException, LoadException {
        this(false);//不重新装载命名空间注册。
    }
    /**创建{@link XmlDefineResource}对象，sourceFile是要装载的配置文件，该路径应当相当于classpath。*/
    public XmlDefineResource(String sourceFile) throws IOException, XMLStreamException, LoadException {
        this();
        this.addSource(sourceFile);
    }
    /**创建{@link XmlDefineResource}对象，sourceFiles是要装载的配置文件，该路径应当相当于classpath。*/
    public XmlDefineResource(String[] sourceFiles) throws IOException, XMLStreamException, LoadException {
        this();
        for (String sf : sourceFiles)
            this.addSource(sf);
    }
    /**创建{@link XmlDefineResource}对象，sourceURL是要装载的配置文件。*/
    public XmlDefineResource(URL sourceURL) throws IOException, XMLStreamException, LoadException {
        this();
        this.addSource(sourceURL);
    }
    /**创建{@link XmlDefineResource}对象，sourceURLs是要装载的配置文件。*/
    public XmlDefineResource(URL[] sourceURLs) throws IOException, XMLStreamException, LoadException {
        this();
        for (URL url : sourceURLs)
            this.addSource(url);
    }
    /**创建{@link XmlDefineResource}对象，sourceURI是要装载的配置文件。*/
    public XmlDefineResource(URI sourceURI) throws IOException, XMLStreamException, LoadException {
        this();
        this.addSource(sourceURI);
    }
    /**创建{@link XmlDefineResource}对象，sourceURIs是要装载的配置文件。*/
    public XmlDefineResource(URI[] sourceURIs) throws IOException, XMLStreamException, LoadException {
        this();
        for (URI uri : sourceURIs)
            this.addSource(uri);
    }
    /**创建{@link XmlDefineResource}对象，sourceFile是要装载的配置文件。*/
    public XmlDefineResource(File sourceFile) throws IOException, XMLStreamException, LoadException {
        this();
        this.addSource(sourceFile);
    }
    /**创建{@link XmlDefineResource}对象，sourceFiles是要装载的配置文件。*/
    public XmlDefineResource(File[] sourceFiles) throws IOException, XMLStreamException, LoadException {
        this();
        for (File file : sourceFiles)
            this.addSource(file);
    }
    /**创建{@link XmlDefineResource}对象，sourceStream是要装载的配置文件流。*/
    public XmlDefineResource(InputStream sourceStream) throws IOException, XMLStreamException, LoadException {
        this();
        this.addSource(sourceStream);
    }
    /**创建{@link XmlDefineResource}对象，sourceStream是要装载的配置文件流。*/
    public XmlDefineResource(InputStream[] sourceStreams) throws IOException, XMLStreamException, LoadException {
        this();
        for (InputStream is : sourceStreams)
            this.addSource(is);
    }
    /*------------------------------------------------------------*/
    /**创建{@link XmlDefineResource}对象，该方法将不会重新扫描ClassPath中的命名空间注册。*/
    public static XmlDefineResource newInstance() throws IOException, XMLStreamException, LoadException {
        return new XmlDefineResource(false);
    };
    /**创建{@link XmlDefineResource}对象，该方法将导致重新扫描ClassPath中的命名空间注册。*/
    public static XmlDefineResource newInstanceByNew() throws IOException, XMLStreamException, LoadException {
        return new XmlDefineResource(true);
    };
    private void addSourceArray(Object source) {
        if (source == null) {
            log.warning("addSource source is null.");
            return;
        }
        if (this.sourceArray.contains(source) == false) {
            log.debug("addSource {%0}.", source);
            this.sourceArray.add(source);
        } else
            log.warning("addSource source error ,exist source. {%0}", source);
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
    /**获取一个状态该状态表述是否已经准备好，{@link XmlDefineResource}类型中当执行了装载方法之后该方法返回true否则返回false。*/
    public boolean isReady() {
        return this.loadMark;
    }
    /**解析配置文件流。*/
    protected synchronized void passerXml(InputStream in, DefineResource conf) throws XMLStreamException, IOException {
        new XmlReader(in).reader(this.xmlRegister, null);/*第二个参数是排除路径*/
    };
    /**手动执行配置装载动作，如果重复装载可能产生异常。该动作将会引发{@link XmlLoadingEvent}事件*/
    public synchronized void loadDefine() throws IOException, XMLStreamException {
        log.info("loadDefine source count = {%0}.", this.sourceArray.size());
        this.throwEvent(Event.getEvent(XmlLoadEvent.class), this);//开始装载Beans
        int count = this.sourceArray.size();
        for (int i = 0; i < count; i++) {
            Object obj = this.sourceArray.get(i);
            log.info("passerXml {%0} of {%1}. source = {%2}.", i, count, obj);
            this.throwEvent(Event.getEvent(XmlLoadingEvent.class), this, obj);
            if (obj instanceof InputStream) {
                InputStream is = (InputStream) obj;
                try {
                    //注意这里有一个试图重置输入流的尝试
                    log.debug("reset InputStream. Stream = {%0}", is);
                    is.reset();
                } catch (Exception e) {
                    log.warning("reset InputStream error ,Stream not supported.");
                }
                this.passerXml(is, this);
            } else if (obj instanceof URL) {
                URL url = ((URL) obj);
                log.debug("load URL '{%0}'", url);
                InputStream is = url.openStream();
                this.passerXml(is, this);
                is.close();
            } else if (obj instanceof URI) {
                URI uri = ((URI) obj);
                log.debug("load URI '{%0}'", uri);
                InputStream is = uri.toURL().openStream();
                this.passerXml(is, this);
                is.close();
            } else if (obj instanceof File) {
                File file = (File) obj;
                log.debug("load File '{%0}'", file);
                FileInputStream is = new FileInputStream(file);
                this.passerXml(is, this);
                is.close();
            } else if (obj instanceof String) {
                List<URL> urls = ResourcesUtil.getResources((String) obj);
                log.debug("load String '{%0}' include [{%1}]", obj, urls);
                for (URL url : urls) {
                    InputStream is = url.openConnection().getInputStream();
                    this.passerXml(is, this);
                    is.close();
                }
            }
        }
        this.throwEvent(Event.getEvent(XmlLoadedEvent.class), this);//装载Beans结束
        log.info("loadDefine finish!");
        this.loadMark = true;
    };
    /**重新装载配置，该方法会首先执行clearDefine()方法其次在执行loadDefine()。在执行之前该方法会引发{@link XmlReloadDefineEvent}事件。*/
    public synchronized void reloadDefine() throws IOException, XMLStreamException {
        log.info("throw XmlReloadDefineEvent...");
        this.throwEvent(Event.getEvent(XmlReloadDefineEvent.class), this);//重载Beans
        this.clearDefine();
        this.getFlash().clearAttribute();
        this.loadDefine();
    }
    public synchronized void clearDefine() {
        super.clearDefine();
        this.loadMark = false;
    }
}