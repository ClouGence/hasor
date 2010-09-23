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
import org.more.beans.ValueMetaData;
/**
* ValueMetaData的一个临时实现
* @version 2010-9-21
* @author 赵永春 (zyc@byshell.org)
*/
public class QuickProperty_ValueMetaData extends ValueMetaData {
    private String value       = null; //value
    private String enumeration = null; //enum
    private String date        = null; //date
    private String format      = null; //format
    private String refBean     = null; //refBean
    private String refScope    = null; //refScope
    private String file        = null; //file
    private String directory   = null; //directory
    private String uriLocation = null; //uriLocation
    /**返回null*/
    public PropertyMetaTypeEnum getPropertyType() {
        return null;
    }
    /**获取 xml attribute value*/
    public String getValue() {
        return value;
    }
    /**设置 xml attribute value*/
    public void setValue(String value) {
        this.value = value;
    }
    /**获取 xml attribute enum*/
    public String getEnumeration() {
        return enumeration;
    }
    /**设置 xml attribute enum*/
    public void setEnumeration(String enumeration) {
        this.enumeration = enumeration;
    }
    /**获取 xml attribute date*/
    public String getDate() {
        return date;
    }
    /**设置 xml attribute date*/
    public void setDate(String date) {
        this.date = date;
    }
    /**获取 xml attribute format*/
    public String getFormat() {
        return format;
    }
    /**设置 xml attribute format*/
    public void setFormat(String format) {
        this.format = format;
    }
    /**获取 xml attribute refBean*/
    public String getRefBean() {
        return refBean;
    }
    /**设置 xml attribute refBean*/
    public void setRefBean(String refBean) {
        this.refBean = refBean;
    }
    /**获取 xml attribute refScope*/
    public String getRefScope() {
        return refScope;
    }
    /**设置 xml attribute refScope*/
    public void setRefScope(String refScope) {
        this.refScope = refScope;
    }
    /**获取 xml attribute file*/
    public String getFile() {
        return file;
    }
    /**设置 xml attribute file*/
    public void setFile(String file) {
        this.file = file;
    }
    /**获取 xml attribute directory*/
    public String getDirectory() {
        return directory;
    }
    /**设置 xml attribute directory*/
    public void setDirectory(String directory) {
        this.directory = directory;
    }
    /**获取 xml attribute uriLocation*/
    public String getUriLocation() {
        return uriLocation;
    }
    /**设置 xml attribute uriLocation*/
    public void setUriLocation(String uriLocation) {
        this.uriLocation = uriLocation;
    }
}