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
import org.more.webui.component.support.NoState;
import org.more.webui.component.support.UICom;
import org.more.webui.components.UIInput;
import org.more.webui.context.ViewContext;
/**
 * <b>作用</b>：将组建的value值绑定到一个客户端脚本的返回值上。
 * <br><b>组建类型</b>：ui_ScriptInput
 * <br><b>标签</b>：@ui_ScriptInput
 * <br><b>服务端事件</b>：无
 * <br><b>渲染器</b>：{@link ScriptInputRender}
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
@UICom(tagName = "ui_ScriptInput", renderType = ScriptInputRender.class)
public class ScriptInput extends UIInput {
    @Override
    public String getComponentType() {
        return "ui_ScriptInput";
    };
    /**通用属性表*/
    public enum Propertys {
        /**绑定的客户端脚本,读（R）*/
        varRScript,
        /**绑定的客户端脚本,写（R）*/
        varWScript,
    };
    @Override
    protected void initUIComponent(ViewContext viewContext) {
        super.initUIComponent(viewContext);
        this.setPropertyMetaValue(Propertys.varRScript.name(), null);
        this.setPropertyMetaValue(Propertys.varWScript.name(), null);
    }
    /**获取,绑定的客户端脚本,读*/
    public String getVarRScript() {
        return this.getProperty(Propertys.varRScript.name()).valueTo(String.class);
    };
    @NoState
    /**设置,绑定的客户端脚本,读*/
    public void setVarRScript(String varRScript) {
        this.getProperty(Propertys.varRScript.name()).value(varRScript);
    };
    /**获取,绑定的客户端脚本,写*/
    public String getVarWScript() {
        return this.getProperty(Propertys.varWScript.name()).valueTo(String.class);
    };
    @NoState
    /**设置,绑定的客户端脚本,写*/
    public void setVarWScript(String varWScript) {
        this.getProperty(Propertys.varWScript.name()).value(varWScript);
    };
}