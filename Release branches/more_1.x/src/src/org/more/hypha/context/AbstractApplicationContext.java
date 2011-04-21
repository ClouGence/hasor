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
import java.util.Map;
import org.more.NoDefinitionException;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ELContext;
import org.more.hypha.EventManager;
import org.more.hypha.ExpandPoint;
import org.more.hypha.ExpandPointManager;
import org.more.hypha.ScopeContext;
import org.more.hypha.ScriptContext;
import org.more.hypha.commons.AbstractScopeContext;
import org.more.hypha.commons.AbstractScriptContext;
import org.more.hypha.el.AbstractELContext;
import org.more.hypha.event.AbstractEventManager;
import org.more.hypha.expandpoint.AbstractExpandPointManager;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 简单的{@link ApplicationContext}接口实现类。
 * Date : 2011-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractApplicationContext implements ApplicationContext {
    private ClassLoader        classLoader        = null; //Context的类装载器
    //可延迟可替换
    private IAttribute         attributeContext   = null; //属性集
    private IAttribute         flashContext       = null; //全局FLASH
    //init期间必须构建的六大基础对象
    private Object             contextObject      = null;
    private EventManager       eventManager       = null;
    private ExpandPointManager expandPointManager = null;
    private ELContext          elContext          = null;
    private ScopeContext       scopeContext       = null;
    private ScriptContext      scriptContext      = null;
    /*------------------------------------------------------------*/
    public Object getContextObject() {
        return this.contextObject;
    }
    public void setContextObject(Object contextObject) {
        this.contextObject = contextObject;
    }
    /**获取事件管理器，通过该管理器可以发送事件，事件的监听也是通过这个接口对象完成的。*/
    public final EventManager getEventManager() {
        if (this.eventManager == null)
            this.eventManager = this.createEventManager();
        if (this.eventManager == null)
            try {
                this.eventManager = new AbstractEventManager(this.getBeanResource()) {};
                this.eventManager.init(this.getFlash());
            } catch (Throwable e) {/*TODO 不会引发任何异常*/}
        return this.eventManager;
    }
    /**获取扩展点管理器，通过扩展点管理器可以检索、注册或者解除注册扩展点。有关扩展点的功能请参见{@link ExpandPoint}*/
    public final ExpandPointManager getExpandPointManager() {
        if (this.expandPointManager == null)
            this.expandPointManager = this.createExpandPointManager();
        if (this.expandPointManager == null)
            try {
                this.expandPointManager = new AbstractExpandPointManager(this.getBeanResource()) {};
                this.expandPointManager.init(this.getFlash());
            } catch (Throwable e) {/*TODO 不会引发任何异常*/}
        return this.expandPointManager;
    }
    /**获取EL执行器。*/
    public final ELContext getELContext() {
        if (this.elContext == null)
            this.elContext = this.createELContext();
        if (this.elContext == null)
            try {
                this.elContext = new AbstractELContext(this) {};
                this.elContext.init(this.getFlash());
            } catch (Throwable e) {/*TODO 不会引发任何异常*/}
        return this.elContext;
    }
    /**作用域管理器。*/
    public final ScopeContext getScopeContext() {
        if (this.scopeContext == null)
            this.scopeContext = this.createScopeContext();
        if (this.scopeContext == null)
            try {
                this.scopeContext = new AbstractScopeContext(this) {};
                this.scopeContext.init(this.getFlash());
            } catch (Throwable e) {/*TODO 不会引发任何异常*/}
        return this.scopeContext;
    }
    /**脚本执行管理器。*/
    public final ScriptContext getScriptContext() {
        if (this.scriptContext == null)
            this.scriptContext = this.createScriptContext();
        if (this.scriptContext == null)
            try {
                this.scriptContext = new AbstractScriptContext(this) {};
                this.scriptContext.init(this.getFlash());
            } catch (Throwable e) {/*TODO 不会引发任何异常*/}
        return this.scriptContext;
    }
    /*------------------------------------------------------------*/
    protected EventManager createEventManager() {
        return this.getBeanResource().getEventManager();
    };
    private ExpandPointManager createExpandPointManager() {
        return this.getBeanResource().getExpandPointManager();
    };
    protected ELContext createELContext() {
        return null;
    };
    private ScopeContext createScopeContext() {
        return null;
    };
    private ScriptContext createScriptContext() {
        return null;
    };
    /**获取全局属性闪存，子类可以通过重写该方法来替换FLASH。*/
    protected IAttribute getFlash() {
        if (this.flashContext == null)
            this.flashContext = new AttBase();
        return this.flashContext;
    };
    /**该方法可以获取{@link AbstractApplicationContext}接口对象所使用的属性管理器。子类可以通过重写该方法以来控制属性管理器对象。*/
    protected IAttribute getAttribute() {
        if (this.attributeContext == null)
            this.attributeContext = this.getBeanResource();
        return this.attributeContext;
    };
    /**设置属性管理器对象。*/
    protected void setAttributeContext(IAttribute attributeContext) {
        this.attributeContext = attributeContext;
    }
    /**设置flash管理器对象。*/
    protected void setFlashContext(IAttribute flashContext) {
        this.flashContext = flashContext;
    }
    /*------------------------------------------------------------*/
    /**该方法由子类决定如何创建目标Bean。*/
    protected abstract Object builderBean(AbstractBeanDefine define, Object[] params) throws Throwable;
    protected abstract Class<?> builderType(AbstractBeanDefine define, Object[] params) throws Throwable;
    //
    public final Object getBean(String id, Object... objects) throws Throwable {
        final String KEY = "GETBEAN_PARAM";
        try {
            this.getFlash().setAttribute(KEY, objects);
            AbstractBeanDefine define = this.getBeanDefinition(id);
            return this.builderBean(define, objects);
        } catch (Throwable e) {
            throw e;
        } finally {
            this.getFlash().removeAttribute(KEY);
        }
    };
    public final Class<?> getBeanType(String id, Object... objects) throws Throwable {
        AbstractBeanDefine define = this.getBeanDefinition(id);
        return this.builderType(define, objects);
    };
    /**设置ClassLoader，通常在初始化之前进行设置。*/
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    };
    public List<String> getBeanDefinitionIDs() {
        return this.getBeanResource().getBeanDefinitionIDs();
    };
    public AbstractBeanDefine getBeanDefinition(String id) throws NoDefinitionException {
        return this.getBeanResource().getBeanDefine(id);
    };
    public ClassLoader getBeanClassLoader() {
        if (this.classLoader == null)
            return ClassLoader.getSystemClassLoader();
        return this.classLoader;
    };
    public boolean containsBean(String id) {
        return this.getBeanResource().containsBeanDefine(id);
    };
    public boolean isPrototype(String id) throws NoDefinitionException {
        return this.getBeanResource().isPrototype(id);
    };
    public boolean isSingleton(String id) throws NoDefinitionException {
        return this.getBeanResource().isSingleton(id);
    };
    public boolean isFactory(String id) throws NoDefinitionException {
        return this.getBeanResource().isFactory(id);
    };
    public boolean isTypeMatch(String id, Class<?> targetType) throws Throwable {
        //Object.class.isAssignableFrom(XmlTest.class); return true;
        if (targetType == null)
            throw new NullPointerException("参数targetType不能为空.");
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
    /*------------------------------------------------------------*/
};