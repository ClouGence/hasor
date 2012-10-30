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
import java.util.Map;
import org.more.webui.component.UIInput;
import org.more.webui.context.ViewContext;
/**
 * 
 * @version : 2012-5-18
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public abstract class AbstractInputRender<T extends UIInput> extends AbstractRender<T> {
    @Override
    public Map<String, Object> tagAttributes(ViewContext viewContext, T component) {
        Map<String, Object> hashMap = super.tagAttributes(viewContext, component);
        //
        Object var = component.getName();
        if (var != null)
            hashMap.put("name", var);
        //
        var = component.getValue();
        if (var != null)
            hashMap.put("value", component.getValue());
        return hashMap;
    }
}