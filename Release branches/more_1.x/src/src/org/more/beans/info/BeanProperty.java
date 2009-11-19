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
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * BeanProperty是用于表示属性的bean定义。在该类中定了一些基本类型，这些基本类型的定义有助于优化性能。但是基本类型的
 * 包装类型例如int的包装类型Integer则不属于基本类型范畴。除了java常见的八个基本类型之外BeanProperty还针对
 * java.lang.String、Array(一维数组)、List接口、Map接口、Set接口做了定义。BeanProperty作为属性对象必须设置propType属性。
 * propType有一些预定义的值请查看BeanProperty的静态字段。
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
    private String             name             = null;                 //属性名，对于构造方法参数配置该值无效。
    private BeanProp           value            = null;                 //属性值。
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
    public BeanProp getValue() {
        return value;
    }
    /**设置属性值。*/
    public void setValue(BeanProp value) {
        this.value = value;
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
        else if (propType.equals("java.lang.String") == true)
            super.setPropType(BeanProperty.TS_String);
        else if (propType.equals("Array") == true)
            super.setPropType(BeanProperty.TS_Array);
        else if (propType.equals("List") == true)
            super.setPropType(BeanProperty.TS_List);
        else if (propType.equals("Map") == true)
            super.setPropType(BeanProperty.TS_Map);
        else if (propType.equals("Set") == true)
            super.setPropType(BeanProperty.TS_Set);
        else
            super.setPropType(propType);
    }
    /** 返回CreateEngine创建对象所使用的类型。 */
    public static Class<?> toClass(Prop prop, ClassLoader loader) throws ClassNotFoundException {
        String propType = prop.getPropType();
        if (propType == BeanProperty.TS_Integer)
            return int.class;
        else if (propType == BeanProperty.TS_Byte)
            return byte.class;
        else if (propType == BeanProperty.TS_Char)
            return char.class;
        else if (propType == BeanProperty.TS_Double)
            return double.class;
        else if (propType == BeanProperty.TS_Float)
            return float.class;
        else if (propType == BeanProperty.TS_Long)
            return long.class;
        else if (propType == BeanProperty.TS_Short)
            return short.class;
        else if (propType == BeanProperty.TS_Boolean)
            return boolean.class;
        else if (propType == BeanProperty.TS_String)
            return String.class;
        else if (propType == BeanProperty.TS_Array) {
            Class<?> element;
            if (prop instanceof BeanProperty)
                element = BeanProperty.toClass(((BeanProperty) prop).getValue(), loader);
            else
                element = BeanProperty.toClass(prop, loader);
            return Array.newInstance(element, 1).getClass();
        } else if (propType == BeanProperty.TS_List)
            return List.class;
        else if (propType == BeanProperty.TS_Map)
            return Map.class;
        else if (propType == BeanProperty.TS_Set)
            return Set.class;
        else
            return loader.loadClass(propType);
    }
}