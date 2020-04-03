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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Cookie 函数库。函数库引入 <code>import 'net.hasor.dataql.fx.web.CookieUdfSource' as cookieTools;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-29
 */
@Singleton
public class CookieUdfSource implements UdfSourceAssembly {
    /** cookieMap */
    public static Map<String, String> map() {
        return FxWebInterceptor.cookieMap();
    }

    /** cookieMap,Value是数组 */
    public static Map<String, List<String>> arrayMap() {
        return FxWebInterceptor.cookieArrayMap();
    }

    /** 获取Cookie */
    public static String get(String cookieName) {
        if (StringUtils.isBlank(cookieName)) {
            return null;
        }
        return map().get(cookieName);
    }

    /** 获取所有名字相同的 Cookie */
    public static List<String> getArray(String cookieName) {
        if (StringUtils.isBlank(cookieName)) {
            return null;
        }
        return arrayMap().get(cookieName);
    }

    /** 设置 Cookie,MaxAge = -1 */
    public static boolean add(String cookieName, String value) {
        HttpServletResponse httpResponse = FxWebInterceptor.invoker().getHttpResponse();
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setMaxAge(-1);
        httpResponse.addCookie(cookie);
        return true;
    }

    /** 批量设置 Cookie */
    public static boolean addAll(Map<String, String> cookieMap) {
        if (cookieMap != null) {
            cookieMap.forEach(CookieUdfSource::add);
            return true;
        }
        return false;
    }

    /** 存储 Cookie */
    public static boolean store(String cookieName, String value, int maxAge) {
        HttpServletResponse httpResponse = FxWebInterceptor.invoker().getHttpResponse();
        Cookie cookie = new Cookie(cookieName, value);
        if (maxAge <= 0) {
            maxAge = -1;
        }
        cookie.setMaxAge(maxAge);
        httpResponse.addCookie(cookie);
        return true;
    }

    /** 批量设置 Cookie */
    public static boolean storeAll(Map<String, String> cookieMap, int maxAge) {
        if (cookieMap != null) {
            cookieMap.forEach((cookieName, value) -> {
                store(cookieName, value, maxAge);
            });
            return true;
        }
        return false;
    }

    /** 删除 Cookie */
    public static boolean remove(String cookieName) {
        HttpServletResponse httpResponse = FxWebInterceptor.invoker().getHttpResponse();
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0);
        httpResponse.addCookie(cookie);
        return true;
    }
}