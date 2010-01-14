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
package org.more.beans.core;
import java.util.HashMap;
import java.util.List;
import org.more.InvokeException;
import org.more.NoDefinitionException;
import org.more.TypeException;
import org.more.beans.BeanFactory;
import org.more.beans.BeanResource;
import org.more.beans.core.factory.CreateFactory;
import org.more.beans.core.injection.InjectionFactory;
import org.more.beans.core.propparser.MainPropertyParser;
import org.more.beans.info.BeanDefinition;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
import org.more.util.attribute.KeepAttDecorator;
/**
 * 该类是BeanFactory接口的一个最基本实现，该类必须指定resource资源对象作为bean定义源，ResourceBeanFactory的子类可以提供更多的新功能新特性。
 * ResourceBeanFactory中提供了对lazyInit属性的支持。<br/>更多的功能描述参看{@link BeanFactory}和相关文档。<br/><br/>
 * <font color="#ff0000">
 * 注意：我不建议在创建ResourceBeanFactory对象之后再次调用setLoader重新设置类装载器。如果整个容器中所有bean没有配置任何aop或者附加接口实现
 * 那么不会引发什么潜在问题，但是一旦配置了而且BeanResource对象支持缓存功能，那么很可能会产生一些有关ClassLoader方面的古怪异常。<br/>
 * 原因是这样的每次factory和injection软件包生成的新字节码文件被装载时都使用BeanFactory的类装载器作为父类装载器。但是一旦这个类装载器被替换。
 * 那么这些已经生成的新类类型由于它们被缓存在BeanResource接口中当再次调用它们时他们的类装载器层次结构已经不包含BeanFactory的新的类装载。因此在
 * 接下来的create或者ioc过程我不确定一定会安全执行。另外在more中所有类型的装载（包括属性类型的装载）都是通过BeanFactory的类装载器进行。
 * 使用这个特性我们的bean可以完全来源于网络或者其他地方而不必在当前系统的ClassPath中。
 * </font>
 * @version 2009-11-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class ResourceBeanFactory implements BeanFactory {
    /**  */
    private static final long       serialVersionUID   = -2164352693306612896L;
    //========================================================================================Field
    private BeanResource            resource           = null;                         //Bean资源
    private HashMap<String, Object> singletonBeanCache = new HashMap<String, Object>(); //用于保存单态bean
    private ClassLoader             loader             = null;                         //类装载
    /**负责对象创建*/
    protected CreateFactory         createFactory      = null;
    /**负责对象依赖注入*/
    protected InjectionFactory      injectionFactory   = null;
    /**属性解析器，专门负责解析BeanProperty属性对象。*/
    protected MainPropertyParser    propParser         = null;
    /**
     * 环境属性集合，ResourceBeanFactory在构造方法中会自动将this加入属性集合中，
     * 并且配置this为保持属性，作为保持属性不可以被覆写，有关保持属性请参阅
     *  {@link org.more.util.attribute.KeepAttDecorator}。
     * 环境属性使用{#name}可以注入这个属性
     */
    protected IAttribute            attribute          = null;
    //==================================================================================Constructor
    /**
     * 创建ResourceBeanFactory类型对象，创建该对象必须指定resource参数否则回引发NullPointerException异常。
     * @param resource ResourceBeanFactory所使用的bean资源。
     * @param loader ResourceBeanFactory所使用的ClassLoader，如果给定null则采取Thread.currentThread().getContextClassLoader();
     */
    public ResourceBeanFactory(BeanResource resource, ClassLoader loader) throws Exception {
        if (resource == null)
            throw new NullPointerException("参数resource不能为空。");
        //确定使用哪个loader。
        if (loader == null)
            this.loader = Thread.currentThread().getContextClassLoader();
        this.resource = resource;
        //
        this.propParser = new MainPropertyParser(this);//属性解析器，专门负责解析BeanProperty属性对象。
        this.createFactory = new CreateFactory(this.propParser);//负责对象创建
        this.injectionFactory = new InjectionFactory(this.propParser);//负责对象依赖注入
        //创建环境属性对象，并且加装保持装饰器。
        KeepAttDecorator kad = new KeepAttDecorator(new AttBase());
        this.attribute = kad;
        kad.setAttribute("this", this);//设置关键字this。
        kad.setKeep("this", true);//设置关键字this为保持属性，不可更改。
        init();//执行初始化方法调用。
    }
    //==========================================================================================Job
    /**该方法主要用于Factory方式处理Ioc时候无法获取属性类型解析器对象而设立。*/
    MainPropertyParser getPropParser() {
        return propParser;
    }
    /**清空所有Bean缓存，并且重新装载lazyInit属性为false的bean。*/
    public void reload() throws Exception {
        clearBeanCache();//清空缓存
        this.init();//重新初始化
    }
    /**清空所有Bean缓存并且通知resource对象清空缓存，该方法不会导致重新装载配置了lazyInit属性的bean。*/
    public void clearBeanCache() {
        this.singletonBeanCache.clear();//清空单态缓存
        injectionFactory.run();//执行InjectionFactory对象的任务，它的任务功能是清理缓存。
        if (this.resource.isCacheBeanMetadata() == true)
            this.resource.clearCache();//清理元信息缓存
    }
    /**初始化设置了lazyInit属性为false的bean并且这些bean一定是单态的。*/
    protected void init() throws Exception {
        List<String> initBeanNames = this.resource.getStrartInitBeanDefinitionNames();
        if (initBeanNames == null)
            return;
        for (String initBN : initBeanNames)
            this.singletonBeanCache.put(initBN, this.getBean(initBN));
    }
    @Override
    public boolean containsBean(String name) {
        if (singletonBeanCache.containsKey(name) == true)
            return true;
        else
            return this.resource.containsBeanDefinition(name);
    }
    /**该方法用于忽略对bean的单态设置而强制创建一个bean的新实例。*/
    private Object getBeanForciblyo(String name, BeanDefinition definition, Object... objects) throws Exception {
        if (definition == null)
            throw new NoDefinitionException("没有定义名称为[" + name + "]的bean。");
        Object obj = this.createFactory.newInstance(definition, objects, this);//创建对象
        this.injectionFactory.ioc(obj, objects, definition, this);//执行依赖注入
        return obj;
    }
    @Override
    public Object getBean(String name, Object... objects) {
        try {
            if (singletonBeanCache.containsKey(name) == true)
                return singletonBeanCache.get(name);
            BeanDefinition definition = this.resource.getBeanDefinition(name);
            Object obj = getBeanForciblyo(name, definition, objects);
            if (definition.isSingleton() == true)
                this.singletonBeanCache.put(name, obj);
            return obj;
        } catch (Exception e) {
            if (e instanceof RuntimeException == true)
                throw (RuntimeException) e;
            throw new InvokeException(e);
        }
    }
    @Override
    public Class<?> getBeanType(String name) {
        BeanDefinition definition = this.resource.getBeanDefinition(name);
        if (definition == null)
            throw new NoDefinitionException("没有定义名称为[" + name + "]的bean。");
        String type = definition.getPropType();
        try {
            return this.loader.loadClass(type);
        } catch (Exception e) {
            throw new TypeException("无法装载类型" + type, e);
        }
    }
    @Override
    public boolean isTypeMatch(String name, Class<?> targetType) {
        return this.getBeanType(name).isAssignableFrom(targetType);
    }
    @Override
    public ClassLoader getBeanClassLoader() {
        return this.loader;
    }
    public void setLoader(ClassLoader loader) {
        this.loader = loader;
    }
    @Override
    public BeanResource getBeanResource() {
        return this.resource;
    }
    @Override
    public boolean isFactory(String name) {
        return this.resource.isFactory(name);
    }
    @Override
    public boolean isPrototype(String name) {
        return this.resource.isPrototype(name);
    }
    @Override
    public boolean isSingleton(String name) {
        return this.resource.isSingleton(name);
    }
    //===================================================================================IAttribute
    /**清空所有环境属性，但是保留this属性。*/
    @Override
    public void clearAttribute() {
        this.attribute.clearAttribute();
        if (this.attribute instanceof KeepAttDecorator == true) {
            KeepAttDecorator kad = (KeepAttDecorator) this.attribute;
            if (kad.contains("this") == true)
                kad.setKeep("this", false);
            kad.setAttribute("this", this);
            kad.setKeep("this", true);
        } else
            this.attribute.setAttribute("this", this);
    }
    @Override
    public boolean contains(String name) {
        return this.attribute.contains(name);
    }
    @Override
    public Object getAttribute(String name) {
        return this.attribute.getAttribute(name);
    }
    @Override
    public String[] getAttributeNames() {
        return this.attribute.getAttributeNames();
    }
    @Override
    public void removeAttribute(String name) {
        this.attribute.removeAttribute(name);
    }
    @Override
    public void setAttribute(String name, Object value) {
        this.attribute.setAttribute(name, value);
    }
}