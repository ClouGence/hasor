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
package org.more.webui.render.form;
import java.util.Map;
import org.more.webui.components.UIForm;
import org.more.webui.context.ViewContext;
import org.more.webui.render.AbstractRender;
/**
 * 将按钮组建渲染成form。
 * <br><b>客户端模型</b>：UIForm（UIForm.js）
 * @version : 2012-5-18
 * @author 赵永春 (zyc@byshell.org)
 */
public class FormRender<T extends UIForm> extends AbstractRender<T> {
    @Override
    public String getClientType() {
        return "UIForm";
    }
    @Override
    public String tagName(ViewContext viewContext, T component) {
        return "form";
    }
    @Override
    public Map<String, Object> tagAttributes(ViewContext viewContext, T component) {
        Map<String, Object> hashMap = super.tagAttributes(viewContext, component);
        hashMap.put("_onsubmit", hashMap.remove("onsubmit"));
        return hashMap;
    }
}