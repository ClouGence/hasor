/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.db.jdbc.lambda;
import net.hasor.utils.reflect.SFunction;

import java.util.Collection;

/**
 * 动态拼条件。
 * <p>主动调用or表示紧接着下一个方法不是用and连接!(不调用or则默认为使用and连接)</p>
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public interface Compare<T, R> {
    /** 等于条件 查询，类似：'or ...' */
    public R or();

    /** 等于条件 查询，类似：'col = ?' */
    public R eq(SFunction<T> property, Object value);

    /** 不等于条件 查询，类似：'col <> ?' */
    public R ne(SFunction<T> property, Object value);

    /** 大于条件 查询，类似：'col > ?' */
    public R gt(SFunction<T> property, Object value);

    /** 大于等于条件 查询，类似：'col >= ?' */
    public R ge(SFunction<T> property, Object value);

    /** 小于条件 查询，类似：'col < ?' */
    public R lt(SFunction<T> property, Object value);

    /** 小于等于条件 查询，类似：'col <= ?' */
    public R le(SFunction<T> property, Object value);

    /** like 查询，类似：'col like CONCAT('%', ?, '%')' */
    public R like(SFunction<T> property, Object value);

    /** not like 查询，类似：'col not like CONCAT('%', ?, '%')' */
    public R notLike(SFunction<T> property, Object value);

    /** like 查询，类似：'col like CONCAT(?, '%')' */
    public R likeRight(SFunction<T> property, Object value);

    /** not like 查询，类似：'col not like CONCAT(?, '%')' */
    public R notLikeRight(SFunction<T> property, Object value);

    /** like 查询，类似：'col like CONCAT('%', ?)' */
    public R likeLeft(SFunction<T> property, Object value);

    /** not like 查询，类似：'col not like CONCAT('%', ?)' */
    public R notLikeLeft(SFunction<T> property, Object value);

    /** is null 查询，类似：'col is null' */
    public R isNull(SFunction<T> property);

    /** not null 查询，类似：'col is not null' */
    public R isNotNull(SFunction<T> property);

    /** in 查询，类似：'col in (?,?,?)' */
    public R in(SFunction<T> property, Collection<?> value);

    /** not in 查询，类似：'col not in (?,?,?)' */
    public R notIn(SFunction<T> property, Collection<?> value);

    /** between 语句，类似：'col between ? and ?' */
    public R between(SFunction<T> property, Object value1, Object value2);

    /** not between 语句，类似：'col not between ? and ?' */
    public R notBetween(SFunction<T> property, Object value1, Object value2);
    //    /** in 子查询，类似：'col in (LambdaQuery)' */
    //    public <V> R andInLambda(SFunction<T> property, CompareBuilder<V> subLambda);
    //    /** in 子查询，类似：'or col in (LambdaQuery)' */
    //    public <V> R orInLambda(SFunction<T> property, CompareBuilder<V> subLambda);
    //    /** not in 子查询，类似：'col not in (LambdaQuery)' */
    //    public <V> R andNotInLambda(SFunction<T> property, CompareBuilder<V> subLambda);
    //    /** not in 子查询，类似：'or col not in (LambdaQuery)' */
    //    public <V> R orNotInLambda(SFunction<T> property, CompareBuilder<V> subLambda);
    //    /** in SQL 子查询，类似：'col in (subQuery)' */
    //    public R andInSql(SFunction<T> property, String subQuery, Object... subArgs);
    //    /** in SQL 子查询，类似：'or col in (subQuery)' */
    //    public R orInSql(SFunction<T> property, String subQuery, Object... subArgs);
    //    /** not in SQL 子查询，类似：'col not in (subQuery)' */
    //    public R andNotInSql(SFunction<T> property, String subQuery, Object... subArgs);
    //    /** not in SQL 子查询，类似：'or col not in (subQuery)' */
    //    public R orNotInSql(SFunction<T> property, String subQuery, Object... subArgs);

    /**
     * 拼接 sql
     * <p>!! 会有 sql 注入风险 !!</p>
     * <p>例1: apply("id = 1")</p>
     * <p>例2: apply("date_format(dateColumn,'%Y-%m-%d') = '2008-08-08'")</p>
     * <p>例3: apply("date_format(dateColumn,'%Y-%m-%d') = {0}", LocalDate.now())</p>
     */
    public R apply(String sqlString, Object... args);
}