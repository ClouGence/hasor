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
package net.hasor.web.definition;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerData;
import net.hasor.web.WebPlugin;
/**
 * WebPlugin 定义
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class WebPluginDefinition implements WebPlugin {
    private BindInfo<? extends WebPlugin> bindInfo   = null;
    private WebPlugin                     instance   = null;
    private AppContext                    appContext = null;
    //
    public WebPluginDefinition(final BindInfo<? extends WebPlugin> bindInfo) {
        this.bindInfo = bindInfo;
    }
    //
    protected WebPlugin getTarget() {
        if (this.instance == null) {
            this.instance = this.appContext.getInstance(this.bindInfo);
        }
        return this.instance;
    }
    //
    @Override
    public String toString() {
        return String.format("type %s pluginKey=%s", //
                WebPluginDefinition.class, this.instance);
    }
    //
    /*--------------------------------------------------------------------------------------------------------*/
    public void initPlugin(AppContext appContext) {
        this.appContext = appContext;
        this.getTarget();
    }
    @Override
    public void beforeFilter(Invoker invoker, InvokerData define) {
        WebPlugin plugin = this.getTarget();
        if (plugin != null) {
            plugin.beforeFilter(invoker, define);
        }
    }
    @Override
    public void afterFilter(Invoker invoker, InvokerData define) {
        WebPlugin plugin = this.getTarget();
        if (plugin != null) {
            plugin.afterFilter(invoker, define);
        }
    }
}