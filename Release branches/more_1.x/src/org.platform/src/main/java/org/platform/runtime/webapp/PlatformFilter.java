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
package org.platform.runtime.webapp;
import java.io.IOException;
import java.util.Enumeration;
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
import org.platform.PlatformFactory;
import org.platform.api.context.AppContext;
import org.platform.api.context.AppContextFactory;
import org.platform.api.context.ContextConfig;
/**
 * 
 * @version : 2013-3-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class PlatformFilter implements Filter {
    private ServletContext    servletContext    = null;
    private AppContextFactory appContextFactory = null;
    //
    public void init(FilterConfig filterConfig) throws ServletException {
        this.servletContext = filterConfig.getServletContext();
        /*1.获取可能存在的AppContextFactory实例。*/
        AppContextFactory contextFactory = PlatformFactory.getContextFactory(this.servletContext);
        /*2.初始化一个AppContextFactory实例。*/
        if (contextFactory == null) {
            contextFactory = PlatformFactory.buildContextFactory(this.servletContext);
            ConfigBridge config = new ConfigBridge(this.servletContext, filterConfig);
            contextFactory.initFactory(config);//初始化AppContext工厂
            this.appContextFactory = contextFactory;
        }
        /*3.*/
    }
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpReq = (HttpServletRequest) request;
        final HttpServletResponse httpRes = (HttpServletResponse) response;
        AppContext appContext = this.appContextFactory.getAppContext();
        // 
        //HttpServletRequest httpReq, HttpServletRequest httpRes, FilterChain chain
        //        RequestEvent requestEvent = new RequestEvent(appContext, httpReq, httpRes, chain) {
        //            public HttpServletRequest getHttpRequest() {
        //                return httpReq;
        //            }
        //            public HttpServletResponse getHttpResponse() {
        //                return httpRes;
        //            }
        //        };
        //        PlatformEventManager.throwEvent(appContext, requestEvent);
        //
    }
    public void destroy() {
        // TODO Auto-generated method stub
    }
}
/**负责桥接FilterConfig和ContextConfig*/
class ConfigBridge implements ContextConfig {
    private ServletContext servletContext = null;
    private FilterConfig   filterConfig   = null;
    //
    public ConfigBridge(ServletContext servletContext, FilterConfig filterConfig) {
        Assert.isNotNull(servletContext, "servletContext is null.");
        this.servletContext = servletContext;
        this.filterConfig = filterConfig;
    }
    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }
    @Override
    public String getInitParameter(String name) {
        if (this.filterConfig == null)
            return null;
        return this.filterConfig.getInitParameter(name);
    }
    @Override
    public Enumeration<String> getInitParameterNames() {
        if (this.filterConfig == null)
            return null;
        return this.filterConfig.getInitParameterNames();
    }
}