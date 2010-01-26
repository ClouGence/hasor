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
package org.more.submit.support.web;
import java.util.HashMap;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import org.more.StateException;
import org.more.submit.ActionStack;
import org.more.submit.ImplSubmitContext;
import org.more.submit.Session;
import org.more.submit.support.web.scope.CookieScope;
import org.more.submit.support.web.scope.HttpSessionScope;
import org.more.submit.support.web.scope.JspPageScope;
import org.more.submit.support.web.scope.RequestScope;
import org.more.submit.support.web.scope.ServletContextScope;
import org.more.util.attribute.IAttribute;
/**
 * 提供了Web特性的ActionStack对象，该类还提供了五个属性作用域的支持。
 * @version 2009-12-28
 * @author 赵永春 (zyc@byshell.org)
 */
public class WebActionStack extends ActionStack implements WebScopeEnum {
    private static final long           serialVersionUID = 5001483997344333143L;
    //========================================================================================Field
    private HttpServletRequest          httpRequest;
    private HttpServletResponse         httpResponse;
    private PageContext                 httpPageContext;
    private HashMap<String, IAttribute> attributeForScopeForMap;                //当前属性作用域操作接口。
    //==================================================================================Constructor
    public WebActionStack(ActionStack parent, Session session, ImplSubmitContext context) {
        super(parent, session, context);
    }
    //==========================================================================================注入属性、初始化
    void setRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }
    void setResponse(HttpServletResponse httpResponse) {
        this.httpResponse = httpResponse;
    }
    void setPageContext(PageContext httpPageContext) {
        this.httpPageContext = httpPageContext;
    }
    //==========================================================================================针对属性的get/set方法
    /**获取ServletContext对象。*/
    public ServletContext getServletContext() {
        WebSubmitContext webContext = (WebSubmitContext) this.getContext();
        return webContext.getServletContext();
    }
    /**获取HttpServletRequest对象。*/
    public HttpServletRequest getRequest() {
        return httpRequest;
    }
    /**获取HttpServletResponse对象。*/
    public HttpServletResponse getResponse() {
        return httpResponse;
    }
    /**获取PageContext对象。*/
    public PageContext getPageContext() {
        return httpPageContext;
    }
    @Override
    protected IAttribute getScopeAttribute(String scope) {
        if (this.attributeForScopeForMap == null)
            this.attributeForScopeForMap = new HashMap<String, IAttribute>();
        IAttribute att = this.attributeForScopeForMap.get(scope);
        if (att != null)
            return att;
        if (Scope_JspPage.equals(scope) == true)
            if (this.httpPageContext != null)
                att = new JspPageScope(this.httpPageContext);//页面上下文
            else
                throw new StateException("当前状态不支持JspPage作用域。");
        else if (Scope_HttpRequest.equals(scope) == true)
            att = new RequestScope(this.httpRequest);//Request范围
        else if (Scope_HttpSession.equals(scope) == true)
            att = new HttpSessionScope(this.httpRequest.getSession(true));//HttpSession范围
        else if (Scope_Cookie.equals(scope) == true)
            att = new CookieScope(this.httpRequest, this.httpResponse);//Cookie范围
        else if (Scope_ServletContext.equals(scope) == true)
            att = new ServletContextScope(this.httpRequest.getSession().getServletContext());//ServletContext范围
        else
            att = super.getScopeAttribute(scope);
        this.attributeForScopeForMap.put(scope, att);
        return att;
    }
    //==========================================================================================request查询参数专用方法
    /** 根据stack->jspPage->request->session->httpSession->servletContext->context->cookie这个顺序依次查找属性，在stack中查找时是在整个stack树中查找。*/
    public Object getParam(String key) {
        Object obj = this.getByStackTree(key);
        if (obj == null && this.httpPageContext != null)
            obj = this.getScopeAttribute(Scope_JspPage).getAttribute(key);
        if (obj == null && this.httpRequest != null)
            obj = this.getScopeAttribute(Scope_HttpRequest).getAttribute(key);
        if (obj == null && this.moreSessionScope != null)
            obj = this.getScopeAttribute(Scope_Session).getAttribute(key);
        if (obj == null && this.httpRequest != null)
            obj = this.getScopeAttribute(Scope_HttpSession).getAttribute(key);
        if (obj == null && this.getServletContext() != null)
            obj = this.getScopeAttribute(Scope_ServletContext).getAttribute(key);
        if (obj == null && this.moreContextScope != null)
            obj = this.getScopeAttribute(Scope_Context).getAttribute(key);
        if (obj == null && this.httpRequest != null && this.httpResponse != null)
            obj = this.getScopeAttribute(Scope_Cookie).getAttribute(key);
        return obj;
    };
    //==========================================================================================request查询参数专用方法
    /**获取request请求参数中所有参数名称。*/
    @SuppressWarnings("unchecked")
    public String[] getRequestParamNames() {
        Set keys = this.httpRequest.getParameterMap().keySet();
        String[] ns = new String[keys.size()];
        keys.toArray(ns);
        return ns;
    }
    /**仅从request请求参数中查找指定属性。*/
    public String[] getRequestParams(String attName) {
        return this.httpRequest.getParameterValues(attName);
    }
    /**仅从request请求参数中查找指定属性。*/
    public String getRequestParam(String attName) {
        return this.httpRequest.getParameter(attName);
    }
    //==========================================================================================Cookie作用域下的快速操作。
    /**如果当前作用域不是Cookie则会引发StateException异常。*/
    public void setCookieAttribute(String name, String value, int age) throws StateException {
        if (this.getScope().equals(Scope_Cookie) == false)
            throw new StateException("当前作用域不是Cookie。");
        ((CookieScope) this.getCurrentScopeAtt()).setCookieAttribute(name, value, age);
    };
    /**如果当前作用域不是Cookie则会引发StateException异常。*/
    public void setCookieAttribute(Cookie cookie) throws StateException {
        if (this.getScope().equals(Scope_Cookie) == false)
            throw new StateException("当前作用域不是Cookie。");
        ((CookieScope) this.getCurrentScopeAtt()).setCookieAttribute(cookie);
    };
    /**如果当前作用域不是Cookie则会引发StateException异常。*/
    public Cookie getCookieAttribute(String name) throws StateException {
        if (this.getScope().equals(Scope_Cookie) == false)
            throw new StateException("当前作用域不是Cookie。");
        return ((CookieScope) this.getCurrentScopeAtt()).getCookieAttribute(name);
    };
}