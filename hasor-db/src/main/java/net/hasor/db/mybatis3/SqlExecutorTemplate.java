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
import net.hasor.db.jdbc.ConnectionCallback;
import net.hasor.db.jdbc.core.JdbcTemplate;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
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

    public SqlSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T> T execute(final SqlSessionCallback<T> sessionCallback) throws SQLException {
        Objects.requireNonNull(sessionCallback, "Callback object must not be null");
        ConnectionCallback<T> callBack = con -> {
            try (SqlSession sqlSession = sessionFactory.openSession(con)) {
                return sessionCallback.doSqlSession(sqlSession);
            }
        };
        return this.execute(callBack);
    }
}