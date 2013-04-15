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
package org.platform.binder;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.more.util.Iterators;
import org.platform.context.AppContext;
import org.platform.context.ViewContext;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-4-11
 * @author 赵永春 (zyc@byshell.org)
 */
class FilterDefinition extends AbstractServletModuleBinding implements Provider<FilterDefinition> {
    private Key<? extends Filter> filterKey      = null; /*Filter对象既有可能绑定在这个Key上*/
    private Filter                filterInstance = null;
    //
    public FilterDefinition(String pattern, Key<? extends Filter> filterKey, UriPatternMatcher uriPatternMatcher, Map<String, String> initParams, Filter filterInstance) {
        super(initParams, pattern, uriPatternMatcher);
        this.filterKey = filterKey;
        this.filterInstance = filterInstance;
    }
    @Override
    public FilterDefinition get() {
        return this;
    }
    protected Filter getTarget(Injector injector) {
        if (this.filterInstance == null)
            this.filterInstance = injector.getInstance(this.filterKey);
        return this.filterInstance;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(FilterDefinition.class)//
                .append("pattern", getPattern())//
                .append("initParams", getInitParams())//
                .append("uriPatternType", getUriPatternType())//
                .toString();
    }
    /*--------------------------------------------------------------------------------------------------------*/
    /**/
    public void init(final AppContext appContext) throws ServletException {
        Filter filter = this.getTarget(appContext.getGuice());
        if (filter == null)
            return;
        final Map<String, String> initParams = this.getInitParams();
        //
        filter.init(new FilterConfig() {
            public String getFilterName() {
                return filterKey.toString();
            }
            public ServletContext getServletContext() {
                return appContext.getInitContext().getServletContext();
            }
            public String getInitParameter(String s) {
                return initParams.get(s);
            }
            public Enumeration<String> getInitParameterNames() {
                return Iterators.asEnumeration(initParams.keySet().iterator());
            }
        });
    }
    /**/
    public void doFilter(ViewContext viewContext, ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        boolean serve = this.matchesUri(path);
        //
        Filter filter = this.getTarget(viewContext.getGuice());
        //
        if (serve == true && filter != null) {
            filter.doFilter(request, response, chain);
        } else {
            chain.doFilter(httpRequest, response);
        }
    }
    /**/
    public void destroy(AppContext appContext) {
        Filter filter = this.getTarget(appContext.getGuice());
        if (filter == null)
            return;
        filter.destroy();
    }
}