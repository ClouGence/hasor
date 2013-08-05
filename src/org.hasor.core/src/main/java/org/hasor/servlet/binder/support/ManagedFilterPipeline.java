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
package org.hasor.servlet.binder.support;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.hasor.context.AppContext;
import org.hasor.servlet.binder.FilterPipeline;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
/**
 *  
 * @version : 2013-4-12
 * @author 赵永春 (zyc@byshell.org)
 */
@Singleton
public class ManagedFilterPipeline implements FilterPipeline {
    private final ManagedServletPipeline servletPipeline;
    private final ManagedErrorPipeline   errorPipeline;
    private FilterDefinition[]           filterDefinitions;
    private volatile boolean             initialized = false;
    private AppContext                   appContext  = null;
    //
    //
    @Inject
    public ManagedFilterPipeline(ManagedServletPipeline servletPipeline, ManagedErrorPipeline errorPipeline) {
        this.servletPipeline = servletPipeline;
        this.errorPipeline = errorPipeline;
    }
    //
    @Override
    public synchronized void initPipeline(AppContext appContext) throws ServletException {
        if (initialized)
            return;
        this.appContext = appContext;
        this.filterDefinitions = collectFilterDefinitions(appContext.getGuice());
        for (FilterDefinition filterDefinition : filterDefinitions) {
            filterDefinition.init(appContext);
        }
        //next, initialize servlets...
        this.servletPipeline.initPipeline(appContext);
        this.errorPipeline.initPipeline(appContext);
        //everything was ok...
        this.initialized = true;
    }
    private FilterDefinition[] collectFilterDefinitions(Injector injector) {
        List<FilterDefinition> filterDefinitions = new ArrayList<FilterDefinition>();
        TypeLiteral<FilterDefinition> FILTER_DEFS = TypeLiteral.get(FilterDefinition.class);
        for (Binding<FilterDefinition> entry : injector.findBindingsByType(FILTER_DEFS)) {
            filterDefinitions.add(entry.getProvider().get());
        }
        // Convert to a fixed size array for speed.
        return filterDefinitions.toArray(new FilterDefinition[filterDefinitions.size()]);
    }
    @Override
    public void dispatch(HttpServletRequest request, HttpServletResponse response, FilterChain defaultFilterChain) throws IOException, ServletException {
        if (!initialized) {
            initPipeline(this.appContext);
        }
        try {
            /*执行过滤器链*/
            ServletRequest dispatcherRequest = withDispatcher(request, this.servletPipeline);
            new FilterChainInvocation(this.filterDefinitions, this.servletPipeline, defaultFilterChain).doFilter(dispatcherRequest, response);
        } catch (Exception e) {
            /*出现错误交给错误处理程序处理.*/
            this.errorPipeline.dispatch(request, response, e);
        }
    }
    @Override
    public void destroyPipeline(AppContext appContext) {
        //destroy servlets first
        this.servletPipeline.destroyPipeline(appContext);
        this.errorPipeline.destroyPipeline(appContext);
        //go down chain and destroy all our filters
        for (FilterDefinition filterDefinition : filterDefinitions) {
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
    private ServletRequest withDispatcher(ServletRequest servletRequest, final ManagedServletPipeline servletPipeline) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        // don't wrap the request if there are no servlets mapped. This prevents us from inserting our
        // wrapper unless it's actually going to be used. This is necessary for compatibility for apps
        // that downcast their HttpServletRequests to a concrete implementation.
        if (!servletPipeline.hasServletsMapped()) {
            return servletRequest;
        }
        //noinspection OverlyComplexAnonymousInnerClass
        return new HttpServletRequestWrapper(request) {
            @Override
            public RequestDispatcher getRequestDispatcher(String path) {
                final RequestDispatcher dispatcher = servletPipeline.getRequestDispatcher(path);
                return (null != dispatcher) ? dispatcher : super.getRequestDispatcher(path);
            }
        };
    }
}