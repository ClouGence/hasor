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
package net.hasor.web.startup;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.web.AsyncSupported;
import net.hasor.web.ServletVersion;
import net.hasor.web.WebAppContext;
import net.hasor.web.binder.FilterPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
/**
 * 入口Filter，同一个应用程序只能实例化一个 RuntimeFilter 对象。
 * @version : 2013-3-25
 * @author 赵永春 (zyc@hasor.net)
 */
public class RuntimeFilter implements Filter {
    protected Logger         logger         = LoggerFactory.getLogger(getClass());
    private   WebAppContext  appContext     = null;
    private   FilterPipeline filterPipeline = null;
    private   AsyncSupported asyncSupported = AsyncSupported.yes;
    //
    /**初始化过滤器，初始化会同时初始化FilterPipeline*/
    @Override
    public synchronized void init(final FilterConfig filterConfig) throws ServletException {
        if (this.appContext == null) {
            ServletContext servletContext = filterConfig.getServletContext();
            this.appContext = (WebAppContext) servletContext.getAttribute(RuntimeListener.AppContextName);
            Hasor.assertIsNotNull(this.appContext, "AppContext is null.");
            this.filterPipeline = this.appContext.getInstance(FilterPipeline.class);
        }
        /*1.初始化执行周期管理器。*/
        Map<String, String> filterConfigMap = new HashMap<String, String>();
        Enumeration<?> names = filterConfig.getInitParameterNames();
        if (names != null) {
            while (names.hasMoreElements()) {
                String name = names.nextElement().toString();
                filterConfigMap.put(name, filterConfig.getInitParameter(name));
            }
        }
        this.filterPipeline.initPipeline(this.appContext, filterConfigMap);
        //
        if (ServletVersion.V2_5.le(this.appContext.getServletVersion())) {
            logger.info("RuntimeFilter started, at {}", filterConfig.getServletContext().getServerInfo());
        } else {
            logger.info("RuntimeFilter started, context at {}", filterConfig.getServletContext().getContextPath());
        }
    }
    //
    /** 销毁 */
    @Override
    public void destroy() {
        logger.info("executeCycle destroyCycle.");
        if (this.filterPipeline != null) {
            this.filterPipeline.destroyPipeline(this.appContext);
        }
    }
    //
    /** 处理request，响应response */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpReq = (HttpServletRequest) request;
        final HttpServletResponse httpRes = (HttpServletResponse) response;
        //
        if (appContext.getServletVersion().ge(ServletVersion.V3_0) && this.asyncSupported == AsyncSupported.yes) {
            try {
                AsyncContext asyncContext = request.startAsync();
                asyncContext.start(new AsyncInvocationWorker(asyncContext, httpReq, httpRes) {
                    public void doWork(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                        doHttpWork(chain, httpReq, httpRes);
                    }
                });
                return;
            } catch (Throwable e) {
                this.asyncSupported = AsyncSupported.no;
            }
        }
        //
        doHttpWork(chain, httpReq, httpRes);
    }
    protected void doHttpWork(FilterChain chain, HttpServletRequest httpReq, HttpServletResponse httpRes) throws IOException, ServletException {
        try {
            this.beforeRequest(this.appContext, httpReq, httpRes);
            this.processFilterPipeline(httpReq, httpRes, chain);
        } finally {
            this.afterResponse(this.appContext, httpReq, httpRes);
        }
    }
    //
    /**执行FilterPipeline*/
    private void processFilterPipeline(final HttpServletRequest httpReq, final HttpServletResponse httpRes, final FilterChain chain) throws IOException, ServletException {
        this.filterPipeline.dispatch(httpReq, httpRes, chain);
    }
    //
    /**在filter请求处理之前，该方法负责通知HttpRequestProvider、HttpResponseProvider、HttpSessionProvider更新对象。*/
    protected void beforeRequest(final AppContext appContext, final HttpServletRequest httpReq, final HttpServletResponse httpRes) {
        //
    }
    //
    /**在filter请求处理之后，该方法负责通知HttpRequestProvider、HttpResponseProvider、HttpSessionProvider重置对象。*/
    protected void afterResponse(final AppContext appContext, final HttpServletRequest httpReq, final HttpServletResponse httpRes) {
        //
    }
}