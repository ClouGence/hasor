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
import net.hasor.db.dal.orm.FieldInfo;
import net.hasor.utils.reflect.SFunction;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * lambda SQL 执行
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public interface UpdateExecute<T> {
    /** 参考的样本对象 */
    public Class<T> exampleType();

    /** 根据 Lambda 构造器的条件执行删除。 */
    public int delete() throws SQLException;

    /** 生成 select count() 查询语句并查询总数。*/
    public int updateCount() throws SQLException;

    /** 生成 select count() 查询语句并查询总数。*/
    public long updateLargeCount() throws SQLException;

    /** 根据 Lambda 构造器的条件作为筛选条件，将它们更新为新的状态。 */
    public int updateTo(T newValue) throws SQLException;

    /**
     * 查询指定列。
     * 在分组查询下：设置参数中，只有 group by 列才会被查询。 */
    public int updateTo(T newValue, String... columns);

    /**
     * 查询指定列。
     * 在分组查询下：设置参数中，只有 group by 列才会被查询。 */
    public default int updateTo(T newValue, SFunction<T> column) {
        return updateTo(newValue, Collections.singletonList(column));
    }

    /**
     * 查询指定列。
     * 在分组查询下：设置参数中，只有 group by 列才会被查询。 */
    public int updateTo(T newValue, List<SFunction<T>> columns);

    /**
     * 按条件过滤查询指定列。
     * 在分组查询下：设置参数中，只有 group by 列才会被查询。 */
    public int updateTo(T newValue, Predicate<FieldInfo> tester);
}
