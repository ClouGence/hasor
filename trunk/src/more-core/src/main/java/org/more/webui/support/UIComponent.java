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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.more.core.error.MoreDataException;
import org.more.core.event.AbstractEventManager;
import org.more.core.event.Event;
import org.more.core.event.EventListener;
import org.more.core.event.EventManager;
import org.more.core.ognl.OgnlException;
import org.more.webui.context.ViewContext;
import org.more.webui.support.values.AbstractValueHolder;
import org.more.webui.support.values.ExpressionValueHolder;
import org.more.webui.support.values.MethodExpression;
import org.more.webui.support.values.StaticValueHolder;
/**
* 所有组件的根，这里拥有组件的所有关键方法。
* @version : 2011-8-4
* @author 赵永春 (zyc@byshell.org)
*/
public abstract class UIComponent {
    private String                           componentID         = null;
    //    private String                           clientID            = null;
    private UIComponent                      parent              = null;
    private List<UIComponent>                components          = new ArrayList<UIComponent>();
    /**私有事件管理器，该事件时间线不会受到其他组件影响*/
    private EventManager                     privateEventManager = new AbstractEventManager() {};
    private Map<String, AbstractValueHolder> propertys           = new HashMap<String, AbstractValueHolder>();
    /*-------------------------------------------------------------------------------get/set属性*/
    /**返回组件的ID*/
    public String getId() {
        return this.componentID;
    };
    /**设置属性ID*/
    public void setId(String componentID) {
        this.componentID = componentID;
    };
    public String getClientID(ViewContext viewContext) {
        //        if (clientID == null)
        return "uiCID_" + viewContext.getComClientID(this);
        //        return clientID;
    }
    /**通用属性表*/
    public static enum Propertys {
        /**客户端在请求之前进行的调用，返回false取消本次ajax请求*/
        beforeScript,
        /**客户端脚本回调函数*/
        afterScript,
        /**调用错误回调函数*/
        errorScript,
        /**Ajax是否使用同步操作*/
        async,
        /**Action动作*/
        actionEL,
        /**表示是否渲染*/
        isRender,
        /**表示是否渲染子组建*/
        isRenderChildren,
    };
    public String getBeforeScript() {
        return this.getProperty(Propertys.beforeScript.name()).valueTo(String.class);
    }
    public void setBeforeScript(String beforeScript) {
        this.getProperty(Propertys.beforeScript.name()).value(beforeScript);
    }
    public String getAfterScript() {
        return this.getProperty(Propertys.afterScript.name()).valueTo(String.class);
    }
    public void setAfterScript(String afterScript) {
        this.getProperty(Propertys.afterScript.name()).value(afterScript);
    }
    public String getErrorScript() {
        return this.getProperty(Propertys.errorScript.name()).valueTo(String.class);
    }
    public void setErrorScript(String errorScript) {
        this.getProperty(Propertys.errorScript.name()).value(errorScript);
    }
    public boolean isAsync() {
        return this.getProperty(Propertys.async.name()).valueTo(Boolean.TYPE);
    }
    public void setAsync(boolean async) {
        this.getProperty(Propertys.async.name()).value(async);
    }
    /**返回一个boolean值，该值决定是否渲染该组件*/
    public boolean isRender() {
        return this.getProperty(Propertys.isRender.name()).valueTo(Boolean.TYPE);
    };
    /**设置一个boolean值，该值决定是否渲染该组件*/
    public void setRender(boolean isRender) {
        this.getProperty(Propertys.isRender.name()).value(isRender);
    };
    /**返回一个boolean值，该值决定是否渲染该组件的子组建。*/
    public boolean isRenderChildren() {
        return this.getProperty(Propertys.isRenderChildren.name()).valueTo(Boolean.TYPE);
    }
    /**设置一个boolean值，该值决定是否渲染该组件的子组建。*/
    public void setRenderChildren(boolean isRenderChildren) {
        this.getProperty(Propertys.isRenderChildren.name()).value(isRenderChildren);
    }
    /**获取Action EL字符串*/
    public String getActionEL() {
        return this.getProperty(Propertys.actionEL.name()).valueTo(String.class);
    }
    /**设置Action EL字符串*/
    public void setActionEL(String action) {
        this.getProperty(Propertys.actionEL.name()).value(action);
    }
    /*-------------------------------------------------------------------------------核心方法*/
    /**在当前组件的子级中寻找某个特定ID的组件*/
    public UIComponent getChildByID(String componentID) {
        if (componentID == null)
            return null;
        if (this.getId().equals(componentID) == true)
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
    public Iterator<UIComponent> getChildren() {
        return this.components.iterator();
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
    }
    /**获取组建的父级。*/
    public UIComponent getParent() {
        return this.parent;
    }
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
    }
    /*-------------------------------------------------------------------------------生命周期*/
    /**子类可以通过该方法初始化组件。*/
    protected void initUIComponent(ViewContext viewContext) {
        /*设置属性默认值，当页面中有值被设置的时候这里设置的默认值就会失效*/
        //        this.clientID = null;
        this.setProperty(Propertys.beforeScript.name(), "true");
        this.setProperty(Propertys.async.name(), true);//默认使用异步操作
        this.setProperty(Propertys.isRender.name(), true);
        this.setProperty(Propertys.isRenderChildren.name(), true);
        this.setProperty(Propertys.actionEL.name(), null);
    };
    /**第1阶段，处理初始化阶段，该阶段负责初始化组件。*/
    public void processInit(ViewContext viewContext) {
        this.initUIComponent(viewContext);
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
            String newValue = viewContext.getHttpRequest().getParameter(this.getId() + ":" + key);
            if (newValue != null)
                this.propertys.get(key).value(newValue);
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
        if (this.getId().equals(viewContext.getTarget()) == true) {
            /*处理客户端引发的事*/
            if (viewContext.getEvent() != null)
                /**事件请求*/
                this.doEvent(viewContext);
            else
                /**Action请求*/
                this.doAction(viewContext);//处理
        }
        for (UIComponent com : this.components)
            com.processApplication(viewContext);
    };
    /*-------------------------------------------------------------------------------事件响应*/
    /**执行事件*/
    protected void doEvent(ViewContext viewContext) {
        Event eventType = Event.getEvent(viewContext.getEvent());
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
    /*-------------------------------------------------------------------------------Action调用处理*/
    /**执行action调用*/
    protected void doAction(ViewContext viewContext) throws OgnlException {
        MethodExpression me = this.getActionExpression();
        if (me != null)
            me.execute(this, viewContext);
    };
    public MethodExpression getActionExpression() {
        String actionString = this.getActionEL();
        if (actionString == null || actionString.equals("")) {} else
            return new MethodExpression(actionString);
        return null;
    }
    /*-------------------------------------------------------------------------------状态处理*/
    /**从状态数据中恢复状态*/
    public void restoreState(Object[] stateData) {
        //1.数据检查
        if (stateData == null)
            return;
        if (stateData.length != 2)
            throw new MoreDataException("WebUI无法重塑组件状态，在重塑组件[" + this.getId() + "]组件发生数据丢失");
        //2.恢复自身数据
        Map<String, Object> mineState = (Map<String, Object>) stateData[0];
        for (String propName : mineState.keySet())
            /*处理除ID之外的所有属性*/
            if ("id".equals(propName) == false) {
                AbstractValueHolder vh = this.propertys.get(propName);
                if (vh != null)
                    vh.value(mineState.get(propName));
            }
        //3.恢复子组件
        Map<String, Object> childrenState = (Map<String, Object>) stateData[1];
        for (UIComponent com : components)
            com.restoreState((Object[]) childrenState.get(com.getId()));
    };
    /**将组件的数据提取出来*/
    public Object[] saveState() {
        //1.持久化自身的状态
        HashMap<String, Object> mineState = new HashMap<String, Object>();
        for (String propName : this.propertys.keySet()) {
            AbstractValueHolder vh = this.propertys.get(propName);
            mineState.put(propName, vh.value());
        }
        //2.持久化子组件的状态
        HashMap<String, Object> childrenState = new HashMap<String, Object>();
        for (UIComponent com : components)
            childrenState.put(com.getId(), com.saveState());
        //3.返回持久化状态
        Object[] thisState = new Object[2];
        thisState[0] = mineState;
        thisState[1] = childrenState;
        return thisState;
    }
};