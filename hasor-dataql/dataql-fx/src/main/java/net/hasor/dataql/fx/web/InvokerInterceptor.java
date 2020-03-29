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

import javax.inject.Singleton;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Web相关的工具，例如在 DataQL 中操做 Request/Response。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
@Singleton
public class InvokerInterceptor implements InvokerFilter {
    private static ThreadLocal<Invoker> invokerThreadLocal = new ThreadLocal<>();

    @Override
    public Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        try {
            invokerThreadLocal.set(invoker);
            return chain.doNext(invoker);
        } finally {
            invokerThreadLocal.remove();
        }
    }

    protected static Invoker invoker() {
        return invokerThreadLocal.get();
    }

    /** headerMap */
    public static Map<String, String> headerMap() {
        Invoker invoker = invoker();
        if (invoker == null) {
            return Collections.emptyMap();
        }
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        Map<String, String> headerMap = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            headerMap.put(header, httpRequest.getHeader(header));
        }
        return headerMap;
    }

    /** headerMap,Value是数组 */
    public static Map<String, List<String>> headerArrayMap() {
        Invoker invoker = invoker();
        if (invoker == null) {
            return Collections.emptyMap();
        }
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        Map<String, List<String>> headerMap = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            Enumeration<String> headers = httpRequest.getHeaders(header);
            List<String> headerValue = new ArrayList<>();
            while (headers.hasMoreElements()) {
                headerValue.add(headers.nextElement());
            }
            headerMap.put(header, headerValue);
        }
        return headerMap;
    }

    /** cookieMap */
    public static Map<String, String> cookieMap() {
        Invoker invoker = invoker();
        if (invoker == null) {
            return Collections.emptyMap();
        }
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        Map<String, String> cookieMap = new HashMap<>();
        Cookie[] cookies = httpRequest.getCookies();
        for (Cookie cookie : cookies) {
            String cookieName = cookie.getName();
            cookieMap.put(cookieName, cookie.getValue());
        }
        return cookieMap;
    }

    /** cookieMap,Value是数组 */
    public static Map<String, List<String>> cookieArrayMap() {
        Invoker invoker = invoker();
        if (invoker == null) {
            return Collections.emptyMap();
        }
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        Map<String, List<String>> cookieMap = new HashMap<>();
        Cookie[] cookies = httpRequest.getCookies();
        for (Cookie cookie : cookies) {
            String cookieName = cookie.getName();
            List<String> cookieValue = cookieMap.computeIfAbsent(cookieName, key -> {
                return new ArrayList<>();
            });
            cookieValue.add(cookie.getValue());
        }
        return cookieMap;
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