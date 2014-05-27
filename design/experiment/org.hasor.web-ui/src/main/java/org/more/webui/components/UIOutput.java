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
package org.more.webui.components;
import org.more.webui.component.UIComponent;
import org.more.webui.context.ViewContext;
import org.more.webui.render.output.OutputRender;
/**
 * <b>组建模型</b>：用于表述输出功能的组建。
 * <br><b>服务端事件</b>：无
 * <br><b>渲染器</b>：{@link OutputRender}
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class UIOutput extends UIComponent {
    /**通用属性表*/
    public enum Propertys {
        /**值（RW）*/
        value,
    }
    @Override
    protected void initUIComponent(ViewContext viewContext) {
        super.initUIComponent(viewContext);
        this.setPropertyMetaValue(Propertys.value.name(), null);
    }
    /**获取组建value属性*/
    public Object getValue() {
        return this.getProperty(Propertys.value.name()).valueTo(Object.class);
    }
    /**设置组建value属性*/
    public void setValue(Object value) {
        this.getProperty(Propertys.value.name()).value(value);
    }
}