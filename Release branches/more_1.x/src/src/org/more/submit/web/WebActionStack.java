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
package org.more.submit.web;
import java.io.IOException;
import java.net.URI;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
import org.more.submit.ActionStack;
import org.more.submit.impl.DefaultActionStack;
import org.more.submit.web.scope.CookieScope;
/**
 * 提供了Web特性的ActionStack对象，该类还提供了五个属性作用域的支持。这五个作用域详见WebScopeEnum接口。
 * @version : 2010-7-27
 * @author 赵永春(zyc@byshell.org)
 */
public class WebActionStack extends DefaultActionStack {
    private static final long serialVersionUID = 5001483997344333143L;
    private WebSubmitService  submitContext    = null;
    //==================================================================================Constructor
    public WebActionStack(URI uri, ActionStack parent, WebSubmitService submitContext) {
        super(uri, parent, submitContext);
        this.submitContext = submitContext;
    };
    //==========================================================================================针对属性的get/set方法
    /**获取PageContext对象。*/
    public PageContext getPageContext() {
        return WebHelper.getPageContext();
    };
    /**获取HttpServletRequest对象。*/
    public HttpServletRequest getHttpRequest() {
        return WebHelper.getHttpRequest();
    };
    /**获取HttpServletResponse对象。*/
    public HttpServletResponse getHttpResponse() {
        return WebHelper.getHttpResponse();
    };
    /**获取HttpSession对象。*/
    public HttpSession getHttpSession() {
        return WebHelper.getHttpSession();
    };
    /**获取ServletContext对象。*/
    public ServletContext getServletContext() {
        return WebHelper.getServletContext();
    };
    public WebSubmitService getSubmitService() {
        return this.submitContext;
    };
    //==========================================================================================request查询参数专用方法
    /**获取request请求参数中所有参数名称。*/
    @SuppressWarnings("unchecked")
    public String[] getRequestParamNames() {
        Set<String> keys = this.getHttpRequest().getParameterMap().keySet();
        String[] ns = new String[keys.size()];
        keys.toArray(ns);
        return ns;
    };
    /**仅从request请求参数中查找指定属性。*/
    public String[] getRequestParams(String attName) {
        return this.getHttpRequest().getParameterValues(attName);
    };
    /**仅从request请求参数中查找指定属性。*/
    public String getRequestParam(String attName) {
        return this.getHttpRequest().getParameter(attName);
    };
    /**服务端转发。*/
    public void forward(String url) throws ServletException, IOException {
        HttpServletRequest request = WebHelper.getHttpRequest();
        HttpServletResponse response = WebHelper.getHttpResponse();
        request.getRequestDispatcher(url).forward(request, response);
    };
    /**发送重定向操作。*/
    public void redirect(String url) throws IOException {
        HttpServletResponse response = WebHelper.getHttpResponse();
        response.sendRedirect(url);
    };
    /**发送错误。*/
    public void error(int error, String message) throws IOException {
        HttpServletResponse response = WebHelper.getHttpResponse();
        response.sendError(error, message);
    };
    /**发送错误。*/
    public void error(int error) throws IOException {
        HttpServletResponse response = WebHelper.getHttpResponse();
        response.sendError(error);
    };
    //==========================================================================================Cookie作用域下的快速操作。
    /**向cookie作用域输出一个cookie对象。*/
    public void setCookieAttribute(String name, String value, int age) {
        CookieScope cs = (CookieScope) this.getSubmitService().getScope(CookieScope.Name);
        cs.setCookieAttribute(name, value, age);
    };
    /**向cookie作用域输出一个cookie对象。*/
    public void setCookieAttribute(Cookie cookie) {
        CookieScope cs = (CookieScope) this.getSubmitService().getScope(CookieScope.Name);
        cs.setCookieAttribute(cookie);
    };
    /**获取一个cookie对象。*/
    public Cookie getCookieAttribute(String name) {
        CookieScope cs = (CookieScope) this.getSubmitService().getScope(CookieScope.Name);
        return cs.getCookieAttribute(name);
    };
};