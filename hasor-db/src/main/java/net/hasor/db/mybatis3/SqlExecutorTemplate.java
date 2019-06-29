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
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.ConnectionCallback;
import net.hasor.db.jdbc.core.JdbcTemplate;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/**
 * @version : 2015年5月27日
 * @author 赵永春 (zyc@hasor.net)
 */
public class SqlExecutorTemplate extends JdbcTemplate implements SqlExecutorOperations {
    private SqlSessionFactory sessionFactory;
    /**
     * Construct a new SqlExecutorTemplate for bean usage.
     * <p>
     * Note: The DataSource has to be set before using the instance.
     *
     * @see #setDataSource
     */
    public SqlExecutorTemplate(SqlSessionFactory sessionFactory) {
        super();
        this.sessionFactory = sessionFactory;
    }
    /**
     * Construct a new SqlExecutorTemplate, given a DataSource to obtain connections from.
     * <p>
     * Note: This will not trigger initialization of the exception translator.
     *
     * @param dataSource the JDBC DataSource to obtain connections from
     */
    public SqlExecutorTemplate(SqlSessionFactory sessionFactory, final DataSource dataSource) {
        super(dataSource);
        this.sessionFactory = sessionFactory;
    }
    /**
     * Construct a new SqlExecutorTemplate, given a DataSource to obtain connections from.
     * <p>
     * Note: This will not trigger initialization of the exception translator.
     */
    public SqlExecutorTemplate(SqlSessionFactory sessionFactory, final Connection conn) {
        super(conn);
        this.sessionFactory = sessionFactory;
    }
    //
    //
    //
    public SqlSessionFactory getSessionFactory() {
        return sessionFactory;
    }
    public void setSessionFactory(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    //
    //
    //
    public <T> T execute(final SqlSessionCallback<T> sessionCallback) throws SQLException {
        Objects.requireNonNull(sessionCallback, "Callback object must not be null");
        //
        ConnectionCallback<T> callBack = new ConnectionCallback<T>() {
            public T doInConnection(Connection con) throws SQLException {
                SqlSession sqlSession = sessionFactory.openSession(con);
                try {
                    T result = sessionCallback.doSqlSession(sqlSession);
                    return result;
                } finally {
                    if (sqlSession != null) {
                        sqlSession.close();
                    }
                }
            }
        };
        return this.execute(callBack);
    }
    //
    public <T> T selectOne(final String statement) throws SQLException {
        return this.execute(new SqlSessionCallback<T>() {
            public T doSqlSession(SqlSession sqlSession) {
                return sqlSession.selectOne(statement);
            }
        });
    }
    public <T> T selectOne(final String statement, final Object parameter) throws SQLException {
        return this.execute(new SqlSessionCallback<T>() {
            public T doSqlSession(SqlSession sqlSession) {
                return sqlSession.selectOne(statement, parameter);
            }
        });
    }
    public <E> List<E> selectList(final String statement) throws SQLException {
        return this.execute(new SqlSessionCallback<List<E>>() {
            public List<E> doSqlSession(SqlSession sqlSession) {
                return sqlSession.selectList(statement);
            }
        });
    }
    public <E> List<E> selectList(final String statement, final Object parameter) throws SQLException {
        return this.execute(new SqlSessionCallback<List<E>>() {
            public List<E> doSqlSession(SqlSession sqlSession) {
                return sqlSession.selectList(statement, parameter);
            }
        });
    }
    public <E> List<E> selectList(final String statement, final Object parameter, final RowBounds rowBounds) throws SQLException {
        return this.execute(new SqlSessionCallback<List<E>>() {
            public List<E> doSqlSession(SqlSession sqlSession) {
                return sqlSession.selectList(statement, parameter, rowBounds);
            }
        });
    }
    public <K, V> Map<K, V> selectMap(final String statement, final String mapKey) throws SQLException {
        return this.execute(new SqlSessionCallback<Map<K, V>>() {
            public Map<K, V> doSqlSession(SqlSession sqlSession) {
                return sqlSession.selectMap(statement, mapKey);
            }
        });
    }
    public <K, V> Map<K, V> selectMap(final String statement, final Object parameter, final String mapKey) throws SQLException {
        return this.execute(new SqlSessionCallback<Map<K, V>>() {
            public Map<K, V> doSqlSession(SqlSession sqlSession) {
                return sqlSession.selectMap(statement, parameter, mapKey);
            }
        });
    }
    public <K, V> Map<K, V> selectMap(final String statement, final Object parameter, final String mapKey, final RowBounds rowBounds) throws SQLException {
        return this.execute(new SqlSessionCallback<Map<K, V>>() {
            public Map<K, V> doSqlSession(SqlSession sqlSession) {
                return sqlSession.selectMap(statement, parameter, mapKey, rowBounds);
            }
        });
    }
    public int insertStatement(final String statement) throws SQLException {
        return this.execute(new SqlSessionCallback<Integer>() {
            public Integer doSqlSession(SqlSession sqlSession) {
                return sqlSession.insert(statement);
            }
        });
    }
    public int insertStatement(final String statement, final Object parameter) throws SQLException {
        return this.execute(new SqlSessionCallback<Integer>() {
            public Integer doSqlSession(SqlSession sqlSession) {
                return sqlSession.insert(statement, parameter);
            }
        });
    }
    public int updateStatement(final String statement) throws SQLException {
        return this.execute(new SqlSessionCallback<Integer>() {
            public Integer doSqlSession(SqlSession sqlSession) {
                return sqlSession.update(statement);
            }
        });
    }
    public int updateStatement(final String statement, final Object parameter) throws SQLException {
        return this.execute(new SqlSessionCallback<Integer>() {
            public Integer doSqlSession(SqlSession sqlSession) {
                return sqlSession.update(statement, parameter);
            }
        });
    }
    public int deleteStatement(final String statement) throws SQLException {
        return this.execute(new SqlSessionCallback<Integer>() {
            public Integer doSqlSession(SqlSession sqlSession) {
                return sqlSession.delete(statement);
            }
        });
    }
    public int deleteStatement(final String statement, final Object parameter) throws SQLException {
        return this.execute(new SqlSessionCallback<Integer>() {
            public Integer doSqlSession(SqlSession sqlSession) {
                return sqlSession.delete(statement, parameter);
            }
        });
    }
}