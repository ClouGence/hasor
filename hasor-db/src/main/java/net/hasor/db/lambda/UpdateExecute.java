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
package net.hasor.db.lambda;
import net.hasor.db.mapping.FieldInfo;
import net.hasor.utils.reflect.SFunction;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * lambda Update 执行器
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public interface UpdateExecute<T> extends BoundSqlBuilder {
    /** 生成 select count() 查询语句并查询总数。*/
    public int doUpdate() throws SQLException;

    /** 允许空 Where条件（注意：空 Where 条件会导致更新整个数据库） */
    public UpdateExecute<T> allowEmptyWhere();

    /** 设置 update 的 set 中的值。 */
    public default UpdateExecute<T> applyNewValue(T newValue) throws SQLException {
        return applyNewValue(newValue, (Predicate<FieldInfo>) fieldInfo -> true);
    }

    /** 设置 update 的 set 中的值。 */
    public default UpdateExecute<T> applyNewValue(T newValue, SFunction<T> property) throws SQLException {
        return applyNewValue(newValue, Collections.singletonList(property));
    }

    /** 设置指定列 update 的 set 中的值 */
    public default UpdateExecute<T> applyNewValue(T newValue, String... propertyArrays) throws SQLException {
        List<String> strings = Arrays.asList(propertyArrays);
        return applyNewValue(newValue, (Predicate<FieldInfo>) fieldInfo -> {
            return strings.contains(fieldInfo.getPropertyName());
        });
    }

    /** 设置指定列 update 的 set 中的值 */
    public UpdateExecute<T> applyNewValue(T newValue, List<SFunction<T>> propertyList) throws SQLException;

    /** 设置指定列 update 的 set 中的值 */
    public UpdateExecute<T> applyNewValue(T newValue, Predicate<FieldInfo> tester) throws SQLException;
}
