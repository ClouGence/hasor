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
package org.more.beans.info;
/**
 * 负责封装bean定义中有关数组属性的信息，新建的数组对象如果没有指定数组类型则默认是:java.lang.Object对象。
 * <br/>Date : 2009-11-18
 * @author 赵永春
 */
public class PropArray extends BeanProp {
    //========================================================================================Field
    /**  */
    private static final long serialVersionUID = -194590250590692070L; //
    private int               length           = 0;                   //数组长度。
    private BeanProp[]        arrayElements    = null;                //数组元素定义。
    //==================================================================================Constructor
    /**使用默认java.lang.Object类型创建一个数组零长度的对象。*/
    public PropArray() {
        this.arrayElements = new BeanProp[0];
        this.length = 0;
        this.setPropType("java.lang.Object");
    }
    /**创建一个数组对象，该对象的元素内容由参数elements决定，同时数组类型是java.lang.Object。*/
    public PropArray(BeanProp[] elements) {
        if (elements == null) {
            this.arrayElements = new BeanProp[0];
            this.length = 0;
        } else {
            this.arrayElements = elements;
            this.length = elements.length;
        }
        this.setPropType("java.lang.Object");
    }
    /**创建一个数组对象，该对象的元素内容由参数elements决定，同时数组类型由参数arrayType指定。*/
    public PropArray(BeanProp[] elements, String arrayType) {
        if (elements == null) {
            this.arrayElements = new BeanProp[0];
            this.length = 0;
        } else {
            this.arrayElements = elements;
            this.length = elements.length;
        }
        this.setPropType(arrayType);
    }
    //==========================================================================================Job
    /**获取数组长度*/
    public int getLength() {
        return length;
    }
    /**设置数组长度*/
    public void setLength(int length) {
        this.length = length;
    }
    /**获取数组元素定义。*/
    public BeanProp[] getArrayElements() {
        return arrayElements;
    }
    /**设置数组元素定义。*/
    public void setArrayElements(BeanProp[] arrayElements) {
        this.arrayElements = arrayElements;
    }
}