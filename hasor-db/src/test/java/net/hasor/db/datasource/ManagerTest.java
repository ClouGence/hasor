package net.hasor.db.datasource;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.TB_User;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerTest extends AbstractDbTest {
    @Test
    public void manager_basic_test_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            DataSource dataSource = appContext.getInstance(DataSource.class);
            Connection connection = DataSourceManager.newConnection(dataSource);
            //
            JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
            int executeUpdate = jdbcTemplate.queryForInt("select count(1) from tb_user");
            List<TB_User> tbUsers = jdbcTemplate.queryForList("select * from tb_user", TB_User.class);
            //
            assert executeUpdate == 3;
            assert tbUsers.size() == 3;
            List<String> collect = tbUsers.stream().map(TB_User::getName).collect(Collectors.toList());
            assert collect.contains("默罕默德");
            assert collect.contains("安妮.贝隆");
            assert collect.contains("赵飞燕");
            //
            connection.close();
        }
    }
}