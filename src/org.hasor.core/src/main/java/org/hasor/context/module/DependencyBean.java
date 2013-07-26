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
import java.util.List;
import org.hasor.context.Dependency;
import org.hasor.context.ModuleInfo;
/**
 * 
 * @version : 2013-7-26
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class DependencyBean implements Dependency {
    private ModuleInfo moduleInfo = null;
    private boolean    option     = false;
    //
    public DependencyBean(ModuleInfo moduleInfo, boolean option) {
        this.moduleInfo = moduleInfo;
        this.option = option;
    }
    @Override
    public ModuleInfo getModuleInfo() {
        return this.moduleInfo;
    }
    @Override
    public boolean isOption() {
        return this.option;
    }
    @Override
    public List<Dependency> getDependency() {
        // TODO Auto-generated method stub
        return null;
    }
}