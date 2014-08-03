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
package net.hasor.web.startup;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.web.WebAppContext;
import net.hasor.web.binder.FilterPipeline;
import net.hasor.web.binder.reqres.RRUpdate;
/**
 * 入口Filter，同一个应用程序只能实例化一个 RuntimeFilter 对象。
 * @version : 2013-3-25
 * @author 赵永春 (zyc@hasor.net)
 */
public class RuntimeFilter implements Filter {
    private RRUpdate       rrRpdate       = null;
    private WebAppContext  appContext     = null;
    private FilterPipeline filterPipeline = null;
    //
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
        Enumeration<String> names = filterConfig.getInitParameterNames();
        if (names != null) {
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                filterConfigMap.put(name, filterConfig.getInitParameter(name));
            }
        }
        this.filterPipeline.initPipeline(this.appContext, filterConfigMap);
        //2.init RR
        this.rrRpdate = this.appContext.getInstance(RRUpdate.class);
        //
        Hasor.logInfo("PlatformFilter started.");
    }
    //
    /** 销毁 */
    @Override
    public void destroy() {
        Hasor.logInfo("executeCycle destroyCycle.");
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
        try {
            //执行.
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
    /**获取{@link AppContext}接口。*/
    protected final AppContext getAppContext() {
        return RuntimeListener.getLocalAppContext();
    }
    //
    /**在filter请求处理之前，该方法负责通知HttpRequestProvider、HttpResponseProvider、HttpSessionProvider更新对象。*/
    protected void beforeRequest(final AppContext appContext, final HttpServletRequest httpReq, final HttpServletResponse httpRes) {
        this.rrRpdate.update(httpReq, httpRes);
    }
    //
    /**在filter请求处理之后，该方法负责通知HttpRequestProvider、HttpResponseProvider、HttpSessionProvider重置对象。*/
    protected void afterResponse(final AppContext appContext, final HttpServletRequest httpReq, final HttpServletResponse httpRes) {
        this.rrRpdate.release();
    }
    /**获取{@link HttpServletRequest}*/
    public static HttpServletRequest getLocalRequest() {
        return RRUpdate.getLocalRequest();
    }
    //
    /**获取{@link HttpServletResponse}*/
    public static HttpServletResponse getLocalResponse() {
        return RRUpdate.getLocalResponse();
    }
    //
    /**获取{@link ServletContext}*/
    public static ServletContext getLocalServletContext() {
        return RuntimeListener.getLocalServletContext();
    }
    //
    /**获取{@link AppContext}*/
    public static AppContext getLocalAppContext() {
        return RuntimeListener.getLocalAppContext();
    }
}