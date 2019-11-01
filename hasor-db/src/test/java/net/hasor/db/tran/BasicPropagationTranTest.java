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
package net.hasor.db.tran;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TranManager;
import net.hasor.db.transaction.TransactionCallbackWithoutResult;
import net.hasor.db.transaction.TransactionTemplate;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.TB_User;
import net.hasor.test.db.single.SingleDataSourceModule;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 事务传播属性
 * @version : 2015年11月10日
 * @author 赵永春 (zyc@hasor.net)
 */
public class BasicPropagationTranTest extends AbstractDbTest {
    @Test
    public void tran_required_test_1() throws Throwable {
        AppContext appContext = Hasor.create().mainSettingWith("/net_hasor_db/jdbc-config.properties").build(apiBinder -> {
            apiBinder.installModule(new SingleDataSourceModule());
        });
        //
        // .REQUIRED：尝试加入已经存在的事务中，如果没有则开启一个新的事务.
        //      - 虽然T2抛了异常但是事务传播属性是加入已有事务，那么 T1 在最后递交事务的时候仍然会把 T2 的数据递交进去。 因为T2只是执行了跟随行为。
        //
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        TransactionTemplate temp = appContext.getInstance(TransactionTemplate.class);
        temp.execute((TransactionCallbackWithoutResult) t1 -> {
            System.out.println("begin T1!");
            /*T1 - 赵子龙*/
            insertData_1(jdbcTemplate);
            assert tableCountWithCurrent(jdbcTemplate) == 4;    // 当前记录数已经是4
            assert tableCountWithNew(jdbcTemplate) == 3;        // 事务没有递交因此外部 Connection 无法看到新数据
            try {
                temp.execute((TransactionCallbackWithoutResult) t2 -> {
                    /*T2 - 诸葛亮*/
                    insertData_2(jdbcTemplate);
                    throw new Exception("rollback T2.");
                }, Propagation.REQUIRED);
                assert false;
            } catch (Exception e) {
                /* T2 Rollback */
                assert true;
            }
            /*T1 - 张果老*/
            insertData_3(jdbcTemplate);
            assert tableCountWithCurrent(jdbcTemplate) == 6;    // 当前记录数已经是6
            assert tableCountWithNew(jdbcTemplate) == 3;        // 事务没有递交因此外部 Connection 无法看到新数据
        }, Propagation.REQUIRED);
        //
        printMapList(jdbcTemplate.queryForList("select * from TB_User"));
        //
        assert jdbcTemplate.queryForInt("select count(1) from TB_User") == 6;
        List<TB_User> tbUsers = jdbcTemplate.queryForList("select * from TB_User", TB_User.class);
        List<String> collect = tbUsers.stream().map(TB_User::getName).collect(Collectors.toList());
        assert collect.contains("赵子龙"); // T1 数据
        assert collect.contains("诸葛亮"); // T2 数据
        assert collect.contains("张果老"); // T1 数据
    }

    @Test
    public void tran_requirednew_test_1() throws Throwable {
        AppContext appContext = Hasor.create().mainSettingWith("/net_hasor_db/jdbc-config.properties").build(apiBinder -> {
            apiBinder.installModule(new SingleDataSourceModule());
        });
        //
        // .REQUIRES_NEW：使用独立事务.
        //      - T2抛了异常由于T2是独立事务因此T2的数据被回滚掉了，最终数据库中只有T1的数据
        //
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        TransactionTemplate temp = appContext.getInstance(TransactionTemplate.class);
        temp.execute((TransactionCallbackWithoutResult) t1 -> {
            System.out.println("begin T1!");
            /*T1 - 赵子龙*/
            insertData_1(jdbcTemplate);
            assert tableCountWithCurrent(jdbcTemplate) == 4;    // 当前记录数已经是4
            assert tableCountWithNew(jdbcTemplate) == 3;        // 事务没有递交因此外部 Connection 无法看到新数据
            /*T2 - 诸葛亮*/
            temp.execute((TransactionCallbackWithoutResult) t2 -> {
                insertData_2(jdbcTemplate);
                assert tableCountWithCurrent(jdbcTemplate) == 4;    // 当前记录数还是4，因为 T1 的事务和 T2 的事务是隔离的相互还看不到
                assert tableCountWithNew(jdbcTemplate) == 3;        // T1，T2 都没有递交因此外部 Connection 无法看到这两个事务的数据
            }, Propagation.REQUIRES_NEW);
            //
            assert tableCountWithCurrent(jdbcTemplate) == 5;    // T2 已经递交因此 T1 可以看到
            assert tableCountWithNew(jdbcTemplate) == 4;        // T1没有递交，T2已经递交，此时外部 Connection 只可以看到T2的新增数据
            /*T1 - 张果老*/
            insertData_3(jdbcTemplate);
        }, Propagation.REQUIRES_NEW);
        //
        printMapList(jdbcTemplate.queryForList("select * from TB_User"));
        //
        assert jdbcTemplate.queryForInt("select count(1) from TB_User") == 6;
        List<TB_User> tbUsers = jdbcTemplate.queryForList("select * from TB_User", TB_User.class);
        List<String> collect = tbUsers.stream().map(TB_User::getName).collect(Collectors.toList());
        assert collect.contains("赵子龙"); // T1 数据
        assert collect.contains("诸葛亮"); // T2 数据
        assert collect.contains("张果老"); // T1 数据
    }

