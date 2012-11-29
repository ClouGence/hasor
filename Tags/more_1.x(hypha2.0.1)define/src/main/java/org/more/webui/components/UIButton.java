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
package org.more.webui.components;
import org.more.webui.component.UIComponent;
import org.more.webui.component.support.NoState;
import org.more.webui.component.values.MethodExpression;
import org.more.webui.context.ViewContext;
import org.more.webui.event.Event;
import org.more.webui.event.EventListener;
import org.more.webui.render.button.LinkButtonRender;
import org.more.webui.render.inputs.ButtonInputRender;
/**
 * <b>组建模型</b>：按钮组建模型（表单元素）。
 * <br><b>服务端事件</b>：OnAction
 * <br><b>渲染器</b>：{@link ButtonInputRender}、{@link LinkButtonRender}
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class UIButton extends UIInput {
    /**通用属性表*/
    public enum Propertys {
        /**Action动作（R）*/
        actionEL
    }
    @Override
    protected void initUIComponent(ViewContext viewContext) {
        super.initUIComponent(viewContext);
        this.setPropertyMetaValue(Propertys.actionEL.name(), null);
        this.addEventListener(UIButton_Event_OnAction.ActionEvent, new UIButton_Event_OnAction());
    }
    /**获取Action EL字符串*/
    public String getActionEL() {
        return this.getProperty(Propertys.actionEL.name()).valueTo(String.class);
    }
    /**设置Action EL字符串*/
    @NoState
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