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
package net.hasor.security._;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
* 表示该类为用户认证服务，该类需要实现{@link SecurityAuth}接口。
* @version : 2013-3-25
* @author 赵永春 (zyc@byshell.org)
*/
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface SecAuth {
    /**出现同名时的排序顺序。（越小越优先）。*/
    public int sort() default Integer.MAX_VALUE;
    /**认证系统名。*/
    public String authSystem();
}