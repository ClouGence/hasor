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
package org.platform.action.support;
import java.lang.reflect.Method;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.more.util.StringUtils;
import org.platform.action.ActionInvoke;
import org.platform.context.AppContext;
/**
 * 
 * @version : 2013-5-11
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
abstract class InternalActionInvoke implements ActionInvoke {
    private String[]   httpMethod     = null;
    private String     actionName     = null;
    private String     restfulMapping = null;
    private AppContext appContext     = null;
    private Object     target         = null;
    public InternalActionInvoke(String actionName) {
        this.actionName = actionName;
    }
    //
    //
    public String[] getHttpMethod() {
        return this.httpMethod;
    }
    public String getActionName() {
        return actionName;
    }
    public String getRestfulMapping() {
        return restfulMapping;
    }
    protected AppContext getAppContext() {
        return appContext;
    }
    protected Object getTarget() {
        return this.target;
    }
    protected void setTarget(Object target) {
        this.target = target;
    }
    protected void setActionMethod(String[] httpMethod) {
        this.httpMethod = httpMethod;
    }
    public void setRestfulMapping(String restfulMapping) {
        this.restfulMapping = restfulMapping;
    }
    /**≥ı ºªØActionInvoke*/
    public void destroyInvoke(AppContext appContext) {
        this.appContext = appContext;
    };
    /**œ˙ªŸActionInvoke*/
    public void initInvoke(AppContext appContext) {
        this.appContext = appContext;
    };
    @Override
    public abstract Object invoke(HttpServletRequest request, HttpServletResponse response) throws ServletException;
    /*-------------------------------------------------------------------------------------------------------------------*/
    /**Method*/
    public static class InternalMethodActionInvoke extends InternalActionInvoke {
        private Method targetMethod = null;
        public InternalMethodActionInvoke(Method targetMethod) {
            super(targetMethod.getName());
            this.targetMethod = targetMethod;
        }
        @Override
        public Object invoke(HttpServletRequest request, HttpServletResponse response) throws ServletException {
            Object targetObject = this.getTarget();
            if (targetObject == null) {
                AppContext appContext = this.getAppContext();
                Class<?> targetClass = this.targetMethod.getDeclaringClass();
                String beanName = appContext.getBeanName(targetClass);
                if (StringUtils.isBlank(beanName) == false)
                    targetObject = appContext.getBean(beanName);
                else
                    targetObject = appContext.getInstance(targetClass);
            }
            //
            if (targetObject == null)
                throw new ServletException("create invokeObject on " + targetMethod.toString() + " return null.");
            //
            s
            return null;
        }
    }
    /**ActionInvoke*/
    public static class InternalInvokeActionInvoke extends InternalActionInvoke {
        public InternalInvokeActionInvoke(String actionName, ActionInvoke targetInvoke) {
            super(actionName);
            this.setTarget(targetInvoke);
        }
        @Override
        public Object invoke(HttpServletRequest request, HttpServletResponse response) throws ServletException {
            return ((ActionInvoke) this.getTarget()).invoke(request, response);
        }
    }
}