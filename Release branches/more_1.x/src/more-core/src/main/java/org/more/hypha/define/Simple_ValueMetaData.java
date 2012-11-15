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
import org.more.util.StringConvertUtil;
/**
 * 表示一个基本类型数据，对应的PropertyMetaTypeEnum类型为{@link PropertyMetaTypeEnum#SimpleType}。
 * @version 2010-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class Simple_ValueMetaData extends ValueMetaData {
    /**当检测到value有值但是又没有定义type时候值类型采用的默认数据类型。*/
    public static final PropertyType DefaultValueType = PropertyType.String;
    //---------------------------------------------------------------------------------------工具方法
    /**根据字符串类型描述取其{@link PropertyType}枚举对应的类型。*/
    public static PropertyType getPropertyType(String typeString) {
        if (typeString == null)
            return PropertyType.Null;
        if (typeString.equals("java.lang.String") == true)
            return PropertyType.String;
        PropertyType type = (PropertyType) StringConvertUtil.parseEnum(typeString, PropertyType.class);
        return type == null ? PropertyType.Null : type;
    };
    /**根据枚举获取其基本类型Class，*/
    public static Class<?> getPropertyType(PropertyType type) {
        if (type == null)
            throw new NullPointerException("参数type，不能为空。");
        if (type == PropertyType.Null)
            return null;
        if (type == PropertyType.Boolean)
            return boolean.class;
        else if (type == PropertyType.Byte)
            return byte.class;
        else if (type == PropertyType.Short)
            return short.class;
        else if (type == PropertyType.Int)
            return int.class;
        else if (type == PropertyType.Long)
            return long.class;
        else if (type == PropertyType.Float)
            return float.class;
        else if (type == PropertyType.Double)
            return double.class;
        else if (type == PropertyType.Char)
            return char.class;
        else if (type == PropertyType.String)
            return String.class;
        return null;//永远不会调用这段代码
    }
    /**根据类型获取其枚举类型，当目标类型无法转换成PropertyType表述的基本类型枚举时返回null。*/
    public static PropertyType getPropertyType(Class<?> type) {
        if (type == null)
            return PropertyType.Null;
        if (type == boolean.class || type == Boolean.class)
            return PropertyType.Boolean;
        else if (type == byte.class || type == Byte.class)
            return PropertyType.Byte;
        else if (type == short.class || type == Short.class)
            return PropertyType.Short;
        else if (type == int.class || type == Integer.class)
            return PropertyType.Int;
        else if (type == long.class || type == Long.class)
            return PropertyType.Long;
        else if (type == float.class || type == Float.class)
            return PropertyType.Float;
        else if (type == double.class || type == Double.class)
            return PropertyType.Double;
        else if (type == char.class || type == Character.class)
            return PropertyType.Char;
        else if (type == String.class)
            return PropertyType.String;
        else
            //转换默认值
            return null;
    }
    //---------------------------------------------------------------------------------------
    private PropertyType valueMetaType = PropertyType.Null; //值类型
    private Object       value         = null;             //值
    /**该方法将会返回{@link PropertyMetaTypeEnum#SimpleType}。*/
    public String getMetaDataType() {
        return PropertyMetaTypeEnum.SimpleType;
    }
    /**获取一个枚举这个枚举值表明了当前ValueMetaData试图描述的属性类型。*/
    public PropertyType getValueMetaType() {
        return this.valueMetaType;
    }
    /**设置一个枚举这个枚举值表明了当前ValueMetaData试图描述的属性类型。*/
    public void setValueMetaType(PropertyType valueMetaType) {
        this.valueMetaType = valueMetaType;
    }
    /**获取值*/
    public Object getValue() {
        return value;
    }
    /**写入值*/
    public void setValue(Object value) {
        this.value = value;
    }
}