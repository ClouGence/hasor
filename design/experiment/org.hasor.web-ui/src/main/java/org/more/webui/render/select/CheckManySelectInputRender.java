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
 * 将选择输入组建渲染成一组check选择框。
 * <br><b>客户端模型</b>：UICheckManySelectInput（UICheckManySelectInput.js）
 * @version : 2012-5-18
 * @author 赵永春 (zyc@byshell.org)
 */
public class CheckManySelectInputRender<T extends UISelectInput> extends AbstractSelectInputRender<T> {
    @Override
    public String getClientType() {
        return "UICheckManySelectInput";
    }
    @Override
    public String tagName(ViewContext viewContext, T component) {
        return "ul";
    }
    @Override
    public Map<String, Object> tagAttributes(ViewContext viewContext, T component) {
        Map<String, Object> attr = super.tagAttributes(viewContext, component);
        attr.put("renderType", this.getRenderType(component).name());
        return attr;
    }
    @Override
    public void render(ViewContext viewContext, T component, TemplateBody arg3, Writer writer) throws IOException, TemplateException {
        List<?> listData = component.getValueList();
        String keyField = component.getKeyField();
        String varField = component.getVarField();
        Object[] selectVar = component.getSelectValues();
        int index = 0;
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
                String checkedStr = "no";
                String checkedMark = "";
                String titleMark = (this.getRenderType(component) == RenderType.onlyTitle) ? " style='display:none;'" : "";
                for (Object selItem : selectVar)
                    if (keyValue != null && keyValue.equals(selItem) == true) {
                        checkedStr = "";
                        checkedMark = "checked='checked'";
                    } else {
                        checkedStr = "no";
                        checkedMark = "";
                    }
                writer.write("<li index='" + index + "' class='" + checkedStr + "checked'>");
                writer.write("  <a href='javascript:void(0)'>");
                writer.write("    <label>");
                writer.write("      <em></em>");
                writer.write("      <input type='checkbox' forComID='" + component.getComponentID() + "' name='" + component.getName() + "' value='" + keyValue + "' oriData='' " + checkedMark + " " + titleMark + "/>");
                writer.write("      <span>" + varValue + "</span>");
                writer.write("    </label>");
                writer.write("  </a>");
                writer.write("</li> ");
                index++;
            }
    }
    /*----------------------------------------------------------------*/
    public static enum RenderType {
        /**选择框前面输出选项文本（有选择框）。*/
        normal,
        /**选择框只包含文本（不带有选择框）。*/
        onlyTitle,
    }
    protected RenderType getRenderType(T component) {
        return RenderType.normal;
    }
}