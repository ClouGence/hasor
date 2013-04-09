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
package org.platform.runtime;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 * 
 * @version : 2013-4-9
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class WebHelper {
    private static final ThreadLocal<HttpServletRequest>  currentRequest  = new ThreadLocal<HttpServletRequest>();
    private static final ThreadLocal<HttpServletResponse> currentResponse = new ThreadLocal<HttpServletResponse>();
    //
    //
    //
    public static boolean canUse() {
        HttpServletRequest request = getHttpRequest();
        if (request == null)
            return false;
        else
            return true;
    }
    public static HttpServletRequest getHttpRequest() {
        return currentRequest.get();
    }
    public static HttpServletResponse getHttpResponse() {
        return currentResponse.get();
    }
    public static ServletContext getServletContext() {
        if (canUse() == true)
            return getHttpRequest().getServletContext();
        return null;
    }
    public static HttpSession getHttpSession(boolean create) {
        if (canUse() == true)
            getHttpRequest().getSession(create);
        return null;
    }
    //
    //
    protected void initWebHelper(HttpServletRequest reqHttp, HttpServletResponse resHttp) {
        this.clearWebHelper();
        currentRequest.set(reqHttp);
        currentResponse.set(resHttp);
    }
    protected void clearWebHelper() {
        if (currentRequest.get() != null)
            currentRequest.remove();
        if (currentResponse.get() != null)
            currentResponse.remove();
    }
}