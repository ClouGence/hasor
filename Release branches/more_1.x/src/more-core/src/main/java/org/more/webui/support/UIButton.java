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
import org.more.webui.context.ViewContext;
import org.more.webui.event.Event;
import org.more.webui.event.EventListener;
import org.more.webui.support.values.MethodExpression;
/**
 * Button
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class UIButton extends UIComponent {
    /**通用属性表*/
    public enum Propertys {
        /**Action动作（RW）*/
        actionEL,
        /**显示的名称（RW）*/
        title
    }
    @Override
    protected void initUIComponent(ViewContext viewContext) {
        super.initUIComponent(viewContext);
        this.setProperty(Propertys.title.name(), "");
        this.setProperty(Propertys.actionEL.name(), null);
        this.addEventListener(UIButton_Event_OnAction.ActionEvent, new UIButton_Event_OnAction());
    }
    public String getTitle() {
        return this.getProperty(Propertys.title.name()).valueTo(String.class);
    }
    public void setTitle(String title) {
        this.getProperty(Propertys.title.name()).value(title);
    }
    /**获取Action EL字符串*/
    public String getActionEL() {
        return this.getProperty(Propertys.actionEL.name()).valueTo(String.class);
    }
    /**设置Action EL字符串*/
    public void setActionEL(String action) {
        this.getProperty(Propertys.actionEL.name()).value(action);
    }
    public MethodExpression getActionExpression() {
        String actionString = this.getActionEL();
        if (actionString == null || actionString.equals("")) {} else
            return new MethodExpression(actionString);
        return null;
    }
};
/**负责处理OnAction事件的EL调用*/
class UIButton_Event_OnAction implements EventListener {
    public static Event ActionEvent = Event.getEvent("OnAction");
    @Override
    public void onEvent(Event event, UIComponent component, ViewContext viewContext) throws Throwable {
        MethodExpression e = ((UIButton) component).getActionExpression();
        if (e != null)
            viewContext.sendObject(e.execute(component, viewContext));
    }
};