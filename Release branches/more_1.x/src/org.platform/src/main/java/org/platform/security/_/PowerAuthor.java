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
package org.platform.security._;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
* 表示该类为用户授权请求处理类，该类需要实现{@link Author}接口。
* 通过该接口可以在不同的权限模型中实现用户的授权认证。
* @version : 2013-3-25
* @author 赵永春 (zyc@byshell.org)
*/
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface PowerAuthor {
    /**用户授权模块初始化参数。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public InitParam[] initParam() default {};
    /**当一个系统中存在多个授权模块时其检测顺序,多个授权模块可以用来实现多数据源。默认：0，数字越大启动越靠后。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public int sort() default 0;
    /**权限检查策略名。*/
    public String authorName();
}