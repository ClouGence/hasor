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
package net.hasor.db.jdbc;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 该接口声明了一些 JDBC 基本操作。
 * @version : 2013-10-9
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @author 赵永春 (zyc@hasor.net)
 */
public interface JdbcOperations {
    /**通过回调函数执行一个JDBC数据访问操作。 */
    public <T> T execute(ConnectionCallback<T> action) throws SQLException;

    /**通过回调函数执行一个JDBC数据访问操作。 */
    public <T> T execute(StatementCallback<T> action) throws SQLException;

    /**执行 JDBC（存储过程、函数）数据访问操作。
     * <p>CallableStatementCreator 接口或者 CallableStatementCallback 接口 对象需要对存储过程的传入参数进行设置。*/
    public <T> T execute(CallableStatementCreator csc, CallableStatementCallback<T> action) throws SQLException;

    /**执行 JDBC（存储过程、函数）数据访问操作。SQL 语句会被编译成 PreparedStatement 类型通过回调接口 CallableStatementCallback 执行。*/
    public <T> T execute(String callString, CallableStatementCallback<T> action) throws SQLException;

    /**执行一个 JDBC 操作。这个 JDBC 调用操作将会使用 PreparedStatement 接口执行。*/
    public <T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action) throws SQLException;

    /**执行一个动态 SQL 语句。SQL 语句会被编译成 PreparedStatement 类型通过回调接口 PreparedStatementCallback 执行。*/
    public <T> T execute(String sql, PreparedStatementCallback<T> action) throws SQLException;

    /**执行一个 JDBC 操作。这个 JDBC 调用操作将会使用 PreparedStatement 接口执行。*/
    public <T> T execute(String sql, SqlParameterSource paramSource, PreparedStatementCallback<T> action) throws SQLException;

    /**执行一个动态 SQL 语句。SQL 语句会被编译成 PreparedStatement 类型通过回调接口 PreparedStatementCallback 执行。*/
    public <T> T execute(String sql, Map<String, ?> paramMap, PreparedStatementCallback<T> action) throws SQLException;

    /**执行一个 SQL语句，通常是一个 DDL 语句.*/
    public boolean execute(String sql) throws SQLException;

    /**执行一个动态查询 SQL 语句。SQL 语句会被编译成 PreparedStatement 类型通过回调接口 PreparedStatementCallback 执行。
     * 返回的结果集使用 ResultSetExtractor 转换。*/
    public <T> T query(PreparedStatementCreator psc, ResultSetExtractor<T> rse) throws SQLException;

    /**执行一个静态 SQL 语句。并通过 ResultSetExtractor 转换结果集。*/
    public <T> T query(String sql, ResultSetExtractor<T> rse) throws SQLException;

    /**执行一个动态查询 SQL 语句。SQL 语句会被编译成 PreparedStatement 类型通过回调接口 PreparedStatementSetter 为动态 SQL 设置属性。返回的结果集使用 ResultSetExtractor 转换。 */
    public <T> T query(String sql, PreparedStatementSetter pss, ResultSetExtractor<T> rse) throws SQLException;

    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作，并且将 SQL 查询结果集使用 ResultSetExtractor 转换。*/
    public <T> T query(String sql, ResultSetExtractor<T> rse, Object... args) throws SQLException;

    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作，并且将 SQL 查询结果集使用 ResultSetExtractor 转换。*/
    public <T> T query(String sql, Object[] arg, ResultSetExtractor<T> rses) throws SQLException;

    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作，并且将 SQL 查询结果集使用 ResultSetExtractor 转换。*/
    public <T> T query(String sql, SqlParameterSource paramSource, ResultSetExtractor<T> rse) throws SQLException;

    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作，并且将 SQL 查询结果集使用 ResultSetExtractor 转换。*/
    public <T> T query(String sql, Map<String, ?> paramMap, ResultSetExtractor<T> rse) throws SQLException;

    /**
     * Query using a prepared statement, reading the ResultSet on a per-row basis with a RowCallbackHandler.
     * <p>A PreparedStatementCreator can either be implemented directly or configured through a PreparedStatementCreatorFactory.
     * @param psc object that can create a PreparedStatement given a Connection
     * @param rch object that will extract results, one row at a time
     */
    public void query(PreparedStatementCreator psc, RowCallbackHandler rch) throws SQLException;

    /**执行一个静态 SQL 语句。并通过 RowCallbackHandler 处理结果集。*/
    public void query(String sql, RowCallbackHandler rch) throws SQLException;

    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作，并且结果集行处理使用 RowCallbackHandler 接口处理。*/
    public void query(String sql, PreparedStatementSetter pss, RowCallbackHandler rch) throws SQLException;

    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作，并且结果集行处理使用 RowCallbackHandler 接口处理。*/
    public void query(String sql, RowCallbackHandler rch, Object... args) throws SQLException;

    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作，并且结果集行处理使用 RowCallbackHandler 接口处理。*/
    public void query(String sql, Object[] args, RowCallbackHandler rch) throws SQLException;

    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作，并且结果集行处理使用 RowCallbackHandler 接口处理。*/
    public void query(String sql, SqlParameterSource paramSource, RowCallbackHandler rch) throws SQLException;

    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作，并且结果集行处理使用 RowCallbackHandler 接口处理。*/
    public void query(String sql, Map<String, ?> paramMap, RowCallbackHandler rch) throws SQLException;

    /**
     * Query using a prepared statement, mapping each row to a Java object via a RowMapper.
     * <p>A PreparedStatementCreator can either be implemented directly or
     * configured through a PreparedStatementCreatorFactory.
     * @param psc object that can create a PreparedStatement given a Connection
     * @param rowMapper object that will map one object per row
     * @return the result List, containing mapped objects
     */
    public <T> List<T> query(PreparedStatementCreator psc, RowMapper<T> rowMapper) throws SQLException;

    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将返回一个 List，每一行将通过 RowMapper 映射。*/
    public <T> List<T> query(String sql, PreparedStatementSetter pss, RowMapper<T> rowMapper) throws SQLException;

    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将返回一个 List，每一行将通过 RowMapper 映射。*/
    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) throws SQLException;

    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将返回一个 List，每一行将通过 RowMapper 映射。*/
    public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) throws SQLException;

    /**执行一个静态 SQL 语句，并使用 RowMapper 处理结果集。*/
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws SQLException;

    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将返回一个 List，每一行将通过 RowMapper 映射。*/
    public <T> List<T> query(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper) throws SQLException;

    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将返回一个 List，每一行将通过 RowMapper 映射。*/
    public <T> List<T> query(String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper) throws SQLException;

    /**执行一个静态 SQL 语句，结果将被映射到一个列表(一个条目为每一行)的对象，列表中每一条记录都是<code>elementType</code>参数指定的类型对象。*/
    public <T> List<T> queryForList(String sql, Class<T> elementType) throws SQLException;

    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将转换成 elementType 参数所表示的类型。
     * @throws SQLException if the query fails
     */
    public <T> List<T> queryForList(String sql, Class<T> elementType, Object... args) throws SQLException;

    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将转换成 elementType 参数所表示的类型。
     * @throws SQLException if the query fails
     */
    public <T> List<T> queryForList(String sql, Object[] args, Class<T> elementType) throws SQLException;

    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将转换成 elementType 参数所表示的类型。
     * @throws SQLException if the query fails
     */
    public <T> List<T> queryForList(String sql, SqlParameterSource paramSource, Class<T> elementType) throws SQLException;

    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将转换成 elementType 参数所表示的类型。
     * @throws SQLException if the query fails
     */
    public <T> List<T> queryForList(String sql, Map<String, ?> paramMap, Class<T> elementType) throws SQLException;

    /**执行一个静态 SQL 语句，并使用 RowMapper 处理结果集。
     * 预计该方法只会处理一条数据，如果查询结果存在多条数据将取第一条记录作为结果。
     * @return 当不存在记录时返回<code>null</code>。
     */
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper) throws SQLException;

    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将通过 RowMapper 映射转换并返回。
     * <p>预计该方法只会处理一条数据，如果查询结果存在多条数据将会取得第一条数据作为结果。
     * @throws SQLException if the query fails
     */
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) throws SQLException;

    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将通过 RowMapper 映射转换并返回。
     * <p>预计该方法只会处理一条数据，如果查询结果存在多条数据将会取得第一条数据作为结果。
     * @throws SQLException if the query fails
     */
    public <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) throws SQLException;

    /**
     * 查询一个 SQL 语句，查询参数使用 SqlParameterSource 封装。
     * <p>预计该方法只会处理一条数据，如果查询结果存在多条数据将会取得第一条数据作为结果。
     * @throws SQLException if the query fails
     */
    public <T> T queryForObject(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper) throws SQLException;

    /**
     * 查询一个 SQL 语句，查询参数使用 Map 封装。
     * <p>预计该方法只会处理一条数据，如果查询结果存在多条数据将会取得第一条数据作为结果。
     * @throws SQLException if the query fails
     */
    public <T> T queryForObject(String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper) throws SQLException;

    /**执行一个静态 SQL 语句，并将结果集数据转换成<code>requiredType</code>参数指定的类型对象。
     * 预计该方法只会处理一条数据，如果查询结果存在多条数据将取第一条记录作为结果。
     * @return 当不存在记录时返回<code>null</code>。
     */
    public <T> T queryForObject(String sql, Class<T> requiredType) throws SQLException;

    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将通过 requiredType 参数所表示的类型封装。
     * <p>预计该方法只会处理一条数据，如果查询结果存在多条数据将会取得第一条数据作为结果。
     * @throws SQLException if the query fails
     */
    public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) throws SQLException;

    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将通过 requiredType 参数所表示的类型封装。
     * <p>预计该方法只会处理一条数据，如果查询结果存在多条数据将会取得第一条数据作为结果。
     * @throws SQLException if the query fails
     */
    public <T> T queryForObject(String sql, Object[] args, Class<T> requiredType) throws SQLException;

    /**
     * 查询一个 SQL 语句，查询参数使用 SqlParameterSource 封装，并将查询结果使用 requiredType 参数表示的类型返回。
     * <p>预计该方法只会处理一条数据，如果查询结果存在多条数据将会取得第一条数据作为结果。
     * @throws SQLException if the query fails
     */
    public <T> T queryForObject(String sql, SqlParameterSource paramSource, Class<T> requiredType) throws SQLException;

    /**
     * 查询一个 SQL 语句，查询参数使用 Map 封装，并将查询结果使用 requiredType 参数表示的类型返回。
     * <p>预计该方法只会处理一条数据，如果查询结果存在多条数据将会取得第一条数据作为结果。
     * @throws SQLException if the query fails
     */
    public <T> T queryForObject(String sql, Map<String, ?> paramMap, Class<T> requiredType) throws SQLException;

    /**执行一个静态 SQL 语句，并将结果集数据转换成<code>Map</code>。
     * 预计该方法只会处理一条数据，如果查询结果存在多条数据将取第一条记录作为结果。
     * @return 当不存在记录时返回<code>null</code>。
     */
    public Map<String, Object> queryForMap(String sql) throws SQLException;

    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将使用 Map 封装。
     * <p>预计该方法只会处理一条数据，如果查询结果存在多条数据将会取得第一条数据作为结果。
     * @throws SQLException if the query fails
     */
    public Map<String, Object> queryForMap(String sql, Object... args) throws SQLException;

    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将使用 Map 封装。
     * <p>预计该方法只会处理一条数据，如果查询结果存在多条数据将会取得第一条数据作为结果。
     * @throws SQLException if the query fails
     */
    public Map<String, Object> queryForMap(String sql, SqlParameterSource paramSource) throws SQLException;

    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将使用 Map 封装。
     * <p>预计该方法只会处理一条数据，如果查询结果存在多条数据将会取得第一条数据作为结果。
     * @throws SQLException if the query fails
     */
    public Map<String, Object> queryForMap(String sql, Map<String, ?> paramMap) throws SQLException;

    /**执行一个静态 SQL 语句，并取得 long 类型数据。
     * 预计该方法只会处理一条数据，如果查询结果存在多条数据或者多列将会引发异常。
     * @return the long value, or 0 in case of SQL NULL
     */
    public long queryForLong(String sql) throws SQLException;

    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将转换成 long 类型。
     * 所以需要保证查询的结果只有一行一列，否则执行会引发异常。
     * @throws SQLException if the query fails
     */
    public long queryForLong(String sql, Object... args) throws SQLException;

    /**
     * 查询一个 SQL 语句，sql 参数通过 SqlParameterSource 封装。查询结果将转换成 long 类型。
     * 所以需要保证查询的结果只有一行一列，否则执行会引发异常。
     * @throws SQLException if the query fails
     */
    public long queryForLong(String sql, SqlParameterSource paramSource) throws SQLException;

    /**
     * 查询一个 SQL 语句，sql 参数通过 Map 封装。查询结果将转换成 long 类型。
     * 所以需要保证查询的结果只有一行一列，否则执行会引发异常。
     * @throws SQLException if the query fails
     */
    public long queryForLong(String sql, Map<String, ?> paramMap) throws SQLException;

    /**执行一个静态 SQL 语句，并取得 int 类型数据。
     * 预计该方法只会处理一条数据，如果查询结果存在多条数据或者多列将会引发异常。
     * @return the int value, or 0 in case of SQL NULL
     */
    public int queryForInt(String sql) throws SQLException;

    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将转换成 int 类型。
     * 所以需要保证查询的结果只有一行一列，否则执行会引发异常。
     * @throws SQLException if the query fails
     */
    public int queryForInt(String sql, Object... args) throws SQLException;

    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将转换成 int 类型。
     * 所以需要保证查询的结果只有一行一列，否则执行会引发异常。
     * @throws SQLException if the query fails
     */
    public int queryForInt(String sql, SqlParameterSource paramSource) throws SQLException;

    /**
     * 查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果将转换成 int 类型。
     * 所以需要保证查询的结果只有一行一列，否则执行会引发异常。
     * @throws SQLException if the query fails
     */
    public int queryForInt(String sql, Map<String, ?> paramMap) throws SQLException;

    /**执行一个静态 SQL 语句，结果将被映射到一个列表(一个条目为每一行)的对象，
     * 列表中每一条记录都是<code>Map</code>类型对象。*/
    public List<Map<String, Object>> queryForList(String sql) throws SQLException;

    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询记录将会使用 Map 保存，并封装到 List 中。*/
    public List<Map<String, Object>> queryForList(String sql, Object... args) throws SQLException;

    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询记录将会使用 Map 保存，并封装到 List 中。*/
    public List<Map<String, Object>> queryForList(String sql, SqlParameterSource paramSource) throws SQLException;

    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询记录将会使用 Map 保存，并封装到 List 中。*/
    public List<Map<String, Object>> queryForList(String sql, Map<String, ?> paramMap) throws SQLException;
    //    /**执行一个静态 SQL 语句，查询结果使用 SqlRowSet 接口封装。*/
    //    public SqlRowSet queryForRowSet(String sql) throws SQLException;
    //    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果使用 SqlRowSet 接口封装。
    //     * @see java.sql.Types*/
    //    public SqlRowSet queryForRowSet(String sql, Object... args) throws SQLException;
    //    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果使用 SqlRowSet 接口封装。
    //     * @see java.sql.Types*/
    //    public SqlRowSet queryForRowSet(String sql, Object[] args, int[] argTypes) throws SQLException;
    //    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果使用 SqlRowSet 接口封装。
    //     * @see java.sql.Types*/
    //    public SqlRowSet queryForRowSet(String sql, SqlParameterSource paramSource) throws SQLException;
    //    /**查询一个 SQL 语句，使用这个查询将会使用 PreparedStatement 接口操作。查询结果使用 SqlRowSet 接口封装。
    //     * @see java.sql.Types*/
    //    public SqlRowSet queryForRowSet(String sql, Map<String, ?> paramMap) throws SQLException;

    /**执行一个更新语句（insert、update、delete），这个查询将会使用 PreparedStatement 接口操作。*/
    public int executeUpdate(PreparedStatementCreator psc) throws SQLException;

    /**执行一条 insert 或 update、delete 语句，返回值用于表示受影响的行数。*/
    public int executeUpdate(String sql) throws SQLException;

    /**执行一个更新语句（insert、update、delete），这个查询将会使用 PreparedStatement 接口操作。*/
    public int executeUpdate(String sql, PreparedStatementSetter pss) throws SQLException;

    /**执行一个更新语句（insert、update、delete），这个查询将会使用 PreparedStatement 接口操作。*/
    public int executeUpdate(String sql, Object... args) throws SQLException;

    /**执行一个更新语句（insert、update、delete），这个查询将会使用 PreparedStatement 接口操作。*/
    public int executeUpdate(String sql, SqlParameterSource paramSource) throws SQLException;

    /**执行一个更新语句（insert、update、delete），这个查询将会使用 PreparedStatement 接口操作。*/
    public int executeUpdate(String sql, Map<String, ?> paramMap) throws SQLException;

    /**批量执行 insert 或 update、delete 语句，返回值用于表示受影响的行数。*/
    public int[] executeBatch(String[] sql) throws SQLException;

    /**批量执行 insert 或 update、delete 语句，这一批次中的SQL 参数使用 BatchPreparedStatementSetter 接口设置。*/
    public int[] executeBatch(String sql, BatchPreparedStatementSetter pss) throws SQLException;

    /**批量执行 insert 或 update、delete 语句，这一批次中的SQL 参数使用 BatchPreparedStatementSetter 接口设置。*/
    public int[] executeBatch(String sql, Map<String, ?>[] batchValues) throws SQLException;

    /**批量执行 insert 或 update、delete 语句，这一批次中的SQL 参数使用 BatchPreparedStatementSetter 接口设置。*/
    public int[] executeBatch(String sql, SqlParameterSource[] batchArgs) throws SQLException;
}