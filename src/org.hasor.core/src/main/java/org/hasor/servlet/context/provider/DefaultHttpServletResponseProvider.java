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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-8-12
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class DefaultHttpServletResponseProvider implements Provider<HttpServletResponse> {
    //
    @Override
    public HttpServletResponse get() {
        IocHttpServletResponse iocHttpServletResponse = new IocHttpServletResponse(HttpProvider.getProvider().getResponse());
        HttpProvider.getProvider().addHttpServletResponseMessenger(iocHttpServletResponse);
        return iocHttpServletResponse;
    }
    /** */
    private static class IocHttpServletResponse extends HttpServletResponseWrapper implements HttpProviderMessenger<HttpServletResponse> {
        public IocHttpServletResponse(HttpServletResponse response) {
            super(response);
        }
        @Override
        public void update(HttpServletResponse newResponse) {
            this.setResponse(newResponse);
        }
    }
}