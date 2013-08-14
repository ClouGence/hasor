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
package org.hasor.mvc.controller.support;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.hasor.Hasor;
import org.hasor.context.AppContext;
import org.hasor.mvc.controller.ActionException;
import org.more.util.MatchUtils;
import com.google.inject.Inject;
/**
 * action功能的入口。
 * @version : 2013-5-11
 * @author 赵永春 (zyc@byshell.org)
 */
class ActionController extends HttpServlet {
    private static final long serialVersionUID = -2579757349905408506L;
    @Inject
    private AppContext        appContext       = null;
    private ActionManager     actionManager    = null;
    private ActionSettings    actionSettings   = null;
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.actionManager = appContext.getInstance(ActionManager.class);
        this.actionManager.initManager(appContext);
        this.actionSettings = appContext.getInstance(ActionSettings.class);
        Hasor.info("ActionController intercept %s.", actionSettings.getIntercept());
    }
    public boolean testURL(HttpServletRequest request) {
        String requestPath = request.getRequestURI().substring(request.getContextPath().length());
        if (MatchUtils.matchWild(actionSettings.getIntercept(), requestPath) == false)
            return false;
        return true;
    }
    //
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestPath = request.getRequestURI().substring(request.getContextPath().length());
        if (MatchUtils.matchWild(actionSettings.getIntercept(), requestPath) == false)
            return;
        //
        //1.拆分请求字符串
        ActionDefineImpl invoke = getActionDefine(requestPath, request.getMethod());
        if (invoke == null) {
            String logInfo = String.format("%s action is not defined.", requestPath);
            throw new ActionException(logInfo);
        }
        //3.执行调用
        try {
            Object result = invoke.createInvoke(request, response).invoke();
        } catch (ServletException e) {
            if (e.getCause() instanceof IOException)
                throw (IOException) e.getCause();
            else
                throw e;
        }
    }
    private ActionDefineImpl getActionDefine(String requestPath, String httpMethod) {
        //1.拆分请求字符串
        String actionNS = requestPath.substring(0, requestPath.lastIndexOf("/") + 1);
        String actionInvoke = requestPath.substring(requestPath.lastIndexOf("/") + 1);
        String actionMethod = actionInvoke.split("\\.")[0];
        //2.获取 ActionInvoke
        ActionNameSpace nameSpace = actionManager.findNameSpace(actionNS);
        if (nameSpace != null)
            return nameSpace.getActionByName(actionMethod);
        return null;
    }
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    /** 为转发提供支持 */
    public RequestDispatcher getRequestDispatcher(String path, String httpMethod) {
        // TODO 需要检查下面代码是否符合Servlet规范（带request参数情况下也需要检查）
        final String newRequestUri = path;
        //1.拆分请求字符串
        final ActionDefineImpl define = getActionDefine(path, httpMethod);
        if (define == null)
            return null;
        else
            return new RequestDispatcher() {
                @Override
                public void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
                    servletRequest.setAttribute(REQUEST_DISPATCHER_REQUEST, Boolean.TRUE);
                    /*执行servlet*/
                    try {
                        define.createInvoke(servletRequest, servletResponse).invoke();
                    } finally {
                        servletRequest.removeAttribute(REQUEST_DISPATCHER_REQUEST);
                    }
                }
                @Override
                public void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
                    if (servletResponse.isCommitted() == true)
                        throw new ServletException("Response has been committed--you can only call forward before committing the response (hint: don't flush buffers)");
                    /*清空缓冲*/
                    servletResponse.resetBuffer();
                    ServletRequest requestToProcess;
                    if (servletRequest instanceof HttpServletRequest) {
                        requestToProcess = new RequestDispatcherRequestWrapper(servletRequest, newRequestUri);
                    } else {
                        //正常情况之下不会执行这段代码。
                        requestToProcess = servletRequest;
                    }
                    /*执行转发*/
                    servletRequest.setAttribute(REQUEST_DISPATCHER_REQUEST, Boolean.TRUE);
                    try {
                        define.createInvoke(requestToProcess, servletResponse).invoke();
                    } finally {
                        servletRequest.removeAttribute(REQUEST_DISPATCHER_REQUEST);
                    }
                }
            };
    }
    /** 使用RequestDispatcherRequestWrapper类处理request.getRequestURI方法的返回值*/
    public static final String REQUEST_DISPATCHER_REQUEST = "javax.servlet.forward.servlet_path";
    private static class RequestDispatcherRequestWrapper extends HttpServletRequestWrapper {
        private final String newRequestUri;
        public RequestDispatcherRequestWrapper(ServletRequest servletRequest, String newRequestUri) {
            super((HttpServletRequest) servletRequest);
            this.newRequestUri = newRequestUri;
        }
        @Override
        public String getRequestURI() {
            return newRequestUri;
        }
    }
}