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
package org.more.util.attribute;
import java.util.Map;
/**
 * 抽象的属性装饰器，所有属性装饰器类必须继承自该类或者其子类。
 * @version 2009-4-30
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AttributeDecorator<T> implements IAttribute<T> {
    private IAttribute<T> source = null;
    /**
     * 创建属性装饰器。
     * @param source 要装饰的目标属性对象。
     * @throws NullPointerException 如果企图设置一个空值到装饰器将引发该异常。
     */
    protected AttributeDecorator(IAttribute<T> source) throws NullPointerException {
        if (source == null)
            throw new NullPointerException("target source IAttribute is null.");
        else
            this.source = source;
    };
    /** 获得被装饰的对象。*/
    public final IAttribute<T> getSource() {
        return this.source;
    };
    /**
     * 设置装饰器要装饰的目标类。如果装饰器已经装饰了某个属性对象那么该方法将替换原有属性对象。
     * @param source 准备替换的属性对象。
     * @throws NullPointerException 如果企图设置一个空值到装饰器将引发该异常。
     */
    protected final void setSource(IAttribute<T> source) throws NullPointerException {
        if (source == null)
            throw new NullPointerException("target source IAttribute is null.");
        else
            this.source = source;
    };
    public Map<String, T> toMap() {
        return new TransformToMap<T>(this);
    }
    public void clearAttribute() {
        this.source.clearAttribute();
    };
    public boolean contains(String name) {
        return this.source.contains(name);
    };
    public T getAttribute(String name) {
        return this.source.getAttribute(name);
    };
    public String[] getAttributeNames() {
        return this.source.getAttributeNames();
    };
    public void removeAttribute(String name) {
        this.source.removeAttribute(name);
    };
    public void setAttribute(String name, T value) {
        this.source.setAttribute(name, value);
    };
    public int size() {
        return this.source.size();
    };
};