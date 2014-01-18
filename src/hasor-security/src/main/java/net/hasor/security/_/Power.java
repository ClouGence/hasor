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
 * 权限配置，可以配置到类级别和方法级别上。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Power {
    /**权限点代码，如果没有填写权限点code则权限检测要求仅仅检测是否登陆。*/
    public String[] value() default "";
    /**权限认证等级，*/
    public Level level() default Level.NeedLogin;
    /**当调用失败时返回的异常内容。*/
    public String errorMsg() default "";
    /**
     * 认证级别枚举
     * @version : 2013-3-12
     * @author 赵永春 (zyc@byshell.org)
     */
    public static enum Level {
        /**自由访问。Level 0*/
        Free,
        /**需要经过登陆。Level 1*/
        NeedLogin,
        /**需要检查权限点。Level 2 */
        NeedAccess
    }
}