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
package net.hasor.mvc;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
public class WebCallWrap implements WebCall {
    private WebCall call;
    public WebCallWrap(WebCall call) {
        this.call = call;
    }
    @Override
    public Method getMethod() {
        return this.call.getMethod();
    }
    @Override
    public Class<?>[] getParameterTypes() {
        return this.call.getParameterTypes();
    }
    @Override
    public Annotation[][] getMethodParamAnnos() {
        return this.call.getMethodParamAnnos();
    }
    @Override
    public Annotation[] getAnnotations() {
        return this.call.getAnnotations();
    }
    @Override
    public MappingInfo getMappingInfo() {
        return this.call.getMappingInfo();
    }
    @Override
    public ModelController getTarget() {
        return this.call.getTarget();
    }
    @Override
    public Object call(Object[] args) throws Throwable {
        return this.call.call(args);
    }
    @Override
    public HttpServletRequest getHttpRequest() {
        return this.call.getHttpRequest();
    }
    @Override
    public HttpServletResponse getHttpResponse() {
        return this.call.getHttpResponse();
    }
}