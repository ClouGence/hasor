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
import java.util.ArrayList;
import java.util.Collections;
/**
 * ParentDecorator类型装饰器，该装饰器的作用是基于原有{@link IAttribute}属性集之外在套一层父属性集。
 * 这样当遇到设置同名属性时新的属性集不会影响父属性集。
 * @version 2010-9-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class ParentDecorator extends AbstractAttDecorator {
    //========================================================================================Field
    private IAttribute parent = null;
    //==================================================================================Constructor
    /**
     * 创建一个ParentDecorator类型装饰器，使用该方法创建的装饰器其父属性集就是源(source)。
     * @param source 指定装饰器要装饰的属性集。
     * @throws NullPointerException 如果source参数为空则会引发该异常。
     */
    public ParentDecorator(IAttribute source) throws NullPointerException {
        super(source);
    }
    /**
     * 创建一个ParentDecorator类型装饰器，使用该方法创建的装饰器其父属性集就是源(source)。
     * @param source 指定装饰器要装饰的属性集。
     * @param parent 所使用的父属性集。
     * @throws NullPointerException 如果source参数为空则会引发该异常。
     */
    public ParentDecorator(IAttribute source, IAttribute parent) throws NullPointerException {
        super(source);
        this.parent = parent;
    }
    //==========================================================================================JOB
    /**获取当前属性集中的父属性集，如果使用的是ParentDecorator(IAttribute)构造方法创建的ParentDecorator对象则分支方法返回值与getSource()方法返回值一样。*/
    public IAttribute getParent() {
        return this.parent;
    }
    /**替换父属性集。*/
    protected void setParent(IAttribute parent) {
        this.parent = parent;
    }
    /**首先从当前属性集中寻找，如果找到返回true。否则到父属性集中去找并且返回查找结果。*/
    public boolean contains(String name) {
        if (super.contains(name) == false)
            return this.parent.contains(name);
        return true;
    }
    /**首先从当前属性集中寻找，如果找到返回这个对象。否则到父属性集中去找并且返回查找结果。*/
    public Object getAttribute(String name) {
        Object obj = super.getAttribute(name);
        if (obj == null)
            return this.parent.getAttribute(name);
        return obj;
    }
    /**返回当前属性集以及父属性集中可以访问到的所有属性名，如果当前属性集中定义的属性在父属性集中重复定义该方法只会保留一个属性名称。*/
    public String[] getAttributeNames() {
        ArrayList<String> ns = new ArrayList<String>();
        Collections.addAll(ns, super.getAttributeNames());
        for (String n : this.parent.getAttributeNames())
            if (ns.contains(n) == false)
                ns.add(n);
        //
        String[] array = new String[ns.size()];
        ns.toArray(array);
        return array;
    }
}