package net.hasor.db.jdbc.mapper;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.dto.TB_User;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static net.hasor.test.db.utils.TestUtils.*;

public class BeanMapRowMapperTest {
    @Test
    public void testColumnMapRowMapper_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            List<TB_User> mapList = jdbcTemplate.queryForList("select * from tb_user", TB_User.class);
            //
            List<String> collect = mapList.stream().map(TB_User::getName).collect(Collectors.toList());
            //
            assert mapList.size() == 3;
            assert collect.contains(beanForData1().getName());
            assert collect.contains(beanForData2().getName());
            assert collect.contains(beanForData3().getName());
        }
    }

    @Test
    public void testColumnMapRowMapper_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            List<TB_User> mapList = jdbcTemplate.query("select * from tb_user", BeanPropertyRowMapper.newInstance(TB_User.class));
            //
            List<String> collect = mapList.stream().map(TB_User::getName).collect(Collectors.toList());
            //
            assert mapList.size() == 3;
            assert collect.contains(beanForData1().getName());
            assert collect.contains(beanForData2().getName());
            assert collect.contains(beanForData3().getName());
        }
    }
}