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
import net.hasor.dataql.UdfSourceAssembly;
import net.hasor.utils.StringUtils;

import javax.inject.Singleton;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Session 函数库。函数库引入 <code>import 'net.hasor.dataql.fx.web.SessionUdfSource' as sessionTools;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-29
 */
@Singleton
public class SessionUdfSource implements UdfSourceAssembly {
    /** sessionKeys */
    public static List<String> keys() {
        HttpSession httpSession = InvokerInterceptor.servletSession();
        if (httpSession == null) {
            return Collections.emptyList();
        }
        Enumeration<String> attributeNames = httpSession.getAttributeNames();
        List<String> names = new ArrayList<>();
        while (attributeNames.hasMoreElements()) {
            names.add(attributeNames.nextElement());
        }
        return names;
    }

    /** 获取Session */
    public static Object get(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        HttpSession httpSession = InvokerInterceptor.servletSession();
        if (httpSession == null) {
            return null;
        }
        return httpSession.getAttribute(key);
    }

    /** 设置Session */
    public static Object set(String key, Object newValue) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        HttpSession httpSession = InvokerInterceptor.servletSession();
        if (httpSession == null) {
            httpSession = InvokerInterceptor.invoker().getHttpRequest().getSession(true);
        }
        Object oldValue = httpSession.getAttribute(key);
        httpSession.setAttribute(key, newValue);
        return oldValue;
    }

    /** 删除Session */
    public static boolean remove(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        HttpSession httpSession = InvokerInterceptor.servletSession();
        if (httpSession == null) {
            return false;
        }
        httpSession.removeAttribute(key);
        return true;
    }

    /** 删除所有Key */
    public static boolean clean() {
        HttpSession httpSession = InvokerInterceptor.servletSession();
        if (httpSession == null) {
            return false;
        }
        Enumeration<String> attributeNames = httpSession.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            httpSession.removeAttribute(attributeNames.nextElement());
        }
        return true;
    }

    /** Invalidates this session then unbinds any objects bound to it. */
    public static boolean invalidate() {
        HttpSession httpSession = InvokerInterceptor.servletSession();
        if (httpSession == null) {
            return false;
        }
        httpSession.invalidate();
        return true;
    }

    /** Session ID. */
    public static String id() {
        HttpSession httpSession = InvokerInterceptor.servletSession();
        if (httpSession == null) {
            return null;
        }
        return httpSession.getId();
    }

    /** 返回客户端发送与之关联的请求的最后一次时间. */
    public static long lastAccessedTime() {
        HttpSession httpSession = InvokerInterceptor.servletSession();
        if (httpSession == null) {
            return 0;
        }
        return httpSession.getLastAccessedTime();
    }
}