/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.web.servlet.binder.support;
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
import net.hasor.core.AppContext;
import org.more.util.Iterators;
import com.google.inject.Key;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-4-11
 * @author 赵永春 (zyc@hasor.net)
 */
class FilterDefinition extends AbstractServletModuleBinding implements Provider<FilterDefinition> {
    private Key<? extends Filter> filterKey      = null; /*Filter对象既有可能绑定在这个Key上*/
    private Filter                filterInstance = null;
    private AppContext            appContext     = null;
    //
    public FilterDefinition(String pattern, Key<? extends Filter> filterKey, UriPatternMatcher uriPatternMatcher, Map<String, String> initParams, Filter filterInstance) {
        super(initParams, pattern, uriPatternMatcher);
        this.filterKey = filterKey;
        this.filterInstance = filterInstance;
    }
    public FilterDefinition get() {
        return this;
    }
    protected Filter getTarget(final AppContext appContext) throws ServletException {
        if (this.filterInstance != null)
            return this.filterInstance;
        //
        final Map<String, String> initParams = this.getInitParams();
        this.filterInstance = appContext.getGuice().getInstance(this.filterKey);
        this.filterInstance.init(new FilterConfig() {
            public String getFilterName() {
                return filterKey.toString();
            }
            public ServletContext getServletContext() {
                Object context = appContext.getContext();
                if (context instanceof ServletContext)
                    return (ServletContext) context;
                return null;
            }
            public String getInitParameter(String s) {
                return initParams.get(s);
            }
            public Enumeration<String> getInitParameterNames() {
                return Iterators.asEnumeration(initParams.keySet().iterator());
            }
        });
        return this.filterInstance;
    }
    public String toString() {
        return String.format("type %s pattern=%s ,initParams=%s ,uriPatternType=%s",//
                FilterDefinition.class, getPattern(), getInitParams(), getUriPatternType());
    }
    /*--------------------------------------------------------------------------------------------------------*/
    /**/
    public void init(final AppContext appContext) throws ServletException {
        this.appContext = appContext;
        this.getTarget(appContext);
    }
    /**/
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        boolean serve = this.matchesUri(path);
        //
        Filter filter = this.getTarget(this.appContext);
        //
        if (serve == true && filter != null) {
            filter.doFilter(request, response, chain);
        } else {
            chain.doFilter(httpRequest, response);
        }
    }
    /**/
    public void destroy(AppContext appContext) {
        if (this.filterInstance == null)
            return;
        this.filterInstance.destroy();
    }
}