package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.FloatTypeHandler;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.List;

public class FloatTypeTest {
    @Test
    public void testFloatTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_float) values (123.123);");
            List<Float> dat = jdbcTemplate.query("select c_float from tb_h2types where c_float is not null limit 1;", (rs, rowNum) -> {
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
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_float) values (123.123);");
            List<Float> dat = jdbcTemplate.query("select c_float from tb_h2types where c_float is not null limit 1;", (rs, rowNum) -> {
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