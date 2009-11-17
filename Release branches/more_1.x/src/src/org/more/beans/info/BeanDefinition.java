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
import org.more.util.attribute.AttBase;
/**
 * 
 * Date : 2009-11-4
 * @author Administrator
 */
public class BeanDefinition extends AttBase {
    /**  */
    private static final long serialVersionUID      = 75468455223536954L;
    //基本数据
    private String            id                    = null;              //唯一的Bean ID。
    private String            name                  = null;              //在一个beanFactory中的唯一名称
    private String            type                  = null;              //定义这个bean的原始类型，该类型应该可以通过Class.forName获取。
    private boolean           lazyInit              = false;             //是否延迟初始化这个bean，只有当bean是单态模式下才生效。
    private String            scope                 = null;              //bean的作用范围。
    private String            description           = null;              //bean描述信息。
    //ioc依赖数据
    private IocTypeEnum       iocType               = null;              //依赖注入方式，InjectionFactory依赖这个属性
    private String            exportIocRefBean      = null;              //如果依赖注入方式为export则需要此属性的支持，这个属性表示一个外部属性注入器
    private BeanProperty[]    propertys             = null;              //bean中注册的属性这些属性需要依赖注入
    //create依赖数据
    private CreateTypeEnum    createType            = null;              //创建方式
    private BeanConstructor   constructor           = null;              //当创建方式为工厂方式时，构造方法将失效，一切创建代码委托给工厂方法。
    private String            factoryRefBean        = null;              //使用工厂方式创建时的工厂bean名称。
    private boolean           factoryIsStaticMethod = false;             //调用工厂类的方法是否为一个静态方法。
    private String            factoryMethodName     = null;              //调用工厂类的方法名
    private BeanProperty[]    factoryMethodParams   = null;              //调用工厂类的方法时需要传递的参数，参数依照数组中元素顺序。
    //aop依赖数据
    private String[]          aopFilterRefBean      = null;              //AOP过滤器bean，CreateTypeEnum如果为Factory方式则AOP使用代理方式创建，如果是New方式创建则创建模式使用Super
    private BeanInterface[]   implImplInterface     = null;              //要附加实现的接口
    //附带数据
    private boolean           isFactory             = false;             //
    private boolean           isSingleton           = false;             //
    private boolean           isPrototype           = false;             //
    private boolean           isInterface           = false;             //
    private boolean           isAbstract            = false;             //
    //=================================================================
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public boolean isLazyInit() {
        return lazyInit;
    }
    public String getScope() {
        return scope;
    }
    public String getDescription() {
        return description;
    }
    public IocTypeEnum getIocType() {
        return iocType;
    }
    public String getExportIocRefBean() {
        return exportIocRefBean;
    }
    public BeanProperty[] getPropertys() {
        return propertys;
    }
    public CreateTypeEnum getCreateType() {
        return createType;
    }
    public BeanConstructor getConstructor() {
        return constructor;
    }
    public String getFactoryRefBean() {
        return factoryRefBean;
    }
    public boolean isFactoryIsStaticMethod() {
        return factoryIsStaticMethod;
    }
    public String getFactoryMethodName() {
        return factoryMethodName;
    }
    public BeanInterface[] getImplImplInterface() {
        return implImplInterface;
    }
    public boolean isFactory() {
        return isFactory;
    }
    public boolean isSingleton() {
        return isSingleton;
    }
    public boolean isPrototype() {
        return isPrototype;
    }
    public boolean isInterface() {
        return isInterface;
    }
    public boolean isAbstract() {
        return isAbstract;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }
    public void setScope(String scope) {
        this.scope = scope;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setIocType(IocTypeEnum iocType) {
        this.iocType = iocType;
    }
    public void setExportIocRefBean(String exportIocRefBean) {
        this.exportIocRefBean = exportIocRefBean;
    }
    public void setPropertys(BeanProperty[] propertys) {
        this.propertys = propertys;
    }
    public void setCreateType(CreateTypeEnum createType) {
        this.createType = createType;
    }
    public void setConstructor(BeanConstructor constructor) {
        this.constructor = constructor;
    }
    public void setFactoryRefBean(String factoryRefBean) {
        this.factoryRefBean = factoryRefBean;
    }
    public void setFactoryIsStaticMethod(boolean factoryIsStaticMethod) {
        this.factoryIsStaticMethod = factoryIsStaticMethod;
    }
    public void setFactoryMethodName(String factoryMethodName) {
        this.factoryMethodName = factoryMethodName;
    }
    public void setImplImplInterface(BeanInterface[] implImplInterface) {
        this.implImplInterface = implImplInterface;
    }
    public void setFactory(boolean isFactory) {
        this.isFactory = isFactory;
    }
    public void setSingleton(boolean isSingleton) {
        this.isSingleton = isSingleton;
    }
    public void setPrototype(boolean isPrototype) {
        this.isPrototype = isPrototype;
    }
    public void setInterface(boolean isInterface) {
        this.isInterface = isInterface;
    }
    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }
    public String[] getAopFilterRefBean() {
        return aopFilterRefBean;
    }
    public void setAopFilterRefBean(String[] aopFilterRefBean) {
        this.aopFilterRefBean = aopFilterRefBean;
    }
    public BeanProperty[] getFactoryMethodParams() {
        return factoryMethodParams;
    }
    public void setFactoryMethodParams(BeanProperty[] factoryMethodParams) {
        this.factoryMethodParams = factoryMethodParams;
    }
}