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

import java.util.Map;

/**
 * 动态拼条件。
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public interface Func<T, R> {
    /**
     * 查询指定列。
     * 在分组查询下：返回所有分组列 */
    public R selectAll();

    /**
     * 查询指定列。
     * 在分组查询下：设置参数中，只有 group by 列才会被查询。 */
    public R select(String... columns);

    /**
     * 查询指定列。
     * 在分组查询下：设置参数中，只有 group by 列才会被查询。 */
    public R select(String columns, Class<?> javaType);

    /**
     * 查询指定列。
     * 在分组查询下：设置参数中，只有 group by 列才会被查询。 */
    public R select(Map<String, Class<?>> columns);

    /**
     * 查询指定列。
     * 在分组查询下：设置参数中，只有 group by 列才会被查询。 */
    public R select(SFunction<T, ?>... columns);

    /**分组，类似：group by xxx */
    public R groupBy(SFunction<T, ?>... columns);

    /** 排序，类似：order by xxx */
    public R orderBy(SFunction<T, ?>... columns);

    /** 排序(升序)，类似：order by xxx desc */
    public R asc(SFunction<T, ?>... columns);

    /** 排序(降序)，类似：order by xxx desc */
    public R desc(SFunction<T, ?>... columns);

    /**
     * 拼接 sql
     * <p>!! 会有 sql 注入风险 !!</p>
     * <p>例1: apply("id = 1")</p>
     * <p>例2: apply("date_format(dateColumn,'%Y-%m-%d') = '2008-08-08'")</p>
     * <p>例3: apply("date_format(dateColumn,'%Y-%m-%d') = {0}", LocalDate.now())</p>
     */
    public R apply(String sqlString, Object... args);
}