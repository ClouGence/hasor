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
package org.more.hypha.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 标记该类是“菌丝”中的一个有效Bean。
 * @version 2010-10-13
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Bean {
    /**配置bean的id，如果配置了没有特殊配置id属性则id属性等同name。*/
    public String id() default "";
    /**配置bean的名称，如果没有配置bean名称则bean名称是以其Class的类名来定义。*/
    public String name() default "";
    /**逻辑包名，该配置允许在不影响bean已所属某个包的情况下去分配它属于另外一个包的权利。如果没有指定该属性值那么该值将会采用当前类所属的真实包名作为值。*/
    public String logicPackage() default "";
    /**该Bean所支持的作用域。*/
    public String scope() default "";
    /**是否为单态模式，默认为true*/
    public boolean isSingleton() default true;
    /**是否延迟初始化这个bean，只有当bean是单态模式下才生效。默认该配置是true。*/
    public boolean lazyInit() default true;
    /**如果需要使用工厂方式初始化该Bean，则咋该处需要指明工厂的Bean名称或ID。*/
    public String factoryName() default "";
    /**获取工厂bean在创建bean时使用的方法，这个方法需要是一个无参的方法。*/
    public String factoryMethod() default "";
    /**如果配置文件中配置了某个模板bean那么就可以使用该属性来配置模板以进一步减少注解配置方式。*/
    public String useTemplate() default "";
    /**注释。*/
    public String description() default "";
}