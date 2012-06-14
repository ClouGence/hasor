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
package org.more.webui.components.targetbutton;
import java.io.IOException;
import java.io.Writer;
import org.more.core.json.JsonUtil;
import org.more.util.Base64;
import org.more.webui.context.ViewContext;
import org.more.webui.render.Render;
import org.more.webui.render.UIRender;
import org.more.webui.tag.TemplateBody;
import freemarker.template.TemplateException;
/**
 * 
 * @version : 2012-5-18
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@UIRender(tagName = "ui_TargetButton")
public class TargetButtonRender implements Render<TargetButton> {
    @Override
    public void beginRender(ViewContext viewContext, TargetButton component, TemplateBody arg3, Writer writer) throws IOException {
        if (component.isUseLink() == true)
            writer.write("<a href='javascript:void(0);'");
        else
            writer.write("<input text='button'");
        /*-------------------------------------------------*/
        //
        /*-------------------------------------------------*/
        writer.write(" id='" + component.getClientID(viewContext) + "'");
        writer.write(" comID='" + component.getId() + "'");
        writer.write(" comType='ui_TargetButton'");
        String base64 = Base64.base64Encode(JsonUtil.transformToJson(component.saveState()));
        writer.write(" uiState='" + base64 + "'");
        //HTML Att
        writer.write(" style='" + component.getProperty("style").valueTo(String.class) + "'");
        writer.write(" class='" + component.getProperty("class").valueTo(String.class) + "'");
        /*-------------------------------------------------*/
        //
        /*-------------------------------------------------*/
        if (component.isUseLink() == false)
            writer.write(" value='" + component.getProperty("value").valueTo(String.class) + "'");
        writer.write(" onclick='" + component.getId() + ".onclick(this);'");
        writer.write(">");
    }
    @Override
    public void render(ViewContext viewContext, TargetButton component, TemplateBody arg3, Writer writer) throws IOException, TemplateException {
        if (component.isUseLink() == false)
            writer.write(component.getProperty("value").valueTo(String.class));
        else
            arg3.render(writer);
    }
    @Override
    public void endRender(ViewContext viewContext, TargetButton component, TemplateBody arg3, Writer writer) throws IOException {
        if (component.isUseLink() == true)
            writer.write("</a>");
        else
            writer.write("</input>");
    }
}