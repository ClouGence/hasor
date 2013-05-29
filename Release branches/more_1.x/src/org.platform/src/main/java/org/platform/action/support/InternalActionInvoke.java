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
import org.platform.action.ActionInvoke;
import org.platform.context.AppContext;
/**
 * 
 * @version : 2013-5-11
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
abstract class InternalActionInvoke implements ActionInvoke {
    private String[] actionMethod = null;
    //
    //
    public InternalActionInvoke(String[] actionMethod) {
        this.actionMethod = actionMethod;
    }
    public String[] getActionMethod() {
        return this.actionMethod;
    }
    /**≥ı ºªØActionInvoke*/
    public void destroyInvoke(AppContext appContext) {};
    /**œ˙ªŸActionInvoke*/
    public void initInvoke(AppContext appContext) {};
    @Override
    public abstract Object invoke(HttpServletRequest request, HttpServletResponse response) throws ServletException;
    /*-------------------------------------------------------------------------------------------------------------------*/
    /**Method*/
    public static class InternalMethodActionInvoke extends InternalActionInvoke {
        private Method targetMethod = null;
        public InternalMethodActionInvoke(String[] actionMethod, Method targetMethod) {
            super(actionMethod);
            this.targetMethod = targetMethod;
        }
        @Override
        public Object invoke(HttpServletRequest request, HttpServletResponse response) throws ServletException {
            // TODO Auto-generated method stub
            return null;
        }
    }
    /**Class*/
    public static class InternalClassActionInvoke extends InternalActionInvoke {
        private Class<?> targetClass = null;
        public InternalClassActionInvoke(String[] actionMethod, Class<?> targetClass) {
            super(actionMethod);
            this.targetClass = targetClass;
        }
        @Override
        public Object invoke(HttpServletRequest request, HttpServletResponse response) throws ServletException {
            // TODO Auto-generated method stub
            return null;
        }
    }
    /**Object*/
    public static class InternalObjectActionInvoke extends InternalActionInvoke {
        private Object targetObject = null;
        public InternalObjectActionInvoke(String[] actionMethod, Object targetObject) {
            super(actionMethod);
            this.targetObject = targetObject;
        }
        @Override
        public Object invoke(HttpServletRequest request, HttpServletResponse response) throws ServletException {
            // TODO Auto-generated method stub
            return null;a
        }
    }
    /**ActionInvoke*/
    public static class InternalInvokeActionInvoke extends InternalActionInvoke {
        private ActionInvoke targetInvoke = null;
        public InternalInvokeActionInvoke(String[] actionMethod, ActionInvoke targetInvoke) {
            super(actionMethod);
            this.targetInvoke = targetInvoke;
        }
        @Override
        public Object invoke(HttpServletRequest request, HttpServletResponse response) throws ServletException {
            return this.targetInvoke.invoke(request, response);
        }
    }
}