    @Test
    public void tran_nested_test_1() throws Throwable {
        AppContext appContext = Hasor.create().mainSettingWith("/net_hasor_db/jdbc-config.properties").build(apiBinder -> {
            apiBinder.installModule(new SingleDataSourceModule());
        });
        //
        // .NESTED：使用保存点方式分割两个事务.
        //      - T2抛了异常由于T2被savepoint回滚掉了，最终数据库中只有T1的数据
        //
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        TransactionTemplate temp = appContext.getInstance(TransactionTemplate.class);
        temp.execute((TransactionCallbackWithoutResult) t1 -> {
            System.out.println("begin T1!");
            /*T1 - 赵子龙*/
            insertData_1(jdbcTemplate);
            assert tableCountWithCurrent(jdbcTemplate) == 4;    // 当前记录数已经是4
            assert tableCountWithNew(jdbcTemplate) == 3;        // 事务没有递交因此外部 Connection 无法看到新数据
            try {
                temp.execute((TransactionCallbackWithoutResult) t2 -> {
                    /*T2 - 诸葛亮*/
                    insertData_2(jdbcTemplate);
                    assert tableCountWithCurrent(jdbcTemplate) == 5;    // 当前记录数已经是4
                    assert tableCountWithNew(jdbcTemplate) == 3;        // 事务没有递交因此外部 Connection 无法看到新数据
                    throw new Exception("rollback T2.");
                }, Propagation.NESTED);
                assert false;
            } catch (Exception e) {
                /* T2 Rollback */
                assert true;
            }
            assert tableCountWithCurrent(jdbcTemplate) == 4;    // 当前记录数已经是4， T2 被回滚了
            assert tableCountWithNew(jdbcTemplate) == 3;        // 事务没有递交因此外部 Connection 无法看到新数据
            /*T1 - 张果老*/
            insertData_3(jdbcTemplate);
        }, Propagation.NESTED);
        //
        printMapList(jdbcTemplate.queryForList("select * from TB_User"));
        //
        assert jdbcTemplate.queryForInt("select count(1) from TB_User") == 5;
        List<TB_User> tbUsers = jdbcTemplate.queryForList("select * from TB_User", TB_User.class);
        List<String> collect = tbUsers.stream().map(TB_User::getName).collect(Collectors.toList());
        assert collect.contains("赵子龙"); // T1 数据
        assert !collect.contains("诸葛亮"); // T2 数据
        assert collect.contains("张果老"); // T1 数据
    }

    @Test
    public void tran_never_test_1() throws Throwable {
        AppContext appContext = Hasor.create().mainSettingWith("/net_hasor_db/jdbc-config.properties").build(apiBinder -> {
            apiBinder.installModule(new SingleDataSourceModule());
        });
        //
        // .NEVER：非事务方式运行.
        //      - T2抛了异常，但是是非事务方式，因此数据已经递交到数据库中。
        //
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        TransactionTemplate temp = appContext.getInstance(TransactionTemplate.class);
        Connection connection = TranManager.currentConnection(jdbcTemplate.getDataSource());
        connection.setAutoCommit(true); // 打开默认事务
        //
        temp.execute((TransactionCallbackWithoutResult) t1 -> {
            System.out.println("begin T1!");
            /*T1 - 赵子龙*/
            insertData_1(jdbcTemplate);
            assert tableCountWithNew(jdbcTemplate) == 4;    // 非事务方式，意味着每次数据库操作都是自动递交事务。外部 connectipn 是可以同时看到的。
            /*T2 - 诸葛亮*/
            temp.execute((TransactionCallbackWithoutResult) t2 -> {
                insertData_2(jdbcTemplate);
                assert tableCountWithNew(jdbcTemplate) == 5;// T2 也是非事务，外部 Connection 也是可以看到的。
            }, Propagation.NEVER);
            /*T1 - 张果老*/
            insertData_3(jdbcTemplate);
        }, Propagation.NEVER);
        //
        printMapList(jdbcTemplate.queryForList("select * from TB_User"));
        //
        assert jdbcTemplate.queryForInt("select count(1) from TB_User") == 6;
        List<TB_User> tbUsers = jdbcTemplate.queryForList("select * from TB_User", TB_User.class);
        List<String> collect = tbUsers.stream().map(TB_User::getName).collect(Collectors.toList());
        assert collect.contains("赵子龙"); // T1 数据
        assert collect.contains("诸葛亮"); // T2 数据
        assert collect.contains("张果老"); // T1 数据
        //
        connection.close();
    }

