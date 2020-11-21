package net.hasor.db.jdbc.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.types.handler.ClobTypeHandler;
import net.hasor.db.jdbc.types.handler.NClobTypeHandler;
import net.hasor.db.jdbc.types.handler.NStringTypeHandler;
import net.hasor.db.jdbc.types.handler.StringTypeHandler;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.List;

public class StringTypeTest {
    @Test
    public void testClobTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_clob) values ('abcdefg');");
            List<String> dat = jdbcTemplate.query("select c_clob from tb_h2types where c_clob is not null limit 1;", (rs, rowNum) -> {
                return new ClobTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0).equals("abcdefg");
        }
    }

    @Test
    public void testClobTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_clob) values ('abcdefg');");
            List<String> dat = jdbcTemplate.query("select c_clob from tb_h2types where c_clob is not null limit 1;", (rs, rowNum) -> {
                return new ClobTypeHandler().getResult(rs, "c_clob");
            });
            assert dat.get(0).equals("abcdefg");
        }
    }

    @Test
    public void testClobTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<String> dat = jdbcTemplate.query("select ?", ps -> {
                new ClobTypeHandler().setParameter(ps, 1, "abcedfg", JDBCType.CLOB);
            }, (rs, rowNum) -> {
                return new ClobTypeHandler().getNullableResult(rs, 1);
            });
            assert dat.get(0).equals("abcedfg");
        }
    }

    @Test
    public void testClobTypeHandler_4() throws SQLException {
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

    @Test
    public void testNClobTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_clob) values ('abcdefg');");
            List<String> dat = jdbcTemplate.query("select c_clob from tb_h2types where c_clob is not null limit 1;", (rs, rowNum) -> {
                return new NClobTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0).equals("abcdefg");
        }
    }

    @Test
    public void testNClobTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_clob) values ('abcdefg');");
            List<String> dat = jdbcTemplate.query("select c_clob from tb_h2types where c_clob is not null limit 1;", (rs, rowNum) -> {
                return new NClobTypeHandler().getResult(rs, "c_clob");
            });
            assert dat.get(0).equals("abcdefg");
        }
    }

    @Test
    public void testNClobTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<String> dat = jdbcTemplate.query("select ?", ps -> {
                new NClobTypeHandler().setParameter(ps, 1, "abcedfg", JDBCType.CLOB);
            }, (rs, rowNum) -> {
                return new NClobTypeHandler().getNullableResult(rs, 1);
            });
            assert dat.get(0).equals("abcedfg");
        }
    }

    @Test
    public void testNClobTypeHandler_4() throws SQLException {
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

    @Test
    public void testStringTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_text) values ('abcdefg');");
            List<String> dat = jdbcTemplate.query("select c_text from tb_h2types where c_text is not null limit 1;", (rs, rowNum) -> {
                return new StringTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0).equals("abcdefg");
        }
    }

    @Test
    public void testStringTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_text) values ('abcdefg');");
            List<String> dat = jdbcTemplate.query("select c_text from tb_h2types where c_text is not null limit 1;", (rs, rowNum) -> {
                return new StringTypeHandler().getResult(rs, "c_text");
            });
            assert dat.get(0).equals("abcdefg");
        }
    }

    @Test
    public void testStringTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            String dat1 = jdbcTemplate.queryForObject("select ?", String.class, "abcdefg");
            String dat2 = jdbcTemplate.queryForObject("select ?", String.class, (String) null);
            assert dat1.equals("abcdefg");
            assert dat2 == null;
            //
            List<String> dat = jdbcTemplate.query("select ?", ps -> {
                new StringTypeHandler().setParameter(ps, 1, "abcdefg", JDBCType.SMALLINT);
            }, (rs, rowNum) -> {
                return new StringTypeHandler().getNullableResult(rs, 1);
            });
            assert dat.get(0).equals("abcdefg");
        }
    }

    @Test
    public void testStringTypeHandler_4() throws SQLException {
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

    @Test
    public void testNStringTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_text) values ('abcdefg');");
            List<String> dat = jdbcTemplate.query("select c_text from tb_h2types where c_text is not null limit 1;", (rs, rowNum) -> {
                return new NStringTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0).equals("abcdefg");
        }
    }

    @Test
    public void testNStringTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_text) values ('abcdefg');");
            List<String> dat = jdbcTemplate.query("select c_text from tb_h2types where c_text is not null limit 1;", (rs, rowNum) -> {
                return new NStringTypeHandler().getResult(rs, "c_text");
            });
            assert dat.get(0).equals("abcdefg");
        }
    }

    @Test
    public void testNStringTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            String dat1 = jdbcTemplate.queryForObject("select ?", String.class, "abcdefg");
            String dat2 = jdbcTemplate.queryForObject("select ?", String.class, (String) null);
            assert dat1.equals("abcdefg");
            assert dat2 == null;
            //
            List<String> dat = jdbcTemplate.query("select ?", ps -> {
                new NStringTypeHandler().setParameter(ps, 1, "abcdefg", JDBCType.SMALLINT);
            }, (rs, rowNum) -> {
                return new NStringTypeHandler().getNullableResult(rs, 1);
            });
            assert dat.get(0).equals("abcdefg");
        }
    }

    @Test
    public void testNStringTypeHandler_4() throws SQLException {
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