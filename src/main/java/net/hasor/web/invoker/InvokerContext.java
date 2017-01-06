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
import net.hasor.web.definition.AbstractDefinition;
import net.hasor.web.definition.WebPluginDefinition;
import org.more.future.BasicFuture;
import org.more.util.Iterators;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.Future;
/**
 * 上下文。
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
public class InvokerContext implements WebPluginCaller {
    private AppContext           appContext     = null;
    private InnerMappingData[]   invokeArray    = new InnerMappingData[0];
    private AbstractDefinition[] filters        = new AbstractDefinition[0];
    private WebPlugin[]          plugins        = new WebPlugin[0];
    private RootInvokerCreater   invokerCreater = null;
    //
    public void initContext(final AppContext appContext, final Map<String, String> configMap) throws Throwable {
        this.appContext = Hasor.assertIsNotNull(appContext);
        final Map<String, String> config = Collections.unmodifiableMap(new HashMap<String, String>(configMap));
        //
        // .MappingData
        List<InnerMappingDataDefinition> mappingList = appContext.findBindingBean(InnerMappingDataDefinition.class);
        Collections.sort(mappingList, new Comparator<InnerMappingDataDefinition>() {
            public int compare(InnerMappingDataDefinition o1, InnerMappingDataDefinition o2) {
                return o1.getMappingTo().compareToIgnoreCase(o2.getMappingTo()) * -1;
            }
        });
        this.invokeArray = mappingList.toArray(new InnerMappingData[mappingList.size()]);
        //
        // .WebPlugin
        List<WebPluginDefinition> pluginList = appContext.findBindingBean(WebPluginDefinition.class);
        this.plugins = pluginList.toArray(new WebPlugin[pluginList.size()]);
        for (WebPluginDefinition plugin : pluginList) {
            plugin.initPlugin(appContext);
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
        final InvokerConfig filterConfig = new InvokerConfig() {
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
        List<AbstractDefinition> filterList = appContext.findBindingBean(AbstractDefinition.class);
        this.filters = filterList.toArray(new AbstractDefinition[filterList.size()]);
        for (InvokerFilter filter : filterList) {
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
    }
    //
    public Invoker newInvoker(HttpServletRequest request, HttpServletResponse response) {
        return this.invokerCreater.createExt(new InvokerSupplier(this.appContext, request, response));
    }
    //
    public ExceuteCaller genCaller(Invoker invoker) {
        InnerMappingData foundDefine = null;
        for (InnerMappingData define : this.invokeArray) {
            if (define.matchingMapping(invoker)) {
                foundDefine = define;
                break;
            }
        }
        if (foundDefine == null) {
            return new ExceuteCaller() {
                @Override
                public Future<Object> invoke(Invoker invoker, final FilterChain chain) throws Throwable {
                    new InvokerChainInvocation(filters, new InvokerChain() {
                        @Override
                        public void doNext(Invoker invoker) throws Throwable {
                            chain.doFilter(invoker.getHttpRequest(), invoker.getHttpResponse());
                        }
                    }).doNext(invoker);
                    //
                    BasicFuture<Object> future = new BasicFuture<Object>();
                    future.completed(null);
                    return future;
                }
            };
        }
        return new InvokerCaller(foundDefine, this.filters, this);
    }
    @Override
    public void beforeFilter(Invoker invoker, InvokerData define) {
        for (WebPlugin plugin : plugins) {
            plugin.beforeFilter(invoker, define);
        }
    }
    @Override
    public void afterFilter(Invoker invoker, InvokerData define) {
        for (WebPlugin plugin : plugins) {
            plugin.afterFilter(invoker, define);
        }
    }
}