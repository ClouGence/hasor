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
package org.more.webui.components.ajaxbutton;
import org.more.core.event.Event;
import org.more.core.event.Event.Sequence;
import org.more.core.event.EventListener;
import org.more.webui.context.ViewContext;
import org.more.webui.support.UIButton;
import org.more.webui.support.UICom;
import org.more.webui.support.values.MethodExpression;
/**
 * ajax方式请求的button
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
@UICom(tagName = "ui_AjaxButton")
public class AjaxButton extends UIButton {
    @Override
    public String getComponentType() {
        return "ui_AjaxButton";
    }
    /**通用属性表*/
    public enum Propertys {
        /**Action动作（RW）*/
        actionEL,
    }
    @Override
    protected void initUIComponent(ViewContext viewContext) {
        super.initUIComponent(viewContext);
        this.getEventManager().addEventListener(AjaxButton_Event_OnAction.ActionEvent, new AjaxButton_Event_OnAction());
        this.setProperty(Propertys.actionEL.name(), null);
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
}
/**负责处理OnAction事件的EL调用*/
class AjaxButton_Event_OnAction implements EventListener {
    public static Event ActionEvent = Event.getEvent("OnAction");
    @Override
    public void onEvent(Event event, Sequence sequence) throws Throwable {
        AjaxButton component = (AjaxButton) sequence.getParams()[0];
        ViewContext viewContext = (ViewContext) sequence.getParams()[1];
        MethodExpression e = component.getActionExpression();
        if (e != null)
            viewContext.sendAjaxData(e.execute(component, viewContext));
    }
};