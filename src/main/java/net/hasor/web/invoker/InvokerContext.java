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
package net.hasor.web.invoker;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.web.*;
import org.more.util.Iterators;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
/**
 * 上下文参数。
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
class InvokerContext {
    private AppContext         appContext     = null;
    private MappingDataInfo[]  invokeArray    = new MappingDataInfo[0];
    private InvokerFilter[]    filters        = new InvokerFilter[0];
    private WebPlugin[]        plugins        = new WebPlugin[0];
    private RootInvokerCreater invokerCreater = null;
    //
    public Invoker newInvoker(HttpServletRequest request, HttpServletResponse response) {
        return this.invokerCreater.createExt(new InvokerSupplier(this.appContext, request, response));
    }
    //
    public InvokerCaller genCaller(Invoker invoker) {
        for (MappingDataInfo define : this.invokeArray) {
            if (define.matchingMapping(invoker)) {
                return new InvokerCaller(define, this.filters, new WebPluginCaller() {
                    @Override
                    public void beforeFilter(Invoker invoker, InvokerInfo define) {
                        InvokerContext.this.beforeFilter(invoker, define);
                    }
                    @Override
                    public void afterFilter(Invoker invoker, InvokerInfo define) {
                        InvokerContext.this.afterFilter(invoker, define);
                    }
                });
            }
        }
        return null;
    }
    //
    public void initContext(final AppContext appContext, final Map<String, String> configMap) throws Exception {
        this.appContext = Hasor.assertIsNotNull(appContext);
        final Map<String, String> config = Collections.unmodifiableMap(new HashMap<String, String>(configMap));
        //
        // .MappingData
        List<MappingDataInfo> mappingList = appContext.findBindingBean(MappingDataInfo.class);
        Collections.sort(mappingList, new Comparator<MappingDataInfo>() {
            public int compare(MappingDataInfo o1, MappingDataInfo o2) {
                return o1.getMappingTo().compareToIgnoreCase(o2.getMappingTo()) * -1;
            }
        });
        this.invokeArray = mappingList.toArray(new MappingDataInfo[mappingList.size()]);
        //
        // .WebPlugin
        List<WebPluginDefinition> pluginList = appContext.findBindingBean(WebPluginDefinition.class);
        this.plugins = pluginList.toArray(new WebPlugin[pluginList.size()]);
        for (WebPlugin plugin : this.plugins) {
            plugin.initPlugin(appContext, configMap);
        }
        //
        // .setup
        List<MappingSetup> setupList = appContext.findBindingBean(MappingSetup.class);
        for (MappingSetup setup : setupList) {
            if (setup == null) {
                continue;
            }
            for (MappingData mapping : this.invokeArray) {
                if (mapping == null) {
                    continue;
                }
                setup.setup(mapping);
            }
        }
        //
        // .filters
        final InvokerFilterConfig filterConfig = new InvokerFilterConfig() {
            @Override
            public String getInitParameter(String name) {
                return config.get(name);
            }
            @Override
            public Enumeration<String> getInitParameterNames() {
                return Iterators.asEnumeration(config.keySet().iterator());
            }
            @Override
            public AppContext getAppContext() {
                return appContext;
            }
        };
        List<InvokeFilterDefinition> filterList = appContext.findBindingBean(InvokeFilterDefinition.class);
        this.filters = filterList.toArray(new InvokerFilter[filterList.size()]);
        for (InvokeFilterDefinition filter : filterList) {
            filter.init(filterConfig);
        }
        //
        // .creater
        this.invokerCreater = new RootInvokerCreater(appContext);
    }
    //
    public void destroyContext() {
        for (InvokerFilter filter : this.filters) {
            filter.destroy();
        }
        for (WebPlugin plugin : this.plugins) {
            plugin.destroy();
        }
    }
    //
    private void beforeFilter(Invoker invoker, InvokerInfo define) {
        for (WebPlugin plugin : this.plugins) {
            plugin.beforeFilter(invoker, define);
        }
    }
    //
    private void afterFilter(Invoker invoker, InvokerInfo define) {
        for (WebPlugin plugin : this.plugins) {
            plugin.afterFilter(invoker, define);
        }
    }
}