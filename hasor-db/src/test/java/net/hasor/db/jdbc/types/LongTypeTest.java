package net.hasor.db.jdbc.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.types.handler.LongTypeHandler;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.List;

public class LongTypeTest {
    @Test
    public void testLongTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_bigint) values (123);");
            List<Long> dat = jdbcTemplate.query("select c_bigint from tb_h2types where c_bigint is not null limit 1;", (rs, rowNum) -> {
                return new LongTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0) == 123l;
        }
    }

    @Test
    public void testLongTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_bigint) values (123);");
            List<Long> dat = jdbcTemplate.query("select c_bigint from tb_h2types where c_bigint is not null limit 1;", (rs, rowNum) -> {
                return new LongTypeHandler().getResult(rs, "c_bigint");
            });
            assert dat.get(0) == 123l;
        }
    }

    @Test
    public void testLongTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            long dat1 = jdbcTemplate.queryForObject("select ?", long.class, 123l);
            Long dat2 = jdbcTemplate.queryForObject("select ?", Long.class, 123l);
            assert dat1 == 123l;
            assert dat2 == 123l;
            //
            List<Long> dat = jdbcTemplate.query("select ?", ps -> {
                new LongTypeHandler().setParameter(ps, 1, 123l, JDBCType.BIGINT);
            }, (rs, rowNum) -> {
                return new LongTypeHandler().getNullableResult(rs, 1);
            });
            assert dat.get(0) == 123l;
        }
    }

    @Test
    public void testLongTypeHandler_4() throws SQLException {
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