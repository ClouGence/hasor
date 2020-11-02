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
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public interface Compare<T, R> {
    /** 等于条件 查询，类似：'and col = ?' */
    public R andEq(SFunction<T, ?> property, Object value);

    /** 等于条件 查询，类似：'or col = ?' */
    public R orEq(SFunction<T, ?> property, Object value);

    /** 不等于条件 查询，类似：'and col <> ?' */
    public R andNe(SFunction<T, ?> property, Object value);

    /** 不等于条件 查询，类似：'or col <> ?' */
    public R orNe(SFunction<T, ?> property, Object value);

    /** 大于条件 查询，类似：'and col > ?' */
    public R andGt(SFunction<T, ?> property, Object value);

    /** 大于条件 查询，类似：'or col > ?' */
    public R orGt(SFunction<T, ?> property, Object value);

    /** 大于等于条件 查询，类似：'and col >= ?' */
    public R andGe(SFunction<T, ?> property, Object value);

    /** 大于等于条件 查询，类似：'or col >= ?' */
    public R orGe(SFunction<T, ?> property, Object value);

    /** 小于条件 查询，类似：'and col < ?' */
    public R andLt(SFunction<T, ?> property, Object value);

    /** 小于条件 查询，类似：'or col < ?' */
    public R orLt(SFunction<T, ?> property, Object value);

    /** 小于等于条件 查询，类似：'and col <= ?' */
    public R andLe(SFunction<T, ?> property, Object value);

    /** 小于等于条件 查询，类似：'or col <= ?' */
    public R orLe(SFunction<T, ?> property, Object value);

    /** like 查询，类似：'and col like ?' */
    public R andLike(SFunction<T, ?> property, Object value);

    /** like 查询，类似：'or col like ?' */
    public R orLike(SFunction<T, ?> property, Object value);

    /** not like 查询，类似：'and col not like ?' */
    public R andNotLike(SFunction<T, ?> property, Object value);

    /** not like 查询，类似：'or col not like ?' */
    public R orNotLike(SFunction<T, ?> property, Object value);

    /** is null 查询，类似：'and col is null' */
    public R andIsNull(SFunction<T, ?> property);

    /** is null 查询，类似：'or col is null' */
    public R orIsNull(SFunction<T, ?> property);

    /** not null 查询，类似：'and col is not null' */
    public R andIsNotNull(SFunction<T, ?> property);

    /** not null 查询，类似：'or col is not null' */
    public R orIsNotNull(SFunction<T, ?> property);

    /** in 查询，类似：'and col in (?,?,?)' */
    public R andIn(SFunction<T, ?> property, Collection<?> value);

    /** in 查询，类似：'or col in (?,?,?)' */
    public R orIn(SFunction<T, ?> property, Collection<?> value);

    /** not in 查询，类似：'and col not in (?,?,?)' */
    public R andNotIn(SFunction<T, ?> property, Collection<?> value);

    /** not in 查询，类似：'or col not in (?,?,?)' */
    public R orNotIn(SFunction<T, ?> property, Collection<?> value);
    //    /** in 子查询，类似：'and col in (LambdaQuery)' */
    //    public <V> R andInLambda(SFunction<T, ?> property, CompareBuilder<V> subLambda);
    //    /** in 子查询，类似：'or col in (LambdaQuery)' */
    //    public <V> R orInLambda(SFunction<T, ?> property, CompareBuilder<V> subLambda);
    //    /** not in 子查询，类似：'and col not in (LambdaQuery)' */
    //    public <V> R andNotInLambda(SFunction<T, ?> property, CompareBuilder<V> subLambda);
    //    /** not in 子查询，类似：'or col not in (LambdaQuery)' */
    //    public <V> R orNotInLambda(SFunction<T, ?> property, CompareBuilder<V> subLambda);
    //    /** in SQL 子查询，类似：'and col in (subQuery)' */
    //    public R andInSql(SFunction<T, ?> property, String subQuery, Object... subArgs);
    //    /** in SQL 子查询，类似：'or col in (subQuery)' */
    //    public R orInSql(SFunction<T, ?> property, String subQuery, Object... subArgs);
    //    /** not in SQL 子查询，类似：'and col not in (subQuery)' */
    //    public R andNotInSql(SFunction<T, ?> property, String subQuery, Object... subArgs);
    //    /** not in SQL 子查询，类似：'or col not in (subQuery)' */
    //    public R orNotInSql(SFunction<T, ?> property, String subQuery, Object... subArgs);

    /** between 语句，类似：'and col between ? and ?' */
    public R andBetween(SFunction<T, ?> property, Object value1, Object value2);

    /** between 语句，类似：'or col between ? and ?' */
    public R orBetween(SFunction<T, ?> property, Object value1, Object value2);

    /** not between 语句，类似：'and col not between ? and ?' */
    public R andNotBetween(SFunction<T, ?> property, Object value1, Object value2);

    /** not between 语句，类似：'or col not between ? and ?' */
    public R orNotBetween(SFunction<T, ?> property, Object value1, Object value2);
}