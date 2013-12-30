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
package net.hasor.plugins.aop;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 标记该类为全局Aop拦截器，该类需要实现 MethodInterceptor 接口。
 * @version : 2013-3-20
 * @author 赵永春 (zyc@hasor.net)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface GlobalAop {
    /**表达式*/
    public String value();
    /**表达式类型，默认是通配符*/
    public RegType regType() default RegType.Wildcard;
    /**该拦截器在所有全局拦截器链中的位置。*/
    public int index() default 0;
    //
    /**表达式类型*/
    public static enum RegType {
        /**正则表达式*/
        Regexp(),
        /**通配符*/
        Wildcard()
    }
}