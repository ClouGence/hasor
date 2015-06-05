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
package net.hasor.db.orm.ar;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 表示数据库的表或结果集的结构
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public final class Sechma {
    private String              name        = null;
    private boolean             ignoreUnset = true;
    private Column              keyColumn   = null;
    private Column[]            columnArray = new Column[0];
    private Map<String, Column> columnMap   = new HashMap<String, Column>();
    //
    /**创建表记录对象。*/
    protected Sechma(String sechmaName) {
        if (sechmaName == null) {
            throw new NullPointerException("sechmaName is null.");
        }
        this.name = sechmaName;
    }
    //
    /**没有给它设置任何值的情况下忽略它作为查询条件*/
    public boolean isIgnoreUnset() {
        return this.ignoreUnset;
    }
    /**没有给它设置任何值的情况下忽略它作为查询条件*/
    protected void setIgnoreUnset(boolean ignoreUnset) {
        this.ignoreUnset = ignoreUnset;
    }
    /**取得表名*/
    public String getName() {
        return this.name;
    }
    /**获取ID列名。*/
    public Column getPrimaryKey() {
        return this.keyColumn;
    }
    /**根据类名获取列*/
    public Column getColumn(String columnName) {
        return this.columnMap.get(columnName);
    }
    /**根据列的顺序获取列*/
    public Column getColumn(int columnIndex) {
        return this.columnArray[columnIndex];
    }
    /**获取所有列*/
    public Column[] getColumns() {
        return this.columnArray;
    }
    /**获取可以用于insert的列。*/
    protected Column[] getInsertColumns() {
        List<Column> insertCols = new ArrayList<Column>();
        for (Column col : this.columnArray) {
            if (col.allowInsert())
                insertCols.add(col);
        }
        return insertCols.toArray(new Column[insertCols.size()]);
    }
    /**获取可以用于update的列。*/
    protected Column[] getUpdateColumns() {
        List<Column> updateCols = new ArrayList<Column>();
        for (Column col : this.columnArray) {
            if (col.allowUpdate())
                updateCols.add(col);
        }
        return updateCols.toArray(new Column[updateCols.size()]);
    }
    /**添加列*/
    protected void addColumn(Column column) {
        if (column.isPrimaryKey() == true) {
            this.keyColumn = column;
        }
        //
        this.columnMap.put(column.getName(), column);
        Column[] newColumnArray = new Column[this.columnArray.length + 1];
        System.arraycopy(this.columnArray, 0, newColumnArray, 0, this.columnArray.length);
        newColumnArray[newColumnArray.length - 1] = column;
        this.columnArray = newColumnArray;
    }
}