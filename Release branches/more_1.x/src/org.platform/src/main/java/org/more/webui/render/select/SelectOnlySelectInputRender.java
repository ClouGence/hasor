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
package org.more.webui.render.select;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import org.more.util.BeanUtils;
import org.more.webui.components.UISelectInput;
import org.more.webui.context.ViewContext;
import org.more.webui.tag.TemplateBody;
import freemarker.template.TemplateException;
/**
 * 将选择输入组建渲染成一个select输入框。
 * <br><b>客户端模型</b>：UISelectOnlySelectInput（UISelectOnlySelectInput.js）
 * @version : 2012-5-18
 * @author 赵永春 (zyc@byshell.org)
 */
public class SelectOnlySelectInputRender<T extends UISelectInput> extends AbstractSelectInputRender<T> {
    @Override
    public String getClientType() {
        return "UISelectOnlySelectInput";
    }
    @Override
    public String tagName(ViewContext viewContext, UISelectInput component) {
        return "select";
    }
    @Override
    public void render(ViewContext viewContext, T component, TemplateBody arg3, Writer writer) throws IOException, TemplateException {
        List<?> listData = component.getValueList();
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
                    keyValue = BeanUtils.readPropertyOrField(obj, keyField);
                    varValue = BeanUtils.readPropertyOrField(obj, varField);
                }
                //输出
                writer.write("  <option value='" + keyValue + "'");
                if (keyValue.equals(component.getSelectValue()) == true)
                    writer.write("selected='selected'");
                writer.write(" varValue='" + varValue + "'");
                writer.write(">" + varValue + "</option>");
            }
    }
}