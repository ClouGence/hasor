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
import net.hasor.utils.Iterators;
import net.hasor.utils.future.BasicFuture;
import net.hasor.web.*;
import net.hasor.web.definition.AbstractDefinition;
import net.hasor.web.definition.WebPluginDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.Future;
/**
 * 上下文。
 * @version : 2017-01-10
 * @author 赵永春(zyc@hasor.net)
 */
public class InvokerContext implements WebPluginCaller {
    protected Logger               logger         = LoggerFactory.getLogger(getClass());
    private   AppContext           appContext     = null;
    private   InMapping[]          invokeArray    = new InMapping[0];
    private   AbstractDefinition[] filters        = new AbstractDefinition[0];
    private   WebPlugin[]          plugins        = new WebPlugin[0];
    private   RootInvokerCreater   invokerCreater = null;
    //
    public void initContext(final AppContext appContext, final Map<String, String> configMap) throws Throwable {
        this.appContext = Hasor.assertIsNotNull(appContext);
        final Map<String, String> config = Collections.unmodifiableMap(new HashMap<String, String>(configMap));
        //
        // .MappingData
        List<InMappingDef> mappingList = appContext.findBindingBean(InMappingDef.class);
        Collections.sort(mappingList, new Comparator<InMappingDef>() {
            public int compare(InMappingDef o1, InMappingDef o2) {
                return o1.getMappingTo().compareToIgnoreCase(o2.getMappingTo()) * -1;
            }
        });
        Collections.sort(mappingList, new Comparator<InMappingDef>() {
            public int compare(InMappingDef o1, InMappingDef o2) {
                long o1Index = o1.getIndex();
                long o2Index = o2.getIndex();
                return o1Index < o2Index ? -1 : o1Index == o2Index ? 0 : 1;
            }
        });
        this.invokeArray = mappingList.toArray(new InMapping[mappingList.size()]);
        for (InMapping inMapping : this.invokeArray) {
            logger.info("mapingTo -> type ‘{}’ mappingTo: ‘{}’.", inMapping.getTargetType().getBindType(), inMapping.getMappingTo());
        }
        //
        // .WebPlugin
        List<WebPluginDefinition> pluginList = appContext.findBindingBean(WebPluginDefinition.class);
        this.plugins = pluginList.toArray(new WebPlugin[pluginList.size()]);
        for (WebPluginDefinition plugin : pluginList) {
            plugin.initPlugin(appContext);
            logger.info("webPlugin -> type ‘{}’.", plugin.toString());
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
        // .Filter Config
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
        //
        // .Filters
        ArrayList<AbstractDefinition> finalList = new ArrayList<AbstractDefinition>();
        List<AbstractDefinition> filterList = appContext.findBindingBean(AbstractDefinition.class);
        Collections.sort(filterList, new Comparator<AbstractDefinition>() {
            public int compare(AbstractDefinition o1, AbstractDefinition o2) {
                long o1Index = o1.getIndex();
                long o2Index = o2.getIndex();
                return o1Index < o2Index ? -1 : o1Index == o2Index ? 0 : 1;
            }
        });
        finalList.addAll(filterList);
        //
        // .init
        for (InvokerFilter filter : finalList) {
            filter.init(filterConfig);
        }
        this.filters = finalList.toArray(new AbstractDefinition[finalList.size()]);
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
        InMapping foundDefine = null;
        for (InMapping define : this.invokeArray) {
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