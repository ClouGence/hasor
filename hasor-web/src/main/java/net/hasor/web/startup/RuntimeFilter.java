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
import net.hasor.core.spi.SpiTrigger;
import net.hasor.utils.StringUtils;
import net.hasor.web.ServletVersion;
import net.hasor.web.binder.OneConfig;
import net.hasor.web.invoker.ExecuteCaller;
import net.hasor.web.invoker.InvokerContext;
import net.hasor.web.spi.AfterResponseListener;
import net.hasor.web.spi.BeforeRequestListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
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
    private             AppContext     appContext                 = null;
    private             SpiTrigger     spiTrigger                 = null;
    private             InvokerContext invokerContext             = null;

    public RuntimeFilter() {
        this(null);
    }

    public RuntimeFilter(AppContext appContext) {
        this.appContext = appContext;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        if (!this.inited.compareAndSet(false, true)) {
            return;
        }
        // .编码
        if (this.appContext == null) {
            this.appContext = Objects.requireNonNull(RuntimeListener.getAppContext(filterConfig.getServletContext()), "AppContext has not been initialized.");
        }
        this.httpRequestEncoding = this.appContext.findBindingBean(HTTP_REQUEST_ENCODING_KEY, String.class);
        this.httpResponseEncoding = this.appContext.findBindingBean(HTTP_RESPONSE_ENCODING_KEY, String.class);
        try {
            this.spiTrigger = this.appContext.getInstance(SpiTrigger.class);
            this.invokerContext = new InvokerContext();
            this.invokerContext.initContext(this.appContext, new OneConfig(filterConfig, () -> appContext));
        } catch (ServletException e) {
            throw e;
        } catch (Throwable e) {
            throw new ServletException(e);
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
        if (this.inited.compareAndSet(true, false)) {
            this.invokerContext.destroyContext();
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //
        // .设置编码
        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;
        if (StringUtils.isNotBlank(this.httpRequestEncoding)) {
            httpReq.setCharacterEncoding(this.httpRequestEncoding);
        }
        if (StringUtils.isNotBlank(this.httpResponseEncoding)) {
            httpRes.setCharacterEncoding(this.httpResponseEncoding);
        }
        //
        // .执行
        Object result = null;
        try {
            this.beforeRequest(this.appContext, httpReq, httpRes);
            result = doFilter(chain, httpReq, httpRes);
        } finally {
            this.afterResponse(this.appContext, httpReq, httpRes, result);
        }
    }

    private Object doFilter(FilterChain chain, HttpServletRequest httpReq, HttpServletResponse httpRes) throws IOException, ServletException {
        Object result = null;
        try {
            ExecuteCaller caller = this.invokerContext.genCaller(httpReq, httpRes);
            if (caller != null) {
                Future<Object> resultData = caller.invoke(chain);
                if (resultData != null && resultData.isDone()) {
                    result = resultData.get();
                }
            } else {
                chain.doFilter(httpReq, httpRes);
            }
        } catch (Throwable e) {
            if (e instanceof ExecutionException) {
                e = e.getCause();
            }
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            if (e instanceof ServletException) {
                throw (ServletException) e;
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new ServletException(e);
        }
        return result;
    }

    /** 在filter请求处理之前 */
    protected void beforeRequest(final AppContext appContext, final HttpServletRequest httpReq, final HttpServletResponse httpRes) {
        this.spiTrigger.notifySpiWithoutResult(BeforeRequestListener.class, listener -> listener.doListener(appContext, httpReq, httpRes));
    }

    /** 在filter请求处理之后 */
    protected void afterResponse(final AppContext appContext, final HttpServletRequest httpReq, final HttpServletResponse httpRes, Object result) {
        this.spiTrigger.notifySpiWithoutResult(AfterResponseListener.class, listener -> listener.doListener(appContext, httpReq, httpRes, result));
    }
}
