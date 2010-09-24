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
package org.more.beans;
import org.more.util.attribute.IAttribute;
/**
 * 该接口用于定义一个beans组建中的bean声明。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public interface AbstractBeanDefine extends IAttribute, DefineConfig {
    /**返回bean的名称，在同一个Factory中name是唯一的。*/
    public String getName();
    /**获取bean的各种属性是如何注入到Bean中的。*/
    public IocTypeEnum getIocType();
    /**获取bean的作用域，如果容器支持多种作用域。*/
    public String getScope();
    /**返回一个boolean值，表示类是否为一个抽象类。*/
    public boolean isAbstract();
    /**返回一个boolean值，表示类是否为一个接口。*/
    public boolean isInterface();
    /**返回一个boolean值，表示这个bean是否为单态的。*/
    public boolean isSingleton();
    /**返回一个boolean值，表示这个bean是否为延迟装载的。*/
    public boolean isLazyInit();
    /**返回bean的描述信息。*/
    public String getDescription();
    /**创建bean的工厂名，如果bean定义时没有指定工厂则该方法返回null。*/
    public String factoryName();
    /**该方法与factoryName()方法是成对出现的，该方法表明目标方法的方法名称描述。*/
    public String factoryMethod();
    /**该属性定义了当创建这个bean时候需要的启动参数。启动参数通常是指构造方法参数，对于工厂形式创建启动参数代表了工厂方法的参数列表。*/
    public AbstractPropertyDefine[] getInitParams();
    /**返回bean的定义属性集合。*/
    public AbstractPropertyDefine[] getPropertys();
    /**返回具有特征的字符串。*/
    public String toString();
}