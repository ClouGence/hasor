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
package net.hasor.db.datasource;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.transaction.Isolation;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.dto.TB_User;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;
import java.util.stream.Collectors;

import static net.hasor.test.db.utils.TestUtils.*;

public class HolderTest extends AbstractDbTest {
    @Test
    public void holder_basic_test_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            DataSource dataSource = appContext.getInstance(DataSource.class);
            ConnectionHolder holder = new ConnectionHolder(dataSource);
            //
            assert !holder.isOpen();
            assert holder.getConnection() == null;
            assert !holder.hasTransaction();
            //
            holder.requested(); // 申请引用计数 +1
            assert holder.isOpen();
            assert holder.getConnection() != null;
            assert !holder.hasTransaction();
            //
            assert holder.supportSavepoint();// h2 是支持 Savepoint 的
            holder.released();
            assert !holder.isOpen();
            assert holder.getConnection() == null;
            assert !holder.hasTransaction();
            //
            assert holder.getDataSource() == dataSource;
            //
            try {
                holder.supportSavepoint();// h2 是支持 Savepoint 的
                assert false;
            } catch (Exception e) {
                assert e.getMessage().equals("Connection is null.");
            }
        }
    }

    @Test
    public void holder_basic_test_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            DataSource dataSource = appContext.getInstance(DataSource.class);
            ConnectionHolder holder = new ConnectionHolder(dataSource);
            //
            assert holder.getConnection() == null;
            assert !holder.isOpen();
            assert !holder.hasTransaction();
            //
            holder.requested(); // 申请引用计数 +1
            //
            assert holder.isOpen();
            assert !holder.hasTransaction();
            {
                JdbcTemplate jdbcTemplate = new JdbcTemplate(holder.getConnection());
                int executeUpdate = jdbcTemplate.queryForInt("select count(1) from tb_user");
                List<TB_User> tbUsers = jdbcTemplate.queryForList("select * from tb_user", TB_User.class);
                //
                assert executeUpdate == 3;
                assert tbUsers.size() == 3;
                List<String> collect = tbUsers.stream().map(TB_User::getName).collect(Collectors.toList());
                assert collect.contains(beanForData1().getName());
                assert collect.contains(beanForData2().getName());
                assert collect.contains(beanForData3().getName());
            }
            holder.released();// 申请引用计数 -1
            //
            assert !holder.isOpen();
        }
    }

    @Test
    public void holder_basic_test_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            DataSource dataSource = appContext.getInstance(DataSource.class);
            //
            Connection conn = DataSourceManager.newConnection(dataSource);
            //
            assert conn instanceof ConnectionProxy;
            assert ((ConnectionProxy) conn).getTargetSource() == dataSource;
            //
            assert ((ConnectionProxy) conn).getTargetConnection() != null;
            assert conn.hashCode() == System.identityHashCode(conn);
            assert conn.equals(conn);
            //
            conn.close();
        }
    }

    @Test
    public void holder_tran_test_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            DataSource dataSource = appContext.getInstance(DataSource.class);
            ConnectionHolder holder1 = new ConnectionHolder(dataSource);    // tran1
            ConnectionHolder holder2 = new ConnectionHolder(dataSource);    // tran2
            //
            // .申请数据库连接并启动事务，同时将隔离级别设置为READ_COMMITTED
            holder1.requested();
            holder1.getConnection().setTransactionIsolation(Isolation.READ_COMMITTED.ordinal());
            JdbcTemplate jdbcTemplate_h1 = new JdbcTemplate(holder1.getConnection());
            holder1.setTransaction();
            assert holder1.hasTransaction();
            holder2.requested();
            holder2.getConnection().setTransactionIsolation(Isolation.READ_COMMITTED.ordinal());
            holder2.setTransaction();
            JdbcTemplate jdbcTemplate_h2 = new JdbcTemplate(holder2.getConnection());
            assert holder2.hasTransaction();
            //
            //
            // .holder1 插入一条数据，不递交
            jdbcTemplate_h1.executeUpdate(INSERT_ARRAY, arrayForData4());
            //
            // .查询 holder1 和 holder2
            {
                int executeUpdate_h1 = jdbcTemplate_h1.queryForInt("select count(1) from tb_user");
                int executeUpdate_h2 = jdbcTemplate_h2.queryForInt("select count(1) from tb_user");
                assert executeUpdate_h1 == 4;   // 同一个会话中可以读取到未递交的数据
                assert executeUpdate_h2 == 3;   // 不同会话无法读取另一个会话里未递交的数据
            }
            //
            {
                holder1.getConnection().commit();
                int executeUpdate_h1 = jdbcTemplate_h1.queryForInt("select count(1) from tb_user");
                int executeUpdate_h2 = jdbcTemplate_h2.queryForInt("select count(1) from tb_user");
                assert executeUpdate_h1 == 4;
                assert executeUpdate_h2 == 4;   // 事务已递交
            }
            //
            holder1.released();
            holder2.released();
        }
    }

    @Test
    public void holder_tran_test_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            DataSource dataSource = appContext.getInstance(DataSource.class);
            ConnectionHolder holder1 = new ConnectionHolder(dataSource);    // tran1
            ConnectionHolder holder2 = new ConnectionHolder(dataSource);    // tran2
            //
            // .申请数据库连接并启动事务，同时将隔离级别设置为READ_COMMITTED（测试的时候会把事务状态取消掉）
            holder1.requested();
            holder1.getConnection().setTransactionIsolation(Isolation.READ_COMMITTED.ordinal());
            JdbcTemplate jdbcTemplate_h1 = new JdbcTemplate(holder1.getConnection());
            holder1.setTransaction();
            assert holder1.hasTransaction();
            holder2.requested();
            holder2.getConnection().setTransactionIsolation(Isolation.READ_COMMITTED.ordinal());
            holder2.setTransaction();
            JdbcTemplate jdbcTemplate_h2 = new JdbcTemplate(holder2.getConnection());
            assert holder2.hasTransaction();
            //
            //
            // .holder1 取消事务，自动递交
            holder1.cancelTransaction();
            //
            jdbcTemplate_h1.executeUpdate(INSERT_ARRAY, arrayForData4());
            //
            // .查询 holder1 和 holder2
            int executeUpdate_h1 = jdbcTemplate_h1.queryForInt("select count(1) from tb_user");
            int executeUpdate_h2 = jdbcTemplate_h2.queryForInt("select count(1) from tb_user");
            assert executeUpdate_h1 == 4;
            assert executeUpdate_h2 == 4;   // 事务自动递交
            //
            holder1.released();
            holder2.released();
        }
    }

    @Test
    public void holder_savepoint_test_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            DataSource dataSource = appContext.getInstance(DataSource.class);
            ConnectionHolder holder1 = new ConnectionHolder(dataSource);    // tran1
            ConnectionHolder holder2 = new ConnectionHolder(dataSource);    // tran2
            //
            // .申请数据库连接并启动事务，同时将隔离级别设置为READ_COMMITTED（测试的时候会把事务状态取消掉）
            holder1.requested();
            holder2.requested();
            holder1.getConnection().setTransactionIsolation(Isolation.READ_COMMITTED.ordinal());
            holder2.getConnection().setTransactionIsolation(Isolation.READ_COMMITTED.ordinal());
            holder1.setTransaction();
            holder2.setTransaction();
            JdbcTemplate jdbcTemplate_h1 = new JdbcTemplate(holder1.getConnection());
            JdbcTemplate jdbcTemplate_h2 = new JdbcTemplate(holder2.getConnection());
            //
            // .holder1 插入两个新数据每插入一个之后都创建一个Savepoint
            jdbcTemplate_h1.executeUpdate(INSERT_ARRAY, arrayForData4());
            Savepoint sp_1 = holder1.createSavepoint();
            jdbcTemplate_h1.executeUpdate(INSERT_ARRAY, arrayForData5());
            Savepoint sp_2 = holder1.createSavepoint();
            jdbcTemplate_h1.executeUpdate(INSERT_ARRAY, arrayForData6());
            //
            // .查询
            assert jdbcTemplate_h1.queryForInt("select count(1) from tb_user") == 6; // holder1,未递交事务一共有 5 条记录
            assert jdbcTemplate_h2.queryForInt("select count(1) from tb_user") == 3; // holder2,独立连接，只能读取到3条
            //
            // .回滚sp_2
            holder1.rollbackToSavepoint(sp_2);
            assert jdbcTemplate_h1.queryForInt("select count(1) from tb_user") == 5; // holder1,部分回滚一共有 5 条记录
            assert jdbcTemplate_h2.queryForInt("select count(1) from tb_user") == 3; // holder2,独立连接，只能读取到3条
            //
            // .递交
            holder1.getConnection().commit();
            assert jdbcTemplate_h1.queryForInt("select count(1) from tb_user") == 5; // holder1,
            assert jdbcTemplate_h2.queryForInt("select count(1) from tb_user") == 5; // holder2,
            //
            holder1.released();
            holder2.released();
        }
    }
}
