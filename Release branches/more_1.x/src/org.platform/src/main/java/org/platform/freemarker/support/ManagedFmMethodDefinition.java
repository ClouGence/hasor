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
package org.platform.freemarker.support;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.platform.context.AppContext;
import com.google.inject.Binding;
import com.google.inject.TypeLiteral;
/**
 * π‹¿ÌTemplateLoaderCreatorDefinition¿‡°£
 * @version : 2013-5-22
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class ManagedFmMethodDefinition {
    private FmMethodDefinition[] fmMethodDefinitionArray = null;
    //
    public ManagedFmMethodDefinition(AppContext appContext) {
        ArrayList<FmMethodDefinition> fmMethodDefinitionList = new ArrayList<FmMethodDefinition>();
        TypeLiteral<FmMethodDefinition> FMMETHOD_DEFS = TypeLiteral.get(FmMethodDefinition.class);
        for (Binding<FmMethodDefinition> entry : appContext.getGuice().findBindingsByType(FMMETHOD_DEFS)) {
            FmMethodDefinition define = entry.getProvider().get();
            define.initAppContext(appContext);
            fmMethodDefinitionList.add(define);
        }
        // Convert to a fixed size array for speed.
        this.fmMethodDefinitionArray = fmMethodDefinitionList.toArray(new FmMethodDefinition[fmMethodDefinitionList.size()]);
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> returnData = new HashMap<String, Object>();
        if (this.fmMethodDefinitionArray != null)
            for (FmMethodDefinition fmDefine : this.fmMethodDefinitionArray)
                returnData.put(fmDefine.getName(), fmDefine.get());
        return returnData;
    }
}