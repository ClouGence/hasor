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
package net.hasor.web.invoker;
import net.hasor.utils.StringUtils;
import net.hasor.utils.function.ESupplier;
import net.hasor.web.Invoker;
import net.hasor.web.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Http 参数解析，对参数的操作不会影响到 request 和 response
 * @version : 2020-06-28
 * @author 赵永春 (zyc@hasor.net)
 */
public final class HttpParameters {
    private static final Logger                                 logger            = LoggerFactory.getLogger(InvokerCaller.class);
    private static final ThreadLocal<Map<String, List<String>>> headerParamLocal  = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, List<String>>> cookieParamLocal  = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, List<String>>> pathParamLocal    = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, List<String>>> queryParamLocal   = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, List<String>>> requestParamLocal = new ThreadLocal<>();

    public static <T> T executeWorker(HttpServletRequest httpRequest, ESupplier<T, Throwable> worker) throws Throwable {
        return executeWorker(null, httpRequest, worker);
    }

    protected static <T> T executeWorker(Invoker invoker, ESupplier<T, Throwable> worker) throws Throwable {
        return executeWorker(invoker, invoker.getHttpRequest(), worker);
    }

    private static <T> T executeWorker(Invoker invoker, HttpServletRequest httpRequest, ESupplier<T, Throwable> worker) throws Throwable {
        try {
            init(invoker, httpRequest);
            return worker.eGet();
        } finally {
            headerParamLocal.remove();
            cookieParamLocal.remove();
            pathParamLocal.remove();
            queryParamLocal.remove();
        }
    }

