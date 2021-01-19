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
import com.alibaba.fastjson.JSON;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.convert.ConverterUtils;
import net.hasor.web.Invoker;
import net.hasor.web.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 负责解析参数并执行调用。
 * @version : 2019-05-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class InvokerCallerParamsBuilder {
    protected static Logger logger = LoggerFactory.getLogger(InvokerCaller.class);

    public Object[] resolveParams(Invoker invoker, Method targetMethod) throws Throwable {
        Class<?>[] targetParamClass = targetMethod.getParameterTypes();
        Annotation[][] targetParamAnno = targetMethod.getParameterAnnotations();
        ArrayList<Object> paramsArray = new ArrayList<>();
        /*准备参数*/
        for (int i = 0; i < targetParamClass.length; i++) {
            Class<?> paramClass = targetParamClass[i];
            Object paramObject = this.resolveParam(invoker, paramClass, targetParamAnno[i], true);//获取参数
            paramsArray.add(paramObject);
        }
        return paramsArray.toArray();
    }

    /**/
    private Object resolveParam(Invoker invoker, Class<?> paramClass, Annotation[] paramAnno, boolean useDefault) {
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
            return finalValue;
        }
        if (useDefault) {
            return BeanUtils.getDefaultValue(paramClass);
        }
        return null;
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
        if (paramClass == ServletContext.class) {
            return invoker.getAppContext().getInstance(ServletContext.class);
        }
        //
        if (paramClass == Invoker.class || paramClass.isInstance(invoker)) {
            return invoker;
        }
        if (paramClass == AppContext.class) {
            return invoker.getAppContext();
        }
        if (paramClass == Environment.class) {
            return invoker.getAppContext().getEnvironment();
        }
        if (paramClass == Settings.class) {
            return invoker.getAppContext().getEnvironment().getSettings();
        }
        return null; //return invoker.getAppContext().getInstance(paramClass);
    }

    private Object resolveParam(Invoker invoker, Class<?> paramClass, Annotation pAnno) {
        Object atData = null;
        //
        if (pAnno instanceof AttributeParameter) {
            atData = this.getAttributeParam(invoker, (AttributeParameter) pAnno);
        } else if (pAnno instanceof CookieParameter) {
            atData = this.getCookieParam((CookieParameter) pAnno);
        } else if (pAnno instanceof HeaderParameter) {
            atData = this.getHeaderParam((HeaderParameter) pAnno);
        } else if (pAnno instanceof QueryParameter) {
            atData = this.getQueryParam((QueryParameter) pAnno);
        } else if (pAnno instanceof PathParameter) {
            atData = this.getPathParam((PathParameter) pAnno);
        } else if (pAnno instanceof RequestParameter) {
            atData = this.getRequestParam((RequestParameter) pAnno);
        } else if (pAnno instanceof RequestBody) {
            String jsonBodyData = invoker.getJsonBodyString().trim();
            if (paramClass == String.class) {
                atData = jsonBodyData;
            } else if (paramClass == Map.class) {
                atData = JSON.parseObject(jsonBodyData);
            } else if (paramClass == List.class) {
                jsonBodyData = (jsonBodyData.charAt(0) != '[') ? ("[" + jsonBodyData + "]") : jsonBodyData;
                atData = JSON.parseArray(jsonBodyData, ArrayList.class);
            } else if (paramClass == Set.class) {
                jsonBodyData = (jsonBodyData.charAt(0) != '[') ? ("[" + jsonBodyData + "]") : jsonBodyData;
                atData = JSON.parseArray(jsonBodyData, HashSet.class);
            } else {
                atData = JSON.parseObject(jsonBodyData, paramClass);
            }
        } else if (pAnno instanceof ParameterGroup) {
            try {
                atData = invoker.getAppContext().justInject(paramClass.newInstance());
                atData = this.getParamsParam(invoker, paramClass, atData);
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
            try {
                Object fieldValue = null;
                Annotation[] annos = field.getAnnotations();
                if (annos != null && annos.length > 0) {
                    fieldValue = resolveParam(invoker, field.getType(), annos, false);
                    if (fieldValue != null) {
                        fieldValue = ConverterUtils.convert(field.getType(), fieldValue);
                        field.setAccessible(true);
                        field.set(paramObject, fieldValue);
                    }
                }
            } catch (Exception e) {
                logger.error(field + "set new Value error.", e.getMessage());
            }
        }
        return paramObject;
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
    private Object getPathParam(PathParameter pAnno) {
        String paramName = pAnno.value();
        return StringUtils.isBlank(paramName) ? null : HttpParameters.pathArrayMap().get(paramName);
    }

    /**/
    private Object getQueryParam(QueryParameter pAnno) {
        String paramName = pAnno.value();
        return StringUtils.isBlank(paramName) ? null : HttpParameters.queryArrayMap().get(paramName);
    }

    /**/
    private Object getHeaderParam(HeaderParameter pAnno) {
        String paramName = pAnno.value();
        return StringUtils.isBlank(paramName) ? null : HttpParameters.headerArrayMap().get(paramName);
    }

    /**/
    private Object getCookieParam(CookieParameter pAnno) {
        String paramName = pAnno.value();
        return StringUtils.isBlank(paramName) ? null : HttpParameters.cookieArrayMap().get(paramName);
    }

    /**/
    private Object getRequestParam(RequestParameter pAnno) {
        String paramName = pAnno.value();
        return StringUtils.isBlank(paramName) ? null : HttpParameters.requestArrayMap().get(paramName);
    }
}