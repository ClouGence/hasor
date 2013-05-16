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
package org.platform.context.startup;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.platform.Assert;
import org.platform.Platform;
import org.platform.binder.FilterPipeline;
import org.platform.context.AppContext;
import com.google.inject.Inject;
import com.google.inject.Singleton;
/**
 * 入口Filter
 * @version : 2013-3-25
 * @author 赵永春 (zyc@byshell.org)
 */
@Singleton
public class RuntimeFilter implements Filter {
    @Inject
    private AppContext     appContext     = null;
    @Inject
    private FilterPipeline filterPipeline = null;
    //
    /**初始化过滤器，初始化会同时初始化FilterPipeline*/
    public void init(FilterConfig filterConfig) throws ServletException {
        if (appContext == null) {
            ServletContext servletContext = filterConfig.getServletContext();
            this.appContext = (AppContext) servletContext.getAttribute(RuntimeListener.AppContextName);
            Assert.isNotNull(this.appContext, "AppContext is null.");
            this.filterPipeline = this.appContext.getInstance(FilterPipeline.class);
        }
        /*1.初始化执行周期管理器。*/
        this.filterPipeline.initPipeline(this.appContext);
        Platform.info("PlatformFilter started.");
    }
    //
    /** 销毁 */
    public void destroy() {
        Platform.info("executeCycle destroyCycle.");
        if (this.filterPipeline != null)
            this.filterPipeline.destroyPipeline(this.appContext);
    }
    //
    /** 处理request，响应response */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpReq = (HttpServletRequest) request;
        final HttpServletResponse httpRes = (HttpServletResponse) response;
        Platform.debug("at http request : %s", httpReq.getRequestURI());
        try {
            //执行.
            this.beforeRequest(appContext, httpReq, httpRes);
            this.processFilterPipeline(httpReq, httpRes, chain);
        } catch (IOException e) {
            Platform.debug("execFilterPipeline IOException :" + Platform.logString(e));
            throw e;
        } catch (ServletException e) {
            Platform.debug("execFilterPipeline ServletException :" + Platform.logString(e));
            throw e;
        } finally {
            this.afterResponse(appContext, httpReq, httpRes);
        }
    }
    //
    /**执行FilterPipeline*/
    private void processFilterPipeline(HttpServletRequest httpReq, HttpServletResponse httpRes, FilterChain chain) throws IOException, ServletException {
        this.filterPipeline.dispatch(httpReq, httpRes, chain);
    }
    //
    /**获取{@link AppContext}接口。*/
    protected final AppContext getAppContext() {
        return appContext;
    }
    //
    /**在filter请求处理之前。*/
    protected void beforeRequest(AppContext appContext, HttpServletRequest httpReq, HttpServletResponse httpRes) {}
    //
    /**在filter请求处理之后。*/
    protected void afterResponse(AppContext appContext, HttpServletRequest httpReq, HttpServletResponse httpRes) {}
}