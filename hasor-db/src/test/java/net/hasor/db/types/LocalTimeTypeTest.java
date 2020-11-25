package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.LocalDateTimeTypeHandler;
import net.hasor.db.types.handler.LocalDateTypeHandler;
import net.hasor.db.types.handler.LocalTimeTypeHandler;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

public class LocalTimeTypeTest {
    @Test
    public void testLocalDateTimeTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (CURRENT_TIMESTAMP(9));");
            List<LocalDateTime> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new LocalDateTimeTypeHandler().getResult(rs, 1);
            });
            //
            LocalDateTime localNow = LocalDateTime.now();
            LocalDateTime dateTime = dat.get(0);
            assert dateTime.getMonth() == localNow.getMonth();
            assert dateTime.getDayOfMonth() == localNow.getDayOfMonth();
            assert dateTime.getHour() == localNow.getHour();
            assert dateTime.getMinute() == localNow.getMinute();
            assert dateTime.getSecond() == localNow.getSecond()  //
                    || (dateTime.getSecond() + 1) == localNow.getSecond(); // UnitTest cross the seconds
        }
    }

    @Test
    public void testLocalDateTimeTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (CURRENT_TIMESTAMP(9));");
            List<LocalDateTime> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new LocalDateTimeTypeHandler().getResult(rs, "c_timestamp");
            });
            //
            LocalDateTime localNow = LocalDateTime.now();
            LocalDateTime dateTime = dat.get(0);
            assert dateTime.getMonth() == localNow.getMonth();
            assert dateTime.getDayOfMonth() == localNow.getDayOfMonth();
            assert dateTime.getHour() == localNow.getHour();
            assert dateTime.getMinute() == localNow.getMinute();
            assert dateTime.getSecond() == localNow.getSecond()  //
                    || (dateTime.getSecond() + 1) == localNow.getSecond(); // UnitTest cross the seconds
        }
    }

    @Test
    public void testLocalDateTimeTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            LocalDateTime testData = LocalDateTime.of(1998, Month.APRIL, 12, 18, 33, 20, 123);
            List<LocalDateTime> dat = jdbcTemplate.query("select ?", ps -> {
                new LocalDateTimeTypeHandler().setParameter(ps, 1, testData, JDBCType.TIMESTAMP);
            }, (rs, rowNum) -> {
                return new LocalDateTimeTypeHandler().getNullableResult(rs, 1);
            });
            //
            LocalDateTime dateTime = dat.get(0);
            assert dateTime.getMonth() == testData.getMonth();
            assert dateTime.getDayOfMonth() == testData.getDayOfMonth();
            assert dateTime.getHour() == testData.getHour();
            assert dateTime.getMinute() == testData.getMinute();
            assert dateTime.getSecond() == testData.getSecond();
            assert dateTime.getNano() == testData.getNano();
        }
    }

    @Test
    public void testLocalDateTimeTypeHandler_4() throws SQLException {
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
    public void testLocalDateTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (CURRENT_TIMESTAMP(9));");
            List<LocalDate> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new LocalDateTypeHandler().getResult(rs, 1);
            });
            //
            LocalDate localNow = LocalDate.now();
            LocalDate dateTime = dat.get(0);
            assert dateTime.getYear() == localNow.getYear();
            assert dateTime.getMonth() == localNow.getMonth();
            assert dateTime.getDayOfMonth() == localNow.getDayOfMonth();
        }
    }

    @Test
    public void testLocalDateTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (CURRENT_TIMESTAMP(9));");
            List<LocalDate> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new LocalDateTypeHandler().getResult(rs, "c_timestamp");
            });
            //
            LocalDate localNow = LocalDate.now();
            LocalDate dateTime = dat.get(0);
            assert dateTime.getYear() == localNow.getYear();
            assert dateTime.getMonth() == localNow.getMonth();
            assert dateTime.getDayOfMonth() == localNow.getDayOfMonth();
        }
    }

    @Test
    public void testLocalDateTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            LocalDate testData = LocalDate.of(1998, Month.APRIL, 12);
            List<LocalDate> dat = jdbcTemplate.query("select ?", ps -> {
                new LocalDateTypeHandler().setParameter(ps, 1, testData, JDBCType.TIMESTAMP);
            }, (rs, rowNum) -> {
                return new LocalDateTypeHandler().getNullableResult(rs, 1);
            });
            //
            LocalDate dateTime = dat.get(0);
            assert dateTime.getYear() == testData.getYear();
            assert dateTime.getMonth() == testData.getMonth();
            assert dateTime.getDayOfMonth() == testData.getDayOfMonth();
        }
    }

    @Test
    public void testLocalDateTypeHandler_4() throws SQLException {
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
    public void testLocalTimeTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (CURRENT_TIMESTAMP(9));");
            List<LocalTime> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new LocalTimeTypeHandler().getResult(rs, 1);
            });
            //
            LocalTime localNow = LocalTime.now();
            LocalTime dateTime = dat.get(0);
            assert dateTime.getHour() == localNow.getHour();
            assert dateTime.getMinute() == localNow.getMinute();
            assert dateTime.getSecond() == localNow.getSecond()  //
                    || (dateTime.getSecond() + 1) == localNow.getSecond(); // UnitTest cross the seconds
        }
    }

    @Test
    public void testLocalTimeTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (CURRENT_TIMESTAMP(9));");
            List<LocalTime> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new LocalTimeTypeHandler().getResult(rs, "c_timestamp");
            });
            //
            LocalTime localNow = LocalTime.now();
            LocalTime dateTime = dat.get(0);
            assert dateTime.getHour() == localNow.getHour();
            assert dateTime.getMinute() == localNow.getMinute();
            assert dateTime.getSecond() == localNow.getSecond()  //
                    || (dateTime.getSecond() + 1) == localNow.getSecond(); // UnitTest cross the seconds
        }
    }

    @Test
    public void testLocalTimeTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            LocalTime testData = LocalTime.of(12, 33, 45, 1243);
            List<LocalTime> dat = jdbcTemplate.query("select ?", ps -> {
                new LocalTimeTypeHandler().setParameter(ps, 1, testData, JDBCType.TIMESTAMP);
            }, (rs, rowNum) -> {
                return new LocalTimeTypeHandler().getNullableResult(rs, 1);
            });
            //
            LocalTime dateTime = dat.get(0);
            assert dateTime.getHour() == testData.getHour();
            assert dateTime.getMinute() == testData.getMinute();
            assert dateTime.getSecond() == testData.getSecond();
            assert dateTime.getNano() == testData.getNano();
        }
    }

    @Test
    public void testLocalTimeTypeHandler_4() throws SQLException {
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