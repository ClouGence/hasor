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
package org.platform.view.freemarker.support;
import com.google.inject.Provider;
import freemarker.template.TemplateModel;
/**
 * 
 * @version : 2013-5-24
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class FmObjectDefinition implements Provider<TemplateModel> {
    private String        objName      = null;
    private TemplateModel targetObject = null;
    //
    public FmObjectDefinition(String objName, TemplateModel targetObject) {
        this.objName = objName;
        this.targetObject = targetObject;
    }
    public String getName() {
        return this.objName;
    }
    @Override
    public TemplateModel get() {
        return this.targetObject;
    }
}