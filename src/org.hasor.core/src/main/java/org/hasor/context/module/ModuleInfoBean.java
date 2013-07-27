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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hasor.Hasor;
import org.hasor.context.AppContext;
import org.hasor.context.Dependency;
import org.hasor.context.HasorModule;
import org.hasor.context.ModuleInfo;
import org.hasor.context.ModuleSettings;
import org.more.UndefinedException;
/**
 * 
 * @version : 2013-7-26
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public final class ModuleInfoBean implements ModuleSettings {
    private String           displayName  = null;
    private String           description  = null;
    private boolean          init         = false;
    private HasorModule      moduleObject = null;
    private String           namespace    = null;
    private AppContext       appContext   = null;
    private List<Dependency> dependency   = new ArrayList<Dependency>();
    //
    //
    public ModuleInfoBean(HasorModule moduleObject, AppContext appContext) {
        Hasor.assertIsNotNull(moduleObject);
        this.moduleObject = moduleObject;
        this.appContext = appContext;
    }
    private ModuleInfo getInfo(Class<? extends HasorModule> targetModule) {
        ModuleInfo[] infoArray = this.appContext.getModules();
        if (infoArray != null)
            for (ModuleInfo info : infoArray)
                if (targetModule == info.getModuleObject().getClass())
                    return info;
        throw new UndefinedException(targetModule.getName() + " module is Undefined!");
    }
    @Override
    public void beforeMe(Class<? extends HasorModule> targetModule) {
        if (init)
            throw new IllegalStateException("HasorModule is initialized");
        //
        ModuleInfo targetModuleInfo = this.getInfo(targetModule);
        for (Dependency dep : this.dependency)
            if (dep.getModuleInfo() == targetModuleInfo)
                throw new IllegalStateException("before dependence is included.");
        //
        Dependency dep = new DependencyBean(targetModuleInfo, true);
        this.dependency.add(dep);
    }
    @Override
    public void followTarget(Class<? extends HasorModule> targetModule) {
        if (init)
            throw new IllegalStateException("HasorModule is initialized");
        //
        ModuleInfo targetModuleInfo = this.getInfo(targetModule);
        for (Dependency dep : this.dependency)
            if (dep.getModuleInfo() == targetModuleInfo)
                throw new IllegalStateException("before dependence is included.");
        //
        Dependency dep = new DependencyBean(targetModuleInfo, false);
        this.dependency.add(dep);
    }
    @Override
    public List<Dependency> getDependency() {
        if (init)
            throw new IllegalStateException("it is not clean.");
        return Collections.unmodifiableList(this.dependency);
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
    public void bindingSettingsNamespace(String namespace) {
        if (init)
            throw new IllegalStateException("HasorModule is initialized");
        this.namespace = namespace;
    }
    @Override
    public String getSettingsNamespace() {
        return this.namespace;
    }
    @Override
    public HasorModule getModuleObject() {
        return this.moduleObject;
    }
    @Override
    public String toString() {
        return Hasor.formatString("displayName is %s, class is %s",//
                this.displayName, this.moduleObject.getClass());
    }
    @Override
    public void afterMe(Class<? extends HasorModule> targetModule) {
        ModuleSettings setting = (ModuleSettings) this.getInfo(targetModule);
        setting.beforeMe(this.getModuleObject().getClass());
    }
}