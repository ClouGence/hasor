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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import net.hasor.db.ar.PageResult;
import net.hasor.db.ar.Paginator;
import net.hasor.db.jdbc.JdbcOperations;
import net.hasor.db.jdbc.RowMapper;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.core.mapper.ColumnMapRowMapper;
import org.more.util.StringUtils;
/**
 * 用来表示数据库。
 * @version : 2014年10月27日
 * @author 赵永春(zyc@hasor.net)
 */
public final class DataBase {
    private static final Map<String, Object> Empty        = new HashMap<String, Object>();
    private Map<String, Sechma>              sechmaDefine = new HashMap<String, Sechma>(200);
    //
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public PageResult<Record> queryBySQL(String sqlQuery) throws SQLException {
        return this.queryBySQL(Record.class, sqlQuery, null, Empty);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public PageResult<Record> queryBySQL(String sqlQuery, Object... params) throws SQLException {
        return this.queryBySQL(Record.class, sqlQuery, null, params);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public PageResult<Record> queryBySQL(String sqlQuery, Map<String, Object> params) throws SQLException {
        return this.queryBySQL(Record.class, sqlQuery, null, params);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public PageResult<Record> queryBySQL(String sqlQuery, Paginator paginator) throws SQLException {
        return this.queryBySQL(Record.class, sqlQuery, paginator, Empty);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public PageResult<Record> queryBySQL(String sqlQuery, Paginator paginator, Object... params) throws SQLException {
        return this.queryBySQL(Record.class, sqlQuery, paginator, params);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public PageResult<Record> queryBySQL(String sqlQuery, Paginator paginator, Map<String, Object> params) throws SQLException {
        return this.queryBySQL(Record.class, sqlQuery, paginator, params);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public <T> PageResult<T> queryBySQL(Class<T> recType, String sqlQuery) throws SQLException {
        return this.queryBySQL(recType, sqlQuery, null, Empty);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public <T> PageResult<T> queryBySQL(Class<T> recType, String sqlQuery, Object... params) throws SQLException {
        return this.queryBySQL(recType, sqlQuery, null, params);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public <T> PageResult<T> queryBySQL(Class<T> recType, String sqlQuery, Map<String, Object> params) throws SQLException {
        return this.queryBySQL(recType, sqlQuery, null, params);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public <T> PageResult<T> queryBySQL(Class<T> recType, String sqlQuery, Paginator paginator) throws SQLException {
        return this.queryBySQL(recType, sqlQuery, paginator, Empty);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public <T> PageResult<T> queryBySQL(Class<T> recType, String sqlQuery, Paginator paginator, Object... params) throws SQLException {
        SQLBuilder builder = this.getSQLBuilder();
        sqlQuery = builder.buildPaginator(sqlQuery, paginator);
        //
        List<T> entList = null;
        if (recType == Record.class) {
            entList = (List<T>) this.getJdbc().query(sqlQuery, params, getRecordRowMapper(sqlQuery));
        } else {
            entList = this.getJdbc().queryForList(sqlQuery, params, recType);
        }
        return new PageResult<T>(paginator, entList);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public <T> PageResult<T> queryBySQL(Class<T> recType, String sqlQuery, Paginator paginator, Map<String, Object> params) throws SQLException {
        SQLBuilder builder = this.getSQLBuilder();
        sqlQuery = builder.buildPaginator(sqlQuery, paginator);
        //
        List<T> entList = null;
        if (recType == Record.class) {
            entList = (List<T>) this.getJdbc().query(sqlQuery, params, getRecordRowMapper(sqlQuery));
        } else {
            entList = this.getJdbc().queryForList(sqlQuery, params, recType);
        }
        return new PageResult<T>(paginator, entList);
    }
    //
    /**装载数据。*/
    public Record loadData(Record ent) throws SQLException {
        checkID(ent);
        Sechma sechma = ent.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        //
        Column idColumn = sechma.getPrimaryKey();
        Object idValue = ent.getID();
        //
        String selectSQL = builder.buildSelect(sechma, new Column[] { idColumn });
        Map<String, Object> dataContainer = this.getJdbc().queryForMap(selectSQL, idValue);
        ent.setMap(dataContainer);
        return ent;
    }
    /**删除对象（无论目标是否存在）。*/
    public int delete(Record ent) throws SQLException {
        checkID(ent);
        Sechma sechma = ent.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        //
        Column idColumn = sechma.getPrimaryKey();
        Object idValue = ent.getID();
        String deleteSQL = builder.buildDelete(sechma, new Column[] { idColumn });
        return this.getJdbc().update(deleteSQL, idValue);
    }
    /**仅保存，如果目标记录不存在则引发异常。*/
    public int update(Record ent) throws SQLException {
        checkID(ent);
        Sechma sechma = ent.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        //
        Column idColumn = sechma.getPrimaryKey();
        String countSQL = builder.buildCount(sechma, new Column[] { idColumn });
        //
        Object idValue = ent.getID();
        JdbcOperations jdbc = this.getJdbc();
        if (jdbc.queryForInt(countSQL, idValue) > 0) {
            Column[] allColumn = ent.hasValueColumns(sechma.getUpdateColumns());//用于执行更新的列
            String updateSQL = builder.buildUpdate(sechma, new Column[] { idColumn }, allColumn);
            Object[] allData = ent.columnValues(allColumn);
            Object[] finalData = new Object[allData.length + 1];
            System.arraycopy(allData, 0, finalData, 0, allData.length);
            finalData[finalData.length - 1] = idValue;
            //
            return jdbc.update(updateSQL, finalData);
        }
        throw new SQLException("record does not exist.");
    }
    /**保存或新增，如果目标记录存在则更新否则新增。*/
    public boolean saveOrUpdate(Record ent) throws SQLException {
        checkID(ent);
        if (this.existByID(ent) == true) {
            return this.saveAsNew(ent);
        } else {
            return this.update(ent) > 0;
        }
    }
    /**保存为新增，无论目标记录是否存在都作为新增。*/
    public boolean saveAsNew(Record ent) throws SQLException {
        Sechma sechma = ent.getSechma();
        Column keyColumn = sechma.getPrimaryKey();
        if (ent.isNull(keyColumn.getName())) {
            Object newID = this.getIdentify(sechma).newUniqueID(ent, sechma, this.getJdbc());
            ent.set(keyColumn, newID);
        }
        return this.saveOrUpdate(ent);
    }
    /**根据ID判断记录在数据库中是否存在。*/
    public boolean existByID(Record ent) throws SQLException {
        checkID(ent);
        Sechma sechma = ent.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        //
        Column idColumn = sechma.getPrimaryKey();
        String countSQL = builder.buildCount(sechma, new Column[] { idColumn });
        Object idValue = ent.getID();
        return this.getJdbc().queryForInt(countSQL, idValue) > 0;
    }
    /**记录在数据库中是否存在。*/
    public int countByExample(Record example) throws SQLException {
        Sechma sechma = example.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        Column[] allColumn = example.hasValueColumns(sechma.getColumns());
        Object[] dataArrays = example.columnValues(allColumn);
        String countSQL = builder.buildCount(sechma, allColumn);
        return this.getJdbc().queryForInt(countSQL, dataArrays);
    }
    /**删除数据库中满足该对象特征的。*/
    public int deleteByExample(Record example) throws SQLException {
        Sechma sechma = example.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        //
        Column[] allColumn = example.hasValueColumns(sechma.getColumns());
        Object[] dataArrays = example.columnValues(allColumn);
        String deleteSQL = builder.buildDelete(sechma, allColumn);
        return this.getJdbc().update(deleteSQL, dataArrays);
    }
    /**更新数据库中满足该对象特征的。*/
    public int updateByExample(Record example, Map<String, Object> dataContainer) throws SQLException {
        if (dataContainer == null || dataContainer.isEmpty())
            return 0;
        return this.updateByExample(example, new MapRecord(example.getSechma(), dataContainer));
    }
    /**更新数据库中满足该对象特征的。*/
    public int updateByExample(Record example, Record dataContainer) throws SQLException {
        if (dataContainer == null)
            return 0;
        Sechma sechma = example.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        //
        Column[] whereColumn = example.hasValueColumns(sechma.getColumns());
        Object[] whereArrays = example.columnValues(whereColumn);
        Column[] dataColumn = dataContainer.hasValueColumns(sechma.getUpdateColumns());
        Object[] dataArrays = dataContainer.columnValues(dataColumn);
        //
        String updateSQL = builder.buildUpdate(sechma, whereColumn, dataColumn);
        Object[] updateData = new Object[whereArrays.length + dataArrays.length];
        System.arraycopy(whereArrays, 0, updateData, 0/*            */, whereArrays.length);
        System.arraycopy(dataArrays, 0, updateData, whereArrays.length, dataArrays.length);
        //
        return this.getJdbc().update(updateSQL, updateData);
    }
    /**从数据库中查询满足该对象特征的。*/
    public PageResult<Record> listByExample(Record example) throws SQLException {
        return this.listByExample(Record.class, example, null);
    }
    /**从数据库中查询满足该对象特征的。*/
    public PageResult<Record> listByExample(final Record example, Paginator paginator) throws SQLException {
        return this.listByExample(Record.class, example, paginator);
    }
    /**从数据库中查询满足该对象特征的。*/
    public <T> PageResult<T> listByExample(Class<T> recType, Record example) throws SQLException {
        return this.listByExample(recType, example, null);
    }
    /**从数据库中查询满足该对象特征的。*/
    public <T> PageResult<T> listByExample(final Class<T> recType, final Record example, Paginator paginator) throws SQLException {
        SQLBuilder builder = this.getSQLBuilder();
        Sechma sechma = example.getSechma();
        Column[] whereColumn = example.hasValueColumns(sechma.getColumns());//所有列
        Object[] whereArrays = example.columnValues(whereColumn);
        String selectSQL = builder.buildSelect(sechma, whereColumn);
        selectSQL = builder.buildPaginator(selectSQL, paginator);
        //
        List<T> entList = null;
        if (recType == Record.class) {
            entList = (List<T>) this.getJdbc().query(selectSQL, whereArrays, getRecordRowMapper(sechma.getName()));
        } else {
            entList = this.getJdbc().queryForList(selectSQL, recType, whereArrays);
        }
        return new PageResult<T>(paginator, entList);
    }
    private void checkID(Record ent) {
        Object idValue = ent.getID();
        if (idValue == null) {
            throw new NullPointerException("id field is empty.");
        }
    }
    //    /**从数据库中查找已存在的表，并创建其{@link Record}实例。*/
    //    public Record loadSechma(final String tableName, final String primarykey) throws SQLException {
    //        String sechmaCacheKey = tableName;// StringUtils.isBlank(catalog) ? tableName : (catalog + "." + tableName);
    //        Sechma define = this.sechmaDefine.get(sechmaCacheKey);
    //        if (define != null) {
    //            return new MapRecord(define);
    //        }
    //        //1.load
    //        define = this.getJdbc().execute(new ConnectionCallback<Sechma>() {
    //            public Sechma doInConnection(Connection con) throws SQLException {
    //                //1.验证表
    //                DatabaseMetaData metaData = con.getMetaData();
    //                ResultSet resultSet = metaData.getTables(null, null, tableName.toUpperCase(), new String[] { "TABLE" });
    //                if (resultSet.next() == false) {
    //                    throw new UndefinedException("table " + tableName + " is Undefined.");
    //                }
    //                //2.装载结构
    //                String emptySelect = getSQLBuilder().buildEmptySelect(tableName);
    //                ResultSetMetaData resMetaData = con.createStatement().executeQuery(emptySelect).getMetaData();
    //                return loadSechma(new Sechma(tableName), resMetaData, primarykey);
    //            }
    //        });
    //        //2.cache
    //        this.sechmaDefine.put(sechmaCacheKey, define);
    //        return new MapRecord(define);
    //    }
    //
    //    private static Sechma loadSechma(Sechma sechma, ResultSetMetaData resMetaData, String primarykey) throws SQLException {
    //        int columnCount = resMetaData.getColumnCount();
    //        for (int i = 1; i < columnCount; i++) {
    //            String colName = resMetaData.getColumnName(i);//列名称。
    //            int colSQLType = resMetaData.getColumnType(i);//来自 java.sql.Types 的 SQL 类型。
    //            //
    //            Column col = new Column(colName, colSQLType);
    //            col.setMaxSize(resMetaData.getPrecision(i));//列的大小。
    //            int allowEmpty = resMetaData.isNullable(i);//是否允许使用 NULL。 
    //            col.setEmpty(allowEmpty == ResultSetMetaData.columnNullable);//明确允许使用null
    //            //
    //            col.setDefaultValue(null);
    //            col.setIdentify(resMetaData.isAutoIncrement(i));
    //            //
    //            if (resMetaData.isReadOnly(i) == true) {
    //                col.setAllowInsert(false);
    //                col.setAllowUpdate(false);
    //            }
    //            //
    //            if (StringUtils.equalsBlankIgnoreCase(primarykey, colName) == true) {
    //                col.setPrimaryKey(true);
    //            }
    //            //
    //            sechma.addColumn(col);
    //        }
    //        return sechma;
    //    }
    //    private RowMapper<Record> getRecordRowMapper() {
    //        return new RecordRowMapper(sechmaKey);
    //    }
    //    private RowMapper<Record> getRecordRowMapper(Sechma sechma) {
    //        return new RecordRowMapper(sechma);
    //    }
    private RowMapper<Record> getRecordRowMapper(String cacheKey) {
        // TODO Auto-generated method stub
        return null;
    }
    private static class RecordRowMapper implements RowMapper<Record> {
        private ColumnMapRowMapper mapRowMapper = new ColumnMapRowMapper();
        private String             sechmaKey    = null;
        private Sechma             sechma       = null;
        //
        public RecordRowMapper(String sechmaKey) {
            this.sechmaKey = sechmaKey;
        }
        public RecordRowMapper(Sechma sechma) {
            this.sechma = sechma;
        }
        public Record mapRow(ResultSet rs, int rowNum) throws SQLException {
            String primaryKey = "";
            //
            Map<String, Object> data = this.mapRowMapper.mapRow(rs, rowNum);
            if (this.sechma == null) {
                this.sechma = loadSechma(new Sechma(this.sechmaKey), rs.getMetaData(), primaryKey);
            }
            return new MapRecord(this.sechma, data);
        }
    }
    //
    //
    //
    private DataSource dataSource = null;
    /**获取JDBC接口*/
    public JdbcOperations getJdbc() {
        return new JdbcTemplate(this.dataSource);
    }
    public DataBase(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    protected SQLBuilder getSQLBuilder() {
        // TODO Auto-generated method stub
        return null;
    }
    protected Identify getIdentify(Sechma sechma) {
        // TODO Auto-generated method stub
        return null;
    }
}