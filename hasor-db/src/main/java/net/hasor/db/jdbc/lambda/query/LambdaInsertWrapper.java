/*
 * Copyright 2002-2010 the original author or authors.
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
package net.hasor.db.jdbc.lambda.query;
import net.hasor.db.dialect.BatchBoundSql;
import net.hasor.db.dialect.BoundSql;
import net.hasor.db.dialect.MultipleInsertSqlDialect;
import net.hasor.db.dialect.SqlDialect;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.lambda.InsertExecute;
import net.hasor.db.jdbc.lambda.LambdaOperations.LambdaInsert;
import net.hasor.db.jdbc.lambda.LambdaOperations.LambdaQuery;
import net.hasor.db.jdbc.lambda.mapping.FieldInfo;
import net.hasor.db.jdbc.lambda.mapping.MappingRowMapper;
import net.hasor.db.jdbc.lambda.mapping.TableInfo;
import net.hasor.db.jdbc.lambda.segment.MergeSqlSegment;
import net.hasor.db.jdbc.lambda.segment.Segment;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.hasor.db.jdbc.lambda.segment.SqlKeyword.LEFT;
import static net.hasor.db.jdbc.lambda.segment.SqlKeyword.RIGHT;

/**
 * 提供 lambda insert 能力。是 LambdaInsert 接口的实现类。
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
public class LambdaInsertWrapper<T> extends AbstractExecute<T> implements LambdaInsert<T> {
    private final List<FieldInfo> insertFields;
    private final List<Object[]>  insertValues;
    private       boolean         useMultipleValues = false;
    private       BoundSql        insertAsQuery;

    public LambdaInsertWrapper(Class<T> exampleType, JdbcTemplate jdbcTemplate) {
        super(exampleType, jdbcTemplate);
        this.insertFields = getInsertFields();
        this.insertValues = new ArrayList<>();
    }

    @Override
    public LambdaInsert<T> useQualifier() {
        this.enableQualifier();
        return this;
    }

    protected List<FieldInfo> getInsertFields() {
        List<FieldInfo> toInsertField = new ArrayList<>();
        MappingRowMapper<T> rowMapper = this.getRowMapper();
        List<String> columnNames = rowMapper.getColumnNames();
        for (String columnName : columnNames) {
            FieldInfo field = rowMapper.findWriteFieldByColumn(columnName);
            if (field.isInsert()) {
                toInsertField.add(field);
            }
        }
        if (toInsertField.size() == 0) {
            throw new IllegalStateException("no column require INSERT.");
        }
        return toInsertField;
    }

    @Override
    public InsertExecute<T> applyMap(List<Map<String, Object>> dataMapList) {
        if (this.insertAsQuery != null) {
            throw new IllegalStateException("there is existing INSERT ... SELECT, cannot be use data");
        }
        int fieldCount = this.insertFields.size();
        for (Map<String, Object> map : dataMapList) {
            Object[] args = new Object[fieldCount];
            for (int i = 0; i < fieldCount; i++) {
                FieldInfo info = this.insertFields.get(i);
                args[i] = map.get(info.getPropertyName());
            }
            this.insertValues.add(args);
        }
        return this;
    }

    @Override
    public InsertExecute<T> applyEntity(List<T> entityList) {
        int fieldCount = this.insertFields.size();
        for (T entity : entityList) {
            Object[] args = new Object[fieldCount];
            for (int i = 0; i < fieldCount; i++) {
                FieldInfo info = this.insertFields.get(i);
                args[i] = BeanUtils.readPropertyOrField(entity, info.getPropertyName());
            }
            this.insertValues.add(args);
        }
        return this;
    }

    @Override
    public InsertExecute<T> useMultipleValues() {
        this.useMultipleValues = true;
        return this;
    }

    protected Segment getTableNameAndColumn(SqlDialect dialect) {
        MappingRowMapper<T> rowMapper = this.getRowMapper();
        TableInfo tableInfo = rowMapper.getTableInfo();
        //
        MergeSqlSegment tableNameAndColumn = new MergeSqlSegment();
        String tableName = dialect.tableName(isQualifier(), tableInfo.getCategory(), tableInfo.getTableName());
        tableNameAndColumn.addSegment(() -> tableName);
        //
        tableNameAndColumn.addSegment(LEFT);
        List<String> columnNames = this.insertFields.stream().map(FieldInfo::getColumnName).collect(Collectors.toList());
        for (int i = 0; i < columnNames.size(); i++) {
            if (i != 0) {
                tableNameAndColumn.addSegment(() -> ",");
            }
            String columnName = columnNames.get(i);
            FieldInfo field = rowMapper.findWriteFieldByColumn(columnName);
            String selectColumn = dialect.columnName(isQualifier(), tableInfo.getCategory(), tableInfo.getTableName(), field.getColumnName(), field.getJdbcType(), field.getJavaType());
            tableNameAndColumn.addSegment(() -> selectColumn);
        }
        tableNameAndColumn.addSegment(RIGHT);
        return tableNameAndColumn;
    }

    protected String getInsertTemplate(SqlDialect dialect) {
        // insert into
        MergeSqlSegment insertTemplate = new MergeSqlSegment();
        insertTemplate.addSegment(() -> "insert into");
        // columns
        insertTemplate.addSegment(getTableNameAndColumn(dialect));
        // values
        insertTemplate.addSegment(() -> "values");
        insertTemplate.addSegment(LEFT);
        insertTemplate.addSegment(() -> StringUtils.repeat(",?", this.insertFields.size()).substring(1));
        insertTemplate.addSegment(RIGHT);
        //
        return insertTemplate.getSqlSegment();
    }

    protected String getMultipleInsertTemplate(MultipleInsertSqlDialect dialect) {
        Segment tableNameAndColumn = getTableNameAndColumn(dialect);
        //
        MergeSqlSegment insertTemplate = new MergeSqlSegment();
        insertTemplate.addSegment(dialect::multipleRecordInsertPrepare);
        for (int i = 0; i < this.insertValues.size(); i++) {
            boolean firstRecord = i == 0;
            if (!firstRecord) {
                insertTemplate.addSegment(dialect::multipleRecordInsertSplitRecord);
            }
            insertTemplate.addSegment(() -> dialect.multipleRecordInsertBeforeValues(firstRecord, tableNameAndColumn.getSqlSegment()));
            insertTemplate.addSegment(() -> StringUtils.repeat(",?", this.insertFields.size()).substring(1));
            insertTemplate.addSegment(dialect::multipleRecordInsertAfterValues);
        }
        insertTemplate.addSegment(dialect::multipleRecordInsertFinish);
        return insertTemplate.getSqlSegment();
    }

    @Override
    public BoundSql getBoundSql() {
        return getBoundSql(dialect());
    }

    @Override
    public BoundSql getBoundSql(SqlDialect dialect) {
        if (this.insertValues.size() == 0) {
            throw new IllegalStateException("there is no data to insert");
        }
        //
        if (this.insertAsQuery != null) {
            // TODO
            //   INSERT INTO Customers (CustomerName, City, Country)
            //   SELECT SupplierName, City, Country FROM Suppliers
            //   WHERE Country='Germany';
        }
        //
        boolean useMultipleValues = this.useMultipleValues && dialect instanceof MultipleInsertSqlDialect && this.insertValues.size() > 1;
        if (useMultipleValues) {
            String sqlString = getMultipleInsertTemplate((MultipleInsertSqlDialect) dialect);
            Object[] args = Arrays.stream(this.insertValues.toArray()).flatMap(o -> Arrays.stream((Object[]) o)).toArray();
            return new BoundSql.BoundSqlObj(sqlString, args);
        } else {
            String sqlString = getInsertTemplate(dialect);
            Object[][] args = new Object[this.insertValues.size()][];
            for (int i = 0; i < this.insertValues.size(); i++) {
                args[i] = this.insertValues.get(i);
            }
            return new BatchBoundSql.BatchBoundSqlObj(sqlString, args);
        }
    }

    @Override
    public <V> InsertExecute<T> applyQueryAsInsert(LambdaQuery<V> lambdaQuery) {
        if (!this.insertValues.isEmpty()) {
            throw new IllegalStateException("there is existing insert data, cannot be use INSERT ... SELECT");
        }
        this.insertAsQuery = lambdaQuery.getBoundSql(dialect());
        return this;
    }

    @Override
    public int doInsert() throws SQLException {
        return 0;
    }
}
