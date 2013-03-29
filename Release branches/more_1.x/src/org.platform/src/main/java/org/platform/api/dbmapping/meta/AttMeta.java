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
package org.platform.api.dbmapping.meta;
/**
 * 基本属性
 * @version : 2013-1-27
 * @author 赵永春 (zyc@byshell.org)
 */
public class AttMeta {
    /**属性名*/
    private String   name         = "";
    /**属性映射到数据库的列名*/
    private String   column       = "";
    /**属性是否允许空值*/
    private boolean  notNull      = true;
    /**数据长度（在某些特定类型下该值的设置无效）*/
    private int      length       = 1000;
    /**默认值*/
    private String   defaultValue = "";
    /**属性是否可以作为保存数据时的一员。*/
    private boolean  update       = true;
    /**属性是否可以作为新增记录的一员。*/
    private boolean  insert       = true;
    /**该属性是否可以被延迟装载。*/
    private boolean  lazy         = false;
    /**属性在数据库中的数据类型。*/
    private String   dbType       = "varchar";
    /**属性映射的类型*/
    private TypeEnum type         = TypeEnum.TString;
    //
    //
    //
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getColumn() {
        return column;
    }
    public void setColumn(String column) {
        this.column = column;
    }
    public boolean isNotNull() {
        return notNull;
    }
    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }
    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    public boolean isUpdate() {
        return update;
    }
    public void setUpdate(boolean update) {
        this.update = update;
    }
    public boolean isInsert() {
        return insert;
    }
    public void setInsert(boolean insert) {
        this.insert = insert;
    }
    public boolean isLazy() {
        return lazy;
    }
    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }
    public String getDbType() {
        return dbType;
    }
    public void setDbType(String dbType) {
        this.dbType = dbType;
    }
    public TypeEnum getType() {
        return type;
    }
    public void setType(TypeEnum type) {
        this.type = type;
    }
}