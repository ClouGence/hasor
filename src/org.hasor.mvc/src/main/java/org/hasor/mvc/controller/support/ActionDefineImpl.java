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
import java.lang.reflect.Method;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hasor.context.AppContext;
import org.hasor.mvc.controller.ActionDefine;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2013-8-12
 * @author 赵永春 (zyc@hasor.net)
 */
class ActionDefineImpl implements ActionDefine {
    private Method     targetMethod;
    private Object     targetObject;
    private String[]   httpMethod;
    private String     mimeType;
    private String     restfulMapping;
    private String     restfulMappingMatches;
    private AppContext appContext;
    //
    public ActionDefineImpl(Method targetMethod, String[] httpMethod, String mimeType, String restfulMapping, Object targetObject) {
        this.targetMethod = targetMethod;
        this.httpMethod = httpMethod;
        this.mimeType = mimeType;
        this.restfulMapping = restfulMapping;
        this.targetObject = targetObject;
    }
    //
    public boolean matchingMethod(String httpMethod) {
        for (String m : this.getHttpMethod())
            if (StringUtils.equalsIgnoreCase(httpMethod, m))
                return true;
            else if (StringUtils.equalsIgnoreCase(m, "ANY"))
                return true;
        return false;
    }
    //
    /**获取Action可以接收的方法*/
    public String[] getHttpMethod() {
        return this.httpMethod;
    }
    //
    /**获取目标方法。*/
    public Method getTargetMethod() {
        return this.targetMethod;
    }
    //
    /**获取映射字符串*/
    public String getRestfulMapping() {
        return this.restfulMapping;
    }
    //
    /**获取映射字符串用于匹配的表达式字符串*/
    public String getRestfulMappingMatches() {
        if (this.restfulMappingMatches == null) {
            String mapping = this.getRestfulMapping();
            this.restfulMappingMatches = mapping.replaceAll("\\{\\w{1,}\\}", "([^/]{1,})");
        }
        return this.restfulMappingMatches;
    }
    //
    /**获取响应类型*/
    public String getMimeType() {
        return mimeType;
    }
    //
    /**获取AppContext*/
    public AppContext getAppContext() {
        return appContext;
    }
    //
    /**初始化*/
    public void initInvoke(AppContext appContext) {
        this.appContext = appContext;
    }
    /**创建一个ActionInvoke*/
    public ActionInvokeImpl createInvoke(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException {
        Object target = this.targetObject;
        Method targetMethod = this.getTargetMethod();
        //
        if (target == null) {
            Class<?> targetClass = targetMethod.getDeclaringClass();
            String beanName = this.getAppContext().getBeanName(targetClass);
            if (StringUtils.isBlank(beanName) == false)
                target = this.getAppContext().getBean(beanName);
            else
                target = this.getAppContext().getInstance(targetClass);
        }
        //
        if (target == null)
            throw new ServletException("create invokeObject on " + targetMethod.toString() + " return null.");
        return new ActionInvokeImpl(this, target, (HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
    }
    public boolean isRESTful() {
        return !StringUtils.isBlank(restfulMapping);
    }
}