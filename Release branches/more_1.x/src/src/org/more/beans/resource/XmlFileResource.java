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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.more.DoesSupportException;
import org.more.InitializationException;
import org.more.beans.BeanResource;
import org.more.beans.info.BeanDefinition;
import org.more.beans.resource.xml.XMLEngine;
import org.more.util.attribute.AttBase;
/**
 * 
//-----------------------------------------------------------------------------
//二、解析构造方法
//三、解析工厂方法配置
//四、解析属性配置
 * <br/>Date : 2009-11-21
 * @author 赵永春
 */
@SuppressWarnings("unchecked")
public class XmlFileResource extends AttBase implements BeanResource {
    //========================================================================================Field
    /**  */
    private static final long                 serialVersionUID = 5085542182667236561L;
    private URI                               xmlFile          = null;                                 //配置文件
    /*---------------------*/
    /** XML解析引擎 */
    protected XMLEngine                       xmlEngine        = null;
    /**静态缓存对象数目。*/
    protected int                             staticCacheSize  = 10;
    /**静态缓存对象。*/
    protected HashMap<String, BeanDefinition> staticCache      = new HashMap<String, BeanDefinition>();
    /**动态缓存对象数目。*/
    protected int                             dynamicCacheSize = 50;
    /**动态缓存对象。*/
    protected HashMap<String, BeanDefinition> dynamicCache     = new HashMap<String, BeanDefinition>();
    //==================================================================================Constructor
    /**创建XmlFileResource对象。*/
    public XmlFileResource() {
        this.xmlEngine = new XMLEngine();
    }
    /**创建XmlFileResource对象，参数filePath是配置文件位置。*/
    public XmlFileResource(String filePath) throws FileNotFoundException {
        this();
        this.xmlFile = new File(filePath).toURI();
    }
    /**创建XmlFileResource对象，参数file是配置文件位置。*/
    public XmlFileResource(File file) throws FileNotFoundException {
        this();
        this.xmlFile = file.toURI();
    }
    //=========================================================================================Impl
    @Override
    public void clearCache() throws DoesSupportException {
        this.staticCache.clear();
        this.dynamicCache.clear();
    }
    @Override
    public boolean containsBeanDefinition(String name) {
        return this.xmlEngine.runTask("", processXPath, params)testPath("/beans/bean/@name=" + name);
    }
    @Override
    public BeanDefinition getBeanDefinition(String name) {
        if (this.staticCache.containsKey(name) == true)
            return this.staticCache.get(name);
        if (this.dynamicCache.containsKey(name) == true)
            return this.dynamicCache.get(name);
        BeanDefinition bean = this.xmlEngine.findBeanDefinition(name);
        if (bean != null) {
            this.dynamicCache.put(bean.getName(), bean);//缓存
        }
        return bean;
    }
    @Override
    public List<String> getBeanDefinitionNames() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getResourceDescription() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public File getSourceFile() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getSourceName() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public URI getSourceURI() {
        return this.xmlFile;
    }
    @Override
    public URL getSourceURL() throws MalformedURLException {
        // TODO Auto-generated method stub
        return this.xmlFile.toURL();
    }
    @Override
    public List<String> getStrartInitBeanDefinitionNames() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public boolean isCacheBeanMetadata() {
        return true;
    }
    @Override
    public boolean isFactory(String name) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isPrototype(String name) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isSingleton(String name) {
        // TODO Auto-generated method stub
        return false;
    }
}