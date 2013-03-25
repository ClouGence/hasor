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
package org.more.core.classcode;
/**
 * classcode使用的Method对象，该类中封装了aop代理方法和真实的目标方法。
 * @version 2010-9-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class Method {
    private java.lang.reflect.Method proxyMethod  = null;
    private java.lang.reflect.Method targetMeyhod = null;
    /**创建Method类型对象。*/
    Method(java.lang.reflect.Method proxyMethod, java.lang.reflect.Method targetMeyhod) {
        this.proxyMethod = proxyMethod;
        this.targetMeyhod = targetMeyhod;
    }
    /**获取aop代理方法，如果在aop期间再次调用该方法将会引发死循环。*/
    public java.lang.reflect.Method getProxyMethod() {
        return this.proxyMethod;
    }
    /**获取aop代理的方法的目标方法。*/
    public java.lang.reflect.Method getTargetMeyhod() {
        return this.targetMeyhod;
    }
}