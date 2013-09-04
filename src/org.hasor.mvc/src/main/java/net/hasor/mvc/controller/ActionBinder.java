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
package net.hasor.mvc.controller;
import java.lang.reflect.Method;
/***
 * 
 * @version : 2013-5-11
 * @author 赵永春 (zyc@hasor.net)
 */
public interface ActionBinder {
    /**绑定一个命名空间*/
    public NameSpaceBindingBuilder bindNameSpace(String path);
    /**用来绑定一个命名空间*/
    public static interface NameSpaceBindingBuilder {
        public String getNameSpace();
        /**注册一个Action，将action绑定到方法上。*/
        public ActionBindingBuilder bindActionClass(Class<?> targetClass);
        /**注册一个Action，将action绑定到方法上。*/
        public ActionBindingBuilder bindActionMethod(Method targetMethod);
    }
    /**用来绑定action的执行目标*/
    public static interface ActionBindingBuilder {
        /**将action绑定的Http方法上。*/
        public ActionBindingBuilder onHttpMethod(String httpMethod);
        /**将设置返回的MimeType。*/
        public ActionBindingBuilder returnMimeType(String mimeType);
        /**restful风格的映射*/
        public void mappingRestful(String restfulMapping);
        /**Action方法的对象*/
        public void toInstance(Object targetAction);
    }
}