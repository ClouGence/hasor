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

import java.util.Collections;
import java.util.List;

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
    public R select(String column, String... columns);

    /**
     * 查询指定列。
     * 在分组查询下：设置参数中，只有 group by 列才会被查询。 */
    public default R select(SFunction<T> column) {
        return select(Collections.singletonList(column));
    }

    /**
     * 查询指定列。
     * 在分组查询下：设置参数中，只有 group by 列才会被查询。 */
    public R select(List<SFunction<T>> columns);

    /**分组，类似：group by xxx */
    public default R groupBy(SFunction<T> column) {
        return groupBy(Collections.singletonList(column));
    }

    /**分组，类似：group by xxx */
    public R groupBy(List<SFunction<T>> columns);

    /** 排序，类似：order by xxx */
    public default R orderBy(SFunction<T> column) {
        return orderBy(Collections.singletonList(column));
    }

    /** 排序，类似：order by xxx */
    public R orderBy(List<SFunction<T>> columns);

    /** 排序(升序)，类似：order by xxx desc */
    public default R asc(SFunction<T> column) {
        return asc(Collections.singletonList(column));
    }

    /** 排序(升序)，类似：order by xxx desc */
    public R asc(List<SFunction<T>> columns);

    /** 排序(降序)，类似：order by xxx desc */
    public default R desc(SFunction<T> column) {
        return desc(Collections.singletonList(column));
    }

    /** 排序(降序)，类似：order by xxx desc */
    public R desc(List<SFunction<T>> columns);
}