package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.IntegerTypeHandler;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.List;

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