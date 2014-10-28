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
package net.hasor.db.ar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.util.BeanUtils;
/**
 * 表示数据库的表结构
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class Sechma {
    private String              name        = null;
    private Column              idColumn    = null;
    private Identify            identify    = null;
    private Column[]            columnArray = new Column[0];
    private Map<String, Column> columnMap   = new HashMap<String, Column>();
    //
    /**取得表名*/
    public String getName() {
        return this.name;
    }
    /**获取主键列*/
    public Column getID() {
        return this.idColumn;
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
    /**通过列定义创建一个空{@link Entity}*/
    public Entity defaultEntity() {
        Entity ent = new Entity(this);
        for (Column atCol : this.getColumns()) {
            //if (col.isPrimaryKey()==true){ }
            Object colValue = atCol.getDefaultValue();
            if (atCol.allowEmpty() == false && colValue == null) {
                colValue = BeanUtils.getDefaultValue(atCol.getJavaType());
                if (atCol.getJavaType() == String.class)
                    colValue = "";
            }
            if (colValue != null) {
                ent.set(atCol, colValue);
            }
        }
        return ent;
    }
    /**添加列*/
    protected void addColumn(Column column) {
        if (column.isPrimaryKey())
            this.idColumn = column;
        //
        this.columnMap.put(column.getName(), column);
        Column[] newColumnArray = new Column[this.columnArray.length + 1];
        System.arraycopy(this.columnArray, 0, newColumnArray, 0, this.columnArray.length);
        newColumnArray[newColumnArray.length - 1] = column;
        this.columnArray = newColumnArray;
    }
    /**用于判断两涨表是否同源（同源是指出自于同一数据库）*/
    public boolean isHomology(Sechma sechma) {
        return this.getDataBase().equals(sechma.getDataBase());
    }
    /**是否支持标识符*/
    public boolean supportIdentify() {
        return this.getIdentify() != null;
    }
    /**获取标识符生成器*/
    public Identify getIdentify() {
        return this.identify;
    }
    //
    //
    //
    //
    //
    //
    public int hashCode() {
        // TODO Auto-generated method stub
        return super.hashCode();
    }
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return super.equals(obj);
    }
    public DataBase getDataBase();
}