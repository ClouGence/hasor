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
 * 属性注入器接口，当使用{@link IocTypeEnum#Fact fact}模式注入bean属性时。more.beans会生成一个专门负责注入属性的属性注入器。
 * 这个属性注入器是继承了即将注入属性的bean类型，同时实现了该接口，因此必须确保在{@link IocTypeEnum#Fact fact}模式下bean
 * 中不能存在该接口的方法或者标志实现该接口。FactIoc是more.beans用于{@link IocTypeEnum#Fact fact}方式注入的接口，
 * 该接口开发人员不会接触到它。仅当bean要求fact方式注入时more.beans会自动调用它。<br/>
 * 此外bean对象及时使用{@link IocTypeEnum#Fact fact}方式注入仍然不能强制类型转换bean对象类型为FactIoc，
 * 因为more.beans使用代理方式执行的注入原始对象不会被破坏。
 * @version 2009-11-8
 * @author 赵永春 (zyc@byshell.org)
 */
public interface FactIoc {
    /** 该方法的实现方法是生成的，生成的代码为原始的get/set方法调用。 */
    public void ioc(Object object, Object[] getBeanParam, BeanDefinition definition, ResourceBeanFactory context) throws Exception;
}