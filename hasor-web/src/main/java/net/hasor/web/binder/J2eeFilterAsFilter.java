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
package net.hasor.web.binder;
import net.hasor.core.Provider;
import net.hasor.utils.ExceptionUtils;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerConfig;
import net.hasor.web.InvokerFilter;

import javax.servlet.*;
import java.io.IOException;
import java.util.function.Supplier;

/**
 * Filter 转换为 InvokerFilter
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class J2eeFilterAsFilter implements InvokerFilter, Filter {
    protected Supplier<? extends Filter> j2eeFilter = null;

    public J2eeFilterAsFilter(Supplier<? extends Filter> j2eeFilter) {
        this.j2eeFilter = Provider.of(j2eeFilter).asSingle();
    }

    public final void init(InvokerConfig config) throws Throwable {
        this.init((FilterConfig) new OneConfig(this.getClass().getName(), config, config::getAppContext));
    }

    @Override
    public final Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        this.doFilter(invoker.getHttpRequest(), invoker.getHttpResponse(), (request, response) -> {
            try {
                chain.doNext(invoker);
            } catch (IOException e) {
                throw (IOException) e;
            } catch (ServletException e) {
                throw (ServletException) e;
            } catch (Throwable e) {
                throw ExceptionUtils.toRuntimeException(e);
            }
        });
        return invoker.get(Invoker.RETURN_DATA_KEY);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.j2eeFilter.get().init(filterConfig);
    }

    @Override
    public final void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        this.j2eeFilter.get().doFilter(servletRequest, servletResponse, filterChain);
    }

    @Override
    public void destroy() {
        this.j2eeFilter.get().destroy();
    }
}