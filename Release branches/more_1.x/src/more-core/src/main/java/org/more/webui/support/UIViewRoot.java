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
import java.util.List;
import org.more.core.event.Event;
import org.more.core.event.Event.Sequence;
import org.more.core.event.EventListener;
import org.more.webui.context.ViewContext;
import org.more.webui.support.values.MethodExpression;
import org.more.webui.web.PostFormEnum;
/**
 * 所有组件的根，同时也负责保存所有视图参数。该组建不使用@UICom注解注册
 * @version : 2012-3-29
 * @author 赵永春 (zyc@byshell.org)
 */
public class UIViewRoot extends UIComponent {
    public UIViewRoot() {
        this.setComponentID("com_root");
    }
    @Override
    public String getComponentType() {
        return "ui_ViewRoot";
    }
    @Override
    protected void initUIComponent(ViewContext viewContext) {
        this.getEventManager().addEventListener(UIViewRoot_Event_OnAction.ActionEvent, new UIViewRoot_Event_OnAction());
        super.initUIComponent(viewContext);
    }
    public void restoreState(String componentPath, List<?> stateData) {
        UIComponent com = this.getChildByPath(componentPath);
        com.restoreState(stateData);
    }
    public List<?> saveState(String componentPath) {
        UIComponent com = this.getChildByPath(componentPath);
        return com.saveState();
    }
}
/**负责处理OnInvoke事件的EL调用*/
class UIViewRoot_Event_OnAction implements EventListener {
    public static Event ActionEvent = Event.getEvent("OnInvoke");
    @Override
    public void onEvent(Event event, Sequence sequence) throws Throwable {
        UIViewRoot component = (UIViewRoot) sequence.getParams()[0];
        ViewContext viewContext = (ViewContext) sequence.getParams()[1];
        String invokeString = viewContext.getHttpRequest().getParameter(PostFormEnum.PostForm_InvokeStringKey.value());
        if (invokeString == null || invokeString.equals("") == true)
            return;
        MethodExpression e = new MethodExpression(invokeString);
        if (e != null)
            viewContext.sendAjaxData(e.execute(component, viewContext));
    }
};