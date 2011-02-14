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
package org.more.hypha.beans.assembler.factory;
import org.more.hypha.ApplicationContext;
import org.more.hypha.beans.AbstractBeanDefine;
/**
* 
* @version 2010-12-23
* @author 赵永春 (zyc@byshell.org)
*/
public abstract class BeanBuilder {
    /**应用上下文*/
    private ApplicationContext context = null;
    public BeanBuilder(ApplicationContext context) {
        this.context = context;
    };
    /**是否装载缓存中的字节码*/
    public boolean canCache() {
        return false;
    };
    /**是否可以被装载成类对象。*/
    public boolean canbuilder() {
        return false;
    };
    /**获取应用上下文*/
    protected ApplicationContext getApplicationContext() {
        return this.context;
    };
    /**装载BeanDefine定义，并获取其Class对象的字节码数据。*/
    public abstract byte[] loadBeanBytes(AbstractBeanDefine define);
    /**对原始新创建的Bean进行一次初始builder。*/
    public abstract Object builderBean(Object obj, AbstractBeanDefine define);
}