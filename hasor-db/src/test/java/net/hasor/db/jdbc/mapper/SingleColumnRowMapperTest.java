package net.hasor.db.jdbc.mapper;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.dto.TB_User2;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

public class SingleColumnRowMapperTest {
    @Test
    public void testSingleColumnRowMapper_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            String resultData = null;
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('abc');");
            resultData = jdbcTemplate.queryForObject(//
                    "select c_varchar from tb_h2types where c_varchar = 'abc';", String.class);
            assert "abc".equals(resultData);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_int) values (123);");
            resultData = jdbcTemplate.queryForObject(//
                    "select c_int from tb_h2types where c_int = 123;", String.class);
            assert "123".equals(resultData);
            //
            SingleColumnRowMapper<String> rowMapper = new SingleColumnRowMapper<>(String.class);
            rowMapper.setRequiredType(String.class);
            resultData = jdbcTemplate.queryForObject(//
                    "select c_int from tb_h2types where c_int = 123;", rowMapper);
            assert "123".equals(resultData);
        }
    }

    @Test
    public void testSingleColumnRowMapper_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_int) values (123);");
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_double) values (123.123);");
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_float) values (123.123);");
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_time) values (?)", new Date());
            //
            int num1 = jdbcTemplate.queryForObject("select c_int from tb_h2types where c_int = 123;", Integer.class);
            Number num2 = jdbcTemplate.queryForObject("select c_int from tb_h2types where c_int = 123;", Number.class);
            double num3 = jdbcTemplate.queryForObject("select c_int from tb_h2types where c_int = 123;", double.class);
            BigDecimal num4 = jdbcTemplate.queryForObject("select c_int from tb_h2types where c_int = 123;", BigDecimal.class);
            Number num5 = jdbcTemplate.queryForObject("select c_time from tb_h2types where c_time is not null limit 1;", Number.class);
            //
            assert num1 == 123;
            assert num2.intValue() == 123;
            assert num2 instanceof Integer;
            assert num3 == 123d;
            assert num4.intValue() == 123;
            assert num5 != null;
            assert num5.longValue() != 0;
        }
    }

    @Test
    public void testSingleColumnRowMapper_3() {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            try {
                jdbcTemplate.queryForList("select *,'' as futures from tb_user", TB_User2.class);
                assert false;
            } catch (Exception e) {
                assert e.getMessage().startsWith("no typeHandler is matched to any available");
            }
        }
    }
}