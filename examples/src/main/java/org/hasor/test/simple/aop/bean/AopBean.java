/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package org.hasor.test.simple.aop.bean;
import net.hasor.plugins.aop.Aop;
import org.hasor.test.simple.aop.interceptor.AopInterceptor_A;
import org.hasor.test.simple.aop.interceptor.AopInterceptor_Font;
import org.hasor.test.simple.aop.interceptor.AopInterceptor_Span;
/**
 * 
 * @version : 2013-8-11
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
@Aop(AopInterceptor_A.class)
public class AopBean {
    @Aop({ AopInterceptor_Span.class, AopInterceptor_Font.class })
    public String fooA(String param1) {
        System.out.println("invoke fooA");
        return "fooA";
    }
    public String fooB(String param1) {
        System.out.println("invoke fooB");
        return "fooB";
    }
}