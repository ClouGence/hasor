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
package org.more.webui.components.clientbutton;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import org.more.webui.context.ViewContext;
import org.more.webui.render.AbstractRender;
import org.more.webui.render.UIRender;
import org.more.webui.tag.TemplateBody;
import freemarker.template.TemplateException;
/**
 * 
 * @version : 2012-5-18
 * @author 赵永春 (zyc@byshell.org)
 */
@UIRender(tagName = "ui_Button")
public class ClientButtonRender extends AbstractRender<ClientButton> {
    @Override
    protected String tagName(ViewContext viewContext, ClientButton component) {
        if (component.isUseLink() == true)
            return "a";
        else
            return "input";
    }
    @Override
    public Map<String, Object> tagAttributes(ViewContext viewContext, ClientButton component) {
        //----覆盖定义的属性
        Map<String, Object> hashMap = super.tagAttributes(viewContext, component);
        if (component.isUseLink() == true)
            hashMap.put("href", "javascript:void(0);");//writer.write("<a href='javascript:void(0);'");
        else
            hashMap.put("type", "button");//writer.write("<input type='button'");
        if (component.isUseLink() == false)
            hashMap.put("value", component.getTitle());
        //----
        return hashMap;
    }
    @Override
    public void render(ViewContext viewContext, ClientButton component, TemplateBody arg3, Writer writer) throws IOException, TemplateException {
        if (component.isUseLink() == true)
            writer.write(component.getTitle());
        if (arg3 != null)
            arg3.render(writer);
    }
}