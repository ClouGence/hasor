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
package org.custom;
import java.lang.reflect.Method;
import org.more.services.submit.impl.AbstractAC;
/**
 * 
 * @version : 2011-8-11
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class CustomAC extends AbstractAC {
    protected Object getBean(Method actionPath, String queryInfo) throws Throwable {
        Class<?> beanType = actionPath.getDeclaringClass();
        System.out.println("get Type:" + beanType);
        return beanType.newInstance();
    };
};