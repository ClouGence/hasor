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
package org.more.webui.component;
import org.more.webui.component.values.MethodExpression;
import org.more.webui.context.ViewContext;
import org.more.webui.event.Event;
import org.more.webui.event.EventListener;
/**
 * 用于表述带有输入输出功能的组建模型（表单元素）。
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class UIInput extends UIOutput {
    /**通用属性表*/
    public enum Propertys {
        /**表单名（RW）*/
        name,
        /**验证输入数据的正则表达式（RW）*/
        verification,
        /**当发生事件OnChange时（RW）*/
        onChangeEL,
    }
    @Override
    protected void initUIComponent(ViewContext viewContext) {
        super.initUIComponent(viewContext);
        this.setPropertyMetaValue(Propertys.name.name(), null);
        this.setPropertyMetaValue(Propertys.verification.name(), null);
        this.setPropertyMetaValue(Propertys.onChangeEL.name(), null);
        this.addEventListener(Event.getEvent("OnChange"), new Event_OnChange());
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
    /**当组建值发生改变之后会ajax调用该表达式（如果配置）*/
    public String getOnChangeEL() {
        return this.getProperty(Propertys.onChangeEL.name()).valueTo(String.class);
    }
    /**设置一个EL表达式该表达式会当组建值发生改变之后调用（如果配置）*/
    public void setOnChangeEL(String onChangeEL) {
        this.getProperty(Propertys.onChangeEL.name()).value(onChangeEL);
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
}
/**负责处理OnChange事件的EL调用*/
class Event_OnChange implements EventListener {
    @Override
    public void onEvent(Event event, UIComponent component, ViewContext viewContext) throws Throwable {
        MethodExpression e = ((UIInput) component).getOnChangeExpression();
        if (e != null)
            viewContext.sendObject(e.execute(component, viewContext));
    }
}