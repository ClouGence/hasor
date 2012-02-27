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
 * 栈结构属性对象，利用该属性装饰器可以在属性集上增加另一个属性栈。
 * @version 2010-9-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class DecStackDecorator<V> extends AttributeDecorator<V> {
    private int depth = 0;
    public DecStackDecorator() {
        super(new Attribute<V>());
    };
    public DecStackDecorator(IAttribute<V> source) throws NullPointerException {
        super(source);
    };
    /**获取当前堆的深度，该值会随着调用createStack方法而增加，随着dropStack方法而减少。*/
    public final int getDepth() {
        return this.depth;
    };
    /** 该方法与getSource()方法返回值一样。 */
    public IAttribute<V> getCurrentStack() {
        return super.getSource();
    };
    /** 获取当前堆的父堆（如果可能）。 */
    public IAttribute<V> getParentStack() {
        if (depth == 0)
            return null;
        IAttribute<V> att = super.getSource();
        return ((DecParentAttribute<V>) att).getParent();
    };
    /**在现有属性栈上创建一个新的栈，操作也会切换到这个新栈上。*/
    public synchronized void createStack() {
        IAttribute<V> source = super.getSource();
        DecParentAttribute<V> parent = new DecParentAttribute<V>(source);
        this.setSource(parent);
        depth++;
    };
    /**销毁当前层次的属性栈，如果在栈顶执行该操作将会引发{@link IndexOutOfBoundsException}类型异常。*/
    public synchronized boolean dropStack() {
        if (depth == 0)
            throw new IndexOutOfBoundsException();
        IAttribute<V> source = super.getSource();
        if (source instanceof DecParentAttribute) {
            super.setSource(((DecParentAttribute<V>) source).getParent());
            depth--;
            return true;
        }
        return false;
    };
}