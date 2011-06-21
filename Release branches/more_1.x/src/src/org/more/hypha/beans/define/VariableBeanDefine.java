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
 * VariableBeanDefine类用于定义一个值作为bean。该bean可以描述基本类型和字符串类型。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class VariableBeanDefine extends AbstractBaseBeanDefine {
    private VariableType type  = null; //值类型
    private String       value = null; //值
    /**返回“VariableBean”。*/
    public String getBeanType() {
        return "VariableBean";
    }
    /**获取值类型。*/
    public VariableType getType() {
        return this.type;
    }
    /**设置值类型。*/
    public void setType(VariableType type) {
        this.type = type;
    }
    /**获取值*/
    public String getValue() {
        return this.value;
    }
    /**设置值*/
    public void setValue(String value) {
        this.value = value;
    }
    public void setUseTemplate(TemplateBeanDefine useTemplate) {}
    //------------------------------------------------------------------
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
    };
    /**将字符串描述转换为{@link VariableType}枚举。*/
    public static VariableType getVariableType(String type) {
        return (VariableType) StringConvertUtil.parseEnum(type, VariableType.class);
    };
    /**根据枚举获取其基本类型Class。*/
    public static Class<?> getType(VariableType typeEnum) {
        if (typeEnum == null)
            return null;
        else if (typeEnum == VariableType.Boolean)
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
        else
            return null;
    }
}