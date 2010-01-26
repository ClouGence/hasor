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
 * 表示BeanProperty属性配置的值是一个基本数据类型(在more.beans中除了八个java基本类型之外还增加了字符串类型)。
 * <pre>
 * propType属性规则。
 * 1.如果没有配置propType属性则使用value所处的BeanProperty的属性作为配置。
 * </pre>
 * @version 2009-11-18
 * @author 赵永春 (zyc@byshell.org)
 */
public class PropVarValue extends BeanProp {
    //========================================================================================Field
    /**  */
    private static final long serialVersionUID = -194590250590692070L;
    private String            value            = null;                //属性值
    //==================================================================================Constructor
    /**创建一个表示基本数据的bean定义属性数据，默认值是null，默认数据类型是String。*/
    public PropVarValue() {}
    /**创建一个表示基本数据的bean定义属性数据。*/
    public PropVarValue(String value) {
        this.value = value;
    }
    /**创建一个表示基本数据的bean定义属性数据。*/
    public PropVarValue(String value, String valueType) {
        this.value = value;
        this.setPropType(valueType);
    }
    //==========================================================================================Job
    /**获取属性值。*/
    public String getValue() {
        return value;
    }
    /**设置属性值。*/
    public void setValue(String value) {
        this.value = value;
    }
}