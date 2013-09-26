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
package net.hasor.controller;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 表示一个控制器类，Controller类可以不必实现任何接口或继承任何类。
 * <PRE>
 * Example 1:
 * HTTP: /user/add/account/password
 * HTTP: /user/add.do?account=aa&password=pwd
 * @Controller("/user")
 *     public String add();
 * 
 * Example 2:
 * HTTP: /user/list/2010-02-13/2013-02-22/zyc/all
 * HTTP: /user/list.do?start=2010-02-13&end=2013-02-22&name=zyc&type=all
 * @Controller("/user") 
 *     public String list();
 * </PRE>
 * @version : 2013-3-26
 * @author 赵永春 (zyc@hasor.net)
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Controller {
    /**action地址空间，默认值为“/”*/
    public String value() default "/";
}