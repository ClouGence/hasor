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
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import org.more.submit.ActionContext;
import org.more.submit.ActionStack;
import org.more.submit.ImplSubmitContext;
import org.more.submit.Session;
/**临时数据*/
class WebSubmitContext_Temp {
    HttpServletRequest  request     = null; //
    HttpServletResponse response    = null; //
    PageContext         pageContext = null; //
}
/**
 * 该类提供了SubmitContext接口，并且保证了SubmitContext的属性与ServletContext属性的同步。
 * <br/>Date : 2009-12-28
 * @author 赵永春
 */
public class WebSubmitContext extends ImplSubmitContext {
    /**  */
    private static final long     serialVersionUID = -1892482332470385149L;
    private ServletContext        context          = null;
    private WebSubmitContext_Temp tempData         = null;
    /***/
    public WebSubmitContext(ActionContext actionContext, ServletContext sc) {
        super(actionContext);
        this.context = sc;
    }
    //==========================================================================================Job
    @Override
    protected ActionStack createActionStack(ActionStack parent, Session session, ImplSubmitContext context) {
        if (tempData == null)
            return super.createActionStack(parent, session, context);
        WebActionStack stack = new WebActionStack(parent, session, context);
        stack.setPageContext(tempData.pageContext);
        stack.setRequest(tempData.request);
        stack.setResponse(tempData.response);
        this.tempData = null;//清空临时数据。
        stack.init();
        return stack;
    }
    public Object doAction(String invokeString, Session session, Map<String, ?> params, HttpServletRequest request, HttpServletResponse response, PageContext pageContext) throws Throwable {
        tempData = new WebSubmitContext_Temp();
        tempData.pageContext = pageContext;
        tempData.request = request;
        tempData.response = response;
        return super.doAction(invokeString, session, params);//该方法会马上调用createActionStack方法。
    }
    @Override
    public Object doActionOnStack(String invokeString, ActionStack stack, Map<String, ?> params) throws Throwable {
        if (stack instanceof WebActionStack) {
            WebActionStack requestStack = (WebActionStack) stack;
            tempData = new WebSubmitContext_Temp();
            tempData.pageContext = requestStack.getPageContext();
            tempData.request = requestStack.getRequest();
            tempData.response = requestStack.getResponse();
        }
        return super.doActionOnStack(invokeString, stack, params);//该方法会马上调用createActionStack方法。
    }
    /**获取ServletContext对象。*/
    public ServletContext getServletContext() {
        return context;
    }
}