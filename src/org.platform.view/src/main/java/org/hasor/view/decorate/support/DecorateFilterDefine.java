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
package org.hasor.view.decorate.support;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.hasor.MoreFramework;
import org.hasor.context.AppContext;
import org.hasor.view.decorate.DecorateFilter;
import org.hasor.view.decorate.DecorateFilterChain;
import org.hasor.view.decorate.DecorateServletRequest;
import org.hasor.view.decorate.DecorateServletResponse;
import org.more.util.Iterators;
import org.more.util.StringUtils;
import com.google.inject.Key;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-6-19
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class DecorateFilterDefine implements Provider<DecorateFilterDefine> {
    private String                        pattern           = null;
    private Key<? extends DecorateFilter> filterKey         = null;
    private UriPatternMatcher             patternMatcher    = null;
    private Map<String, String>           initParams        = null;
    private DecorateFilter                decFilterInstance = null;
    private AppContext                    appContext        = null;
    private String                        contentType       = null;
    //
    //
    public DecorateFilterDefine(String pattern, String contentType, Key<? extends DecorateFilter> filterKey, UriPatternMatcher patternMatcher, Map<String, String> initParams, DecorateFilter decFilterInstance) {
        this.pattern = pattern;
        this.contentType = contentType;
        this.filterKey = filterKey;
        this.patternMatcher = patternMatcher;
        this.initParams = initParams;
        this.decFilterInstance = decFilterInstance;
    }
    @Override
    public DecorateFilterDefine get() {
        return this;
    }
    protected DecorateFilter getTarget(final AppContext appContext) throws ServletException {
        if (this.decFilterInstance == null)
            this.decFilterInstance = appContext.getGuice().getInstance(this.filterKey);
        this.decFilterInstance.init(new FilterConfig() {
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
        return this.decFilterInstance;
    }
    @Override
    public String toString() {
        return MoreFramework.formatString("type %s pattern=%s ,initParams=%s ,uriPatternType=%s",//
                DecorateFilterDefine.class, pattern, initParams, patternMatcher);
    }
    /** Returns true if the given URI will match this binding. */
    public boolean matches(HttpServletRequest httpRequest) {
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        return patternMatcher.matches(path);
    }
    /*--------------------------------------------------------------------------------------------------------*/
    public void init(final AppContext appContext) throws ServletException {
        this.appContext = appContext;
        this.getTarget(appContext);
    }
    public void doDecorate(DecorateServletRequest request, DecorateServletResponse response, DecorateFilterChain chain) throws ServletException, IOException {
        boolean serve = this.matches(request);
        if (serve)
            serve = StringUtils.eqUnCaseSensitive(response.getContentType(), this.contentType);
        //
        DecorateFilter decorateFilter = this.getTarget(this.appContext);
        //
        if (serve == true && decorateFilter != null) {
            decorateFilter.doDecorate(request, response, chain);
        } else {
            chain.doDecorate(request, response);
        }
    }
    public void destroy() {
        if (this.decFilterInstance != null)
            this.decFilterInstance.destroy();
    }
}