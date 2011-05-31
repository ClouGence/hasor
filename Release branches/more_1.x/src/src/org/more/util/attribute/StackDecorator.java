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
import org.more.core.error.SupportException;
/**
 * 堆属性装饰器，利用该属性装饰器可以在属性集上增加另一个属性堆。其数据结构与堆相似。
 * 当在该装饰器上获取某个属性时，StackDecorator类型对象会首先在当前属性堆中寻找如果找不到则去上一个堆中去寻找。
 * 通过createStack方法可以创建一个新的属性堆。而dropStack方法可以销毁位于属性堆最上层。
 * @version 2010-9-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class StackDecorator extends AbstractAttDecorator {
    //========================================================================================Field
    private int depth = 0;
    //==================================================================================Constructor
    public StackDecorator(IAttribute source) throws NullPointerException {
        super(source);
    }
    //==========================================================================================JOB
    /**由于StackDecorator类型在createStack和dropStack方法中都需要改变创建source因此为了避免滥用setSource，所以StackDecorator装饰器不支持该方法。*/
    public void setSource(IAttribute source) throws SupportException {
        throw new SupportException("StackDecorator装饰器不支持该方法。");
    }
    /**获取源*/
    public IAttribute getSource() {
        IAttribute att = super.getSource();
        if (depth == 0)
            return att;
        return ((ParentDecorator) att).getSource();
    }
    /** 该方法与getSource()方法返回值一样。 */
    public IAttribute getCurrentStack() {
        return super.getSource();
    }
    /** 获取当前堆的父堆。 */
    public IAttribute getParentStack() {
        if (depth == 0)
            return null;
        IAttribute att = super.getSource();
        return ((ParentDecorator) att).getParent();
    }
    /**在现有属性堆上创建一个堆。*/
    public synchronized void createStack() {
        IAttribute source = super.getSource();
        super.setSource(new ParentDecorator(new AttBase(), source));
        depth++;
    }
    /**销毁当前层次的属性堆，如果当前属性堆不是最初的哪个源则销毁这个层次的属性堆并将当前属性堆替换为父属性堆。操作成功返回true否则返回false。*/
    public synchronized boolean dropStack() {
        if (depth == 0)
            return false;
        //
        IAttribute source = super.getSource();
        if (source instanceof ParentDecorator) {
            super.setSource(((ParentDecorator) source).getParent());
            depth--;
            return true;
        }
        return false;
    }
    /**获取当前堆的深度，该值会随着调用createStack方法而增加，随着dropStack方法而减少。*/
    public int getDepth() {
        return this.depth;
    }
}