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
import org.more.beans.core.ResourceBeanFactory;
import org.more.beans.core.injection.ExportInjectionProperty;
import org.more.util.attribute.AttBase;
/**
 * 一个Bean的完整定义信息，这个类是一个标准的pojo，more.beans可以通过不同方式提供这个bean定义。有了bean定义整个
 * more.beans就会工作起来。<br/>在BeanDefinition中一共分为六种数据，
 * 它们分别是【基本数据、ioc依赖数据、create依赖数据、aop依赖数据、附带数据、属性数据】下面分别介绍着六种数据。
 * <br/><br/>一、基本数据:<br/>
 * 基本数据中定义了bean的名字，类型是否延迟初始化，以及作用域和描述信息。其中name和type必须设置。lazyInit属性默认为true，
 * lazyInit的支持是在{@link ResourceBeanFactory}中实现的。
 * <br/><br/>二、ioc依赖数据:<br/>
 * ioc依赖数据中提供了对属性依赖注入的配置信息，iocType是指定的注入类型它是根据{@link IocTypeEnum}枚举定义。
 * 属性exportIocRefBean是当iocType类型为{@link IocTypeEnum#Export Export}方式时所依赖的数据表示导出的属性注入器，属性注入器必须是
 * {@link ExportInjectionProperty}接口对象。propertys则是需要注入的属性数组，所有对属性的配置都在这个数组中。
 * 使用Export方式时一切注入过程由{@link ExportInjectionProperty}接口 决定。iocType的默认值是{@link IocTypeEnum#Ioc Ioc}。
 * <br/><br/>三、create依赖数据:<br/>
 * 所有有关创建方面的数据都在这里它们包括了createType创建类型(默认是New)、constructor创建对象时调用的构造方法定义、factoryRefBean该属性是
 * 当createType为{@link CreateTypeEnum#Factory Factory}时所依赖，表示了使用的工厂bean名称、factoryIsStaticMethod表示所使用的工厂方法是否为静态方法。
 * 针对静态工厂方法，在Factory方式中不会去创建Factory对象但这需要给定factoryIsStaticMethod属性、factoryMethodName工厂方法名、
 * factoryMethodParams调用工厂方法时需要传递的参数信息。
 * <br/><br/>四、aop依赖数据:<br/>
 * 配置了aop链相关信息。同时这类数据中还包含了附加接口实现相关数据。aopFilterRefBean代表AOP方法过滤器bean名称。
 * 而implImplInterface则是附加接口实现相关数据。
 * <br/><br/>五、附带数据:<br/>
 * 附带数据中有一个最重要的属性就是isSingleton它决定了{@link ResourceBeanFactory ResourceBeanFactory}是否对其进行缓存。
 * <br/><br/>六、属性数据:<br/>
 * 属性数据是由BeanDefinition继承的{@link AttBase AttBase}类提供支持，通常这些属性都是配置的附加属性信息。注意所有属性请不要使用“$more_”作为属性开头。
 * “$more_”是more.beans属性名称的保留区域，有些用于提升性能的缓存数据都是使用这个保留区域的名称存放的。
 * <br/>Date : 2009-11-17
 * @author 赵永春
 */
