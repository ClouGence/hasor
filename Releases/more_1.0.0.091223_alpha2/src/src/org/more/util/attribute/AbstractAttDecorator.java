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
/**
 *    抽象的属性装饰器，所有属性装饰器类必须继承自该类或者其子类。抽象的装饰器类中提供
 * 了对原始属性对象的一套get/set方法。通过装饰器的get/set方法可以方便的在不同的装饰
 * 器上进行切换或者采用装饰器嵌套。注意：调用装饰器中的保护方法不会影响到装饰的目标类。
 * Date : 2009-4-30
 * @author 赵永春
 */
public abstract class AbstractAttDecorator implements IAttribute {
    //========================================================================================Field
    /** 原始的属性类 */
    private IAttribute source = null;
    /**
     * 创建属性装饰器。
     * @param source 要装饰的目标属性对象。
     * @throws NullPointerException 如果企图设置一个空值到装饰器将引发该异常。
     */
    protected AbstractAttDecorator(IAttribute source) throws NullPointerException {
        if (source == null)
            throw new NullPointerException("装饰目标属性对象为空。");
        else
            this.source = source;
    }
    //==================================================================================Constructor
    /**
     * 获得装饰器装饰的原始属性对象。
     * @return 返回装饰器装饰的原始属性对象。
     */
    public IAttribute getSource() {
        return source;
    }
    /**
     * 设置装饰器要装饰的目标类。如果装饰器已经装饰了某个属性对象那么该方法将替换原有属性对象。
     * @param source 准备替换的属性对象。
     * @throws NullPointerException 如果企图设置一个空值到装饰器将引发该异常。
     */
    public void setSource(AttBase source) throws NullPointerException {
        if (source == null)
            throw new NullPointerException("装饰目标属性对象为空。");
        else
            this.source = source;
    }
    @Override
    public void clearAttribute() {
        this.source.clearAttribute();
    }
    @Override
    public boolean contains(String name) {
        return this.source.contains(name);
    }
    @Override
    public Object getAttribute(String name) {
        return this.source.getAttribute(name);
    }
    @Override
    public String[] getAttributeNames() {
        return this.source.getAttributeNames();
    }
    @Override
    public void removeAttribute(String name) {
        this.source.removeAttribute(name);
    }
    @Override
    public void setAttribute(String name, Object value) {
        this.source.setAttribute(name, value);
    }
}