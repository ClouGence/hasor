/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.web.controller.support;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import net.hasor.core.AppContext;
import net.hasor.web.controller.AbstractController;
import net.hasor.web.controller.Controller;
import net.hasor.web.controller.ControllerException;
import net.hasor.web.controller.ControllerInvoke;
import org.more.util.ArrayUtils;
import org.more.util.BeanUtils;
import org.more.util.StringUtils;
import org.more.util.exception.ExceptionUtils;
import com.google.inject.Inject;
/**
 * action功能的入口。
 * @version : 2013-5-11
 * @author 赵永春 (zyc@hasor.net)
 */
class ControllerServlet extends HttpServlet {
    private static final long serialVersionUID = -8402094243884745631L;
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestPath = request.getRequestURI().substring(request.getContextPath().length());
        //
        //1.拆分请求字符串
        ControllerInvoke invoke = getActionInvoke(requestPath, request.getMethod());
        if (invoke == null) {
            String logInfo = String.format("%s action is not defined.", requestPath);
            throw new ControllerException(logInfo);
        }
        //3.执行调用
        doInvoke(invoke, request, response);
    }
    private void doInvoke(ControllerInvoke invoke, ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        try {
            invoke.invoke((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
        } catch (Throwable e) {
            //1.尝试拆开异常
            Throwable target = ExceptionUtils.getCause(e);
            //            if (target instanceof UnhandledException)
            //                target = target.getCause();
            //            if (target instanceof InvocationTargetException)
            //                target = ((InvocationTargetException) target).getTargetException();
            //2.判定异常类型
            if (target instanceof ServletException)
                throw (ServletException) target;
            if (target instanceof IOException)
                throw (IOException) target;
            throw new ServletException(target);
        }
    }
    //
    //
    //
    //
    //
    //
    @Inject
    private AppContext            appContext = null;
    private ControllerNameSpace[] spaceMap   = null;
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ControllerSettings settings = this.appContext.getInstance(ControllerSettings.class);
        Set<Class<?>> controllerSet = this.appContext.getClassSet(Controller.class);
        if (controllerSet == null)
            return;
        //2.注册服务
        Object[] ignoreMethods = settings.getIgnoreMethod().toArray();//忽略
        Map<String, ControllerNameSpace> nsMap = new HashMap<String, ControllerNameSpace>();
        for (Class<?> controllerType : controllerSet) {
            if (!AbstractController.class.isAssignableFrom(controllerType))
                continue;
            //
            Controller controllerAnno = controllerType.getAnnotation(Controller.class);
            String namespace = controllerAnno.value();
            namespace = StringUtils.isBlank(namespace) ? "/" : (namespace.charAt(namespace.length() - 1) == '/') ? namespace : (namespace + "/");
            //
            ControllerNameSpace nameSpace = nsMap.get(namespace);
            if (nameSpace == null) {
                nameSpace = new ControllerNameSpace(namespace);
                nsMap.put(namespace, nameSpace);
            }
            List<Method> actionMethods = BeanUtils.getMethods(controllerType);
            for (Method targetMethod : actionMethods) {
                if (ArrayUtils.contains(ignoreMethods, targetMethod.getName()) == true)
                    continue;/*执行忽略*/
                nameSpace.addAction(targetMethod, this.appContext);
            }
            /**/
        }
        //3.
        this.spaceMap = nsMap.values().toArray(new ControllerNameSpace[nsMap.size()]);
    }
    private ControllerInvoke getActionInvoke(String requestPath, String httpMethod) {
        //1.拆分请求字符串
        String actionNS = requestPath.substring(0, requestPath.lastIndexOf("/") + 1);
        String actionInvoke = requestPath.substring(requestPath.lastIndexOf("/") + 1);
        String actionMethod = actionInvoke.split("\\.")[0];
        //2.获取 ActionInvoke
        for (ControllerNameSpace ns : this.spaceMap) {
            if (!StringUtils.equalsIgnoreCase(ns.getNameSpace(), actionNS))
                continue;
            return ns.getActionByName(actionMethod);
        }
        return null;
    }
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
        final ControllerInvoke define = getActionInvoke(path, httpMethod);
        if (define == null)
            return null;
        else
            return new RequestDispatcher() {
                public void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
                    servletRequest.setAttribute(REQUEST_DISPATCHER_REQUEST, Boolean.TRUE);
                    /*执行servlet*/
                    try {
                        doInvoke(define, servletRequest, servletResponse);
                    } finally {
                        servletRequest.removeAttribute(REQUEST_DISPATCHER_REQUEST);
                    }
                }
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
                        doInvoke(define, requestToProcess, servletResponse);
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
        public String getRequestURI() {
            return newRequestUri;
        }
    }
}