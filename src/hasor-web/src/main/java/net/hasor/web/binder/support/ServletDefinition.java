/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
import static net.hasor.web.binder.support.ManagedServletPipeline.REQUEST_DISPATCHER_REQUEST;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import net.hasor.core.Provider;
import net.hasor.web.WebAppContext;
import org.more.util.Iterators;
/**
 * 
 * @version : 2013-4-11
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
class ServletDefinition extends AbstractServletModuleBinding implements Provider<ServletDefinition> {
    private Provider<HttpServlet> servletProvider = null;
    private HttpServlet           servletInstance = null;
    private UriPatternMatcher     patternMatcher  = null;
    private WebAppContext         appContext      = null;
    //
    public ServletDefinition(int index, String pattern, UriPatternMatcher uriPatternMatcher, Provider<HttpServlet> servletProvider, Map<String, String> initParams) {
        super(index, initParams, pattern, uriPatternMatcher);
        this.servletProvider = servletProvider;
        this.patternMatcher = uriPatternMatcher;
    }
    public ServletDefinition get() {
        return this;
    }
    protected HttpServlet getTarget() throws ServletException {
        if (this.servletInstance != null)
            return this.servletInstance;
        //
        final Map<String, String> initParams = this.getInitParams();
        this.servletInstance = this.servletProvider.get();
        this.servletInstance.init(new ServletConfig() {
            public String getServletName() {
                return ((servletInstance == null) ? servletProvider : servletInstance).toString();
            }
            public ServletContext getServletContext() {
                return appContext.getServletContext();
            }
            public String getInitParameter(String s) {
                return initParams.get(s);
            }
            public Enumeration<String> getInitParameterNames() {
                return Iterators.asEnumeration(initParams.keySet().iterator());
            }
        });
        return this.servletInstance;
    }
    public String toString() {
        return String.format("type %s pattern=%s ,initParams=%s ,uriPatternType=%s",//
                ServletDefinition.class, getPattern(), getInitParams(), getUriPatternType());
    }
    /*--------------------------------------------------------------------------------------------------------*/
    /**/
    public void init(final WebAppContext appContext) throws ServletException {
        this.appContext = appContext;
        this.getTarget();
    }
    /**/
    public boolean service(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        boolean serve = this.matchesUri(path);
        // 
        if (serve)
            doService(request, response);
        return serve;
    }
    /**/
    private void doService(final ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        HttpServletRequest request = new HttpServletRequestWrapper((HttpServletRequest) servletRequest) {
            private String  path;
            private boolean pathComputed     = false;
            //must use a boolean on the memo field, because null is a legal value (TODO no, it's not)
            private boolean pathInfoComputed = false;
            private String  pathInfo;
            public String getPathInfo() {
                if (!isPathInfoComputed()) {
                    int servletPathLength = getServletPath().length();
                    pathInfo = getRequestURI().substring(getContextPath().length()).replaceAll("[/]{2,}", "/");
                    pathInfo = pathInfo.length() > servletPathLength ? pathInfo.substring(servletPathLength) : null;
                    // Corner case: when servlet path and request path match exactly (without trailing '/'),
                    // then pathinfo is null
                    if ("".equals(pathInfo) && servletPathLength != 0) {
                        pathInfo = null;
                    }
                    pathInfoComputed = true;
                }
                return pathInfo;
            }
            // NOTE(dhanji): These two are a bit of a hack to help ensure that request dipatcher-sent
            // requests don't use the same path info that was memoized for the original request.
            private boolean isPathInfoComputed() {
                return pathInfoComputed && !(null != servletRequest.getAttribute(REQUEST_DISPATCHER_REQUEST));
            }
            private boolean isPathComputed() {
                return pathComputed && !(null != servletRequest.getAttribute(REQUEST_DISPATCHER_REQUEST));
            }
            public String getServletPath() {
                return computePath();
            }
            public String getPathTranslated() {
                final String info = getPathInfo();
                return (null == info) ? null : getRealPath(info);
            }
            // Memoizer pattern.
            private String computePath() {
                if (!isPathComputed()) {
                    String servletPath = super.getServletPath();
                    path = patternMatcher.extractPath(servletPath);
                    pathComputed = true;
                    if (null == path) {
                        path = servletPath;
                    }
                }
                return path;
            }
        };
        //
        HttpServlet servlet = this.getTarget();
        if (servlet == null)
            return;
        servlet.service(request, servletResponse);
    }
    /**/
    public void destroy() {
        if (this.servletInstance == null)
            return;
        this.servletInstance.destroy();
    }
}