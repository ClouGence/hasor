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
package org.more.webui.support;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.core.error.MoreDataException;
import org.more.core.event.AbstractEventManager;
import org.more.core.event.Event;
import org.more.core.event.EventListener;
import org.more.core.event.EventManager;
import org.more.core.ognl.OgnlException;
import org.more.util.BeanUtil;
import org.more.webui.context.ViewContext;
import org.more.webui.support.values.AbstractValueHolder;
import org.more.webui.support.values.ExpressionValueHolder;
import org.more.webui.support.values.StaticValueHolder;
/**
* 所有组件的根，这里拥有组件的所有关键方法。
* @version : 2011-8-4
* @author 赵永春 (zyc@byshell.org)
*/
public abstract class UIComponent {
    private String                           componentID         = null;
    private String                           componentPath       = null;
    private UIComponent                      parent              = null;
    private List<UIComponent>                components          = new ArrayList<UIComponent>();
    /**私有事件管理器，该事件时间线不会受到其他组件影响*/
    private EventManager                     privateEventManager = new AbstractEventManager() {};
    private Map<String, AbstractValueHolder> propertys           = new HashMap<String, AbstractValueHolder>();
    private Map<String, Object>              atts                = new HashMap<String, Object>();
    /*-------------------------------------------------------------------------------get/set属性*/
    /**返回组件的ID*/
    public String getComponentID() {
        return componentID;
    }
    /**设置属性ID*/
    public void setComponentID(String componentID) {
        this.componentID = componentID;
    }
    /**通用属性表*/
    public static enum Propertys {
        /**客户端在请求之前进行的调用，返回false取消本次ajax请求（R）*/
        beforeScript,
        /**客户端脚本回调函数（R）*/
        afterScript,
        /**调用错误回调函数（R）*/
        errorScript,
        /**Ajax是否使用同步操作（R）*/
        async,
        /**表示是否渲染（-）*/
        render,
        /**表示是否渲染子组建（-）*/
        renderChildren,
    };
    public String getBeforeScript() {
        return this.getProperty(Propertys.beforeScript.name()).valueTo(String.class);
    }
    @NoState
    public void setBeforeScript(String beforeScript) {
        this.getProperty(Propertys.beforeScript.name()).value(beforeScript);
    }
    public String getAfterScript() {
        return this.getProperty(Propertys.afterScript.name()).valueTo(String.class);
    }
    @NoState
    public void setAfterScript(String afterScript) {
        this.getProperty(Propertys.afterScript.name()).value(afterScript);
    }
    public String getErrorScript() {
        return this.getProperty(Propertys.errorScript.name()).valueTo(String.class);
    }
    @NoState
    public void setErrorScript(String errorScript) {
        this.getProperty(Propertys.errorScript.name()).value(errorScript);
    }
    public boolean isAsync() {
        return this.getProperty(Propertys.async.name()).valueTo(Boolean.TYPE);
    }
    @NoState
    public void setAsync(boolean async) {
        this.getProperty(Propertys.async.name()).value(async);
    }
    /**返回一个boolean值，该值决定是否渲染该组件*/
    @NoState
    public boolean isRender() {
        return this.getProperty(Propertys.render.name()).valueTo(Boolean.TYPE);
    };
    /**设置一个boolean值，该值决定是否渲染该组件*/
    @NoState
    public void setRender(boolean isRender) {
        this.getProperty(Propertys.render.name()).value(isRender);
    };
    /**返回一个boolean值，该值决定是否渲染该组件的子组建。*/
    @NoState
    public boolean isRenderChildren() {
        return this.getProperty(Propertys.renderChildren.name()).valueTo(Boolean.TYPE);
    }
    /**设置一个boolean值，该值决定是否渲染该组件的子组建。*/
    @NoState
    public void setRenderChildren(boolean isRenderChildren) {
        this.getProperty(Propertys.renderChildren.name()).value(isRenderChildren);
    }
    /*-------------------------------------------------------------------------------核心方法*/
    /**获取用于附加的属性的Map对象*/
    public Map<String, Object> getAtts() {
        return atts;
    };
    /**获取组建类型*/
    public abstract String getComponentType();
    /**获取组建在组建树中的位置格式为：/1/3/4/2*/
    public String getComponentPath() {
        if (this.componentPath == null) {
            StringBuffer buffer = new StringBuffer();
            UIComponent target = this;
            UIComponent targetParent = target.getParent();
            while (targetParent != null) {
                int index = targetParent.getChildren().indexOf(target);
                buffer.append('/');
                buffer.append(new StringBuffer(String.valueOf(index)).reverse());
                //
                target = targetParent;
                targetParent = target.getParent();
            }
            this.componentPath = buffer.reverse().toString();
        }
        return this.componentPath;
    }
    /**获取一个可用的客户端ID*/
    public String getClientID(ViewContext viewContext) {
        if (this.getComponentID() != null)
            return getComponentID();
        else
            return "uiCID_" + viewContext.getComClientID(this);
    }
    public UIComponent getChildByPath(String componentPath) {
        if (componentPath == null || componentPath.equals("") == true)
            return null;
        if (componentPath.startsWith(this.getComponentPath()) == false)
            return null;//判断要获取的目标不是自己的孩子
        String targetPath = componentPath.substring(this.getComponentPath().length());
        //
        int firstSpan = targetPath.indexOf('/');
        int index = Integer.parseInt(targetPath.substring(0, firstSpan));
        return this.getChildren().get(index);
    }
    /**在当前组件的子级中寻找某个特定ID的组件*/
    public UIComponent getChildByID(String componentID) {
        if (componentID == null)
            return null;
        if (this.getComponentID().equals(componentID) == true)
            return this;
        for (UIComponent component : this.components) {
            UIComponent com = component.getChildByID(componentID);
            if (com != null)
                return com;
        }
        return null;
    };
    /**获取一个int，该值表明当前组件中共有多少个子元素*/
    public int getChildCount() {
        return this.components.size();
    };
    /**获取一个元素集合，该集合是存放子组件的场所*/
    public List<UIComponent> getChildren() {
        return Collections.unmodifiableList(this.components);
    };
    /**获取一个组建列表该列表中包含了该组建以及该组建的所有子组建。*/
    public List<UIComponent> getALLChildren() {
        ArrayList<UIComponent> list = new ArrayList<UIComponent>();
        list.add(this);
        for (UIComponent uic : components)
            list.addAll(uic.getALLChildren());
        return list;
    };
    /**添加子组建*/
    public void addChildren(UIComponent componentItem) {
        componentItem.setParent(this);
        this.components.add(componentItem);
    };
    /**获取组建的父级。*/
    public UIComponent getParent() {
        return this.parent;
    };
    /**设置组建的父级别。*/
    private void setParent(UIComponent parent) {
        this.parent = parent;
    }
    /**获取保存属性的集合。*/
    public Map<String, AbstractValueHolder> getPropertys() {
        return this.propertys;
    };
    /**获取用于表示组件属性对象。*/
    public AbstractValueHolder getProperty(String propertyName) {
        AbstractValueHolder value = this.getPropertys().get(propertyName);
        if (value == null)
            return new StaticValueHolder();
        return value;
    };
    /**添加一个EL形式的组建。属性参数readString、writeString分别对应了业务组建的读写属性。*/
    public void setPropertyEL(String propertyName, String readString, String writeString) {
        AbstractValueHolder value = this.getPropertys().get(propertyName);
        ExpressionValueHolder elValueHolder = null;
        if (value == null || value instanceof ExpressionValueHolder == false)
            elValueHolder = new ExpressionValueHolder(readString, writeString);
        this.getPropertys().put(propertyName, elValueHolder);
    };
    /**该方法会将elString参数会作为readString和、writeString。*/
    public void setPropertyEL(String propertyName, String elString) {
        this.setPropertyEL(propertyName, elString, elString);
    };
    /**设置用于表示组件属性的字符串。*/
    public void setProperty(String propertyName, Object newValue) {
        AbstractValueHolder value = this.getPropertys().get(propertyName);
        if (value == null)
            value = new StaticValueHolder(newValue);
        this.getPropertys().put(propertyName, value);
    };
    /**将map中的属性全部安装到当前组建上*/
    public void setupPropertys(Map<String, Object> objMap) {
        if (objMap != null)
            for (String key : this.propertys.keySet())
                if (objMap.containsKey(key) == true) {
                    AbstractValueHolder vh = this.propertys.get(key);
                    Object newValue = objMap.get(key);
                    vh.value(newValue);
                }
    };
    /*-------------------------------------------------------------------------------生命周期*/
    /**子类可以通过该方法初始化组件。*/
    protected void initUIComponent(ViewContext viewContext) {
        /*设置属性默认值，当页面中有值被设置的时候这里设置的默认值就会失效*/
        this.setProperty(Propertys.beforeScript.name(), "true");
        this.setProperty(Propertys.async.name(), true);//默认使用异步操作事件
        this.setProperty(Propertys.render.name(), true);
        this.setProperty(Propertys.renderChildren.name(), true);
    };
    /**组建被初始化标记*/
    private Boolean doInit = false;
    /**第1阶段，处理初始化阶段，该阶段负责初始化组件。*/
    public final void processInit(ViewContext viewContext) {
        if (this.doInit == false) {
            this.initUIComponent(viewContext);
            this.doInit = true;
        }
        /*重置属性，重置属性会保证每个生命周期内的属性值是由UI中定义的原始值。*/
        for (AbstractValueHolder vh : this.propertys.values())
            vh.reset();
        for (UIComponent com : this.components)
            com.processInit(viewContext);
    };
    /**第3阶段，将请求参数中与属性名一致的属性灌入属性上。*/
    public void processApplyRequest(ViewContext viewContext) {
        /*将请求参数中要求灌入的属性值灌入到属性上*/
        for (String key : this.propertys.keySet()) {
            /*被灌入的属性名，请求参数中必须是“componentID:attName”*/
            String[] newValues = viewContext.getHttpRequest().getParameterValues(this.getComponentPath() + ":" + key);
            if (newValues == null)
                continue;
            else if (newValues.length == 1)
                this.propertys.get(key).value(newValues[0]);
            else
                this.propertys.get(key).value(newValues);
        }
        for (UIComponent com : this.components)
            com.processApplyRequest(viewContext);
    };
    /**第4阶段，该阶段用于提供一组验证数据的合法性。*/
    public void processValidate(ViewContext viewContext) {
        for (UIComponent com : this.components)
            com.processValidate(viewContext);
    };
    /**第5阶段，将组件模型中的新值应用到，Bean*/
    public void processUpdate(ViewContext viewContext) throws OgnlException {
        /*更新所有注册到propertys中的属性值*/
        for (String key : this.propertys.keySet()) {
            AbstractValueHolder vh = this.propertys.get(key);
            if (vh.isUpdate() == true)
                vh.updateModule(this, viewContext);
        }
        for (UIComponent com : this.components)
            com.processUpdate(viewContext);
    };
    /**第6阶段，处理Action动作和客户端回传的事件*/
    public void processApplication(ViewContext viewContext) throws OgnlException {
        if (this.getComponentPath().equals(viewContext.getTargetPath()) == true) {
            /*处理客户端引发的事*/
            Event eventType = Event.getEvent(viewContext.getEvent());
            if (eventType != null)
                /**事件请求*/
                this.doEvent(eventType, viewContext);
        }
        for (UIComponent com : this.components)
            com.processApplication(viewContext);
    };
    /*-------------------------------------------------------------------------------事件响应*/
    /**执行事件*/
    protected void doEvent(Event eventType, ViewContext viewContext) {
        this.privateEventManager.doEvent(eventType, this, viewContext);
    };
    /**发出事件，该事件会被发送到私有事件管理器中。*/
    protected void pushEvent(Event eventType, Object... objects) {
        this.privateEventManager.pushEvent(eventType, objects);
    };
    /**通知事件管理器抛出某一个类型的事件。*/
    protected void popEvent(Event event) {
        this.privateEventManager.popEvent(event);
    };
    /**添加一种类型事件的事件监听器。*/
    public void addEventListener(Event eventType, EventListener listener) {
        this.privateEventManager.addEventListener(eventType, listener);
    };
    /**获取私有事件管理器。*/
    protected EventManager getEventManager() {
        return this.privateEventManager;
    };
    /*-------------------------------------------------------------------------------状态处理*/
    /**从状态数据中恢复组建状态*/
    public void restoreState(List<?> stateData) {
        //1.数据检查
        if (stateData == null)
            return;
        if (stateData.size() == 0)
            throw new MoreDataException("WebUI无法重塑组件状态，在重塑组件[" + this.getComponentID() + "]组件发生数据丢失");
        //2.恢复自身数据
        Map<String, Object> mineState = (Map<String, Object>) stateData.get(0);
        for (String propName : mineState.keySet()) {
            /*排除错误*/
            if (propName == null)
                continue;
            /*ID属性不处理*/
            if (propName.toLowerCase().equals("id") == true)
                continue;
            /*处理注解*/
            Method rm = BeanUtil.getWriteMethod(propName, this.getClass());
            if (rm == null)
                continue;
            if (rm.getAnnotation(NoState.class) != null)
                continue;
            /*写入属性*/
            AbstractValueHolder vh = this.propertys.get(propName);
            if (vh != null)
                vh.value(mineState.get(propName));
        }
        //3.恢复子组件
        if (stateData.size() == 2) {
            Map<String, Object> childrenState = (Map<String, Object>) stateData.get(1);
            for (UIComponent com : components)
                com.restoreState((List<?>) childrenState.get(com.getComponentPath()));
        }
    };
    /**保存组建的当前状态，不包含子组建。*/
    public List<Object> saveStateOnlyMe() {
        //1.持久化自身的状态
        HashMap<String, Object> mineState = new HashMap<String, Object>();
        for (String propName : this.propertys.keySet()) {
            Method rm = BeanUtil.getReadMethod(propName, this.getClass());
            if (rm == null)
                continue;
            if (rm.getAnnotation(NoState.class) != null)
                continue;
            AbstractValueHolder vh = this.propertys.get(propName);
            mineState.put(propName, vh.value());
        }
        //3.返回持久化状态
        ArrayList<Object> array = new ArrayList<Object>();
        array.add(mineState);
        return array;
    };
    /**保存组建的当前状态，包含子组建。*/
    public List<Object> saveState() {
        //1.持久化自身的状态
        List<Object> array = this.saveStateOnlyMe();
        //2.持久化子组件的状态
        HashMap<String, Object> childrenState = new HashMap<String, Object>();
        for (UIComponent com : components)
            childrenState.put(com.getComponentPath(), com.saveState());
        //3.返回持久化状态
        array.add(childrenState);
        return array;
    };
};