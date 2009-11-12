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
    private static final long serialVersionUID  = 75468455223536954L;
    //基本数据
    private String            id                = null;              //
    private String            name              = null;              //
    private String            type              = null;              //
    private boolean           lazyInit          = false;             //
    private String            scope             = null;              //
    private String            description       = null;              //
    private BeanProperty[]    propertys         = null;              //
    private BeanConstructor   constructor       = null;              //
    //ioc依赖数据
    private IocTypeEnum       iocType           = null;              //
    private String            factIocRefBean    = null;              //
    //create依赖数据
    private String            factoryBean       = null;              //
    private String            factoryBeanMethod = null;              //
    private String            implDelegateBean  = null;              //
    //
    private BeanInterface[]   implImplInterface = null;              //
    //附带数据
    private boolean           isFactory         = false;             //
    private boolean           isSingleton       = false;             //
    private boolean           isPrototype       = false;             //
    private boolean           isInterface       = false;             //
    private boolean           isAbstract        = false;             //
    //=================================================================
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public boolean isLazyInit() {
        return lazyInit;
    }
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }
    public String getScope() {
        return scope;
    }
    public void setScope(String scope) {
        this.scope = scope;
    }
    public IocTypeEnum getIocType() {
        return iocType;
    }
    public void setIocType(IocTypeEnum iocType) {
        this.iocType = iocType;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getFactoryBean() {
        return factoryBean;
    }
    public void setFactoryBean(String factoryBean) {
        this.factoryBean = factoryBean;
    }
    public String getFactoryBeanMethod() {
        return factoryBeanMethod;
    }
    public void setFactoryBeanMethod(String factoryBeanMethod) {
        this.factoryBeanMethod = factoryBeanMethod;
    }
    public String getImplDelegateBean() {
        return implDelegateBean;
    }
    public void setImplDelegateBean(String implDelegateBean) {
        this.implDelegateBean = implDelegateBean;
    }
    public boolean isFactory() {
        return isFactory;
    }
    public void setFactory(boolean isFactory) {
        this.isFactory = isFactory;
    }
    public boolean isSingleton() {
        return isSingleton;
    }
    public void setSingleton(boolean isSingleton) {
        this.isSingleton = isSingleton;
    }
    public boolean isPrototype() {
        return isPrototype;
    }
    public void setPrototype(boolean isPrototype) {
        this.isPrototype = isPrototype;
    }
    public boolean isInterface() {
        return isInterface;
    }
    public void setInterface(boolean isInterface) {
        this.isInterface = isInterface;
    }
    public boolean isAbstract() {
        return isAbstract;
    }
    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }
    public BeanProperty[] getPropertys() {
        return propertys;
    }
    public void setPropertys(BeanProperty[] propertys) {
        this.propertys = propertys;
    }
    public BeanInterface[] getImplImplInterface() {
        return implImplInterface;
    }
    public void setImplImplInterface(BeanInterface[] implImplInterface) {
        this.implImplInterface = implImplInterface;
    }
    public String getFactIocRefBean() {
        return factIocRefBean;
    }
    public void setFactIocRefBean(String factIocRefBean) {
        this.factIocRefBean = factIocRefBean;
    }
    public BeanConstructor getConstructor() {
        return constructor;
    }
    public void setConstructor(BeanConstructor constructor) {
        this.constructor = constructor;
    }
}