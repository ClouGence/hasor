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
package org.more.webui.components.text;
import org.more.webui.context.ViewContext;
import org.more.webui.support.UICom;
import org.more.webui.support.UIInput;
/**
 * Text输入框，可以被配置为多行的或者单行的
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
@UICom(tagName = "ui_Text")
public class TextInput extends UIInput {
    /**通用属性表*/
    public enum Propertys {
        /**是否启用多行支持*/
        multiLine,
    }
    @Override
    protected void initUIComponent(ViewContext viewContext) {
        super.initUIComponent(viewContext);
        this.setProperty(Propertys.multiLine.name(), false);
    }
    public boolean isMultiLine() {
        return this.getProperty(Propertys.multiLine.name()).valueTo(Boolean.TYPE);
    }
    public void setMultiLine(boolean multiLine) {
        this.getProperty(Propertys.multiLine.name()).value(multiLine);
    }
}