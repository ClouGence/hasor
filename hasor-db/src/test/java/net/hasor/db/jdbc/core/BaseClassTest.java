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
package net.hasor.db.jdbc.core;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.datasource.ConnectionProxy;
import net.hasor.db.jdbc.ConnectionCallback;
import net.hasor.db.jdbc.StatementCallback;
import net.hasor.db.lambda.mapping.MappingHandler;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/***
 *
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class BaseClassTest extends AbstractDbTest {
    @Test
    public void jdbcAccessorTest_1() {
        DataSource dataSource = PowerMockito.mock(DataSource.class);
        Connection connection = PowerMockito.mock(Connection.class);
        //
        JdbcAccessor jdbcTemplate = new JdbcAccessor();
        jdbcTemplate.setDataSource(dataSource);
        jdbcTemplate.setConnection(connection);
        assert jdbcTemplate.getConnection() == connection;
        assert jdbcTemplate.getDataSource() == dataSource;
    }

    @Test
    public void jdbcAccessorTest_2() {
        JdbcAccessor jdbcTemplate = new JdbcAccessor();
        jdbcTemplate.setAccessorApply(null);
        try {
            jdbcTemplate.applyConnection(null);
        } catch (Exception e) {
            assert e.getMessage().equals("accessorApply is null.");
        }
    }

    @Test
    public void jdbcAccessorTest_3() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        Function<DataSource, Connection> accessorApply = dataSource -> {
            atomicBoolean.set(true);
            return null;
        };
        //
        JdbcAccessor jdbcTemplate = new JdbcAccessor();
        jdbcTemplate.setAccessorApply(accessorApply);
        jdbcTemplate.getAccessorApply().apply(null);
        assert atomicBoolean.get();
    }

    @Test
    public void jdbcConnectionTest_1() {
        JdbcConnection jdbcTemplate = new JdbcConnection();
        jdbcTemplate.setFetchSize(123);
        assert jdbcTemplate.getFetchSize() == 123;
        jdbcTemplate.setMaxRows(321);
        assert jdbcTemplate.getMaxRows() == 321;
        jdbcTemplate.setQueryTimeout(11111);
        assert jdbcTemplate.getQueryTimeout() == 11111;
    }

    @Test
    public void jdbcConnectionTest_2() throws SQLException {
        Connection connection = PowerMockito.mock(Connection.class);
        JdbcConnection jdbcTemplate = new JdbcConnection(connection);
        jdbcTemplate.setIgnoreWarnings(false);
        //
        Statement statement = PowerMockito.mock(Statement.class);
        SQLWarning warning = new SQLWarning("abc");
        PowerMockito.when(statement.getWarnings()).thenReturn(warning);
        //
        try {
            jdbcTemplate.handleWarnings(statement);
            assert false;
        } catch (Exception e) {
            assert e instanceof SQLException;
            assert e.getMessage().equals("Warning not ignored");
            assert e.getCause() == warning;
        }
    }

    @Test
    public void jdbcConnectionTest_3() {
        try {
            JdbcConnection jdbcTemplate = new JdbcConnection();
            jdbcTemplate.execute((ConnectionCallback<Object>) con -> null);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("DataSource or Connection are not available.");
        }
    }

    @Test
    public void jdbcConnectionTest_4() {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            DataSource dataSource = appContext.getInstance(DataSource.class);
            //
            try {
                new JdbcConnection(dataSource).execute((ConnectionCallback<Object>) con -> {
                    con.createStatement().execute("xxxxx");
                    return null;
                });
                assert false;
            } catch (SQLException e) {
                assert e.getMessage().contains("Syntax error in SQL");
                assert e.getMessage().contains("xxxxx");
            }
            //
            //
            try {
                new JdbcConnection(dataSource).execute((StatementCallback<Object>) stat -> {
                    stat.execute("xxxxx");
                    return null;
                });
                assert false;
            } catch (SQLException e) {
                assert e.getMessage().contains("Syntax error in SQL");
                assert e.getMessage().contains("xxxxx");
            }
        }
    }

    @Test
    public void jdbcConnectionTest_5() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            DataSource dataSource = appContext.getInstance(DataSource.class);
            JdbcConnection jdbcConnection = new JdbcConnection(dataSource);
            jdbcConnection.setMaxRows(123);
            jdbcConnection.setFetchSize(10);
            jdbcConnection.setQueryTimeout(1234567);
            //
            jdbcConnection.execute((StatementCallback<Object>) stat -> {
                assert stat.getMaxRows() == 123;
                assert stat.getFetchSize() == 10;
                assert stat.getQueryTimeout() == 1234567;
                return null;
            });
        }
    }

    @Test
    public void jdbcConnectionTest_6() throws SQLException {
        Connection connection = PowerMockito.mock(Connection.class);
        JdbcConnection jdbcConnection = new JdbcConnection(connection);
        //
        jdbcConnection.execute((ConnectionCallback<Object>) con -> {
            assert con instanceof ConnectionProxy;
            assert ((ConnectionProxy) con).getTargetConnection() == connection;
            assert con != connection;
            assert ((ConnectionProxy) con).getTargetSource() == null;
            assert con.equals(con);
            assert !con.equals(connection);
            //
            con.hashCode();
            con.close();
            return null;
        });
    }

    @Test
    public void jdbcTemplateTest_6() {
        DataSource dataSource = PowerMockito.mock(DataSource.class);
        Connection connection = PowerMockito.mock(Connection.class);
        MappingHandler mappingHandler = PowerMockito.mock(MappingHandler.class);
        //
        assert new JdbcTemplate(dataSource, mappingHandler).getMappingHandler() == mappingHandler;
        assert new JdbcTemplate(connection, mappingHandler).getMappingHandler() == mappingHandler;
        //
        assert new JdbcTemplate().getMappingHandler() == MappingHandler.DEFAULT;
        //
        assert new JdbcTemplate().isResultsCaseInsensitive();
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        assert jdbcTemplate.isResultsCaseInsensitive();
        jdbcTemplate.setResultsCaseInsensitive(false);
        assert !jdbcTemplate.isResultsCaseInsensitive();
    }
}
