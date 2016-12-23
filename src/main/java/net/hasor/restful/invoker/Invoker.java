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
package net.hasor.restful.invoker;
import net.hasor.core.Provider;
import net.hasor.web.RenderData;
import net.hasor.web.WebController;
import net.hasor.web.annotation.*;
import net.hasor.web.valid.ValidRule;
import org.more.convert.ConverterUtils;
import org.more.util.BeanUtils;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 内置插件，负责处理参数映射。   
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
class Invoker {
    protected Logger                    logger          = LoggerFactory.getLogger(getClass());
    private   MappingToDefine           mappingToDefine = null;
    private   Map<String, List<String>> queryParamLocal = null;
    private   Map<String, Object>       pathParamsLocal = null;
    private   InnerRenderData           renderData      = null;
    //
    public Invoker(MappingToDefine mappingToDefine, InnerRenderData renderData) {
        this.mappingToDefine = mappingToDefine;
        this.renderData = renderData;
    }
    //
    /** 执行调用 */
    public void exeCall(Provider<?> targetProvider, Method targetMethod, ValidRule needValid) throws Throwable {
        Object targetObject = targetProvider.get();
        HttpServletRequest httpRequest = this.renderData.getHttpRequest();
        HttpServletResponse httpResponse = this.renderData.getHttpResponse();
        if (targetObject instanceof WebController) {
            ((WebController) targetObject).initController(this.renderData);
        }
        //
        Object[] resolveParams = this.resolveParams(targetMethod);
        if (needValid != null) {
            needValid.doValid(this.renderData, resolveParams);
        }
        Object resultData = this.invoke(targetMethod, targetObject, resolveParams);
        this.renderData.setReturnData(resultData);
        //
        if (targetMethod.isAnnotationPresent(Produces.class)) {
            Produces pro = targetMethod.getAnnotation(Produces.class);
            String proValue = pro.value();
            if (!StringUtils.isBlank(proValue)) {
                String mimeType = this.renderData.getMimeType(proValue);
                if (StringUtils.isBlank(mimeType)) {
                    httpResponse.setContentType(proValue);
                    this.renderData.viewType(proValue);
                } else {
                    httpResponse.setContentType(mimeType);
                    this.renderData.viewType(mimeType);
                }
            }
        }
        //
    }
    /**准备参数*/
    protected final Object[] resolveParams(Method targetMethod) throws Throwable {
        //
        Class<?>[] targetParamClass = targetMethod.getParameterTypes();
        Annotation[][] targetParamAnno = targetMethod.getParameterAnnotations();
        targetParamClass = (targetParamClass == null) ? new Class<?>[0] : targetParamClass;
        targetParamAnno = (targetParamAnno == null) ? new Annotation[0][0] : targetParamAnno;
        ArrayList<Object> paramsArray = new ArrayList<Object>();
        /*准备参数*/
        for (int i = 0; i < targetParamClass.length; i++) {
            Class<?> paramClass = targetParamClass[i];
            Object paramObject = this.resolveParam(paramClass, targetParamAnno[i]);//获取参数
            paramsArray.add(paramObject);
        }
        Object[] invokeParams = paramsArray.toArray();
        return invokeParams;
    }
    /**准备参数*/
    private final Object resolveParam(Class<?> paramClass, Annotation[] paramAnno) {
        // .特殊类型参数
        Object specialParam = resolveSpecialParam(paramClass);
        if (specialParam != null) {
            return specialParam;
        }
        // .注解解析
        for (Annotation pAnno : paramAnno) {
            Object finalValue = resolveParam(paramClass, pAnno);
            finalValue = ConverterUtils.convert(paramClass, finalValue);
            if (finalValue != null) {
                return finalValue;
            }
        }
        return BeanUtils.getDefaultValue(paramClass);
    }
    private Object resolveSpecialParam(Class<?> paramClass) {
        if (paramClass == ServletRequest.class || paramClass == HttpServletRequest.class) {
            return this.renderData.getHttpRequest();
        }
        if (paramClass == ServletResponse.class || paramClass == HttpServletResponse.class) {
            return this.renderData.getHttpRequest();
        }
        if (paramClass == HttpSession.class) {
            return this.renderData.getHttpRequest().getSession(true);
        }
        //
        if (paramClass == RenderData.class) {
            return this.renderData;
        }
        //
        if (!paramClass.isInterface()) {
            return null;
        }
        return this.renderData.getAppContext().getInstance(paramClass);
    }
    //
    /**/
    protected Object resolveParam(Class<?> paramClass, Annotation pAnno) {
        Object atData = null;
        //
        if (atData == null) {
            /*   */
            if (pAnno instanceof AttributeParam) {
                atData = this.getAttributeParam((AttributeParam) pAnno);
            } else if (pAnno instanceof CookieParam) {
                atData = this.getCookieParam((CookieParam) pAnno);
            } else if (pAnno instanceof HeaderParam) {
                atData = this.getHeaderParam((HeaderParam) pAnno);
            } else if (pAnno instanceof QueryParam) {
                atData = this.getQueryParam((QueryParam) pAnno);
            } else if (pAnno instanceof PathParam) {
                atData = this.getPathParam((PathParam) pAnno);
            } else if (pAnno instanceof ReqParam) {
                atData = this.renderData.getHttpRequest().getParameterValues(((ReqParam) pAnno).value());
            } else if (pAnno instanceof Params) {
                atData = this.getParamsParam(paramClass);
            }
        }
        //
        return atData;
    }
    /**/
    private Object getParamsParam(Class<?> paramClass) {
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
                    fieldValue = this.renderData.getHttpRequest().getParameterValues(field.getName());
                } else {
                    fieldValue = resolveParam(field.getType(), annos);
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
    private Object getPathParam(PathParam pAnno) {
        String paramName = pAnno.value();
        return StringUtils.isBlank(paramName) ? null : this.getPathParamMap().get(paramName);
    }
    /**/
    private Object getQueryParam(QueryParam pAnno) {
        String paramName = pAnno.value();
        return StringUtils.isBlank(paramName) ? null : this.getQueryParamMap().get(paramName);
    }
    /**/
    private Object getHeaderParam(HeaderParam pAnno) {
        String paramName = pAnno.value();
        if (StringUtils.isBlank(paramName)) {
            return null;
        }
        //
        HttpServletRequest httpRequest = this.renderData.getHttpRequest();
        Enumeration<?> e = httpRequest.getHeaderNames();
        while (e.hasMoreElements()) {
            String name = e.nextElement().toString();
            if (StringUtils.equalsIgnoreCase(name, paramName)) {
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
    private Object getCookieParam(CookieParam pAnno) {
        String paramName = pAnno.value();
        if (StringUtils.isBlank(paramName)) {
            return null;
        }
        //
        HttpServletRequest httpRequest = this.renderData.getHttpRequest();
        Cookie[] cookies = httpRequest.getCookies();
        ArrayList<String> cookieList = new ArrayList<String>();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (StringUtils.equalsIgnoreCase(cookie.getName(), paramName)) {
                    cookieList.add(cookie.getValue());
                }
            }
        }
        return cookieList;
    }
    /**/
    private Object getAttributeParam(AttributeParam pAnno) {
        String paramName = pAnno.value();
        if (StringUtils.isBlank(paramName)) {
            return null;
        }
        HttpServletRequest httpRequest = this.renderData.getHttpRequest();
        Enumeration<?> e = httpRequest.getAttributeNames();
        while (e.hasMoreElements()) {
            String name = e.nextElement().toString();
            if (StringUtils.equalsIgnoreCase(name, paramName)) {
                return httpRequest.getAttribute(paramName);
            }
        }
        return null;
    }
    /**/
    private Map<String, List<String>> getQueryParamMap() {
        if (this.queryParamLocal != null) {
            return this.queryParamLocal;
        }
        //
        HttpServletRequest httpRequest = this.renderData.getHttpRequest();
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
    private Map<String, Object> getPathParamMap() {
        if (this.pathParamsLocal != null) {
            return this.pathParamsLocal;
        }
        //
        HttpServletRequest httpRequest = this.renderData.getHttpRequest();
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
    /**/
    public Object invoke(Method targetMethod, Object targetObject, Object[] paramArrays) throws Throwable {
        return targetMethod.invoke(targetObject, paramArrays);
    }
}