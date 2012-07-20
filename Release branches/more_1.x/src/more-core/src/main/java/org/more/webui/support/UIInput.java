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
import org.more.core.event.Event;
import org.more.core.event.Event.Sequence;
import org.more.core.event.EventListener;
import org.more.webui.context.ViewContext;
import org.more.webui.support.values.MethodExpression;
/**
 * 带有输入功能的组建
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class UIInput extends UIComponent {
    /**通用属性表*/
    public enum Propertys {
        /**表单名（RW）*/
        name,
        /**表单值（RW）*/
        value,
        /**验证,正则表达式（RW）*/
        verification,
        /**当发生事件OnChange时（RW）*/
        onChangeEL,
        /**当发生事件OnLoadData时（RW）*/
        onLoadDataEL,
    }
    @Override
    protected void initUIComponent(ViewContext viewContext) {
        super.initUIComponent(viewContext);
        this.getEventManager().addEventListener(Event.getEvent("OnChange"), new Event_OnChange());
        this.getEventManager().addEventListener(Event.getEvent("OnLoadData"), new Event_OnLoadData());
        this.setProperty(Propertys.value.name(), null);
    }
    /*-------------------------------------------------------------------------------*/
    /**获取组建表单名*/
    public String getName() {
        return this.getProperty(Propertys.name.name()).valueTo(String.class);
    }
    /**设置组建表单名*/
    public void setName(String name) {
        this.getProperty(Propertys.name.name()).value(name);
    }
    /**获取组建表单值*/
    public Object getValue() {
        return this.getProperty(Propertys.value.name()).valueTo(Object.class);
    }
    /**设置组建表单值*/
    public void setValue(String value) {
        this.getProperty(Propertys.value.name()).value(value);
    }
    /**当组建值发生改变之后会ajax调用该表达式（如果配置）*/
    public String getOnChangeEL() {
        return this.getProperty(Propertys.onChangeEL.name()).valueTo(String.class);
    }
    /**设置一个EL表达式该表达式会当组建值发生改变之后调用（如果配置）*/
    public void setOnChangeEL(String onChangeEL) {
        this.getProperty(Propertys.onChangeEL.name()).value(onChangeEL);
    }
    /**当企图装载数据时EL调用表达式（如果配置）*/
    public String getOnLoadDataEL() {
        return this.getProperty(Propertys.onLoadDataEL.name()).valueTo(String.class);
    }
    /**当企图装载数据时EL调用表达式（如果配置）*/
    public void setOnLoadDataEL(String onLoadDataEL) {
        this.getProperty(Propertys.onLoadDataEL.name()).value(onLoadDataEL);
    }
    /**验证,正则表达式（如果配置）*/
    public String getVerification() {
        return this.getProperty(Propertys.verification.name()).valueTo(String.class);
    }
    /**验证,正则表达式（如果配置）*/
    public void setVerification(String verification) {
        this.getProperty(Propertys.verification.name()).value(verification);
    }
    /*-------------------------------------------------------------------------------*/
    private MethodExpression onChangeExp = null;
    public MethodExpression getOnChangeExpression() {
        if (this.onChangeExp == null) {
            String onChangeExpString = this.getOnChangeEL();
            if (onChangeExpString == null || onChangeExpString.equals("")) {} else
                this.onChangeExp = new MethodExpression(onChangeExpString);
        }
        return this.onChangeExp;
    }
    private MethodExpression onLoadDataExp = null;
    public MethodExpression getOnLoadDataExpression() {
        if (this.onLoadDataExp == null) {
            String onLoadDataExpString = this.getOnLoadDataEL();
            if (onLoadDataExpString == null || onLoadDataExpString.equals("")) {} else
                this.onLoadDataExp = new MethodExpression(onLoadDataExpString);
        }
        return this.onLoadDataExp;
    }
}
/**负责处理OnChange事件的EL调用*/
class Event_OnChange implements EventListener {
    @Override
    public void onEvent(Event event, Sequence sequence) throws Throwable {
        UIInput component = (UIInput) sequence.getParams()[0];
        ViewContext viewContext = (ViewContext) sequence.getParams()[1];
        MethodExpression e = component.getOnChangeExpression();
        if (e != null)
            viewContext.sendAjaxData(e.execute(component, viewContext));
    }
}
/**负责处理OnLoadData事件的EL调用*/
class Event_OnLoadData implements EventListener {
    @Override
    public void onEvent(Event event, Sequence sequence) throws Throwable {
        UIInput component = (UIInput) sequence.getParams()[0];
        ViewContext viewContext = (ViewContext) sequence.getParams()[1];
        MethodExpression e = component.getOnLoadDataExpression();
        if (e != null)
            viewContext.sendAjaxData(e.execute(component, viewContext));
    }
}