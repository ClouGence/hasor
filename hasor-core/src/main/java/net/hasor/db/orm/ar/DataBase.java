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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.JdbcOperations;
import net.hasor.db.jdbc.RowMapper;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.core.mapper.ColumnMapRowMapper;
import net.hasor.db.orm.PageResult;
import net.hasor.db.orm.Paginator;
import net.hasor.db.orm.ar.SQLBuilder.BuilderData;
import net.hasor.db.orm.ar.SQLBuilder.BuilderMapData;
import net.hasor.db.orm.ar.dialect.SQLBuilderEnum;
import net.hasor.db.orm.ar.record.MapRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 用来表示数据库。
 * @version : 2014年10月27日
 * @author 赵永春(zyc@hasor.net)
 */
public final class DataBase {
    protected Logger                         logger          = LoggerFactory.getLogger(getClass());
    private static final Map<String, Object> Empty           = new HashMap<String, Object>();
    private static final ArConfiguration     arConfiguration = new ArConfiguration();
    //
    //
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public PageResult<Record> queryBySQL(String sqlQuery) throws SQLException {
        return this.queryBySQL(Record.class, sqlQuery, null, null, Empty);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public PageResult<Record> queryBySQL(String sqlQuery, Object... params) throws SQLException {
        return this.queryBySQL(Record.class, sqlQuery, null, null, params);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public PageResult<Record> queryBySQL(String sqlQuery, Map<String, Object> params) throws SQLException {
        return this.queryBySQL(Record.class, sqlQuery, null, null, params);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public PageResult<Record> queryBySQL(String sqlQuery, Paginator paginator) throws SQLException {
        return this.queryBySQL(Record.class, sqlQuery, paginator, null, Empty);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public PageResult<Record> queryBySQL(String sqlQuery, Paginator paginator, Object... params) throws SQLException {
        return this.queryBySQL(Record.class, sqlQuery, paginator, null, params);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public PageResult<Record> queryBySQL(String sqlQuery, Paginator paginator, Map<String, Object> params) throws SQLException {
        return this.queryBySQL(Record.class, sqlQuery, paginator, null, params);
    }
    //
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public <T> PageResult<T> queryBySQL(Class<T> recType, String sqlQuery) throws SQLException {
        return this.queryBySQL(recType, sqlQuery, null, this.loadSechma(recType), Empty);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public <T> PageResult<T> queryBySQL(Class<T> recType, String sqlQuery, Object... params) throws SQLException {
        return this.queryBySQL(recType, sqlQuery, null, this.loadSechma(recType), params);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public <T> PageResult<T> queryBySQL(Class<T> recType, String sqlQuery, Map<String, Object> params) throws SQLException {
        return this.queryBySQL(recType, sqlQuery, null, this.loadSechma(recType), params);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public <T> PageResult<T> queryBySQL(Class<T> recType, String sqlQuery, Paginator paginator) throws SQLException {
        return this.queryBySQL(recType, sqlQuery, paginator, this.loadSechma(recType), Empty);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public <T> PageResult<T> queryBySQL(final Class<T> recType, final String sqlQuery, final Paginator paginator, final Object... params) throws SQLException {
        return this.queryBySQL(recType, sqlQuery, paginator, this.loadSechma(recType), params);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public <T> PageResult<T> queryBySQL(final Class<T> recType, final String sqlQuery, final Paginator paginator, final Map<String, Object> params) throws SQLException {
        return this.queryBySQL(recType, sqlQuery, paginator, this.loadSechma(recType), params);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public <T> PageResult<T> queryBySQL(final Class<T> recType, final String sqlQuery, final Paginator paginator, Sechma useSechma, final Object... params) throws SQLException {
        SQLBuilder builder = this.getSQLBuilder();
        BuilderData queryData = builder.buildPaginator(sqlQuery, paginator, params);
        //
        if (recType == Record.class) {
            logger.info("selectSQL:{}", queryData);
            List<T> entList = (List<T>) this.getJdbc().query(queryData.getSQL(), queryData.getData(), getRecordRowMapper(useSechma));
            return new PageResult<T>(paginator, entList);
        } else {
            logger.info("selectSQL:{}", queryData);
            List<T> entList = this.getJdbc().queryForList(queryData.getSQL(), queryData.getData(), recType);
            return new PageResult<T>(paginator, entList);
        }
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    public <T> PageResult<T> queryBySQL(final Class<T> recType, final String sqlQuery, final Paginator paginator, Sechma useSechma, final Map<String, Object> params) throws SQLException {
        SQLBuilder builder = this.getSQLBuilder();
        BuilderMapData queryData = builder.buildPaginator(sqlQuery, paginator, params);
        //
        if (recType == Record.class) {
            logger.info("selectSQL:{}", queryData);
            List<T> entList = (List<T>) this.getJdbc().query(queryData.getSQL(), queryData.getData(), getRecordRowMapper(useSechma));
            return new PageResult<T>(paginator, entList);
        } else {
            logger.info("selectSQL:{}", queryData);
            List<T> entList = this.getJdbc().queryForList(queryData.getSQL(), queryData.getData(), recType);
            return new PageResult<T>(paginator, entList);
        }
    }
    //
    /**装载数据。*/
    public Record loadData(Record ent) throws SQLException {
        checkID(ent);
        Sechma sechma = ent.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        //
        Column[] idColumn = as(sechma.getPrimaryKey());
        Object[] idValue = as(ent.getID());
        //
        BuilderData loadData = builder.buildSelect(sechma, idColumn, idValue);
        //
        logger.info("selectSQL:{}", loadData);
        Map<String, Object> dataContainer = this.getJdbc().queryForMap(loadData.getSQL(), loadData.getData());
        ent.setMap(dataContainer);
        return ent;
    }
    /**删除对象（无论目标是否存在）。*/
    public int delete(Record ent) throws SQLException {
        checkID(ent);
        Sechma sechma = ent.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        //
        Column[] idColumn = as(sechma.getPrimaryKey());
        Object[] idValue = as(ent.getID());
        BuilderData deleteData = builder.buildDelete(sechma, idColumn, idValue);
        //
        logger.info("deleteSQL:{}", deleteData);
        return this.getJdbc().update(deleteData.getSQL(), deleteData.getData());
    }
    /**仅保存，如果目标记录不存在则引发异常。*/
    public int update(Record ent) throws SQLException {
        checkID(ent);
        Sechma sechma = ent.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        //
        Column[] idColumn = as(sechma.getPrimaryKey());
        Object[] idValue = as(ent.getID());
        final BuilderData countData = builder.buildCount(sechma, idColumn, idValue);
        //
        JdbcOperations jdbc = this.getJdbc();
        if (jdbc.queryForInt(countData.getSQL(), countData.getData()) > 0) {
            Column[] updateColumn = ent.hasValueColumns(sechma.getUpdateColumns());//用于执行更新的列
            Object[] updateData = ent.columnValues(updateColumn);
            BuilderData updateSqlData = builder.buildUpdate(sechma, idColumn, idValue, updateColumn, updateData);
            //
            logger.info("updateSQL:{}", updateSqlData);
            return jdbc.update(updateSqlData.getSQL(), updateSqlData.getData());
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
        if (keyColumn != null && ent.isNull(keyColumn.getName())) {
            Identify identify = this.getIdentify(sechma);
            if (identify != null) {
                Object newID = identify.newUniqueID(ent, sechma, this.getJdbc());
                ent.set(keyColumn, newID);
            }
            checkID(ent);
        }
        //
        //
        JdbcOperations jdbc = this.getJdbc();
        Column[] insertColumn = ent.hasValueColumns(sechma.getColumns());//用于执行更新的列
        Object[] insertParam = ent.columnValues(insertColumn);
        SQLBuilder builder = this.getSQLBuilder();
        final BuilderData insertData = builder.buildInsert(sechma, insertColumn, insertParam);
        //
        logger.info("insertSQL:{}", insertData);
        return jdbc.update(insertData.getSQL(), insertData.getData()) > 0;
    }
    /**根据ID判断记录在数据库中是否存在。*/
    public boolean existByID(Record ent) throws SQLException {
        checkID(ent);
        Sechma sechma = ent.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        //
        Column[] idColumn = as(sechma.getPrimaryKey());
        Object[] idValue = as(ent.getID());
        BuilderData countData = builder.buildCount(sechma, idColumn, idValue);
        //
        logger.info("countSQL:{}", countData);
        return this.getJdbc().queryForInt(countData.getSQL(), countData.getData()) > 0;
    }
    /**记录在数据库中是否存在。*/
    public int countByExample(Record example) throws SQLException {
        Sechma sechma = example.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        Column[] allColumn = example.hasValueColumns(sechma.getColumns());
        Object[] dataArrays = example.columnValues(allColumn);
        BuilderData countData = builder.buildCount(sechma, allColumn, dataArrays);
        //
        logger.info("countSQL:{}", countData);
        return this.getJdbc().queryForInt(countData.getSQL(), countData.getData());
    }
    /**删除数据库中满足该对象特征的。*/
    public int deleteByExample(Record example) throws SQLException {
        Sechma sechma = example.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        //
        Column[] allColumn = example.hasValueColumns(sechma.getColumns());
        Object[] dataArrays = example.columnValues(allColumn);
        BuilderData deleteData = builder.buildDelete(sechma, allColumn, dataArrays);
        //
        logger.info("deleteSQL:{}", deleteData);
        return this.getJdbc().update(deleteData.getSQL(), deleteData.getData());
    }
    /**更新数据库中满足该对象特征的。*/
    public int updateByExample(Record example, Map<String, Object> dataContainer) throws SQLException {
        if (dataContainer == null || dataContainer.isEmpty()) {
            return 0;
        }
        return this.updateByExample(example, new MapRecord(example.getSechma(), dataContainer));
    }
    /**更新数据库中满足该对象特征的。*/
    public int updateByExample(Record example, Record dataContainer) throws SQLException {
        if (dataContainer == null) {
            return 0;
        }
        Sechma sechma = example.getSechma();
        SQLBuilder builder = this.getSQLBuilder();
        //
        Column[] whereColumn = example.hasValueColumns(sechma.getColumns());
        Object[] whereArrays = example.columnValues(whereColumn);
        Column[] dataColumn = dataContainer.hasValueColumns(sechma.getUpdateColumns());
        Object[] dataArrays = dataContainer.columnValues(dataColumn);
        //
        BuilderData updateData = builder.buildUpdate(sechma, whereColumn, whereArrays, dataColumn, dataArrays);
        //
        logger.info("updateSQL:{}", updateData);
        return this.getJdbc().update(updateData.getSQL(), updateData.getData());
    }
    /**从数据库中查询满足该对象特征的。*/
    public PageResult<Record> listByExample(Record example) throws SQLException {
        return this.listByExample(Record.class, example, null, example.getSechma());
    }
    /**从数据库中查询满足该对象特征的。*/
    public PageResult<Record> listByExample(final Record example, Paginator paginator) throws SQLException {
        return this.listByExample(Record.class, example, paginator, example.getSechma());
    }
    /**从数据库中查询满足该对象特征的。*/
    public <T> PageResult<T> listByExample(Class<T> recType, Record example) throws SQLException {
        return this.listByExample(recType, example, null, this.loadSechma(recType));
    }
    /**从数据库中查询满足该对象特征的。*/
    public <T> PageResult<T> listByExample(final Class<T> recType, final Record example, Paginator paginator) throws SQLException {
        return this.listByExample(recType, example, paginator, this.loadSechma(recType));
    }
    /**从数据库中查询满足该对象特征的。*/
    public <T> PageResult<T> listByExample(final Class<T> recType, final Record example, Paginator paginator, Sechma useSechma) throws SQLException {
        SQLBuilder builder = this.getSQLBuilder();
        Sechma sechma = example.getSechma();
        Column[] whereColumn = example.hasValueColumns(sechma.getColumns());//所有列
        Object[] whereArrays = example.columnValues(whereColumn);
        //
        BuilderData selectData = builder.buildSelect(sechma, whereColumn, whereArrays);
        /*        */selectData = builder.buildPaginator(selectData.getSQL(), paginator, selectData.getData());
        //
        logger.info("selectSQL:{}", selectData);
        if (recType == Record.class) {
            List<T> entList = (List<T>) this.getJdbc().query(selectData.getSQL(), selectData.getData(), getRecordRowMapper(useSechma));
            return new PageResult<T>(paginator, entList);
        } else {
            List<T> entList = this.getJdbc().queryForList(selectData.getSQL(), recType, selectData.getData());
            return new PageResult<T>(paginator, entList);
        }
    }
    private void checkID(Record ent) {
        Object idValue = ent.getID();
        if (idValue == null) {
            throw new NullPointerException("id field is empty.");
        }
    }
    //
    private RowMapper<Record> getRecordRowMapper(Sechma sechma) {
        return new RecordRowMapper(sechma == null ? new Sechma("none") : sechma);
    }
    private static class RecordRowMapper implements RowMapper<Record> {
        private ColumnMapRowMapper mapRowMapper = new ColumnMapRowMapper();
        private Sechma             sechma       = null;
        //
        public RecordRowMapper(Sechma sechma) {
            this.sechma = Hasor.assertIsNotNull(sechma);
        }
        public Record mapRow(ResultSet rs, int rowNum) throws SQLException {
            Map<String, Object> data = this.mapRowMapper.mapRow(rs, rowNum);
            return new MapRecord(this.sechma, data);
        }
    }
    //
    private DataSource dataSource = null;
    private SQLBuilder sqlBuilder = null;
    /**获取JDBC接口*/
    public JdbcOperations getJdbc() {
        return new JdbcTemplate(this.dataSource);
    }
    public DataBase(DataSource dataSource, SQLBuilderEnum dialectEnum) {
        this.dataSource = dataSource;
        this.sqlBuilder = dialectEnum.createBuilder();
    }
    /**转为数组*/
    private static <T> T[] as(final T... arr) {
        return arr;
    }
    protected SQLBuilder getSQLBuilder() {
        return this.sqlBuilder;
    }
    protected Identify getIdentify(Sechma sechma) {
        return arConfiguration.getIdentify(sechma);
    }
    public Sechma loadSechma(Class<?> sechmaType) {
        return arConfiguration.loadSechma(sechmaType);
    }
}