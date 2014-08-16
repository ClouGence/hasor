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
package net.hasor.web.binder.support;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import net.hasor.core.BindInfo;
import net.hasor.web.WebAppContext;
import org.more.util.Iterators;
/**
 * 
 * @version : 2013-4-11
 * @author 赵永春 (zyc@hasor.net)
 */
class ServletDefinition extends AbstractServletModuleBinding {
    private BindInfo<HttpServlet> servletRegister = null;
    private HttpServlet               servletInstance = null;
    private UriPatternMatcher         patternMatcher  = null;
    //
    public ServletDefinition(final int index, final String pattern, final UriPatternMatcher uriPatternMatcher, final BindInfo<HttpServlet> servletRegister, final Map<String, String> initParams) {
        super(index, initParams, pattern, uriPatternMatcher);
        this.servletRegister = servletRegister;
        this.patternMatcher = uriPatternMatcher;
    }
    protected HttpServlet getTarget() throws ServletException {
        if (this.servletInstance != null) {
            return this.servletInstance;
        }
        //
        final Map<String, String> initParams = this.getInitParams();
        this.servletInstance = this.getAppContext().getInstance(this.servletRegister);
        this.servletInstance.init(new ServletConfig() {
            @Override
            public String getServletName() {
                return (servletInstance == null ? servletRegister : servletInstance).toString();
            }
            @Override
            public ServletContext getServletContext() {
                return getAppContext().getServletContext();
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
        return this.servletInstance;
    }
    @Override
    public String toString() {
        return String.format("type %s pattern=%s ,initParams=%s ,uriPatternType=%s",//
                ServletDefinition.class, this.getPattern(), this.getInitParams(), this.getUriPatternType());
    }
    /*--------------------------------------------------------------------------------------------------------*/
    /**/
    public void init(final WebAppContext appContext, final Map<String, String> filterConfig) throws ServletException {
        super.init(appContext);
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
    public boolean service(final ServletRequest request, final ServletResponse response) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        //
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        boolean serve = this.matchesUri(path);
        // 
        if (serve) {
            //
            this.updateRR(httpRequest, httpResponse);
            //
            this.doService(request, response);
        }
        return serve;
    }
    /**/
    private void doService(final ServletRequest servletRequest, final ServletResponse servletResponse) throws ServletException, IOException {
        HttpServletRequest request = new HttpServletRequestWrapper((HttpServletRequest) servletRequest) {
            private String  path;
            private boolean pathComputed     = false;
            //must use a boolean on the memo field, because null is a legal value (TODO no, it's not)
            private boolean pathInfoComputed = false;
            private String  pathInfo;
            @Override
            public String getPathInfo() {
                if (!this.isPathInfoComputed()) {
                    int servletPathLength = this.getServletPath().length();
                    this.pathInfo = this.getRequestURI().substring(this.getContextPath().length()).replaceAll("[/]{2,}", "/");
                    this.pathInfo = this.pathInfo.length() > servletPathLength ? this.pathInfo.substring(servletPathLength) : null;
                    // Corner case: when servlet path and request path match exactly (without trailing '/'),
                    // then pathinfo is null
                    if ("".equals(this.pathInfo) && servletPathLength != 0) {
                        this.pathInfo = null;
                    }
                    this.pathInfoComputed = true;
                }
                return this.pathInfo;
            }
            // NOTE(dhanji): These two are a bit of a hack to help ensure that request dipatcher-sent
            // requests don't use the same path info that was memoized for the original request.
            private boolean isPathInfoComputed() {
                return this.pathInfoComputed && !(null != servletRequest.getAttribute(ManagedServletPipeline.REQUEST_DISPATCHER_REQUEST));
            }
            private boolean isPathComputed() {
                return this.pathComputed && !(null != servletRequest.getAttribute(ManagedServletPipeline.REQUEST_DISPATCHER_REQUEST));
            }
            @Override
            public String getServletPath() {
                return this.computePath();
            }
            @Override
            public String getPathTranslated() {
                final String info = this.getPathInfo();
                return null == info ? null : this.getRealPath(info);
            }
            // Memoizer pattern.
            private String computePath() {
                if (!this.isPathComputed()) {
                    String servletPath = super.getServletPath();
                    this.path = ServletDefinition.this.patternMatcher.extractPath(servletPath);
                    this.pathComputed = true;
                    if (null == this.path) {
                        this.path = servletPath;
                    }
                }
                return this.path;
            }
        };
        //
        HttpServlet servlet = this.getTarget();
        if (servlet == null) {
            return;
        }
        //
        servlet.service(request, servletResponse);
    }
    /**/
    public void destroy() {
        if (this.servletInstance == null) {
            return;
        }
        this.servletInstance.destroy();
    }
}