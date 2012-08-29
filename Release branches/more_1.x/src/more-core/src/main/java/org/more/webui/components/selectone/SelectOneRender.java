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
package org.more.webui.components.selectone;
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
@UIRender(tagName = "ui_SelectOne")
public class SelectOneRender extends AbstractInputRender<SelectOne> {
    @Override
    protected String tagName(ViewContext viewContext, SelectOne component) {
        return "select";
    }
    @Override
    public void render(ViewContext viewContext, SelectOne component, TemplateBody arg3, Writer writer) throws IOException, TemplateException {
        List<?> listData = component.getListData();
        String keyField = component.getKeyField();
        String varField = component.getVarField();
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
                writer.write("  <option value='" + keyValue + "'");
                if (keyValue.equals(component.getValue()) == true)
                    writer.write("selected='selected'");
                writer.write(" varValue='" + varValue + "'");
                writer.write(">" + varValue + "</option>");
            }
    }
}