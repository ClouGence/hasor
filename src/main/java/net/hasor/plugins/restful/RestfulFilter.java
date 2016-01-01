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
package net.hasor.plugins.restful;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.more.util.StringUtils;
import net.hasor.core.AppContext;
import net.hasor.web.startup.RuntimeListener;
/**
 * action功能的入口。
 * @version : 2013-5-11
 * @author 赵永春 (zyc@hasor.net)
 */
class RestfulFilter implements Filter {
    private String[]          interceptNames = null;
    private MappingToDefine[] invokeArray    = new MappingToDefine[0];
    //
    public void init(FilterConfig filterConfig) throws ServletException {
        //1.拦截
        AppContext appContext = RuntimeListener.getAppContext(filterConfig.getServletContext());
        String interceptNames = appContext.getEnvironment().getSettings().getString("hasor.restful.urlPatterns", "do;");
        Set<String> names = new HashSet<String>();
        for (String name : interceptNames.split(";")) {
            if (StringUtils.isBlank(name) == false) {
                names.add(name);
            }
        }
        this.interceptNames = names.toArray(new String[names.size()]);
        //
        //2.Find MappingInfoDefine
        List<MappingToDefine> mappingList = appContext.findBindingBean(MappingToDefine.class);
        Collections.sort(mappingList, new Comparator<MappingToDefine>() {
            public int compare(MappingToDefine o1, MappingToDefine o2) {
                return o1.getMappingTo().compareToIgnoreCase(o2.getMappingTo()) * -1;
            }
        });
        //3.初始化
        for (MappingToDefine define : mappingList) {
            define.init(appContext);
        }
        MappingToDefine[] defineArrays = mappingList.toArray(new MappingToDefine[mappingList.size()]);
        if (defineArrays != null) {
            this.invokeArray = defineArrays;
        }
    }
    //
    public void destroy() {
        //
    }
    //
    private MappingToDefine findMapping(String actionMethod, String actionPath) {
        for (MappingToDefine invoke : this.invokeArray) {
            if (invoke.matchingMapping(actionMethod, actionPath) == true) {
                return invoke;
            }
        }
        return null;
    }
    //
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String actionPath = request.getRequestURI().substring(request.getContextPath().length());
        String actionMethod = request.getMethod();
        //
        for (int i = 0; i < this.interceptNames.length; i++) {
            String name = this.interceptNames[i];
            if (actionPath.endsWith(name)) {
                MappingToDefine define = findMapping(actionMethod, actionPath);
                if (define != null) {
                    doInvoke(define, request, resp);
                    return;
                }
            }
        }
        //
        if (resp.isCommitted() == false) {
            chain.doFilter(request, resp);
        }
    }
    private void doInvoke(MappingToDefine define, ServletRequest servletRequest, final ServletResponse servletResponse) throws ServletException, IOException {
        try {
            HttpServletRequest httpReq = (HttpServletRequest) servletRequest;
            httpReq = new HttpServletRequestWrapper(httpReq) {
                public RequestDispatcher getRequestDispatcher(final String path) {
                    final RequestDispatcher dispatcher = getReqDispatcher(path, this);
                    return null != dispatcher ? dispatcher : super.getRequestDispatcher(path);
                }
            };
            //
            HttpServletResponse httpResp = (HttpServletResponse) servletResponse;
            define.invoke(httpReq, httpResp);
            //
        } catch (Throwable target) {
            if (target instanceof ServletException)
                throw (ServletException) target;
            if (target instanceof IOException)
                throw (IOException) target;
            if (target instanceof RuntimeException)
                throw (RuntimeException) target;
            throw new ServletException(target);
        }
    }
    //
    //
    /** 为转发提供支持 */
    private RequestDispatcher getReqDispatcher(final String newRequestUri, final HttpServletRequest request) {
        // TODO 需要检查下面代码是否符合Servlet规范（带request参数情况下也需要检查）
        //1.拆分请求字符串
        final MappingToDefine define = this.findMapping(request.getMethod(), newRequestUri);
        if (define == null)
            return null;
        //
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
    //
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