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
package org.more.web.submit;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
import org.more.services.submit.SubmitService;
/**
 * 协助获取外部request等对象。
 * @version : 2011-7-22
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class WebHelper {
    private static ThreadLocal<PageContext>         httpPageContext = new ThreadLocal<PageContext>();
    private static ThreadLocal<HttpServletRequest>  httpRequest     = new ThreadLocal<HttpServletRequest>();
    private static ThreadLocal<HttpServletResponse> httpResponse    = new ThreadLocal<HttpServletResponse>();
    private static ThreadLocal<HttpSession>         httpSession     = new ThreadLocal<HttpSession>();
    private static ThreadLocal<ServletContext>      httpContext     = new ThreadLocal<ServletContext>();
    private static ThreadLocal<SubmitService>       submitContext   = new ThreadLocal<SubmitService>();
    /*-----------------------------------------------------------------------------------------------------*/
    static void setHttpPageContext(PageContext httpPageContext) {
        if (WebHelper.httpPageContext.get() != null)
            WebHelper.httpPageContext.remove();
        if (httpPageContext == null)
            WebHelper.httpPageContext.remove();
        else
            WebHelper.httpPageContext.set(httpPageContext);
    }
    static void setHttpRequest(HttpServletRequest httpRequest) {
        if (WebHelper.httpRequest.get() != null)
            WebHelper.httpRequest.remove();
        if (httpRequest == null)
            WebHelper.httpRequest.remove();
        else
            WebHelper.httpRequest.set(httpRequest);
    }
    static void setHttpResponse(HttpServletResponse httpResponse) {
        if (WebHelper.httpResponse.get() != null)
            WebHelper.httpResponse.remove();
        if (httpResponse == null)
            WebHelper.httpResponse.remove();
        else
            WebHelper.httpResponse.set(httpResponse);
    }
    static void setSubmitContext(SubmitService submitContext) {
        if (WebHelper.submitContext.get() != null)
            WebHelper.submitContext.remove();
        if (submitContext == null)
            WebHelper.submitContext.remove();
        else
            WebHelper.submitContext.set(submitContext);
    }
    static void setHttpContext(ServletContext httpContext) {
        if (WebHelper.httpContext.get() != null)
            WebHelper.httpContext.remove();
        if (httpContext == null)
            WebHelper.httpContext.remove();
        else
            WebHelper.httpContext.set(httpContext);
    }
    static void setHttpSession(HttpSession httpSession) {
        if (WebHelper.httpSession.get() != null)
            WebHelper.httpSession.remove();
        if (httpSession == null)
            WebHelper.httpSession.remove();
        else
            WebHelper.httpSession.set(httpSession);
    }
    /*-----------------------------------------------------------------------------------------------------*/
    public static void reset() {
        setHttpPageContext(null);
        setHttpRequest(null);
        setHttpResponse(null);
        setSubmitContext(null);
        setHttpContext(null);
        setHttpSession(null);
    };
    /**获取PageContext对象。*/
    public static PageContext getPageContext() {
        return httpPageContext.get();
    };
    /**获取HttpServletRequest对象。*/
    public static HttpServletRequest getHttpRequest() {
        return httpRequest.get();
    };
    /**获取HttpServletResponse对象。*/
    public static HttpServletResponse getHttpResponse() {
        return httpResponse.get();
    };
    /**获取HttpSession对象。*/
    public static HttpSession getHttpSession() {
        return httpSession.get();
    };
    /**获取ServletContext对象。*/
    public static ServletContext getServletContext() {
        return httpContext.get();
    };
    public static SubmitService getSubmitService() {
        return submitContext.get();
    }
}