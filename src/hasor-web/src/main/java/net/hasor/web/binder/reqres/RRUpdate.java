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
package net.hasor.web.binder.reqres;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 
 * @version : 2014-8-3
 * @author 赵永春(zyc@hasor.net)
 */
public class RRUpdate {
    private static ThreadLocal<HttpServletRequest>  LocalRequest  = new ThreadLocal<HttpServletRequest>();
    private static ThreadLocal<HttpServletResponse> LocalResponse = new ThreadLocal<HttpServletResponse>();
    //
    /**获取{@link HttpServletRequest}*/
    public static HttpServletRequest getLocalRequest() {
        return LocalRequest.get();
    }
    //
    /**获取{@link HttpServletResponse}*/
    public static HttpServletResponse getLocalResponse() {
        return LocalResponse.get();
    }
    //
    public void update(HttpServletRequest httpReq, HttpServletResponse httpRes) {
        if (httpReq != null) {
            if (LocalRequest.get() != null) {
                LocalRequest.remove();
            }
            LocalRequest.set(httpReq);
        }
        if (httpRes != null) {
            if (LocalResponse.get() != null) {
                LocalResponse.remove();
            }
            LocalResponse.set(httpRes);
        }
    }
    //
    public void release() {
        if (LocalRequest.get() != null) {
            LocalRequest.remove();
        }
        if (LocalResponse.get() != null) {
            LocalResponse.remove();
        }
    }
}