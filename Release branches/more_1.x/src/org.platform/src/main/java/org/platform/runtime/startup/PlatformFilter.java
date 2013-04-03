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
import org.platform.api.context.ContextConfig;
import org.platform.runtime.Platform;
import org.platform.runtime.PlatformFactoryFinder;
import org.platform.runtime.context.AppContextFactory;
import org.platform.runtime.execycle.ExecuteCycle;
import org.platform.runtime.execycle.ExecuteCycleFactory;
import org.platform.runtime.manager.AppManager;
import org.platform.runtime.manager.AppManagerFactory;
/**
 * 
 * @version : 2013-3-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class PlatformFilter implements Filter {
    private ExecuteCycle      executeCycle   = null;
    private AppContextFactory contextFactory = null;
    //
    public void init(FilterConfig filterConfig) throws ServletException {
        Platform.info("init PlatformFilter...");
        ServletContext servletContext = filterConfig.getServletContext();
        ContextConfig config = new PlatformContextConfig(servletContext);
        //
        /*1.获取可能存在的AppContextFactory实例。*/
        this.contextFactory = (AppContextFactory) servletContext.getAttribute(PlatformListener.ContextFactoryName);
        Assert.isNotNull(this.contextFactory, "AppContextFactory is null.");
        //
        /*2.创建ExecuteCycle接口的工厂方法。*/
        ExecuteCycleFactory cycleFactory = PlatformFactoryFinder.getExecuteCycleFactory(config);
        Assert.isNotNull(cycleFactory, "ExecuteCycleFactory is null.");
        Platform.info("ExecuteCycleFactory buildCycle...");
        this.executeCycle = cycleFactory.buildCycle(config);
        Assert.isNotNull(this.executeCycle, "buildCycle return null.");
        //
        /*3.设置获取到的配置管理程序。*/
        AppManagerFactory configManagerFactory = PlatformFactoryFinder.getConfigManagerFactory(config);
        if (configManagerFactory != null) {
            Platform.info("AppManagerFactory buildManager...");
            AppManager manager = configManagerFactory.buildManager(config);
            Platform.info("executeCycle setManager : " + Platform.logString(manager));
            this.executeCycle.setManager(manager);
        } else
            Platform.warning("executeCycle setManager is null.");
        //
        /*4.初始化执行周期管理器。*/
        Platform.info("executeCycle initCycle... ");
        this.executeCycle.initCycle(new PlatformContextConfig(servletContext));
        //
        Platform.info("Platform started.");
    }
    public void destroy() {
        Platform.info("executeCycle destroyCycle.");
        if (this.executeCycle != null)
            this.executeCycle.destroyCycle();
    }
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpReq = (HttpServletRequest) request;
        final HttpServletResponse httpRes = (HttpServletResponse) response;
        Platform.debug("at http request :" + httpReq.getRequestURI());
        //
        try {
            AppContext appContext = this.contextFactory.getAppContext(httpReq.getServletContext());
            this.executeCycle.setCurrentAppContext(appContext);
            this.executeCycle.execCycle(httpReq, httpRes, chain);
        } catch (IOException e) {
            Platform.debug("execCycle IOException :" + Platform.logString(e));
            throw e;
        } catch (ServletException e) {
            Platform.debug("execCycle ServletException :" + Platform.logString(e));
            throw e;
        } finally {
            this.executeCycle.setCurrentAppContext(null);
        }
    }
}