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
package org.more.classcode;
/**
 * 该枚举定义了ClassCode的工作模式。<br/>BuilderMode枚举中定义了{@link BuilderMode#Propxy}和{@link BuilderMode#Super}两种模式。
 * 这两种模式可以大多数应付各种紧急情况和需求。在Propxy模式下可以在不重新构造对象的情况下附加新的属性、代理属性、委托。而Super模式是传统的
 * 继承父类改写方式来实现。当然无论是Propxy模式还是Super模式他们都可以支持相同功能的aop特性，只不过这需要受到java虚拟机访问修饰符的制约。
 * @version 2010-9-3
 * @author 赵永春 (zyc@hasor.net)
 */
public enum BuilderMode {
    /**
     * 使用继承方式实现新类，这种生成模式下必须要求先有类型后有对象。生成的新类是继承原有类实现的，所有附加方法都写到新类中。
     * 原始类中的所有方法都被重写并且以super形式调用父类。私有方法不包括重写范畴。同时如果配置了aop特性private方法将不参与Aop装配。
     * <br/>在该模式下只有public和protected方法参与Aop装配。所有不能参与aop的方法，aop策略接口不会发现这个方法。
     */
    Super,
    /**
     * 使用代理方式实现新类，这种生成模式的特点是可以在对象上直接使用ClassCode的功能无需从新创建一个新的类型并且去构造它。
     * 其整个实现方式就是一个静态代理方式实现。不过需要注意的是代理对象的类型不等于原始类型，它会把原始类中的所有构造方法
     * 全部删除取而代之的是生成一个只有一个参数的构造方法，该参数类型就是基类类型。所有方法调用都使用这个注入的类型对象调用。
     * <br/>在该模式下只有public方法参与Aop装配，private方法和protected的方法因访问权限问题不能参与Aop，aop策略接口不会发现这些被忽略的方法。
     */
    Propxy
}