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
package org.hasor.web.controller.support;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hasor.context.AppContext;
import org.hasor.web.controller.support.ActionSettings.ActionWorkMode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
/**
 * action功能的入口。
 * @version : 2013-5-11
 * @author 赵永春 (zyc@byshell.org)
 */
@Singleton
public class MergedController implements Filter {
    @Inject
    private AppContext        appContext        = null;
    private ActionSettings    actionSettings    = null;
    private RestfulController restfulController = null;
    private ActionController  actionController  = null;
    //
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.actionSettings = this.appContext.getInstance(ActionSettings.class);
        this.restfulController = this.appContext.getInstance(RestfulController.class);
        this.actionController = this.appContext.getInstance(ActionController.class);
        this.restfulController.init(filterConfig);
        this.actionController.init(new ServletConfigBridge(filterConfig));
    }
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, final FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        if (this.actionSettings.getMode() == ActionWorkMode.ServletOnly) {
            this.actionController.service(request, response);
            return;
        }
        if (this.actionSettings.getMode() == ActionWorkMode.RestOnly) {
            this.restfulController.doFilter(request, response, chain);
            return;
        }
        if (this.actionSettings.getMode() == ActionWorkMode.Both) {
            this.restfulController.doFilter(request, response, new FilterChain() {
                @Override
                public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                    HttpServletRequest req = (HttpServletRequest) request;
                    if (MergedController.this.actionController.testURL(req) == true)
                        MergedController.this.actionController.service(request, response);
                    else
                        chain.doFilter(request, response);
                }
            });
            return;
        }
    }
    @Override
    public void destroy() {
        this.restfulController.destroy();
        this.actionController.destroy();
    }
    //
    private static class ServletConfigBridge implements ServletConfig {
        private FilterConfig filterConfig = null;
        public ServletConfigBridge(FilterConfig filterConfig) {
            this.filterConfig = filterConfig;
        }
        @Override
        public String getInitParameter(String name) {
            return this.filterConfig.getInitParameter(name);
        }
        @Override
        public Enumeration<String> getInitParameterNames() {
            return this.filterConfig.getInitParameterNames();
        }
        @Override
        public ServletContext getServletContext() {
            return this.filterConfig.getServletContext();
        }
        @Override
        public String getServletName() {
            return this.filterConfig.getFilterName();
        }
    }
}