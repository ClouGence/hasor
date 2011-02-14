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
import java.util.List;
import org.more.InitializationException;
import org.more.NoDefinitionException;
import org.more.core.ognl.OgnlContext;
import org.more.hypha.ApplicationContext;
import org.more.hypha.DefineResource;
import org.more.hypha.EventManager;
import org.more.hypha.ExpandPointManager;
import org.more.hypha.beans.AbstractBeanDefine;
import org.more.hypha.beans.assembler.factory.BeanEngine;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 
 * @version 2010-11-30
 * @author 赵永春 (zyc@byshell.org)
 */
public class HyphaApplicationContext implements ApplicationContext {
    private DefineResource defineResource = null; //
    private ClassLoader    classLoader    = null; //Context的类装载器
    //
    private IAttribute     flashContext   = null; //Hypha的全局FLASH，如果是来自于ArrayDefineResource的FLASH则是经过代理的以确保与DefineResource中的保持一致。
    //
    private BeanEngine     engine         = null; //类创建引擎
    private OgnlContext    elContext      = null; //el上下文
    private Object         context        = null; //绑定到Context上的上下文。
    /*------------------------------------------------------------*/
    public HyphaApplicationContext(DefineResource defineResource, Object context, IAttribute flashContext) {
        this.defineResource = defineResource;
        this.context = context;
        this.flashContext = flashContext;
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
    public boolean isTypeMatch(String id, Class<?> targetType) throws NoDefinitionException, NullPointerException {
        //Object.class.isAssignableFrom(XmlTest.class); return true
        if (targetType == null)
            throw new NullPointerException("参数targetType不能为空.");
        Class<?> beanType = this.getBeanType(id);
        return targetType.isAssignableFrom(beanType);
    };
    public synchronized void init() throws Exception {
        //1.环境初始化，如果defineResource没有准备好就一直等待
        while (!this.defineResource.isReady())
            Thread.sleep(1000);
        //2.
        System.out.println();
        this.engine = new BeanEngine(this, this.flashContext);
        this.engine.init();
        this.elContext = new OgnlContext();
        //2.初始化el属性。
        ////this
        ////$context
        ////$att
        ////$beans
        //TODO
        //this.elContext.put("context", this.getAttribute());
        //this.elContext.put("this", this);
        //3.初始化bean
        for (String id : this.getBeanDefinitionIDs()) {
            AbstractBeanDefine define = this.getBeanDefinition(id);
            if (define.isLazyInit() == false)
                this.getBean(id);
        }
    };
    public void destroy() throws Exception {
        this.engine = null;
        this.elContext = null;
        this.getAttribute().clearAttribute();
        this.defineResource.clearDefine();
    };
    public Object getBean(String id, Object... objects) throws NoDefinitionException, InitializationException {
        AbstractBeanDefine define = this.getBeanDefinition(id);
        return this.engine.builderBean(define, objects);
    };
    public Class<?> getBeanType(String id) throws NoDefinitionException {
        AbstractBeanDefine define = this.getBeanDefinition(id);
        return this.engine.builderType(define);
    };
    public Object getContext() {
        return this.context;
    };
    /**获取el上下文环境。*/
    protected OgnlContext getElContext() {
        return this.elContext;
    };
    /*------------------------------------------------------------*/
    private AttBase attBase = null;
    protected IAttribute getAttribute() {
        if (this.attBase == null)
            this.attBase = new AttBase();
        return this.attBase;
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
    public EventManager getEventManager() {
        return this.defineResource.getEventManager();
    };
    public ExpandPointManager getExpandPointManager() {
        return this.defineResource.getExpandPointManager();
    };
}