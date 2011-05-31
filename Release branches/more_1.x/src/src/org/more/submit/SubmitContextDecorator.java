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
package org.more.submit;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import org.more.core.error.SupportException;
import org.more.submit.web.WebSubmitContext;
/**
 * SubmitContext接口装饰器，该类仅仅是一个无任何附加功能的空装饰器，可以通过其子类可以重写某些方法以扩充其功能的目的。
 * @version : 2010-7-26
 * @author 赵永春(zyc@byshell.org)
 */
public abstract class SubmitContextDecorator implements SubmitContext, WebSubmitContext {
    /**  */
    private static final long  serialVersionUID = -546605355053294936L;
    protected SubmitContext    submitContext    = null;
    protected WebSubmitContext webSubmitContext = null;
    /**如果这个装饰器成功初始化则需要返回true否则返回false，submit根据这个上下文决定是否保留这个装饰器。*/
    public boolean initDecorator(SubmitContext submitContext) {
        this.submitContext = submitContext;
        if (submitContext instanceof WebSubmitContext)
            this.webSubmitContext = (WebSubmitContext) submitContext;
        return true;
    };
    //===================================================================================
    public Object doAction(String invokeString, Map<String, ?> params) throws Throwable {
        return this.submitContext.doAction(invokeString, params);
    };
    public Object doAction(String invokeString, Session session, Map<String, ?> params) throws Throwable {
        return this.submitContext.doAction(invokeString, session, params);
    };
    public Object doAction(String invokeString, String sessionID, Map<String, ?> params) throws Throwable {
        return this.submitContext.doAction(invokeString, sessionID, params);
    };
    public Object doAction(String invokeString) throws Throwable {
        return this.submitContext.doAction(invokeString);
    };
    public Object doActionOnStack(String invokeString, ActionStack stack, Map<String, ?> params) throws Throwable {
        return this.submitContext.doActionOnStack(invokeString, stack, params);
    };
    public ActionContext getActionContext() {
        return this.submitContext.getActionContext();
    };
    public Iterator<String> getActionInvokeStringIterator() {
        return this.submitContext.getActionInvokeStringIterator();
    };
    public SessionManager getSessionManager() {
        return this.submitContext.getSessionManager();
    };
    public boolean isWebContext() {
        return this.submitContext.isWebContext();
    };
    public void setSessionManager(SessionManager sessionManager) {
        this.submitContext.setSessionManager(sessionManager);
    };
    //===================================================================================
    public Object doAction(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        if (this.webSubmitContext == null)
            throw new SupportException("被装饰的submitContext不是一个webSubmitContext。");
        return this.webSubmitContext.doAction(request, response);
    };
    public Object doAction(PageContext pageContext, HttpServletRequest request, HttpServletResponse response) throws Throwable {
        if (this.webSubmitContext == null)
            throw new SupportException("被装饰的submitContext不是一个webSubmitContext。");
        return this.webSubmitContext.doAction(pageContext, request, response);
    };
    public Object doAction(PageContext pageContext) throws Throwable {
        if (this.webSubmitContext == null)
            throw new SupportException("被装饰的submitContext不是一个webSubmitContext。");
        return this.webSubmitContext.doAction(pageContext);
    };
    public Object doAction(String actionInvoke, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Throwable {
        if (this.webSubmitContext == null)
            throw new SupportException("被装饰的submitContext不是一个webSubmitContext。");
        return this.webSubmitContext.doAction(actionInvoke, request, response, params);
    };
    public Object doAction(String actionInvoke, HttpServletRequest request, HttpServletResponse response) throws Throwable {
        if (this.webSubmitContext == null)
            throw new SupportException("被装饰的submitContext不是一个webSubmitContext。");
        return this.webSubmitContext.doAction(actionInvoke, request, response);
    };
    public Object doAction(String actionInvoke, PageContext pageContext, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Throwable {
        if (this.webSubmitContext == null)
            throw new SupportException("被装饰的submitContext不是一个webSubmitContext。");
        return this.webSubmitContext.doAction(actionInvoke, pageContext, request, response, params);
    };
    public Object doAction(String actionInvoke, PageContext pageContext, HttpServletRequest request, HttpServletResponse response) throws Throwable {
        if (this.webSubmitContext == null)
            throw new SupportException("被装饰的submitContext不是一个webSubmitContext。");
        return this.webSubmitContext.doAction(actionInvoke, pageContext, request, response);
    };
    public String getProtocol() {
        if (this.webSubmitContext == null)
            throw new SupportException("被装饰的submitContext不是一个webSubmitContext。");
        return this.webSubmitContext.getProtocol();
    };
    public ServletContext getServletContext() {
        if (this.webSubmitContext == null)
            throw new SupportException("被装饰的submitContext不是一个webSubmitContext。");
        return this.webSubmitContext.getServletContext();
    };
    public boolean isActionRequest(HttpServletRequest request) {
        if (this.webSubmitContext == null)
            throw new SupportException("被装饰的submitContext不是一个webSubmitContext。");
        return this.webSubmitContext.isActionRequest(request);
    };
    public void setProtocol(String protocol) {
        if (this.webSubmitContext == null)
            throw new SupportException("被装饰的submitContext不是一个webSubmitContext。");
        this.webSubmitContext.setProtocol(protocol);
    };
    //===================================================================================
    public void clearAttribute() {
        this.submitContext.clearAttribute();
    };
    public boolean contains(String name) {
        return this.submitContext.contains(name);
    };
    public Object getAttribute(String name) {
        return this.submitContext.getAttribute(name);
    };
    public String[] getAttributeNames() {
        return this.submitContext.getAttributeNames();
    };
    public void removeAttribute(String name) {
        this.submitContext.removeAttribute(name);
    };
    public void setAttribute(String name, Object value) {
        this.submitContext.setAttribute(name, value);
    };
};