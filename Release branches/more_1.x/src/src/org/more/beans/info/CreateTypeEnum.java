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
package org.more.beans.info;
/**
 * CreateTypeEnum枚举定义了【Factory、New】两种创建方式。
 * Factory方式：<br/>
 * 使用工厂方式创建一个Bean对象这种方式需要指定工厂类以及工厂方法相关参数，Factory方式中aop所能拦截到的方法与classcode工具的
 * {@link org.more.core.classcode.ClassEngine.BuilderMode#Propxy Propxt}方式相同。<br/>该方式需要beans配置createType属性为Factory，
 * 并且提供对象创建工厂时所依赖的工厂对象以及工厂方法。如果bean配置了aop或者附加接口实现则工厂bean返回的对象将由这个子系统创建一个这个对象的子类，
 * 并且以静态代理方式在代理类上实现aop以及附加接口实现。此时aop所能拦截到的方法与classcode工具的
 * {@link org.more.core.classcode.ClassEngine.BuilderMode#Propxy Propxt}方式相同（私有和保护方法将不受到aop影响，如果是new方式则可以受到影响）。<br/><br/>
 * New方式：<br/>
 * new方式是常规的执行构造方法来创建对象，如果bean没有配置构造方法则系统会调用{@link Class}的newInstance()方法创建对象。如果配置了构造方法，那么系统会自动
 * 寻找相关构造方法并且执行其构造方法（注意：默认不带参的构造方法可以不配置）。在首次找到相关类和构造方法之后这些信息会被缓存在{@link BeanDefinition}对象中。<br/>
 * 有关AOP或者附加接口实现。如果New方式创建的类配置了AOP或者接口实现则性能会大大下降，但是这个是在10万~100万个不同Class类对象上的测试结果，测试数据在下面会有介绍。
 * 在AOP或者附加接口配置下新的类对象与classcode工具的{@link org.more.core.classcode.ClassEngine.BuilderMode#Super Super}方式相同
 * （私有和保护方法将不受到aop影响，如果是new方式则可以受到影响）。
 * <br/>Date : 2009-11-12
 * @author 赵永春
 */
public enum CreateTypeEnum {
    /** 使用工厂方式创建bean。 */
    Factory,
    /** 通过调用构造方法创建对象。 */
    New
}
