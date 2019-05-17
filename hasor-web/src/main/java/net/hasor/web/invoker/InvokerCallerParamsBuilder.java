package net.hasor.web.invoker;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.convert.ConverterUtils;
import net.hasor.web.Invoker;
import net.hasor.web.Mapping;
import net.hasor.web.annotation.*;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//
public class InvokerCallerParamsBuilder {
    protected static Logger                    logger          = LoggerFactory.getLogger(InvokerCaller.class);
    private          Map<String, List<String>> queryParamLocal = null;
    private          Map<String, Object>       pathParamsLocal = null;
    //
    public Object[] resolveParams(Invoker invoker, Method targetMethod) throws Throwable {
        //
        Class<?>[] targetParamClass = targetMethod.getParameterTypes();
        Annotation[][] targetParamAnno = targetMethod.getParameterAnnotations();
        targetParamAnno = (targetParamAnno == null) ? new Annotation[0][0] : targetParamAnno;
        ArrayList<Object> paramsArray = new ArrayList<>();
        /*准备参数*/
        for (int i = 0; i < targetParamClass.length; i++) {
            Class<?> paramClass = targetParamClass[i];
            Object paramObject = this.resolveParam(invoker, paramClass, targetParamAnno[i]);//获取参数
            paramsArray.add(paramObject);
        }
        return paramsArray.toArray();
    }
    /**/
    private Object resolveParam(Invoker invoker, Class<?> paramClass, Annotation[] paramAnno) {
        // .特殊类型参数
        Object specialParam = resolveSpecialParam(invoker, paramClass);
        if (specialParam != null) {
            return specialParam;
        }
        // .注解解析
        for (Annotation pAnno : paramAnno) {
            if (pAnno.annotationType().getAnnotation(WebParameter.class) == null) {
                continue;
            }
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
        if (paramClass.isInstance(invoker)) {
            return invoker;
        }
        return null; //return invoker.getAppContext().getInstance(paramClass);
    }
    private Object resolveParam(Invoker invoker, Class<?> paramClass, Annotation pAnno) {
        Object atData = null;
        //
        if (pAnno instanceof AttributeParameter) {
            atData = this.getAttributeParam(invoker, (AttributeParameter) pAnno);
        } else if (pAnno instanceof CookieParameter) {
            atData = this.getCookieParam(invoker, (CookieParameter) pAnno);
        } else if (pAnno instanceof HeaderParameter) {
            atData = this.getHeaderParam(invoker, (HeaderParameter) pAnno);
        } else if (pAnno instanceof QueryParameter) {
            atData = this.getQueryParam(invoker, (QueryParameter) pAnno);
        } else if (pAnno instanceof PathParameter) {
            atData = this.getPathParam(invoker, (PathParameter) pAnno);
        } else if (pAnno instanceof RequestParameter) {
            atData = invoker.getHttpRequest().getParameterValues(((RequestParameter) pAnno).value());
        } else if (pAnno instanceof ParameterForm) {
            try {
                atData = this.getParamsParam(invoker, paramClass, paramClass.newInstance());
            } catch (Throwable e) {
                logger.error(paramClass.getName() + "newInstance error.", e.getMessage());
                atData = null;
            }
        }
        return atData;
    }
    /**/
    public <T> T getParamsParam(Invoker invoker, Class<? extends T> paramClass, T paramObject) {
        if (paramObject == null) {
            return null;
        }
        List<Field> fieldList = BeanUtils.findALLFields(paramClass);
        if (fieldList == null || fieldList.isEmpty()) {
            return paramObject;
        }
        for (Field field : fieldList) {
            if (field.isAnnotationPresent(IgnoreParam.class)) {
                logger.debug(field + " -> Ignore.");
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
    private Object getPathParam(Invoker invoker, PathParameter pAnno) {
        String paramName = pAnno.value();
        return StringUtils.isBlank(paramName) ? null : this.getPathParamMap(invoker).get(paramName);
    }
    /**/
    private Object getQueryParam(Invoker invoker, QueryParameter pAnno) {
        String paramName = pAnno.value();
        return StringUtils.isBlank(paramName) ? null : this.getQueryParamMap(invoker).get(paramName);
    }
    /**/
    private Object getHeaderParam(Invoker invoker, HeaderParameter pAnno) {
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
                ArrayList<Object> headerList = new ArrayList<>();
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
    private Object getCookieParam(Invoker invoker, CookieParameter pAnno) {
        String paramName = pAnno.value();
        if (StringUtils.isBlank(paramName)) {
            return null;
        }
        //
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        Cookie[] cookies = httpRequest.getCookies();
        ArrayList<String> cookieList = new ArrayList<>();
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
    private Object getAttributeParam(Invoker invoker, AttributeParameter pAnno) {
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
        this.queryParamLocal = new HashMap<>();
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
            pArray = pArray == null ? new ArrayList<>() : pArray;
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
        Mapping ownerMapping = invoker.ownerMapping();
        if (ownerMapping == null) {
            return Collections.EMPTY_MAP;
        }
        //
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
        Map<String, List<String>> uriParams = new HashMap<>();
        for (int i = 0; i < keyArray.size(); i++) {
            String k = keyArray.get(i);
            String v = varArray.get(i);
            List<String> pArray = uriParams.get(k);
            pArray = pArray == null ? new ArrayList<>() : pArray;
            if (!pArray.contains(v)) {
                pArray.add(v);
            }
            uriParams.put(k, pArray);
        }
        this.pathParamsLocal = new HashMap<>();
        for (Map.Entry<String, List<String>> ent : uriParams.entrySet()) {
            String k = ent.getKey();
            List<String> v = ent.getValue();
            this.pathParamsLocal.put(k, v.toArray(new String[0]));
        }
        return this.pathParamsLocal;
    }
}