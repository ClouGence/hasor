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
import net.hasor.utils.BeanUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.convert.ConverterUtils;
import net.hasor.utils.future.BasicFuture;
import net.hasor.web.*;
import net.hasor.web.annotation.*;
import net.hasor.web.definition.AbstractDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 负责解析参数并执行调用。
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
class InvokerCaller implements ExceuteCaller {
    protected Logger                    logger          = LoggerFactory.getLogger(getClass());
    private   InMapping                 mappingToDefine = null;
    private   AbstractDefinition[]      filterArrays    = null;
    private   WebPluginCaller           pluginCaller    = null;
    private   Map<String, List<String>> queryParamLocal = null;
    private   Map<String, Object>       pathParamsLocal = null;
    //
    public InvokerCaller(InMapping mappingToDefine, AbstractDefinition[] filterArrays, WebPluginCaller pluginCaller) {
        this.mappingToDefine = mappingToDefine;
        this.filterArrays = (filterArrays == null) ? new AbstractDefinition[0] : filterArrays;
        this.pluginCaller = pluginCaller;
    }
    /**
     * 调用目标
     * @throws Throwable 异常抛出
     */
    public Future<Object> invoke(final Invoker invoker, final FilterChain chain) throws Throwable {
        final BasicFuture<Object> future = new BasicFuture<Object>();
        Method targetMethod = this.mappingToDefine.findMethod(invoker);
        if (targetMethod == null) {
            chain.doFilter(invoker.getHttpRequest(), invoker.getHttpResponse());
            future.completed(null);
            return future;
        }
        //
        // .异步调用
        boolean needAsync = this.mappingToDefine.isAsync(invoker);
        ServletVersion version = invoker.getAppContext().getInstance(ServletVersion.class);
        if (version.ge(ServletVersion.V3_0) && needAsync) {
            // .必须满足: Servlet3.x、环境支持异步Servlet、目标开启了Servlet3
            try {
                AsyncContext asyncContext = invoker.getHttpRequest().startAsync();
                asyncContext.start(new AsyncInvocationWorker(asyncContext, targetMethod) {
                    public void doWork(Method targetMethod) throws Throwable {
                        try {
                            Object invoke = invoke(targetMethod, invoker);
                            future.completed(invoke);
                        } catch (Throwable e) {
                            future.failed(e);
                        }
                    }
                });
                return future;
            } catch (Throwable e) { /* 不支持异步 */ }
        }
        //
        // .同步调用
        try {
            Object invoke = invoke(targetMethod, invoker);
            future.completed(invoke);
        } catch (Throwable e) {
            future.failed(e);
        }
        return future;
    }
    /** 执行调用 */
    private Object invoke(final Method targetMethod, Invoker invoker) throws Throwable {
        //
        // .初始化WebController
        final Object targetObject = this.mappingToDefine.newInstance(invoker);
        if (targetObject != null && targetObject instanceof Controller) {
            ((Controller) targetObject).initController(invoker);
        }
        //
        // .准备过滤器链
        final ArrayList<Object[]> resolveParams = new ArrayList<Object[]>(1);
        InvokerChain invokerChain = new InvokerChain() {
            @Override
            public void doNext(Invoker invoker) throws Throwable {
                Object result = targetMethod.invoke(targetObject, resolveParams.get(0));
                invoker.put(Invoker.RETURN_DATA_KEY, result);
            }
        };
        InvokerData invokerData = new InvokerData() {
            @Override
            public Method targetMethod() {
                return targetMethod;
            }
            @Override
            public Object[] getParameters() {
                return resolveParams.isEmpty() ? new Object[0] : resolveParams.get(0);
            }
            @Override
            public MappingData getMappingTo() {
                return mappingToDefine;
            }
        };
        //
        // .执行Filters
        try {
            final Object[] resolveParamsArrays = this.resolveParams(invoker, targetMethod);
            resolveParams.add(0, resolveParamsArrays);
            //
            this.pluginCaller.beforeFilter(invoker, invokerData);
            new InvokerChainInvocation(this.filterArrays, invokerChain).doNext(invoker);
        } finally {
            this.pluginCaller.afterFilter(invoker, invokerData);
        }
        //
        return invoker.get(Invoker.RETURN_DATA_KEY);
    }
    //
    /**/
    private Object[] resolveParams(Invoker invoker, Method targetMethod) throws Throwable {
        //
        Class<?>[] targetParamClass = targetMethod.getParameterTypes();
        Annotation[][] targetParamAnno = targetMethod.getParameterAnnotations();
        targetParamClass = (targetParamClass == null) ? new Class<?>[0] : targetParamClass;
        targetParamAnno = (targetParamAnno == null) ? new Annotation[0][0] : targetParamAnno;
        ArrayList<Object> paramsArray = new ArrayList<Object>();
        /*准备参数*/
        for (int i = 0; i < targetParamClass.length; i++) {
            Class<?> paramClass = targetParamClass[i];
            Object paramObject = this.resolveParam(invoker, paramClass, targetParamAnno[i]);//获取参数
            paramsArray.add(paramObject);
        }
        Object[] invokeParams = paramsArray.toArray();
        return invokeParams;
    }
    private Object resolveParam(Invoker invoker, Class<?> paramClass, Annotation[] paramAnno) {
        // .特殊类型参数
        Object specialParam = resolveSpecialParam(invoker, paramClass);
        if (specialParam != null) {
            return specialParam;
        }
        // .注解解析
        for (Annotation pAnno : paramAnno) {
            Object finalValue = resolveParam(invoker, paramClass, pAnno);
            finalValue = ConverterUtils.convert(paramClass, finalValue);
            if (finalValue != null) {
                return finalValue;
            }
        }
        return BeanUtils.getDefaultValue(paramClass);
    }
    private Object resolveSpecialParam(Invoker invoker, Class<?> paramClass) {
        if (!paramClass.isInterface()) {
            return null;
        }
        if (paramClass == ServletRequest.class || paramClass == HttpServletRequest.class) {
            return invoker.getHttpRequest();
        }
        if (paramClass == ServletResponse.class || paramClass == HttpServletResponse.class) {
            return invoker.getHttpResponse();
        }
        if (paramClass == HttpSession.class) {
            return invoker.getHttpRequest().getSession(true);
        }
        //
        if (paramClass == Invoker.class) {
            return invoker;
        }
        if (paramClass.isInterface() && paramClass.isInstance(invoker)) {
            return invoker;
        }
        //
        return invoker.getAppContext().getInstance(paramClass);
    }
    private Object resolveParam(Invoker invoker, Class<?> paramClass, Annotation pAnno) {
        Object atData = null;
        //
        if (pAnno instanceof AttributeParam) {
            atData = this.getAttributeParam(invoker, (AttributeParam) pAnno);
        } else if (pAnno instanceof CookieParam) {
            atData = this.getCookieParam(invoker, (CookieParam) pAnno);
        } else if (pAnno instanceof HeaderParam) {
            atData = this.getHeaderParam(invoker, (HeaderParam) pAnno);
        } else if (pAnno instanceof QueryParam) {
            atData = this.getQueryParam(invoker, (QueryParam) pAnno);
        } else if (pAnno instanceof PathParam) {
            atData = this.getPathParam(invoker, (PathParam) pAnno);
        } else if (pAnno instanceof ReqParam) {
            atData = invoker.getHttpRequest().getParameterValues(((ReqParam) pAnno).value());
        } else if (pAnno instanceof Params) {
            atData = this.getParamsParam(invoker, paramClass);
        }
        //
        return atData;
    }
    /**/
    private Object getParamsParam(Invoker invoker, Class<?> paramClass) {
        Object paramObject = null;
        try {
            paramObject = paramClass.newInstance();
        } catch (Throwable e) {
            logger.error(paramClass.getName() + "newInstance error.", e.getMessage());
            return paramObject;
        }
        List<Field> fieldList = BeanUtils.findALLFields(paramClass);
        if (fieldList == null || fieldList.isEmpty()) {
            return paramObject;
        }
        for (Field field : fieldList) {
            if (field.isAnnotationPresent(IgnoreParam.class)) {
                if (logger.isDebugEnabled()) {
                    logger.debug(field + " -> Ignore.");
                }
                continue;
            }
            try {
                Object fieldValue = null;
                Annotation[] annos = field.getAnnotations();
                if (annos == null || annos.length == 0) {
                    fieldValue = invoker.getHttpRequest().getParameterValues(field.getName());
                } else {
                    fieldValue = resolveParam(invoker, field.getType(), annos);
                }
                if (fieldValue == null) {
                    fieldValue = BeanUtils.getDefaultValue(field.getType());
                }
                fieldValue = ConverterUtils.convert(field.getType(), fieldValue);
                field.setAccessible(true);
                field.set(paramObject, fieldValue);
            } catch (Exception e) {
                logger.error(field + "set new Value error.", e.getMessage());
            }
        }
        return paramObject;
    }
    /**/
    private Object getPathParam(Invoker invoker, PathParam pAnno) {
        String paramName = pAnno.value();
        return StringUtils.isBlank(paramName) ? null : this.getPathParamMap(invoker).get(paramName);
    }
    /**/
    private Object getQueryParam(Invoker invoker, QueryParam pAnno) {
        String paramName = pAnno.value();
        return StringUtils.isBlank(paramName) ? null : this.getQueryParamMap(invoker).get(paramName);
    }
    /**/
    private Object getHeaderParam(Invoker invoker, HeaderParam pAnno) {
        String paramName = pAnno.value();
        if (StringUtils.isBlank(paramName)) {
            return null;
        }
        //
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        Enumeration<?> e = httpRequest.getHeaderNames();
        while (e.hasMoreElements()) {
            String name = e.nextElement().toString();
            if (name.equalsIgnoreCase(paramName)) {
                ArrayList<Object> headerList = new ArrayList<Object>();
                Enumeration<?> v = httpRequest.getHeaders(paramName);
                while (v.hasMoreElements()) {
                    headerList.add(v.nextElement());
                }
                return headerList;
            }
        }
        return null;
    }
    /**/
    private Object getCookieParam(Invoker invoker, CookieParam pAnno) {
        String paramName = pAnno.value();
        if (StringUtils.isBlank(paramName)) {
            return null;
        }
        //
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        Cookie[] cookies = httpRequest.getCookies();
        ArrayList<String> cookieList = new ArrayList<String>();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                if (cookieName == null) {
                    continue;
                }
                if (cookieName.equalsIgnoreCase(paramName)) {
                    cookieList.add(cookie.getValue());
                }
            }
        }
        return cookieList;
    }
    /**/
    private Object getAttributeParam(Invoker invoker, AttributeParam pAnno) {
        String paramName = pAnno.value();
        if (StringUtils.isBlank(paramName)) {
            return null;
        }
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        Enumeration<?> e = httpRequest.getAttributeNames();
        while (e.hasMoreElements()) {
            String name = e.nextElement().toString();
            if (name.equalsIgnoreCase(paramName)) {
                return httpRequest.getAttribute(paramName);
            }
        }
        return null;
    }
    /**/
    private Map<String, List<String>> getQueryParamMap(Invoker invoker) {
        if (this.queryParamLocal != null) {
            return this.queryParamLocal;
        }
        //
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        String queryString = httpRequest.getQueryString();
        if (StringUtils.isBlank(queryString)) {
            return Collections.EMPTY_MAP;
        }
        //
        this.queryParamLocal = new HashMap<String, List<String>>();
        String[] params = queryString.split("&");
        for (String pData : params) {
            String oriData = pData;
            String encoding = httpRequest.getCharacterEncoding();
            try {
                if (encoding != null) {
                    oriData = URLDecoder.decode(pData, encoding);
                }
            } catch (Exception e) {
                logger.warn("use ‘{}’ decode ‘{}’ error.", encoding, pData);
                continue;
            }
            String[] kv = oriData.split("=");
            if (kv.length < 2) {
                continue;
            }
            String k = kv[0].trim();
            String v = kv[1];
            //
            List<String> pArray = this.queryParamLocal.get(k);
            pArray = pArray == null ? new ArrayList<String>() : pArray;
            if (!pArray.contains(v)) {
                pArray.add(v);
            }
            this.queryParamLocal.put(k, pArray);
        }
        return this.queryParamLocal;
    }
    /**/
    private Map<String, Object> getPathParamMap(Invoker invoker) {
        if (this.pathParamsLocal != null) {
            return this.pathParamsLocal;
        }
        //
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        String requestPath = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        String matchVar = this.mappingToDefine.getMappingToMatches();
        String matchKey = "(?:\\{(\\w+)\\}){1,}";//  (?:\{(\w+)\}){1,}
        Matcher keyM = Pattern.compile(matchKey).matcher(this.mappingToDefine.getMappingTo());
        Matcher varM = Pattern.compile(matchVar).matcher(requestPath);
        ArrayList<String> keyArray = new ArrayList<String>();
        ArrayList<String> varArray = new ArrayList<String>();
        while (keyM.find()) {
            keyArray.add(keyM.group(1));
        }
        varM.find();
        for (int i = 1; i <= varM.groupCount(); i++) {
            varArray.add(varM.group(i));
        }
        //
        Map<String, List<String>> uriParams = new HashMap<String, List<String>>();
        for (int i = 0; i < keyArray.size(); i++) {
            String k = keyArray.get(i);
            String v = varArray.get(i);
            List<String> pArray = uriParams.get(k);
            pArray = pArray == null ? new ArrayList<String>() : pArray;
            if (!pArray.contains(v)) {
                pArray.add(v);
            }
            uriParams.put(k, pArray);
        }
        this.pathParamsLocal = new HashMap<String, Object>();
        for (Entry<String, List<String>> ent : uriParams.entrySet()) {
            String k = ent.getKey();
            List<String> v = ent.getValue();
            this.pathParamsLocal.put(k, v.toArray(new String[v.size()]));
        }
        return this.pathParamsLocal;
    }
}