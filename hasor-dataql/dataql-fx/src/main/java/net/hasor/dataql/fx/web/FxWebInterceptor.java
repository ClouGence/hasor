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
package net.hasor.dataql.fx.web;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerFilter;
import net.hasor.web.invoker.HttpParameters;

import javax.inject.Singleton;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * Web相关的工具，例如在 DataQL 中操做 Request/Response。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
@Singleton
public class FxWebInterceptor implements InvokerFilter {
    private static final ThreadLocal<Invoker> invokerThreadLocal = new ThreadLocal<>();

    @Override
    public Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        try {
            invokerThreadLocal.set(invoker);
            return chain.doNext(invoker);
        } finally {
            invokerThreadLocal.remove();
        }
    }

    public static Invoker invoker() {
        return invokerThreadLocal.get();
    }

    /** headerMap */
    public static Map<String, String> headerMap() {
        return HttpParameters.headerMap();
    }

    /** headerMap,Value是数组 */
    public static Map<String, List<String>> headerArrayMap() {
        return HttpParameters.headerArrayMap();
    }

    /** cookieMap */
    public static Map<String, String> cookieMap() {
        return HttpParameters.cookieMap();
    }

    /** cookieMap,Value是数组 */
    public static Map<String, List<String>> cookieArrayMap() {
        return HttpParameters.cookieArrayMap();
    }

    /** session */
    public static HttpSession servletSession() {
        Invoker invoker = invoker();
        if (invoker == null) {
            return null;
        }
        return invoker.getHttpRequest().getSession();
    }
}