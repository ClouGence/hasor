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
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import org.more.webui.context.ViewContext;
import org.more.webui.render.AbstractInputRender;
import org.more.webui.render.UIRender;
import org.more.webui.tag.TemplateBody;
import freemarker.template.TemplateException;
/**
 * 
 * @version : 2012-5-18
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@UIRender(tagName = "ui_Text")
public class TextInputRender extends AbstractInputRender<TextInput> {
    @Override
    protected String tagName(ViewContext viewContext, TextInput component) {
        if (component.isMultiLine() == true)
            return "textarea";
        else
            return "input";
    }
    @Override
    public Map<String, Object> tagAttributes(ViewContext viewContext, TextInput component) {
        Map<String, Object> hashMap = super.tagAttributes(viewContext, component);
        if (component.isMultiLine() == false) {
            if (component.isPwd() == true)
                hashMap.put("type", "password");
            else
                hashMap.put("type", "text");
        } else
            hashMap.remove("value");
        return hashMap;
    }
    @Override
    public void render(ViewContext viewContext, TextInput component, TemplateBody arg3, Writer writer) throws IOException, TemplateException {
        super.render(viewContext, component, arg3, writer);
        if (component.isMultiLine() == true) {
            Object var = component.getValue();
            if (var != null)
                writer.append(String.valueOf(component.getValue()));
        }
    }
}