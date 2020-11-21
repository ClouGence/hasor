package net.hasor.db.jdbc.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.types.handler.BigIntegerTypeHandler;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.math.BigInteger;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.List;

public class BigIntegerTypeTest {
    @Test
    public void testBigIntegerTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_bigint) values (1234567890);");
            List<BigInteger> dat = jdbcTemplate.query("select c_bigint from tb_h2types where c_bigint is not null limit 1;", (rs, rowNum) -> {
                return new BigIntegerTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0).toString().equals("1234567890");
        }
    }

    @Test
    public void testBigIntegerTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_bigint) values (1234567890);");
            List<BigInteger> dat = jdbcTemplate.query("select c_bigint from tb_h2types where c_bigint is not null limit 1;", (rs, rowNum) -> {
                return new BigIntegerTypeHandler().getResult(rs, "c_bigint");
            });
            assert dat.get(0).toString().equals("1234567890");
        }
    }

    @Test
    public void testBigIntegerTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<BigInteger> dat = jdbcTemplate.query("select ?", ps -> {
                new BigIntegerTypeHandler().setParameter(ps, 1, new BigInteger("1234567890"), JDBCType.BIGINT);
            }, (rs, rowNum) -> {
                return new BigIntegerTypeHandler().getNullableResult(rs, 1);
            });
            assert dat.get(0).toString().equals("1234567890");
        }
    }

    @Test
    public void testBigIntegerTypeHandler_4() throws SQLException {
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