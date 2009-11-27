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
 * 整个more.beans中最基本的核心类，Prop用于表示一个拥有具体类型的bean定义资源。
 * 在该类中定了一些基本类型，这些基本类型的定义有助于优化性能。但是基本类型的
 * 包装类型例如int的包装类型Integer则不属于基本类型范畴。除了java常见的八个基本类型之外BeanProperty还针对java.lang.String做了定义。
 * BeanProperty作为属性对象必须设置propType属性，propType有一些预定义的值请查看BeanProperty的静态字段。
 * <br/>Date : 2009-11-18
 * @author 赵永春
 */
public abstract class Prop extends AttBase {
    //========================================================================================Field
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
    private static final long  serialVersionUID = -3350281122432032642L;
    private String             id               = null;                 //唯一的Bean ID
    private String             propType         = null;                 //该类型应该可以通过BeanFactory.getBeanClassLoader().loadClass()获取
    //==========================================================================================Job
    /**获取唯一的Bean ID。*/
    public String getId() {
        return id;
    }
    /**设置唯一的Bean ID。*/
    public void setId(String id) {
        this.id = id;
    }
    /**获取属性的类型（必须配置）部分属性类型由TS_常量定义。*/
    public String getPropType() {
        return propType;
    }
    /**设置属性的类型（必须配置）部分属性类型由TS_常量定义。*/
    public void setPropType(String propType) {
        if (propType.equals("int") == true)
            this.propType = Prop.TS_Integer;
        else if (propType.equals("byte") == true)
            this.propType = Prop.TS_Byte;
        else if (propType.equals("char") == true)
            this.propType = Prop.TS_Char;
        else if (propType.equals("double") == true)
            this.propType = Prop.TS_Double;
        else if (propType.equals("float") == true)
            this.propType = Prop.TS_Float;
        else if (propType.equals("long") == true)
            this.propType = Prop.TS_Long;
        else if (propType.equals("short") == true)
            this.propType = Prop.TS_Short;
        else if (propType.equals("boolean") == true)
            this.propType = Prop.TS_Boolean;
        else if (propType.equals("String") == true)
            this.propType = Prop.TS_String;
        else
            this.propType = propType;
    }
    /***/
    public static Class<?> getType(String propType, ClassLoader loader) throws Exception {
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
        else
            return loader.loadClass(propType);
    }
}