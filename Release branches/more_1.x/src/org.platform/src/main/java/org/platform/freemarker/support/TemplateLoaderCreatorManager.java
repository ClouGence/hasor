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
import org.more.util.StringUtils;
import org.platform.context.AppContext;
import org.platform.freemarker.ITemplateLoaderCreator;
import com.google.inject.Binding;
import com.google.inject.TypeLiteral;
/**
 * π‹¿ÌTemplateLoaderCreatorDefinition¿‡°£
 * @version : 2013-5-22
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class TemplateLoaderCreatorManager {
    private TemplateLoaderCreatorDefinition[] creatorDefinitionArray = null;
    //
    public TemplateLoaderCreatorManager(AppContext appContext) {
        ArrayList<TemplateLoaderCreatorDefinition> creatorDefinitionList = new ArrayList<TemplateLoaderCreatorDefinition>();
        TypeLiteral<TemplateLoaderCreatorDefinition> CREATOR_DEFS = TypeLiteral.get(TemplateLoaderCreatorDefinition.class);
        for (Binding<TemplateLoaderCreatorDefinition> entry : appContext.getGuice().findBindingsByType(CREATOR_DEFS)) {
            TemplateLoaderCreatorDefinition define = entry.getProvider().get();
            define.setAppContext(appContext);
            creatorDefinitionList.add(define);
        }
        // Convert to a fixed size array for speed.
        creatorDefinitionArray = creatorDefinitionList.toArray(new TemplateLoaderCreatorDefinition[creatorDefinitionList.size()]);
    }
    public ITemplateLoaderCreator getCreator(String key) {
        if (creatorDefinitionArray != null)
            for (TemplateLoaderCreatorDefinition define : creatorDefinitionArray)
                if (StringUtils.eqUnCaseSensitive(define.getName(), key))
                    return define.get();
        return null;
    }
}