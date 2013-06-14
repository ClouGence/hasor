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
package org.platform.servlet.action.support;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.more.util.BeanUtils;
import org.more.util.StringConvertUtils;
import org.more.util.StringUtils;
import org.platform.Platform;
import org.platform.context.AppContext;
import org.platform.servlet.action.Var;
/**
 * 
 * @version : 2013-5-11
 * @author 赵永春 (zyc@byshell.org)
 */
class InternalActionInvoke implements ActionInvoke, ActionInvoke2 {
    private Method   targetMethod   = null;
    private Object   targetObject   = null;
    private String[] httpMethod     = null;
    private String   mimeType       = null;
    private String   restfulMapping = null;
    //
    public InternalActionInvoke(Method targetMethod, String mimeType) {
        this(targetMethod, mimeType, null);
    }
    public InternalActionInvoke(Method targetMethod, String mimeType, Object targetObject) {
        this.targetMethod = targetMethod;
        this.targetObject = targetObject;
        this.mimeType = mimeType;
    }
    //
    public String[] getHttpMethod() {
        return this.httpMethod;
    }
    public Method getMethod() {
        return targetMethod;
    }
    public String getRestfulMapping() {
        return restfulMapping;
    }
    protected void setHttpMethod(String[] httpMethod) {
        this.httpMethod = httpMethod;
    }
    protected void setRestfulMapping(String restfulMapping) {
        this.restfulMapping = restfulMapping;
    }
    //
    //
    /*--------------------------------------------------------------*/
    private AppContext appContext = null;
    @Override
    public void initInvoke(AppContext appContext) {
        this.appContext = appContext;
    }
    @Override
    public void destroyInvoke() {
        // TODO Auto-generated method stub
    }
    @Override
    public Object invoke(HttpServletRequest request, HttpServletResponse response, Map<String, Object> overwriteHttpParams) throws ServletException {
        if (this.targetObject == null) {
            Class<?> targetClass = this.targetMethod.getDeclaringClass();
            String beanName = this.appContext.getBeanName(targetClass);
            if (StringUtils.isBlank(beanName) == false)
                this.targetObject = this.appContext.getBean(beanName);
            else
                this.targetObject = this.appContext.getInstance(targetClass);
        }
        //
        if (this.targetObject == null)
            throw new ServletException("create invokeObject on " + this.targetMethod.toString() + " return null.");
        //
        try {
            Class<?>[] targetParamClass = this.targetMethod.getParameterTypes();
            Annotation[][] targetParamAnno = this.targetMethod.getParameterAnnotations();
            targetParamClass = (targetParamClass == null) ? new Class<?>[0] : targetParamClass;
            targetParamAnno = (targetParamAnno == null) ? new Annotation[0][0] : targetParamAnno;
            ArrayList<Object> paramsArray = new ArrayList<Object>();
            /*准备参数*/
            for (int i = 0; i < targetParamClass.length; i++) {
                Class<?> paramClass = targetParamClass[i];
                Annotation[] paramAnno = targetParamAnno[i];
                paramAnno = (paramAnno == null) ? new Annotation[0] : paramAnno;
                String paramName = null;
                for (Annotation pAnno : paramAnno) {
                    if (pAnno instanceof Var)
                        paramName = ((Var) pAnno).value();
                }
                /**普通参数*/
                Object paramObject = (overwriteHttpParams != null) ? overwriteHttpParams.get(paramName) : request.getParameterValues(paramName);
                /**特殊参数*/
                if (paramObject == null)
                    paramObject = getSpecialParamObject(request, response, paramClass);
                /**处理参数类型*/
                if (paramObject != null) {
                    try {
                        paramObject = processParamObject(paramObject, paramClass);
                    } catch (Exception e) {
                        /*该代码不会被执行，StringConvertUtils的类方法遇到错误之后会自动使用默认值替代*/
                        paramObject = BeanUtils.getDefaultValue(paramClass);
                        Platform.error("the action request parameter %s Convert Type error %s", paramName, e);
                    }
                }
                paramsArray.add(paramObject);
            }
            //
            if (!StringUtils.isBlank(this.mimeType))
                response.setContentType(this.mimeType);
            //
            return this.targetMethod.invoke(this.targetObject, paramsArray.toArray());
        } catch (InvocationTargetException e) {
            throw new ServletException(e.getCause());
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    /*处理特殊类型参数*/
    private Object getSpecialParamObject(HttpServletRequest request, HttpServletResponse response, Class<?> paramClass) {
        if (paramClass.isEnum() || paramClass.isArray() || paramClass.isPrimitive() || paramClass == String.class)
            return null;/*忽略：基本类型、字符串类型*/
        //
        if (paramClass.isAssignableFrom(HttpServletRequest.class) || paramClass.isAssignableFrom(ServletRequest.class))
            return request;
        if (paramClass.isAssignableFrom(HttpServletResponse.class) || paramClass.isAssignableFrom(ServletResponse.class))
            return response;
        if (paramClass.isAssignableFrom(HttpSession.class))
            return request.getSession(true);
        if (paramClass.isAssignableFrom(ServletContext.class))
            return request.getServletContext();
        try {
            return this.appContext.getInstance(paramClass);
        } catch (Exception e) {
            return null;
        }
    }
    /*处理参数类型转换*/
    private Object processParamObject(Object targetValue, Class<?> targetType) {
        Object returnData = null;
        if (targetType.isArray() == true) {
            //处理数组
            Class<?> targetClass = targetType.getComponentType();
            if (targetValue instanceof Object[]) {
                Object[] arr = (Object[]) targetValue;
                returnData = Array.newInstance(targetClass, arr.length);
                for (int i = 0; i < arr.length; i++)
                    Array.set(returnData, i, processParamObject(arr[i], targetClass));
            } else {
                returnData = Array.newInstance(targetClass, 1);
                Array.set(returnData, 0, targetValue);
            }
        } else {
            //处理单值
            if (targetValue instanceof Object[]) {
                Object[] arrayParamObject = (Object[]) targetValue;
                targetValue = arrayParamObject.length == 0 ? null : arrayParamObject[0];
            }
            returnData = StringConvertUtils.changeType(targetValue, targetType, BeanUtils.getDefaultValue(targetType));
        }
        return returnData;
    }
}