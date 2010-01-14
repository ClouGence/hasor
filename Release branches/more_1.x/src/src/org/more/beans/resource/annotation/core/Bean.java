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
package org.more.beans.resource.annotation.core;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.more.beans.info.CreateTypeEnum;
import org.more.beans.info.IocTypeEnum;
/**
 * 
 * @version 2010-1-7
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE })
public @interface Bean {
    public String name() default ""; //在一个beanFactory中的唯一名称。
    public boolean lazyInit() default true; //是否延迟初始化这个bean，只有当bean是单态模式下才生效。
    public String description() default ""; //bean描述信息。
    //ioc依赖数据
    public IocTypeEnum iocType() default IocTypeEnum.Ioc; //依赖注入方式，InjectionFactory依赖这个属性，默认值为Ioc。
    public String exportRefBean() default ""; //依赖注入方式为export时导出的属性注入器bean名。
    //create依赖数据
    public CreateTypeEnum createType() default CreateTypeEnum.New; //创建方式，默认为New。
    public String factoryRefBean() default "";//使用工厂方式创建时的工厂bean名称。
    public boolean factoryIsStaticMethod() default true; //调用工厂类的方法是否为一个静态方法。
    public String factoryMethodName() default ""; //调用工厂类的方法名
    //aop依赖数据
    public String[] aopFiltersRefBean() default {}; //AOP过滤器bean，CreateTypeEnum如果为Factory方式则AOP使用代理方式创建，如果是New方式创建则创建模式使用Super
    //  public BeanInterface[] implImplInterface() default {}; //要附加实现的接口
    //附带数据
    public boolean isSingleton() default true; //是否为单态模式
}