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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 工具。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
public class RequestUtils {
    public static Map<String, List<String>> headerMap(Invoker invoker) {
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

    public static Map<String, List<String>> cookieMap(Invoker invoker) {
        Map<String, List<String>> cookieMap = new HashMap<>();
        HttpServletRequest httpRequest = invoker.getHttpRequest();
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
}
