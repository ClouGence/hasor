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
import org.more.util.attribute.AttBase;
/**
 * BeanProperty是用于表示属性的bean定义。在该类中定了一些基本类型，这些基本类型的定义有助于优化性能。
 * 但是基本类型的包装类型例如int的包装类型Integer则不属于基本类型范畴。除了java常见的八个基本类型之外
 * BeanProperty还针对java.lang.String、Array(一维Object数组)、List接口、Map接口、Set接口做了定义。
 * Date : 2009-11-18
 * @author 赵永春
 */
public class BeanProperty extends AttBase {
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
    public static final String TS_String        = "java.lang.String";
    /**基本类型Array。*/
    public static final String TS_Array         = "Array";
    /**基本类型List。*/
    public static final String TS_List          = "List";
    /**基本类型Map。*/
    public static final String TS_Map           = "Map";
    /**基本类型Set。*/
    public static final String TS_Set           = "Set";
    /**  */
    private static final long  serialVersionUID = -3492072515778133870L;
    private String             id               = null;                 //唯一的ID值。
    private String             name             = null;                 //属性名，对于构造方法参数配置该值无效。
    private String             propType         = null;                 //属性的类型（必须配置）部分属性类型由TS_常量定义。
    private String             refBean          = null;                 //bean在进行依赖注入时所依赖的其他bean。
    private String             value            = null;                 //属性值，该值可能是一个XML片段。
    //=========================================================================
    /**获取唯一的ID值。*/
    public String getId() {
        return id;
    }
    /**设置唯一的ID值。*/
    public void setId(String id) {
        this.id = id;
    }
    /**获取属性名，对于构造方法参数配置该值无效。*/
    public String getName() {
        return name;
    }
    /**设置属性名，对于构造方法参数配置该值无效。*/
    public void setName(String name) {
        this.name = name;
    }
    /**获取bean在进行依赖注入时所依赖的其他bean。*/
    public String getRefBean() {
        return refBean;
    }
    /**设置bean在进行依赖注入时所依赖的其他bean。*/
    public void setRefBean(String refBean) {
        this.refBean = refBean;
    }
    /**获取属性值，该值可能是一个XML片段。*/
    public String getValue() {
        return value;
    }
    /**设置属性值，该值可能是一个XML片段。*/
    public void setValue(String value) {
        this.value = value;
    }
    /**获取属性的类型（必须配置）部分属性类型由TS_常量定义。*/
    public String getPropType() {
        return propType;
    }
    /**设置属性的类型（必须配置）部分属性类型由TS_常量定义。*/
    public void setPropType(String propType) {
        if (propType.equals("int") == true)
            this.propType = BeanProperty.TS_Integer;
        else if (propType.equals("byte") == true)
            this.propType = BeanProperty.TS_Byte;
        else if (propType.equals("char") == true)
            this.propType = BeanProperty.TS_Char;
        else if (propType.equals("double") == true)
            this.propType = BeanProperty.TS_Double;
        else if (propType.equals("float") == true)
            this.propType = BeanProperty.TS_Float;
        else if (propType.equals("long") == true)
            this.propType = BeanProperty.TS_Long;
        else if (propType.equals("short") == true)
            this.propType = BeanProperty.TS_Short;
        else if (propType.equals("boolean") == true)
            this.propType = BeanProperty.TS_Boolean;
        else if (propType.equals("java.lang.String") == true)
            this.propType = BeanProperty.TS_String;
        else if (propType.equals("Array") == true)
            this.propType = BeanProperty.TS_Array;
        else if (propType.equals("List") == true)
            this.propType = BeanProperty.TS_List;
        else if (propType.equals("Map") == true)
            this.propType = BeanProperty.TS_Map;
        else if (propType.equals("Set") == true)
            this.propType = BeanProperty.TS_Set;
        else
            this.propType = propType;
    }
}