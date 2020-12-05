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
import net.hasor.db.jdbc.JdbcOperations;
import net.hasor.db.jdbc.lambda.LambdaOperations.LambdaQuery;
import net.hasor.db.jdbc.lambda.dialect.SqlDialect;
import net.hasor.db.jdbc.lambda.segment.MergeSqlSegment;
import net.hasor.db.jdbc.lambda.segment.OrderByKeyword;
import net.hasor.db.jdbc.lambda.segment.Segment;
import net.hasor.db.jdbc.mapping.FieldInfo;
import net.hasor.db.jdbc.mapping.TableInfo;
import net.hasor.utils.StringUtils;
import net.hasor.utils.reflect.SFunction;

import java.util.*;

import static net.hasor.db.jdbc.lambda.segment.OrderByKeyword.*;
import static net.hasor.db.jdbc.lambda.segment.SqlKeyword.*;

/**
 * 提供 lambda 方式生成 SQL。
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
public class LambdaQueryWrapper<T> extends AbstractCompareQuery<T, LambdaQuery<T>> implements LambdaQuery<T> {
    private final Map<String, Segment> selectSegments  = new LinkedHashMap<>();
    private final Map<String, Segment> groupBySegments = new LinkedHashMap<>();
    private final Map<String, Segment> orderBySegments = new LinkedHashMap<>();
    private       boolean              lockGroupBy     = false;
    private       boolean              lockOrderBy     = false;
    private       String               result          = null;

    public LambdaQueryWrapper(Class<T> exampleType, JdbcOperations jdbcOperations) {
        super(exampleType, jdbcOperations);
    }

    private Segment buildTabName(SqlDialect dialect) {
        TableInfo tableInfo = super.getRowMapper().findTableInfo();
        if (tableInfo == null) {
            throw new IllegalArgumentException("tableInfo not found.");
        }
        return () -> dialect.buildTableName(tableInfo);
    }

    private static Segment buildColumns(Map<String, Segment> columnSegments) {
        if (columnSegments.isEmpty()) {
            return COLUMNS;
        }
        return buildBySeparator(columnSegments, ",");
    }

    private static Segment buildBySeparator(Map<String, Segment> orderBySegments, String separator) {
        MergeSqlSegment sqlSegment = new MergeSqlSegment();
        Iterator<Map.Entry<String, Segment>> columnIterator = orderBySegments.entrySet().iterator();
        while (columnIterator.hasNext()) {
            Map.Entry<String, Segment> entry = columnIterator.next();
            sqlSegment.addSegment(entry.getValue());
            if (columnIterator.hasNext()) {
                sqlSegment.addSegment(() -> separator);
            }
        }
        return sqlSegment;
    }

    protected void lockGroupBy() {
        this.lockCondition();
        this.lockGroupBy = true;
    }

    protected void lockOrderBy() {
        this.lockGroupBy();
        this.lockOrderBy = true;
    }

    @Override
    protected LambdaQuery<T> getSelf() {
        return this;
    }

    @Override
    public LambdaQuery<T> selectAll() {
        this.selectSegments.clear();
        return this;
    }

    @Override
    public LambdaQuery<T> select(String column, String... columns) {
        List<String> matching = new ArrayList<>();
        if (StringUtils.isNotBlank(column)) {
            matching.add(column);
        }
        if (columns != null && columns.length > 0) {
            matching.addAll(Arrays.asList(columns));
        }
        matching.stream().map(c -> {
            return super.getRowMapper().findFieldInfoByProperty(c);
        }).filter(Objects::nonNull).forEach(this::addSelection);
        return this;
    }

    @Override
    public final LambdaQuery<T> select(List<SFunction<T>> columns) {
        if (columns != null) {
            columns.stream().filter(Objects::nonNull).forEach(property -> {
                addSelection(columnName(property));
            });
        }
        return this;
    }

    private LambdaQuery<T> addSelection(FieldInfo fieldInfo) {
        TableInfo tableInfo = super.getRowMapper().findTableInfo();
        String select = this.dialect.buildSelect(tableInfo, fieldInfo);
        this.selectSegments.put(fieldInfo.getColumnName(), () -> select);
        return this;
    }

    public final LambdaQuery<T> groupBy(List<SFunction<T>> columns) {
        if (this.lockGroupBy) {
            throw new IllegalStateException("group by is locked.");
        }
        this.lockCondition();
        if (columns != null && !columns.isEmpty()) {
            if (this.groupBySegments.isEmpty()) {
                this.queryTemplate.addSegment(GROUP_BY);
            }
            for (SFunction<T> fun : columns) {
                FieldInfo cm = columnName(fun);
                this.groupBySegments.put(cm.getColumnName(), () -> conditionName(fun));
            }
            this.queryTemplate.addSegment(buildBySeparator(this.groupBySegments, ","));
        }
        return this.getSelf();
    }

    public LambdaQuery<T> orderBy(List<SFunction<T>> columns) {
        return this.addOrderBy(ORDER_DEFAULT, columns);
    }

    public LambdaQuery<T> asc(List<SFunction<T>> columns) {
        return this.addOrderBy(ASC, columns);
    }

    public LambdaQuery<T> desc(List<SFunction<T>> columns) {
        return this.addOrderBy(DESC, columns);
    }

    private LambdaQuery<T> addOrderBy(OrderByKeyword keyword, List<SFunction<T>> order) {
        if (this.lockOrderBy) {
            throw new IllegalStateException("order by is locked.");
        }
        this.lockGroupBy();
        if (order != null && !order.isEmpty()) {
            if (this.orderBySegments.isEmpty()) {
                this.queryTemplate.addSegment(ORDER_BY);
            }
            for (SFunction<T> fun : order) {
                FieldInfo cm = columnName(fun);
                this.orderBySegments.put(cm.getColumnName(), new MergeSqlSegment(() -> conditionName(fun), keyword));
            }
            this.queryTemplate.addSegment(buildBySeparator(this.orderBySegments, ","));
        }
        return this.getSelf();
    }

    @Override
    public String getSqlString() {
        if (this.result != null) {
            return this.result;
        }
        MergeSqlSegment sqlSegment = new MergeSqlSegment();
        sqlSegment.addSegment(SELECT);
        sqlSegment.addSegment(buildColumns((this.groupBySegments.isEmpty() ? this.selectSegments : this.groupBySegments)));
        sqlSegment.addSegment(FROM);
        sqlSegment.addSegment(buildTabName(this.dialect));
        if (!this.queryTemplate.isEmpty()) {
            sqlSegment.addSegment(WHERE);
            sqlSegment.addSegment(this.queryTemplate.sub(1));
        }
        this.result = sqlSegment.getSqlSegment();
        return this.result;
    }
}