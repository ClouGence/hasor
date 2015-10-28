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
package net.hasor.mvc.scope;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import net.hasor.core.BindInfo;
import net.hasor.core.Provider;
import net.hasor.core.Scope;
import net.hasor.core.scope.SingleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @version : 2015年7月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class RequestScope implements Scope, Filter {
    public void init(FilterConfig filterConfig) throws ServletException {}
    public void destroy() {}
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (httpRequest.get() != null) {
            httpRequest.remove();
        }
        httpRequest.set((HttpServletRequest) request);
        chain.doFilter(request, response);
        if (httpRequest.get() != null) {
            httpRequest.remove();
        }
    }
    //
    protected Logger                        logger      = LoggerFactory.getLogger(getClass());
    private ThreadLocal<HttpServletRequest> httpRequest = new ThreadLocal<HttpServletRequest>();
    //
    public <T> Provider<T> scope(Object key, Provider<T> provider) {
        BindInfo<?> infoKey = (BindInfo<?>) key;
        String keyStr = "RequestScope#" + infoKey.getBindID();
        HttpServletRequest request = httpRequest.get();
        if (request != null) {
            Object cacheProvider = request.getAttribute(keyStr);
            if (cacheProvider == null) {
                provider = new SingleProvider<T>(provider);
                request.setAttribute(keyStr, provider);
                if (logger.isDebugEnabled()) {
                    logger.debug("request scope, attribute key={},value={}", keyStr, provider);
                }
            } else {
                provider = (Provider<T>) cacheProvider;
            }
        }
        return provider;
    }
}