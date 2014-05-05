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
package net.hasor.core.plugin;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
/**
 * 
 * @version : 2013-11-4
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
public abstract class PluginHelper {
    public static Module toModule(Class<? extends HasorPlugin> pluginType) throws InstantiationException, IllegalAccessException {
        Hasor.assertIsNotNull(pluginType);
        return new PropxyPluginModule(pluginType);
    }
    public static Module toModule(HasorPlugin pluginBean) {
        Hasor.assertIsNotNull(pluginBean);
        return new PropxyPluginModule(pluginBean);
    }
    private static class PropxyPluginModule implements Module {
        private HasorPlugin pluginBean;
        public PropxyPluginModule(Class<? extends HasorPlugin> pluginType) throws InstantiationException, IllegalAccessException {
            this.pluginBean = pluginType.newInstance();
        }
        public PropxyPluginModule(HasorPlugin pluginBean) {
            this.pluginBean = pluginBean;
        }
        public void init(ApiBinder apiBinder) throws Throwable {
            apiBinder.bindingType(HasorPlugin.class).toInstance(pluginBean);
            pluginBean.loadPlugin(apiBinder);
            apiBinder.configModule().setDisplayName("PropxyPlugin-" + pluginBean.getClass().getSimpleName());
            apiBinder.configModule().setDescription(pluginBean.getClass().getName());
        }
        public void start(AppContext appContext) {}
    }
}