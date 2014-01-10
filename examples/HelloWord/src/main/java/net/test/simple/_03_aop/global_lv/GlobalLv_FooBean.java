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
package net.test.simple._03_aop.global_lv;
/**
 * 该 Bean 是一个普通 Pojo ，但是由于 GlobalInterceptor 
 * 拦截器配置的拦截范围包含了该 Bean 因此该类会被加装 Aop 切面。
 * @version : 2014-1-3
 * @author 赵永春(zyc@hasor.net)
 */
public class GlobalLv_FooBean {
    public void fooCall() {
        System.out.println("GlobalLv_FooBean.fooCall");
    }
}