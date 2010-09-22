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
package org.more.beans.define;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.more.RepeateException;
import org.more.beans.AbstractBeanDefine;
import org.more.beans.AbstractPropertyDefine;
import org.more.beans.IocTypeEnum;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * TemplateBeanDefine类用于定义一个bean的模板。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class TemplateBeanDefine implements AbstractBeanDefine {
    private String                            name          = null;                                   //名称
    private IocTypeEnum                       iocType       = null;                                   //默认属性注入方式
    private String                            scope         = null;                                   //bean作用域
    private boolean                           boolAbstract  = false;                                  //抽象标志
    private boolean                           boolInterface = false;                                  //接口标志
    private boolean                           boolSingleton = false;                                  //单态标志
    private boolean                           boolLazyInit  = false;                                  //延迟装载标志
    private String                            description   = null;                                   //描述信息
    private String                            factoryName   = null;                                   //创建工厂名
    private String                            factoryMethod = null;                                   //创建工厂方法名
    private TemplateBeanDefine                useTemplate   = null;                                   //应用的模板
    private ArrayList<AbstractPropertyDefine> initParams    = new ArrayList<AbstractPropertyDefine>(); //初始化参数
    private ArrayList<AbstractPropertyDefine> propertys     = new ArrayList<AbstractPropertyDefine>(); //属性
    private IAttribute                        attribute     = new AttBase();                          //元信息描述
    private IAttribute                        defineconfig  = new AttBase();                          //扩展配置描述
    //-------------------------------------------------------------
    /**返回bean的名称，在同一个Factory中name是唯一的。*/
    public String getName() {
        return this.name;
    };
    /**获取bean的各种属性是如何注入到Bean中的。*/
    public IocTypeEnum getIocType() {
        return this.iocType;
    };
    /**获取bean的作用域，如果容器支持多种作用域。*/
    public String getScope() {
        return this.scope;
    };
    /**返回一个boolean值，表示类是否为一个抽象类。*/
    public boolean isAbstract() {
        return this.boolAbstract;
    };
    /**返回一个boolean值，表示类是否为一个接口。*/
    public boolean isInterface() {
        return this.boolInterface;
    };
    /**返回一个boolean值，表示这个bean是否为单态的。*/
    public boolean isSingleton() {
        return this.boolSingleton;
    };
    /**返回一个boolean值，表示这个bean是否为延迟装载的。*/
    public boolean isLazyInit() {
        return this.boolLazyInit;
    };
    /**返回bean的描述信息。*/
    public String getDescription() {
        return this.description;
    };
    /**创建bean的工厂名。*/
    public String factoryName() {
        return this.factoryName;
    };
    /**表明目标方法的方法名称描述。*/
    public String factoryMethod() {
        return this.factoryMethod;
    };
    /**获取bean使用的模板。*/
    public TemplateBeanDefine getUseTemplate() {
        return useTemplate;
    }
    /**获取当创建这个bean时候需要的启动参数。*/
    public AbstractPropertyDefine[] getInitParams() {
        AbstractPropertyDefine[] define = new AbstractPropertyDefine[this.initParams.size()];
        this.initParams.toArray(define);
        return define;
    };
    /**获取当创建这个bean时候需要的启动参数。*/
    public AbstractPropertyDefine[] getPropertys() {
        AbstractPropertyDefine[] define = new AbstractPropertyDefine[this.propertys.size()];
        this.propertys.toArray(define);
        return define;
    };
    /**返回扩展Define配置描述。*/
    public IAttribute getDefineConfig() {
        return this.defineconfig;
    }
    /**返回具有特征的字符串。*/
    public String toString() {
        return this.getClass().getSimpleName() + "@" + this.hashCode() + " name=" + this.getName();
    };
    /**添加一个启动参数。*/
    public void addInitParam(AbstractPropertyDefine property) {
        this.initParams.add(property);
        final TemplateBeanDefine define = this;
        Collections.sort(this.initParams, new Comparator<AbstractPropertyDefine>() {
            public int compare(AbstractPropertyDefine arg0, AbstractPropertyDefine arg1) {
                int cdefine_1 = ((ConstructorDefine) arg0).getIndex();
                int cdefine_2 = ((ConstructorDefine) arg1).getIndex();
                if (cdefine_1 > cdefine_2)
                    return 1;
                else if (cdefine_1 < cdefine_2)
                    return -1;
                else
                    throw new RepeateException(define + "[" + arg0 + "]与[" + arg1 + "]构造参数索引重复.");
            }
        });
    };
    /**添加一个属性。*/
    public void addProperty(AbstractPropertyDefine property) {
        this.propertys.add(property);
    };
    //-------------------------------------------------------------
    /**设置Bean名。*/
    public void setName(String name) {
        this.name = name;
    }
    /**设置Bean属性的Ioc方式。*/
    public void setIocType(IocTypeEnum iocType) {
        this.iocType = iocType;
    }
    /**设置bean有效作用域。*/
    public void setScope(String scope) {
        this.scope = scope;
    }
    /**设置描述信息。*/
    public void setDescription(String description) {
        this.description = description;
    }
    /**设置创建该Bean时使用的工厂bean名。*/
    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }
    /**设置创建该Bean时使用的工厂bean的方法名。*/
    public void setFactoryMethod(String factoryMethod) {
        this.factoryMethod = factoryMethod;
    }
    /**设置该bean是否为一个抽象的。*/
    public void setBoolAbstract(boolean boolAbstract) {
        this.boolAbstract = boolAbstract;
    }
    /**设置该bean是否为一个接口。*/
    public void setBoolInterface(boolean boolInterface) {
        this.boolInterface = boolInterface;
    }
    /**设置该bean是否为一个单态的。*/
    public void setBoolSingleton(boolean boolSingleton) {
        this.boolSingleton = boolSingleton;
    }
    /**设置该bean是否为一个延迟初始化的。*/
    public void setBoolLazyInit(boolean boolLazyInit) {
        this.boolLazyInit = boolLazyInit;
    }
    /**设置bean使用的模板。*/
    public void setUseTemplate(TemplateBeanDefine useTemplate) {
        this.useTemplate = useTemplate;
    }
    //-------------------------------------------------------------
    public boolean contains(String name) {
        return this.attribute.contains(name);
    };
    public void setAttribute(String name, Object value) {
        this.attribute.setAttribute(name, value);
    };
    public Object getAttribute(String name) {
        return this.attribute.getAttribute(name);
    };
    public void removeAttribute(String name) {
        this.attribute.removeAttribute(name);
    };
    public String[] getAttributeNames() {
        return this.attribute.getAttributeNames();
    };
    public void clearAttribute() {
        this.attribute.clearAttribute();
    }
}