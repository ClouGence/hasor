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
package net.hasor.core.plugin;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.context.AnnoModule;
/**
 * 插件体系支持
 * @version : 2013-4-8
 * @author 赵永春 (zyc@hasor.net)
 */
@AnnoModule()
public class PluginsSupportModule implements Module {
    /**初始化.*/
    public void init(ApiBinder apiBinder) {
        Set<Class<?>> pluginSet = apiBinder.findClass(Plugin.class);
        if (pluginSet == null)
            return;
        //
        Map<Class<?>, String> loadState = new HashMap<Class<?>, String>();
        for (Class<?> pluginClass : pluginSet) {
            if (HasorPlugin.class.isAssignableFrom(pluginClass) == false) {
                Hasor.logWarn("not implemented PluginFace :%s", pluginClass);
                continue;
            }
            try {
                HasorPlugin hasorPlugin = (HasorPlugin) pluginClass.newInstance();
                Hasor.logInfo("loadPlugin %s.", pluginClass);
                hasorPlugin.loadPlugin(apiBinder);
                loadState.put(pluginClass, "<-- OK.");
                apiBinder.bindingType(HasorPlugin.class).toInstance(hasorPlugin);
            } catch (Throwable e) {
                loadState.put(pluginClass, "<-- Error.");
                Hasor.logError("config Plugin error at %s.%s", pluginClass, e);
            }
        }
        //
        if (Hasor.isInfoLogger()) {
            StringBuffer sb = new StringBuffer();
            for (Entry<Class<?>, String> e : loadState.entrySet()) {
                sb.append("\n  " + e.getKey().getName());
                sb.append(e.getValue());
            }
            String outData = (sb.length() == 0 ? "nothing." : sb.toString());
            Hasor.logInfo("find Plugin : " + outData);
        }
    }
    /***/
    public void start(AppContext appContext) {}
    /***/
    public void stop(AppContext appContext) {}
}