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
package org.more.hypha.beans.define;
import org.more.util.StringConvertUtil;
/**
 * 表示一个大文本数据段，通常使用CDATA来描述对应的PropertyMetaTypeEnum类型为{@link PropertyMetaTypeEnum#Enum}。
 * @version 2010-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class Enum_ValueMetaData extends AbstractValueMetaData {
    private String   enumValue = null; //枚举表述的字符串形式
    private String   enumType  = null; //枚举类型
    private Class<?> eType     = null;
    /**该方法将会返回{@link PropertyMetaTypeEnum#Enum}。*/
    public String getMetaDataType() {
        return PropertyMetaTypeEnum.Enum;
    }
    /**直接返回解析之后的枚举，如果没有设置enumType将会引发异常。*/
    public Enum<?> getEnumType(ClassLoader enumLoader) throws ClassNotFoundException {
        Class<?> eType = this.getEnumClass(enumLoader);
        return this.getEnum(eType);
    }
    /**使用一个枚举类型返回属于这个枚举类型中的枚举值。*/
    public Enum<?> getEnum(Class<?> enumType) {
        return (Enum<?>) StringConvertUtil.changeType(enumValue, enumType);
    }
    /**使用一个枚举类型返回属于这个枚举类型中的枚举值。*/
    public Enum<?> getEnum(Class<?> enumType, Enum<?> defaultValue) {
        return (Enum<?>) StringConvertUtil.changeType(enumValue, enumType, defaultValue);
    }
    /**获取装载的枚举类型。*/
    public Class<?> getEnumClass(ClassLoader enumLoader) throws ClassNotFoundException {
        if (this.eType == null)
            this.eType = enumLoader.loadClass(this.enumType);
        return this.eType;
    }
    /**获取枚举表述的字符串形式。*/
    public String getEnumValue() {
        return this.enumValue;
    }
    /**设置枚举表述的字符串形式。*/
    public void setEnumValue(String enumValue) {
        this.enumValue = enumValue;
    }
    /**获取枚举类型*/
    public String getEnumType() {
        return this.enumType;
    }
    /**设置枚举类型*/
    public void setEnumType(String enumType) {
        this.enumType = enumType;
    }
}