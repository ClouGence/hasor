package net.hasor.db.jdbc.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.types.handler.MonthOfNumberTypeHandler;
import net.hasor.db.jdbc.types.handler.MonthOfStringTypeHandler;
import net.hasor.db.jdbc.types.handler.MonthOfTimeTypeHandler;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.time.Month;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MonthTypeTest {
    @Test
    public void testMonthOfNumberTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_tinyint) values (05);");
            List<Month> dat = jdbcTemplate.query("select c_tinyint from tb_h2types where c_tinyint is not null limit 1;", (rs, rowNum) -> {
                return new MonthOfNumberTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0) == Month.MAY;
        }
    }

    @Test
    public void testMonthOfNumberTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_tinyint) values (05);");
            List<Month> dat = jdbcTemplate.query("select c_tinyint from tb_h2types where c_tinyint is not null limit 1;", (rs, rowNum) -> {
                return new MonthOfNumberTypeHandler().getResult(rs, "c_tinyint");
            });
            assert dat.get(0) == Month.MAY;
        }
    }

    @Test
    public void testMonthOfNumberTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Month dat1 = jdbcTemplate.queryForObject("select ?", Month.class, 5);
            assert dat1 == Month.MAY;
            //
            List<Month> dat2 = jdbcTemplate.query("select ?", ps -> {
                new MonthOfNumberTypeHandler().setParameter(ps, 1, Month.MAY, JDBCType.SMALLINT);
            }, (rs, rowNum) -> {
                return new MonthOfNumberTypeHandler().getNullableResult(rs, 1);
            });
            assert dat2.get(0) == Month.MAY;
        }
    }

    @Test
    public void testMonthOfNumberTypeHandler_4() throws SQLException {
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
    public void testMonthOfStringTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('05');");
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('may');");
            List<Month> dat = jdbcTemplate.query("select c_varchar from tb_h2types where c_varchar is not null limit 2;", (rs, rowNum) -> {
                return new MonthOfStringTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0) == Month.MAY;
            assert dat.get(1) == Month.MAY;
        }
    }

    @Test
    public void testMonthOfStringTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('05');");
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('may');");
            List<Month> dat = jdbcTemplate.query("select c_varchar from tb_h2types where c_varchar is not null limit 2;", (rs, rowNum) -> {
                return new MonthOfStringTypeHandler().getResult(rs, "c_varchar");
            });
            assert dat.get(0) == Month.MAY;
            assert dat.get(1) == Month.MAY;
        }
    }

    @Test
    public void testMonthOfStringTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Month dat1 = jdbcTemplate.queryForObject("select ?", Month.class, "5");
            assert dat1 == Month.MAY;
            Month dat2 = jdbcTemplate.queryForObject("select ?", Month.class, "may");
            assert dat2 == Month.MAY;
            //
            List<Month> dat3 = jdbcTemplate.query("select ?", ps -> {
                new MonthOfStringTypeHandler().setParameter(ps, 1, Month.MAY, JDBCType.SMALLINT);
            }, (rs, rowNum) -> {
                return new MonthOfStringTypeHandler().getNullableResult(rs, 1);
            });
            assert dat3.get(0) == Month.MAY;
        }
    }

    @Test
    public void testMonthOfStringTypeHandler_4() throws SQLException {
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
    public void testMonthOfTimeTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (CURRENT_TIMESTAMP(9));");
            List<Month> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new MonthOfTimeTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0) == YearMonth.now().getMonth();
        }
    }

    @Test
    public void testMonthOfTimeTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (CURRENT_TIMESTAMP(9));");
            List<Month> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new MonthOfTimeTypeHandler().getResult(rs, "c_timestamp");
            });
            assert dat.get(0) == YearMonth.now().getMonth();
        }
    }

    @Test
    public void testMonthOfTimeTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            //
            Month dat1 = jdbcTemplate.queryForObject("select ?", Month.class, new Date());
            assert dat1 == YearMonth.now().getMonth();
            //
            //
            List<Month> dat2 = jdbcTemplate.query("select ?", ps -> {
                new MonthOfTimeTypeHandler().setParameter(ps, 1, Month.MAY, JDBCType.TIMESTAMP);
            }, (rs, rowNum) -> {
                return new MonthOfTimeTypeHandler().getNullableResult(rs, 1);
            });
            assert dat2.get(0) == Month.MAY;
            //
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", ps -> {
                new MonthOfTimeTypeHandler().setParameter(ps, 1, Month.MAY, JDBCType.TIMESTAMP);
            });
            Date dat = jdbcTemplate.queryForObject("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", Date.class);
            Calendar instance = Calendar.getInstance();
            instance.setTime(dat);
            int month = instance.get(Calendar.MONTH);
            assert month == Calendar.MAY;
        }
    }

    @Test
    public void testMonthOfTimeTypeHandler_4() throws SQLException {
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