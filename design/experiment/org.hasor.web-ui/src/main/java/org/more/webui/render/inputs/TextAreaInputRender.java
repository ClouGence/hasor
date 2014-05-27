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
package org.more.webui.render.inputs;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import org.more.webui.components.UIInput;
import org.more.webui.context.ViewContext;
import org.more.webui.tag.TemplateBody;
import freemarker.template.TemplateException;
/**
 * 将输入组建渲染成textarea。
 * <br><b>客户端模型</b>：UIInput（UIInput.js）
 * @version : 2012-5-18
 * @author 赵永春 (zyc@byshell.org)
 */
public class TextAreaInputRender<T extends UIInput> extends AbstractInputRender<T> {
    /**要使用的标签*/
    @Override
    public String tagName(ViewContext viewContext, T component) {
        return "textarea";
    }
    /**该方法的返回值会被tagAttributes方法删除。*/
    public InputType getInputType(ViewContext viewContext, T component) {
        return InputType.text;
    };
    @Override
    public Map<String, Object> tagAttributes(ViewContext viewContext, T component) {
        Map<String, Object> hashMap = super.tagAttributes(viewContext, component);
        hashMap.remove("value");
        hashMap.remove("type");
        return hashMap;
    }
    @Override
    public void render(ViewContext viewContext, T component, TemplateBody arg3, Writer writer) throws IOException, TemplateException {
        Object var = component.getValue();
        if (var != null)
            writer.append(String.valueOf(component.getValue()));
        arg3.render(writer);
    }
}