public class BeanDefinition extends Prop {
    //========================================================================================Field
    /**  */
    private static final long serialVersionUID      = 75468455223536954L;
    //基本数据
    private String            name                  = null;              //在一个beanFactory中的唯一名称。
    private boolean           lazyInit              = true;              //是否延迟初始化这个bean，只有当bean是单态模式下才生效。
    private String            description           = null;              //bean描述信息。
    //ioc依赖数据
    private IocTypeEnum       iocType               = IocTypeEnum.Ioc;   //依赖注入方式，InjectionFactory依赖这个属性，默认值为Ioc。
    private String            exportIocRefBean      = null;              //依赖注入方式为export时导出的属性注入器bean名。
    private BeanProperty[]    propertys             = null;              //bean中注册的属性这些属性需要依赖注入。
    //create依赖数据
    private CreateTypeEnum    createType            = CreateTypeEnum.New; //创建方式，默认为New。
    private BeanConstructor   constructor           = null;              //配置的构造方法。当创建方式为工厂方式时，构造方法将失效，一切创建代码委托给工厂方法。
    private String            factoryRefBean        = null;              //使用工厂方式创建时的工厂bean名称。
    private boolean           factoryIsStaticMethod = false;             //调用工厂类的方法是否为一个静态方法。
    private String            factoryMethodName     = null;              //调用工厂类的方法名
    private BeanProperty[]    factoryMethodParams   = null;              //调用工厂类的方法时需要传递的参数，参数依照数组中元素顺序。
    //aop依赖数据
    private String[]          aopFilterRefBean      = null;              //AOP过滤器bean，CreateTypeEnum如果为Factory方式则AOP使用代理方式创建，如果是New方式创建则创建模式使用Super
    private BeanInterface[]   implImplInterface     = null;              //要附加实现的接口
    //附带数据
    private boolean           isSingleton           = false;             //是否为单态模式
    //==========================================================================================Job
    /**获取在一个beanFactory中的唯一名称。*/
    public String getName() {
        return name;
    }
    /**设置在一个beanFactory中的唯一名称。*/
    public void setName(String name) {
        this.name = name;
    }
    /**获取是否延迟初始化这个bean，只有当bean是单态模式下才生效。*/
    public boolean isLazyInit() {
        return lazyInit;
    }
    /**设置是否延迟初始化这个bean，只有当bean是单态模式下才生效。*/
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }
    /**获取bean描述信息。*/
    public String getDescription() {
        return description;
    }
    /**设置bean描述信息。*/
    public void setDescription(String description) {
        this.description = description;
    }
    /**获取依赖注入方式，InjectionFactory依赖这个属性，默认值为Ioc。*/
    public IocTypeEnum getIocType() {
        return iocType;
    }
    /**设置依赖注入方式，InjectionFactory依赖这个属性，默认值为Ioc。*/
    public void setIocType(IocTypeEnum iocType) {
        this.iocType = iocType;
    }
    /**获取依赖注入方式为export时导出的属性注入器bean名。*/
    public String getExportIocRefBean() {
        return exportIocRefBean;
    }
    /**设置依赖注入方式为export时导出的属性注入器bean名。*/
    public void setExportIocRefBean(String exportIocRefBean) {
        this.exportIocRefBean = exportIocRefBean;
    }
    /**获取bean中注册的属性这些属性需要依赖注入。*/
    public BeanProperty[] getPropertys() {
        return propertys;
    }
    /**设置bean中注册的属性这些属性需要依赖注入。*/
    public void setPropertys(BeanProperty[] propertys) {
        this.propertys = propertys;
    }
    /**获取创建方式，默认为New。*/
    public CreateTypeEnum getCreateType() {
        return createType;
    }
    /**设置创建方式，默认为New。*/
    public void setCreateType(CreateTypeEnum createType) {
        this.createType = createType;
    }
    /**获取Bean在New模式下创建的构造方法配置。*/
    public BeanConstructor getConstructor() {
        return constructor;
    }
    /**设置Bean在New模式下创建的构造方法配置。*/
    public void setConstructor(BeanConstructor constructor) {
        this.constructor = constructor;
    }
    /**获取使用工厂方式创建时的工厂bean名称。*/
    public String getFactoryRefBean() {
        return factoryRefBean;
    }
    /**设置使用工厂方式创建时的工厂bean名称。*/
    public void setFactoryRefBean(String factoryRefBean) {
        this.factoryRefBean = factoryRefBean;
    }
    /**获取调用工厂类的方法是否为一个静态方法。*/
    public boolean isFactoryIsStaticMethod() {
        return factoryIsStaticMethod;
    }
    /**设置调用工厂类的方法是否为一个静态方法。*/
    public void setFactoryIsStaticMethod(boolean factoryIsStaticMethod) {
        this.factoryIsStaticMethod = factoryIsStaticMethod;
    }
    /**获取调用工厂类的方法名。*/
    public String getFactoryMethodName() {
        return factoryMethodName;
    }
    /**设置调用工厂类的方法名。*/
    public void setFactoryMethodName(String factoryMethodName) {
        this.factoryMethodName = factoryMethodName;
    }
    /**获取调用工厂类的方法时需要传递的参数配置。*/
    public BeanProperty[] getFactoryMethodParams() {
        return factoryMethodParams;
    }
    /**设置调用工厂类的方法时需要传递的参数配置。*/
    public void setFactoryMethodParams(BeanProperty[] factoryMethodParams) {
        this.factoryMethodParams = factoryMethodParams;
    }
    /**获取AOP过滤器bean名称数组。*/
    public String[] getAopFilterRefBean() {
        return aopFilterRefBean;
    }
    /**设置AOP过滤器bean名称数组。*/
    public void setAopFilterRefBean(String[] aopFilterRefBean) {
        this.aopFilterRefBean = aopFilterRefBean;
    }
    /**获取要附加实现的接口。*/
    public BeanInterface[] getImplImplInterface() {
        return implImplInterface;
    }
    /**设置要附加实现的接口。*/
    public void setImplImplInterface(BeanInterface[] implImplInterface) {
        this.implImplInterface = implImplInterface;
    }
    /**获取是否为单态模式。*/
    public boolean isSingleton() {
        return isSingleton;
    }
    /**设置是否为单态模式。*/
    public void setSingleton(boolean isSingleton) {
        this.isSingleton = isSingleton;
    }
}