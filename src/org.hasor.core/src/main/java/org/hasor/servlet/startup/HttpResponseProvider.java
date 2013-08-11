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
package org.hasor.servlet.startup;
import javax.servlet.http.HttpServletResponse;
import com.google.inject.Provider;
/**
 *  
 * @version : 2013-8-11
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class HttpResponseProvider implements Provider<HttpServletResponse> {
    private static HttpResponseProvider      responseProvider = null;
    private ThreadLocal<HttpServletResponse> response         = new ThreadLocal<HttpServletResponse>();
    //
    public static HttpResponseProvider getProvider() {
        if (responseProvider == null)
            responseProvider = new HttpResponseProvider();
        return responseProvider;
    }
    @Override
    public synchronized HttpServletResponse get() {
        return this.response.get();
    }
    public synchronized void update(HttpServletResponse httpRes) {
        if (this.response.get() != null)
            this.reset();
        this.response.set(httpRes);
    }
    public synchronized void reset() {
        this.response.remove();
    }
}