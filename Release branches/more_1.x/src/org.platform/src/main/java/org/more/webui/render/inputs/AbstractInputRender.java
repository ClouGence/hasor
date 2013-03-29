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
package org.more.webui.render.inputs;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import org.more.webui.components.UIInput;
import org.more.webui.context.ViewContext;
import org.more.webui.render.AbstractRender;
import org.more.webui.tag.TemplateBody;
import freemarker.template.TemplateException;
/**
 * 抽象类，输入组建的渲染器，主要用于input标签。
 * <br><b>客户端模型</b>：UIInput（UIInput.js）
 * @version : 2012-5-18
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractInputRender<T extends UIInput> extends AbstractRender<T> {
    @Override
    public String getClientType() {
        return "UIInput";
    }
    /**要使用的标签*/
    @Override
    public String tagName(ViewContext viewContext, T component) {
        return "input";
    }
    /**获取渲染的input标签其type属性*/
    public abstract InputType getInputType(ViewContext viewContext, T component);
    @Override
    public Map<String, Object> tagAttributes(ViewContext viewContext, T component) {
        Map<String, Object> hashMap = super.tagAttributes(viewContext, component);
        //name
        Object var = component.getName();
        if (var != null)
            hashMap.put("name", var);
        //value
        var = component.getValue();
        if (var != null)
            hashMap.put("value", component.getValue());
        //type
        InputType inputType = this.getInputType(viewContext, component);
        if (inputType != null)
            hashMap.put("type", inputType.name());//writer.write("<input type='???'");
        //_onchange
        hashMap.put("_onchange", hashMap.remove("onchange"));
        return hashMap;
    }
    @Override
    public void render(ViewContext viewContext, T component, TemplateBody arg3, Writer writer) throws IOException, TemplateException {}
    public static enum InputType {
        button, checkbox, file, hidden, image, password, radio, reset, submit, text,
    }
}