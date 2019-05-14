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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
/**
 * 上下文。
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class InvokerContext {
    protected static Logger               logger         = LoggerFactory.getLogger(InvokerContext.class);
    private          AppContext           appContext     = null;
    private          Mapping[]            invokeArray    = new Mapping[0];
    private          AbstractDefinition[] filters        = new AbstractDefinition[0];
    private          RootInvokerCreater   invokerCreater = null;
    //
    public void initContext(final AppContext appContext, final Map<String, String> configMap) throws Throwable {
        this.appContext = Hasor.assertIsNotNull(appContext);
        //
        // .MappingData
        List<InMappingDef> mappingList = appContext.findBindingBean(InMappingDef.class);
        mappingList.sort(Comparator.comparingLong(InMappingDef::getIndex));
        this.invokeArray = mappingList.toArray(new Mapping[0]);
        for (Mapping inMapping : this.invokeArray) {
            logger.info("mapingTo -> type ‘{}’ mappingTo: ‘{}’.", inMapping.getTargetType().getBindType(), inMapping.getMappingTo());
        }
        //
        // .discover
        List<MappingDiscoverer> setupList = appContext.findBindingBean(MappingDiscoverer.class);
        for (MappingDiscoverer setup : setupList) {
            for (Mapping mapping : this.invokeArray) {
                setup.discover(mapping);
            }
        }
        //
        // .Filter Config
        final Map<String, String> config = Collections.unmodifiableMap(new HashMap<>(configMap));
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
        this.filters = appContext.findBindingBean(AbstractDefinition.class).stream()//
                .sorted(Comparator.comparingLong(AbstractDefinition::getIndex))     //
                .toArray(AbstractDefinition[]::new);                                //
        // .init
        for (InvokerFilter filter : this.filters) {
            filter.init(filterConfig);
        }
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
    public Invoker newInvoker(Mapping define, HttpServletRequest request, HttpServletResponse response) {
        return this.invokerCreater.createExt(new InvokerSupplier(define, this.appContext, request, response));
    }
    //
    public ExceuteCaller genCaller(HttpServletRequest httpReq, HttpServletResponse httpRes) {
        Mapping foundDefine = null;
        for (Mapping define : this.invokeArray) {
            if (define.matchingMapping(httpReq)) {
                foundDefine = define;
                break;
            }
        }
        final Invoker invoker = this.newInvoker(foundDefine, httpReq, httpRes);
        if (foundDefine == null) {
            return (chain) -> {
                BasicFuture<Object> future = new BasicFuture<>();
                future.completed(new InvokerChainInvocation(filters, innerInv -> {
                    if (chain != null) {
                        chain.doFilter(innerInv.getHttpRequest(), innerInv.getHttpResponse());
                    }
                    return innerInv.get(Invoker.RETURN_DATA_KEY);
                }).doNext(invoker));
                return future;
            };
        }
        //
        return new InvokerCaller(() -> invoker, this.filters);
    }
}