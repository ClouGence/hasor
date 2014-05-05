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
package net.hasor.plugins.guice;
import java.util.Set;
import net.hasor.core.ApiBinder;
import net.hasor.core.Hasor;
import net.hasor.core.plugin.AbstractHasorPlugin;
import net.hasor.quick.plugin.Plugin;
import com.google.inject.Module;
/**
 * 提供 <code>@GuiceModule</code>注解 功能支持。
 * @version : 2013-9-13
 * @author 赵永春 (zyc@byshell.org)
 */
@Plugin
public class GuicePlugin extends AbstractHasorPlugin {
    public void loadPlugin(ApiBinder apiBinder) {
        Set<Class<?>> guiceModuleSet = apiBinder.findClass(GuiceModule.class);
        if (guiceModuleSet == null || guiceModuleSet.isEmpty())
            return;
        Hasor.logInfo("find Module : " + Hasor.logString(guiceModuleSet));
        for (Class<?> moduleClass : guiceModuleSet) {
            if (com.google.inject.Module.class.isAssignableFrom(moduleClass) == false) {
                /*错误*/
                Hasor.logWarn("not implemented com.google.inject.Module :%s", moduleClass);
                continue;
            }
            try {
                apiBinder.getGuiceBinder().install((Module) moduleClass.newInstance());
                Hasor.logInfo("install com.google.inject.Module %s.", moduleClass);
            } catch (Exception e) {
                Hasor.logError("install com.google.inject.Module %s.%s", moduleClass, e);
            }
        }
    }
}