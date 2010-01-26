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
package org.more.beans.resource.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.more.beans.info.IocTypeEnum;
/**
 * 配置该类是一个Bean。该注解可以配置一些bean的基本参数。如果该类包含了构造方法那么beans会搜索构造方法
 * 及其相关参数，注意如果类存在多个构造方法beans只会选取第一个。对于aop的过滤器也可以通过该注解配置。
 * @version 2010-1-7
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE })
public @interface Bean {
    /**用于配置bean的名称，如果没有配置bean名称则bean名称是以其Class的简短类名(首字母小写)来定义。*/
    public String name() default "";
    /**是否延迟初始化这个bean，只有当bean是单态模式下才生效。默认该配置是true。*/
    public boolean lazyInit() default true;
    /**依赖注入方式，InjectionFactory依赖这个属性，默认值为Ioc。*/
    public IocTypeEnum iocType() default IocTypeEnum.Ioc;
    /**依赖注入方式为export时导出的属性注入器bean名。*/
    public String exportRefBean() default "";
    /**AOP过滤器bean，CreateTypeEnum如果为Factory方式则AOP使用代理方式创建，如果是New方式创建则创建模式使用Super*/
    public String[] aopFiltersRefBean() default {};
    /**是否为单态模式，默认为true*/
    public boolean isSingleton() default true;
}