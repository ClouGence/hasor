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
package org.platform.security;
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
import org.platform.context.AppContext;
import org.platform.context.setting.Config;
import org.platform.startup.RuntimeListener;
/**
 * 权限系统URL请求处理支持。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@byshell.org)
 */
class SecurityFilter implements Filter {
    private SecuritySettings settings   = new SecuritySettings();
    private AppContext      appContext = null;
    private SecurityContext secService = null;
    //
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Platform.info("init SecurityFilter...");
        ServletContext servletContext = filterConfig.getServletContext();
        this.appContext = (AppContext) servletContext.getAttribute(RuntimeListener.AppContextName);
        Config systemConfig = this.appContext.getInitContext().getConfig();
        Assert.isNotNull(this.appContext, "AppContext is null.");
        //
        this.secService = this.appContext.getBean(SecurityContext.class);
        //
        /*配置文件读取*/
        this.settings.loadConfig(systemConfig.getSettings());
        /*加入，监听配置文件改动*/
        systemConfig.addSettingsListener(this.settings);
    }
    @Override
    public void destroy() {
        Config systemConfig = this.appContext.getInitContext().getConfig();
        /*撤销，监听配置文件改动*/
        systemConfig.removeSettingsListener(this.settings);
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        //1.处理禁用状态
        if (this.settings.isEnableURL() == false) {
            chain.doFilter(httpRequest, httpResponse);
            return;
        }
        //2.处理权限
        String reqPath = httpRequest.getRequestURI();
        reqPath = reqPath.substring(httpRequest.getContextPath().length());
        AuthSession authSession = this.secService.getAuthSession(httpRequest, httpResponse, true);//必然创建authSession
        //
        if (reqPath.endsWith(this.settings.getLoginURL()) == true) {
            //1.登入匹配
            String account = httpRequest.getParameter(this.settings.getAccountField());
            String password = httpRequest.getParameter(this.settings.getPasswordField());
            Platform.info("Security -> doLogin acc=" + account + " , pwd=" + password);
            authSession.doLogin(account, password);/*登入会话*/
            //
            SecurityDispatcher dispatcher = this.secService.getDispatcher(reqPath);/*获取到跳转对象*/
            dispatcher.forwardIndex(httpRequest, httpResponse);//跳转到登入成功之后的地址
            return;
        } else if (reqPath.endsWith(this.settings.getLogoutURL()) == true) {
            //2.登出匹配
            Platform.info("Security -> doLogout. user=" + authSession.getUserObject());
            authSession.doLogout();/*退出会话*/
            SecurityDispatcher dispatcher = this.secService.getDispatcher(reqPath);/*获取到跳转对象*/
            dispatcher.forwardLogout(httpRequest, httpResponse);//跳转到退出之后的地址
            return;
        }
        //3.访问权限判断
        UriPatternMatcher uriMatcher = this.secService.getUriMatcher(reqPath);
        if (uriMatcher.testPermission(authSession) == false) {
            Platform.info("Security -> authSession= ‘" + authSession.getSessionID() + "’  testPermission failure! uri= " + reqPath);
            /*没有权限，执行跳转*/
            SecurityDispatcher dispatcher = this.secService.getDispatcher(reqPath);/*获取到跳转对象*/
            dispatcher.forwardError(request, response);//跳转到出现异常的地址
            return;
        }
        //4.具备权限继续访问
        chain.doFilter(httpRequest, httpResponse);
    }
}