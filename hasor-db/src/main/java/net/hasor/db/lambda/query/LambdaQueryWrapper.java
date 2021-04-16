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
import net.hasor.db.dialect.SqlDialect;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.lambda.LambdaOperations.LambdaQuery;
import net.hasor.db.lambda.QueryExecute;
import net.hasor.db.lambda.page.Page;
import net.hasor.db.lambda.segment.MergeSqlSegment;
import net.hasor.db.lambda.segment.OrderByKeyword;
import net.hasor.db.lambda.segment.Segment;
import net.hasor.db.mapping.TableMapping;
import net.hasor.db.metadata.ColumnDef;
import net.hasor.db.metadata.TableDef;
import net.hasor.utils.reflect.SFunction;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.hasor.db.lambda.segment.OrderByKeyword.*;
import static net.hasor.db.lambda.segment.SqlKeyword.*;

/**
 * 提供 lambda query 能力。是 LambdaQuery 接口的实现类。
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
public class LambdaQueryWrapper<T> extends AbstractQueryCompare<T, LambdaQuery<T>> implements LambdaQuery<T> {
    private final List<Segment> customSelect    = new ArrayList<>();
    private final List<Segment> groupBySegments = new ArrayList<>();
    private final List<Segment> orderBySegments = new ArrayList<>();
    private       boolean       lockGroupBy     = false;
    private       boolean       lockOrderBy     = false;

    public LambdaQueryWrapper(Class<T> exampleType, JdbcTemplate jdbcTemplate) {
        super(exampleType, jdbcTemplate);
    }

    @Override
    protected boolean supportPage() {
        return true;// query is enable Page;
    }

    @Override
    public BoundSql getOriginalBoundSql() {
        // must be clean , The rebuildSQL will reinitialize.
        this.queryParam.clear();
        //
        String sqlQuery = reBuildSql();
        Object[] args = this.queryParam.toArray().clone();
        return new BoundSql.BoundSqlObj(sqlQuery, args);
    }

    private String reBuildSql() {
        MergeSqlSegment sqlSegment = new MergeSqlSegment();
        sqlSegment.addSegment(SELECT);
        sqlSegment.addSegment(buildColumns((this.groupBySegments.isEmpty() ? this.customSelect : this.groupBySegments)));
        sqlSegment.addSegment(FROM);
        sqlSegment.addSegment(buildTabName(this.dialect()));
        if (!this.queryTemplate.isEmpty()) {
            Segment firstSqlSegment = this.queryTemplate.firstSqlSegment();
            if (firstSqlSegment == GROUP_BY || firstSqlSegment == HAVING || firstSqlSegment == ORDER_BY) {
                sqlSegment.addSegment(this.queryTemplate);
            } else {
                sqlSegment.addSegment(WHERE);
                sqlSegment.addSegment(this.queryTemplate.sub(1));
            }
        }
        return sqlSegment.getSqlSegment();
    }

    private Segment buildTabName(SqlDialect dialect) {
        TableDef tableDef = super.getTableMapping();
        if (tableDef == null) {
            throw new IllegalArgumentException("tableDef not found.");
        }
        return () -> dialect.tableName(isQualifier(), tableDef);
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
    public QueryExecute<T> useQualifier() {
        this.enableQualifier();
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
    public final LambdaQuery<T> select(List<SFunction<T>> properties) {
        List<ColumnDef> selectColumn = properties.stream()//
                .filter(Objects::nonNull).map(this::propertyMapping).collect(Collectors.toList());
        return this.select0(selectColumn, fieldInfo -> true);
    }

    @Override
    public final LambdaQuery<T> select(Predicate<ColumnDef> tester) {
        TableMapping tableDef = super.getTableMapping();
        List<String> allProperty = tableDef.getPropertyNames();
        List<ColumnDef> collect = allProperty.stream().map(tableDef::getMapping).collect(Collectors.toList());
        return this.select0(collect, tester);
    }

    private LambdaQuery<T> select0(Collection<ColumnDef> allFiled, Predicate<ColumnDef> tester) {
        TableMapping tableDef = super.getTableMapping();
        allFiled.stream().filter(tester).forEach(columnDef -> {
            String selectColumn = dialect().columnName(isQualifier(), tableDef, columnDef);
            customSelect.add(() -> selectColumn);
        });
        return this;
    }

    public final LambdaQuery<T> groupBy(List<SFunction<T>> properties) {
        if (this.lockGroupBy) {
            throw new IllegalStateException("group by is locked.");
        }
        this.lockCondition();
        if (properties != null && !properties.isEmpty()) {
            if (this.groupBySegments.isEmpty()) {
                this.queryTemplate.addSegment(GROUP_BY);
            }
            List<Segment> groupBySeg = new ArrayList<>();
            for (SFunction<T> fun : properties) {
                groupBySeg.add(() -> conditionName(fun));
            }
            this.groupBySegments.addAll(groupBySeg);
            this.queryTemplate.addSegment(buildBySeparator(groupBySeg, ","));
        }
        return this.getSelf();
    }

    public LambdaQuery<T> orderBy(List<SFunction<T>> properties) {
        return this.addOrderBy(ORDER_DEFAULT, properties);
    }

    public LambdaQuery<T> asc(List<SFunction<T>> properties) {
        return this.addOrderBy(ASC, properties);
    }

    public LambdaQuery<T> desc(List<SFunction<T>> properties) {
        return this.addOrderBy(DESC, properties);
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
    public LambdaQuery<T> usePage(Page pageInfo) {
        Page page = this.pageInfo();
        page.setPageSize(pageInfo.getPageSize());
        page.setCurrentPage(pageInfo.getCurrentPage());
        page.setPageNumberOffset(pageInfo.getPageNumberOffset());
        return this.getSelf();
    }

    @Override
    public LambdaQuery<T> initPage(int pageSize, int pageNumber) {
        Page pageInfo = pageInfo();
        pageInfo.setPageNumberOffset(0);
        pageInfo.setPageSize(pageSize);
        pageInfo.setCurrentPage(pageNumber);
        return this.getSelf();
    }
}
