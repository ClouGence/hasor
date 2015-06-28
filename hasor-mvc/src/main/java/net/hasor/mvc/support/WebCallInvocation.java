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
package net.hasor.mvc.support;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hasor.mvc.MappingInfo;
import net.hasor.mvc.ModelController;
import net.hasor.mvc.WebCall;
import net.hasor.mvc.WebCallInterceptor;
/**
 * 
 * @version : 2013-4-13
 * @author 赵永春 (zyc@hasor.net)
 */
class WebCallInvocation implements WebCall {
    private WebCallInterceptor[] webCallInterceptor;
    private WebCall              targetWebCall;
    private int                  index = -1;
    //
    public WebCallInvocation(WebCall targetWebCall, WebCallInterceptor[] webCallInterceptor) {
        this.targetWebCall = targetWebCall;
        if (webCallInterceptor == null) {
            this.webCallInterceptor = new WebCallInterceptor[0];
        } else {
            this.webCallInterceptor = webCallInterceptor;
        }
    }
    public Method getMethod() {
        return targetWebCall.getMethod();
    }
    public Class<?>[] getParameterTypes() {
        return targetWebCall.getParameterTypes();
    }
    public Annotation[][] getMethodParamAnnos() {
        return targetWebCall.getMethodParamAnnos();
    }
    public Annotation[] getAnnotations() {
        return targetWebCall.getAnnotations();
    }
    public MappingInfo getMappingInfo() {
        return targetWebCall.getMappingInfo();
    }
    public ModelController getTarget() {
        return targetWebCall.getTarget();
    }
    public HttpServletRequest getHttpRequest() {
        return targetWebCall.getHttpRequest();
    }
    public HttpServletResponse getHttpResponse() {
        return targetWebCall.getHttpResponse();
    }
    public Object call() throws Throwable {
        this.index++;
        if (this.index < this.webCallInterceptor.length) {
            return this.webCallInterceptor[this.index].exeCall(this);
        } else {
            return this.targetWebCall.call();
        }
    }
    public Object[] getArgs() {
        return this.targetWebCall.getArgs();
    }
}