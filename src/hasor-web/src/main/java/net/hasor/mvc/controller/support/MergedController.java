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
package net.hasor.mvc.controller.support;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import net.hasor.context.AppContext;
import net.hasor.mvc.controller.support.ActionSettings.ActionWorkMode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
/**
 * action功能的入口。
 * @version : 2013-5-11
 * @author 赵永春 (zyc@hasor.net)
 */
@Singleton
public class MergedController implements Filter {
    @Inject
    private AppContext        appContext        = null;
    private ActionSettings    actionSettings    = null;
    private RestfulController restfulController = null;
    private ActionController  actionController  = null;
    //
    public void init(FilterConfig filterConfig) throws ServletException {
        this.actionSettings = this.appContext.getInstance(ActionSettings.class);
        this.restfulController = this.appContext.getInstance(RestfulController.class);
        this.actionController = this.appContext.getInstance(ActionController.class);
        this.restfulController.init(filterConfig);
        this.actionController.init(new ServletConfigBridge(filterConfig));
    }
    public void doFilter(ServletRequest req, ServletResponse resp, final FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) withDispatcher(req);
        HttpServletResponse response = (HttpServletResponse) resp;
        /*运行模式：ServletOnly*/
        if (this.actionSettings.getMode() == ActionWorkMode.ServletOnly) {
            this.actionController.service(request, response);
            return;
        }
        /*运行模式：RestOnly*/
        if (this.actionSettings.getMode() == ActionWorkMode.RestOnly) {
            this.restfulController.doFilter(request, response, chain);
            return;
        }
        /*运行模式：Both*/
        if (this.actionSettings.getMode() == ActionWorkMode.Both) {
            this.restfulController.doFilter(request, response, new FilterChain() {
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
    public void destroy() {
        this.restfulController.destroy();
        this.actionController.destroy();
    }
    //
    /**负责从FilterConfig接口转为ServletConfig接口*/
    private static class ServletConfigBridge implements ServletConfig {
        private FilterConfig filterConfig = null;
        public ServletConfigBridge(FilterConfig filterConfig) {
            this.filterConfig = filterConfig;
        }
        public String getInitParameter(String name) {
            return this.filterConfig.getInitParameter(name);
        }
        public Enumeration<String> getInitParameterNames() {
            return this.filterConfig.getInitParameterNames();
        }
        public ServletContext getServletContext() {
            return this.filterConfig.getServletContext();
        }
        public String getServletName() {
            return this.filterConfig.getFilterName();
        }
    };
    //
    /** 使用代理ServletRequest对象来处理位于Action之内的请求转发. */
    private ServletRequest withDispatcher(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        return new HttpServletRequestWrapper(request) {
            public RequestDispatcher getRequestDispatcher(String path) {
                RequestDispatcher dispatcher = null;
                /*运行模式：ServletOnly*/
                if (actionSettings.getMode() == ActionWorkMode.ServletOnly) {
                    dispatcher = actionController.getRequestDispatcher(path, this.getMethod());
                }
                /*运行模式：RestOnly*/
                else if (actionSettings.getMode() == ActionWorkMode.RestOnly) {
                    dispatcher = restfulController.getRequestDispatcher(path, this);
                }
                /*运行模式：Both*/
                else if (actionSettings.getMode() == ActionWorkMode.Both) {
                    dispatcher = restfulController.getRequestDispatcher(path, this);
                    if (dispatcher == null)
                        dispatcher = actionController.getRequestDispatcher(path, this.getMethod());
                }
                return (null != dispatcher) ? dispatcher : super.getRequestDispatcher(path);
            }
        };
    }
}