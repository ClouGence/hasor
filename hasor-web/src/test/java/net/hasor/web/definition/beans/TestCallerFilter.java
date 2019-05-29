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
package net.hasor.web.definition.beans;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerConfig;
import net.hasor.web.InvokerFilter;

import javax.servlet.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
//
public class TestCallerFilter implements Filter, InvokerFilter {
    private static AtomicBoolean initCall    = new AtomicBoolean(false);
    private static AtomicBoolean doCall      = new AtomicBoolean(false);
    private static AtomicBoolean destroyCall = new AtomicBoolean(false);
    //
    public static boolean isInitCall() {
        return initCall.get();
    }
    public static boolean isDoCall() {
        return doCall.get();
    }
    public static boolean isDestroyCall() {
        return destroyCall.get();
    }
    public static void resetCalls() {
        initCall.set(false);
        doCall.set(false);
        destroyCall.set(false);
    }
    //
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        initCall.set(true);
    }
    @Override
    public void init(InvokerConfig config) throws Throwable {
        initCall.set(true);
    }
    @Override
    public Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        doCall.set(true);
        return null;
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doCall.set(true);
    }
    @Override
    public void destroy() {
        destroyCall.set(true);
    }
}
