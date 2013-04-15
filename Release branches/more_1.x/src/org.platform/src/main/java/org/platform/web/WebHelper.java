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
package org.platform.web;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 * 该类提供了获取与当前线程进行绑定的{@link HttpServletRequest}、{@link HttpServletResponse}，其子类通过调用initWebHelper和clearWebHelper两个静态方法以管理绑定对象。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class WebHelper {
    private static final ThreadLocal<HttpServletRequest>  currentRequest  = new ThreadLocal<HttpServletRequest>();
    private static final ThreadLocal<HttpServletResponse> currentResponse = new ThreadLocal<HttpServletResponse>();
    //
    /**判断{@link WebHelper}类是否可以使用。*/
    public static boolean canUse() {
        HttpServletRequest request = getHttpRequest();
        if (request == null)
            return false;
        else
            return true;
    }
    /**获取与当前线程相关联的{@link HttpServletRequest}接口对象。*/
    public static HttpServletRequest getHttpRequest() {
        return currentRequest.get();
    }
    /**获取与当前线程相关联的{@link HttpServletResponse}接口对象。*/
    public static HttpServletResponse getHttpResponse() {
        return currentResponse.get();
    }
    /**获取与当前线程相关联的{@link ServletContext}接口对象。*/
    public static ServletContext getServletContext() {
        if (canUse() == true)
            return getHttpRequest().getServletContext();
        return null;
    }
    /**获取与当前线程相关联的{@link HttpSession}接口对象。*/
    public static HttpSession getHttpSession(boolean create) {
        if (canUse() == true)
            getHttpRequest().getSession(create);
        return null;
    }
    //
    //
    /**该方法由runtime保护起来不允许开发者直接调用，调用该方法会导致环境中{@link HttpServletRequest}、{@link HttpServletResponse}对象混乱。*/
    protected synchronized static void initWebHelper(HttpServletRequest reqHttp, HttpServletResponse resHttp) {
        clearWebHelper();
        currentRequest.set(reqHttp);
        currentResponse.set(resHttp);
    }
    /**清空WebHelper中与当前线程关联的{@link HttpServletRequest}、{@link HttpServletResponse}对象。*/
    protected synchronized static void clearWebHelper() {
        if (currentRequest.get() != null)
            currentRequest.remove();
        if (currentResponse.get() != null)
            currentResponse.remove();
    }
}