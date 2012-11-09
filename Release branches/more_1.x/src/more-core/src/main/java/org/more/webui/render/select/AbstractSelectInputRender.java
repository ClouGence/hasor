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
import java.util.Map;
import org.more.webui.component.UISelectInput;
import org.more.webui.context.ViewContext;
import org.more.webui.render.support.AbstractRender;
/**
 * 抽象类，输入组建的渲染器，主要用于input标签。
 * <br><b>客户端模型</b>：UISelectInput（UISelectInput.js）
 * @version : 2012-5-18
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractSelectInputRender<T extends UISelectInput> extends AbstractRender<T> {
    @Override
    public Map<String, Object> tagAttributes(ViewContext viewContext, T component) {
        Map<String, Object> tagNames = super.tagAttributes(viewContext, component);
        String name = component.getName();
        if (name == null || name.equals("") == true) {} else
            tagNames.put("name", name);
        //_onchange
        tagNames.put("_onchange", tagNames.remove("onchange"));
        return tagNames;
    }
}