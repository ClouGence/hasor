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
package org.more.webui.render;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;
import org.more.util.CommonCodeUtil;
import org.more.webui.context.ViewContext;
import org.more.webui.support.UIComponent;
import org.more.webui.support.values.AbstractValueHolder;
import org.more.webui.tag.TemplateBody;
import com.alibaba.fastjson.JSONObject;
import freemarker.template.TemplateModelException;
/**
 * 
 * @version : 2012-5-18
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractRender<T extends UIComponent> implements Render<T> {
    private String tagString = null;
    @Override
    public void beginRender(ViewContext viewContext, T component, TemplateBody arg3, Writer writer) throws IOException, TemplateModelException {
        this.tagString = this.tagName(viewContext, component);
        if (this.tagString == null)
            throw new NullPointerException("tagName is null.");
        writer.write("<" + this.tagString);
        /*-------------------------------------------------*/
        //Core Atts
        /*-------------------------------------------------*/
        writer.write(" id='" + component.getClientID(viewContext) + "'");
        writer.write(" comID='" + component.getId() + "'");
        writer.write(" comType='" + component.getComponentType() + "'");
        if (this.isSaveState(viewContext, component) == true) {
            String base64 = CommonCodeUtil.Base64.base64Encode(JSONObject.toJSONString(component.saveState()));
            writer.write(" uiState='" + base64 + "'");
        }
        /*-------------------------------------------------*/
        //Atts
        /*-------------------------------------------------*/
        Map<String, Object> atts = this.tagAttributes(viewContext, component);
        for (Entry<String, Object> ent : atts.entrySet()) {
            Object value = ent.getValue();
            if (value == null)
                continue;
            String var = null;
            if (value instanceof AbstractValueHolder == true)
                var = ((AbstractValueHolder) value).valueTo(String.class);
            else
                var = String.valueOf(value);
            if (var != null)
                writer.write(" " + ent.getKey() + "=\"" + var + "\"");
        }
        writer.write(">");
    };
    @Override
    public void endRender(ViewContext viewContext, T component, TemplateBody arg3, Writer writer) throws IOException, TemplateModelException {
        writer.write("</" + this.tagString + ">");
    };
    /**要使用的标签*/
    protected abstract String tagName(ViewContext viewContext, T component);
    /**位于标签上的属性名，如果名与核心属性名发生冲突则保留核心属性。*/
    protected abstract Map<String, Object> tagAttributes(ViewContext viewContext, T component);
    /**是否保存状态。*/
    protected abstract boolean isSaveState(ViewContext viewContext, T component);
}