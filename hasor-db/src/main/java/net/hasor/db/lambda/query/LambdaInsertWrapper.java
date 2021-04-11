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
import net.hasor.db.mapping.MappingRowMapper;
import net.hasor.db.mapping.PropertyMapping;
import net.hasor.db.metadata.ColumnDef;
import net.hasor.db.metadata.TableDef;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.StringUtils;

import java.sql.SQLException;
import java.util.*;
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
    private final List<PropertyMapping> insertProperties;
    private final List<PropertyMapping> primaryKeyProperties;
    private final List<Object[]>        insertValues;
    private       InsertStrategy        insertStrategy;
    private       LambdaQuery<?>        insertAsQuery;

    public LambdaInsertWrapper(Class<T> exampleType, JdbcTemplate jdbcTemplate) {
        super(exampleType, jdbcTemplate);
        this.insertProperties = getInsertProperties();
        this.primaryKeyProperties = getPrimaryKeyColumns();
        this.insertValues = new ArrayList<>();
        this.insertStrategy = InsertStrategy.Into;
    }

    @Override
    public LambdaInsert<T> useQualifier() {
        this.enableQualifier();
        return this;
    }

    protected List<PropertyMapping> getInsertProperties() {
        MappingRowMapper<T> rowMapper = this.getRowMapper();
        List<String> propertyNames = rowMapper.getPropertyNames();
        //
        List<PropertyMapping> toInsertProperties = new ArrayList<>();
        Set<String> insertColumns = new HashSet<>();
        for (String propertyName : propertyNames) {
            PropertyMapping mapping = rowMapper.findFieldByProperty(propertyName);
            String columnName = mapping.getName();
            if (!mapping.isInsert()) {
                continue;
            }
            //
            if (insertColumns.contains(columnName)) {
                throw new IllegalStateException("Multiple property mapping to '" + columnName + "' column");
            } else {
                insertColumns.add(columnName);
                toInsertProperties.add(mapping);
            }
        }
        //
        if (toInsertProperties.size() == 0) {
            throw new IllegalStateException("no column require INSERT.");
        }
        return toInsertProperties;
    }

    protected List<PropertyMapping> getPrimaryKeyColumns() {
        MappingRowMapper<T> rowMapper = this.getRowMapper();
        List<String> propertyNames = rowMapper.getPropertyNames();
        //
        List<PropertyMapping> pkProperties = new ArrayList<>();
        Set<String> pkColumns = new HashSet<>();
        for (String propertyName : propertyNames) {
            PropertyMapping mapping = rowMapper.findFieldByProperty(propertyName);
            String columnName = mapping.getName();
            if (!mapping.isPrimaryKey()) {
                continue;
            }
            //
            if (pkColumns.contains(columnName)) {
                throw new IllegalStateException("Multiple property mapping to '" + columnName + "' column");
            } else {
                pkColumns.add(columnName);
                pkProperties.add(mapping);
            }
        }
        return pkProperties;
    }

    @Override
    public InsertExecute<T> applyMap(List<Map<String, Object>> dataMapList) {
        if (this.insertAsQuery != null) {
            throw new IllegalStateException("there is existing INSERT ... SELECT, cannot be use data");
        }
        int propertyCount = this.insertProperties.size();
        for (Map<String, Object> map : dataMapList) {
            Object[] args = new Object[propertyCount];
            for (int i = 0; i < propertyCount; i++) {
                PropertyMapping mapping = this.insertProperties.get(i);
                args[i] = map.get(mapping.getPropertyName());
            }
            this.insertValues.add(args);
        }
        return this;
    }

    @Override
    public InsertExecute<T> applyEntity(List<T> entityList) {
        if (this.insertAsQuery != null) {
            throw new IllegalStateException("there is existing INSERT ... SELECT, cannot be use data");
        }
        int propertyCount = this.insertProperties.size();
        for (Object entity : entityList) {
            Object[] args = new Object[propertyCount];
            for (int i = 0; i < propertyCount; i++) {
                PropertyMapping mapping = this.insertProperties.get(i);
                args[i] = BeanUtils.readPropertyOrField(entity, mapping.getPropertyName());
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
        for (int i = 0; i < this.insertProperties.size(); i++) {
            if (i != 0) {
                tableNameAndColumn.addSegment(() -> ",");
            }
            PropertyMapping mapping = this.insertProperties.get(i);
            String columnName = dialect.columnName(isQualifier(), tableDef, mapping);
            tableNameAndColumn.addSegment(() -> columnName);
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
            //
            switch (this.insertStrategy) {
                case Ignore: {
                    List<ColumnDef> primaryKeyDefList = this.primaryKeyProperties.parallelStream().map((Function<PropertyMapping, ColumnDef>) o -> o).collect(Collectors.toList());
                    List<ColumnDef> insertColumnDefList = this.insertProperties.parallelStream().map((Function<PropertyMapping, ColumnDef>) o -> o).collect(Collectors.toList());
                    if (isInsertSqlDialect && ((InsertSqlDialect) dialect).supportInsertIgnoreFromSelect(primaryKeyDefList)) {
                        String sqlString = ((InsertSqlDialect) dialect).insertIgnoreFromSelect(this.isQualifier(), tableDef, primaryKeyDefList, insertColumnDefList);
                        insertTemplate.addSegment(() -> sqlString);
                        break;
                    }
                }
                case Replace: {
                    List<ColumnDef> primaryKeyDefList = this.primaryKeyProperties.parallelStream().map((Function<PropertyMapping, ColumnDef>) o -> o).collect(Collectors.toList());
                    List<ColumnDef> insertColumnDefList = this.insertProperties.parallelStream().map((Function<PropertyMapping, ColumnDef>) o -> o).collect(Collectors.toList());
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
        insertTemplate.addSegment(() -> StringUtils.repeat(",?", this.insertProperties.size()).substring(1));
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
        switch (this.insertStrategy) {
            case Ignore: {
                List<ColumnDef> primaryKeyDefList = this.primaryKeyProperties.parallelStream().map((Function<PropertyMapping, ColumnDef>) o -> o).collect(Collectors.toList());
                List<ColumnDef> insertColumnDefList = this.insertProperties.parallelStream().map((Function<PropertyMapping, ColumnDef>) o -> o).collect(Collectors.toList());
                if (dialect.supportInsertIgnore(primaryKeyDefList)) {
                    String sqlString = dialect.insertWithIgnore(this.isQualifier(), tableDef, primaryKeyDefList, insertColumnDefList);
                    return buildBatchBoundSql(sqlString);
                }
                break;
            }
            case Replace: {
                List<ColumnDef> primaryKeyDefList = this.primaryKeyProperties.parallelStream().map((Function<PropertyMapping, ColumnDef>) o -> o).collect(Collectors.toList());
                List<ColumnDef> insertColumnDefList = this.insertProperties.parallelStream().map((Function<PropertyMapping, ColumnDef>) o -> o).collect(Collectors.toList());
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
