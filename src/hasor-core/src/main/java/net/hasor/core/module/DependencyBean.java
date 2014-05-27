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
package net.hasor.core.module;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.hasor.core.Dependency;
import net.hasor.core.ModuleInfo;
/**
 * 
 * @version : 2013-7-26
 * @author 赵永春 (zyc@hasor.net)
 */
class DependencyBean implements Dependency {
    private ModuleInfo       moduleInfo     = null;
    private boolean          option         = false;
    private List<Dependency> dependencyLiat = null;
    //
    public DependencyBean(ModuleInfo moduleInfo, boolean option) {
        this.moduleInfo = moduleInfo;
        this.option = option;
    }
    public ModuleInfo getModuleInfo() {
        return this.moduleInfo;
    }
    public boolean isOption() {
        return this.option;
    }
    public String toString() {
        return String.format("name %s, option is %s."//
                , this.moduleInfo.getDisplayName(), this.option);
    }
    public List<Dependency> getDependency() {
        return Collections.unmodifiableList(this.dependencyLiat);
    }
    public void updateDependency(List<Dependency> depLiat) {
        if (this.dependencyLiat == null)
            this.dependencyLiat = new ArrayList<Dependency>();
        this.dependencyLiat.clear();
        this.dependencyLiat.addAll(depLiat);
    }
}