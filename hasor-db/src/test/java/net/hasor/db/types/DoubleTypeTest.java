package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.DoubleTypeHandler;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.List;

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
        //        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
        //            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        //            //
        //            jdbcTemplate.executeUpdate("CREATE ALIAS AS_BIGINTEGER FOR \"net.hasor.test.db.CallableFunction.asBigInteger\";");
        //            BigInteger BigInteger = jdbcTemplate.execute("call AS_BIGINTEGER(?)", (CallableStatementCallback<BigInteger>) cs -> {
        //                cs.ge
        //                return null;
        //            });
        //            assert BigInteger.intValue() == 123;
        //        }
    }
}