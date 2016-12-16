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
package net.hasor.web.binder.support;
import net.hasor.web.WebAppContext;
import net.hasor.web.binder.FilterPipeline;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
/**
 *
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
public class ManagedFilterPipeline implements FilterPipeline {
    private final ManagedServletPipeline servletPipeline;
    private       FilterDefinition[]     filterDefinitions;
    private volatile boolean initialized = false;
    private WebAppContext appContext;
    //
    //
    public ManagedFilterPipeline(final ManagedServletPipeline servletPipeline) {
        this.servletPipeline = servletPipeline;
    }
    //
    @Override
    public synchronized void initPipeline(final WebAppContext appContext, final Map<String, String> filterConfig) throws ServletException {
        if (this.initialized) {
            return;
        }
        this.appContext = appContext;
        this.filterDefinitions = this.collectFilterDefinitions(appContext);
        for (FilterDefinition filterDefinition : this.filterDefinitions) {
            filterDefinition.init(appContext, filterConfig);
        }
        //next, initialize servlets...
        this.servletPipeline.initPipeline(appContext, filterConfig);
        //everything was ok...
        this.initialized = true;
    }
    private FilterDefinition[] collectFilterDefinitions(final WebAppContext appContext) {
        List<FilterDefinition> filterDefinitions = appContext.findBindingBean(FilterDefinition.class);
        Collections.sort(filterDefinitions, new Comparator<FilterDefinition>() {
            @Override
            public int compare(final FilterDefinition o1, final FilterDefinition o2) {
                int o1Index = o1.getIndex();
                int o2Index = o2.getIndex();
                return o1Index < o2Index ? -1 : o1Index == o2Index ? 0 : 1;
            }
        });
        return filterDefinitions.toArray(new FilterDefinition[filterDefinitions.size()]);
    }
    @Override
    public void dispatch(final HttpServletRequest request, final HttpServletResponse response, final FilterChain defaultFilterChain) throws IOException, ServletException {
        if (!this.initialized) {
            this.initPipeline(this.appContext, null);
        }
        //
        /*执行过滤器链*/
        HttpServletRequest dispatcherRequest = this.withDispatcher(request, this.servletPipeline);
        FilterChainInvocation invocation = new FilterChainInvocation(this.filterDefinitions, this.servletPipeline, defaultFilterChain);
        invocation.doFilter(dispatcherRequest, response);
    }
    @Override
    public void destroyPipeline(final WebAppContext appContext) {
        //destroy servlets first
        this.servletPipeline.destroyPipeline(appContext);
        //go down chain and destroy all our filters
        for (FilterDefinition filterDefinition : this.filterDefinitions) {
            filterDefinition.destroy(appContext);
        }
    }
    /**
     * Used to create an proxy that dispatches either to the guice-servlet pipeline or the regular
     * pipeline based on uri-path match. This proxy also provides minimal forwarding support.
     *
     * We cannot forward from a web.xml Servlet/JSP to a guice-servlet (because the filter pipeline
     * is not called again). However, we can wrap requests with our own dispatcher to forward the
     * *other* way. web.xml Servlets/JSPs can forward to themselves as per normal.
     *
     * This is not a problem cuz we intend for people to migrate from web.xml to guice-servlet,
     * incrementally, but not the other way around (which, we should actively discourage).
     */
    private HttpServletRequest withDispatcher(final HttpServletRequest httpRequest, final ManagedServletPipeline servletPipeline) {
        // don't wrap the request if there are no servlets mapped. This prevents us from inserting our
        // wrapper unless it's actually going to be used. This is necessary for compatibility for apps
        // that downcast their HttpServletRequests to a concrete implementation.
        if (!servletPipeline.hasServletsMapped()) {
            return httpRequest;
        }
        //noinspection OverlyComplexAnonymousInnerClass
        return new HttpServletRequestWrapper(httpRequest) {
            @Override
            public RequestDispatcher getRequestDispatcher(final String path) {
                final RequestDispatcher dispatcher = servletPipeline.getRequestDispatcher(path);
                return null != dispatcher ? dispatcher : super.getRequestDispatcher(path);
            }
        };
    }
}