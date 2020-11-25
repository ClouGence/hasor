package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.ByteTypeHandler;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.List;

public class ByteTypeTest {
    @Test
    public void testByteTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_tinyint) values (123);");
            List<Byte> dat = jdbcTemplate.query("select c_tinyint from tb_h2types where c_tinyint is not null limit 1;", (rs, rowNum) -> {
                return new ByteTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0) == 123;
        }
    }

    @Test
    public void testByteTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_tinyint) values (123);");
            List<Byte> dat = jdbcTemplate.query("select c_tinyint from tb_h2types where c_tinyint is not null limit 1;", (rs, rowNum) -> {
                return new ByteTypeHandler().getResult(rs, "c_tinyint");
            });
            assert dat.get(0) == 123;
        }
    }

    @Test
    public void testByteTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            byte dat1 = jdbcTemplate.queryForObject("select ?", byte.class, 12);
            Byte dat2 = jdbcTemplate.queryForObject("select ?", Byte.class, 34);
            assert dat1 == 12;
            assert dat2 == 34;
            //
            List<Byte> dat = jdbcTemplate.query("select ?", ps -> {
                new ByteTypeHandler().setParameter(ps, 1, (byte) 123, JDBCType.SMALLINT);
            }, (rs, rowNum) -> {
                return new ByteTypeHandler().getNullableResult(rs, 1);
            });
            assert dat.get(0) == 123;
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