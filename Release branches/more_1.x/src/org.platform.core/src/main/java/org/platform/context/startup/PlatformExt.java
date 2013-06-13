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
package org.platform.context.startup;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.platform.context.PlatformListener;
/**
 * 标志该类注册到系统初始化过程，该类在标记注解时必须实现{@link PlatformListener}接口。
 * @version : 2013-3-20
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface PlatformExt {
    /**默认名称，该名称在系统控制台用于管理显示用途。*/
    public String displayName() default "";
    /**对该类的描述信息。*/
    public String description() default "";
    /**启动顺序默认值0，该值越大表示启动顺序越延后。提示：负值等同于“0”。*/
    public int startIndex() default 0;
    /*级别定义*/
    public static int Lv_0 = Integer.MIN_VALUE + 0;
    public static int Lv_1 = Integer.MIN_VALUE + 100;
    public static int Lv_2 = Integer.MIN_VALUE + 200;
    public static int Lv_3 = Integer.MIN_VALUE + 300;
    public static int Lv_4 = Integer.MIN_VALUE + 400;
    public static int Lv_5 = Integer.MIN_VALUE + 500;
    public static int Lv_6 = Integer.MIN_VALUE + 600;
    public static int Lv_7 = Integer.MIN_VALUE + 700;
    public static int Lv_8 = Integer.MIN_VALUE + 800;
    public static int Lv_9 = Integer.MIN_VALUE + 900;
}