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
 * 
 * Date : 2009-11-4
 * @author Administrator
 */
public class BeanProperty extends AttBase {
    public static final String TS_Integer       = "int";
    public static final String TS_Byte          = "byte";
    public static final String TS_Char          = "char";
    public static final String TS_Double        = "double";
    public static final String TS_Float         = "float";
    public static final String TS_Long          = "long";
    public static final String TS_Short         = "short";
    public static final String TS_Boolean       = "boolean";
    public static final String TS_String        = "java.lang.String";
    public static final String TS_Array         = "Array";
    public static final String TS_List          = "List";
    public static final String TS_Map           = "Map";
    public static final String TS_Set           = "Set";
    /**  */
    private static final long  serialVersionUID = -3492072515778133870L;
    private String             id               = null;                 //
    private String             name             = null;                 //
    private String             propType         = null;                 //
    private String             refBean          = null;                 //
    private String             value            = null;                 //
    //=================================================================
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getRefBean() {
        return refBean;
    }
    public void setRefBean(String refBean) {
        this.refBean = refBean;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getPropType() {
        return propType;
    }
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