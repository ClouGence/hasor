package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.CallableSqlParameter;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.BigIntegerTypeHandler;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_bigint;");
            jdbcTemplate.execute("create procedure proc_bigint(out p_out bigint) begin set p_out=123123; end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_bigint(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.BIGINT, new BigIntegerTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof BigInteger;
            assert objectMap.get("out").equals(new BigInteger("123123"));
            assert objectMap.get("#update-count-1").equals(0);
        }
    }
}