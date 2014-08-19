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
/**
字节码生成工具，该工具可以在已有类型上附加接口实现，还可以对类对象提供AOP的支持，ClassEngine提供了两种工作模式。
classcode内核使用的是ASM3.2框架。ASM是目前最轻量化的字节码操作框架。大名顶顶的Hibernate、Spring就使用的ASM作为字节码内核。
此外它是一个字节码工具与cglib一样，都是通过修改字节码来达到目的。<br/>
<b>引擎说明：</b><div style=" margin-left: 20px;">
引擎类装载器，classcode目前支持通过类装载器来装载新类的基类类型。在装载这个基类类型时可以通过引擎提供的类装载器装载。
</div>
<b>一、工作模式：</b><div style=" margin-left: 20px;">
<i><b>{@link org.more.classcode.BuilderMode#Super Super}：</b></i><br/>
使用继承方式实现新类，这种生成模式下必须要求先有类型后有对象。生成的新类是继承原有类实现的，所有附加方法都写到新类中。
原始类中的所有方法都被重写并且以super形式调用父类。私有方法不包括重写范畴。同时如果配置了aop特性private方法将不参与Aop装配。
<span style="font-size: 12px; font-weight: bold; color: #999;"><i>在该模式下只有public和protected方法参与Aop装配。所有不能参与aop的方法aop策略接口将不会发现它。</i></span><br/>
<i><b>{@link org.more.classcode.BuilderMode#Propxy Propxy}：</b></i><br/>
使用代理方式实现新类，这种生成模式的特点是可以在对象上直接使用ClassCode的功能无需从新创建一个新的类型并且去构造它。
其整个实现方式就是一个静态代理方式实现。不过需要注意的是代理对象的类型不等于原始类型，它会把原始类中的所有构造方法
全部删除取而代之的是生成一个只有一个参数的构造方法，该参数类型就是基类类型。所有方法调用都使用这个注入的类型对象调用。
<span style="font-size: 12px; font-weight: bold; color: #999;"><i>在该模式下只有public方法参与Aop装配，private方法和protected的方法因访问权限问题不能参与Aop，aop策略接口不会发现这些被忽略的方法。</i></span><br/></div>
<b>二、生成策略：</b><div style=" margin-left: 20px;">
classcode提供了下面5个种策略来控制字节码的生成，
{@link org.more.classcode.ClassNameStrategy ClassNameStrategy}策略接口负责新生成的类名称和包名。
{@link org.more.classcode.DelegateStrategy DelegateStrategy}策略接口负责委托的策略，通过委托策略可以决定是否忽略该委托。
{@link org.more.classcode.MethodStrategy MethodStrategy}方法忽略策略接口，可以确定这个方法是否忽略，如果被忽略则生成的新类中不会包含该方法的定义。
{@link org.more.classcode.PropertyStrategy PropertyStrategy}策略是基本属性和委托属性的策略接口，这些附加的属性在输出到class之前都会通过该接口来确定是否忽略它以及属性的读写策略。
{@link org.more.classcode.AopStrategy AopStrategy}Aop生成策略，可以通过该接口来确定aop方面的生成策略才会生效。
</div>
<b>三、委托：</b><div style=" margin-left: 20px;">
委托的作用是在已有类或对象上，附加一个接口实现。而原始java代码不必做任何修改或编译。{@link org.more.classcode.MethodDelegate MethodDelegate}附加的接口实现方法都是通过该接口实现。
</div>
<b>四、动态属性：</b><div style=" margin-left: 20px;">
可以提供该功能在已有类或者对象上动态的添加一个get/set属性而无需在原始类上修改。这个功能生成的属性与Java Beans的标准get/set一致。但是boolean类型classcode也使用的是get开头而不是is开头。
</div>
<b>五、委托属性：</b><div style=" margin-left: 20px;">
这是一个高级的属性生成方式传统的属性在get/set方法中只是对某一个属性字段做读写属性。而委托属性的工作原理是通过导出get/set方法到一个接口中来增强对某一个属性的行为。
生成的委托属性在其类中不会保存一个属性相关的字段，而是换做一个{@link org.more.classcode.PropertyDelegate PropertyDelegate}接口。
该接口上定义了两个主要方法分别对应属性的get/set行为，属性类型由getType方法确定。
</div>
<b>六、AOP支持：</b><div style=" margin-left: 20px;">
在classcode v2.0中新增了三个切面的事件通知接口，这三个切面分别是
{@link org.more.classcode.AopBeforeListener before}、
{@link org.more.classcode.AopReturningListener returning}、
{@link org.more.classcode.AopThrowingListener throwing}。
这三个切面可以用于Aop通知。classcode在v1.0版本中就开始提供{@link org.more.classcode.AopInvokeFilter AopInvokeFilter}接口
通过这个接口除了可以完成上述三个切面的功能之外还可以影响aop方法的返回结果。由于其原理是Filter方式因此aop链可以无限制的挂载。<br/>
</div>
<b>七、ASM扩展：</b><div style=" margin-left: 20px;">
classcode在封装的同时提供了来自于底层的支持，可以使用ASM3.2框架直接从底层支持扩展。也可以通过继承ClassBuilder类在子类中重新生成字节码并且使用这个新字节码。
</div>
 */
package org.more.classcode;