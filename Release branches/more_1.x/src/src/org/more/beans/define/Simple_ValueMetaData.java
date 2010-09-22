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
package org.more.beans.define;
import org.more.beans.AbstractPropertyDefine;
import org.more.beans.ValueMetaData;
import org.more.beans.ValueMetaData.PropertyMetaTypeEnum;
/**
 * 表示一个基本类型数据，对应的PropertyMetaTypeEnum类型为{@link PropertyMetaTypeEnum#SimpleType}。
 * @version 2010-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class Simple_ValueMetaData extends ValueMetaData {
    /** 用于描述{@link AbstractPropertyDefine}定义的属性的属性类型是哪种，如果属性的类型是比较复杂的比如Map,List那么统统使用Object。*/
    public enum PropertyType {
        /**表示属性的值类型是一个null。*/
        Null,
        /**表示属性的值类型是一个boolean类型，或者{@link Boolean}类型。*/
        Boolean,
        /**表示属性的值类型是一个byte类型，或者{@link Byte}类型。*/
        Byte,
        /**表示属性的值类型是一个short类型，或者{@link Short}类型。*/
        Short,
        /**表示属性的值类型是一个int类型，或者{@link Integer}类型。*/
        Integer,
        /**表示属性的值类型是一个long类型，或者{@link Long}类型。*/
        Long,
        /**表示属性的值类型是一个float类型，或者{@link Float}类型。*/
        Float,
        /**表示属性的值类型是一个double类型，或者{@link Double}类型。*/
        Double,
        /**表示属性的值类型是一个char类型，或者{@link Character}类型。*/
        Char,
        /**表示属性的值类型是一个string类型，或者{@link String}类型。*/
        String,
    }
    private PropertyType valueMetaType = PropertyType.Null; //值类型
    private Object       value         = null;             //值
    /**该方法将会返回{@link PropertyMetaTypeEnum#SimpleType}。*/
    public PropertyMetaTypeEnum getPropertyType() {
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