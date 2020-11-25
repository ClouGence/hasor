package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.BigDecimalTypeHandler;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.List;

public class BigDecimalTypeTest {
    @Test
    public void testBigDecimalTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_decimal) values (1234567890.1234567890);");
            List<BigDecimal> dat1 = jdbcTemplate.query("select c_decimal from tb_h2types where c_decimal is not null limit 1;", (rs, rowNum) -> {
                return new BigDecimalTypeHandler().getResult(rs, 1);
            });
            assert dat1.get(0).toString().equals("1234567890.1234567890");
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_decimal_2) values (1234567890.1234567890);");
            List<BigDecimal> dat2 = jdbcTemplate.query("select c_decimal_2 from tb_h2types where c_decimal_2 is not null limit 1;", (rs, rowNum) -> {
                return new BigDecimalTypeHandler().getResult(rs, 1);
            });
            assert dat2.get(0).toString().equals("1234567890.12");
        }
    }

    @Test
    public void testBigDecimalTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_decimal) values (1234567890.1234567890);");
            List<BigDecimal> dat1 = jdbcTemplate.query("select c_decimal from tb_h2types where c_decimal is not null limit 1;", (rs, rowNum) -> {
                return new BigDecimalTypeHandler().getResult(rs, "c_decimal");
            });
            assert dat1.get(0).toString().equals("1234567890.1234567890");
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_decimal_2) values (1234567890.1234567890);");
            List<BigDecimal> dat2 = jdbcTemplate.query("select c_decimal_2 from tb_h2types where c_decimal_2 is not null limit 1;", (rs, rowNum) -> {
                return new BigDecimalTypeHandler().getResult(rs, "c_decimal_2");
            });
            assert dat2.get(0).toString().equals("1234567890.12");
        }
    }

    @Test
    public void testBigDecimalTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<BigDecimal> dat = jdbcTemplate.query("select ?", ps -> {
                new BigDecimalTypeHandler().setParameter(ps, 1, new BigDecimal("1234567890.1234567890"), JDBCType.DECIMAL);
            }, (rs, rowNum) -> {
                return new BigDecimalTypeHandler().getNullableResult(rs, 1);
            });
            assert dat.get(0).toString().equals("1234567890.1234567890");
        }
    }

    @Test
    public void testBigDecimalTypeHandler_4() throws SQLException {
        //        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
        //            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        //            //
        //            jdbcTemplate.executeUpdate("CREATE ALIAS AS_BIGINTEGER FOR \"net.hasor.test.db.CallableFunction.asBigInteger\";");
        //            BigDecimal bigDecimal = jdbcTemplate.execute("call AS_BIGINTEGER(?)", (CallableStatementCallback<BigDecimal>) cs -> {
        //                cs.ge
        //                return null;
        //            });
        //            assert bigDecimal.intValue() == 123;
        //        }
    }
}