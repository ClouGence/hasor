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
 * BeanProperty是用于表示属性的bean定义。在该类中定了一些基本类型，这些基本类型的定义有助于优化性能。但是基本类型的
 * 包装类型例如int的包装类型Integer则不属于基本类型范畴。除了java常见的八个基本类型之外BeanProperty还针对java.lang.String做了定义。
 * BeanProperty作为属性对象必须设置propType属性，propType有一些预定义的值请查看BeanProperty的静态字段。
 * <br/>Date : 2009-11-18
 * @author 赵永春
 */
public class BeanProperty extends Prop {
    /**基本类型int。*/
    public static final String TS_Integer       = "int";
    /**基本类型byte。*/
    public static final String TS_Byte          = "byte";
    /**基本类型char。*/
    public static final String TS_Char          = "char";
    /**基本类型double。*/
    public static final String TS_Double        = "double";
    /**基本类型float。*/
    public static final String TS_Float         = "float";
    /**基本类型long。*/
    public static final String TS_Long          = "long";
    /**基本类型short。*/
    public static final String TS_Short         = "short";
    /**基本类型boolean。*/
    public static final String TS_Boolean       = "boolean";
    /**基本类型String。*/
    public static final String TS_String        = "String";
    /**  */
    private static final long  serialVersionUID = -3492072515778133870L;
    private String             name             = null;                 //属性名，对于构造方法参数配置该值无效。
    private BeanProp           refValue         = null;                 //属性值。
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
    /**获取属性的类型（必须配置）部分属性类型由TS_常量定义。*/
    public String getPropType() {
        return super.getPropType();
    }
    /**设置属性的类型（必须配置）部分属性类型由TS_常量定义。*/
    public void setPropType(String propType) {
        if (propType.equals("int") == true)
            super.setPropType(BeanProperty.TS_Integer);
        else if (propType.equals("byte") == true)
            super.setPropType(BeanProperty.TS_Byte);
        else if (propType.equals("char") == true)
            super.setPropType(BeanProperty.TS_Char);
        else if (propType.equals("double") == true)
            super.setPropType(BeanProperty.TS_Double);
        else if (propType.equals("float") == true)
            super.setPropType(BeanProperty.TS_Float);
        else if (propType.equals("long") == true)
            super.setPropType(BeanProperty.TS_Long);
        else if (propType.equals("short") == true)
            super.setPropType(BeanProperty.TS_Short);
        else if (propType.equals("boolean") == true)
            super.setPropType(BeanProperty.TS_Boolean);
        else if (propType.equals("String") == true)
            super.setPropType(BeanProperty.TS_String);
        else
            super.setPropType(propType);
    }
}