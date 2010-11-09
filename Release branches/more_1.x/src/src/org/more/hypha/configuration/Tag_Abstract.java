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
package org.more.hypha.configuration;
import java.lang.reflect.Method;
import java.util.Date;
import org.more.DoesSupportException;
import org.more.PropertyException;
import org.more.hypha.beans.define.VariableBeanDefine;
import org.more.util.StringConvert;
import org.more.util.StringUtil;
/**
 * namespace包中凡是涉及解析xml的类都需要集成的类，该类目的是为了提供一个统一的{@link DefineResourceImpl}对象获取接口和一些Tag解析时的公共方法。
 * @version 2010-9-23
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class Tag_Abstract {
    private DefineResourceImpl configuration = null;
    /**创建Tag_Abstract类型*/
    public Tag_Abstract(DefineResourceImpl configuration) {
        this.configuration = configuration;
    }
    /**获取{@link DefineResourceImpl}类型*/
    protected DefineResourceImpl getConfiguration() {
        return this.configuration;
    }
    //================================================================================================================工具性方法
    /**该枚举中定义了{@link VariableBeanDefine}类可以表示的基本类型。*/
    protected enum VariableType {
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
    protected static Class<?> getBaseType(VariableType typeEnum) {
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
        else if (typeEnum == VariableType.Date)
            return Date.class;
        else
            return null;
    }
    /**查找某个名称的方法，该方法必须有一个参数。*/
    private Method findMethod(String methodName, Class<?> type) {
        for (Method m : type.getMethods())
            if (m.getName().equals(methodName) == true)
                if (m.getParameterTypes().length == 1)
                    return m;
        return null;
    }
    /**执行属性注入，除了注入int,short,long,等基本类型之外该方法还支持注入枚举类型。*/
    protected final void putAttribute(Object define, String attName, Object value) {
        if (define == null || attName == null)
            throw new NullPointerException("定义对象或者要注入的属性名为空。");
        //1.查找方法
        String methodName = "set" + StringUtil.toUpperCase(attName);
        Method writeMethod = this.findMethod(methodName, define.getClass());
        if (writeMethod == null)
            throw new DoesSupportException(define.getClass().getSimpleName() + "：对象不支持写属性[" + attName + "]操作(不存在[" + methodName + "]方法。)");
        //2.执行属性转换
        Class<?> toType = writeMethod.getParameterTypes()[0];
        Object attValueObject = StringConvert.changeType(value, toType);
        //3.执行属性注入
        try {
            writeMethod.invoke(define, attValueObject);
        } catch (Exception e) {
            throw new PropertyException("在Method.invoke期间发生异常，无法将" + attName + ",属性写入[" + define + "]对象：" + e.getMessage());
        }
    };
}