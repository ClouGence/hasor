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
import org.more.webui.context.ViewContext;
import org.more.webui.support.UIButton;
import org.more.webui.support.UICom;
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
        /**表示渲染时候是否使用a标签代替input标签，默认：是（RW）*/
        useLink,
    }
    @Override
    protected void initUIComponent(ViewContext viewContext) {
        super.initUIComponent(viewContext);
        this.setProperty(Propertys.useLink.name(), true);
    }
    public boolean isUseLink() {
        return this.getProperty(Propertys.useLink.name()).valueTo(Boolean.TYPE);
    }
    public void setUseLink(boolean useLink) {
        this.getProperty(Propertys.useLink.name()).value(useLink);
    }
}