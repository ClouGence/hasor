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
import net.hasor.db.dialect.BoundSql;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.lambda.LambdaOperations.LambdaUpdate;
import net.hasor.db.lambda.UpdateExecute;
import net.hasor.db.lambda.segment.MergeSqlSegment;
import net.hasor.db.mapping.ColumnMapping;
import net.hasor.db.mapping.MappingRowMapper;
import net.hasor.db.metadata.ColumnDef;
import net.hasor.db.metadata.TableDef;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.reflect.SFunction;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.hasor.db.lambda.segment.SqlKeyword.*;

/**
 * 提供 lambda update 能力，是 LambdaUpdate 接口的实现类。
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
public class LambdaUpdateWrapper<T> extends AbstractQueryCompare<T, LambdaUpdate<T>> implements LambdaUpdate<T> {
    protected final Map<String, ColumnMapping> allowUpdateColumns;
    protected final Map<String, Object>        updateValueMap;
    private         boolean                    allowEmptyWhere = false;

    public LambdaUpdateWrapper(Class<T> exampleType, JdbcTemplate jdbcTemplate) {
        super(exampleType, jdbcTemplate);
        this.allowUpdateColumns = this.getAllowUpdateColumns();
        this.updateValueMap = new HashMap<>();
    }

    protected Map<String, ColumnMapping> getAllowUpdateColumns() {
        Map<String, ColumnMapping> toUpdateField = new LinkedHashMap<>();
        MappingRowMapper<T> rowMapper = this.getRowMapper();
        List<String> columnNames = rowMapper.getColumnNames();
        for (String columnName : columnNames) {
            ColumnMapping columnMapping = rowMapper.findWriteFieldByColumn(columnName);
            if (columnMapping.isUpdate()) {
                toUpdateField.put(columnMapping.getName(), columnMapping);
            }
        }
        if (toUpdateField.size() == 0) {
            throw new IllegalStateException("no column require UPDATE.");
        }
        return toUpdateField;
    }

    @Override
    protected boolean supportPage() {
        return false;// update is disable Page;
    }

    @Override
    protected LambdaUpdate<T> getSelf() {
        return this;
    }

    @Override
    public LambdaUpdate<T> useQualifier() {
        this.enableQualifier();
        return this;
    }

    @Override
    public UpdateExecute<T> allowEmptyWhere() {
        this.allowEmptyWhere = true;
        return this;
    }

    @Override
    public UpdateExecute<T> updateTo(T newValue, List<SFunction<T>> propertyList) {
        if (propertyList == null || propertyList.isEmpty()) {
            throw new NullPointerException("propertyList not be null.");
        }
        Map<String, ColumnMapping> updateFieldList = propertyList.stream().map(this::columnName)//
                .collect(Collectors.toMap(ColumnMapping::getPropertyName, o -> o));
        //
        return this.updateTo(newValue, fieldInfo -> {
            return updateFieldList.containsKey(fieldInfo.getPropertyName());
        });
    }

    @Override
    public UpdateExecute<T> updateTo(T newValue, Predicate<ColumnMapping> tester) {
        if (newValue == null) {
            throw new NullPointerException("newValue is null.");
        }
        return this.updateTo(property -> {
            return BeanUtils.readPropertyOrField(newValue, property);
        }, tester);
    }

    @Override
    public UpdateExecute<T> updateTo(Map<String, Object> propertyMap, List<SFunction<T>> propertyList) {
        if (propertyList == null || propertyList.isEmpty()) {
            throw new NullPointerException("propertyList not be null.");
        }
        Map<String, ColumnDef> updateFieldList = propertyList.stream().map(this::columnName)//
                .collect(Collectors.toMap(ColumnMapping::getPropertyName, o -> o));
        //
        return this.updateTo(propertyMap, fieldInfo -> {
            return updateFieldList.containsKey(fieldInfo.getPropertyName());
        });
    }

    @Override
    public UpdateExecute<T> updateTo(Map<String, Object> propertyMap, Predicate<ColumnMapping> tester) {
        if (propertyMap == null) {
            throw new NullPointerException("newValue is null.");
        }
        return this.updateTo(propertyMap::get, tester);
    }

    protected <R> UpdateExecute<T> updateTo(Function<String, R> readValueFunction, Predicate<ColumnMapping> tester) {
        if (tester == null) {
            throw new NullPointerException("tester is null.");
        }
        //
        this.updateValueMap.clear();
        for (Map.Entry<String, ColumnMapping> allowFieldEntry : allowUpdateColumns.entrySet()) {
            ColumnMapping allowField = allowFieldEntry.getValue();
            if (!tester.test(allowField)) {
                continue;
            }
            Object fieldValue = readValueFunction.apply(allowField.getPropertyName());
            String columnName = allowField.getName();
            if (this.updateValueMap.containsKey(columnName)) {
                throw new IllegalStateException("Multiple property mapping to '" + columnName + "' column");
            } else {
                this.updateValueMap.put(columnName, fieldValue);
            }
        }
        return this;
    }

    @Override
    public BoundSql getOriginalBoundSql() {
        if (this.updateValueMap.isEmpty()) {
            return null;
        }
        // must be clean , The getOriginalBoundSql will reinitialize.
        this.queryParam.clear();
        //
        // update
        MergeSqlSegment updateTemplate = new MergeSqlSegment();
        updateTemplate.addSegment(UPDATE);
        // tableName
        MappingRowMapper<T> rowMapper = this.getRowMapper();
        TableDef tableDef = rowMapper.getTableInfo();
        String tableName = dialect().tableName(isQualifier(), tableDef);
        updateTemplate.addSegment(() -> tableName);
        //
        updateTemplate.addSegment(SET);
        boolean isFirstColumn = true;
        for (String column : updateValueMap.keySet()) {
            if (isFirstColumn) {
                isFirstColumn = false;
            } else {
                updateTemplate.addSegment(() -> ",");
            }
            //
            ColumnDef columnDef = allowUpdateColumns.get(column);
            String columnName = dialect().columnName(isQualifier(), tableDef, columnDef);
            Object columnValue = updateValueMap.get(column);
            updateTemplate.addSegment(() -> columnName, EQ, formatSegment(columnValue));
        }
        //
        if (!this.queryTemplate.isEmpty()) {
            updateTemplate.addSegment(WHERE);
            updateTemplate.addSegment(this.queryTemplate.sub(1));
        } else if (!this.allowEmptyWhere) {
            throw new UnsupportedOperationException("The dangerous UPDATE operation, You must call `allowEmptyWhere()` to enable UPDATE ALL.");
        }
        //
        String sqlQuery = updateTemplate.getSqlSegment();
        Object[] args = this.queryParam.toArray().clone();
        return new BoundSql.BoundSqlObj(sqlQuery, args);
    }

    @Override
    public int doUpdate() throws SQLException {
        if (this.updateValueMap.isEmpty()) {
            throw new IllegalStateException("Nothing to update.");
        }
        BoundSql boundSql = getBoundSql();
        String sqlString = boundSql.getSqlString();
        return this.getJdbcTemplate().executeUpdate(sqlString, boundSql.getArgs());
    }
}
