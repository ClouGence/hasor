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
package org.more.beans.core.injection;
import org.more.beans.core.ResourceBeanFactory;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.IocTypeEnum;
/**
 * FactIoc
 * bean注入处理器，more.beans是通过该接口实现对属性进行注入控制。在more.beans中预定义了
 * {@link IocTypeEnum#Export Export}、{@link IocTypeEnum#Fact Fact}、{@link IocTypeEnum#Ioc Ioc}三种属性注入方式。
 * <br/><br/>Export方式：<br/>使用Export方式可以使开发人员参与属性注入过程，在Export方式下more.beans不会对属性做任何注入操作。
 * more.bean会委托{@link ExportInjectionProperty}接口进行注入请求。决定对属性的注入类必须实现{@link ExportInjectionProperty}接口。
 * 如果外部注入处理对象为空则Export将忽略注入请求。
 * <br/><br/>Fact方式：<br/>在Fact方式下首次请求装载类时more.beans会生成一个属性注入类，并且使用这个属性注入类进行注入。
 * 这个属性注入类的代码是完全由more.classcode工具生成，生成的类代码使用最原始的方式对bean进行get/set。
 * Fact方式比较Ioc方式省略了反射注入的过程，Fact采用直接调用方法进行属性注入，从而增加运行速度。经过测试
 * fact方式的运行速度与原始get/set运行速度相当接近，100万次进行基本类型属性注入速度只相差15毫秒落后。
 * 在1000万次注入测试下get/set消耗了312毫秒而fact消耗了843毫秒，ioc方式则需要消耗18.3秒。
 * 这可以证明在Fact方式下会有很好的属性注入运行效率，但是Fact也会对每个要求Fact的bean生成一个注入器。
 * 这也就是说在fact方式下会比ioc方式增加少量内存消耗。生成的注入器被保存在{@link BeanDefinition}的属性中。
 * 只有{@link BeanDefinition}对象被缓存才有上述运行效率，否则fact的效率可能远远不足ioc。
 * <br/><br/>Ioc方式：<br/>传统的注入方式，使用java.lang.reflect包中的类进行反射调用来实现依赖注入。Ioc方式比较Fact方式运行效率要慢的多。
 * @version 2009-11-7
 * @author 赵永春 (zyc@byshell.org)
 */
public interface Injection {
    /**
     * 请求执行对目标bean对象进行依赖注入。具体注入方式由Injection接口的实现类决定。
     * 如果开发人员使用的Bean容器对象是{@link org.more.beans.BeanContext}那么context就是使用的哪个BeanContext对象。
     * @param object 等待被注入的对象。
     * @param params 当编程方式调用getbean时传递的参数对象
     * @param definition 等待被注入的对象定义。
     * @param context 注入对象所处的Bean工厂。
     * @return 返回注入完毕的对象。
     * @throws Exception 如果在注入期间发生异常。
     */
    public Object ioc(Object object, Object[] params, BeanDefinition definition, ResourceBeanFactory context) throws Exception;
}