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
package net.hasor.mvc.web.support;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hasor.mvc.support.Call;
import net.hasor.mvc.support.CallWrap;
import net.hasor.mvc.web.WebCall;
/**
 * 
 * @version : 2014年10月21日
 * @author 赵永春(zyc@hasor.net)
 */
class WebCallWrap extends CallWrap implements WebCall {
    private HttpServletRequest  httpRequest  = null;
    private HttpServletResponse httpResponse = null;
    //
    public WebCallWrap(WebCall targetCall) {
        super(targetCall);
        this.httpRequest = targetCall.getHttpRequest();
        this.httpResponse = targetCall.getHttpResponse();
    }
    public WebCallWrap(Call targetCall, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        super(targetCall);
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
    }
    @Override
    public HttpServletRequest getHttpRequest() {
        return this.httpRequest;
    }
    @Override
    public HttpServletResponse getHttpResponse() {
        return this.httpResponse;
    }
}