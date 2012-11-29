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
package org.more.hypha.define;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.hypha.utils.DefineUtils;
/**
 * TemplateBeanDefine类用于定义一个bean的模板。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class BeanDefine extends AbstractDefine {
    /**Base Info*/
    //
    /*每个Bean唯一的ID值。*/
    private String                      id            = null;
    /*名称,在不同的scope下可以重复定义。*/
    private String                      name          = null;
    /*所属的作用域。*/
    private String                      scope         = null;
    /*初始化参数&构造方法参数*/
    private List<ParamDefine>           initParams    = new ArrayList<ParamDefine>();
    /*应用的模板*/
    private String                      useTemplate   = null;
    /*------------------------------------------------------------------*/
    /**Mark Info*/
    //
    /*抽象标志*/
    private boolean                     abstractMark  = false;
    /*单态标志*/
    private boolean                     singletonMark = false;
    /*延迟装载标志*/
    private boolean                     lazyMark      = true;
    /*描述信息*/
    private String                      description   = null;
    /*------------------------------------------------------------------*/
    /**Create Info*/
    //
    /*工厂Bean名称或ID。*/
    private String                      factoryBean   = null;
    /*工厂Bean,下用于创建该bean的方法名。*/
    private String                      factoryMethod = null;
    /*初始化方法（生命周期：阶段-出生）*/
    private String                      initMethod    = null;
    /*销毁方法（生命周期：阶段-死亡）*/
    private String                      destroyMethod = null;
    /*------------------------------------------------------------------*/
    /**Member Info*/
    //
    /*属性成员*/
    private Map<String, PropertyDefine> propertys     = new HashMap<String, PropertyDefine>();
    /*方法成员*/
    private Map<String, MethodDefine>   methods       = new HashMap<String, MethodDefine>();
    /*------------------------------------------------------------------*/
    /**返回具有特征的字符串。*/
    public String toString() {
        return this.getClass().getSimpleName() + "@" + this.hashCode() + " UID=" + DefineUtils.getFullName(this);
    };
    //
    /**获取Bean唯一的ID值。*/
    public String getId() {
        return id;
    }
    /**设置Bean唯一的ID值。*/
    public void setId(String id) {
        this.id = id;
    }
    /**获取名称,名称在不同的scope下可以重复定义。*/
    public String getName() {
        return name;
    }
    /**设置名称,名称在不同的scope下可以重复定义。*/
    public void setName(String name) {
        this.name = name;
    }
    /**获取所属的作用域。*/
    public String getScope() {
        return scope;
    }
    /**设置所属的作用域。*/
    public void setScope(String scope) {
        this.scope = scope;
    }
    /**获取初始化参数&构造方法参数*/
    public List<ParamDefine> getInitParams() {
        return initParams;
    }
    /**设置初始化参数&构造方法参数*/
    public void setInitParams(List<ParamDefine> initParams) {
        this.initParams = initParams;
    }
    /**获取应用的模板*/
    public String getUseTemplate() {
        return useTemplate;
    }
    /**设置应用的模板*/
    public void setUseTemplate(String useTemplate) {
        this.useTemplate = useTemplate;
    }
    /**获取抽象标志*/
    public boolean isAbstractMark() {
        return abstractMark;
    }
    /**设置抽象标志*/
    public void setAbstractMark(boolean abstractMark) {
        this.abstractMark = abstractMark;
    }
    /**获取单态标志*/
    public boolean isSingletonMark() {
        return singletonMark;
    }
    /**设置单态标志*/
    public void setSingletonMark(boolean singletonMark) {
        this.singletonMark = singletonMark;
    }
    /**获取延迟装载标志*/
    public boolean isLazyMark() {
        return lazyMark;
    }
    /**设置延迟装载标志*/
    public void setLazyMark(boolean lazyMark) {
        this.lazyMark = lazyMark;
    }
    /**获取描述信息*/
    public String getDescription() {
        return description;
    }
    /**设置描述信息*/
    public void setDescription(String description) {
        this.description = description;
    }
    /**获取工厂Bean名称或ID。*/
    public String getFactoryBean() {
        return factoryBean;
    }
    /**设置工厂Bean名称或ID。*/
    public void setFactoryBean(String factoryBean) {
        this.factoryBean = factoryBean;
    }
    /**获取工厂Bean,下用于创建该bean的方法名。*/
    public String getFactoryMethod() {
        return factoryMethod;
    }
    /**设置工厂Bean,下用于创建该bean的方法名。*/
    public void setFactoryMethod(String factoryMethod) {
        this.factoryMethod = factoryMethod;
    }
    /**获取初始化方法（生命周期：阶段-出生）*/
    public String getInitMethod() {
        return initMethod;
    }
    /**设置初始化方法（生命周期：阶段-出生）*/
    public void setInitMethod(String initMethod) {
        this.initMethod = initMethod;
    }
    /**获取销毁方法（生命周期：阶段-死亡）*/
    public String getDestroyMethod() {
        return destroyMethod;
    }
    /**设置销毁方法（生命周期：阶段-死亡）*/
    public void setDestroyMethod(String destroyMethod) {
        this.destroyMethod = destroyMethod;
    }
    /**获取属性成员*/
    public Map<String, PropertyDefine> getPropertys() {
        return propertys;
    }
    /**设置属性成员*/
    public void setPropertys(Map<String, PropertyDefine> propertys) {
        this.propertys = propertys;
    }
    /**获取方法成员*/
    public Map<String, MethodDefine> getMethods() {
        return methods;
    }
    /**设置方法成员*/
    public void setMethods(Map<String, MethodDefine> methods) {
        this.methods = methods;
    }
}