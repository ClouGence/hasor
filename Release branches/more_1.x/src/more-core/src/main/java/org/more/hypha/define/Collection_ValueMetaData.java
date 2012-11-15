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
package org.more.hypha.define;
/**
 * 表示一个集合类型的抽象类其子类决定具体可以表述的集合类型。
 * @version 2010-9-18
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class Collection_ValueMetaData<T extends ValueMetaData> extends ValueMetaData {
    private String collectionType = null; //集合对象类型，如果是数组则该值与属性collectionValueType一致。
    private int    initSize       = 0;   //表示集合初始化大小
    /**获取集合对象类型，如果是数组则该值与属性collectionValueType一致。*/
    public String getCollectionType() {
        return this.collectionType;
    }
    /**设置集合对象类型，如果是数组则该值与属性collectionValueType一致。*/
    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }
    /**返回最大的，*/
    public int getInitSize() {
        int size = this.size();
        if (size > initSize)
            return size;
        return initSize;
    };
    /**设置集合初始化大小*/
    public void setInitSize(int initSize) {
        this.initSize = initSize;
    }
    /**添加一个属性值到当前集合中。*/
    public abstract void addObject(T value);
    /**获取集合当前数据内容条数数。*/
    public abstract int size();
}