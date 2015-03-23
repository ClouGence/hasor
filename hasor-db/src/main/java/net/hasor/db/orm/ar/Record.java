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
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.more.convert.ConverterUtils;
import org.more.util.BeanUtils;
/**
 * 用来表示查询结果中的一条数据记录
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class Record implements Cloneable, Serializable {
    private static final long serialVersionUID = 7553043036092551721L;
    private final Sechma      sechma;
    //
    /**创建{@link Record}并用具体数据填充。*/
    public Record(Sechma sechma) {
        if (sechma == null) {
            throw new NullPointerException("sechma is null.");
        }
        this.sechma = sechma;
    }
    //
    /**获取数据容器。*/
    protected abstract Map<String, Object> getDataContainer();
    /**获取记录所属的Sechma（表）。*/
    protected Sechma getSechma() {
        return this.sechma;
    }
    /**获取指定列的值。*/
    protected Object[] columnValues(Column[] columnArrays) {
        if (columnArrays == null)
            return null;
        Object[] arrays = new Object[columnArrays.length];
        Map<String, Object> dataContainer = this.getDataContainer();
        //
        for (int i = 0; i < columnArrays.length; i++) {
            arrays[i] = dataContainer.get(columnArrays[i].getName());
        }
        return arrays;
    }
    /**获取有值的列*/
    protected Column[] hasValueColumns(Column[] refColumn) {
        Sechma sechma = this.getSechma();
        if (refColumn == null) {
            refColumn = sechma.getColumns();
        }
        Set<String> dataKeys = getDataContainer().keySet();
        //
        List<Column> finalColumn = new ArrayList<Column>();
        for (Column columnKey : refColumn) {
            if (dataKeys.contains(columnKey.getName()) == false)
                continue;
            finalColumn.add(columnKey);
        }
        //
        return finalColumn.toArray(new Column[finalColumn.size()]);
    }
    //
    /**为记录的每一列数据给定一个默认值（不覆盖原有属性）。*/
    public void initDefault() {
        Sechma sechma = this.getSechma();
        for (Column atCol : sechma.getColumns()) {
            if (this.isNull(atCol.getName()) == false) {
                continue;
            }
            //if (col.isPrimaryKey()==true){ }
            Object colValue = atCol.getDefaultValue();
            if (atCol.allowEmpty() == false && colValue == null) {
                colValue = BeanUtils.getDefaultValue(atCol.getJavaType());
                if (atCol.getJavaType() == String.class) {
                    colValue = "";
                }
            }
            if (colValue != null) {
                this.set(atCol, colValue);
            }
        }
    }
    /**克隆一个新的{@link Record}*/
    public abstract Object clone() throws CloneNotSupportedException;
    /**按照列名获取数据。*/
    public abstract Object get(String column);
    /**按照列索引获取数据。*/
    public Object get(int column) {
        /*使用Column包装了 name*/
        return this.get(this.getSechma().getColumn(column).getName());
    }
    /**按照列获取数据。*/
    public Object get(Column column) {
        return this.get(column.getName());
    }
    //
    /**按照列名获取数据。*/
    public abstract Record set(String column, Object var);
    /**按照列索引设置数据。*/
    public Record set(int column, Object var) {
        return this.set(this.getSechma().getColumn(column).getName(), var);
    }
    /**按照列设置数据。*/
    public Record set(Column column, Object var) {
        return this.set(column.getName(), var);
    }
    /**按照列名获取数据。*/
    public Record setMap(Map<String, Object> dataMap) {
        if (dataMap == null || dataMap.isEmpty()) {
            return this;
        }
        Sechma sechma = this.getSechma();
        for (Entry<String, Object> ent : dataMap.entrySet()) {
            this.set(sechma.getColumn(ent.getKey()), ent.getValue());
        }
        return this;
    }
    //
    /**获取ID数据。*/
    public Object getID() {
        return this.get(this.getSechma().getPrimaryKey());
    }
    /**设置ID数据。*/
    public Record setID(Object var) {
        return this.set(this.getSechma().getPrimaryKey(), var);
    }
    /**判断某个字段是否存在。*/
    public boolean hasValue(String columnName) {
        return this.getDataContainer().containsKey(columnName);
    }
    /**判断某个字段是否存在。*/
    public boolean hasColumn(String columnName) {
        return this.getSechma().getColumn(columnName) != null;
    }
    /**判断某个字段是否为空。*/
    public boolean isNull(String column) {
        return this.get(column) == null;
    }
    /**判断某个字段是否为空。*/
    public boolean isNull(int column) {
        return this.get(column) == null;
    }
    /**将某个字段转换为char形式格式返回。*/
    public char asChar(String column) {
        return this.toType(this.get(column), char.class, Character.valueOf((char) 0));
    }
    /**将某个字段转换为char形式格式返回，第二个参数为默认值。*/
    public char asChar(String column, char defaultValue) {
        return this.toType(this.get(column), char.class, defaultValue);
    }
    /**将某个字段转换为char形式格式返回。*/
    public char asChar(int column) {
        return this.toType(this.get(column), char.class, Character.valueOf((char) 0));
    }
    /**将某个字段转换为char形式格式返回，第二个参数为默认值。*/
    public char asChar(int column, char defaultValue) {
        return this.toType(this.get(column), char.class, defaultValue);
    }
    //
    /**将某个字段转换为{@link String}形式格式返回。*/
    public String asString(String column) {
        return this.toType(this.get(column), String.class, null);
    }
    /**将某个字段转换为{@link String}形式格式返回，第二个参数为默认值。*/
    public String asString(String column, String defaultValue) {
        return this.toType(this.get(column), String.class, defaultValue);
    }
    /**将某个字段转换为{@link String}形式格式返回。*/
    public String asString(int column) {
        return this.toType(this.get(column), String.class, null);
    }
    /**将某个字段转换为{@link String}形式格式返回，第二个参数为默认值。*/
    public String asString(int column, String defaultValue) {
        return this.toType(this.get(column), String.class, defaultValue);
    }
    //
    /**将某个字段转换为boolean形式格式返回。*/
    public boolean asBoolean(String column) {
        return this.toType(this.get(column), boolean.class, false);
    }
    /**将某个字段转换为boolean形式格式返回，第二个参数为默认值。*/
    public boolean asBoolean(String column, boolean defaultValue) {
        return this.toType(this.get(column), boolean.class, defaultValue);
    }
    /**将某个字段转换为boolean形式格式返回。*/
    public boolean asBoolean(int column) {
        return this.toType(this.get(column), boolean.class, false);
    }
    /**将某个字段转换为boolean形式格式返回，第二个参数为默认值。*/
    public boolean asBoolean(int column, boolean defaultValue) {
        return this.toType(this.get(column), boolean.class, defaultValue);
    }
    //
    /**将某个字段转换为short形式格式返回。*/
    public short asShort(String column) {
        return this.toType(this.get(column), short.class, Short.valueOf((short) 0));
    }
    /**将某个字段转换为short形式格式返回，第二个参数为默认值。*/
    public short asShort(String column, short defaultValue) {
        return this.toType(this.get(column), short.class, defaultValue);
    }
    /**将某个字段转换为short形式格式返回。*/
    public short asShort(int column) {
        return this.toType(this.get(column), short.class, Short.valueOf((short) 0));
    }
    /**将某个字段转换为short形式格式返回，第二个参数为默认值。*/
    public short asShort(int column, short defaultValue) {
        return this.toType(this.get(column), short.class, defaultValue);
    }
    //
    /**将某个字段转换为int形式格式返回。*/
    public int asInt(String column) {
        return this.toType(this.get(column), int.class, 0);
    }
    /**将某个字段转换为int形式格式返回，第二个参数为默认值。*/
    public int asInt(String column, int defaultValue) {
        return this.toType(this.get(column), int.class, defaultValue);
    }
    /**将某个字段转换为int形式格式返回。*/
    public int asInt(int column) {
        return this.toType(this.get(column), int.class, 0);
    }
    /**将某个字段转换为int形式格式返回，第二个参数为默认值。*/
    public int asInt(int column, int defaultValue) {
        return this.toType(this.get(column), int.class, defaultValue);
    }
    //
    /**将某个字段转换为long形式格式返回。*/
    public long asLong(String column) {
        return this.toType(this.get(column), long.class, 0);
    }
    /**将某个字段转换为long形式格式返回，第二个参数为默认值。*/
    public long asLong(String column, long defaultValue) {
        return this.toType(this.get(column), long.class, defaultValue);
    }
    /**将某个字段转换为long形式格式返回。*/
    public long asLong(int column) {
        return this.toType(this.get(column), long.class, 0);
    }
    /**将某个字段转换为long形式格式返回，第二个参数为默认值。*/
    public long asLong(int column, long defaultValue) {
        return this.toType(this.get(column), long.class, defaultValue);
    }
    //
    /**将某个字段转换为float形式格式返回。*/
    public float asFloat(String column) {
        return this.toType(this.get(column), float.class, 0);
    }
    /**将某个字段转换为float形式格式返回，第二个参数为默认值。*/
    public float asFloat(String column, float defaultValue) {
        return this.toType(this.get(column), float.class, defaultValue);
    }
    /**将某个字段转换为float形式格式返回。*/
    public float asFloat(int column) {
        return this.toType(this.get(column), float.class, 0);
    }
    /**将某个字段转换为float形式格式返回，第二个参数为默认值。*/
    public float asFloat(int column, float defaultValue) {
        return this.toType(this.get(column), float.class, defaultValue);
    }
    //
    /**将某个字段转换为double形式格式返回。*/
    public double asDouble(String column) {
        return this.toType(this.get(column), double.class, 0);
    }
    /**将某个字段转换为double形式格式返回，第二个参数为默认值。*/
    public double asDouble(String column, double defaultValue) {
        return this.toType(this.get(column), double.class, defaultValue);
    }
    /**将某个字段转换为double形式格式返回。*/
    public double asDouble(int column) {
        return this.toType(this.get(column), double.class, 0);
    }
    /**将某个字段转换为double形式格式返回，第二个参数为默认值。*/
    public double asDouble(int column, double defaultValue) {
        return this.toType(this.get(column), double.class, defaultValue);
    }
    //
    /**将某个字段转换为{@link Date}形式格式返回。*/
    public Date asDate(String column) {
        return this.toType(this.get(column), Date.class, null);
    }
    /**将某个字段转换为{@link Date}形式格式返回，第二个参数为默认值。*/
    public Date asDate(String column, Date defaultValue) {
        return this.toType(this.get(column), Date.class, defaultValue);
    }
    /**将某个字段转换为{@link Date}形式格式返回，第二个参数为默认值。*/
    public Date asDate(String column, long defaultValue) {
        return this.toType(this.get(column), Date.class, new Date(defaultValue));
    }
    /**将某个字段转换为{@link Date}形式格式返回，第二个参数为时间日期格式。*/
    public Date asDate(String column, String format) {
        return this.asDate(column, format, null);
    }
    /**将某个字段转换为{@link Date}形式格式返回，第二个参数为时间日期格式，第三个参数为默认值。*/
    public Date asDate(String column, String format, Date defaultValue) {
        String oriData = this.toType(this.get(column), String.class, null);
        if (oriData == null || oriData.length() == 0) {
            return defaultValue;
        }
        return this.str2Date(oriData, format, defaultValue);
    }
    /**将某个字段转换为{@link Date}形式格式返回，第二个参数为时间日期格式，第三个参数为默认值。*/
    public Date asDate(String column, String format, long defaultValue) {
        return this.asDate(column, format, new Date(defaultValue));
    }
    /**将某个字段转换为{@link Date}形式格式返回。*/
    public Date asDate(int column) {
        return this.toType(this.get(column), Date.class, null);
    }
    /**将某个字段转换为{@link Date}形式格式返回，第二个参数为默认值。*/
    public Date asDate(int column, Date defaultValue) {
        return this.toType(this.get(column), Date.class, defaultValue);
    }
    /**将某个字段转换为{@link Date}形式格式返回，第二个参数为默认值。*/
    public Date asDate(int column, long defaultValue) {
        return this.toType(this.get(column), Date.class, new Date(defaultValue));
    }
    /**将某个字段转换为{@link Date}形式格式返回，第二个参数为时间日期格式。*/
    public Date asDate(int column, String format) {
        return this.asDate(column, format, null);
    }
    /**将某个字段转换为{@link Date}形式格式返回，第二个参数为时间日期格式，第三个参数为默认值。*/
    public Date asDate(int column, String format, Date defaultValue) {
        String oriData = this.toType(this.get(column), String.class, null);
        if (oriData == null || oriData.length() == 0) {
            return defaultValue;
        }
        return this.str2Date(oriData, format, defaultValue);
    }
    /**将某个字段转换为{@link Date}形式格式返回，第二个参数为时间日期格式，第三个参数为默认值。*/
    public Date asDate(int column, String format, long defaultValue) {
        return this.asDate(column, format, new Date(defaultValue));
    }
    private Date str2Date(String oriData, String format, Date defaultValue) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        ParsePosition pos = new ParsePosition(0);
        dateFormat.setLenient(false);
        Date parsedDate = dateFormat.parse(oriData, pos); // ignore the result (use the Calendar)
        if (pos.getErrorIndex() >= 0 || pos.getIndex() != oriData.length() || parsedDate == null) {
            return defaultValue;
        } else {
            return parsedDate;
        }
    }
    //
    /**将某个字段转换为{@link Enum}形式格式返回。*/
    public <T extends Enum<?>> T asEnum(String column, Class<T> enmType) {
        return this.toType(this.get(column), enmType, null);
    }
    /**将某个字段转换为{@link Enum}形式格式返回，第三个参数为默认值。*/
    public <T extends Enum<?>> T asEnum(String column, Class<T> enmType, T defaultValue) {
        return this.toType(this.get(column), enmType, defaultValue);
    }
    /**将某个字段转换为{@link Enum}形式格式返回。*/
    public <T extends Enum<?>> T asEnum(int column, Class<T> enmType) {
        return this.toType(this.get(column), enmType, null);
    }
    /**将某个字段转换为{@link Enum}形式格式返回，第三个参数为默认值。*/
    public <T extends Enum<?>> T asEnum(int column, Class<T> enmType, T defaultValue) {
        return this.toType(this.get(column), enmType, defaultValue);
    }
    //
    @SuppressWarnings("unchecked")
    private <T> T toType(Object oriData, Class<?> toType, T defaultValue) {
        if (oriData == null) {
            return defaultValue;
        }
        return (T) ConverterUtils.convert(toType, oriData);
    }
}