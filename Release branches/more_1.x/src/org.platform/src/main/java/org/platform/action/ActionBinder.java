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
package org.platform.action;
import java.lang.reflect.Method;
/***
 * 
 * @version : 2013-5-11
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ActionBinder {
    /**绑定一个命名空间*/
    public NameSpaceBindingBuilder bindNameSpace(String path);
    /**用来绑定一个命名空间*/
    public static interface NameSpaceBindingBuilder {
        public String getNameSpace();
        /**注册一个Action*/
        public ActionBindingBuilder bindAction(String actionName);
    }
    /**用来绑定action的执行目标*/
    public static interface ActionBindingBuilder {
        public String getActionName();
        /**将action绑定的Http方法上。*/
        public ActionBindingBuilder onMethod(String httpMethod);
        /**将action绑定到方法上。*/
        public void bindClass(Class<?> targetClass);
        /**将action绑定到方法上。*/
        public void bindObject(Object targetObject);
        /**将action绑定到方法上。*/
        public void bindMethod(Method targetMethod);
        /**将action绑定到{@link ActionInvoke}接口。*/
        public void bindActionInvoke(ActionInvoke targetInvoke);
    }
}