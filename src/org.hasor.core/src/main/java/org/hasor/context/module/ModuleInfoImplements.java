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
package org.hasor.context.module;
import java.util.Collections;
import java.util.List;
import org.hasor.Hasor;
import org.hasor.context.AppContext;
import org.hasor.context.Dependency;
import org.hasor.context.HasorModule;
import org.hasor.context.ModuleSettings;
/**
 * 
 * @version : 2013-7-26
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class ModuleInfoImplements implements ModuleSettings {
    private String           displayName;
    private String           description;
    private HasorModule      moduleObject;
    private String           namespace;
    private AppContext       appContext;
    private List<Dependency> allDependency;
    private List<Dependency> beforeDependency;
    private List<Dependency> afterDependency;
    //
    public ModuleInfoImplements(HasorModule moduleObject, AppContext appContext) {
        Hasor.assertIsNotNull(moduleObject);
        this.moduleObject = moduleObject;
        this.appContext = appContext;
    }
    @Override
    public void beforeMe(Class<? extends HasorModule> targetModule) {
        // TODO Auto-generated method stub
    }
    @Override
    public void afterMe(Class<? extends HasorModule> targetModule) {
        // TODO Auto-generated method stub
    }
    @Override
    public void followTarget(Class<? extends HasorModule> targetModule) {
        // TODO Auto-generated method stub
    }
    @Override
    public List<Dependency> getDependency() {
        return Collections.unmodifiableList(this.allDependency);
    }
    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    @Override
    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public String getDisplayName() {
        return this.displayName;
    }
    @Override
    public String getDescription() {
        return this.description;
    }
    @Override
    public HasorModule getModuleObject() {
        return this.moduleObject;
    }
    @Override
    public void bindingSettingsNamespace(String namespace) {
        this.namespace = namespace;
    }
    @Override
    public String getSettingsNamespace() {
        return this.namespace;
    }
}