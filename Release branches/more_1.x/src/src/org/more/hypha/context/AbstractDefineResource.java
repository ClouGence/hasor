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
import org.more.hypha.DefineResource;
import org.more.hypha.EventManager;
import org.more.hypha.ExpandPoint;
import org.more.hypha.ExpandPointManager;
import org.more.hypha.event.AbstractEventManager;
import org.more.hypha.expandpoint.AbstractExpandPointManager;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 数组{@link DefineResource}接口基本实现类，其子类需要实现有关{@link AbstractBeanDefine}类型的操作功能。
 * @version 2010-11-30
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractDefineResource implements DefineResource {
    private static final long  serialVersionUID   = 1420351981612281917L;
    private String             sourceName         = null;                //资源名
    private IAttribute         flashContext       = null;                //全局闪存，通过重写受保护的方法createFlash来达到植入的目的。
    private IAttribute         attributeManager   = null;                //属性管理器
    //以下字段都可以通过重写相应方法达到重写的目的。
    private EventManager       eventManager       = null;                //事件管理器
    private ExpandPointManager expandPointManager = null;                //扩展点管理器
    //========================================================================================================================IAttribute
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
    //========================================================================================================================Base
    /**获取一个状态该状态表述是否已经准备好，{@link AbstractDefineResource}类型中该方法始终返回true。*/
    public boolean isReady() {
        return true;
    };
    /**设置资源名。*/
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
    public String getSourceName() {
        return this.sourceName;
    }
    //========================================================================================================================GET
    /**获取DefineResource的属性访问接口。子类可以通过重写该方法来改变属性管理器。*/
    public final IAttribute getAttribute() {
        if (this.attributeManager == null)
            this.attributeManager = this.createAttribute();
        if (this.attributeManager == null)
            this.attributeManager = new AttBase();
        return this.attributeManager;
    }
    /**获取Flash，这个flash是一个内部信息携带体。它可以贯穿整个hypha的所有阶段。得到flash有两种办法一种是主动获取。另外一种是在特定的位置由hypha提供。*/
    public final IAttribute getFlash() {
        if (this.flashContext == null)
            this.flashContext = this.createFlash();
        if (this.flashContext == null)
            this.flashContext = new AttBase();
        return this.flashContext;
    };
    /**获取事件管理器，通过该管理器可以发送事件，事件的监听也是通过这个接口对象完成的。子类可以通过重写该方法来改变事件管理器。*/
    public final EventManager getEventManager() {
        if (this.eventManager == null)
            this.eventManager = this.getEventManager();
        if (this.eventManager == null)
            try {
                this.eventManager = new AbstractEventManager(this) {};
                this.eventManager.init(this.getFlash());
            } catch (Throwable e) {/*TODO 不会引发任何异常*/}
        return this.eventManager;
    }
    /**获取扩展点管理器，通过扩展点管理器可以检索、注册或者解除注册扩展点。有关扩展点的功能请参见{@link ExpandPoint}。子类可以通过重写该方法来改变扩展点管理器。*/
    public final ExpandPointManager getExpandPointManager() {
        if (this.expandPointManager == null)
            this.expandPointManager = this.createExpandPointManager();
        if (this.expandPointManager == null)
            try {
                this.expandPointManager = new AbstractExpandPointManager(this) {};
                this.expandPointManager.init(this.getFlash());
            } catch (Throwable e) {/*TODO 不会引发任何异常*/}
        return this.expandPointManager;
    }
    //========================================================================================================================
    /**创建一个属性管理器，如果返回空则创建默认的一个属性管理器。*/
    protected IAttribute createAttribute() {
        return null;
    };
    /**创建一个Flash，如果返回空则创建默认的一个Flash。*/
    protected IAttribute createFlash() {
        return null;
    };
    /**创建一个{@link EventManager}，如果返回空则创建默认的一个管理器。*/
    protected EventManager createEventManager() {
        return null;
    };
    /**创建一个{@link ExpandPointManager}，如果返回空则创建默认的一个管理器。*/
    protected ExpandPointManager createExpandPointManager() {
        return null;
    };
};