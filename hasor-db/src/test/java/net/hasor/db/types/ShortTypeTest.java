package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.ShortTypeHandler;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.List;

public class ShortTypeTest {
    @Test
    public void testShortTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_smallint) values (123);");
            List<Short> dat = jdbcTemplate.query("select c_smallint from tb_h2types where c_smallint is not null limit 1;", (rs, rowNum) -> {
                return new ShortTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0) == 123;
        }
    }

    @Test
    public void testShortTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_smallint) values (123);");
            List<Short> dat = jdbcTemplate.query("select c_smallint from tb_h2types where c_smallint is not null limit 1;", (rs, rowNum) -> {
                return new ShortTypeHandler().getResult(rs, "c_smallint");
            });
            assert dat.get(0) == 123;
        }
    }

    @Test
    public void testShortTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            short dat1 = jdbcTemplate.queryForObject("select ?", short.class, 123);
            Short dat2 = jdbcTemplate.queryForObject("select ?", Short.class, 123);
            assert dat1 == 123;
            assert dat2 == 123;
            //
            List<Short> dat = jdbcTemplate.query("select ?", ps -> {
                new ShortTypeHandler().setParameter(ps, 1, (short) 123, JDBCType.SMALLINT);
            }, (rs, rowNum) -> {
                return new ShortTypeHandler().getNullableResult(rs, 1);
            });
            assert dat.get(0) == 123;
        }
    }

    @Test
    public void testShortTypeHandler_4() throws SQLException {
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