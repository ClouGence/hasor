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
package org.more.hypha.define.anno;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.more.core.classcode.BuilderMode;
/**
 * 该bean使用aop增强。
 * @version 2010-10-13
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Aop {
    /**确定该bean使用的aop配置是哪个，如果配置了该属性那么其他配置将全部失效。*/
    public String useConfig() default "";
    /**该属性可以通来定义informeds属性所表示的切入点集合所使用的默认aop切入点表达式，默认值是*。*/
    public String defaultPointcut() default "*";
    /**该类描述作用于这些切点*/
    public AopInformed[] informeds() default {};
    /**生成类时候使用的生成模式。*/
    public BuilderMode mode() default BuilderMode.Super;
}