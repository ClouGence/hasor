package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.CallableSqlParameter;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.DoubleTypeHandler;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DoubleTypeTest {
    @Test
    public void testDoubleTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_double) values (123.123);");
            List<Double> dat = jdbcTemplate.query("select c_double from tb_h2types where c_double is not null limit 1;", (rs, rowNum) -> {
                return new DoubleTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0) == 123.123d;
        }
    }

    @Test
    public void testDoubleTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_double) values (123.123);");
            List<Double> dat = jdbcTemplate.query("select c_double from tb_h2types where c_double is not null limit 1;", (rs, rowNum) -> {
                return new DoubleTypeHandler().getResult(rs, "c_double");
            });
            assert dat.get(0) == 123.123d;
        }
    }

    @Test
    public void testDoubleTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            double dat1 = jdbcTemplate.queryForObject("select ?", double.class, 123.123d);
            Double dat2 = jdbcTemplate.queryForObject("select ?", Double.class, 123.123d);
            assert dat1 == 123.123d;
            assert dat2 == 123.123d;
            //
            List<Double> dat = jdbcTemplate.query("select ?", ps -> {
                new DoubleTypeHandler().setParameter(ps, 1, 123.123d, JDBCType.DOUBLE);
            }, (rs, rowNum) -> {
                return new DoubleTypeHandler().getNullableResult(rs, 1);
            });
            assert dat.get(0) == 123.123d;
        }
    }

    @Test
    public void testCharacterTypeHandler_4() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DsUtils.JDBC_URL)) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_double;");
            jdbcTemplate.execute("create procedure proc_double(out p_out double) begin set p_out=123.123; end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_double(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.DOUBLE, new DoubleTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Double;
            assert objectMap.get("out").equals(123.123d);
            assert objectMap.get("#update-count-1").equals(0);
        }
    }
}