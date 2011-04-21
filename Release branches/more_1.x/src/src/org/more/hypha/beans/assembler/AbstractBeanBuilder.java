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
package org.more.hypha.beans.assembler;
import java.io.IOException;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.ApplicationContext;
/**
* 该类的子类可以对不同类型的{@link AbstractBeanDefine}分开进行字节码装载和对象创建之后的第一次初始化。假如ejb作为{@link AbstractBeanDefine}配置在系统中。
* 由于真正的实现存在于远程，所以本地会通过某种方式与远程ebj建立一个对象用来代理远程对象。那么这个代理对象的创建需要通过hypha的ioc机制进行，但
* @version 2010-12-23
* @author 赵永春 (zyc@byshell.org)
*/
public abstract class AbstractBeanBuilder<T extends AbstractBeanDefine> {
    /**应用上下文*/
    private ApplicationContext applicationContext = null;
    /**是否可以被装载成类对象，默认返回值false。*/
    public boolean canBuilder() {
        return false;
    };
    /**
     * 该方法确定{@link AbstractBeanBuilder}类是否使用默认bean创建模式，在默认模式下使用ioc的两种方式(工厂，构造方法)创建bean。
     * 如果该方法返值为false则通过{@link AbstractBeanBuilder#createBean(AbstractBeanDefine, Object[])}方法来创建bean。
     * <br/>默认返回值false。
     */
    public boolean ifDefaultBeanCreateMode() {
        return true;
    };
    /**设置{@link ApplicationContext}接口对象。*/
    void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    /**获取应用上下文*/
    protected ApplicationContext getApplicationContext() {
        return this.applicationContext;
    };
    /**
     * 装载BeanDefine定义，并获取其Class对象的字节码数据。在执行该方法之前{@link BeanEngine}类
     * 会通过{@link #canBuilder()}方法判定是否支持{@link #loadBeanBytes(AbstractBeanDefine)}装载字节码。
     */
    public abstract byte[] loadBeanBytes(T define) throws IOException;
    /**当ifDefaultBeanCreateMode方法返回值为false时，通过{@link BeanEngine}引擎会通过该方法创建bean。*/
    public abstract Object createBean(T define, Object[] params) throws Throwable;
};