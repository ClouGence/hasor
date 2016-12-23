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
package net.hasor.web.pipeline;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import org.more.util.Iterators;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
/**
 *
 * @version : 2013-4-11
 * @author 赵永春 (zyc@hasor.net)
 */
class FilterDefinition extends AbstractServletModuleBinding {
    private BindInfo<Filter> filterRegister = null;
    private Filter           filterInstance = null;
    //
    public FilterDefinition(final int index, final String pattern, final UriPatternMatcher uriPatternMatcher, final BindInfo<Filter> filterRegister, final Map<String, String> initParams) {
        super(index, initParams, pattern, uriPatternMatcher);
        this.filterRegister = filterRegister;
    }
    protected Filter getTarget() throws ServletException {
        if (this.filterInstance != null) {
            return this.filterInstance;
        }
        //
        final Map<String, String> initParams = this.getInitParams();
        this.filterInstance = this.getAppContext().getInstance(this.filterRegister);
        this.filterInstance.init(new FilterConfig() {
            @Override
            public String getFilterName() {
                return (filterInstance == null ? filterRegister : filterInstance).toString();
            }
            @Override
            public ServletContext getServletContext() {
                return getAppContext().getInstance(ServletContext.class);
            }
            @Override
            public String getInitParameter(final String s) {
                return initParams.get(s);
            }
            @Override
            public Enumeration<String> getInitParameterNames() {
                return Iterators.asEnumeration(initParams.keySet().iterator());
            }
        });
        return this.filterInstance;
    }
    @Override
    public String toString() {
        return String.format("type %s pattern=%s ,initParams=%s ,uriPatternType=%s", //
                FilterDefinition.class, this.getPattern(), this.getInitParams(), this.getUriPatternType());
    }
    /*--------------------------------------------------------------------------------------------------------*/
    /**/
    public void init(final AppContext appContext, final Map<String, String> filterConfig) throws ServletException {
        super.init(appContext);
        //
        if (filterConfig != null) {
            Map<String, String> thisConfig = this.getInitParams();
            for (Entry<String, String> ent : filterConfig.entrySet()) {
                String key = ent.getKey();
                if (!thisConfig.containsKey(key)) {
                    thisConfig.put(key, ent.getValue());
                }
            }
        }
        //
        this.getTarget();
    }
    /**/
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        boolean serve = this.matchesUri(path);
        //
        Filter filter = this.getTarget();
        if (serve && filter != null) {
            filter.doFilter(request, response, chain);
        } else {
            chain.doFilter(httpRequest, response);
        }
    }
    /**/
    public void destroy(final AppContext appContext) {
        if (this.filterInstance == null) {
            return;
        }
        this.filterInstance.destroy();
    }
}