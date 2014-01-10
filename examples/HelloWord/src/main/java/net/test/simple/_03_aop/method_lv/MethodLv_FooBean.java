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
package net.test.simple._03_aop.method_lv;
import net.hasor.plugins.aop.Aop;
import net.test.simple._03_aop.SimpleInterceptor;
/**
 * 该例子演示了如何通过 {@code @Aop} 注解为某个 Bean 添加方法级别的拦截器。 
 * @version : 2014-1-3
 * @author 赵永春(zyc@hasor.net)
 */
public class MethodLv_FooBean {
    @Aop(SimpleInterceptor.class)
    public void fooCall() {
        System.out.println("MethodLv_FooBean.fooCall");
    }
}