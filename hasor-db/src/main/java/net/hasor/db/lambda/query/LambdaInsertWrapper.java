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
import net.hasor.db.mapping.ColumnMapping;
import net.hasor.db.mapping.MappingRowMapper;
import net.hasor.db.metadata.ColumnDef;
import net.hasor.db.metadata.TableDef;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.hasor.db.lambda.segment.SqlKeyword.*;

/**
 * 提供 lambda insert 能力。是 LambdaInsert 接口的实现类。
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
public class LambdaInsertWrapper<T> extends AbstractExecute<T> implements LambdaInsert<T> {
    private final List<ColumnMapping> insertColumns;
    private final List<ColumnMapping> primaryKeyColumns;
    private final List<Object[]>      insertValues;
    private       InsertStrategy      insertStrategy;
    private       LambdaQuery<?>      insertAsQuery;

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

    protected List<ColumnMapping> getPrimaryKeyColumns() {
        List<ColumnMapping> pkColumn = new ArrayList<>();
        MappingRowMapper<T> rowMapper = this.getRowMapper();
        List<String> columnNames = rowMapper.getColumnNames();
        for (String columnName : columnNames) {
            ColumnMapping columnMapping = rowMapper.findWriteFieldByColumn(columnName);
            if (columnMapping.isPrimaryKey()) {
                pkColumn.add(columnMapping);
            }
        }
        return pkColumn;
    }

    protected List<ColumnMapping> getInsertColumns() {
        List<ColumnMapping> toInsertColumns = new ArrayList<>();
        MappingRowMapper<T> rowMapper = this.getRowMapper();
        List<String> columnNames = rowMapper.getColumnNames();
        for (String columnName : columnNames) {
            ColumnMapping columnMapping = rowMapper.findWriteFieldByColumn(columnName);
            if (columnMapping.isInsert()) {
                toInsertColumns.add(columnMapping);
            }
        }
        if (toInsertColumns.size() == 0) {
            throw new IllegalStateException("no column require INSERT.");
        }
        return toInsertColumns;
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
                ColumnMapping columnMapping = this.insertColumns.get(i);
                args[i] = map.get(columnMapping.getPropertyName());
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
                ColumnMapping columnMapping = this.insertColumns.get(i);
                args[i] = BeanUtils.readPropertyOrField(entity, columnMapping.getPropertyName());
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
        TableDef tableDef = rowMapper.getTableInfo();
        //
        MergeSqlSegment tableNameAndColumn = new MergeSqlSegment();
        String tableName = dialect.tableName(isQualifier(), tableDef);
        tableNameAndColumn.addSegment(() -> tableName);
        //
        tableNameAndColumn.addSegment(LEFT);
        List<String> columnNames = this.insertColumns.stream().map(ColumnDef::getName).collect(Collectors.toList());
        for (int i = 0; i < columnNames.size(); i++) {
            if (i != 0) {
                tableNameAndColumn.addSegment(() -> ",");
            }
            String columnName = columnNames.get(i);
            ColumnDef columnDef = rowMapper.findWriteFieldByColumn(columnName);
            String selectColumn = dialect.columnName(isQualifier(), tableDef, columnDef);
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
            MergeSqlSegment insertTemplate = new MergeSqlSegment();
            boolean isInsertSqlDialect = dialect instanceof InsertSqlDialect;
            TableDef tableDef = this.getRowMapper().getTableInfo();
            List<ColumnDef> primaryKeyDefList = this.primaryKeyColumns.stream().map((Function<ColumnMapping, ColumnDef>) columnMapping -> columnMapping).collect(Collectors.toList());
            List<ColumnDef> insertColumnDefList = this.insertColumns.stream().map((Function<ColumnMapping, ColumnDef>) columnMapping -> columnMapping).collect(Collectors.toList());
            //
            switch (this.insertStrategy) {
                case Ignore: {
                    if (isInsertSqlDialect && ((InsertSqlDialect) dialect).supportInsertIgnoreFromSelect(primaryKeyDefList)) {
                        String sqlString = ((InsertSqlDialect) dialect).insertIgnoreFromSelect(this.isQualifier(), tableDef, primaryKeyDefList, insertColumnDefList);
                        insertTemplate.addSegment(() -> sqlString);
                        break;
                    }
                }
                case Replace: {
                    if (isInsertSqlDialect && ((InsertSqlDialect) dialect).supportInsertReplaceFromSelect(primaryKeyDefList)) {
                        String sqlString = ((InsertSqlDialect) dialect).insertWithReplaceFromSelect(this.isQualifier(), tableDef, primaryKeyDefList, insertColumnDefList);
                        insertTemplate.addSegment(() -> sqlString);
                        break;
                    }
                }
                case Into:
                default: {
                    // insert into
                    insertTemplate.addSegment(INSERT, INTO);
                    insertTemplate.addSegment(getTableNameAndColumn(dialect));
                }
            }
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
        TableDef tableDef = this.getRowMapper().getTableInfo();
        List<ColumnDef> primaryKeyDefList = this.primaryKeyColumns.stream().map((Function<ColumnMapping, ColumnDef>) columnMapping -> columnMapping).collect(Collectors.toList());
        List<ColumnDef> insertColumnDefList = this.insertColumns.stream().map((Function<ColumnMapping, ColumnDef>) columnMapping -> columnMapping).collect(Collectors.toList());
        //
        switch (this.insertStrategy) {
            case Ignore: {
                if (dialect.supportInsertIgnore(primaryKeyDefList)) {
                    String sqlString = dialect.insertWithIgnore(this.isQualifier(), tableDef, primaryKeyDefList, insertColumnDefList);
                    return buildBatchBoundSql(sqlString);
                }
                break;
            }
            case Replace: {
                if (dialect.supportInsertReplace(primaryKeyDefList)) {
                    String sqlString = dialect.insertWithReplace(this.isQualifier(), tableDef, primaryKeyDefList, insertColumnDefList);
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
