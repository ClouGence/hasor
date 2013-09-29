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
package net.hasor.web.restful.support;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hasor.Hasor;
import net.hasor.core.AppContext;
import net.hasor.web.restful.AttributeParam;
import net.hasor.web.restful.CookieParam;
import net.hasor.web.restful.HeaderParam;
import net.hasor.web.restful.HttpMethod;
import net.hasor.web.restful.Path;
import net.hasor.web.restful.PathParam;
import net.hasor.web.restful.Produces;
import net.hasor.web.restful.QueryParam;
import org.more.UndefinedException;
import org.more.classcode.FormatException;
import org.more.convert.ConverterUtils;
import org.more.util.BeanUtils;
import org.more.util.StringUtils;
/**
 * 线程安全
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
class RestfulInvoke {
    private String[]                         httpMethod;
    private String                           restfulMapping;
    private String                           restfulMappingMatches;
    private String                           produces;
    private Method                           targetMethod;
    private Class<?>                         targetClass;
    private AppContext                       appContext;
    private ThreadLocal<HttpServletRequest>  requestLocal;
    private ThreadLocal<HttpServletResponse> responseLocal;
    private ThreadLocal<Object>              localObject;
    //
    public RestfulInvoke(AppContext appContext, Method targetMethod) {
        Path pathAnno = targetMethod.getAnnotation(Path.class);
        if (pathAnno == null)
            throw new UndefinedException("is not a valid Restful Service.");
        String servicePath = pathAnno.value();
        if (StringUtils.isBlank(servicePath))
            throw new NullPointerException("Service path is empty.");
        if (!servicePath.matches("/.+"))
            throw new FormatException("Service path format error");
        /*HttpMethod*/
        Annotation[] annos = targetMethod.getAnnotations();
        ArrayList<String> allHttpMethod = new ArrayList<String>();
        if (annos != null) {
            for (Annotation anno : annos) {
                HttpMethod httpMethodAnno = anno.annotationType().getAnnotation(HttpMethod.class);
                if (httpMethodAnno != null) {
                    String bindMethod = httpMethodAnno.value();
                    if (StringUtils.isBlank(bindMethod) == false)
                        allHttpMethod.add(bindMethod);
                }
            }
        }
        if (allHttpMethod.isEmpty())
            allHttpMethod.add("ANY");
        this.httpMethod = allHttpMethod.toArray(new String[allHttpMethod.size()]);
        //
        Produces produces = targetMethod.getAnnotation(Produces.class);
        if (produces != null)
            this.produces = produces.value();
        //
        this.restfulMapping = servicePath;
        this.targetMethod = targetMethod;
        this.targetClass = targetMethod.getDeclaringClass();
        this.appContext = appContext;
        this.requestLocal = new ThreadLocal<HttpServletRequest>();
        this.responseLocal = new ThreadLocal<HttpServletResponse>();
        this.localObject = new ThreadLocal<Object>();
    }
    //
    //
    /**获取AppContext*/
    public AppContext getAppContext() {
        return this.appContext;
    }
    /**获取调用的目标对象*/
    public Object getTargetObject() {
        Object targetObject = this.localObject.get();
        if (targetObject != null)
            return targetObject;
        targetObject = this.appContext.getInstance(this.targetClass);
        this.localObject.set(targetObject);
        return targetObject;
    }
    /**获取目标方法*/
    public Method getTargetMethod() {
        return this.targetMethod;
    }
    /**获取目标类*/
    public Class<?> getTargetClass() {
        return targetClass;
    }
    /**获取request*/
    public HttpServletRequest getRequest() {
        return this.requestLocal.get();
    }
    /**获取response*/
    public HttpServletResponse getResponse() {
        return this.responseLocal.get();
    }
    /**获取映射字符串*/
    public String getRestfulMapping() {
        return this.restfulMapping;
    }
    /**获取映射字符串用于匹配的表达式字符串*/
    public String getRestfulMappingMatches() {
        if (this.restfulMappingMatches == null)
            this.restfulMappingMatches = this.restfulMapping.replaceAll("\\{\\w{1,}\\}", "([^/]{1,})");
        return this.restfulMappingMatches;
    }
    //
    /**判断Restful实例是否支持这个 请求方法。*/
    public boolean matchingMethod(String httpMethod) {
        for (String m : this.httpMethod)
            if (StringUtils.equalsIgnoreCase(httpMethod, m))
                return true;
            else if (StringUtils.equalsIgnoreCase(m, "ANY"))
                return true;
        return false;
    }
    /**执行调用*/
    public Object invoke(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws Throwable {
        Method targetMethod = this.getTargetMethod();
        Class<?>[] targetParamClass = targetMethod.getParameterTypes();
        Annotation[][] targetParamAnno = targetMethod.getParameterAnnotations();
        targetParamClass = (targetParamClass == null) ? new Class<?>[0] : targetParamClass;
        targetParamAnno = (targetParamAnno == null) ? new Annotation[0][0] : targetParamAnno;
        ArrayList<Object> paramsArray = new ArrayList<Object>();
        /*准备参数*/
        for (int i = 0; i < targetParamClass.length; i++) {
            Class<?> paramClass = targetParamClass[i];
            Object paramObject = this.getIvnokeParams(paramClass, targetParamAnno[i]);//获取参数
            /*获取到的参数需要做一个类型转换，以防止method.invoke时发生异常。*/
            if (paramObject == null)
                paramObject = BeanUtils.getDefaultValue(paramClass);
            else
                paramObject = ConverterUtils.convert(paramClass, paramObject);
            paramsArray.add(paramObject);
        }
        Object[] invokeParams = paramsArray.toArray();
        /*执行调用*/
        try {
            this.requestLocal.set(servletRequest);
            this.responseLocal.set(servletResponse);
            servletResponse.setContentType(this.produces);
            return this.call(targetMethod, invokeParams);
        } finally {
            this.requestLocal.remove();
            this.responseLocal.remove();
        }
    }
    /**执行调用，并引发事件*/
    private Object call(Method targetMethod, Object[] invokeParams) throws Throwable {
        Object targetObject = this.getTargetObject();
        return targetMethod.invoke(targetObject, invokeParams);
    }
    /**获得参数项*/
    private Object getIvnokeParams(Class<?> paramClass, Annotation[] paramAnno) {
        for (Annotation pAnno : paramAnno) {
            if (pAnno instanceof AttributeParam)
                return this.getAttributeParam(paramClass, (AttributeParam) pAnno);
            else if (pAnno instanceof CookieParam)
                return this.getCookieParam(paramClass, (CookieParam) pAnno);
            else if (pAnno instanceof HeaderParam)
                return this.getHeaderParam(paramClass, (HeaderParam) pAnno);
            else if (pAnno instanceof QueryParam)
                return this.getQueryParam(paramClass, (QueryParam) pAnno);
            else if (pAnno instanceof PathParam)
                return this.getPathParam(paramClass, (PathParam) pAnno);
        }
        return BeanUtils.getDefaultValue(paramClass);
    }
    //
    /**/
    private Object getPathParam(Class<?> paramClass, PathParam pAnno) {
        String paramName = pAnno.value();
        return StringUtils.isBlank(paramName) ? null : this.getPathParamMap().get(paramName.toUpperCase());
    }
    /**/
    private Object getQueryParam(Class<?> paramClass, QueryParam pAnno) {
        String paramName = pAnno.value();
        return StringUtils.isBlank(paramName) ? null : this.getQueryParamMap().get(paramName.toUpperCase());
    }
    /**/
    private Object getHeaderParam(Class<?> paramClass, HeaderParam pAnno) {
        String paramName = pAnno.value();
        if (StringUtils.isBlank(paramName))
            return null;
        //
        HttpServletRequest httpRequest = this.getRequest();
        Enumeration<?> e = httpRequest.getHeaderNames();
        while (e.hasMoreElements()) {
            String name = e.nextElement().toString();
            if (StringUtils.equalsIgnoreCase(name, paramName)) {
                ArrayList<Object> headerList = new ArrayList<Object>();
                Enumeration<?> v = httpRequest.getHeaders(paramName);
                while (v.hasMoreElements())
                    headerList.add(v.nextElement());
                return headerList;
            }
        }
        return null;
    }
    /**/
    private Object getCookieParam(Class<?> paramClass, CookieParam pAnno) {
        String paramName = pAnno.value();
        if (StringUtils.isBlank(paramName))
            return null;
        //
        HttpServletRequest httpRequest = this.getRequest();
        Cookie[] cookies = httpRequest.getCookies();
        ArrayList<String> cookieList = new ArrayList<String>();
        if (cookies != null)
            for (Cookie cookie : cookies)
                if (StringUtils.equalsIgnoreCase(cookie.getName(), paramName))
                    cookieList.add(cookie.getValue());
        return cookieList;
    }
    /**/
    private Object getAttributeParam(Class<?> paramClass, AttributeParam pAnno) {
        String paramName = pAnno.value();
        if (StringUtils.isBlank(paramName))
            return null;
        HttpServletRequest httpRequest = this.getRequest();
        Enumeration<?> e = httpRequest.getAttributeNames();
        while (e.hasMoreElements()) {
            String name = e.nextElement().toString();
            if (StringUtils.equalsIgnoreCase(name, paramName))
                return httpRequest.getAttribute(paramName);
        }
        return null;
    }
    /**/
    private ThreadLocal<Map<String, List<String>>> queryParamLocal = new ThreadLocal<Map<String, List<String>>>();
    private Map<String, List<String>> getQueryParamMap() {
        Map<String, List<String>> queryParam = this.queryParamLocal.get();
        if (queryParam != null)
            return queryParam;
        //
        HttpServletRequest httpRequest = getRequest();
        String queryString = httpRequest.getQueryString();
        if (StringUtils.isBlank(queryString))
            return null;
        //
        Map<String, List<String>> uriParams = new HashMap<String, List<String>>();
        String[] params = queryString.split("&");
        for (String pData : params) {
            String oriData = null;
            String encoding = httpRequest.getCharacterEncoding();
            try {
                oriData = URLDecoder.decode(pData, encoding);
            } catch (Exception e) {
                Hasor.warning("use ‘%s’ decode ‘%s’ error.", encoding, pData);
                continue;
            }
            String[] kv = oriData.split("=");
            if (kv.length < 2)
                continue;
            String k = kv[0].trim().toUpperCase();
            String v = kv[1];
            //
            List<String> pArray = uriParams.get(k);
            pArray = pArray == null ? new ArrayList<String>() : pArray;
            if (pArray.contains(v) == false)
                pArray.add(v);
            uriParams.put(k, pArray);
        }
        this.queryParamLocal.set(queryParam);
        return queryParam;
    }
    /**/
    private ThreadLocal<Map<String, Object>> pathParamsLocal = new ThreadLocal<Map<String, Object>>();
    private Map<String, Object> getPathParamMap() {
        Map<String, Object> pathParams = pathParamsLocal.get();
        if (pathParams != null)
            return pathParams;
        //
        HttpServletRequest httpRequest = getRequest();
        String requestPath = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        String matchVar = this.restfulMappingMatches;
        String matchKey = "(?:\\{(\\w+)\\}){1,}";//  (?:\{(\w+)\}){1,}
        Matcher keyM = Pattern.compile(matchKey).matcher(this.restfulMapping);
        Matcher varM = Pattern.compile(matchVar).matcher(requestPath);
        ArrayList<String> keyArray = new ArrayList<String>();
        ArrayList<String> varArray = new ArrayList<String>();
        while (keyM.find())
            keyArray.add(keyM.group(1));
        varM.find();
        for (int i = 1; i <= varM.groupCount(); i++)
            varArray.add(varM.group(i));
        //
        Map<String, List<String>> uriParams = new HashMap<String, List<String>>();
        for (int i = 0; i < keyArray.size(); i++) {
            String k = keyArray.get(i);
            String v = varArray.get(i);
            List<String> pArray = uriParams.get(k);
            pArray = pArray == null ? new ArrayList<String>() : pArray;
            if (pArray.contains(v) == false)
                pArray.add(v);
            uriParams.put(k, pArray);
        }
        pathParams = new HashMap<String, Object>();
        //        pathParams.putAll(request.getParameterMap());
        for (Entry<String, List<String>> ent : uriParams.entrySet()) {
            String k = ent.getKey();
            List<String> v = ent.getValue();
            pathParams.put(k, v.toArray(new String[v.size()]));
        }
        this.pathParamsLocal.set(pathParams);
        return pathParams;
    }
}