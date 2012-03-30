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
package org.more.core.xml.register;
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
import org.more.core.io.AutoCloseInputStream;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.core.xml.XmlParserKitManager;
import org.more.core.xml.stream.XmlReader;
import org.more.util.ResourcesUtil;
/**
 * 
 * @version : 2011-11-7
 * @author 赵永春 (zyc@byshell.org)
 */
public class XmlRegister extends XmlParserKitManager {
    private final static Log     log           = LogFactory.getLog(XmlRegister.class);
    private final static String  defaultConfig = "register.xml";
    public final static String[] Configs       = new String[] { "META-INF/resource/core/register.xml", "META-INF/register.xml" };
    private ArrayList<Object>    sourceArray   = new ArrayList<Object>();
    //
    //
    private void loadRegisterXML(URL registerURL) throws IOException, XMLStreamException {
        log.info("loadRegisterXML for '{%0}'.", registerURL);
        InputStream in = new AutoCloseInputStream(registerURL.openStream());
        new XmlReader(in).reader(new Reg_Parser(this), null);
    }
    /**创建{@link XmlRegister}对象,不重新装载命名空间注册。*/
    public XmlRegister(Object context) throws IOException, XMLStreamException, LoadException {
        this.setContext(context);
        for (String config : Configs) {
            List<URL> urls = ResourcesUtil.getResources(config);
            for (URL url : urls)
                this.loadRegisterXML(url);
        }
        if (isLoadDefault() == true) {
            List<URL> urls = ResourcesUtil.getResources(defaultConfig);
            for (URL url : urls)
                this.loadRegisterXML(url);
        }
    }
    /**创建{@link XmlRegister}对象,不重新装载命名空间注册。*/
    public XmlRegister() throws IOException, XMLStreamException, LoadException {
        this(null);
    }
    /**是否装载由{@link #Configs}常量定义的默认配置文件（默认true）。*/
    protected boolean isLoadDefault() {
        return true;
    }
    /*------------------------------------------------------------*/
    /**添加register配置文件。*/
    public void loadRegister(InputStream stream) throws XMLStreamException, IOException {
        if (stream != null)
            new XmlReader(stream).reader(new Reg_Parser(this), null);
    }
    /**添加register配置文件。*/
    public void loadRegister(URI uri) throws XMLStreamException, IOException {
        this.loadRegister(ResourcesUtil.getResourceAsStream(uri));
    }
    /**添加register配置文件。*/
    public void loadRegister(URL url) throws XMLStreamException, IOException {
        this.loadRegister(ResourcesUtil.getResourceAsStream(url));
    }
    /**添加register配置文件。*/
    public void loadRegister(File file) throws XMLStreamException, IOException {
        this.loadRegister(ResourcesUtil.getResourceAsStream(file));
    }
    /**添加register配置文件，该资源的存放路径是相对于classpath。*/
    public void loadRegister(String source) throws XMLStreamException, IOException {
        List<URL> urls = ResourcesUtil.getResources(source);
        if (urls != null)
            for (URL url : urls)
                this.loadRegister(url);
    }
    /*------------------------------------------------------------*/
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
    /**解析配置文件流。*/
    public void passerXml(InputStream in, Object context) throws XMLStreamException, IOException {
        if (context != null)
            this.setContext(context);
        new XmlReader(in).reader(this, null);
    };
    /**手动执行配置装载动作，如果重复装载可能产生异常。*/
    public synchronized void loadXml() throws IOException, XMLStreamException {
        this.loadXml(this.getContext());
    }
    /**手动执行配置装载动作，如果重复装载可能产生异常。参数是传入的环境对象，在解析xml的时候可以获取到。*/
    public synchronized void loadXml(Object context) throws IOException, XMLStreamException {
        log.info("loadXml source count = {%0}.", this.sourceArray.size());
        int count = this.sourceArray.size();
        for (int i = 0; i < count; i++) {
            Object obj = this.sourceArray.get(i);
            log.info("passerXml {%0} of {%1}. source = {%2}.", i, count, obj);
            if (obj instanceof InputStream) {
                InputStream is = (InputStream) obj;
                try {
                    //注意这里有一个试图重置输入流的尝试
                    log.debug("reset InputStream. Stream = {%0}", is);
                    is.reset();
                } catch (Exception e) {
                    log.warning("reset InputStream error ,Stream not supported.");
                }
                this.passerXml(is, context);
            } else if (obj instanceof URL) {
                URL url = ((URL) obj);
                log.debug("load URL '{%0}'", url);
                InputStream is = url.openStream();
                this.passerXml(is, context);
                is.close();
            } else if (obj instanceof URI) {
                URI uri = ((URI) obj);
                log.debug("load URI '{%0}'", uri);
                InputStream is = uri.toURL().openStream();
                this.passerXml(is, context);
                is.close();
            } else if (obj instanceof File) {
                File file = (File) obj;
                log.debug("load File '{%0}'", file);
                FileInputStream is = new FileInputStream(file);
                this.passerXml(is, context);
                is.close();
            } else if (obj instanceof String) {
                List<URL> urls = ResourcesUtil.getResources((String) obj);
                log.debug("load String '{%0}' include [{%1}]", obj, urls);
                for (URL url : urls) {
                    InputStream is = url.openConnection().getInputStream();
                    this.passerXml(is, context);
                    is.close();
                }
            }
        }
        log.info("loadXml finish!");
    }
}