    @Test
    public void tran_never_test_2() throws Throwable {
        AppContext appContext = Hasor.create().mainSettingWith("/net_hasor_db/jdbc-config.properties").build(apiBinder -> {
            apiBinder.installModule(new SingleDataSourceModule());
        });
        //
        // .NEVER：环境中必须没有事务，否则抛异常
        //
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        TransactionTemplate temp = appContext.getInstance(TransactionTemplate.class);
        TranManager.currentConnection(jdbcTemplate.getDataSource()).setAutoCommit(false); // 关闭默认事务，需要手动 commit
        //
        try {
            temp.execute((TransactionCallbackWithoutResult) t1 -> {
                assert false;// 这里是不会进来的。
                System.out.println("begin T1!");
                /*T1 - 赵子龙*/
                insertData_1(jdbcTemplate);
                /*T1 - 张果老*/
                insertData_3(jdbcTemplate);
            }, Propagation.NEVER);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("Existing transaction found for transaction marked with propagation 'never'");
        }
    }

    @Test
    public void tran_mandatory_test_1() throws Throwable {
        AppContext appContext = Hasor.create().mainSettingWith("/net_hasor_db/jdbc-config.properties").build(apiBinder -> {
            apiBinder.installModule(new SingleDataSourceModule());
        });
        //
        // .MANDATORY：要求环境中必须有事务
        //
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        TransactionTemplate temp = appContext.getInstance(TransactionTemplate.class);
        Connection connection = TranManager.currentConnection(jdbcTemplate.getDataSource());
        connection.setAutoCommit(false); // 关闭默认事务，开启手动事务
        //
        /* 赵子龙*/
        insertData_1(jdbcTemplate);
        assert tableCountWithNew(jdbcTemplate) == 3;    // 事务尚未递交
        assert tableCountWithCurrent(jdbcTemplate) == 4;// 事务尚未递交
        //
        temp.execute((TransactionCallbackWithoutResult) t1 -> {
            /*T1 - 诸葛亮*/
            insertData_2(jdbcTemplate);
            assert tableCountWithNew(jdbcTemplate) == 3;    // 事务尚未递交
            assert tableCountWithCurrent(jdbcTemplate) == 5;// 事务尚未递交
        }, Propagation.MANDATORY);
        //
        assert tableCountWithNew(jdbcTemplate) == 3;    // 事务尚未递交
        assert tableCountWithCurrent(jdbcTemplate) == 5;// 事务尚未递交
        //
        connection.commit();
        connection.close();
        assert tableCountWithNew(jdbcTemplate) == 5;    // 事务尚未递交
        assert tableCountWithCurrent(jdbcTemplate) == 5;// 事务尚未递交
        //
        printMapList(jdbcTemplate.queryForList("select * from TB_User"));
        //
        assert jdbcTemplate.queryForInt("select count(1) from TB_User") == 5;
        List<TB_User> tbUsers = jdbcTemplate.queryForList("select * from TB_User", TB_User.class);
        List<String> collect = tbUsers.stream().map(TB_User::getName).collect(Collectors.toList());
        assert collect.contains("赵子龙");
        assert collect.contains("诸葛亮");
    }

    @Test
    public void tran_mandatory_test_2() throws Throwable {
        AppContext appContext = Hasor.create().mainSettingWith("/net_hasor_db/jdbc-config.properties").build(apiBinder -> {
            apiBinder.installModule(new SingleDataSourceModule());
        });
        //
        // .MANDATORY：必须要有事务，否则抛异常
        //
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        TransactionTemplate temp = appContext.getInstance(TransactionTemplate.class);
        Connection connection = TranManager.currentConnection(jdbcTemplate.getDataSource());
        connection.setAutoCommit(true); // 关闭手动事务，使用自动事务。
        //
        try {
            temp.execute((TransactionCallbackWithoutResult) t1 -> {
                System.out.println("begin T1!");
                /*T1 - 赵子龙*/
                insertData_1(jdbcTemplate);
                /*T1 - 张果老*/
                insertData_3(jdbcTemplate);
            }, Propagation.MANDATORY);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("No existing transaction found for transaction marked with propagation 'mandatory'");
        }
        //
        connection.close();
    }

