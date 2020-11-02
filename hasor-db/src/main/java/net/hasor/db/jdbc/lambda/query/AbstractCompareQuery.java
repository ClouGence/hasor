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
import net.hasor.db.jdbc.lambda.Compare;
import net.hasor.db.jdbc.lambda.dialect.SqlDialect;
import net.hasor.db.jdbc.lambda.mapping.ColumnMeta;
import net.hasor.db.jdbc.lambda.mapping.MetaManager;
import net.hasor.db.jdbc.lambda.segment.MergeSqlSegment;
import net.hasor.db.jdbc.lambda.segment.Segment;
import net.hasor.db.jdbc.lambda.segment.SqlLike;
import net.hasor.utils.ArrayUtils;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.reflect.MethodUtils;
import net.hasor.utils.reflect.SFunction;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static net.hasor.db.jdbc.lambda.segment.SqlKeyword.*;

/**
 * 提供 lambda 方式生成 SQL。
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractCompareQuery<T, R> extends AbstractQueryExecute<T> implements Compare<T, R> {
    private static final Map<String, ColumnMeta> COLUMN_CACHE      = Collections.synchronizedMap(new WeakHashMap<>());
    private static final ReadWriteLock           COLUMN_CACHE_LOCK = new ReentrantReadWriteLock();
    protected            MergeSqlSegment         queryTemplate     = new MergeSqlSegment();
    protected            AtomicInteger           paramNameSeq      = new AtomicInteger();
    protected            Map<String, Object>     queryParam        = new HashMap<>();

    protected static <T> ColumnMeta columnName(SFunction<T, ?> property) {
        Method targetMethod = MethodUtils.lambdaMethodName(property);
        String cacheKey = targetMethod.toGenericString();
        Lock readLock = COLUMN_CACHE_LOCK.readLock();
        try {
            readLock.lock();
            ColumnMeta columnMeta = COLUMN_CACHE.get(cacheKey);
            if (columnMeta != null) {
                return columnMeta;
            }
        } finally {
            readLock.unlock();
        }
        //
        Lock writeLock = COLUMN_CACHE_LOCK.writeLock();
        try {
            writeLock.lock();
            ColumnMeta columnMeta = COLUMN_CACHE.get(cacheKey);
            if (columnMeta != null) {
                return columnMeta;
            }
            String methodName = targetMethod.getName();
            String attr = null;
            if (methodName.startsWith("get")) {
                attr = methodName.substring(3);
            } else {
                attr = methodName.substring(2);
            }
            String fieldName = StringUtils.firstCharToLowerCase(attr);
            Field field = BeanUtils.getField(fieldName, targetMethod.getDeclaringClass());
            columnMeta = MetaManager.loadColumnMeta(field);
            columnMeta = columnMeta == null ? MetaManager.toColumnMeta(fieldName, field.getType()) : columnMeta;
            COLUMN_CACHE.put(cacheKey, columnMeta);
            return columnMeta;
        } finally {
            writeLock.unlock();
        }
    }

    public AbstractCompareQuery(Class<T> exampleType, JdbcOperations jdbcOperations) {
        super(exampleType, jdbcOperations);
    }

    AbstractCompareQuery(Class<T> exampleType, JdbcOperations jdbcOperations, String dbType, SqlDialect dialect) {
        super(exampleType, jdbcOperations, dbType, dialect);
    }

    public R andEq(SFunction<T, ?> property, Object value) {
        return this.addCondition(AND, () -> conditionName(property), EQ, formatValue(value));
    }

    public R orEq(SFunction<T, ?> property, Object value) {
        return this.addCondition(OR, () -> conditionName(property), EQ, formatValue(value));
    }

    public R andNe(SFunction<T, ?> property, Object value) {
        return this.addCondition(AND, () -> conditionName(property), NE, formatValue(value));
    }

    public R orNe(SFunction<T, ?> property, Object value) {
        return this.addCondition(OR, () -> conditionName(property), NE, formatValue(value));
    }

    public R andGt(SFunction<T, ?> property, Object value) {
        return this.addCondition(AND, () -> conditionName(property), GT, formatValue(value));
    }

    public R orGt(SFunction<T, ?> property, Object value) {
        return this.addCondition(OR, () -> conditionName(property), GT, formatValue(value));
    }

    public R andGe(SFunction<T, ?> property, Object value) {
        return this.addCondition(AND, () -> conditionName(property), GE, formatValue(value));
    }

    public R orGe(SFunction<T, ?> property, Object value) {
        return this.addCondition(OR, () -> conditionName(property), GE, formatValue(value));
    }

    public R andLt(SFunction<T, ?> property, Object value) {
        return this.addCondition(AND, () -> conditionName(property), LT, formatValue(value));
    }

    public R orLt(SFunction<T, ?> property, Object value) {
        return this.addCondition(OR, () -> conditionName(property), LT, formatValue(value));
    }

    public R andLe(SFunction<T, ?> property, Object value) {
        return this.addCondition(AND, () -> conditionName(property), LE, formatValue(value));
    }

    public R orLe(SFunction<T, ?> property, Object value) {
        return this.addCondition(OR, () -> conditionName(property), LE, formatValue(value));
    }

    public R andLike(SFunction<T, ?> property, Object value) {
        return this.addCondition(AND, () -> conditionName(property), LIKE, formatLikeValue(SqlLike.DEFAULT, value));
    }

    public R orLike(SFunction<T, ?> property, Object value) {
        return this.addCondition(OR, () -> conditionName(property), LIKE, formatLikeValue(SqlLike.DEFAULT, value));
    }

    public R andNotLike(SFunction<T, ?> property, Object value) {
        return this.addCondition(AND, () -> conditionName(property), NOT, LIKE, formatLikeValue(SqlLike.DEFAULT, value));
    }

    public R orNotLike(SFunction<T, ?> property, Object value) {
        return this.addCondition(OR, () -> conditionName(property), NOT, LIKE, formatLikeValue(SqlLike.DEFAULT, value));
    }

    public R andLikeRight(SFunction<T, ?> property, Object value) {
        return this.addCondition(OR, () -> conditionName(property), NOT, LIKE, formatLikeValue(SqlLike.RIGHT, value));
    }

    public R orLikeRight(SFunction<T, ?> property, Object value) {
        return this.addCondition(OR, () -> conditionName(property), NOT, LIKE, formatLikeValue(SqlLike.RIGHT, value));
    }

    public R andIsNull(SFunction<T, ?> property) {
        return this.addCondition(AND, () -> conditionName(property), IS_NULL);
    }

    public R orIsNull(SFunction<T, ?> property) {
        return this.addCondition(OR, () -> conditionName(property), IS_NULL);
    }

    public R andIsNotNull(SFunction<T, ?> property) {
        return this.addCondition(AND, () -> conditionName(property), IS_NOT_NULL);
    }

    public R orIsNotNull(SFunction<T, ?> property) {
        return this.addCondition(OR, () -> conditionName(property), IS_NOT_NULL);
    }

    public R andIn(SFunction<T, ?> property, Collection<?> value) {
        return this.addCondition(AND, () -> conditionName(property), IN, LEFT, formatValue(value), RIGHT);
    }

    public R orIn(SFunction<T, ?> property, Collection<?> value) {
        return this.addCondition(OR, () -> conditionName(property), IN, LEFT, formatValue(value), RIGHT);
    }

    public R andNotIn(SFunction<T, ?> property, Collection<?> value) {
        return this.addCondition(AND, () -> conditionName(property), NOT, IN, LEFT, formatValue(value), RIGHT);
    }

    public R orNotIn(SFunction<T, ?> property, Collection<?> value) {
        return this.addCondition(OR, () -> conditionName(property), NOT, IN, LEFT, formatValue(value), RIGHT);
    }

    public R andBetween(SFunction<T, ?> property, Object value1, Object value2) {
        return this.addCondition(AND, () -> conditionName(property), BETWEEN, formatValue(value1), AND, formatValue(value2));
    }

    public R orBetween(SFunction<T, ?> property, Object value1, Object value2) {
        return this.addCondition(OR, () -> conditionName(property), BETWEEN, formatValue(value1), AND, formatValue(value2));
    }

    public R andNotBetween(SFunction<T, ?> property, Object value1, Object value2) {
        return this.addCondition(AND, () -> conditionName(property), NOT, BETWEEN, formatValue(value1), AND, formatValue(value2));
    }

    public R orNotBetween(SFunction<T, ?> property, Object value1, Object value2) {
        return this.addCondition(OR, () -> conditionName(property), NOT, BETWEEN, formatValue(value1), AND, formatValue(value2));
    }

    protected R addCondition(Segment... segments) {
        for (Segment segment : segments) {
            this.queryTemplate.addSegment(segment);
        }
        return this.getSelf();
    }

    protected abstract R getSelf();

    @Override
    public String getSqlString() {
        return this.queryTemplate.noFirstSqlSegment();
    }

    @Override
    public Map<String, Object> getArgs() {
        return Collections.unmodifiableMap(this.queryParam);
    }

    private Segment formatLikeValue(SqlLike like, Object param) {
        if (this.dialect == null) {
            return () -> SqlDialect.DEFAULT.buildLike(like, format(param), param);
        } else {
            return () -> this.dialect.buildLike(like, format(param), param);
        }
    }

    private Segment formatValue(Object... params) {
        if (ArrayUtils.isEmpty(params)) {
            return () -> "";
        }
        MergeSqlSegment mergeSqlSegment = new MergeSqlSegment();
        Iterator<Object> iterator = Arrays.asList(params).iterator();
        while (iterator.hasNext()) {
            mergeSqlSegment.addSegment(formatSegment(iterator.next()));
            if (iterator.hasNext()) {
                mergeSqlSegment.addSegment(() -> ",");
            }
        }
        return mergeSqlSegment;
    }

    protected Segment formatSegment(Object param) {
        return () -> format(param);
    }

    protected String format(Object param) {
        String genParamName = "param_" + this.paramNameSeq.incrementAndGet();
        this.queryParam.put(genParamName, param);
        return ":" + genParamName;
    }

    protected String conditionName(SFunction<T, ?> property) {
        return this.dialect.buildConditionName(columnName(property));
    }
}