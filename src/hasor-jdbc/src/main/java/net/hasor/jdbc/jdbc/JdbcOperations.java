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
package net.hasor.jdbc.jdbc;
import java.util.List;
import java.util.Map;
import net.hasor.jdbc.dao.DataAccessException;
import net.hasor.jdbc.jdbc.parameter.SqlParameter;
import net.hasor.jdbc.jdbc.rowset.SqlRowSet;
/**
 * 该接口声明了一些 JDBC 基本操作。
 * @version : 2013-10-9
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @author 赵永春(zyc@hasor.net)
 */
public interface JdbcOperations {
    /**通过回调函数执行一个JDBC数据访问操作。 */
    public <T> T execute(ConnectionCallback<T> action) throws DataAccessException;
    /**通过回调函数执行一个JDBC数据访问操作。 */
    public <T> T execute(StatementCallback<T> action) throws DataAccessException;
    /**执行一个 SQL语句，通常是一个 DDL 语句. */
    public void execute(String sql) throws DataAccessException;
    /**执行一个静态 SQL 语句。并通过 ResultSetExtractor 转换结果集。*/
    public <T> T query(String sql, ResultSetExtractor<T> rse) throws DataAccessException;
    /**执行一个静态 SQL 语句。并通过 RowCallbackHandler 处理结果集。*/
    public void query(String sql, RowCallbackHandler rch) throws DataAccessException;
    /**执行一个静态 SQL 语句，并使用 RowMapper 处理结果集。*/
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws DataAccessException;
    /**执行一个静态 SQL 语句，并使用 RowMapper 处理结果集。
     * 预计该方法只会处理一条数据，如果查询结果存在多条数据会引发异常。
     * @return 当不存在记录时返回<code>null</code>。
     */
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper) throws DataAccessException;
    /**执行一个静态 SQL 语句，并将结果集数据转换成<code>requiredType</code>参数指定的类型对象。
     * 预计该方法只会处理一条数据，如果查询结果存在多条数据会引发异常。
     * @return 当不存在记录时返回<code>null</code>。
     */
    public <T> T queryForObject(String sql, Class<T> requiredType) throws DataAccessException;
    /**执行一个静态 SQL 语句，并将结果集数据转换成<code>Map</code>。
     * 预计该方法只会处理一条数据，如果查询结果存在多条数据会引发异常。
     * @return 当不存在记录时返回<code>null</code>。
     */
    public Map<String, Object> queryForMap(String sql) throws DataAccessException;
    /**执行一个静态 SQL 语句，并取得 long 类型数据。
     * 预计该方法只会处理一条数据，如果查询结果存在多条数据或者存在多列内容会引发异常。
     * @return the long value, or 0 in case of SQL NULL
     */
    public long queryForLong(String sql) throws DataAccessException;
    /**执行一个静态 SQL 语句，并取得 int 类型数据。
     * 预计该方法只会处理一条数据，如果查询结果存在多条数据或者存在多列内容会引发异常。
     * @return the int value, or 0 in case of SQL NULL
     */
    public int queryForInt(String sql) throws DataAccessException;
    /**执行一个静态 SQL 语句，结果将被映射到一个列表(一个条目为每一行)的对象，
     * 列表中每一条记录都是<code>elementType</code>参数指定的类型对象。*/
    public <T> List<T> queryForList(String sql, Class<T> elementType) throws DataAccessException;
    /**执行一个静态 SQL 语句，结果将被映射到一个列表(一个条目为每一行)的对象，
     * 列表中每一条记录都是<code>Map</code>类型对象。*/
    public List<Map<String, Object>> queryForList(String sql) throws DataAccessException;
    /**执行一个静态 SQL 语句，查询结果使用 SqlRowSet 接口封装。*/
    public SqlRowSet queryForRowSet(String sql) throws DataAccessException;
    /**执行一条 insert 或 update、delete 语句，返回值用于表示受影响的行数。*/
    public int update(String sql) throws DataAccessException;
    /**批量执行 insert 或 update、delete 语句，返回值用于表示受影响的行数。*/
    public int[] batchUpdate(String[] sql) throws DataAccessException;
    //-------------------------------------------------------------------------
    // Methods dealing with prepared statements
    //-------------------------------------------------------------------------
    /**执行一个 JDBC 操作。这个 JDBC 调用操作将会使用 PreparedStatement 接口执行。*/
    public <T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action) throws DataAccessException;
    /**执行一个动态 SQL 语句。SQL 语句会被编译成 PreparedStatement 类型通过回调接口 PreparedStatementCallback 执行。*/
    public <T> T execute(String sql, PreparedStatementCallback<T> action) throws DataAccessException;
    /**执行一个动态查询 SQL 语句。SQL 语句会被编译成 PreparedStatement 类型通过回调接口 PreparedStatementCallback 执行。
     * 返回的结果集使用 ResultSetExtractor 转换。*/
    public <T> T query(PreparedStatementCreator psc, ResultSetExtractor<T> rse) throws DataAccessException;
    /**执行一个动态查询 SQL 语句。SQL 语句会被编译成 PreparedStatement 类型通过回调接口 PreparedStatementSetter 为动态 SQL 设置属性。
     * 返回的结果集使用 ResultSetExtractor 转换。*/
    public <T> T query(String sql, PreparedStatementSetter pss, ResultSetExtractor<T> rse) throws DataAccessException;
    /**执行一个动态查询 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。
     * SQL 参数类型通过 argTypes 参数给定。返回的结果集使用 ResultSetExtractor 转换。
     * @param sql SQL query to execute
     * @param args arguments to bind to the query
     * @param argTypes SQL types of the arguments (constants from <code>java.sql.Types</code>)
     * @param rse object that will extract results
     * @see java.sql.Types
     */
    public <T> T query(String sql, Object[] args, int[] argTypes, ResultSetExtractor<T> rse) throws DataAccessException;
    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作，并且将 SQL 查询结果集使用 ResultSetExtractor 转换。*/
    public <T> T query(String sql, Object[] args, ResultSetExtractor<T> rse) throws DataAccessException;
    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作，并且将 SQL 查询结果集使用 ResultSetExtractor 转换。*/
    public <T> T query(String sql, ResultSetExtractor<T> rse, Object... args) throws DataAccessException;
    /**
     * Query using a prepared statement, reading the ResultSet on a per-row basis with a RowCallbackHandler.
     * <p>A PreparedStatementCreator can either be implemented directly or configured through a PreparedStatementCreatorFactory.
     * @param psc object that can create a PreparedStatement given a Connection
     * @param rch object that will extract results, one row at a time
     */
    public void query(PreparedStatementCreator psc, RowCallbackHandler rch) throws DataAccessException;
    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作，并且结果集行处理使用 RowCallbackHandler 接口处理。*/
    public void query(String sql, PreparedStatementSetter pss, RowCallbackHandler rch) throws DataAccessException;
    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作，并且结果集行处理使用 RowCallbackHandler 接口处理。
     * @see java.sql.Types*/
    public void query(String sql, Object[] args, int[] argTypes, RowCallbackHandler rch) throws DataAccessException;
    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作，并且结果集行处理使用 RowCallbackHandler 接口处理。*/
    public void query(String sql, Object[] args, RowCallbackHandler rch) throws DataAccessException;
    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作，并且结果集行处理使用 RowCallbackHandler 接口处理。*/
    public void query(String sql, RowCallbackHandler rch, Object... args) throws DataAccessException;
    /**
     * Query using a prepared statement, mapping each row to a Java object via a RowMapper.
     * <p>A PreparedStatementCreator can either be implemented directly or
     * configured through a PreparedStatementCreatorFactory.
     * @param psc object that can create a PreparedStatement given a Connection
     * @param rowMapper object that will map one object per row
     * @return the result List, containing mapped objects
     */
    public <T> List<T> query(PreparedStatementCreator psc, RowMapper<T> rowMapper) throws DataAccessException;
    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将返回一个 List，每一行将通过 RowMapper 映射。*/
    public <T> List<T> query(String sql, PreparedStatementSetter pss, RowMapper<T> rowMapper) throws DataAccessException;
    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将返回一个 List，每一行将通过 RowMapper 映射。*/
    public <T> List<T> query(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException;
    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将返回一个 List，每一行将通过 RowMapper 映射。*/
    public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException;
    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将返回一个 List，每一行将通过 RowMapper 映射。*/
    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException;
    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将通过 RowMapper 映射转换并返回。
     * <p>请确保查询结果只有一条记录，否则会引发异常。
     * @throws DataAccessException if the query fails
     */
    public <T> T queryForObject(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException;
    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将通过 RowMapper 映射转换并返回。
     * <p>请确保查询结果只有一条记录，否则会引发异常。
     * @throws DataAccessException if the query fails
     */
    public <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException;
    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将通过 RowMapper 映射转换并返回。
     * <p>请确保查询结果只有一条记录，否则会引发异常。
     * @throws DataAccessException if the query fails
     */
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException;
    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将通过 requiredType 参数所表示的类型封装。
     * <p>请确保查询结果只有一条记录，否则会引发异常。
     * @throws DataAccessException if the query fails
     * @see java.sql.Types*/
    public <T> T queryForObject(String sql, Object[] args, int[] argTypes, Class<T> requiredType) throws DataAccessException;
    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将通过 requiredType 参数所表示的类型封装。
     * <p>请确保查询结果只有一条记录，否则会引发异常。
     * @throws DataAccessException if the query fails
     */
    public <T> T queryForObject(String sql, Object[] args, Class<T> requiredType) throws DataAccessException;
    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将通过 requiredType 参数所表示的类型封装。
     * <p>请确保查询结果只有一条记录，否则会引发异常。
     * @throws DataAccessException if the query fails
     */
    public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) throws DataAccessException;
    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将使用 Map 封装。
     * <p>请确保查询结果只有一条记录，否则会引发异常。
     * @throws DataAccessException if the query fails
     * @see java.sql.Types*/
    public Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes) throws DataAccessException;
    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将使用 Map 封装。
     * <p>请确保查询结果只有一条记录，否则会引发异常。
     * @throws DataAccessException if the query fails
     */
    public Map<String, Object> queryForMap(String sql, Object... args) throws DataAccessException;
    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将转换成 long 类型。
     * 所以需要保证查询的结果只有一行一列，否则执行会引发异常。
     * @throws DataAccessException if the query fails
     * @see java.sql.Types*/
    public long queryForLong(String sql, Object[] args, int[] argTypes) throws DataAccessException;
    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将转换成 long 类型。
     * 所以需要保证查询的结果只有一行一列，否则执行会引发异常。
     * @throws DataAccessException if the query fails
     */
    public long queryForLong(String sql, Object... args) throws DataAccessException;
    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将转换成 int 类型。
     * 所以需要保证查询的结果只有一行一列，否则执行会引发异常。
     * @throws DataAccessException if the query fails
     * @see java.sql.Types*/
    public int queryForInt(String sql, Object[] args, int[] argTypes) throws DataAccessException;
    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将转换成 long 类型。
     * 所以需要保证查询的结果只有一行一列，否则执行会引发异常。
     * @throws DataAccessException if the query fails
     */
    public int queryForInt(String sql, Object... args) throws DataAccessException;
    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将转换成 elementType 参数所表示的类型。
     * @throws DataAccessException if the query fails
     * @see java.sql.Types*/
    public <T> List<T> queryForList(String sql, Object[] args, int[] argTypes, Class<T> elementType) throws DataAccessException;
    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将转换成 elementType 参数所表示的类型。
     * @throws DataAccessException if the query fails
     */
    public <T> List<T> queryForList(String sql, Object[] args, Class<T> elementType) throws DataAccessException;
    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将转换成 elementType 参数所表示的类型。
     * @throws DataAccessException if the query fails
     */
    public <T> List<T> queryForList(String sql, Class<T> elementType, Object... args) throws DataAccessException;
    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将转换成 Map 类型。
     * @throws DataAccessException if the query fails
     * @see java.sql.Types*/
    public List<Map<String, Object>> queryForList(String sql, Object[] args, int[] argTypes) throws DataAccessException;
    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询记录将会使用 Map 保存，并封装到 List 中。*/
    public List<Map<String, Object>> queryForList(String sql, Object... args) throws DataAccessException;
    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果使用 SqlRowSet 接口封装。
     * @see java.sql.Types*/
    public SqlRowSet queryForRowSet(String sql, Object[] args, int[] argTypes) throws DataAccessException;
    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果使用 SqlRowSet 接口封装。*/
    public SqlRowSet queryForRowSet(String sql, Object... args) throws DataAccessException;
    /**执行一个更新语句（insert、update、delete），这个查询将会使用 PreparedStatement 接口操作。*/
    public int update(PreparedStatementCreator psc) throws DataAccessException;
    /**执行一个更新语句（insert、update、delete），这个查询将会使用 PreparedStatement 接口操作。*/
    public int update(String sql, PreparedStatementSetter pss) throws DataAccessException;
    /**执行一个更新语句（insert、update、delete），这个查询将会使用 PreparedStatement 接口操作。
     * @see java.sql.Types*/
    public int update(String sql, Object[] args, int[] argTypes) throws DataAccessException;
    /**执行一个更新语句（insert、update、delete），这个查询将会使用 PreparedStatement 接口操作。*/
    public int update(String sql, Object... args) throws DataAccessException;
    /**批量执行 SQL 语句，这一批次中的SQL 参数使用 BatchPreparedStatementSetter 接口设置。*/
    public int[] batchUpdate(String sql, BatchPreparedStatementSetter pss) throws DataAccessException;
    //-------------------------------------------------------------------------
    // Methods dealing with callable statements
    //-------------------------------------------------------------------------
    /**执行 JDBC（存储过程、函数）数据访问操作。*/
    public <T> T execute(CallableStatementCreator csc, CallableStatementCallback<T> action) throws DataAccessException;
    /**执行 JDBC（存储过程、函数）数据访问操作。*/
    public <T> T execute(String callString, CallableStatementCallback<T> action) throws DataAccessException;
    /**执行 JDBC（存储过程、函数）数据访问操作。*/
    public Map<String, Object> call(CallableStatementCreator csc, List<SqlParameter> declaredParameters) throws DataAccessException;
}