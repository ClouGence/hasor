package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.BooleanTypeHandler;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.List;

public class BooleanTypeTest {
    @Test
    public void testBooleanTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_boolean) values (true);");
            List<Boolean> dat = jdbcTemplate.query("select c_boolean from tb_h2types where c_boolean is not null limit 1;", (rs, rowNum) -> {
                return new BooleanTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0);
        }
    }

    @Test
    public void testBooleanTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_boolean) values (true);");
            List<Boolean> dat = jdbcTemplate.query("select c_boolean from tb_h2types where c_boolean is not null limit 1;", (rs, rowNum) -> {
                return new BooleanTypeHandler().getResult(rs, "c_boolean");
            });
            assert dat.get(0);
        }
    }

    @Test
    public void testBooleanTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            boolean dat1 = jdbcTemplate.queryForObject("select ?", boolean.class, true);
            Boolean dat2 = jdbcTemplate.queryForObject("select ?", Boolean.class, true);
            boolean dat3 = jdbcTemplate.queryForObject("select ?", boolean.class, false);
            Boolean dat4 = jdbcTemplate.queryForObject("select ?", Boolean.class, false);
            assert dat1;
            assert dat2;
            assert !dat3;
            assert !dat4;
            //
            List<Boolean> dat = jdbcTemplate.query("select ?", ps -> {
                new BooleanTypeHandler().setParameter(ps, 1, true, JDBCType.BOOLEAN);
            }, (rs, rowNum) -> {
                return new BooleanTypeHandler().getNullableResult(rs, 1);
            });
            assert dat.get(0);
        }
    }

    @Test
    public void testBooleanTypeHandler_4() throws SQLException {
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