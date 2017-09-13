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
package net.hasor.web.startup;
import net.hasor.core.AppContext;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import net.hasor.web.Invoker;
import net.hasor.web.ServletVersion;
import net.hasor.web.invoker.ExceuteCaller;
import net.hasor.web.invoker.InvokerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * 入口Filter，同一个应用程序只能实例化一个 RuntimeFilter 对象。
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class RuntimeFilter implements Filter {
    protected           Logger         logger                     = LoggerFactory.getLogger(getClass());
    private final       AtomicBoolean  inited                     = new AtomicBoolean(false);
    public static final String         HTTP_REQUEST_ENCODING_KEY  = "HTTP_REQUEST_ENCODING";
    public static final String         HTTP_RESPONSE_ENCODING_KEY = "HTTP_RESPONSE_ENCODING";
    private             String         httpRequestEncoding        = null;
    private             String         httpResponseEncoding       = null;
    //
    private             AppContext     appContext                 = null;
    private             InvokerContext invokerContext             = null;
    //
    public void init(FilterConfig filterConfig) throws ServletException {
        if (!this.inited.compareAndSet(false, true)) {
            return;
        }
        Map<String, String> configMap = new HashMap<String, String>();
        Enumeration<?> names = filterConfig.getInitParameterNames();
        if (names != null) {
            while (names.hasMoreElements()) {
                String name = names.nextElement().toString();
                configMap.put(name, filterConfig.getInitParameter(name));
            }
        }
        // .编码
        AppContext appContext = RuntimeListener.getAppContext(filterConfig.getServletContext());
        this.httpRequestEncoding = appContext.findBindingBean(HTTP_REQUEST_ENCODING_KEY, String.class);
        this.httpResponseEncoding = appContext.findBindingBean(HTTP_RESPONSE_ENCODING_KEY, String.class);
        try {
            this.appContext = appContext;
            this.invokerContext = new InvokerContext();
            this.invokerContext.initContext(appContext, configMap);
        } catch (ServletException e) {
            throw (ServletException) e;
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
        //
        // .启动日志
        if (ServletVersion.V2_5.le(this.appContext.getInstance(ServletVersion.class))) {
            logger.info("RuntimeFilter started, at {}", filterConfig.getServletContext().getServerInfo());
        } else {
            logger.info("RuntimeFilter started, context at {}", filterConfig.getServletContext().getContextPath());
        }
    }
    @Override
    public void destroy() {
        this.invokerContext.destroyContext();
    }
    //
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //
        // .设置编码
        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;
        if (StringUtils.isNotBlank(this.httpRequestEncoding))
            httpReq.setCharacterEncoding(this.httpRequestEncoding);
        if (StringUtils.isNotBlank(this.httpResponseEncoding))
            httpRes.setCharacterEncoding(this.httpResponseEncoding);
        //
        // .执行
        try {
            this.beforeRequest(this.appContext, httpReq, httpRes);
            doFilter(chain, httpReq, httpRes);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            String mode = appContext.getEnvironment().getWorkMode();
            if ("debug".equalsIgnoreCase(mode) && !response.isCommitted()) {
                e.printStackTrace(response.getWriter());
            }
            if (e instanceof IOException)
                throw (IOException) e;
            if (e instanceof ServletException)
                throw (ServletException) e;
            throw ExceptionUtils.toRuntimeException(e);
        } finally {
            this.afterResponse(this.appContext, httpReq, httpRes);
        }
    }
    private void doFilter(FilterChain chain, HttpServletRequest httpReq, HttpServletResponse httpRes) throws IOException, ServletException {
        try {
            Invoker invoker = this.invokerContext.newInvoker(httpReq, httpRes);
            ExceuteCaller caller = this.invokerContext.genCaller(invoker);
            if (caller != null) {
                Future<Object> resultData = caller.invoke(invoker, chain);
                if (resultData != null && resultData.isDone()) {
                    resultData.get();
                }
            } else {
                chain.doFilter(httpReq, httpRes);
            }
            //
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException)
                throw (IOException) cause;
            if (cause instanceof ServletException)
                throw (ServletException) cause;
            if (cause instanceof RuntimeException)
                throw (RuntimeException) cause;
            throw ExceptionUtils.toRuntimeException(cause);
            //
        } catch (IOException e) {
            throw (IOException) e;
        } catch (ServletException e) {
            throw (ServletException) e;
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    //
    /**在filter请求处理之前。*/
    protected void beforeRequest(final AppContext appContext, final HttpServletRequest httpReq, final HttpServletResponse httpRes) {
    }
    //
    /**在filter请求处理之后。*/
    protected void afterResponse(final AppContext appContext, final HttpServletRequest httpReq, final HttpServletResponse httpRes) {
    }
}