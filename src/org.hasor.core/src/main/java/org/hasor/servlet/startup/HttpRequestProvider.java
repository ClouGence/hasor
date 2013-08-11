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
import javax.servlet.http.HttpServletRequest;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-8-11 
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class HttpRequestProvider implements Provider<HttpServletRequest> {
    private static HttpRequestProvider      requestProvider = null;
    private ThreadLocal<HttpServletRequest> request         = new ThreadLocal<HttpServletRequest>();
    //
    public static HttpRequestProvider getProvider() {
        if (requestProvider == null)
            requestProvider = new HttpRequestProvider();
        return requestProvider;
    }
    @Override
    public synchronized HttpServletRequest get() {
        return this.request.get();
    }
    public synchronized void update(HttpServletRequest httpReq) {
        if (this.request.get() != null)
            this.reset();
        this.request.set(httpReq);
    }
    public synchronized void reset() {
        this.request.remove();
    }
}