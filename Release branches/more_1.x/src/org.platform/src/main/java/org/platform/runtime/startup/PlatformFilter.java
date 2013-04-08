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
package org.platform.runtime.startup;
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
import org.platform.api.context.AppContext;
import org.platform.runtime.Platform;
import org.platform.runtime.context.AbstractAppContext;
import org.platform.runtime.context.AppContextFactory;
import org.platform.runtime.execycle.ExecuteCycle;
import org.platform.runtime.manager.AppManager;
/**
 * 
 * @version : 2013-3-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class PlatformFilter implements Filter {
    private AppContextFactory contextFactory = null;
    private ExecuteCycle      executeCycle   = null;
    private AppManager        appManager     = null;
    //
    //
    /**first visit it or start Filter*/
    public void init(FilterConfig filterConfig) throws ServletException {
        Platform.info("init PlatformFilter...");
        ServletContext servletContext = filterConfig.getServletContext();
        PlatformBuild platformBuild = (PlatformBuild) servletContext.getAttribute(PlatformListener.PlatformBuild);
        Assert.isNotNull(platformBuild, "PlatformBuild is null.");
        //
        /*1.从platformBuild中获取运行必须的对象。*/
        this.contextFactory = platformBuild.getAppContextFactory();
        this.executeCycle = platformBuild.getExecuteCycle();
        this.appManager = platformBuild.buildManager();//可以返回空值。
        //
        Assert.isNotNull(this.contextFactory, "AppContextFactory is null.");
        Assert.isNotNull(this.executeCycle, "buildCycle return null.");
        //
        /*2.初始化执行周期管理器。*/
        Platform.info("executeCycle initCycle... ");
        this.executeCycle.setManager(this.appManager);
        this.executeCycle.initCycle();
        Platform.info("execute Cycle started.");
    }
    /** destroy */
    public void destroy() {
        Platform.info("executeCycle destroyCycle.");
        if (this.executeCycle != null)
            this.executeCycle.destroyCycle();
    }
    /** process request and anser response */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpReq = (HttpServletRequest) request;
        final HttpServletResponse httpRes = (HttpServletResponse) response;
        Platform.debug("at http request :" + httpReq.getRequestURI());
        AbstractAppContext appContext = this.contextFactory.getAppContext(httpReq.getServletContext());
        try {
            //执行.
            this.beforeRequest(appContext, httpReq, httpRes);
            this.executeCycle.execCycle(appContext, httpReq, httpRes, chain);
        } catch (IOException e) {
            Platform.debug("execCycle IOException :" + Platform.logString(e));
            throw e;
        } catch (ServletException e) {
            Platform.debug("execCycle ServletException :" + Platform.logString(e));
            throw e;
        } finally {
            this.afterResponse(appContext, httpReq, httpRes);
        }
    }
    /**在filter请求处理之前。*/
    protected void beforeRequest(AppContext appContext, HttpServletRequest httpReq, HttpServletResponse httpRes) {}
    /**在filter请求处理之后。*/
    protected void afterResponse(AppContext appContext, HttpServletRequest httpReq, HttpServletResponse httpRes) {}
}