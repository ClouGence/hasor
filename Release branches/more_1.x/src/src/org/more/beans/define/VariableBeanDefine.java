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
import java.util.Date;
/**
 * VariableBeanDefine类用于定义一个值作为bean其类型由{@link VariableType}枚举限定。对于该类型Bean而言模板是不起作用的。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class VariableBeanDefine extends TemplateBeanDefine {
    /**该枚举中定义了{@link VariableBeanDefine}类可以表示的基本类型。*/
    public enum VariableType {
        /**null数据。*/
        Null,
        /**布尔类型。*/
        Boolean,
        /**字节类型。*/
        Byte,
        /**短整数类型。*/
        Short,
        /**整数类型。*/
        Int,
        /**长整数类型。*/
        Long,
        /**单精度浮点数类型。*/
        Float,
        /**双精度浮点数类型。*/
        Double,
        /**字符类型。*/
        Char,
        /**字符串类型。*/
        String,
        /**时间类型*/
        Date,
    }
    /**根据枚举获取其基本类型Class。*/
    public static Class<?> getBaseType(VariableType typeEnum) {
        if (typeEnum == VariableType.Boolean)
            return boolean.class;
        else if (typeEnum == VariableType.Byte)
            return byte.class;
        else if (typeEnum == VariableType.Short)
            return short.class;
        else if (typeEnum == VariableType.Int)
            return int.class;
        else if (typeEnum == VariableType.Long)
            return long.class;
        else if (typeEnum == VariableType.Float)
            return float.class;
        else if (typeEnum == VariableType.Double)
            return double.class;
        else if (typeEnum == VariableType.Char)
            return char.class;
        else if (typeEnum == VariableType.String)
            return String.class;
        else if (typeEnum == VariableType.Date)
            return Date.class;
        else
            return null;
    }
    /**根据枚举获取其包装类型Class。*/
    public static Class<?> getPackType(VariableType typeEnum) {
        if (typeEnum == VariableType.Boolean)
            return Boolean.class;
        else if (typeEnum == VariableType.Byte)
            return Byte.class;
        else if (typeEnum == VariableType.Short)
            return Short.class;
        else if (typeEnum == VariableType.Int)
            return Integer.class;
        else if (typeEnum == VariableType.Long)
            return Long.class;
        else if (typeEnum == VariableType.Float)
            return Float.class;
        else if (typeEnum == VariableType.Double)
            return Double.class;
        else if (typeEnum == VariableType.Char)
            return Character.class;
        else if (typeEnum == VariableType.String)
            return String.class;
        else if (typeEnum == VariableType.Date)
            return Date.class;
        else
            return null;
    }
    //------------------------------------------------------------------
    private VariableType type   = VariableType.Null; //值类型
    private Object       value  = null;             //值
    private String       format = null;             //数据格式化
    /**获取值类型。*/
    public VariableType getType() {
        return type;
    }
    /**设置值类型。*/
    public void setType(VariableType type) {
        this.type = type;
    }
    /**获取值*/
    public Object getValue() {
        return value;
    }
    /**设置值*/
    public void setValue(Object value) {
        this.value = value;
    }
    /**获取值的数据格式化*/
    public String getFormat() {
        return format;
    }
    /**设置值的数据格式化*/
    public void setFormat(String format) {
        this.format = format;
    }
    public TemplateBeanDefine getUseTemplate() {
        return null;
    }
    public void setUseTemplate(TemplateBeanDefine useTemplate) {}
}