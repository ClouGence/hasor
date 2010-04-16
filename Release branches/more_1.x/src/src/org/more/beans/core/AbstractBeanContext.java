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
package org.more.beans.core;
import org.more.beans.BeanContext;
import org.more.beans.BeanFactory;
import org.more.beans.BeanResource;
/**
 * 基本的BeanContext接口实现。
 * @version 2010-2-26
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractBeanContext implements BeanContext {
    //========================================================================================Field
    /**  */
    protected BeanFactory defaultBeanFactory = null;
    protected BeanContext parent             = null;
    //=============================================================================Impl BeanFactory
    @Override
    public void init() throws Exception {
        this.defaultBeanFactory.init();
    }
    @Override
    public void destroy() throws Exception {
        this.defaultBeanFactory.destroy();
    }
    @Override
    public BeanContext getParent() {
        return this.parent;
    }
    public void setParent(BeanContext parent) {
        this.parent = parent;
    }
    @Override
    public boolean containsBean(String name) {
        if (this.defaultBeanFactory.containsBean(name) == false && parent != null)
            return this.parent.containsBean(name);
        else
            return true;
    }
    @Override
    public Object getBean(String name, Object... objects) throws Exception {
        if (this.defaultBeanFactory.containsBean(name) == false && parent != null)
            return this.parent.getBean(name, objects);
        else
            return this.defaultBeanFactory.getBean(name, objects);
    }
    @Override
    public Class<?> getBeanType(String name) {
        if (this.defaultBeanFactory.containsBean(name) == false && parent != null)
            return this.parent.getBeanType(name);
        else
            return this.defaultBeanFactory.getBeanType(name);
    }
    @Override
    public boolean isTypeMatch(String name, Class<?> targetType) {
        if (this.defaultBeanFactory.isTypeMatch(name, targetType) == false && parent != null)
            return this.parent.isTypeMatch(name, targetType);
        else
            return true;
    }
    @Override
    public boolean isFactory(String name) {
        if (this.defaultBeanFactory.isFactory(name) == false && parent != null)
            return this.parent.isFactory(name);
        else
            return true;
    }
    @Override
    public boolean isPrototype(String name) {
        if (this.defaultBeanFactory.isPrototype(name) == false && parent != null)
            return this.parent.isPrototype(name);
        else
            return true;
    }
    @Override
    public boolean isSingleton(String name) {
        if (this.defaultBeanFactory.isSingleton(name) == false && parent != null)
            return this.parent.isSingleton(name);
        else
            return true;
    }
    @Override
    public ClassLoader getBeanClassLoader() {
        return this.defaultBeanFactory.getBeanClassLoader();
    }
    @Override
    public BeanResource getBeanResource() {
        return this.defaultBeanFactory.getBeanResource();
    }
    @Override
    public boolean contains(String name) {
        return this.defaultBeanFactory.contains(name);
    }
    @Override
    public void clearAttribute() {
        this.defaultBeanFactory.clearAttribute();
    }
    @Override
    public Object getAttribute(String name) {
        return this.defaultBeanFactory.getAttribute(name);
    }
    @Override
    public String[] getAttributeNames() {
        return this.defaultBeanFactory.getAttributeNames();
    }
    @Override
    public void removeAttribute(String name) {
        this.defaultBeanFactory.removeAttribute(name);
    }
    @Override
    public void setAttribute(String name, Object value) {
        this.defaultBeanFactory.setAttribute(name, value);
    }
}