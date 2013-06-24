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
package org.platform.jdbc;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
/**
 * 
 * @version : 2013-5-7
 * @author 赵永春 (zyc@byshell.org)
 */
public interface JdbcOperations {
    /**是否开启了事务*/
    public boolean hasTransaction();
    /**提交事务*/
    public void commitTransaction();
    /**回滚事务*/
    public void rollbackTransaction();
    /**打开事务*/
    public void beginTransaction();
    /*---------------------------------------------------------------------------------------------*/
    /**使用{@link Connection}接口直接执行JDBC数据操作。*/
    public <T> T execute(ConnectionCallback<T> action) throws SQLException;
    /**使用{@link Statement}接口执行JDBC数据操作。*/
    public <T> T execute(StatementCallback<T> action) throws SQLException;
    /**使用{@link PreparedStatement}接口执行JDBC数据操作。*/
    public <T> T execute(String callString, PreparedStatementCallback<T> action) throws SQLException;
    /**使用{@link CallableStatement}接口执行JDBC数据操作。*/
    public <T> T execute(String callString, CallableStatementCallback<T> action) throws SQLException;
    /**执行sql，适用于执行DDL语句。*/
    public void execute(String sqlQuery) throws SQLException;
    /**执行sql，适用于执行DDL语句。*/
    public void execute(String sqlQuery, Object... args) throws SQLException;
    //
    /**创建{@link PreparedStatement}接口对象来查询SQL。
     * @see java.sql.Types */
    public List<Map<String, Object>> queryForList(String sqlQuery) throws SQLException;
    /**创建{@link PreparedStatement}接口对象来查询SQL。
     * @see java.sql.Types */
    public List<Map<String, Object>> queryForList(String sqlQuery, Object... args) throws SQLException;
    //
    /**创建{@link PreparedStatement}接口对象来查询SQL。
     * @see java.sql.Types */
    public Map<String, Object> queryForMap(String sqlQuery) throws SQLException;
    /**创建{@link PreparedStatement}接口对象来查询SQL。
     * @see java.sql.Types */
    public Map<String, Object> queryForMap(String sqlQuery, Object... args) throws SQLException;
    //
    /**执行SQL更新语句，返回受影响的行数。*/
    public int update(String sqlQuery) throws SQLException;
    /**执行SQL更新语句，返回受影响的行数。*/
    public int update(String sqlQuery, Object... args) throws SQLException;
    //
    /**对一批数据执行批处理操作。*/
    public int[] batchUpdate(String[] sqls);
    /**对一批数据执行批处理操作。*/
    public int[] batchUpdate(String sqls, List<Object[]> args);
    /**对一批数据执行批处理操作。*/
    public int[] batchUpdate(String sqls, List<Object[]> args, List<int[]> argTypes);
    //
    /**查询一条记录返回其字符串形式的数据*/
    public String queryForString(String sql) throws SQLException;
    /**查询一条记录返回其字符串形式的数据*/
    public String queryForString(String sql, Object... args) throws SQLException;
    /**查询一条记录返回其int形式的数据*/
    public int queryForInt(String sql) throws SQLException;
    /**查询一条记录返回其int形式的数据*/
    public int queryForInt(String sql, Object... args) throws SQLException;
    /**查询一条记录返回其long形式的数据*/
    public long queryForLong(String sql) throws SQLException;
    /**查询一条记录返回其long形式的数据*/
    public long queryForLong(String sql, Object... args) throws SQLException;
    /**查询一条记录返回其Object形式的数据*/
    public <T> T queryForObject(String sql, Class<T> requiredType) throws SQLException;
    /**查询一条记录返回其Object形式的数据*/
    public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) throws SQLException;
}