    private static void init(Invoker invoker, HttpServletRequest httpRequest) {
        //
        // cookie
        Map<String, List<String>> cookieMap = new HashMap<>();
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                List<String> cookieValue = cookieMap.computeIfAbsent(cookieName, key -> {
                    return new ArrayList<>();
                });
                cookieValue.add(cookie.getValue());
            }
        }
        cookieParamLocal.set(cookieMap);
        //
        // header
        Map<String, List<String>> headerMap = new HashMap<>();
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String header = headerNames.nextElement();
                Enumeration<String> headers = httpRequest.getHeaders(header);
                if (headers == null) {
                    continue;
                }
                List<String> headerValue = new ArrayList<>();
                while (headers.hasMoreElements()) {
                    headerValue.add(headers.nextElement());
                }
                headerMap.put(header, headerValue);
            }
        }
        headerParamLocal.set(headerMap);
        //
        // query
        String queryString = httpRequest.getQueryString();
        Map<String, List<String>> queryMap = new HashMap<>();
        if (StringUtils.isNotBlank(queryString)) {
            String[] params = queryString.split("&");
            for (String pData : params) {
                String encoding = httpRequest.getCharacterEncoding();
                String[] kv = pData.split("=");
                if (kv.length < 2) {
                    continue;
                }
                String k = kv[0].trim();
                String v = kv[1];
                if (StringUtils.isNotBlank(encoding)) {
                    k = urlDecoder(encoding, k);
                    v = urlDecoder(encoding, v);
                }
                //
                List<String> pArray = queryMap.get(k);
                pArray = pArray == null ? new ArrayList<>() : pArray;
                if (!pArray.contains(v)) {
                    pArray.add(v);
                }
                queryMap.put(k, pArray);
            }
        }
        queryParamLocal.set(queryMap);
        //
        // path
        Map<String, List<String>> pathMap = new HashMap<>();
        if (invoker != null) {
            Mapping ownerMapping = invoker.ownerMapping();
            if (ownerMapping != null) {
                String requestPath = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
                String matchVar = ownerMapping.getMappingToMatches();
                String matchKey = "(?:\\{(\\w+)\\}){1,}";//  (?:\{(\w+)\}){1,}
                Matcher keyM = Pattern.compile(matchKey).matcher(ownerMapping.getMappingTo());
                Matcher varM = Pattern.compile(matchVar).matcher(requestPath);
                ArrayList<String> keyArray = new ArrayList<>();
                ArrayList<String> varArray = new ArrayList<>();
                while (keyM.find()) {
                    keyArray.add(keyM.group(1));
                }
                varM.find();
                for (int i = 1; i <= varM.groupCount(); i++) {
                    varArray.add(varM.group(i));
                }
                //
                for (int i = 0; i < keyArray.size(); i++) {
                    String k = keyArray.get(i);
                    String v = varArray.get(i);
                    List<String> pArray = pathMap.get(k);
                    pArray = pArray == null ? new ArrayList<>() : pArray;
                    if (!pArray.contains(v)) {
                        pArray.add(v);
                    }
                    pathMap.put(k, pArray);
                }
            }
        }
        pathParamLocal.set(pathMap);
        //
        // request
        Map<String, List<String>> requestMap = new HashMap<>();
        Map<String, String[]> parameterMap = httpRequest.getParameterMap();
        if (parameterMap != null) {
            parameterMap.forEach((key, value) -> {
                requestMap.put(key, Arrays.asList(value));
            });
        }
        requestParamLocal.set(requestMap);
    }

    private static String urlDecoder(String encoding, String oriData) {
        try {
            if (StringUtils.isNotBlank(oriData)) {
                encoding = URLDecoder.decode(oriData, encoding);
            }
            return encoding;
        } catch (Exception e) {
            logger.warn("use '{}' decode '{}' error.", encoding, oriData);
            return encoding;
        }
    }

    private static Map<String, String> mapList2Map(Map<String, List<String>> mapList) {
        if (mapList == null) {
            return Collections.emptyMap();
        }
        Set<String> headerNames = mapList.keySet();
        Map<String, String> headerMap = new HashMap<>();
        for (String headerName : headerNames) {
            List<String> stringList = mapList.get(headerName);
            if (!stringList.isEmpty()) {
                headerMap.put(headerName, stringList.get(stringList.size() - 1));
            }
        }
        return headerMap;
    }

    /** 清空并合并， */
    private static boolean clearReplaceMap(Map<String, List<String>> target, Map<String, List<String>> newData) {
        if (newData == null) {
            return false;
        }
        target.clear();
        target.putAll(newData);
        return true;
    }

    /** 替换并合并， */
    private static boolean mergeReplaceMap(Map<String, List<String>> target, Map<String, List<String>> newData) {
        if (newData == null) {
            return false;
        }
        target.putAll(newData);
        return true;
    }

    /** 追加合并， */
    private static boolean appendMap(Map<String, List<String>> target, Map<String, List<String>> newData) {
        if (newData == null) {
            return false;
        }
        Map<String, List<String>> listMap = target;
        newData.forEach((key, val) -> {
            List<String> merge = listMap.merge(key, val, (first, second) -> {
                HashSet<String> hashSet = new HashSet<>(first);
                hashSet.addAll(second);
                return Arrays.asList(hashSet.toArray(new String[0]));
            });
        });
        target.putAll(newData);
        return true;
    }
    // ---------------------------------------------------------------------------

    /** 获取 cookie ，数据是 Map 形式 */
    public static Map<String, String> cookieMap() {
        return mapList2Map(cookieParamLocal.get());
    }

    /** 获取 cookie ，Map 的 Value 是数组 */
    public static Map<String, List<String>> cookieArrayMap() {
        return cookieParamLocal.get();
    }

    /** 清空并替换 cookie */
    public static boolean clearReplaceCookieArrayMap(Map<String, List<String>> newCookie) {
        return clearReplaceMap(cookieParamLocal.get(), newCookie);
    }

    /** 将 newCookie 合并到 cookie 中，遇到冲突 key 用新的进行替换 */
    public static boolean mergeReplaceCookieArrayMap(Map<String, List<String>> newCookie) {
        return mergeReplaceMap(cookieParamLocal.get(), newCookie);
    }

    /** 将 newCookie 合并到 cookie 中，遇到冲突 key 合并它们 */
    public static boolean appendCookieArrayMap(Map<String, List<String>> newCookie) {
        return appendMap(cookieParamLocal.get(), newCookie);
    }
    // ---------------------------------------------------------------------------

    /** 获取 header ，数据是 Map 形式 */
    public static Map<String, String> headerMap() {
        return mapList2Map(headerParamLocal.get());
    }

    /** 获取 header ，Map 的 Value 是数组 */
    public static Map<String, List<String>> headerArrayMap() {
        return headerParamLocal.get();
    }

    /** 清空并替换 newHeader */
    public static boolean clearReplaceHeaderArrayMap(Map<String, List<String>> newHeader) {
        return clearReplaceMap(headerParamLocal.get(), newHeader);
    }

    /** 将 newHeader 合并到 header 中，遇到冲突 key 用新的进行替换 */
    public static boolean mergeReplaceHeaderArrayMap(Map<String, List<String>> newHeader) {
        return mergeReplaceMap(headerParamLocal.get(), newHeader);
    }

    /** 将 newHeader 合并到 header 中，遇到冲突 key 合并它们 */
    public static boolean appendHeaderArrayMap(Map<String, List<String>> newHeader) {
        return appendMap(headerParamLocal.get(), newHeader);
    }
    // ---------------------------------------------------------------------------

    /** 获取 URL "?" 后面的查询参数 ，数据是 Map 形式 */
    public static Map<String, String> queryMap() {
        return mapList2Map(queryParamLocal.get());
    }

    /** 获取 URL "?" 后面的查询参数 ，Map 的 Value 是数组 */
    public static Map<String, List<String>> queryArrayMap() {
        return queryParamLocal.get();
    }

    /** 清空并替换 `查询参数` */
    public static boolean clearReplaceQueryArrayMap(Map<String, List<String>> newQuery) {
        return clearReplaceMap(queryParamLocal.get(), newQuery);
    }

    /** 将 newQuery 合并到 `查询参数` 中，遇到冲突 key 用新的进行替换 */
    public static boolean mergeReplaceQueryArrayMap(Map<String, List<String>> newQuery) {
        return mergeReplaceMap(queryParamLocal.get(), newQuery);
    }

    /** 将 newQuery 合并到 `查询参数` 中，遇到冲突 key 合并它们 */
    public static boolean appendQueryArrayMap(Map<String, List<String>> newQuery) {
        return appendMap(queryParamLocal.get(), newQuery);
    }
    // ---------------------------------------------------------------------------

    /** 获取 URL 请求路径上的查询参数 ，数据是 Map 形式 */
    public static Map<String, String> pathMap() {
        return mapList2Map(pathParamLocal.get());
    }

    /** 获取 URL 请求路径上的查询参数 ，Map 的 Value 是数组 */
    public static Map<String, List<String>> pathArrayMap() {
        return pathParamLocal.get();
    }

    /** 清空并替换 `查询参数` */
    public static boolean clearReplacePathArrayMap(Map<String, List<String>> newPath) {
        return clearReplaceMap(pathParamLocal.get(), newPath);
    }

    /** 将 newPath 合并到 `查询参数` 中，遇到冲突 key 用新的进行替换 */
    public static boolean mergeReplacePathArrayMap(Map<String, List<String>> newPath) {
        return mergeReplaceMap(pathParamLocal.get(), newPath);
    }

    /** 将 newPath 合并到 `查询参数` 中，遇到冲突 key 合并它们 */
    public static boolean appendPathArrayMap(Map<String, List<String>> newPath) {
        return appendMap(pathParamLocal.get(), newPath);
    }
    // ---------------------------------------------------------------------------

    /** 获取 Http 标准的请求参数 ，数据是 Map 形式 */
    public static Map<String, String> requestMap() {
        return mapList2Map(requestParamLocal.get());
    }

    /** 获取 Http 标准的请求参数 ，Map 的 Value 是数组 */
    public static Map<String, List<String>> requestArrayMap() {
        return requestParamLocal.get();
    }

    /** 清空并替换 `请求参数` */
    public static boolean clearReplaceRequestArrayMap(Map<String, List<String>> newRequestParam) {
        return clearReplaceMap(requestParamLocal.get(), newRequestParam);
    }

    /** 将 newPath 合并到 `请求参数` 中，遇到冲突 key 用新的进行替换 */
    public static boolean mergeReplaceRequestArrayMap(Map<String, List<String>> newRequestParam) {
        return mergeReplaceMap(requestParamLocal.get(), newRequestParam);
    }

    /** 将 newPath 合并到 `请求参数` 中，遇到冲突 key 合并它们 */
    public static boolean appendRequestArrayMap(Map<String, List<String>> newRequestParam) {
        return appendMap(requestParamLocal.get(), newRequestParam);
    }
}
