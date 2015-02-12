/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.db.ar.record;
import java.io.Serializable;
import java.sql.Types;
/**
 * 
 * @version : 2014年10月27日
 * @author 赵永春(zyc@hasor.net)
 */
public final class Column implements Serializable {
    private static final long serialVersionUID = 7779635033083814425L;
    private String            name             = null;                //字段名
    private String            title            = null;                //字段title
    private int               sqlType          = Types.NULL;          //SQL类型
    //
    private boolean           primaryKey       = false;               //主键约束
    private Integer           maxSize          = null;                //字段值大小限制
    private boolean           identify         = false;               //具有自增标识属性的列。
    private boolean           empty            = true;                //非空约束
    private Object            defaultValue     = null;                //默认约束
    private boolean           insert           = true;                //是否允许用于数据新增
    private boolean           update           = true;                //是否允许用于数据更新
    //
    public Column(String colName, int colSQLType) {
        this.name = colName;
        this.sqlType = colSQLType;
    }
    //
    //
    /**获取列名*/
    public String getName() {
        return this.name;
    }
    /**获取列标题*/
    public String getTitle() {
        return this.title;
    }
    /**设置列标题*/
    public void setTitle(String title) {
        this.title = title;
    }
    /**获取列表示的 SQL 类型。
     * @see java.sql.Types*/
    public int getSqlType() {
        return this.sqlType;
    }
    /**获取列表示的Java类型。*/
    public Class<?> getJavaType() {
        return InnerArUtils.sqlTypeToJavaType(getSqlType());
    }
    //
    /**表示列是否为主键列。*/
    public boolean isPrimaryKey() {
        return this.primaryKey;
    }
    /**字段值内容大小限制（0表示不详）*/
    public int getMaxSize() {
        return this.maxSize == null ? 0 : this.maxSize.intValue();
    }
    /**表示是否具有自增标识属性的列。*/
    public boolean isIdentify() {
        return this.identify;
    }
    /**表示列是否在insert时将实际数据用于新增(true)，如果不是新增将采用默认值约束(false)。*/
    public boolean allowInsert() {
        return this.insert;
    }
    /**表示列是否在update时将实际数据用于更新(true)，如果不是则忽略对该列的更新(false)。*/
    public boolean allowUpdate() {
        return this.update;
    }
    /**表示列是否允许空值出现。*/
    public boolean allowEmpty() {
        return this.empty;
    }
    /**表示默认值约束的值。*/
    public Object getDefaultValue() {
        return this.defaultValue;
    }
    /**设置修改默认值约束.*/
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
    /**设置允许用于数据新增*/
    public void setAllowInsert(boolean insert) {
        this.insert = insert;
    }
    /**设置允许用于数据更新*/
    public void setAllowUpdate(boolean update) {
        this.update = update;
    }
    //
    /**设置是否为主键列*/
    protected void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }
    /**设置是否支持自动增长。*/
    protected void setIdentify(boolean identify) {
        this.identify = identify;
    }
    /**设置是否允许为空*/
    protected void setEmpty(boolean empty) {
        this.empty = empty;
    }
    /**设置字段的大小限制*/
    protected void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
}