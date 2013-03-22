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
package org.platform.api.orm.meta;
import java.util.ArrayList;
import java.util.List;
/**
 * 扩展属性定义
 * @version : 2013-1-27
 * @author 赵永春 (zyc@byshell.org)
 */
public class ExtAttMeta extends AttMeta {
    /**扩展表名。*/
    private String        extTable     = "";
    /**扩展表的主键列。*/
    private String        extPKColumn  = "";
    /**扩展表的外键列。*/
    private String        extFKColumn  = "";
    /**扩展属性模式（column将列名作为扩展属性，row将记录作为扩展属性）*/
    private ExtModeEnum   extMode      = ExtModeEnum.Row;
    /**extMode在row模式下存放属性名的列名。*/
    private String        extKeyColumn = "";
    /**extMode在row模式下存放属性值的列名。*/
    private String        extVarColumn = "";
    /**属性元素*/
    private List<AttMeta> attList      = new ArrayList<AttMeta>();
    //
    //
    //
    public String getExtTable() {
        return extTable;
    }
    public void setExtTable(String extTable) {
        this.extTable = extTable;
    }
    public String getExtPKColumn() {
        return extPKColumn;
    }
    public void setExtPKColumn(String extPKColumn) {
        this.extPKColumn = extPKColumn;
    }
    public String getExtFKColumn() {
        return extFKColumn;
    }
    public void setExtFKColumn(String extFKColumn) {
        this.extFKColumn = extFKColumn;
    }
    public ExtModeEnum getExtMode() {
        return extMode;
    }
    public void setExtMode(ExtModeEnum extMode) {
        this.extMode = extMode;
    }
    public String getExtKeyColumn() {
        return extKeyColumn;
    }
    public void setExtKeyColumn(String extKeyColumn) {
        this.extKeyColumn = extKeyColumn;
    }
    public String getExtVarColumn() {
        return extVarColumn;
    }
    public void setExtVarColumn(String extVarColumn) {
        this.extVarColumn = extVarColumn;
    }
    public List<AttMeta> getAttList() {
        return attList;
    }
    public void setAttList(List<AttMeta> attList) {
        this.attList = attList;
    }
}