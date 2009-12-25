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
package org.more.beans.core.propparser;
import org.more.beans.core.ResourceBeanFactory;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.BeanProp;
import org.more.beans.info.BeanProperty;
/**
 * 属性解析器接口，其实现类决定解析那种属性类型（{@link BeanProp}类的子类）。在软件包propparser中已经针对info软件包已经定义的属性定义类型进行了一对一实现。
 * <br/>Date : 2009-11-18
 * @author 赵永春
 */
public interface PropertyParser {
    /**
     * 解析属性并且返回对属性片段的解析结果。
     * @param context 当前要解析的属性是哪个对象上的属性，如果解析的属性是构造方法属性则由于目标对象还没有创建所以在构造方法属性中context是null。除此之外工厂方法也一样。
     * @param contextParams 创建bean时候附带的环境参数，这个参数是ResourceBeanFactory.getBean()时传递的参数。
     * @param prop 要解析的属性片段。
     * @param propContext 该属性属性片段所属的那个{@link BeanDefinition}中定义的属性对象。如果属性片段有多个层次结构但是解析这些片段之后的需要注入的属性却是不变的，这个属性的定义就是该参数。
     * @param definition 参数propContext所属的那个{@link BeanDefinition}对象。
     * @param factory 当解属性片段是引用类型时则需要返回引用的对象这时就需要{@link ResourceBeanFactory}类型对象的依赖。
     * @param contextParser 当解析某一个属性类型时需要传递一个整个属性解析器的入口对象。
     * @return 返回解析的结果。
     * @throws Exception 如果解析期间发生异常。
     */
    public Object parser(Object context, Object[] contextParams, BeanProp prop, BeanProperty propContext, BeanDefinition definition, ResourceBeanFactory factory, PropertyParser contextParser) throws Exception;
    /**
     * 解析属性类型并且返回解析的属性类型结果。
     * @param context 当前要解析的属性是哪个对象上的属性，如果解析的属性是构造方法属性则由于目标对象还没有创建所以在构造方法属性中context是null。除此之外工厂方法也一样。
     * @param contextParams 创建bean时候附带的环境参数，这个参数是ResourceBeanFactory.getBean()时传递的参数。
     * @param prop 要解析的属性片段。
     * @param propContext 该属性属性片段所属的那个{@link BeanDefinition}中定义的属性对象。如果属性片段有多个层次结构但是解析这些片段之后的需要注入的属性却是不变的，这个属性的定义就是该参数。
     * @param definition 参数propContext所属的那个{@link BeanDefinition}对象。
     * @param factory 当解属性片段是引用类型时则需要返回引用的对象这时就需要{@link ResourceBeanFactory}类型对象的依赖。
     * @param contextParser 当解析某一个属性类型时需要传递一个整个属性解析器的入口对象。
     * @return 返回解析的结果。
     * @throws Exception 如果解析期间发生异常。
     */
    public Class<?> parserType(Object context, Object[] contextParams, BeanProp prop, BeanProperty propContext, BeanDefinition definition, ResourceBeanFactory factory, PropertyParser contextParser) throws Exception;
}