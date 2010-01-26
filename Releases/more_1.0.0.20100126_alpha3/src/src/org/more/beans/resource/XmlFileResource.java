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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.more.DoesSupportException;
import org.more.FormatException;
import org.more.beans.BeanResource;
import org.more.beans.info.BeanDefinition;
import org.more.beans.resource.xml.TagProcess;
import org.more.beans.resource.xml.TaskProcess;
import org.more.beans.resource.xml.XmlEngine;
import org.more.core.io.AutoCloseInputStream;
import org.more.util.attribute.AttBase;
/**
 * 提供了以XML作为数据源提供bean数据的支持。如果使用无参的构造方法XmlFileResource类将使用字段
 * defaultConfigFile所表示的文件名作为默认配置文件位置，该配置文件位置相对于程序启动目录下。
 * @version 2010-1-11
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings("unchecked")
public class XmlFileResource extends ArrayResource implements BeanResource {
    //========================================================================================Field
    /**  */
    private static final long  serialVersionUID  = 5085542182667236561L;
    /**默认配置文件名*/
    public final static String defaultConfigFile = "more-config.xml";
    /**是否开启XSD验证*/
    private boolean            validator         = true;
    /**配置文件位置URI，字符串*/
    private URI                sourceURI;
    //==================================================================================Constructor
    /**创建XmlFileResource对象，该对象使用XmlFileResource.defaultConfigFile作为默认配置文件位置。 */
    public XmlFileResource(String sourceName) {
        super(sourceName, null);
        this.sourceURI = new File(XmlFileResource.defaultConfigFile).toURI();
    };
    /**创建XmlFileResource对象，参数xmlResourceURI是配置文件位置。*/
    public XmlFileResource(URI xmlResourceURI) {
        this(xmlResourceURI.toString());
        this.sourceURI = xmlResourceURI;
    };
    /**创建XmlFileResource对象，参数xmlResourceFile是配置文件位置。*/
    public XmlFileResource(File xmlResourceFile) {
        this(xmlResourceFile.toString());
        this.sourceURI = xmlResourceFile.toURI();
    };
    //==========================================================================================Job
    /**是否开启Schema验证XML配置正确性，默认值是true开启验证。*/
    public boolean isValidator() {
        return validator;
    };
    /**设置是否开启Schema验证XML配置正确性。*/
    public void setValidator(boolean validator) {
        this.validator = validator;
    };
    @Override
    public URI getSourceURI() {
        return this.sourceURI;
    };
    @Override
    public boolean isCacheBeanMetadata() {
        return true;
    };
    /** 通过Schema验证XML配置是否正确，返回null表示验证通过，否则返回错误信息。 */
    public Exception validatorXML() {
        if (this.validator == false)
            return null;
        //----
        try {
            InputStream in = new AutoCloseInputStream(XmlFileResource.class.getResourceAsStream("/META-INF/xsl-list"));
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String str = null;
            ArrayList<Source> sourceList = new ArrayList<Source>();
            while ((str = br.readLine()) != null) {
                InputStream xsdIn = XmlFileResource.class.getResourceAsStream(str);
                if (xsdIn != null)
                    sourceList.add(new StreamSource(xsdIn));
            }
            Source[] source = new Source[sourceList.size()];
            sourceList.toArray(source);
            //
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);//建立schema工厂
            Schema schema = schemaFactory.newSchema(source); //利用schema工厂，接收验证文档文件对象生成Schema对象
            Validator validator = schema.newValidator();//通过Schema产生针对于此Schema的验证器，利用students.xsd进行验证
            Source xmlSource = new StreamSource(this.getXmlInputStream());//得到验证的数据源
            //开始验证，成功输出success!!!，失败输出fail
            validator.validate(xmlSource);
            return null;
        } catch (Exception ex) {
            return ex;
        }
    };
    /**获取XML输入流*/
    protected InputStream getXmlInputStream() throws MalformedURLException, IOException {
        return this.sourceURI.toURL().openStream();
    };
    /*-------------------------------------------------------------------------------------------*/
    /**/
    /**/
    //=====================================================================================Override
    /**/
    /**/
    /*-------------------------------------------------------------------------------------------*/
    /**XML解析引擎*/
    protected XmlEngine                     xmlEngine = new XmlEngine();
    /*-------------------------------------------------------*/
    /**所有的bean名称*/
    private List<String>                    xmlBeanNames;
    /**所有要求启动装载的bean名称*/
    private List<String>                    xmlStrartInitBeans;
    /*-------------------------------------------------------*/
    /**动态缓存对象数目。*/
    private int                             dynamicCacheSize;
    /**动态缓存对象。*/
    private HashMap<String, BeanDefinition> dynamicCache;
    /**动态缓存对象名称集合*/
    private LinkedList<String>              dynamicCacheNames;
    //=======================================================Protected
    protected void putDynamicCache(BeanDefinition bean) {
        if (dynamicCacheNames.size() >= this.dynamicCacheSize)
            this.dynamicCache.remove(dynamicCacheNames.removeFirst());
        this.dynamicCache.put(bean.getName(), bean);//缓存
    }
    // (TaskProcess)tasks.getAttribute(value)
    protected void anotherXmlEngine(XmlEngine engine) {};
    /**执行XML任务*/
    protected Object runTask(TaskProcess task, String processXPath, Object[] params) throws Exception {
        return this.xmlEngine.runTask(this.getXmlInputStream(), task, processXPath, params);
    };
    //=============================================================Job
    /** 如果已经初始化则执行销毁在执行初始化。*/
    public synchronized void reload() throws Exception {
        if (this.isInit() == true)
            this.destroy();
        this.init();
    }
    public synchronized void init() throws Exception {
        if (this.isInit() == true)
            return;
        super.init();
        /*----------------------------------------------一、验证XML*/
        Exception validator = this.validatorXML();
        if (validator != null)
            throw new FormatException("Schema验证失败", validator);
        /*----------------------------------------------二、初始化必要属性*/
        this.dynamicCache = new HashMap<String, BeanDefinition>();//动态缓存对象。
        this.dynamicCacheNames = new LinkedList<String>();//动态缓存对象名称集合
        this.setSourceName(this.sourceURI.toString());
        /*----------------------------------------------三、读取标签配置*/
        Properties tag = new Properties();
        tag.load(new AutoCloseInputStream(XmlFileResource.class.getResourceAsStream("/org/more/beans/resource/xml/core/tagProcess.properties")));//装载标签处理属性配置
        for (Object tagName : tag.keySet()) {
            Class<?> tagProcessType = Class.forName(tag.getProperty((String) tagName));
            this.xmlEngine.regeditTag((String) tagName, (TagProcess) tagProcessType.newInstance());
        }
        //
        Properties task = new Properties();
        task.load(new AutoCloseInputStream(XmlFileResource.class.getResourceAsStream("/org/more/beans/resource/xml/core/taskProcess.properties")));//装载任务处理配置
        for (Object taskName : task.keySet()) {
            Class<?> taskType = Class.forName(task.getProperty((String) taskName));
            xmlEngine.setAttribute((String) taskName, (TaskProcess) taskType.newInstance());
        }
        this.anotherXmlEngine(xmlEngine);
        /*----------------------------------------------四、处理任务执行结果。*/
        TaskProcess init_task = (TaskProcess) xmlEngine.getAttribute("init");
        AttBase att = (AttBase) this.runTask(init_task, ".*", null);
        this.dynamicCacheSize = (Integer) att.get("dynamicCache");
        HashMap<String, BeanDefinition> staticCache = (HashMap<String, BeanDefinition>) att.get("beanList");//获取静态bean缓存
        for (BeanDefinition b : staticCache.values())
            this.addBeanDefinition(b);
        this.xmlBeanNames = (List<String>) att.get("allNames");//获取所有bean名。
        this.xmlStrartInitBeans = (List<String>) att.get("initBean");//获取所有要求初始化的bean名。
    };
    @Override
    public synchronized void destroy() {
        this.clearCache();
        this.clearAttribute();
        this.xmlBeanNames.clear();//所有的bean名称
        this.xmlBeanNames = null;//所有的bean名称
        this.dynamicCacheSize = 50;//动态缓存对象数目。
        this.dynamicCache.clear();//动态缓存对象。
        this.dynamicCache = null;//动态缓存对象。
        this.dynamicCacheNames.clear();//动态缓存对象名称集合
        this.dynamicCacheNames = null;//动态缓存对象名称集合
        this.xmlStrartInitBeans.clear();
        this.xmlStrartInitBeans = null;
        this.xmlEngine.destroy();
        super.destroy();
    };
    @Override
    public synchronized void clearCache() throws DoesSupportException {
        this.dynamicCacheNames.clear();
        this.dynamicCache.clear();
        super.clearCache();
    }
    @Override
    public boolean containsBeanDefinition(String name) {
        if (super.containsBeanDefinition(name) == true)
            return true;
        else
            return this.xmlBeanNames.contains(name);
    }
    @Override
    protected BeanDefinition findBeanDefinition(String name) throws Exception {
        if (this.dynamicCacheNames.contains(name) == true)
            return this.dynamicCache.get(name);
        /*--------------*/
        TaskProcess find_task = (TaskProcess) xmlEngine.getAttribute("findBean");
        BeanDefinition bean = (BeanDefinition) this.runTask(find_task, ".*", new Object[] { name });
        if (bean != null)
            this.putDynamicCache(bean);
        return bean;
    }
    @Override
    public List<String> getBeanDefinitionNames() {
        List<String> staticCache = super.getBeanDefinitionNames();
        ArrayList<String> al = new ArrayList<String>(staticCache.size() + this.xmlBeanNames.size());
        al.addAll(staticCache);
        for (String str : this.xmlBeanNames)
            if (al.contains(str) == false)
                al.add(str);
        return al;
    }
    @Override
    public List<String> getStrartInitBeanDefinitionNames() {
        List<String> start_1 = super.getStrartInitBeanDefinitionNames();
        ArrayList<String> al = new ArrayList<String>(start_1.size() + this.xmlStrartInitBeans.size());
        al.addAll(start_1);
        for (String str : this.xmlStrartInitBeans)
            if (al.contains(str) == false)
                al.add(str);
        return al;
    }
};