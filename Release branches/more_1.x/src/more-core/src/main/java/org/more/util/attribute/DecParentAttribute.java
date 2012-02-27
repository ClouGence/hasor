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
import java.util.Collections;
import java.util.HashSet;
/**
 * 提供一种对父层级的依赖支持。
 * @version 2010-9-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class DecParentAttribute<V> extends AttributeDecorator<V> {
    private IAttribute<V> parent = null;
    /**
    * 创建一个ParentAttribute类型对象，该构造方法内部会自动创造一个{@link Attribute}对象作为。
    * @param parent 所使用的父属性集。
    * @throws NullPointerException 如果source参数为空则会引发该异常。
    */
    public DecParentAttribute(IAttribute<V> parent) throws NullPointerException {
        this(parent, new Attribute<V>());
    };
    /**
    * 创建一个ParentAttribute类型对象，该构造方法可以将两个{@link IAttribute}以父子层级的方式组合起来。
    * @param parent 所使用的父属性集。
    * @param source 源属性集。
    * @throws NullPointerException 如果source参数为空则会引发该异常。
    */
    public DecParentAttribute(IAttribute<V> parent, Attribute<V> source) throws NullPointerException {
        super(source);
        if (parent == null)
            throw new NullPointerException("parent IAttribute is null.");
        this.parent = parent;
    };
    /**获取父属性集。*/
    public final IAttribute<V> getParent() {
        return this.parent;
    };
    /**
     * 设置装饰器要装饰的目标类。如果装饰器已经装饰了某个属性对象那么该方法将替换原有属性对象。
     * @param source 准备替换的属性对象。
     * @throws NullPointerException 如果企图设置一个空值到装饰器将引发该异常。
     */
    protected final void setParent(IAttribute<V> parent) throws NullPointerException {
        if (parent == null)
            throw new NullPointerException("target parent IAttribute is null.");
        else
            this.parent = parent;
    }
    /**首先从当前属性集中寻找，如果找到返回这个对象。否则到父属性集中去找并且返回查找结果。*/
    @Override
    public boolean contains(String name) {
        if (super.contains(name) == false)
            return this.parent.contains(name);
        return true;
    }
    /**首先从当前属性集中寻找，如果找到返回这个对象。否则到父属性集中去找并且返回查找结果。*/
    @Override
    public V getAttribute(String name) {
        V obj = super.getAttribute(name);
        if (obj == null)
            return this.parent.getAttribute(name);
        return obj;
    }
    /**返回当前属性集以及父属性集中可以访问到的所有属性名，如果当前属性集中定义的属性在父属性集中重复定义该方法只会保留一个属性名称。
     * <br/><b>这是一个高成本操作，它会使用HashSet合并{@link #getParent()}、{@link #getSource()}两个方法的返回值。</b>*/
    @Override
    public String[] getAttributeNames() {
        HashSet<String> keys = new HashSet<String>();
        Collections.addAll(keys, this.getParent().getAttributeNames());
        Collections.addAll(keys, this.getSource().getAttributeNames());
        String[] array = new String[keys.size()];
        keys.toArray(array);
        return array;
    }
    /**取得可以取得的属性名总数目。
     * <br/><b>这是一个高成本操作，它会使用HashSet合并{@link #getParent()}、{@link #getSource()}两个方法的返回值。</b>*/
    @Override
    public int size() {
        HashSet<String> keys = new HashSet<String>();
        Collections.addAll(keys, this.getParent().getAttributeNames());
        Collections.addAll(keys, this.getSource().getAttributeNames());
        return keys.size();
    };
}