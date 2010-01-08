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
 * 负责封装bean定义中有关Map集合属性的信息，新建的集合对象如果没有指定类型则默认是:java.util.Map对象。
 * propType属性规则。
 * 1.如果PropMap配置了propType属性则直接返回，否则返回propContext的propType属性。
 * 2.如果propContext也没有配置propType属性则返回java.util.Map。
 * @version 2009-11-18
 * @author 赵永春 (zyc@byshell.org)
 */
public class PropMap extends BeanProp {
    //========================================================================================Field
    /**  */
    private static final long serialVersionUID = -194590250590692070L;
    private BeanProp[][]      mapElements      = null;                //集合元素。
    //=======================================================================================Static
    /**创建一个java.util.HashMp类型的空Map集合。*/
    public PropMap() {
        this.mapElements = new BeanProp[0][0];
        this.setPropType("java.util.Map");
    }
    /**创建一个java.util.HashMp类型的集合，集合元素由参数elements决定。*/
    public PropMap(BeanProp[][] elements) {
        if (elements == null)
            this.mapElements = new BeanProp[0][0];
        else
            this.mapElements = elements;
        this.setPropType("java.util.Map");
    }
    /**创建一个PropMap集合定义对象，集合元素由参数elements决定，集合类型由mapType决定。*/
    public PropMap(BeanProp[][] elements, String mapType) {
        if (elements == null)
            this.mapElements = new BeanProp[0][0];
        else
            this.mapElements = elements;
        this.setPropType(mapType);
    }
    //==================================================================================Constructor
    //==========================================================================================Job
    /**获取集合元素。*/
    public BeanProp[][] getMapElements() {
        return mapElements;
    }
    /**设置集合元素。*/
    public void setMapElements(BeanProp[][] mapElements) {
        this.mapElements = mapElements;
    }
}