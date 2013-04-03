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
package org.platform.runtime.context;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.platform.api.context.AppContext;
import org.platform.api.context.ContextConfig;
/**
 * runtime的基本实现，这个类主要负责处理HttpServletRequest、HttpServletResponse
 * @version : 2013-4-3
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractAppContext extends AppContext {
    private ThreadLocal<ReqRes> httpLocal = new ThreadLocal<AbstractAppContext.ReqRes>();
    //
    protected AbstractAppContext(ContextConfig config) {
        super(config);
    }
    /**设置本次请求的request,response*/
    public void setCurrentHttp(HttpServletRequest req, HttpServletResponse res) {
        ReqRes reqres = this.httpLocal.get();
        if (reqres == null) {
            reqres = new ReqRes();
            this.httpLocal.set(reqres);
        }
        reqres.request = req;
        reqres.response = res;
    }
    /**重置request,response*/
    public void resetCurrentHttp() {
        ReqRes reqres = this.httpLocal.get();
        if (reqres == null) {
            reqres = new ReqRes();
            this.httpLocal.set(reqres);
        }
        reqres.request = null;
        reqres.response = null;
    }
    @Override
    public HttpServletRequest getHttpRequest() {
        ReqRes reqres = this.httpLocal.get();
        if (reqres != null)
            return reqres.request;
        return null;
    }
    @Override
    public HttpServletResponse getHttpResponse() {
        ReqRes reqres = this.httpLocal.get();
        if (reqres != null)
            return reqres.response;
        return null;
    }
    /**负责存放req,res的结构*/
    public static class ReqRes {
        public HttpServletRequest  request;
        public HttpServletResponse response;
    }
}