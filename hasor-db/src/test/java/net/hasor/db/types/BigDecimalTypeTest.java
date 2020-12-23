package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.CallableSqlParameter;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.BigDecimalTypeHandler;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BigDecimalTypeTest {
    @Test
    public void testBigDecimalTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_decimal_1) values (1234567890.1234567890);");
            List<BigDecimal> dat1 = jdbcTemplate.query("select c_decimal_1 from tb_h2_types where c_decimal_1 is not null limit 1;", (rs, rowNum) -> {
                return new BigDecimalTypeHandler().getResult(rs, 1);
            });
            assert dat1.get(0).toString().equals("1234567890.1234567890");
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_decimal_2) values (1234567890.1234567890);");
            List<BigDecimal> dat2 = jdbcTemplate.query("select c_decimal_2 from tb_h2_types where c_decimal_2 is not null limit 1;", (rs, rowNum) -> {
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
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_decimal_1) values (1234567890.1234567890);");
            List<BigDecimal> dat1 = jdbcTemplate.query("select c_decimal_1 from tb_h2_types where c_decimal_1 is not null limit 1;", (rs, rowNum) -> {
                return new BigDecimalTypeHandler().getResult(rs, "c_decimal_1");
            });
            assert dat1.get(0).toString().equals("1234567890.1234567890");
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_decimal_2) values (1234567890.1234567890);");
            List<BigDecimal> dat2 = jdbcTemplate.query("select c_decimal_2 from tb_h2_types where c_decimal_2 is not null limit 1;", (rs, rowNum) -> {
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
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_decimal;");
            jdbcTemplate.execute("create procedure proc_decimal(out p_out decimal(10,2)) begin set p_out=123.123; end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_decimal(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.NUMERIC, new BigDecimalTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof BigDecimal;
            assert objectMap.get("out").equals(new BigDecimal("123.12"));
            assert objectMap.get("#update-count-1").equals(0);
        }
    }
}