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
package org.more.webui.components.selectcheck;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import org.more.util.BeanUtil;
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
@UIRender(tagName = "ui_SelectCheck")
public class SelectCheckBoxRender extends AbstractInputRender<SelectCheckBox> {
    @Override
    protected String tagName(ViewContext viewContext, SelectCheckBox component) {
        return "div";
    }
    @Override
    public void render(ViewContext viewContext, SelectCheckBox component, TemplateBody arg3, Writer writer) throws IOException, TemplateException {
        List<?> listData = component.getListData();
        String keyField = component.getKeyField();
        String varField = component.getVarField();
        Object[] selectVar = component.getSelectValue();
        if (listData != null)
            for (Object obj : listData) {
                if (obj == null)
                    continue;
                Object keyValue = null;
                Object varValue = null;
                if (obj instanceof Map) {
                    Map mapData = (Map) obj;
                    keyValue = mapData.get(keyField);
                    varValue = mapData.get(varField);
                } else {
                    keyValue = BeanUtil.readPropertyOrField(obj, keyField);
                    varValue = BeanUtil.readPropertyOrField(obj, varField);
                }
                // ‰≥ˆ
                writer.write("<li>");
                if (component.isTitleFirst() == true) {
                    this.renderSpan(component, writer, varValue, selectVar);
                    this.renderInput(component, writer, keyValue, varValue, selectVar);
                } else {
                    this.renderInput(component, writer, keyValue, varValue, selectVar);
                    this.renderSpan(component, writer, varValue, selectVar);
                }
                writer.write("</li>");
            }
    }
    public void renderInput(SelectCheckBox component, Writer writer, Object keyValue, Object varValue, Object[] selectVar) throws IOException, TemplateException {
        writer.write("<input type='checkbox' forComID='" + component.getComponentID() + "'");
        String name = component.getName();
        if (name == null || name.equals("") == true) {} else
            writer.write(" name='" + name + "'");
        writer.write(" value='" + keyValue + "'");
        writer.write(" varValue='" + varValue + "'");
        for (Object obj : selectVar)
            if ((keyValue == null && obj == null) == true || keyValue.equals(obj) == true)
                writer.write(" checked='checked'");
        writer.write("/>");
    }
    public void renderSpan(SelectCheckBox component, Writer writer, Object varValue, Object[] selectVar) throws IOException, TemplateException {
        writer.write("<span>" + varValue + "</span>");
    }
}