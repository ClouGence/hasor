/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.more.webui.components.input;
import org.more.webui.component.support.UICom;
import org.more.webui.components.UIInput;
import org.more.webui.context.ViewContext;
import org.more.webui.render.inputs.TextAreaInputRender;
/**
 * <b>作用</b>：Text多行输入框。
 * <br><b>组建类型</b>：ui_TextArea
 * <br><b>标签</b>：@ui_TextArea
 * <br><b>服务端事件</b>：无
 * <br><b>渲染器</b>：{@link TextAreaInputRender}
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
@UICom(tagName = "ui_TextArea", renderType = TextAreaInputRender.class)
public class TextArea extends UIInput {
    /**通用属性表*/
    public enum Propertys {
        /**该值是当value没有设置值时会用该值替代显示（RW）*/
        tipTitle,
    }
    @Override
    public String getComponentType() {
        return "ui_TextArea";
    }
    @Override
    protected void initUIComponent(ViewContext viewContext) {
        super.initUIComponent(viewContext);
        this.setPropertyMetaValue(Propertys.tipTitle.name(), null);
    }
    public String getTipTitle() {
        return this.getProperty(Propertys.tipTitle.name()).valueTo(String.class);
    }
    public void setTipTitle(String tipTitle) {
        this.getProperty(Propertys.tipTitle.name()).value(tipTitle);
    }
}