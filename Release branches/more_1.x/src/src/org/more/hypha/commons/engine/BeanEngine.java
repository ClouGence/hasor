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
package org.more.hypha.commons.engine;
import java.util.HashMap;
import java.util.Map;
import org.more.RepeateException;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ValueMetaData;
import org.more.util.attribute.IAttribute;
/**
 * 该接口是基本的bean获取接口，该接口的职责是给定bean定义并且将这个bean定义创建出来。
 * @version : 2011-4-22
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class BeanEngine {
    private Map<String, AbstractBeanBuilder<AbstractBeanDefine>> beanBuilderMap = new HashMap<String, AbstractBeanBuilder<AbstractBeanDefine>>();
    private RootValueMetaDataParser                              rootParser     = null;
    //----------------------------------------------------------------------------------------------------------
    /**注册{@link ValueMetaDataParser}，如果注册的解析器出现重复则会引发{@link RepeateException}异常。*/
    public void regeditValueMetaDataParser(String metaDataType, ValueMetaDataParser<ValueMetaData> parser) {
        this.rootParser.addParser(metaDataType, parser);
    };
    /**解除注册{@link ValueMetaDataParser}，如果要移除的解析器如果不存在也不会抛出异常。*/
    public void unRegeditValueMetaDataParser(String metaDataType) {
        this.rootParser.removeParser(metaDataType);
    };
    /**
     * 注册一种bean定义类型，使之可以被引擎解析。如果重复注册同一种bean类型将会引发{@link RepeateException}类型异常。
     * @param beanType 注册的bean定义类型。
     * @param builder 要注册的bean定义生成器。
     */
    public void regeditBeanBuilder(String beanType, AbstractBeanBuilder<AbstractBeanDefine> builder) throws RepeateException {
        if (this.beanBuilderMap.containsKey(beanType) == false)
            this.beanBuilderMap.put(beanType, builder);
        else
            throw new RepeateException("不能重复注册[" + beanType + "]类型的BeanBuilder。");
    };
    /**解除指定类型bean的解析支持，无论要接触注册的bean类型是否存在该方法都会被正确执行。*/
    public void unRegeditBeanBuilder(String beanType) {
        if (this.beanBuilderMap.containsKey(beanType) == true)
            this.beanBuilderMap.remove(beanType);
    };
    /**获取指定名称的Bean生成器。*/
    protected AbstractBeanBuilder<AbstractBeanDefine> getBeanBuilder(String name) {
        return this.beanBuilderMap.get(name);
    };
    /**获取{@link ValueMetaData}元信息解析器。*/
    protected ValueMetaDataParser<ValueMetaData> getValueMetaDataParser() {
        return this.rootParser;
    };
    //----------------------------------------------------------------------------------------------------------
    /**初始化方法。 */
    public abstract void init(ApplicationContext context, IAttribute flash) throws Throwable;
    /**销毁方法。*/
    public void destroy() throws Throwable {}
    /**
     * 获取某个Bean的实例对象，该实例被创建时会根据其配置决定其创建是原型模式还是单态模式。 
     * 如果这个bean有属性的依赖注入则依赖注入也会在创建时进行。
     * @param define 要被创建的Bean定义。
     * @param objects 在获取bean实例时可能会传递的参数信息。
     */
    public abstract <T> T builderBean(AbstractBeanDefine define, Object[] params) throws Throwable;
    /**
     * 根据Bean名称获取其bean类型，该方法将返回在bean定义中配置的bean类型。
     * 那么getBeanType方法将返回生成的新类类型对象。
     * @param define 要被创建的Bean定义。
     * @param objects 在获取bean实例时可能会传递的参数信息。
     */
    public abstract Class<?> builderType(AbstractBeanDefine define, Object[] params) throws Throwable;
};