package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.CallableSqlParameter;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.IntegerTypeHandler;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class IntegerTypeTest {
    @Test
    public void testIntegerTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_integer) values (123);");
            List<Integer> dat = jdbcTemplate.query("select c_integer from tb_h2types where c_integer is not null limit 1;", (rs, rowNum) -> {
                return new IntegerTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0) == 123;
        }
    }

    @Test
    public void testIntegerTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_integer) values (123);");
            List<Integer> dat = jdbcTemplate.query("select c_integer from tb_h2types where c_integer is not null limit 1;", (rs, rowNum) -> {
                return new IntegerTypeHandler().getResult(rs, "c_integer");
            });
            assert dat.get(0) == 123;
        }
    }

    @Test
    public void testIntegerTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            int dat1 = jdbcTemplate.queryForObject("select ?", int.class, 123);
            Integer dat2 = jdbcTemplate.queryForObject("select ?", Integer.class, 123);
            assert dat1 == 123;
            assert dat2 == 123;
            //
            List<Integer> dat = jdbcTemplate.query("select ?", ps -> {
                new IntegerTypeHandler().setParameter(ps, 1, 123, JDBCType.INTEGER);
            }, (rs, rowNum) -> {
                return new IntegerTypeHandler().getNullableResult(rs, 1);
            });
            assert dat.get(0) == 123;
        }
    }

    @Test
    public void testIntegerTypeHandler_4() throws SQLException {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_integer;");
            jdbcTemplate.execute("create procedure proc_integer(out p_out integer) begin set p_out=123123; end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_integer(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.INTEGER, new IntegerTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Integer;
            assert objectMap.get("out").equals(123123);
            assert objectMap.get("#update-count-1").equals(0);
        }
    }
}