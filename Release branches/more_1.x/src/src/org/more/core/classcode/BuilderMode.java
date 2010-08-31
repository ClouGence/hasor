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
package org.more.core.classcode;
/** ClassEngine引擎类生成模式。 */
public enum BuilderMode {
    /**
     * 继承方式实现新类，这种生成模式下必须要求先有类型后有对象。生成的新类是继承原有类实现的，
     * 所有附加方法都写到新类中。原始类中的所有方法都被重写并且以super形式调用父类。私有方法不包括重写范畴。
     * 私有方法将不参与AOP功能。在继承模式下保护方法与公共方法参与AOP功能。
     */
    Super,
    /**
     * 代理方式实现新类，这种生成模式下可以在已有的对象上附加接口实现而不需要重新创建对象。同时生成的新对象
     * 不破坏原有对象。整个实现方式就是一个静态代理方式实现。注意这种生成方式会取消所有原始类中的构造方法。
     * 取而代之的是生成一个一个参数的构造方法，该参数类型就是基类类型。所有方法调用都使用这个注入的类型对象调用。
     * 同时该中生成方式的私有方法不包括重写范畴。<br/>
     * 在代理模式下只有公共方法参与AOP功能，私有方法和受保护的方法因访问权限问题不能参与AOP。
     */
    Propxy
}
