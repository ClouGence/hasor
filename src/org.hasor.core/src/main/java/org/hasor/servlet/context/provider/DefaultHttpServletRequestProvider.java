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
package org.hasor.servlet.context.provider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-8-12
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class DefaultHttpServletRequestProvider implements Provider<HttpServletRequest> {
    private IocHttpServletRequest iocHttpServletRequest = null;
    //
    @Override
    public HttpServletRequest get() {
        if (this.iocHttpServletRequest == null) {
            this.iocHttpServletRequest = new IocHttpServletRequest(HttpProvider.getProvider().getRequest());
            HttpProvider.getProvider().addHttpServletRequestMessenger(this.iocHttpServletRequest);
        }
        return iocHttpServletRequest;
    }
    /** */
    private static class IocHttpServletRequest extends HttpServletRequestWrapper implements HttpProviderMessenger<HttpServletRequest> {
        public IocHttpServletRequest(HttpServletRequest request) {
            super(request);
        }
        @Override
        public void update(HttpServletRequest newRequest) {
            this.setRequest(newRequest);
        }
    }
}
