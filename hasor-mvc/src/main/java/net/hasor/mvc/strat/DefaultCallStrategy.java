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
package net.hasor.mvc.strat;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import net.hasor.mvc.Call;
import net.hasor.mvc.CallStrategy;
import net.hasor.mvc.MappingInfo;
import net.hasor.mvc.ModelController;
import net.hasor.mvc.api.AbstractWebController;
import net.hasor.mvc.api.AttributeParam;
import net.hasor.mvc.api.CookieParam;
import net.hasor.mvc.api.HeaderParam;
import net.hasor.mvc.api.Param;
import net.hasor.mvc.api.PathParam;
import net.hasor.mvc.api.Produces;
import net.hasor.mvc.api.QueryParam;
import org.more.convert.ConverterUtils;
import org.more.util.BeanUtils;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
public class DefaultCallStrategy implements CallStrategy {
    protected Logger                 logger   = LoggerFactory.getLogger(getClass());
    public static final CallStrategy Instance = new DefaultCallStrategy();
    //
    /**初始化调用。*/
    protected void initCall(Call call) {
        ModelController controller = call.getTarget();
        if (controller instanceof AbstractWebController) {
            ((AbstractWebController) controller).initController(call.getHttpRequest(), call.getHttpResponse());
        }
    }
    /** 执行调用 */
    public Object exeCall(Call call) throws Throwable {
        this.initCall(call);
        Object[] args = this.resolveParams(call);
        return this.returnCallBack(call.call(args), call);
    }
    /**处理结果 */
    protected Object returnCallBack(Object returnData, Call call) {
        Method targetMethod = call.getMethod();
        if (targetMethod.isAnnotationPresent(Produces.class) == true) {
            Produces pro = targetMethod.getAnnotation(Produces.class);
            String proValue = pro.value();
            if (StringUtils.isBlank(proValue) == false) {
                call.getHttpResponse().setContentType(proValue);
            }
        }
        //
        return returnData;
    }
    /**准备参数*/
    protected final Object[] resolveParams(Call call) throws Throwable {
        Method targetMethod = call.getMethod();
        //
        Class<?>[] targetParamClass = call.getParameterTypes();
        Annotation[][] targetParamAnno = call.getMethodParamAnnos();
        targetParamClass = (targetParamClass == null) ? new Class<?>[0] : targetParamClass;
        targetParamAnno = (targetParamAnno == null) ? new Annotation[0][0] : targetParamAnno;
        ArrayList<Object> paramsArray = new ArrayList<Object>();
        /*准备参数*/
        for (int i = 0; i < targetParamClass.length; i++) {
            Class<?> paramClass = targetParamClass[i];
            Object paramObject = this.resolveParam(paramClass, targetParamAnno[i], call);//获取参数
            /*获取到的参数需要做一个类型转换，以防止method.invoke时发生异常。*/
            if (paramObject == null) {
                paramObject = BeanUtils.getDefaultValue(paramClass);
            } else {
                paramObject = ConverterUtils.convert(paramClass, paramObject);
            }
            paramsArray.add(paramObject);
        }
        Object[] invokeParams = paramsArray.toArray();
        return invokeParams;
    }
    /**准备参数*/
    private final Object resolveParam(Class<?> paramClass, Annotation[] paramAnno, Call call) {
        for (Annotation pAnno : paramAnno) {
            Object finalValue = resolveParam(paramClass, pAnno, call);
            if (finalValue != null) {
                return finalValue;
            }
        }
        return BeanUtils.getDefaultValue(paramClass);
    }
    //
    //
    //
    /**/
    protected Object resolveParam(Class<?> paramClass, Annotation pAnno, Call call) {
        Object atData = null;
        //
        if (atData == null) {
            /*   */if (pAnno instanceof AttributeParam) {
                atData = this.getAttributeParam(call, paramClass, (AttributeParam) pAnno);
            } else if (pAnno instanceof Param) {
                atData = call.getParam(((Param) pAnno).value());
            } else if (pAnno instanceof CookieParam) {
                atData = this.getCookieParam(call, paramClass, (CookieParam) pAnno);
            } else if (pAnno instanceof HeaderParam) {
                atData = this.getHeaderParam(call, paramClass, (HeaderParam) pAnno);
            } else if (pAnno instanceof QueryParam) {
                atData = this.getQueryParam(call, paramClass, (QueryParam) pAnno);
            } else if (pAnno instanceof PathParam) {
                atData = this.getPathParam(call, paramClass, (PathParam) pAnno, call.getMappingInfo());
            }
        }
        //
        return atData;
    }
    /**/
    private Object getPathParam(Call call, Class<?> paramClass, PathParam pAnno, MappingInfo mappingInfo) {
        String paramName = pAnno.value();
        return StringUtils.isBlank(paramName) ? null : this.getPathParamMap(call, mappingInfo).get(paramName);
    }
    /**/
    private Object getQueryParam(Call call, Class<?> paramClass, QueryParam pAnno) {
        String paramName = pAnno.value();
        return StringUtils.isBlank(paramName) ? null : this.getQueryParamMap(call).get(paramName);
    }
    /**/
    private Object getHeaderParam(Call call, Class<?> paramClass, HeaderParam pAnno) {
        String paramName = pAnno.value();
        if (StringUtils.isBlank(paramName)) {
            return null;
        }
        //
        HttpServletRequest httpRequest = call.getHttpRequest();
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
    private Object getCookieParam(Call call, Class<?> paramClass, CookieParam pAnno) {
        String paramName = pAnno.value();
        if (StringUtils.isBlank(paramName)) {
            return null;
        }
        //
        HttpServletRequest httpRequest = call.getHttpRequest();
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
    private Object getAttributeParam(Call call, Class<?> paramClass, AttributeParam pAnno) {
        String paramName = pAnno.value();
        if (StringUtils.isBlank(paramName)) {
            return null;
        }
        HttpServletRequest httpRequest = call.getHttpRequest();
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
    private Map<String, List<String>> queryParamLocal;
    private Map<String, List<String>> getQueryParamMap(Call call) {
        if (queryParamLocal != null) {
            return queryParamLocal;
        }
        //
        HttpServletRequest httpRequest = call.getHttpRequest();
        String queryString = httpRequest.getQueryString();
        if (StringUtils.isBlank(queryString)) {
            return Collections.EMPTY_MAP;
        }
        //
        queryParamLocal = new HashMap<String, List<String>>();
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
            List<String> pArray = queryParamLocal.get(k);
            pArray = pArray == null ? new ArrayList<String>() : pArray;
            if (pArray.contains(v) == false) {
                pArray.add(v);
            }
            queryParamLocal.put(k, pArray);
        }
        return queryParamLocal;
    }
    /**/
    private Map<String, Object> pathParamsLocal;
    private Map<String, Object> getPathParamMap(Call call, MappingInfo mappingInfo) {
        if (this.pathParamsLocal != null) {
            return this.pathParamsLocal;
        }
        //
        HttpServletRequest httpRequest = call.getHttpRequest();
        String requestPath = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        String matchVar = mappingInfo.getMappingToMatches();
        String matchKey = "(?:\\{(\\w+)\\}){1,}";//  (?:\{(\w+)\}){1,}
        Matcher keyM = Pattern.compile(matchKey).matcher(mappingInfo.getMappingTo());
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
            if (pArray.contains(v) == false) {
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