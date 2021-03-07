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
import net.hasor.db.jdbc.lambda.mapping.FieldInfo;
import net.hasor.utils.reflect.SFunction;

import java.sql.SQLException;
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

    /** 设置 update 的 set 中的值。 */
    public UpdateExecute<T> applyUpdateTo(T newValue) throws SQLException;

    /** 设置指定列 update 的 set 中的值。 */
    public UpdateExecute<T> applyUpdateTo(T newValue, String... columns) throws SQLException;

    /** 设置指定列 update 的 set 中的值。 */
    public UpdateExecute<T> applyUpdateTo(T newValue, SFunction<T> property) throws SQLException;

    /** 设置指定列 update 的 set 中的值。 */
    public UpdateExecute<T> applyUpdateTo(T newValue, List<SFunction<T>> propertyList) throws SQLException;

    /** 设置指定列 update 的 set 中的值。 */
    public UpdateExecute<T> applyUpdateTo(T newValue, Predicate<FieldInfo> tester) throws SQLException;
}
