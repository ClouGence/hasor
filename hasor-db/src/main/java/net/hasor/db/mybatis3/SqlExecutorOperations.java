/*
 * Copyright 2008-2009 the original author or authors.
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
package net.hasor.db.mybatis3;
import net.hasor.db.jdbc.JdbcOperations;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @version : 2015年5月27日
 * @author 赵永春 (zyc@hasor.net)
 */
public interface SqlExecutorOperations extends JdbcOperations {
    /**
     * 查询单个数据的方法。 与mybatis中的用法一致。
     * @param sessionCallback 创建一个{@link SqlSession}用来执行{@link SqlSessionCallback}。
     */
    public <T> T execute(final SqlSessionCallback<T> sessionCallback) throws SQLException;

    /**
     * Retrieve a single row mapped from the statement key
     * @param <T> the returned object type
     * @param statement
     * @return Mapped object
     */
    public default <T> T selectOne(final String statement) throws SQLException {
        return this.execute((SqlSessionCallback<T>) sqlSession -> sqlSession.selectOne(statement));
    }

    /**
     * Retrieve a single row mapped from the statement key and parameter.
     * @param <T> the returned object type
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @return Mapped object
     */
    public default <T> T selectOne(final String statement, final Object parameter) throws SQLException {
        return this.execute((SqlSessionCallback<T>) sqlSession -> sqlSession.selectOne(statement, parameter));
    }

    /**
     * Retrieve a list of mapped objects from the statement key and parameter.
     * @param <E> the returned list element type
     * @param statement Unique identifier matching the statement to use.
     * @return List of mapped object
     */
    public default <E> List<E> selectList(final String statement) throws SQLException {
        return this.execute((SqlSessionCallback<List<E>>) sqlSession -> sqlSession.selectList(statement));
    }

    /**
     * Retrieve a list of mapped objects from the statement key and parameter.
     * @param <E> the returned list element type
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @return List of mapped object
     */
    public default <E> List<E> selectList(final String statement, final Object parameter) throws SQLException {
        return this.execute((SqlSessionCallback<List<E>>) sqlSession -> sqlSession.selectList(statement, parameter));
    }

    /**
     * Retrieve a list of mapped objects from the statement key and parameter, within the specified row bounds.
     * @param <E> the returned list element type
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @param rowBounds Bounds to limit object retrieval
     * @return List of mapped object
     */
    public default <E> List<E> selectList(final String statement, final Object parameter, final RowBounds rowBounds) throws SQLException {
        return this.execute((SqlSessionCallback<List<E>>) sqlSession -> sqlSession.selectList(statement, parameter, rowBounds));
    }

    /**
     * The selectMap is a special case in that it is designed to convert a list of results into a Map based on one of the properties in the resulting objects. Eg. Return a of Map[Integer,Author] for selectMap("selectAuthors","id")
     * @param <K> the returned Map keys type
     * @param <V> the returned Map values type
     * @param statement Unique identifier matching the statement to use.
     * @param mapKey The property to use as key for each value in the list.
     * @return Map containing key pair data.
     */
    public default <K, V> Map<K, V> selectMap(final String statement, final String mapKey) throws SQLException {
        return this.execute((SqlSessionCallback<Map<K, V>>) sqlSession -> sqlSession.selectMap(statement, mapKey));
    }

    /**
     * The selectMap is a special case in that it is designed to convert a list of results into a Map based on one of the properties in the resulting objects.
     * @param <K> the returned Map keys type
     * @param <V> the returned Map values type
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @param mapKey The property to use as key for each value in the list.
     * @return Map containing key pair data.
     */
    public default <K, V> Map<K, V> selectMap(final String statement, final Object parameter, final String mapKey) throws SQLException {
        return this.execute((SqlSessionCallback<Map<K, V>>) sqlSession -> sqlSession.selectMap(statement, parameter, mapKey));
    }

    /**
     * The selectMap is a special case in that it is designed to convert a list of results into a Map based on one of the properties in the resulting objects.
     * @param <K> the returned Map keys type
     * @param <V> the returned Map values type
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @param mapKey The property to use as key for each value in the list.
     * @param rowBounds Bounds to limit object retrieval
     * @return Map containing key pair data.
     */
    public default <K, V> Map<K, V> selectMap(final String statement, final Object parameter, final String mapKey, final RowBounds rowBounds) throws SQLException {
        return this.execute((SqlSessionCallback<Map<K, V>>) sqlSession -> sqlSession.selectMap(statement, parameter, mapKey, rowBounds));
    }

    /**
     * Execute an insert statement.
     * @param statement Unique identifier matching the statement to execute.
     * @return int The number of rows affected by the insert.
     */
    public default int insertStatement(final String statement) throws SQLException {
        return this.execute((SqlSessionCallback<Integer>) sqlSession -> sqlSession.insert(statement));
    }

    /**
     * Execute an insert statement with the given parameter object. Any generated autoincrement values or selectKey entries will modify the given parameter object properties. Only the number of rows affected will be returned.
     * @param statement Unique identifier matching the statement to execute.
     * @param parameter A parameter object to pass to the statement.
     * @return int The number of rows affected by the insert.
     */
    public default int insertStatement(final String statement, final Object parameter) throws SQLException {
        return this.execute((SqlSessionCallback<Integer>) sqlSession -> sqlSession.insert(statement, parameter));
    }

    /**
     * Execute an update statement. The number of rows affected will be returned.
     * @param statement Unique identifier matching the statement to execute.
     * @return int The number of rows affected by the update.
     */
    public default int updateStatement(final String statement) throws SQLException {
        return this.execute((SqlSessionCallback<Integer>) sqlSession -> sqlSession.update(statement));
    }

    /**
     * Execute an update statement. The number of rows affected will be returned.
     * @param statement Unique identifier matching the statement to execute.
     * @param parameter A parameter object to pass to the statement.
     * @return int The number of rows affected by the update.
     */
    public default int updateStatement(final String statement, final Object parameter) throws SQLException {
        return this.execute((SqlSessionCallback<Integer>) sqlSession -> sqlSession.update(statement, parameter));
    }

    /**
     * Execute a delete statement. The number of rows affected will be returned.
     * @param statement Unique identifier matching the statement to execute.
     * @return int The number of rows affected by the delete.
     */
    public default int deleteStatement(final String statement) throws SQLException {
        return this.execute((SqlSessionCallback<Integer>) sqlSession -> sqlSession.delete(statement));
    }

    /**
     * Execute a delete statement. The number of rows affected will be returned.
     * @param statement Unique identifier matching the statement to execute.
     * @param parameter A parameter object to pass to the statement.
     * @return int The number of rows affected by the delete.
     */
    public default int deleteStatement(final String statement, final Object parameter) throws SQLException {
        return this.execute((SqlSessionCallback<Integer>) sqlSession -> sqlSession.delete(statement, parameter));
    }
}