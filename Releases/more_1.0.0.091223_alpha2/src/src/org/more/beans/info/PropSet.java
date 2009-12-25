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
 * 负责封装bean定义中有关Set集合属性的信息，新建的集合对象如果没有指定类型则默认是:java.util.Set类型。
 * propType属性规则。
 * 1.如果PropSet配置了propType属性则直接返回，否则返回propContext的propType属性。
 * 2.如果propContext也没有配置propType属性则返回java.util.Set。
 * <br/>Date : 2009-11-18
 * @author 赵永春
 */
public class PropSet extends BeanProp {
    //========================================================================================Field
    /**  */
    private static final long serialVersionUID = -194590250590692070L;
    private BeanProp[]        setElements      = null;                //集合元素。
    //==================================================================================Constructor
    /**创建一个PropSet集合定义对象。*/
    public PropSet() {
        this.setElements = new BeanProp[0];
        this.setPropType("java.util.Set");
    }
    /**创建一个PropSet集合定义对象，集合元素由参数elements决定。*/
    public PropSet(BeanProp[] elements) {
        if (elements == null)
            this.setElements = new BeanProp[0];
        else
            this.setElements = elements;
        this.setPropType("java.util.Set");
    }
    /**创建一个PropSet集合定义对象，集合元素由参数elements决定，集合类型由setType决定。*/
    public PropSet(BeanProp[] elements, String setType) {
        if (elements == null)
            this.setElements = new BeanProp[0];
        else
            this.setElements = elements;
        this.setPropType(setType);
    }
    //==========================================================================================Job
    /**获取集合元素。*/
    public BeanProp[] getSetElements() {
        return setElements;
    }
    /**设置集合元素。*/
    public void setSetElements(BeanProp[] setElements) {
        this.setElements = setElements;
    }
}