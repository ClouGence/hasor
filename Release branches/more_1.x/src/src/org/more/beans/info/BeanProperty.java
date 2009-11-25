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
 * BeanProperty是用于表示属性的bean定义。
 * <br/>Date : 2009-11-18
 * @author 赵永春
 */
public class BeanProperty extends Prop {
    /**  */
    private static final long serialVersionUID = -3492072515778133870L;
    private String            name             = null;                 //属性名，对于构造方法参数配置该值无效。
    private BeanProp          refValue         = null;                 //属性值。
    //=========================================================================
    /**获取属性名，对于构造方法参数配置该值无效。*/
    public String getName() {
        return name;
    }
    /**设置属性名，对于构造方法参数配置该值无效。*/
    public void setName(String name) {
        this.name = name;
    }
    /**获取属性值。*/
    public BeanProp getRefValue() {
        return refValue;
    }
    /**设置属性值。*/
    public void setRefValue(BeanProp refValue) {
        this.refValue = refValue;
    }
}