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
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.Provider;
import net.hasor.core.Scope;
import net.hasor.core.binder.SingleProvider;
/**
 * 
 * @version : 2015年7月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class RequestScope implements Scope, Filter, EventListener {
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
    private ThreadLocal<HttpServletRequest> httpRequest = new ThreadLocal<HttpServletRequest>();
    private AppContext                      appContext;
    public void onEvent(String event, Object[] params) throws Throwable {
        AppContext appContext = (AppContext) params[0];
        this.appContext = appContext;
    }
    public <T> Provider<T> scope(Object key, Provider<T> provider) {
        key = (key == null) ? "RequestScope#" : key;
        HttpServletRequest request = httpRequest.get();
        if (request != null) {
            String keyAttr = "RequestScope#" + key.toString();
            Object cacheProvider = request.getAttribute(keyAttr);
            if (cacheProvider == null) {
                provider = new SingleProvider<T>(provider);
                request.setAttribute(keyAttr, provider);
            } else {
                provider = (Provider<T>) cacheProvider;
            }
        }
        return provider;
    }
}