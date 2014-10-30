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
package net.hasor.db.ar.support;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.hasor.db.ar.PageResult;
import net.hasor.db.ar.Paginator;
import net.hasor.db.jdbc.JdbcOperations;
import net.hasor.db.jdbc.RowMapper;
import net.hasor.db.jdbc.core.LinkedCaseInsensitiveMap;
import net.hasor.db.jdbc.core.mapper.ColumnMapRowMapper;
import org.more.convert.ConverterUtils;
/**
 * 用来表示表的数据记录
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class Entity implements Cloneable, Serializable {
    private static final long   serialVersionUID = 7553043036092551721L;
    private Sechma              sechma           = null;
    private Map<String, Object> dataContainer    = null;
    //
    /**创建表记录对象。*/
    public Entity(Sechma sechma) {
        this(sechma, null);
    }
    /**创建表记录对象，并用具体数据填充。*/
    public Entity(Entity entity, Map<String, Object> dataContainer) {
        this(entity.getSechma(), dataContainer);
    }
    /**创建表记录对象，并用具体数据填充。*/
    public Entity(Sechma sechma, Map<String, Object> dataContainer) {
        if (sechma == null) {
            throw new NullPointerException("sechma is null.");
        }
        this.sechma = sechma;
        this.dataContainer = new LinkedCaseInsensitiveMap<Object>();
        if (dataContainer != null)
            this.dataContainer.putAll(dataContainer);
    }
    //
    /**获取记录所属的Sechma（表）。*/
    protected Sechma getSechma() {
        return this.sechma;
    }
    /**获取数据容器。*/
    protected Map<String, Object> getDataContainer() {
        return this.dataContainer;
    }
    /**获取JDBC接口*/
    protected JdbcOperations getJdbc() {
        return this.getSechma().getDataBase().getJdbc();
    }
    /**获取SQLBuilder接口*/
    protected SQLBuilder getSQLBuilder() {
        return this.getSechma().getDataBase().getSQLBuilder();
    }
    //
    /**克隆一个新的{@link Entity}*/
    public Object clone() throws CloneNotSupportedException {
        return new Entity(this.getSechma(), this.getDataContainer());
    }
    /**获取指定列的值。*/
    private Object[] columnValues(Column[] columnArrays) {
        if (columnArrays == null)
            return null;
        Object[] arrays = new Object[columnArrays.length];
        Map<String, Object> dataContainer = this.getDataContainer();
        //
        for (int i = 0; i <= columnArrays.length; i++) {
            arrays[i] = dataContainer.get(columnArrays[i].getName());
        }
        return arrays;
    }
    /**用于判断基于是否出自于一个Sechma。*/
    public boolean isHomology(Sechma sechma) {
        if (sechma == null)
            return false;
        return sechma.equals(this);
    }
    /**用于判断两条记录是否同源（同源是指出自于同一张表）*/
    public boolean isHomology(Entity dataContainer) {
        if (dataContainer == null)
            return false;
        return dataContainer.getSechma().equals(this);
    }
    //
    /**记录在数据库中是否存在。*/
    public int countByExample() throws SQLException {
        Sechma sechma = this.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        Column[] allColumn = this.hasValueColumns(sechma.getColumns());
        Object[] dataArrays = this.columnValues(allColumn);
        String countSQL = builder.buildCount(sechma, allColumn);
        return this.getJdbc().queryForInt(countSQL, dataArrays);
    }
    /**删除数据库中满足该对象特征的。*/
    public int deleteByExample() throws SQLException {
        Sechma sechma = this.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        //
        Column[] allColumn = this.hasValueColumns(sechma.getColumns());
        Object[] dataArrays = this.columnValues(allColumn);
        String deleteSQL = builder.buildDelete(sechma, allColumn);
        return this.getJdbc().update(deleteSQL, dataArrays);
    }
    /**更新数据库中满足该对象特征的。*/
    public int updateByExample(Entity dataContainer) throws SQLException {
        if (dataContainer == null || this.isHomology(dataContainer) == false)
            return 0;
        Sechma sechma = this.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        //
        Column[] whereColumn = this.hasValueColumns(sechma.getColumns());
        Column[] dataColumn = dataContainer.hasValueColumns(sechma.getUpdateColumns());
        Object[] whereArrays = this.columnValues(whereColumn);
        Object[] dataArrays = dataContainer.columnValues(dataColumn);
        //
        String updateSQL = builder.buildUpdate(sechma, whereColumn, dataColumn);
        Object[] updateData = new Object[whereArrays.length + dataArrays.length];
        System.arraycopy(whereArrays, 0, updateData, 0/*            */, whereArrays.length);
        System.arraycopy(dataArrays, 0, updateData, whereArrays.length, dataArrays.length);
        //
        return this.getJdbc().update(updateSQL, updateData);
    }
    /**更新数据库中满足该对象特征的。*/
    public int updateByExample(Map<String, Object> dataContainer) throws SQLException {
        if (dataContainer == null || dataContainer.isEmpty())
            return 0;
        return this.updateByExample(new Entity(this, dataContainer));
    }
    /**从数据库中查询满足该对象特征的。*/
    public PageResult<Entity> listByExample() throws SQLException {
        return this.listByExample(null);
    }
    /**从数据库中查询满足该对象特征的。*/
    public PageResult<Entity> listByExample(Paginator pageInfo) throws SQLException {
        final Sechma sechma = this.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        //
        Column[] whereColumn = this.hasValueColumns(sechma.getColumns());//所有列
        Object[] whereArrays = this.columnValues(whereColumn);
        //
        String selectSQL = builder.buildSelect(sechma, whereColumn, pageInfo);
        List<Entity> entList = this.getJdbc().query(selectSQL, whereArrays, new RowMapper<Entity>() {
            private ColumnMapRowMapper mapRowMapper = new ColumnMapRowMapper();
            public Entity mapRow(ResultSet rs, int rowNum) throws SQLException {
                Map<String, Object> data = this.mapRowMapper.mapRow(rs, rowNum);
                return new Entity(sechma, data);
            }
        });
        return new PageResult<Entity>(pageInfo, entList);
    }
    //
    /**获取ID数据。*/
    public Object getID() {
        return this.get(this.getSechma().getID().getName());
    }
    /**按照列名获取数据。*/
    public Entity setID(Object var) {
        return this.set(this.getSechma().getID().getName(), var);
    }
    /**按照列名获取数据。*/
    public Object get(String column) {
        return this.getDataContainer().get(column);
    }
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
    public Entity set(String column, Object var) {
        this.getDataContainer().put(column, var);
        return this;
    }
    /**按照列索引设置数据。*/
    public Entity set(int column, Object var) {
        return this.set(this.getSechma().getColumn(column).getName(), var);
    }
    /**按照列设置数据。*/
    public Entity set(Column column, Object var) {
        return this.set(column.getName(), var);
    }
    /**按照列名获取数据。*/
    public Entity setMap(Map<String, Object> dataMap) {
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
    /**删除对象（无论目标是否存在）。*/
    public int delete() throws SQLException {
        checkID();
        Sechma sechma = this.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        //
        Column idColumn = sechma.getID();
        Object idValue = this.getID();
        String deleteSQL = builder.buildDelete(sechma, new Column[] { idColumn });
        return this.getJdbc().update(deleteSQL, idValue);
    }
    /**仅保存，如果目标记录不存在则引发异常。*/
    public int update() throws SQLException {
        checkID();
        Sechma sechma = this.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        //
        Column idColumn = sechma.getID();
        String countSQL = builder.buildCount(sechma, new Column[] { idColumn });
        //
        Object idValue = this.getID();
        JdbcOperations jdbc = this.getJdbc();
        if (jdbc.queryForInt(countSQL, idValue) > 0) {
            Column[] allColumn = this.hasValueColumns(sechma.getUpdateColumns());//用于执行更新的列
            String updateSQL = builder.buildUpdate(sechma, new Column[] { idColumn }, allColumn);
            Object[] allData = this.columnValues(allColumn);
            Object[] finalData = new Object[allData.length + 1];
            System.arraycopy(allData, 0, finalData, 0, allData.length);
            finalData[finalData.length - 1] = idValue;
            //
            return jdbc.update(updateSQL, finalData);
        }
        throw new SQLException("record does not exist.");
    }
    /**保存或新增，如果目标记录存在则更新否则新增。*/
    public boolean saveOrUpdate() throws SQLException {
        checkID();
        Sechma sechma = this.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        //1.决定update or insert
        Column idColumn = sechma.getID();
        String countSQL = builder.buildCount(sechma, new Column[] { idColumn });
        Object idValue = this.getID();
        JdbcOperations jdbc = this.getJdbc();
        int targetCount = jdbc.queryForInt(countSQL, idValue);
        if (targetCount > 1) {
            throw new SQLException("Incorrect rows count: expected 1, actual " + targetCount);
        }
        //2.准备 sql 的参数和语句。
        Object[] finalData = null;
        String executeSQL = "";
        if (targetCount == 1) {
            //Save
            Column[] allColumn = this.hasValueColumns(sechma.getUpdateColumns());//更新只需要一部分列
            Object[] allData = this.columnValues(allColumn);
            finalData = new Object[allData.length + 1];
            System.arraycopy(allData, 0, finalData, 0, allData.length);
            finalData[finalData.length - 1] = idValue;
            executeSQL = builder.buildUpdate(sechma, new Column[] { idColumn }, allColumn);
        } else {
            //Insert
            Column[] allColumn = sechma.getInsertColumns();//新增需要所有列
            finalData = this.columnValues(allColumn);
            executeSQL = builder.buildInsert(sechma, allColumn);
        }
        //3.执行SQL
        return jdbc.update(executeSQL, finalData) > 0;
    }
    /**保存为新增，无论目标记录是否存在都作为新增。*/
    public boolean saveAsNew() throws SQLException {
        Sechma sechma = this.getSechma();
        if (sechma.supportIdentify() == false) {
            Object newID = sechma.getIdentify().newUniqueID(this);
            this.set(this.getSechma().getID().getName(), newID);
        }
        return this.saveOrUpdate();
    }
    /**根据ID判断记录在数据库中是否存在。*/
    public boolean existByID() throws SQLException {
        checkID();
        Sechma sechma = this.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        //
        Column idColumn = sechma.getID();
        String countSQL = builder.buildCount(sechma, new Column[] { idColumn });
        Object idValue = this.getID();
        return this.getJdbc().queryForInt(countSQL, idValue) > 0;
    }
    /**装载数据。*/
    public void loadData() throws SQLException {
        checkID();
        Sechma sechma = this.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        //
        Column idColumn = sechma.getID();
        Object idValue = this.getID();
        //
        String selectSQL = builder.buildSelect(sechma, new Column[] { idColumn }, null);
        Map<String, Object> dataContainer = this.getJdbc().queryForMap(selectSQL, idValue);
        this.getDataContainer().putAll(dataContainer);
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
    private <T> T toType(Object oriData, Class<?> toType, T defaultValue) {
        if (oriData == null) {
            return defaultValue;
        }
        return (T) ConverterUtils.convert(toType, oriData);
    }
    private void checkID() {
        Object idValue = this.getID();
        if (idValue == null) {
            throw new NullPointerException("id field is empty.");
        }
    }
}