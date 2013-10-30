/*
 * Copyright 2002-2008 the original author or authors.
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
package net.hasor.jdbc.opface;
import java.sql.CallableStatement;
import java.sql.SQLException;
import net.hasor.jdbc.DataAccessException;
/**
 * 通用的回调接口。用来执行基于 {@link CallableStatement}上的任意数量任意类型数据库操作。
 * @version : 2013-10-9
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @author 赵永春(zyc@hasor.net)
 */
public interface CallableStatementCallback<T> {
    /**
     * 执行一个 JDBC 操作。开发者不需要关心数据库连接的状态和事务。
     * @param con 一个可用的 JDBC 数据库连接
     * @return 返回操作执行的最终结果。
     */
    public T doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException;
}