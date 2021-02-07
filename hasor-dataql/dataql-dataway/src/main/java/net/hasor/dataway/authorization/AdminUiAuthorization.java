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
package net.hasor.dataway.authorization;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
import net.hasor.core.spi.AppContextAware;
import net.hasor.core.spi.SpiTrigger;
import net.hasor.dataway.config.DatawayUtils;
import net.hasor.dataway.service.InterfaceUiFilter;
import net.hasor.dataway.spi.LoginPerformChainSpi;
import net.hasor.dataway.spi.LoginTokenChainSpi;
import net.hasor.utils.CommonCodeUtils;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.io.IOUtils;
import net.hasor.utils.io.output.ByteArrayOutputStream;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

/**
 * 基础登陆验证。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-01-19
 */
public class AdminUiAuthorization implements InvokerFilter, AppContextAware {
    protected static Logger     logger               = LoggerFactory.getLogger(AdminUiAuthorization.class);
    private final    String     adminBaseUri;
    private final    String     loginPageUri;
    private final    String     loginActionUri;
    private final    boolean    enableAdminAuthorization;
    private          SpiTrigger spiTrigger;
    //
    private final    String     dwAuthDataCookieName = "DW_AUTH_DATA";
    private final    String     dwAuthData;
    private          byte[]     loginPageData;

    public AdminUiAuthorization(String adminBaseUri, Environment environment) throws NoSuchAlgorithmException {
        this.adminBaseUri = adminBaseUri;
        this.loginActionUri = fixUrl(adminBaseUri + "/login");
        //
        Settings settings = environment.getSettings();
        this.enableAdminAuthorization = settings.getBoolean("hasor.dataway.authorization.enable", false);
        String adminUser = settings.getString("hasor.dataway.authorization.username", "admin");
        String adminPassword = settings.getString("hasor.dataway.authorization.password", "admin");
        this.dwAuthData = CommonCodeUtils.MD5.getMD5(adminUser + ":" + adminPassword);
        this.loginPageUri = settings.getString("hasor.dataway.authorization.loginPageUri", fixUrl(InterfaceUiFilter.resourceBaseUri + "/login.html"));
    }

    private static String fixUrl(String url) {
        return url.replaceAll("/+", "/");
    }

    @Override
    public void setAppContext(AppContext appContext) {
        this.spiTrigger = appContext.getInstance(SpiTrigger.class);
    }

    @Override
    public Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        if (!invoker.getRequestPath().startsWith(this.adminBaseUri) || !this.enableAdminAuthorization) {
            return chain.doNext(invoker);
        }
        // check ok.
        if (checkDwAuthData(invoker)) {
            return chain.doNext(invoker);
        }
        // process login action
        if (invoker.getRequestPath().equalsIgnoreCase(this.loginActionUri)) {
            if (doLogin(invoker)) {
                String contextPath = DatawayUtils.getDwContextPath(invoker, null);
                String redirect = fixUrl(contextPath + "/" + this.adminBaseUri);
                invoker.getHttpResponse().sendRedirect(redirect);
                return null;
            }
        }
        // response login page.
        return responseLoginPage(invoker);
    }

    protected boolean checkDwAuthData(final Invoker invoker) throws NoSuchAlgorithmException {
        if (this.spiTrigger.hasSpi(LoginTokenChainSpi.class)) {
            // use spi trigger to check login
            Boolean result = this.spiTrigger.chainSpi(LoginTokenChainSpi.class, (listener, lastResult) -> {
                return lastResult != null ? lastResult : listener.doCheckToken(invoker);
            }, null);
            return result != null && result;
        } else {
            // use default
            String cookieDwAuthData = cookieValue(invoker.getHttpRequest(), this.dwAuthDataCookieName);
            return this.dwAuthData.equalsIgnoreCase(cookieDwAuthData);
        }
    }

    protected Object responseLoginPage(Invoker invoker) throws IOException, ServletException {
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        HttpServletResponse httpResponse = invoker.getHttpResponse();
        String loginPageUriLowerCase = this.loginPageUri.toLowerCase();
        if (loginPageUriLowerCase.startsWith("forward:")) {
            httpRequest.getRequestDispatcher(this.loginPageUri.substring("forward:".length())).forward(httpRequest, httpResponse);
            return null;
        } else if (loginPageUriLowerCase.startsWith("redirect:")) {
            httpResponse.sendRedirect(this.loginPageUri.substring("redirect:".length()));
            return null;
        }
        //
        if (this.loginPageData == null) {
            synchronized (this) {
                if (this.loginPageData == null) {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    try (InputStream inputStream = ResourcesUtils.getResourceAsStream(this.loginPageUri)) {
                        if (inputStream == null) {
                            httpResponse.sendError(404, "not found " + this.loginPageUri);
                            return null;
                        }
                        IOUtils.copy(inputStream, output);
                        this.loginPageData = output.toByteArray();
                    } catch (Exception e) {
                        logger.error("load " + this.loginPageUri + " failed -> " + e.getMessage(), e);
                    }
                }
            }
        }
        //
        httpResponse.setContentType(invoker.getMimeType("html"));
        httpResponse.setContentLength(this.loginPageData.length);
        try (OutputStream outputStream = httpResponse.getOutputStream()) {
            outputStream.write(this.loginPageData);
            outputStream.flush();
        }
        return null;
    }

    protected String cookieValue(HttpServletRequest httpRequest, String cookieName) {
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    protected boolean doLogin(Invoker invoker) throws NoSuchAlgorithmException {
        String username = invoker.getHttpRequest().getParameter("userName");
        String password = invoker.getHttpRequest().getParameter("userPassword");
        //
        if (this.spiTrigger.hasSpi(LoginPerformChainSpi.class)) {
            // use spi trigger to login
            Boolean result = this.spiTrigger.chainSpi(LoginPerformChainSpi.class, (listener, lastResult) -> {
                return lastResult != null ? lastResult : listener.doLogin(invoker);
            }, null);
            return result != null && result;
        } else {
            // use default
            String loginDwAuthData = CommonCodeUtils.MD5.getMD5(username + ":" + password);
            if (this.dwAuthData.equalsIgnoreCase(loginDwAuthData)) {
                invoker.getHttpResponse().addCookie(new Cookie(this.dwAuthDataCookieName, loginDwAuthData));
                return true;
            }
            return false;
        }
    }
}
