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
import net.hasor.db.jdbc.lambda.LambdaOperations;
import net.hasor.db.jdbc.lambda.LambdaOperations.LambdaQuery;
import net.hasor.db.jdbc.lambda.dialect.SqlDialect;
import net.hasor.db.jdbc.lambda.segment.MergeSqlSegment;
import net.hasor.db.jdbc.lambda.segment.OrderByKeyword;
import net.hasor.db.jdbc.lambda.segment.Segment;
import net.hasor.db.jdbc.mapping.FieldMeta;
import net.hasor.db.jdbc.mapping.MetaManager;
import net.hasor.db.jdbc.mapping.TableMeta;
import net.hasor.utils.StringUtils;
import net.hasor.utils.reflect.SFunction;

import java.util.*;
import java.util.function.Consumer;

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
    private       String               result          = null;

    public LambdaQueryWrapper(Class<T> exampleType, FieldMeta[] selectColumns, JdbcOperations jdbcOperations) {
        super(exampleType, jdbcOperations);
        for (FieldMeta cm : selectColumns) {
            addSelection(cm);
        }
    }

    private static Segment buildTabName(Class<?> exampleType, SqlDialect dialect) {
        TableMeta tableMeta = MetaManager.loadTableMeta(exampleType);
        if (tableMeta == null) {
            return () -> dialect.buildTableName(MetaManager.toTableMeta(exampleType.getSimpleName()));
        } else {
            return () -> dialect.buildTableName(tableMeta);
        }
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

    @Override
    protected LambdaQuery<T> getSelf() {
        return this;
    }

    @Override
    public LambdaOperations.AbstractLambdaQuery<T, LambdaQuery<T>> and(Consumer<LambdaOperations.NestedQuery<T>> lambda) {
        Segment andBody = () -> {
            lambda.accept(new NestedQueryWrapper<>(this));
            return "";
        };
        this.addCondition(AND, new MergeSqlSegment(LEFT, andBody, RIGHT));
        return this;
    }

    @Override
    public LambdaOperations.AbstractLambdaQuery<T, LambdaQuery<T>> or(Consumer<LambdaOperations.NestedQuery<T>> lambda) {
        Segment orBody = () -> {
            lambda.accept(new NestedQueryWrapper<>(this));
            return "";
        };
        this.addCondition(OR, new MergeSqlSegment(LEFT, orBody, RIGHT));
        return this;
    }

    @Override
    public LambdaQuery<T> selectAll() {
        this.selectSegments.clear();
        return this;
    }

    @Override
    public LambdaQuery<T> select(String... columns) {
        FieldMeta[] fieldMetas = MetaManager.loadColumnMeta(exampleType());
        if (fieldMetas != null) {
            Set<String> matching = new HashSet<>(Arrays.asList(columns));
            Arrays.stream(fieldMetas).filter(cm -> {
                return matching.contains(cm.getColumnName());
            }).forEach(this::addSelection);
        }
        return this;
    }

    @Override
    public LambdaQuery<T> select(String columns, Class<?> javaType) {
        return addSelection(MetaManager.toColumnMeta(columns, javaType));
    }

    @Override
    public LambdaQuery<T> select(Map<String, Class<?>> columns) {
        columns.forEach(this::select);
        return this;
    }

    @Override
    public LambdaQuery<T> select(SFunction<T, ?>... columns) {
        Arrays.stream(columns).forEach(property -> {
            addSelection(columnName(property));
        });
        return this;
    }

    private LambdaQuery<T> addSelection(FieldMeta cm) {
        String name = cm.getColumnName();
        String alias = cm.getAliasName();
        String key = StringUtils.isNotBlank(alias) ? alias : name;
        //
        String select = this.dialect.buildSelect(cm);
        this.selectSegments.put(key, () -> select);
        return this;
    }

    @Override
    public LambdaQuery<T> apply(String sqlString, Object... args) {
        if (StringUtils.isBlank(sqlString)) {
            return this.getSelf();
        }
        if (args == null || args.length == 0) {
            this.addCondition(() -> sqlString);
            return null;
        }
        MergeSqlSegment mergeSqlSegment = new MergeSqlSegment();
        String[] splitKeep = StringUtils.splitKeep(sqlString, "?");
        for (int i = 0; i < splitKeep.length; i++) {
            String term = splitKeep[i];
            if ("?".equals(term)) {
                mergeSqlSegment.addSegment(formatSegment(args[i]));
            } else if (StringUtils.isNotBlank(term)) {
                mergeSqlSegment.addSegment(() -> term);
            }
        }
        return this.addCondition(mergeSqlSegment);
    }

    public LambdaQuery<T> groupBy(SFunction<T, ?>... columns) {
        for (SFunction<T, ?> fun : columns) {
            this.addGroupBy(fun, () -> conditionName(fun));
        }
        return this.getSelf();
    }

    protected LambdaQuery<T> addGroupBy(SFunction<T, ?> column, Segment segment) {
        FieldMeta cm = columnName(column);
        this.groupBySegments.put(cm.getColumnName(), segment);
        return this.getSelf();
    }

    public LambdaQuery<T> orderBy(SFunction<T, ?>... columns) {
        return this.addOrderBy(ORDER_DEFAULT, columns);
    }

    public LambdaQuery<T> asc(SFunction<T, ?>... columns) {
        return this.addOrderBy(ASC, columns);
    }

    public LambdaQuery<T> desc(SFunction<T, ?>... columns) {
        return this.addOrderBy(DESC, columns);
    }

    private LambdaQuery<T> addOrderBy(OrderByKeyword keyword, SFunction<T, ?>... columns) {
        for (SFunction<T, ?> fun : columns) {
            this.addOrderBy(fun, new MergeSqlSegment(() -> conditionName(fun), keyword));
        }
        return this.getSelf();
    }

    protected LambdaQuery<T> addOrderBy(SFunction<T, ?> column, Segment segment) {
        FieldMeta cm = columnName(column);
        this.orderBySegments.put(cm.getColumnName(), segment);
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
        sqlSegment.addSegment(buildTabName(exampleType(), this.dialect));
        if (!this.queryTemplate.isEmpty()) {
            sqlSegment.addSegment(WHERE);
            sqlSegment.addSegment(this.queryTemplate.sub(1));
        }
        if (!this.groupBySegments.isEmpty()) {
            sqlSegment.addSegment(GROUP_BY);
            sqlSegment.addSegment(buildBySeparator(this.groupBySegments, ","));
        }
        if (!this.orderBySegments.isEmpty()) {
            sqlSegment.addSegment(ORDER_BY);
            sqlSegment.addSegment(buildBySeparator(this.orderBySegments, ","));
        }
        this.result = sqlSegment.getSqlSegment();
        return this.result;
    }
}