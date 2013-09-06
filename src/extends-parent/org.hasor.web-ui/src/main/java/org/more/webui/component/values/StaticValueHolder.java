/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package org.more.webui.component.values;
import org.more.webui.component.UIComponent;
import org.more.webui.context.ViewContext;
/**
 * æ≤Ã¨÷µValueHolder
 * @version : 2012-5-11
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class StaticValueHolder extends AbstractValueHolder {
    //
    public StaticValueHolder() {}
    public StaticValueHolder(Object staticValue) {
        this.setMetaValue(staticValue);
    }
    @Override
    public boolean isUpdate() {
        return false;
    }
    @Override
    public boolean isReadOnly() {
        return false;
    }
    @Override
    public void updateModule(UIComponent component, ViewContext viewContext) {}
}