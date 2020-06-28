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
 * Http 参数解析
 * @version : 2020-06-28
 * @author 赵永春 (zyc@hasor.net)
 */
public class HttpParameters {
    private static final Logger                                 logger            = LoggerFactory.getLogger(InvokerCaller.class);
    private static final ThreadLocal<Map<String, List<String>>> headerParamLocal  = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, List<String>>> cookieParamLocal  = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, List<String>>> pathParamLocal    = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, List<String>>> queryParamLocal   = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, List<String>>> requestParamLocal = new ThreadLocal<>();

    public static interface PreCaller<T> {
        public T invoke(HttpParameters httpParameters) throws Throwable;
    }

    static <T> T preInvoke(Invoker invoker, PreCaller<T> preCaller) throws Throwable {
        try {
            init(invoker, invoker.getHttpRequest());
            return preCaller.invoke(new HttpParameters());
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

    /** 获取 cookie ，数据是 Map 形式 */
    public static Map<String, String> cookieMap() {
        return mapList2Map(cookieParamLocal.get());
    }

    /** 获取 cookie ，Map 的 Value 是数组 */
    public static Map<String, List<String>> cookieArrayMap() {
        return cookieParamLocal.get();
    }

    /** 获取 header ，数据是 Map 形式 */
    public static Map<String, String> headerMap() {
        return mapList2Map(headerParamLocal.get());
    }

    /** 获取 header ，Map 的 Value 是数组 */
    public static Map<String, List<String>> headerArrayMap() {
        return headerParamLocal.get();
    }

    /** 获取 URL "?" 后面的查询参数 ，数据是 Map 形式 */
    public static Map<String, String> queryMap() {
        return mapList2Map(queryParamLocal.get());
    }

    /** 获取 URL "?" 后面的查询参数 ，Map 的 Value 是数组 */
    public static Map<String, List<String>> queryArrayMap() {
        return queryParamLocal.get();
    }

    /** 获取 URL 请求路径上的查询参数 ，数据是 Map 形式 */
    public static Map<String, String> pathMap() {
        return mapList2Map(pathParamLocal.get());
    }

    /** 获取 URL 请求路径上的查询参数 ，Map 的 Value 是数组 */
    public static Map<String, List<String>> pathArrayMap() {
        return pathParamLocal.get();
    }

    /** 获取 Http 标准的请求参数 ，数据是 Map 形式 */
    public static Map<String, String> requestMap() {
        return mapList2Map(requestParamLocal.get());
    }

    /** 获取 Http 标准的请求参数 ，Map 的 Value 是数组 */
    public static Map<String, List<String>> requestArrayMap() {
        return requestParamLocal.get();
    }
}