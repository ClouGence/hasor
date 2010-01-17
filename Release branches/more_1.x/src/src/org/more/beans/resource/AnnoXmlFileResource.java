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
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.more.beans.info.BeanDefinition;
import org.more.beans.resource.annotation.core.Scan_ClassAnno;
import org.more.beans.resource.annotation.core.Tag_Anno;
import org.more.beans.resource.annotation.util.AnnoEngine;
import org.more.beans.resource.annotation.util.AnnoProcess;
import org.more.beans.resource.xml.XmlEngine;
import org.more.core.io.AutoCloseInputStream;
/**
 * 扩展XmlFileResource类提供注解配置方式的支持，配置文件中的配置比较注解有优先权。
 * @version 2010-1-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class AnnoXmlFileResource extends XmlFileResource {
    //========================================================================================Field
    /**  */
    private static final long serialVersionUID = -4764919069857076109L;
    //==================================================================================Constructor
    /**创建AnnoXmlFileResource对象。*/
    public AnnoXmlFileResource() {
        super((String) null);
    };
    /**创建AnnoXmlFileResource对象，参数file是配置文件位置。*/
    public AnnoXmlFileResource(File file) {
        super(file);
    };
    /**创建AnnoXmlFileResource对象，参数xmlURI是配置文件位置。*/
    public AnnoXmlFileResource(URI xmlURI) {
        super(xmlURI);
    };
    //=====================================================================================Job Core
    private Tag_Anno            annoTag  = new Tag_Anno();
    private Scan_ClassAnno      annoScan = new Scan_ClassAnno();
    /**所有的bean名称*/
    private Map<String, String> annoBeanNameMap;
    /**所有要求启动装载的bean名称*/
    private List<String>        annoStrartInitBeans;
    /*-------------------------------------------------*/
    protected void anotherClassAnnoEngine(Scan_ClassAnno annoEngine) {}
    @Override
    protected void anotherXmlEngine(XmlEngine engine) {
        super.anotherXmlEngine(engine);
        engine.regeditTag("anno", annoTag);
    };
    @Override
    @SuppressWarnings("unchecked")
    public synchronized void init() throws Exception {
        if (this.isInit() == true)
            return;
        super.init();//执行初始化方法，在初始化时会自动调用到anno标签处理函数。
        this.annoBeanNameMap = this.annoTag.getScanBeansResult();//获取扫描到的bean名称与类名映射结果
        this.annoStrartInitBeans = this.annoTag.getScanInitBeansResult();//获取要求初始化的bean名结果。
        this.annoTag.lockScan();//锁定扫描结果，在解锁前不在处理anno:anno扫描标签的扫描操作。
        this.annoScan.init();
        Properties tag = new Properties();
        tag.load(new AutoCloseInputStream(XmlFileResource.class.getResourceAsStream("/org/more/beans/resource/annotation/core/anno.properties")));//装载标签处理属性配置
        for (Object key : tag.keySet()) {
            Class<? extends Annotation> forAnno = (Class<? extends Annotation>) Class.forName((String) key);
            Class<?> forProcess = Class.forName(tag.getProperty((String) key));
            this.annoScan.regeditAnno(forAnno, (AnnoProcess) forProcess.newInstance());
        }
        this.anotherClassAnnoEngine(this.annoScan);
    };
    @Override
    public synchronized void destroy() {
        this.annoTag.unLockScan();//解锁扫描结果锁定。
        this.annoBeanNameMap.clear();
        this.annoBeanNameMap = null;
        this.annoStrartInitBeans.clear();
        this.annoStrartInitBeans = null;
        this.annoTag.destroy();//启动标签销毁
        this.annoScan.destroy();
        super.destroy();
    };
    @Override
    public boolean containsBeanDefinition(String name) {
        if (super.containsBeanDefinition(name) == true)
            return true;
        else
            return this.annoBeanNameMap.containsKey(name);
    }
    @Override
    protected BeanDefinition findBeanDefinition(String name) throws Exception {
        BeanDefinition bean = super.findBeanDefinition(name);
        if (bean != null)
            return bean;
        if (this.annoBeanNameMap.containsKey(name) == false)
            return null;
        AnnoEngine ae = new AnnoEngine();
        return (BeanDefinition) ae.runTask(Class.forName(this.annoBeanNameMap.get(name)), annoScan, new BeanDefinition()).context;
    }
    @Override
    public List<String> getBeanDefinitionNames() {
        List<String> superCache = super.getBeanDefinitionNames();
        ArrayList<String> al = new ArrayList<String>(superCache.size() + this.annoBeanNameMap.size());
        al.addAll(superCache);
        al.addAll(this.annoBeanNameMap.keySet());
        return al;
    };
    public List<String> getStrartInitBeanDefinitionNames() {
        List<String> superCache = super.getStrartInitBeanDefinitionNames();
        ArrayList<String> al = new ArrayList<String>(superCache.size() + this.annoStrartInitBeans.size());
        al.addAll(superCache);
        al.addAll(this.annoStrartInitBeans);
        return al;
    };
}