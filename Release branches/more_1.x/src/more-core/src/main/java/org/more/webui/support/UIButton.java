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
/**
 * Button
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class UIButton extends UIComponent {
    /**通用属性表*/
    public enum Propertys {
        /**表示渲染时候是否使用a标签代替input标签，默认：是（RW）*/
        useLink,
        /**显示的名称（RW）*/
        title
    }
    @Override
    protected void initUIComponent(ViewContext viewContext) {
        super.initUIComponent(viewContext);
        this.setProperty(Propertys.useLink.name(), true);
        this.setProperty(Propertys.title.name(), "");
    }
    public boolean isUseLink() {
        return this.getProperty(Propertys.useLink.name()).valueTo(Boolean.TYPE);
    }
    public void setUseLink(boolean useLink) {
        this.getProperty(Propertys.useLink.name()).value(useLink);
    }
    public String getTitle() {
        return this.getProperty(Propertys.title.name()).valueTo(String.class);
    }
    public void setTitle(String title) {
        this.getProperty(Propertys.title.name()).value(title);
    }
};