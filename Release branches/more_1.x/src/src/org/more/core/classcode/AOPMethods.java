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
 * 该类型中存放了两个Method类型对象，分别是AOP代理方法和被代理的方法。代理方法负责触发AOP过滤器。
 * 而被代理方法则就是原始方法。该方法中可能包含了注解方面信息。
 * 该对象是当调用AOP方法时候动态类自动创建的类型。开发人员不必考虑创建细节。
 * Date : 2009-10-19
 * @author 赵永春
 */
public class AOPMethods {
    /** 原始方法，原始方法中包含了有效的注解信息。 */
    public final java.lang.reflect.Method method;
    /** 代理方法，如果执行代理方法的invoke将会出现递归调用。 */
    public final java.lang.reflect.Method propxyMethod;
    /** 创建AOPMethods对象。第一个参数是原始方法对象第二个参数是代理方法对象。 */
    public AOPMethods(java.lang.reflect.Method method, java.lang.reflect.Method propxyMethod) {
        this.method = method;
        this.propxyMethod = propxyMethod;
    }
}