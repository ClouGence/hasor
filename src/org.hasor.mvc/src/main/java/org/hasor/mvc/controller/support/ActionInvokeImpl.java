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
package org.hasor.mvc.controller.support;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
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
import org.hasor.Hasor;
import org.hasor.context.AppContext;
import org.hasor.context.EventManager;
import org.hasor.mvc.controller.ActionDefine;
import org.hasor.mvc.controller.ActionInvoke;
import org.hasor.mvc.controller.AttributeParam;
import org.hasor.mvc.controller.CookieParam;
import org.hasor.mvc.controller.HeaderParam;
import org.hasor.mvc.controller.InjectParam;
import org.hasor.mvc.controller.PathParam;
import org.hasor.mvc.controller.QueryParam;
import org.more.convert.ConverterUtils;
import org.more.util.BeanUtils;
import org.more.util.StringUtils;
/**
 * 线程安全
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
class ActionInvokeImpl implements ActionInvoke {
    private ActionDefine        actionDefine = null;
    private AppContext          appContext   = null;
    private Object              targetObject = null;
    private HttpServletRequest  request      = null;
    private HttpServletResponse response     = null;
    private String              actionPath   = null;
    //
    public ActionInvokeImpl(ActionDefine actionDefine, Object targetObject, HttpServletRequest request, HttpServletResponse response) {
        this.actionDefine = actionDefine;
        this.targetObject = targetObject;
        this.request = request;
        this.response = response;
        this.appContext = this.getActionDefine().getAppContext();
        this.actionPath = request.getRequestURI().substring(request.getContextPath().length());
    }
    //
    /**获取ActionDefine*/
    public ActionDefine getActionDefine() {
        return this.actionDefine;
    }
    //
    /**获取AppContext*/
    public AppContext getAppContext() {
        return this.appContext;
    }
    //
    /**获取调用的目标对象*/
    public Object getTargetObject() {
        return targetObject;
    }
    //
    /**执行调用*/
    public Object invoke() throws InvocationTargetException {
        Method targetMethod = this.getActionDefine().getTargetMethod();
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
        /*设置返回ContentType*/
        String mimeType = this.getActionDefine().getMimeType();
        if (!StringUtils.isBlank(mimeType))
            response.setContentType(mimeType);
        /*执行调用*/
        return this.call(targetMethod, invokeParams);
    }
    //
    /**执行调用，并引发事件*/
    private Object call(Method targetMethod, Object[] invokeParams) throws InvocationTargetException {
        if (this.targetObject instanceof AbstractController)
            ((AbstractController) this.targetObject).initController(this.request, this.response);
        //
        Object returnData = null;
        try {
            EventManager eventManager = this.getAppContext().getEventManager();
            eventManager.doSyncEvent(ActionDefineImpl.Event_BeforeInvoke, this, invokeParams);/*引发事件*/
            returnData = targetMethod.invoke(this.targetObject, invokeParams);
            eventManager.doSyncEvent(ActionDefineImpl.Event_AfterInvoke, this, invokeParams, returnData); /*引发事件*/
        } catch (Throwable e) {
            if (e instanceof InvocationTargetException)
                throw (InvocationTargetException) e;
            throw new InvocationTargetException(e);//将异常包装为InvocationTargetException类型Controller会拆开该异常。
        }
        return returnData;
    }
    //
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
            else if (pAnno instanceof InjectParam)
                return this.getInjectParam(paramClass, (InjectParam) pAnno);
        }
        return BeanUtils.getDefaultValue(paramClass);
    }
    //
    //
    //
    //
    //
    //
    //
    //
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
        Enumeration e = this.request.getHeaderNames();
        while (e.hasMoreElements()) {
            String name = e.nextElement().toString();
            if (StringUtils.equalsIgnoreCase(name, paramName)) {
                ArrayList<Object> headerList = new ArrayList<Object>();
                Enumeration v = this.request.getHeaders(paramName);
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
        Cookie[] cookies = this.request.getCookies();
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
        Enumeration e = this.request.getAttributeNames();
        while (e.hasMoreElements()) {
            String name = e.nextElement().toString();
            if (StringUtils.equalsIgnoreCase(name, paramName))
                return this.request.getAttribute(paramName);
        }
        return null;
    }
    /**/
    private Object getInjectParam(Class<?> paramClass, InjectParam injectParam) {
        if (StringUtils.isBlank(injectParam.value()))
            return this.appContext.getInstance(paramClass);
        else
            return this.appContext.getBean(injectParam.value());
    }
    /**/
    private Map<String, List<String>> queryParam = null;
    private Map<String, List<String>> getQueryParamMap() {
        if (this.queryParam != null)
            return this.queryParam;
        //
        String queryString = this.request.getQueryString();
        if (StringUtils.isBlank(queryString))
            return null;
        //
        Map<String, List<String>> uriParams = new HashMap<String, List<String>>();
        String[] params = queryString.split("&");
        for (String pData : params) {
            String oriData = null;
            String encoding = this.request.getCharacterEncoding();
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
        this.queryParam = uriParams;
        return this.queryParam;
    }
    /**/
    private Map<String, Object> pathParams = null;
    private Map<String, Object> getPathParamMap() {
        if (this.pathParams != null || this.actionDefine.isRESTful() == false)
            return this.pathParams;
        //
        String matchVar = this.actionDefine.getRestfulMappingMatches();
        String matchKey = "(?:\\{(\\w+)\\}){1,}";//  (?:\{(\w+)\}){1,}
        Matcher keyM = Pattern.compile(matchKey).matcher(this.actionDefine.getRestfulMapping());
        Matcher varM = Pattern.compile(matchVar).matcher(actionPath);
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
        HashMap<String, Object> pathParams = new HashMap<String, Object>();
        pathParams.putAll(request.getParameterMap());
        for (Entry<String, List<String>> ent : uriParams.entrySet()) {
            String k = ent.getKey();
            List<String> v = ent.getValue();
            pathParams.put(k.toUpperCase(), v.toArray(new String[v.size()]));
        }
        this.pathParams = pathParams;
        return this.pathParams;
    }
    public HttpServletRequest getRequest() {
        return this.request;
    }
    public HttpServletResponse getResponse() {
        return this.response;
    }
}