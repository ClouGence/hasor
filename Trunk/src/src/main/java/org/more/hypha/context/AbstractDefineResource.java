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
import java.util.Map;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.DefineResource;
import org.more.hypha.Event;
import org.more.hypha.EventManager;
import org.more.hypha.commons.AbstractEventManager;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 数组{@link DefineResource}接口基本实现类，其子类需要实现有关{@link AbstractBeanDefine}类型的操作功能。
 * @version 2010-11-30
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractDefineResource implements DefineResource {
    private static final long       serialVersionUID = 1420351981612281917L;
    private static Log              log              = LogFactory.getLog(AbstractDefineResource.class);
    private String                  sourceName       = null;                                           //资源名
    private IAttribute              flashContext     = null;                                           //全局闪存，通过重写受保护的方法createFlash来达到植入的目的。
    private ThreadLocal<IAttribute> threadFlash      = null;                                           //全局闪存，通过重写受保护的方法createFlash来达到植入的目的。
    private IAttribute              attributeManager = null;                                           //属性管理器
    //以下字段都可以通过重写相应方法达到重写的目的。
    private AbstractEventManager    eventManager     = null;                                           //事件管理器
    /*------------------------------------------------------------------------------*/
    public boolean contains(String name) {
        return this.getAttribute().contains(name);
    }
    public void setAttribute(String name, Object value) {
        this.getAttribute().setAttribute(name, value);
    }
    public Object getAttribute(String name) {
        return this.getAttribute().getAttribute(name);
    }
    public void removeAttribute(String name) {
        this.getAttribute().removeAttribute(name);
    }
    public String[] getAttributeNames() {
        return this.getAttribute().getAttributeNames();
    }
    public void clearAttribute() {
        this.getAttribute().clearAttribute();
    }
    public Map<String, Object> toMap() {
        return this.getAttribute().toMap();
    };
    /*------------------------------------------------------------------------------*/
    /**设置资源名。*/
    public void setSourceName(String sourceName) {
        log.info("change sourceName form '{%0}' to '{%1}'", this.sourceName, sourceName);
        this.sourceName = sourceName;
    }
    public String getSourceName() {
        return this.sourceName;
    }
    /*------------------------------------------------------------------------------*/
    /**获取DefineResource的属性访问接口。子类可以通过重写该方法来改变属性管理器。*/
    public final IAttribute getAttribute() {
        if (this.attributeManager == null) {
            log.info("create attributeManager...");
            this.attributeManager = this.createAttribute();
        }
        log.debug("getAttribute , att = {%0}", this.attributeManager);
        return this.attributeManager;
    }
    /**获取Flash，这个flash是一个内部信息携带体。它可以贯穿整个hypha的所有阶段。
     * 得到flash有两种办法一种是主动获取。另外一种是在特定的位置由hypha提供。不受线程限制。*/
    public final IAttribute getFlash() {
        if (this.flashContext == null) {
            log.info("create flashContext By Public...");
            this.flashContext = this.createFlash("Public");
        }
        log.debug("getFlash , flash = {%0}", this.flashContext);
        return this.flashContext;
    };
    /**获取Flash，这个flash是一个内部信息携带体。它可以贯穿整个hypha的所有阶段。
     * 得到flash有两种办法一种是主动获取。另外一种是在特定的位置由hypha提供。线程间独立。*/
    public final IAttribute getThreadFlash() {
        IAttribute att = null;
        if (this.threadFlash == null) {
            this.threadFlash = new ThreadLocal<IAttribute>();
            att = this.createFlash("Thread");
            this.threadFlash.set(att);
            log.info("create flashContext By Thread...");
        } else {
            att = this.threadFlash.get();
            if (att == null) {
                att = this.createFlash("Thread");
                log.info("create flashContext By Thread...");
                this.threadFlash.set(att);
            }
        }
        log.debug("getThreadFlash , flash = {%0}", att);
        return att;
    }
    public final EventManager getEventManager() {
        if (this.eventManager == null) {
            log.info("create EventManager ...");
            this.eventManager = this.createEventManager();
        }
        return this.eventManager;
    }
    /**抛出一个事件，如果事件中断会引发执行错误会引发*/
    protected void throwEvent(Event event, Object... params) {
        log.debug("throwEvent , event = {%0}, params = {%1}", event, params);
        this.getEventManager().doEvent(event, params);
    }
    /*------------------------------------------------------------------------------*/
    /**创建一个属性管理器，重新该方法可以替换{@link DefineResource}接口使用的Attribute对象。*/
    protected IAttribute createAttribute() {
        return new AttBase();
    };
    /**创建一个Flash，重新该方法可以替换{@link DefineResource}接口使用的Flash对象。
     * 如果参数值为‘Public’则表示创建是一个可以跨越所有线程的FLASH。
     * 如果为‘Thread’则表示创建的是一个只在当前线程里有效的FLASH*/
    protected IAttribute createFlash(String type) {
        return new AttBase();
    };
    /**创建一个{@link EventManager}并且负责初始化它，重新该方法可以替换{@link DefineResource}接口使用的{@link EventManager}对象。*/
    protected AbstractEventManager createEventManager() {
        AbstractEventManager em = new AbstractEventManager() {};
        log.info("init EventManager , manager = {%0} , initparam = {%1}.", em, this);
        em.init(this);
        return em;
    };
};