package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.MonthDayOfNumberTypeHandler;
import net.hasor.db.types.handler.MonthDayOfStringTypeHandler;
import net.hasor.db.types.handler.MonthDayOfTimeTypeHandler;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.time.Month;
import java.time.MonthDay;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;

public class MonthDayTypeTest {
    @Test
    public void testMonthDayOfNumberTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_integer) values (0223);");
            List<MonthDay> dat = jdbcTemplate.query("select c_integer from tb_h2types where c_integer is not null limit 1;", (rs, rowNum) -> {
                return new MonthDayOfNumberTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0).getMonth() == Month.FEBRUARY;
            assert dat.get(0).getDayOfMonth() == 23;
        }
    }

    @Test
    public void testMonthDayOfNumberTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_integer) values (0223);");
            List<MonthDay> dat = jdbcTemplate.query("select c_integer from tb_h2types where c_integer is not null limit 1;", (rs, rowNum) -> {
                return new MonthDayOfNumberTypeHandler().getResult(rs, "c_integer");
            });
            assert dat.get(0).getMonth() == Month.FEBRUARY;
            assert dat.get(0).getDayOfMonth() == 23;
        }
    }

    @Test
    public void testMonthDayOfNumberTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            YearMonth dat1 = jdbcTemplate.queryForObject("select ?", YearMonth.class, YearMonth.of(2008, 2));
            assert dat1.getYear() == 2008;
            assert dat1.getMonth() == Month.FEBRUARY;
            //
            List<MonthDay> dat2 = jdbcTemplate.query("select ?", ps -> {
                new MonthDayOfNumberTypeHandler().setParameter(ps, 1, MonthDay.of(8, 2), JDBCType.SMALLINT);
            }, (rs, rowNum) -> {
                return new MonthDayOfNumberTypeHandler().getNullableResult(rs, 1);
            });
            assert dat2.get(0).getMonth() == Month.AUGUST;
            assert dat2.get(0).getDayOfMonth() == 2;
        }
    }

    @Test
    public void testMonthDayOfNumberTypeHandler_4() throws SQLException {
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
    public void testMonthDayOfStringTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('08-01');");
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('09-03');");
            List<MonthDay> dat = jdbcTemplate.query("select c_varchar from tb_h2types where c_varchar is not null limit 2;", (rs, rowNum) -> {
                return new MonthDayOfStringTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0).getMonth() == Month.AUGUST;
            assert dat.get(0).getDayOfMonth() == 1;
            assert dat.get(1).getMonth() == Month.SEPTEMBER;
            assert dat.get(1).getDayOfMonth() == 3;
        }
    }

    @Test
    public void testMonthDayOfStringTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('08-01');");
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('09-03');");
            List<MonthDay> dat = jdbcTemplate.query("select c_varchar from tb_h2types where c_varchar is not null limit 2;", (rs, rowNum) -> {
                return new MonthDayOfStringTypeHandler().getResult(rs, "c_varchar");
            });
            assert dat.get(0).getMonth() == Month.AUGUST;
            assert dat.get(0).getDayOfMonth() == 1;
            assert dat.get(1).getMonth() == Month.SEPTEMBER;
            assert dat.get(1).getDayOfMonth() == 3;
        }
    }

    @Test
    public void testMonthDayOfStringTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            MonthDay dat1 = jdbcTemplate.queryForObject("select ?", MonthDay.class, "05-01");
            assert dat1.getMonth() == Month.MAY;
            assert dat1.getDayOfMonth() == 1;
            MonthDay dat2 = jdbcTemplate.queryForObject("select ?", MonthDay.class, "12-31");
            assert dat2.getMonth() == Month.DECEMBER;
            assert dat2.getDayOfMonth() == 31;
            //
            List<MonthDay> dat3 = jdbcTemplate.query("select ?", ps -> {
                new MonthDayOfStringTypeHandler().setParameter(ps, 1, MonthDay.of(Month.FEBRUARY, 26), JDBCType.SMALLINT);
            }, (rs, rowNum) -> {
                return new MonthDayOfStringTypeHandler().getNullableResult(rs, 1);
            });
            assert dat3.get(0).getMonth() == Month.FEBRUARY;
            assert dat3.get(0).getDayOfMonth() == 26;
        }
    }

    @Test
    public void testMonthDayOfStringTypeHandler_4() throws SQLException {
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
    public void testMonthDayOfTimeTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (CURRENT_TIMESTAMP(9));");
            List<MonthDay> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new MonthDayOfTimeTypeHandler().getResult(rs, 1);
            });
            MonthDay monthDay = MonthDay.now();
            assert dat.get(0).getMonth() == monthDay.getMonth();
            assert dat.get(0).getDayOfMonth() == monthDay.getDayOfMonth();
        }
    }

    @Test
    public void testMonthDayOfTimeTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (CURRENT_TIMESTAMP(9));");
            List<MonthDay> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new MonthDayOfTimeTypeHandler().getResult(rs, "c_timestamp");
            });
            MonthDay monthDay = MonthDay.now();
            assert dat.get(0).getMonth() == monthDay.getMonth();
            assert dat.get(0).getDayOfMonth() == monthDay.getDayOfMonth();
        }
    }

    @Test
    public void testMonthDayOfTimeTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            //
            MonthDay dat1 = jdbcTemplate.queryForObject("select ?", MonthDay.class, new Date());
            MonthDay monthDay = MonthDay.now();
            assert dat1.getMonth() == monthDay.getMonth();
            assert dat1.getDayOfMonth() == monthDay.getDayOfMonth();
            //
            //
            List<MonthDay> dat2 = jdbcTemplate.query("select ?", ps -> {
                new MonthDayOfTimeTypeHandler().setParameter(ps, 1, MonthDay.of(Month.APRIL, 23), JDBCType.TIMESTAMP);
            }, (rs, rowNum) -> {
                return new MonthDayOfTimeTypeHandler().getNullableResult(rs, 1);
            });
            assert dat2.get(0).getMonth() == Month.APRIL;
            assert dat2.get(0).getDayOfMonth() == 23;
            //
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", ps -> {
                new MonthDayOfTimeTypeHandler().setParameter(ps, 1, MonthDay.of(Month.APRIL, 23), JDBCType.TIMESTAMP);
            });
            MonthDay dat3 = jdbcTemplate.queryForObject("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", MonthDay.class);
            assert dat3.getMonth() == Month.APRIL;
            assert dat3.getDayOfMonth() == 23;
        }
    }

    @Test
    public void testMonthDayOfTimeTypeHandler_4() throws SQLException {
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