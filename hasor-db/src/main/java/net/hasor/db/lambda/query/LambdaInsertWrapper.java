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
package net.hasor.db.lambda.query;
import net.hasor.db.dialect.BatchBoundSql;
import net.hasor.db.dialect.BoundSql;
import net.hasor.db.dialect.InsertSqlDialect;
import net.hasor.db.dialect.SqlDialect;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.lambda.InsertExecute;
import net.hasor.db.lambda.LambdaOperations.LambdaInsert;
import net.hasor.db.lambda.LambdaOperations.LambdaQuery;
import net.hasor.db.lambda.segment.MergeSqlSegment;
import net.hasor.db.lambda.segment.Segment;
import net.hasor.db.mapping.FieldInfo;
import net.hasor.db.mapping.MappingRowMapper;
import net.hasor.db.mapping.TableInfo;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static net.hasor.db.lambda.segment.SqlKeyword.*;

/**
 * 提供 lambda insert 能力。是 LambdaInsert 接口的实现类。
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
public class LambdaInsertWrapper<T> extends AbstractExecute<T> implements LambdaInsert<T> {
    private final List<FieldInfo> insertColumns;
    private final List<FieldInfo> primaryKeyColumns;
    private final List<Object[]>  insertValues;
    private       InsertStrategy  insertStrategy;
    private       LambdaQuery<?>  insertAsQuery;

    public LambdaInsertWrapper(Class<T> exampleType, JdbcTemplate jdbcTemplate) {
        super(exampleType, jdbcTemplate);
        this.insertColumns = getInsertColumns();
        this.primaryKeyColumns = getPrimaryKeyColumns();
        this.insertValues = new ArrayList<>();
        this.insertStrategy = InsertStrategy.Into;
    }

    @Override
    public LambdaInsert<T> useQualifier() {
        this.enableQualifier();
        return this;
    }

    protected List<FieldInfo> getPrimaryKeyColumns() {
        List<FieldInfo> pkField = new ArrayList<>();
        MappingRowMapper<T> rowMapper = this.getRowMapper();
        List<String> columnNames = rowMapper.getColumnNames();
        for (String columnName : columnNames) {
            FieldInfo field = rowMapper.findWriteFieldByColumn(columnName);
            if (field.isPrimary()) {
                pkField.add(field);
            }
        }
        return pkField;
    }

    protected List<FieldInfo> getInsertColumns() {
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
        int fieldCount = this.insertColumns.size();
        for (Map<String, Object> map : dataMapList) {
            Object[] args = new Object[fieldCount];
            for (int i = 0; i < fieldCount; i++) {
                FieldInfo info = this.insertColumns.get(i);
                args[i] = map.get(info.getPropertyName());
            }
            this.insertValues.add(args);
        }
        return this;
    }

    @Override
    public InsertExecute<T> applyEntity(List<T> entityList) {
        int fieldCount = this.insertColumns.size();
        for (T entity : entityList) {
            Object[] args = new Object[fieldCount];
            for (int i = 0; i < fieldCount; i++) {
                FieldInfo info = this.insertColumns.get(i);
                args[i] = BeanUtils.readPropertyOrField(entity, info.getPropertyName());
            }
            this.insertValues.add(args);
        }
        return this;
    }

    @Override
    public InsertExecute<T> onDuplicateKeyUpdate() {
        this.insertStrategy = InsertStrategy.Replace;
        return this;
    }

    @Override
    public InsertExecute<T> onDuplicateKeyIgnore() {
        this.insertStrategy = InsertStrategy.Ignore;
        return this;
    }

    @Override
    public InsertExecute<T> onDuplicateKeyBlock() {
        this.insertStrategy = InsertStrategy.Into;
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
        List<String> columnNames = this.insertColumns.stream().map(FieldInfo::getColumnName).collect(Collectors.toList());
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

    @Override
    public BoundSql getBoundSql() {
        return getBoundSql(dialect());
    }

    @Override
    public BoundSql getBoundSql(SqlDialect dialect) {
        // insert ... select
        if (this.insertAsQuery != null) {
            // insert into
            MergeSqlSegment insertTemplate = new MergeSqlSegment();
            insertTemplate.addSegment(INSERT, INTO);
            // columns
            insertTemplate.addSegment(getTableNameAndColumn(dialect));
            // select
            insertTemplate.addSegment(dialect::selectAsInsertConcatStr);
            //
            if (this.isQualifier()) {
                this.insertAsQuery.useQualifier();
            }
            BoundSql boundSql = this.insertAsQuery.getBoundSql(dialect);
            String sqlString = boundSql.getSqlString();
            insertTemplate.addSegment(() -> sqlString);
            return new BoundSql.BoundSqlObj(insertTemplate.getSqlSegment(), boundSql.getArgs());
        }
        //
        // insert ...
        if (this.insertValues.size() == 0) {
            throw new IllegalStateException("there is no data to insert");
        }
        boolean isInsertSqlDialect = dialect instanceof InsertSqlDialect;
        if (isInsertSqlDialect) {
            return dialectInsert((InsertSqlDialect) dialect);
        } else {
            return standardInsert(dialect);
        }
    }

    @Override
    public <V> InsertExecute<T> applyQueryAsInsert(LambdaQuery<V> lambdaQuery) {
        if (!this.insertValues.isEmpty()) {
            throw new IllegalStateException("there is existing insert data, cannot be use INSERT ... SELECT");
        }
        this.insertAsQuery = lambdaQuery;
        return this;
    }

    @Override
    public <V> InsertExecute<T> applyQueryAsInsert(Class<V> exampleType, Consumer<LambdaQuery<V>> queryBuilderConsumer) {
        if (queryBuilderConsumer != null) {
            LambdaQueryWrapper<V> queryWrapper = new LambdaQueryWrapper<>(exampleType, this.getJdbcTemplate());
            queryBuilderConsumer.accept(queryWrapper);
            return applyQueryAsInsert(queryWrapper);
        } else {
            return this;
        }
    }

    @Override
    public int[] executeGetResult() throws SQLException {
        try {
            BoundSql boundSql = getBoundSql();
            String sqlString = boundSql.getSqlString();
            if (boundSql instanceof BatchBoundSql) {
                if (boundSql.getArgs().length > 1) {
                    return this.getJdbcTemplate().executeBatch(sqlString, ((BatchBoundSql) boundSql).getArgs());
                } else {
                    int i = this.getJdbcTemplate().executeUpdate(sqlString, (Object[]) boundSql.getArgs()[0]);
                    return new int[] { i };
                }
            } else {
                int i = this.getJdbcTemplate().executeUpdate(sqlString, boundSql.getArgs());
                return new int[] { i };
            }
        } finally {
            this.insertValues.clear();
            this.insertAsQuery = null;
        }
    }

    protected BoundSql standardInsert(SqlDialect dialect) {
        // insert into
        MergeSqlSegment insertTemplate = new MergeSqlSegment();
        insertTemplate.addSegment(INSERT, INTO);
        // columns
        insertTemplate.addSegment(getTableNameAndColumn(dialect));
        // values
        insertTemplate.addSegment(VALUES, LEFT);
        insertTemplate.addSegment(() -> StringUtils.repeat(",?", this.insertColumns.size()).substring(1));
        insertTemplate.addSegment(RIGHT);
        //
        String sqlString = insertTemplate.getSqlSegment();
        Object[][] args = new Object[this.insertValues.size()][];
        for (int i = 0; i < this.insertValues.size(); i++) {
            args[i] = this.insertValues.get(i);
        }
        return new BatchBoundSql.BatchBoundSqlObj(sqlString, args);
    }

    protected BoundSql dialectInsert(InsertSqlDialect dialect) {
        MappingRowMapper<T> rowMapper = this.getRowMapper();
        String category = rowMapper.getCategory();
        String tableName = rowMapper.getTableName();
        switch (this.insertStrategy) {
            case Ignore: {
                if (dialect.supportInsertIgnore(this.primaryKeyColumns)) {
                    String sqlString = dialect.insertWithIgnore(this.isQualifier(), category, tableName, this.primaryKeyColumns, this.insertColumns);
                    return buildBatchBoundSql(sqlString);
                }
                break;
            }
            case Replace: {
                if (dialect.supportInsertReplace(this.primaryKeyColumns)) {
                    String sqlString = dialect.insertWithReplace(this.isQualifier(), category, tableName, this.primaryKeyColumns, this.insertColumns);
                    return buildBatchBoundSql(sqlString);
                }
                break;
            }
        }
        return standardInsert(dialect);
    }

    protected BatchBoundSql buildBatchBoundSql(String batchSql) {
        Object[][] args = new Object[this.insertValues.size()][];
        for (int i = 0; i < this.insertValues.size(); i++) {
            args[i] = this.insertValues.get(i);
        }
        return new BatchBoundSql.BatchBoundSqlObj(batchSql, args);
    }

    protected static enum InsertStrategy {
        Ignore,
        Replace,
        Into
    }
}
