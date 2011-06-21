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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.core.error.DefineException;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ELContext;
import org.more.hypha.Event;
import org.more.hypha.EventManager;
import org.more.hypha.ExpandPointManager;
import org.more.hypha.commons.AbstractELContext;
import org.more.hypha.commons.logic.EngineLogic;
import org.more.log.ILog;
import org.more.log.LogFactory;
import org.more.util.attribute.IAttribute;
/**
 * 简单的{@link ApplicationContext}接口实现类，该类只是提供了一个平台。
 * Date : 2011-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractApplicationContext implements ApplicationContext {
    private static ILog           log             = LogFactory.getLog(AbstractApplicationContext.class);
    private PropxyClassLoader     classLoader     = null;
    //init期间必须构建的六大基础对象
    private Object                contextObject   = null;
    private AbstractELContext     elContext       = null;
    //
    private Map<String, Object>   singleBeanCache = null;
    private Map<String, Class<?>> typeCache       = null;
    //
    private EngineLogic           engineLogic     = null;
    /*------------------------------------------------------------*/
    public AbstractApplicationContext(ClassLoader classLoader) {
        this.classLoader = new PropxyClassLoader();
        this.classLoader.setLoader(classLoader);
    };
    public Object getContextObject() {
        return this.contextObject;
    };
    public void setContextObject(Object contextObject) {
        log.info("change contextObject form '{%0}' to '{%1}'", this.contextObject, contextObject);
        this.contextObject = contextObject;
    };
    public EventManager getEventManager() {
        return this.getBeanResource().getEventManager();
    };
    public ExpandPointManager getExpandPointManager() {
        return this.getBeanResource().getExpandPointManager();
    };
    public ELContext getELContext() {
        return this.elContext;
    };
    public ClassLoader getBeanClassLoader() {
        return this.classLoader;
    };
    /**替换当前的ClassLoader。*/
    public void setBeanClassLoader(ClassLoader loader) {
        log.info("change ParentClassLoader form '{%0}' to '{%1}'", this.classLoader.getLoader(), loader);
        this.classLoader.setLoader(loader);
    };
    public EngineLogic getEngineLogic() {
        return this.engineLogic;
    };
    /*------------------------------------------------------------*/
    /**清理掉{@link AbstractApplicationContext}对象中所缓存的单例Bean对象。*/
    public void clearSingleBean() {
        log.info("clear all Single Bean!");
        this.singleBeanCache.clear();
    };
    /**获取一个int该int表示了{@link AbstractApplicationContext}对象中已经缓存了的单例对象数目。*/
    public int getCacheBeanCount() {
        return this.singleBeanCache.size();
    };
    public abstract AbstractDefineResource getBeanResource();
    /**在init期间被调用，子类可以重写它用来替换EL上下文。*/
    protected AbstractELContext createELContext() {
        return new AbstractELContext() {};
    };
    /**该方法可以获取{@link AbstractApplicationContext}接口对象所使用的属性管理器。子类可以通过重写该方法以来控制属性管理器对象。*/
    protected IAttribute getAttribute() {
        return this.getBeanResource();
    };
    /**获取Flash，这个flash是一个内部信息携带体。它可以贯穿整个hypha的所有阶段。而且不受跨线程限制。*/
    public IAttribute getFlash() {
        return this.getBeanResource().getFlash();
    };
    /**获取Flash，这个flash是一个内部信息携带体。它可以贯穿整个hypha的所有阶段。但是这个FLASH受跨线程限制。*/
    public IAttribute getThreadFlash() {
        return this.getBeanResource().getThreadFlash();
    };
    /*------------------------------------------------------------*/
    public void init() {
        log.info("starting init ApplicationContext...");
        this.singleBeanCache = new HashMap<String, Object>();
        this.typeCache = new HashMap<String, Class<?>>();
        log.info("created cache, singleBeanCache = {%0}, typeCache = {%1}.", singleBeanCache, typeCache);
        //
        this.elContext = this.createELContext();
        this.engineLogic = new EngineLogic();
        log.info("created elContext = {%0}, engineLogic = {%1}", elContext, engineLogic);
        //
        this.elContext.init(this);
        this.engineLogic.init(this);
        log.info("inited elContext and engineLogic OK!");
        //
        log.info("sending event...");
        this.getEventManager().doEvent(Event.getEvent(InitEvent.class), this);
        this.getEventManager().doEvent(Event.getEvent(InitedEvent.class), this);
        log.info("started!");
    };
    /**当JVM回收该对象时自动调用销毁方法。*/
    protected void finalize() throws Throwable {
        if (this.engineLogic != null)
            try {
                log.info("use finalize destroy ApplicationContext...");
                this.destroy();
            } catch (Exception e) {
                log.warning("use finalize destroy ApplicationContext an error , error = {%0}", e);
            }
        super.finalize();
    };
    public void destroy() {
        /**销毁事件*/
        log.info("sending Context Destroy event...");
        this.getEventManager().doEvent(Event.getEvent(DestroyEvent.class), this);
        log.info("popEvent all event...");
        this.getEventManager().popEvent();//弹出所有事件
        //
        log.info("set null...");
        this.elContext = null;
        this.singleBeanCache = null;
        this.typeCache = null;
        this.engineLogic = null;
        log.info("destroy is OK!");
    };
    /*------------------------------------------------------------*/
    private final String KEY = "GETBEAN_PARAM";
    @SuppressWarnings("unchecked")
    public <T> T getBean(String defineID, Object... objects) throws Throwable {
        if (defineID == null || defineID.equals("") == true) {
            log.error("error , defineID is null or empty.");
            throw new NullPointerException("error , defineID is null or empty.");
        }
        if (this.singleBeanCache.containsKey(defineID) == true) {
            Object obj = this.singleBeanCache.get(defineID);
            log.info("{%0} bean form cache return.", defineID);
            return (T) obj;
        }
        AbstractBeanDefine define = this.getBeanDefinition(defineID);
        if (define == null) {
            log.error("{%0} define is not exist.", defineID);
            throw new DefineException(defineID + " define is not exist.");
        }
        //-------------------------------------------------------------------获取
        log.info("start building {%0} bean , params is {%1}", defineID, objects);
        try {
            this.getThreadFlash().setAttribute(KEY, objects);
            log.debug("put params to ThreadFlash key is {%0}", KEY);
            //1.获取bean以及bean类型。
            Class<?> beanType = this.getBeanType(defineID, objects);//获取类型
            Object bean = this.engineLogic.builderBean(define, objects);//生成Bean
            log.info("finish build!, object = {%0}", bean);
            //3.单态缓存&类型匹配
            if (beanType != null)
                /**检测对象类型是否匹配定义类型，如果没有指定beanType参数则直接返回。*/
                bean = beanType.cast(bean);
            log.debug("bean cast type {%0} OK!", beanType);
            if (define.isSingleton() == true) {
                log.info("{%0} bean is Singleton!", defineID);
                this.singleBeanCache.put(defineID, bean);
            }
            return (T) bean;
        } catch (Throwable e) {
            log.error("get bean is error, error = {%0}", e);
            throw e;
        } finally {
            log.debug("remove params from ThreadFlash key is {%0}", KEY);
            this.getThreadFlash().removeAttribute(KEY);
        }
    };
    public Class<?> getBeanType(String defineID, Object... objects) throws Throwable {
        if (defineID == null || defineID.equals("") == true) {
            log.error("error , defineID is null or empty.");
            throw new NullPointerException("error , defineID is null or empty.");
        }
        if (this.typeCache.containsKey(defineID) == true) {
            Class<?> type = this.typeCache.get(defineID);
            log.info("{%0} bean type form cache return.", defineID);
            return type;
        }
        AbstractBeanDefine define = this.getBeanDefinition(defineID);
        if (define == null) {
            log.error("{%0} define is not exist.", defineID);
            throw new DefineException(defineID + " define is not exist.");
        }
        //-------------------------------------------------------------------获取
        log.info("start building {%0} bean type , params is {%1}", defineID, objects);
        try {
            this.getThreadFlash().setAttribute(KEY, objects);
            log.debug("put params to ThreadFlash key is {%0}", KEY);
            Class<?> beanType = this.engineLogic.builderType(define, objects);
            log.info("finish build! type = {%0}", beanType);
            this.typeCache.put(defineID, beanType);
            return beanType;
        } catch (Throwable e) {
            log.error("get BeanType is error, error = {%0}", e);
            throw e;
        } finally {
            log.debug("remove params from ThreadFlash key is {%0}", KEY);
            this.getThreadFlash().removeAttribute(KEY);
        }
    };
    /**返回当前线程getBean时传入的所有参数，如果在getBean返回之后使用该方法。将会返回一个null。*/
    public Object[] getGetBeanParams() {
        Object[] params = null;
        IAttribute tflash = this.getThreadFlash();
        if (tflash.contains(KEY) == false)
            return null;
        params = (Object[]) tflash.getAttribute(KEY);
        log.debug("GetBean params KEY = {%0}, params = {%1}", KEY, params);
        return params.clone();//使用克隆以防止外部操作数组对象。
    }
    /**返回当前线程getBean时传入的指定位置参数，如果在getBean返回之后使用该方法。将会返回一个null。*/
    public Object getGetBeanParam(int index) {
        IAttribute tflash = this.getThreadFlash();
        Object[] params = (Object[]) tflash.getAttribute(KEY);
        if (params == null) {
            log.debug("GetBean params {%0} is null.");
            return null;
        }
        if (index < 0 || index > params.length) {
            log.debug("GetBean params [{%0}] index out of bounds.", index);
            return null;
        }
        Object obj = params[index];
        log.debug("GetBean params index = {%0}, value = {%1}", index, obj);
        return obj;
    }
    /*------------------------------------------------------------*/
    public List<String> getBeanDefinitionIDs() {
        return this.getBeanResource().getBeanDefinitionIDs();
    };
    public AbstractBeanDefine getBeanDefinition(String id) {
        return this.getBeanResource().getBeanDefine(id);
    };
    public boolean containsBean(String id) {
        return this.getBeanResource().containsBeanDefine(id);
    };
    public boolean isPrototype(String id) throws DefineException {
        return this.getBeanResource().isPrototype(id);
    };
    public boolean isSingleton(String id) throws DefineException {
        return this.getBeanResource().isSingleton(id);
    };
    public boolean isFactory(String id) throws DefineException {
        return this.getBeanResource().isFactory(id);
    };
    public boolean isTypeMatch(String id, Class<?> targetType) throws Throwable {
        //Object.class.isAssignableFrom(XmlTest.class); return true;
        if (id == null || targetType == null)
            throw new NullPointerException("参数id或targetType不能为空.");
        Class<?> beanType = this.getBeanType(id);
        return targetType.isAssignableFrom(beanType);
    };
    /*------------------------------------------------------------*/
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
    public Map<String, Object> toMap() {
        return this.getAttribute().toMap();
    };
};