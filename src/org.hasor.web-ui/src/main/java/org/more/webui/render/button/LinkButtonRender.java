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
package org.more.webui.render.button;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import org.more.webui.components.UIButton;
import org.more.webui.context.ViewContext;
import org.more.webui.render.AbstractRender;
import org.more.webui.tag.TemplateBody;
import freemarker.template.TemplateException;
/**
 * 将按钮组建渲染成a标签。
 * <br><b>客户端模型</b>：UILinkButton（UILinkButton.js）
 * @version : 2012-5-18
 * @author 赵永春 (zyc@byshell.org)
 */
public class LinkButtonRender<T extends UIButton> extends AbstractRender<T> {
    @Override
    public String getClientType() {
        return "UILinkButton";
    }
    @Override
    public String tagName(ViewContext viewContext, T component) {
        return "a";
    }
    @Override
    public Map<String, Object> tagAttributes(ViewContext viewContext, T component) {
        //----覆盖定义的属性
        Map<String, Object> hashMap = super.tagAttributes(viewContext, component);
        hashMap.put("href", "javascript:void(0);");//writer.write("<a href='javascript:void(0);'");
        if (hashMap.containsKey("onclick") == true)
            hashMap.put("_onclick", hashMap.remove("onclick"));//writer.write("<a _onclick='xxx'");
        return hashMap;
    }
    @Override
    public void render(ViewContext viewContext, T component, TemplateBody arg3, Writer writer) throws IOException, TemplateException {
        Object val = component.getValue();
        if (val != null)
            writer.write(String.valueOf(val));
        if (arg3 != null)
            arg3.render(writer);
    }
}