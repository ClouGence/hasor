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
package org.test.aop;
import org.hasor.context.anno.Bean;
import org.hasor.context.anno.Before;
@Bean("jobBean")
@Before(AopInterceptor_2.class)//使用@Before注解声明该方法需要拦截器
public class JobBean {
    @Before(AopInterceptor_1.class)//使用@Before注解声明该方法需要拦截器
    public String println(String msg) {
        return "println->" + msg;
    }
    public String foo(String msg) {
        return "foo->" + msg;
    }
}