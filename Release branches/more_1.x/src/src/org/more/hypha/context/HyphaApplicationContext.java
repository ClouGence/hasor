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
import java.io.IOException;
import java.util.List;
import org.more.ClassFormatException;
import org.more.DoesSupportException;
import org.more.NoDefinitionException;
import org.more.hypha.AbstractEventManager;
import org.more.hypha.AbstractExpandPointManager;
import org.more.hypha.ApplicationContext;
import org.more.hypha.DefineResource;
import org.more.hypha.ELContext;
import org.more.hypha.beans.AbstractBeanDefine;
import org.more.hypha.beans.assembler.AbstractBeanEngine;
import org.more.hypha.beans.assembler.AbstractELContext;
import org.more.hypha.beans.assembler.RootValueMetaDataParser;
import org.more.hypha.event.DestroyEvent;
import org.more.hypha.event.InitEvent;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 简单的{@link ApplicationContext}接口实现类。
 * Date : 2011-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class HyphaApplicationContext implements ApplicationContext {
    private DefineResource          defineResource     = null;
    private ClassLoader             classLoader        = null; //Context的类装载器
    private Object                  context            = null; //绑定到Context上的上下文。
    //可延迟可替换
    private IAttribute              attributeContext   = null; //属性集
    private IAttribute              flashContext       = null; //全局FLASH
    //init期间必须构建
    private RootValueMetaDataParser rootMetaDataParser = null; //元信息解析器
    private AbstractBeanEngine      engine             = null; //Bean构造引擎
    private AbstractELContext       elContext          = null; //EL管理器
    /**
     * 构造{@link HyphaApplicationContext}对象，这个构造方法会导致{@link HyphaApplicationContext}内部创建一个{@link ArrayDefineResource}对象。
     * @param context 构造方法中有一个参数该参数表示一个上下文 。通过el可以访问到这个对象。
     */
    public HyphaApplicationContext(Object context) {
        ArrayDefineResource adr = new ArrayDefineResource();
        this.flashContext = adr.getFlash();
        this.attributeContext = adr.getAttribute();
        this.defineResource = adr;
        this.context = context;
    };
    /**
     * 构造{@link HyphaApplicationContext}对象，这个构造方法会导致{@link HyphaApplicationContext}内部创建一个{@link ArrayDefineResource}对象。
     * @param defineResource 指定{@link HyphaApplicationContext}对象一个Bean定义的数据源。
     * @param context 构造方法中有一个参数该参数表示一个上下文 。通过el可以访问到这个对象。
     */
    public HyphaApplicationContext(DefineResource defineResource, Object context) {
        if (defineResource == null)
            throw new NullPointerException("defineResource参数不能为空");
        this.defineResource = defineResource;
        this.context = context;
    };
    /**设置ClassLoader，通常在初始化之前进行设置。*/
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    };
    public List<String> getBeanDefinitionIDs() {
        return this.defineResource.getBeanDefinitionIDs();
    };
    public AbstractBeanDefine getBeanDefinition(String id) throws NoDefinitionException {
        return this.defineResource.getBeanDefine(id);
    };
    public DefineResource getBeanResource() {
        return this.defineResource;
    };
    public ClassLoader getBeanClassLoader() {
        if (this.classLoader == null)
            return ClassLoader.getSystemClassLoader();
        return this.classLoader;
    };
    public boolean containsBean(String id) {
        return this.defineResource.containsBeanDefine(id);
    };
    public boolean isPrototype(String id) throws NoDefinitionException {
        return this.defineResource.isPrototype(id);
    };
    public boolean isSingleton(String id) throws NoDefinitionException {
        return this.defineResource.isSingleton(id);
    };
    public boolean isFactory(String id) throws NoDefinitionException {
        return this.defineResource.isFactory(id);
    };
    public boolean isTypeMatch(String id, Class<?> targetType) throws Throwable {
        //Object.class.isAssignableFrom(XmlTest.class); return true;
        if (targetType == null)
            throw new NullPointerException("参数targetType不能为空.");
        Class<?> beanType = this.getBeanType(id);
        return targetType.isAssignableFrom(beanType);
    };
    public synchronized void init() throws Throwable {
        //1.环境初始化，如果defineResource没有准备好就一直等待
        while (!this.defineResource.isReady())
            Thread.sleep(500);
        //----------------------------------------
        //2.构造RootValueMetaDataParser
        this.rootMetaDataParser = new RootValueMetaDataParser(this, this.getFlash());
        this.rootMetaDataParser.loadConfig();//处理“regedit-metadata.prop”配置文件。
        //3.构造AbstractBeanEngine
        this.engine = new AbstractBeanEngine(this, this.getFlash()) {
            protected AbstractExpandPointManager getExpandPointManager() {
                return HyphaApplicationContext.this.defineResource.getExpandPointManager();
            }
        };
        this.engine.loadConfig();//处理“regedit-beantype.prop”配置文件。
        //4.构造AbstractELContext
        this.elContext = new AbstractELContext() {};
        this.elContext.loadConfig();//处理“regedit-el.prop”配置文件。
        //----------------------------------------
        //5.初始化bean
        for (String id : this.getBeanDefinitionIDs()) {
            AbstractBeanDefine define = this.getBeanDefinition(id);
            if (define.isLazyInit() == false)
                this.getBean(id);
        }
        //6.初始化事件 
        this.getEventManager().doEvent(new InitEvent(this, this));
    };
    public void destroy() throws Throwable {
        this.engine = null;
        this.elContext = null;
        this.getAttribute().clearAttribute();
        this.defineResource.clearDefine();
        this.getEventManager().doEvent(new DestroyEvent(this, this));
    };
    public Object getBean(String id, Object... objects) throws Throwable {
        AbstractBeanDefine define = this.getBeanDefinition(id);
        return this.engine.builderBean(define, objects);
    };
    public Class<?> getBeanType(String id) throws DoesSupportException, IOException, ClassFormatException, ClassNotFoundException {
        AbstractBeanDefine define = this.getBeanDefinition(id);
        return this.engine.builderType(define);
    };
    public Object getContext() {
        return this.context;
    };
    /*------------------------------------------------------------*/
    /**该方法可以获取{@link HyphaApplicationContext}接口对象所使用的属性管理器。子类可以通过重写该方法以来控制属性管理器对象。*/
    protected IAttribute getAttribute() {
        if (this.attributeContext == null)
            this.attributeContext = this.defineResource.getAttribute();
        if (this.attributeContext == null)
            this.attributeContext = new AttBase();
        return this.attributeContext;
    };
    public boolean contains(String name) {
        return this.getAttribute().contains(name);
    };
    public void setAttribute(String name, Object value) {
        this.getAttribute().setAttribute(name, value);
    };
    public Object getAttribute(String name) {
        return this.getAttribute().getAttribute(name);
    };
    public void removeAttribute(String name) {
        this.getAttribute().removeAttribute(name);
    };
    public String[] getAttributeNames() {
        return this.getAttribute().getAttributeNames();
    };
    public void clearAttribute() {
        this.getAttribute().clearAttribute();
    };
    /*------------------------------------------------------------*/
    /**获取用于保存属性的对象。*/
    /**获取全局属性闪存，子类可以通过重写该方法来替换FLASH。*/
    protected IAttribute getFlash() {
        if (this.flashContext == null)
            this.flashContext = new AttBase();
        return this.flashContext;
    };
    public AbstractEventManager getEventManager() {
        return this.defineResource.getEventManager();
    };
    public AbstractExpandPointManager getExpandPointManager() {
        return this.defineResource.getExpandPointManager();
    }
    public ELContext getELContext() {
        return this.elContext;
    };
};