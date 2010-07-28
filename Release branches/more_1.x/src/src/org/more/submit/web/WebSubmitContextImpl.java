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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import org.more.FormatException;
import org.more.StateException;
import org.more.submit.ActionContext;
import org.more.submit.ActionInvoke;
import org.more.submit.ActionStack;
import org.more.submit.Session;
import org.more.submit.SubmitContextImpl;
import org.more.submit.Util;
/**
 * 该类是AbstractSubmitContextImpl的子类，该类为了提供web下的SubmitContext支持。
 * @version : 2010-7-26
 * @author 赵永春(zyc@byshell.org)
 */
public class WebSubmitContextImpl extends SubmitContextImpl implements WebSubmitContext {
    /**  */
    private static final long serialVersionUID = -7968875552670171988L;
    private ServletContext    servletContext   = null;
    private String            protocol         = "action";
    public WebSubmitContextImpl(ActionContext actionContext, ServletContext context) {
        super(actionContext);
    };
    @Override
    public boolean isWebContext() {
        return true;
    };
    protected WebActionStack createStack(String actionName, String actionMethod, ActionStack parent, Session session, PageContext pageContext, HttpServletRequest request, HttpServletResponse response) {
        return new WebActionStack(actionName, actionMethod, parent, session, this, request, response, pageContext);
    };
    public String getProtocol() {
        return protocol;
    }
    public void setProtocol(String protocol) {
        if (protocol == null || protocol.equals(""))
            this.protocol = WebSubmitContext.Default_Protocol;
        else
            this.protocol = protocol;
    }
    public ServletContext getServletContext() {
        return this.servletContext;
    };
    /**解析请求url并且分离出ActionInvoke字符串。*/
    protected String analyticURL(String headInfo) {
        if (headInfo == null)
            return null;
        Pattern p = Pattern.compile(".*/" + this.protocol + "!(.*)(\\?.*){0,}$");// /post:(//){0,1}(.*)(\?.*){0,}
        Matcher m = p.matcher(headInfo);
        if (m.find())
            return m.group(1);//获取请求参数中的调用表达试
        else
            return null;
    };
    public boolean isActionRequest(HttpServletRequest request) {
        String headInfo = ((HttpServletRequest) request).getRequestURL().toString();
        String actionInvoke = analyticURL(headInfo);
        if (actionInvoke == null)
            return false;
        return true;
    }
    public Object doActionOnStack(String invokeString, ActionStack stack, Map<String, ?> params) throws Throwable {
        Session session = stack.getSession();
        if (this.getSessionManager().isBelong(session) == false)
            throw new StateException("session " + session.getSessionID() + " 不属于当前sessionManager管理的session");
        String[] ss = Util.splitInvokeString(invokeString);
        ActionStack as = null;
        if (stack instanceof WebActionStack == true) {
            WebActionStack was = (WebActionStack) stack;
            as = this.createStack(ss[0], ss[1], stack, session, was.getPageContext(), was.getHttpRequest(), was.getHttpResponse());
        } else
            as = this.createStack(ss[0], ss[1], stack, session);
        ActionInvoke ai = this.getActionContext().findAction(ss[0], ss[1]);
        return this.invokeAction(ai, as, params);
    };
    @Override
    public Object doAction(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String headInfo = ((HttpServletRequest) request).getRequestURL().toString();
        String actionInvoke = analyticURL(headInfo);
        return this.doAction(actionInvoke, null, request, response, null);
    };
    @Override
    public Object doAction(String actionInvoke, HttpServletRequest request, HttpServletResponse response) throws Throwable {
        return this.doAction(actionInvoke, null, request, response, null);
    };
    @Override
    public Object doAction(String actionInvoke, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Throwable {
        return this.doAction(actionInvoke, null, request, response, params);
    };
    @Override
    public Object doAction(PageContext pageContext) throws Throwable {
        return doAction(pageContext, (HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse());
    };
    @Override
    public Object doAction(PageContext pageContext, HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String headInfo = ((HttpServletRequest) request).getRequestURL().toString();
        String actionInvoke = analyticURL(headInfo);
        return this.doAction(actionInvoke, pageContext, request, response, null);
    };
    @Override
    public Object doAction(String actionInvoke, PageContext pageContext, HttpServletRequest request, HttpServletResponse response) throws Throwable {
        return this.doAction(actionInvoke, pageContext, request, response, null);
    };
    @Override
    public Object doAction(String actionInvoke, PageContext pageContext, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) throws Throwable {
        if (actionInvoke == null || actionInvoke.equals(""))
            throw new FormatException("请指定actionInvoke调用字符串。");
        String actionName = Util.getActionString(actionInvoke);
        String actionMethod = Util.getMethodString(actionInvoke);
        Session session = new SessionSynchronize(request.getSession(true));
        //
        WebActionStack stack = this.createStack(actionName, actionMethod, null, session, pageContext, request, response);
        ActionInvoke action = this.getActionContext().findAction(actionName, actionMethod);
        return this.invokeAction(action, stack, params);
    };
};