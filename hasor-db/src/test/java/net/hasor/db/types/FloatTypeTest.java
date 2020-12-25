package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.SqlParameterUtils;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.FloatTypeHandler;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FloatTypeTest {
    @Test
    public void testFloatTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_float) values (123.123);");
            List<Float> dat = jdbcTemplate.query("select c_float from tb_h2_types where c_float is not null limit 1;", (rs, rowNum) -> {
                return new FloatTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0) == 123.123f;
        }
    }

    @Test
    public void testFloatTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_float) values (123.123);");
            List<Float> dat = jdbcTemplate.query("select c_float from tb_h2_types where c_float is not null limit 1;", (rs, rowNum) -> {
                return new FloatTypeHandler().getResult(rs, "c_float");
            });
            assert dat.get(0) == 123.123f;
        }
    }

    @Test
    public void testFloatTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            float dat1 = jdbcTemplate.queryForObject("select ?", float.class, 123.123f);
            Float dat2 = jdbcTemplate.queryForObject("select ?", Float.class, 123.123f);
            assert dat1 == 123.123f;
            assert dat2 == 123.123f;
            //
            List<Float> dat = jdbcTemplate.query("select ?", ps -> {
                new FloatTypeHandler().setParameter(ps, 1, 123.123f, JDBCType.FLOAT);
            }, (rs, rowNum) -> {
                return new FloatTypeHandler().getNullableResult(rs, 1);
            });
            assert dat.get(0) == 123.123f;
        }
    }

    @Test
    public void testFloatTypeHandler_4() throws SQLException {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_float;");
            jdbcTemplate.execute("create procedure proc_float(out p_out float) begin set p_out=123.123; end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_float(?)}",//
                    Collections.singletonList(SqlParameterUtils.withOutput("out", JDBCType.FLOAT, new FloatTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Float;
            assert objectMap.get("out").equals(123.123f);
            assert objectMap.get("#update-count-1").equals(0);
        }
    }
}