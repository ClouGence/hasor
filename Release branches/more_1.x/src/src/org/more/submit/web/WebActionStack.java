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
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
import org.more.submit.ActionStack;
import org.more.submit.Session;
import org.more.submit.SubmitContext;
/**
 * 提供了Web特性的ActionStack对象，该类还提供了五个属性作用域的支持。这五个作用域详见WebScopeEnum接口。
 * @version : 2010-7-27
 * @author 赵永春(zyc@byshell.org)
 */
public class WebActionStack extends ActionStack implements WebScopeEnum {
    private static final long   serialVersionUID = 5001483997344333143L;
    //========================================================================================Field
    private PageContext         httpPageContext  = null;
    private HttpServletRequest  httpRequest      = null;
    private HttpServletResponse httpResponse     = null;
    private HttpSession         httpSession      = null;
    private ServletContext      servletContext   = null;
    //==================================================================================Constructor
    public WebActionStack(String actionName, String actionMethod, ActionStack parent, Session session, SubmitContext context, HttpServletRequest httpRequest, HttpServletResponse httpResponse, PageContext httpPageContext) {
        super(actionName, actionMethod, parent, session, context);
        this.httpPageContext = httpPageContext;
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        this.httpSession = httpRequest.getSession(true);
        this.servletContext = httpSession.getServletContext();
        //注册新的作用域。
        this.putScope(Scope_Cookie, new CookieScope(this.httpRequest, this.httpResponse));
        this.putScope(Scope_HttpSession, new HttpSessionScope(this.httpSession));
        if (httpPageContext != null)
            this.putScope(Scope_JspPage, new JspPageScope(this.httpPageContext));
        this.putScope(Scope_HttpRequest, new RequestScope(this.httpRequest));
        this.putScope(Scope_ServletContext, new ServletContextScope(this.servletContext));
    };
    //==========================================================================================针对属性的get/set方法
    /**获取PageContext对象。*/
    public PageContext getPageContext() {
        return this.httpPageContext;
    };
    /**获取HttpServletRequest对象。*/
    public HttpServletRequest getHttpRequest() {
        return this.httpRequest;
    };
    /**获取HttpServletResponse对象。*/
    public HttpServletResponse getHttpResponse() {
        return this.httpResponse;
    };
    /**获取HttpSession对象。*/
    public HttpSession getHttpSession() {
        return this.httpSession;
    };
    /**获取ServletContext对象。*/
    public ServletContext getServletContext() {
        return this.servletContext;
    };
    //==========================================================================================request查询参数专用方法
    /** 根据requestParam->stack->parent->jspPage->request->session->httpSession->context->servletContext->cookie这个顺序依次查找属性，在stack中查找时是在整个stack树中查找。*/
    public Object getParam(String key) {
        //requestParam
        Object obj = (String[]) this.getRequestParams(key);
        if (obj != null) {
            String[] paramMap = (String[]) obj;
            if (paramMap.length == 0)
                obj = null;
            else if (paramMap.length == 1)
                obj = paramMap[0];
            else
                obj = paramMap;
        }
        //stack->parent
        if (obj == null)
            obj = this.getByStackTree(key);
        //jspPage
        if (obj == null && this.containsScopeKEY(Scope_JspPage) == true)
            obj = this.getPageContext().getAttribute(key);
        //request
        if (obj == null && this.containsScopeKEY(Scope_HttpRequest) == true)
            obj = this.getHttpRequest().getAttribute(key);
        //session
        if (obj == null && this.containsScopeKEY(Scope_Session) == true)
            obj = this.getSession().getAttribute(key);
        //httpSession
        if (obj == null && this.containsScopeKEY(Scope_HttpSession) == true)
            obj = this.getHttpSession().getAttribute(key);
        //context
        if (obj == null && this.containsScopeKEY(Scope_Context) == true)
            obj = this.getContext().getAttribute(key);
        //servletContext
        if (obj == null && this.containsScopeKEY(Scope_ServletContext) == true)
            obj = this.getServletContext().getAttribute(key);
        //cookie
        if (obj == null && this.containsScopeKEY(Scope_Cookie) == true)
            obj = this.getScopeAttribute(Scope_Cookie).getAttribute(key);
        return obj;
    };
    //==========================================================================================request查询参数专用方法
    /**获取request请求参数中所有参数名称。*/
    @SuppressWarnings("unchecked")
    public String[] getRequestParamNames() {
        Set<String> keys = this.httpRequest.getParameterMap().keySet();
        String[] ns = new String[keys.size()];
        keys.toArray(ns);
        return ns;
    };
    /**仅从request请求参数中查找指定属性。*/
    public String[] getRequestParams(String attName) {
        return this.httpRequest.getParameterValues(attName);
    };
    /**仅从request请求参数中查找指定属性。*/
    public String getRequestParam(String attName) {
        return this.httpRequest.getParameter(attName);
    };
    //==========================================================================================Cookie作用域下的快速操作。
    /**向cookie作用域输出一个cookie对象。*/
    public void setCookieAttribute(String name, String value, int age) {
        CookieScope cs = (CookieScope) this.getScopeAttribute(Scope_Cookie);
        cs.setCookieAttribute(name, value, age);
    };
    /**向cookie作用域输出一个cookie对象。*/
    public void setCookieAttribute(Cookie cookie) {
        CookieScope cs = (CookieScope) this.getScopeAttribute(Scope_Cookie);
        cs.setCookieAttribute(cookie);
    };
    /**获取一个cookie对象。*/
    public Cookie getCookieAttribute(String name) {
        CookieScope cs = (CookieScope) this.getScopeAttribute(Scope_Cookie);
        return cs.getCookieAttribute(name);
    };
};