    @Test
    public void tran_supports_test_1() throws Throwable {
        AppContext appContext = Hasor.create().mainSettingWith("/net_hasor_db/jdbc-config.properties").build(apiBinder -> {
            apiBinder.installModule(new SingleDataSourceModule());
        });
        //
        // .SUPPORTS：跟随环境上的事务,  环境上启动事务，T1跟随环境，T2使用独立事务
        //
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        TransactionTemplate temp = appContext.getInstance(TransactionTemplate.class);
        Connection connection = TranManager.currentConnection(jdbcTemplate.getDataSource());
        connection.setAutoCommit(false); // 启动事务
        //
        /*T0 - 赵子龙*/
        insertData_1(jdbcTemplate);
        assert tableCountWithCurrent(jdbcTemplate) == 4;
        assert tableCountWithNew(jdbcTemplate) == 3;    // 环境事务还未递交
        //
        temp.execute((TransactionCallbackWithoutResult) t1 -> {
            /*T1 - 诸葛亮*/
            insertData_2(jdbcTemplate);
            assert tableCountWithCurrent(jdbcTemplate) == 5;
            assert tableCountWithNew(jdbcTemplate) == 3;    // 环境事务还未递交
            /*T2 - 张果老*/
            temp.execute((TransactionCallbackWithoutResult) t2 -> {
                insertData_3(jdbcTemplate);
            }, Propagation.REQUIRES_NEW);
            assert tableCountWithCurrent(jdbcTemplate) == 6;
            assert tableCountWithNew(jdbcTemplate) == 4;    // T2是独立事务并且已经递交。
            /*T1 - 吴广*/
            insertData_4(jdbcTemplate);
        }, Propagation.SUPPORTS);
        //
        assert tableCountWithCurrent(jdbcTemplate) == 7;
        assert tableCountWithNew(jdbcTemplate) == 4;    // T2是独立事务并且已经递交。
        connection.commit();
        connection.close();
        assert tableCountWithCurrent(jdbcTemplate) == 7;
        assert tableCountWithNew(jdbcTemplate) == 7;    // 所有事务都已经递交
        printMapList(jdbcTemplate.queryForList("select * from TB_User"));
        //
        assert jdbcTemplate.queryForInt("select count(1) from TB_User") == 7;
        List<TB_User> tbUsers = jdbcTemplate.queryForList("select * from TB_User", TB_User.class);
        List<String> collect = tbUsers.stream().map(TB_User::getName).collect(Collectors.toList());
        assert collect.contains("赵子龙"); // T1 数据
        assert collect.contains("诸葛亮"); // T2 数据
        assert collect.contains("张果老"); // T1 数据
    }

    @Test
    public void tran_not_supported_test_2() throws Throwable {
        AppContext appContext = Hasor.create().mainSettingWith("/net_hasor_db/jdbc-config.properties").build(apiBinder -> {
            apiBinder.installModule(new SingleDataSourceModule());
        });
        //
        // .NOT_SUPPORTED：无事务，如有就挂起。 T1独立事务，T2排除事务
        //
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        TransactionTemplate temp = appContext.getInstance(TransactionTemplate.class);
        //
        temp.execute((TransactionCallbackWithoutResult) t1 -> {
            /*T1 - 赵子龙*/
            insertData_1(jdbcTemplate);
            assert tableCountWithCurrent(jdbcTemplate) == 4;
            assert tableCountWithNew(jdbcTemplate) == 3;    // 环境事务还未递交
            /*T2 - 诸葛亮*/
            temp.execute((TransactionCallbackWithoutResult) t2 -> {
                insertData_2(jdbcTemplate);
                assert tableCountWithCurrent(jdbcTemplate) == 4;// 未递交的和 T2 刚刚递交的在一起
                List<TB_User> tbUsers = jdbcTemplate.queryForList("select * from TB_User", TB_User.class);
                Set<String> collect = tbUsers.stream().map(TB_User::getName).collect(Collectors.toSet());
                assert !collect.contains("赵子龙");    // T2 把 T1 的连接挂起了，并且重新申请了一个新的，此时 查询所有数据的时是不包含 T1 中的 "赵子龙"
                assert tableCountWithNew(jdbcTemplate) == 4;    // 排除事务的会递交。
            }, Propagation.NOT_SUPPORTED);
        }, Propagation.REQUIRES_NEW);
        //
        assert tableCountWithCurrent(jdbcTemplate) == 5;
        assert tableCountWithNew(jdbcTemplate) == 5;    // 所有事务都已经递交
        printMapList(jdbcTemplate.queryForList("select * from TB_User"));
        assert jdbcTemplate.queryForInt("select count(1) from TB_User") == 5;
        List<TB_User> tbUsers = jdbcTemplate.queryForList("select * from TB_User", TB_User.class);
        List<String> collect = tbUsers.stream().map(TB_User::getName).collect(Collectors.toList());
        assert collect.contains("赵子龙"); // T1 数据
        assert collect.contains("诸葛亮"); // T2 数据
    }
}