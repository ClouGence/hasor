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
package org.hasor.mvc.controller.support;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hasor.Hasor;
import org.hasor.context.AppContext;
import org.hasor.context.EventManager;
import org.hasor.mvc.controller.ActionDefine;
import org.hasor.mvc.controller.ActionInvoke;
import org.hasor.mvc.controller.Var;
import org.hasor.servlet.context.provider.HttpProvider;
import org.more.convert.ConverterUtils;
import org.more.util.BeanUtils;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2013-6-5
 * @author 赵永春 (zyc@byshell.org)
 */
class ActionInvokeImpl implements ActionInvoke {
    private ActionDefine    actionDefine = null;
    private AppContext      appContext   = null;
    private Object          targetObject = null;
    private ServletRequest  request      = null;
    private ServletResponse response     = null;
    //
    public ActionInvokeImpl(ActionDefine actionDefine, Object targetObject, ServletRequest request, ServletResponse response) {
        this.actionDefine = actionDefine;
        this.targetObject = targetObject;
        this.request = request;
        this.response = response;
        this.appContext = this.getActionDefine().getAppContext();
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
    public Object invoke() throws ServletException {
        HashMap<String, Object> overwriteHttpParams = new HashMap<String, Object>();
        overwriteHttpParams.putAll(request.getParameterMap());
        return this.invoke(overwriteHttpParams);
    }
    //
    /**执行调用*/
    public Object invoke(Map<String, Object> overwriteHttpParams) throws ServletException {
        try {
            Method targetMethod = this.getActionDefine().getTargetMethod();
            Class<?>[] targetParamClass = targetMethod.getParameterTypes();
            Annotation[][] targetParamAnno = targetMethod.getParameterAnnotations();
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
                Object paramObject = null;
                if (StringUtils.isBlank(paramName) == false)
                    paramObject = (overwriteHttpParams != null) ? overwriteHttpParams.get(paramName) : request.getParameterValues(paramName);
                /**特殊参数*/
                if (paramObject == null)
                    paramObject = getSpecialParamObject(paramClass);
                /**处理参数类型*/
                if (paramObject != null) {
                    try {
                        paramObject = processParamObject(paramObject, paramClass);
                    } catch (Exception e) {
                        /*该代码不会被执行，StringConvertUtils的类方法遇到错误之后会自动使用默认值替代*/
                        paramObject = BeanUtils.getDefaultValue(paramClass);
                        Hasor.error("the action request parameter %s Convert Type error %s", paramName, e);
                    }
                }
                paramsArray.add(paramObject);
            }
            Object[] invokeParams = paramsArray.toArray();
            //
            String mimeType = this.getActionDefine().getMimeType();
            if (!StringUtils.isBlank(mimeType))
                response.setContentType(mimeType);
            //
            return this.call(targetMethod, this.getTargetObject(), invokeParams);
        } catch (InvocationTargetException e) {
            throw new ServletException(e.getCause());
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    //
    /*执行调用*/
    private Object call(Method targetMethod, Object targetObject, Object[] invokeParams) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        HttpProvider httpProvider = HttpProvider.getProvider();
        HttpServletRequest oriRequest = httpProvider.getRequest();
        HttpServletResponse oriResponse = httpProvider.getResponse();
        {
            httpProvider.update((HttpServletRequest) this.request, (HttpServletResponse) this.response);
        }
        EventManager eventManager = this.getAppContext().getEventManager();
        eventManager.doSyncEvent(ActionDefineImpl.Event_BeforeInvoke, this, invokeParams);/*引发事件*/
        Object returnData = targetMethod.invoke(this.getTargetObject(), invokeParams);
        eventManager.doSyncEvent(ActionDefineImpl.Event_AfterInvoke, this, invokeParams, returnData); /*引发事件*/
        {
            httpProvider.update(oriRequest, oriResponse);
        }
        return returnData;
    }
    /*处理特殊类型参数*/
    private Object getSpecialParamObject(Class<?> paramClass) {
        if (paramClass.isEnum() || paramClass.isArray() || paramClass.isPrimitive() || paramClass == String.class)
            return null;/*忽略：基本类型、字符串类型*/
        //
        try {
            return this.getActionDefine().getAppContext().getInstance(paramClass);
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
            returnData = ConverterUtils.convert(targetType, targetValue, BeanUtils.getDefaultValue(targetType));
        }
        return returnData;
    }
}