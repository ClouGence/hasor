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
        /**值*/
        value,
        /**当发生改变时EL调用表达式（ajax）。*/
        onChangeEL,
    }
    private boolean addEventListener = false;
    @Override
    protected void initUIComponent(ViewContext viewContext) {
        super.initUIComponent(viewContext);
        if (addEventListener == false) {
            this.getEventManager().addEventListener(Event.getEvent("OnChange"), new Event_OnChange());
            this.addEventListener = true;
        }
        this.setProperty(Propertys.value.name(), null);
    }
    /*-------------------------------------------------------------------------------*/
    /**获取组建值*/
    public Object getValue() {
        return this.getProperty(Propertys.value.name()).valueTo(Object.class);
    }
    /**设置组建值*/
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
}
/**负责处理OnChange事件的EL调用*/
class Event_OnChange implements EventListener {
    @Override
    public void onEvent(Event event, Sequence sequence) throws Throwable {
        UIInput component = (UIInput) sequence.getParams()[0];
        ViewContext viewContext = (ViewContext) sequence.getParams()[1];
        MethodExpression e = component.getOnChangeExpression();
        if (e != null)
            e.execute(component, viewContext);
    }
    /*-------------------------------------------------------------------------------*/
}