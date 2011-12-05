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
import java.util.Collection;
import java.util.List;
import java.util.Map;
/**
 * 
 * @version : 2011-7-22
 * @author 赵永春 (zyc@byshell.org) 
 */
public class SequenceStack<T> implements IAttribute<T> {
    private List<IAttribute<T>> attList = null;
    public SequenceStack() {
        this.attList = new ArrayList<IAttribute<T>>();
    }
    public SequenceStack(Collection<IAttribute<T>> collection) {
        this();
        for (IAttribute<T> att : collection)
            if (att != null)
                attList.add(att);
    }
    /**将一个{@link IAttribute}接口对象加入到队列中，越先加入的优先级越低。*/
    public void putStack(IAttribute<T> scope) {
        if (this.attList.contains(scope) == false)
            this.attList.add(scope);
    };
    public boolean contains(String name) {
        for (IAttribute<?> iatt : this.attList)
            if (iatt.contains(name) == true)
                return true;
        return false;
    };
    public T getAttribute(String name) {
        for (IAttribute<T> iatt : this.attList) {
            T res = iatt.getAttribute(name);
            if (res != null)
                return res;
        }
        return null;
    };
    public String[] getAttributeNames() {
        ArrayList<String> as = new ArrayList<String>();
        for (IAttribute<?> iatt : this.attList)
            for (String n : iatt.getAttributeNames())
                if (as.contains(n) == false)
                    as.add(n);
        String[] array = new String[as.size()];
        as.toArray(array);
        return array;
    };
    public Map<String, T> toMap() {
        return new TransformToMap<T>(this);
    };
    /**该方法只会对第一个加入的{@link IAttribute}对象起作用。*/
    public void clearAttribute() {
        if (this.attList.isEmpty() == false)
            this.attList.get(0).clearAttribute();
    };
    /**该方法只会对第一个加入的{@link IAttribute}对象起作用。*/
    public void removeAttribute(String name) {
        if (this.attList.isEmpty() == false)
            this.attList.get(0).removeAttribute(name);
    };
    /**该方法只会对第一个加入的{@link IAttribute}对象起作用。*/
    public void setAttribute(String name, T value) {
        if (this.attList.isEmpty() == false)
            this.attList.get(0).setAttribute(name, value);
    }
    /**返回队列中元素个数*/
    public int attCount() {
        return this.attList.size();
    }
    /**返回所有队列元素中的属性总数*/
    public int size() {
        return this.getAttributeNames().length;
    }
    public IAttribute<T> getIndex(int index) {
        return this.attList.get(index);
    }
    public List<IAttribute<T>> getList() {
        return this.attList;
    }
};