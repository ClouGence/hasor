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
import net.hasor.utils.reflect.SFunction;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.hasor.db.jdbc.lambda.segment.OrderByKeyword.*;
import static net.hasor.db.jdbc.lambda.segment.SqlKeyword.*;

/**
 * 提供 lambda 方式生成 SQL。
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
public class LambdaQueryWrapper<T> extends AbstractCompareQuery<T, LambdaQuery<T>> implements LambdaQuery<T> {
    private final List<Segment> customSelect    = new ArrayList<>();
    private final List<Segment> groupBySegments = new ArrayList<>();
    private final List<Segment> orderBySegments = new ArrayList<>();
    private       boolean       lockGroupBy     = false;
    private       boolean       lockOrderBy     = false;
    private       String        result          = null;

    public LambdaQueryWrapper(Class<T> exampleType, JdbcOperations jdbcOperations) {
        super(exampleType, jdbcOperations);
    }

    private Segment buildTabName(SqlDialect dialect) {
        TableInfo tableInfo = super.getRowMapper().getTableInfo();
        if (tableInfo == null) {
            throw new IllegalArgumentException("tableInfo not found.");
        }
        return () -> dialect.buildTableName(tableInfo);
    }

    private static Segment buildColumns(Collection<Segment> columnSegments) {
        if (columnSegments.isEmpty()) {
            return COLUMNS;
        }
        return buildBySeparator(columnSegments, ",");
    }

    private static Segment buildBySeparator(Collection<Segment> orderBySegments, String separator) {
        MergeSqlSegment sqlSegment = new MergeSqlSegment();
        Iterator<Segment> columnIterator = orderBySegments.iterator();
        while (columnIterator.hasNext()) {
            Segment entry = columnIterator.next();
            sqlSegment.addSegment(entry);
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
        this.customSelect.clear();
        return this;
    }

    @Override
    public LambdaQuery<T> select(String... columns) {
        if (columns != null && columns.length > 0) {
            this.customSelect.addAll(Arrays.stream(columns).map((Function<String, Segment>) s -> {
                return () -> s;
            }).collect(Collectors.toList()));
        }
        return this;
    }

    @Override
    public final LambdaQuery<T> select(List<SFunction<T>> columns) {
        List<FieldInfo> selectColumn = columns.stream()//
                .filter(Objects::nonNull).map(this::columnName).collect(Collectors.toList());
        return this.select0(selectColumn, fieldInfo -> true);
    }

    @Override
    public final LambdaQuery<T> select(Predicate<FieldInfo> tester) {
        Collection<FieldInfo> allFiled = super.getRowMapper().allFieldInfoByProperty();
        return this.select0(allFiled, tester);
    }

    private LambdaQuery<T> select0(Collection<FieldInfo> allFiled, Predicate<FieldInfo> tester) {
        TableInfo tableInfo = super.getRowMapper().getTableInfo();
        allFiled.stream().filter(tester).forEach(fieldInfo -> {
            String selectColumn = dialect.buildSelect(tableInfo, fieldInfo);
            customSelect.add(() -> selectColumn);
        });
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
            List<Segment> groupBySeg = new ArrayList<>();
            for (SFunction<T> fun : columns) {
                groupBySeg.add(() -> conditionName(fun));
            }
            this.groupBySegments.addAll(groupBySeg);
            this.queryTemplate.addSegment(buildBySeparator(groupBySeg, ","));
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

    private LambdaQuery<T> addOrderBy(OrderByKeyword keyword, List<SFunction<T>> orderBy) {
        if (this.lockOrderBy) {
            throw new IllegalStateException("order by is locked.");
        }
        this.lockGroupBy();
        if (orderBy != null && !orderBy.isEmpty()) {
            if (this.orderBySegments.isEmpty()) {
                this.queryTemplate.addSegment(ORDER_BY);
            } else {
                this.queryTemplate.addSegment(() -> ",");
            }
            List<Segment> orderBySeg = new ArrayList<>();
            for (SFunction<T> fun : orderBy) {
                MergeSqlSegment orderByItem = new MergeSqlSegment(() -> conditionName(fun), keyword);
                orderBySeg.add(orderByItem);
            }
            this.orderBySegments.addAll(orderBySeg);
            this.queryTemplate.addSegment(buildBySeparator(orderBySeg, ","));
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
        sqlSegment.addSegment(buildColumns((this.groupBySegments.isEmpty() ? this.customSelect : this.groupBySegments)));
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