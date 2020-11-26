package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.OffsetDateTimeForSqlTypeHandler;
import net.hasor.db.types.handler.OffsetDateTimeForUTCTypeHandler;
import net.hasor.db.types.handler.OffsetTimeForSqlTypeHandler;
import net.hasor.db.types.handler.OffsetTimeForUTCTypeHandler;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.time.*;
import java.util.List;

public class OffsetTimeTypeTest {
    @Test
    public void testOffsetDateTimeForSqlTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp_z) values ('1998-04-12T18:33:20.000000123+08:00');");
            List<OffsetDateTime> dat = jdbcTemplate.query("select c_timestamp_z from tb_h2types where c_timestamp_z is not null limit 1;", (rs, rowNum) -> {
                return new OffsetDateTimeForSqlTypeHandler().getResult(rs, 1);
            });
            //
            OffsetDateTime dateTime = dat.get(0);
            OffsetDateTime testData = LocalDateTime.of(1998, Month.APRIL, 12, 18, 33, 20, 123)//
                    .atOffset(ZoneOffset.ofHours(8));
            //
            assert dateTime.getOffset().getId().equals(testData.getOffset().getId());
            assert dateTime.getMonth() == testData.getMonth();
            assert dateTime.getDayOfMonth() == testData.getDayOfMonth();
            assert dateTime.getHour() == testData.getHour();
            assert dateTime.getMinute() == testData.getMinute();
            assert dateTime.getSecond() == testData.getSecond();
            assert dateTime.getNano() == testData.getNano();
        }
    }

    @Test
    public void testOffsetDateTimeForSqlTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp_z) values ('1998-04-12T18:33:20.000000123+08:00');");
            List<OffsetDateTime> dat = jdbcTemplate.query("select c_timestamp_z from tb_h2types where c_timestamp_z is not null limit 1;", (rs, rowNum) -> {
                return new OffsetDateTimeForSqlTypeHandler().getResult(rs, "c_timestamp_z");
            });
            //
            OffsetDateTime dateTime = dat.get(0);
            OffsetDateTime testData = LocalDateTime.of(1998, Month.APRIL, 12, 18, 33, 20, 123)//
                    .atOffset(ZoneOffset.ofHours(8));
            //
            assert dateTime.getOffset().getId().equals(testData.getOffset().getId());
            assert dateTime.getMonth() == testData.getMonth();
            assert dateTime.getDayOfMonth() == testData.getDayOfMonth();
            assert dateTime.getHour() == testData.getHour();
            assert dateTime.getMinute() == testData.getMinute();
            assert dateTime.getSecond() == testData.getSecond();
            assert dateTime.getNano() == testData.getNano();
        }
    }

    @Test
    public void testOffsetDateTimeForSqlTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            OffsetDateTime testData = LocalDateTime.of(1998, Month.APRIL, 12, 18, 33, 20, 123)//
                    .atOffset(ZoneOffset.ofHours(8));
            //
            List<OffsetDateTime> dat = jdbcTemplate.query("select ?", ps -> {
                new OffsetDateTimeForSqlTypeHandler().setParameter(ps, 1, testData, JDBCType.TIMESTAMP_WITH_TIMEZONE);
            }, (rs, rowNum) -> {
                return new OffsetDateTimeForSqlTypeHandler().getNullableResult(rs, 1);
            });
            OffsetDateTime dateTime = dat.get(0);
            //
            assert dateTime.getOffset().getId().equals(testData.getOffset().getId());
            assert dateTime.getMonth() == testData.getMonth();
            assert dateTime.getDayOfMonth() == testData.getDayOfMonth();
            assert dateTime.getHour() == testData.getHour();
            assert dateTime.getMinute() == testData.getMinute();
            assert dateTime.getSecond() == testData.getSecond();
            assert dateTime.getNano() == testData.getNano();
        }
    }

    @Test
    public void testOffsetDateTimeForSqlTypeHandler_4() throws SQLException {
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
    public void testOffsetDateTimeForUTCTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp_z) values ('1998-04-12T18:33:20.000000123+08:00');");
            List<OffsetDateTime> dat = jdbcTemplate.query("select c_timestamp_z from tb_h2types where c_timestamp_z is not null limit 1;", (rs, rowNum) -> {
                return new OffsetDateTimeForUTCTypeHandler().getResult(rs, 1);
            });
            OffsetDateTime dateTime = dat.get(0);
            ZonedDateTime utcTime = LocalDateTime.of(1998, Month.APRIL, 12, 18, 33, 20, 123)//
                    .atOffset(ZoneOffset.ofHours(8))//
                    .atZoneSameInstant(ZoneOffset.UTC);
            //
            assert dateTime.getOffset().getId().equals(utcTime.getOffset().getId());
            assert dateTime.getMonth() == utcTime.getMonth();
            assert dateTime.getDayOfMonth() == utcTime.getDayOfMonth();
            assert dateTime.getHour() == utcTime.getHour();
            assert dateTime.getMinute() == utcTime.getMinute();
            assert dateTime.getSecond() == utcTime.getSecond();
            assert dateTime.getNano() == utcTime.getNano();
        }
    }

    @Test
    public void testOffsetDateTimeForUTCTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp_z) values ('1998-04-12T18:33:20.000000123+08:00');");
            List<OffsetDateTime> dat = jdbcTemplate.query("select c_timestamp_z from tb_h2types where c_timestamp_z is not null limit 1;", (rs, rowNum) -> {
                return new OffsetDateTimeForUTCTypeHandler().getResult(rs, "c_timestamp_z");
            });
            OffsetDateTime dateTime = dat.get(0);
            ZonedDateTime utcTime = LocalDateTime.of(1998, Month.APRIL, 12, 18, 33, 20, 123)//
                    .atOffset(ZoneOffset.ofHours(8))//
                    .atZoneSameInstant(ZoneOffset.UTC);
            //
            assert dateTime.getOffset().getId().equals(utcTime.getOffset().getId());
            assert dateTime.getMonth() == utcTime.getMonth();
            assert dateTime.getDayOfMonth() == utcTime.getDayOfMonth();
            assert dateTime.getHour() == utcTime.getHour();
            assert dateTime.getMinute() == utcTime.getMinute();
            assert dateTime.getSecond() == utcTime.getSecond();
            assert dateTime.getNano() == utcTime.getNano();
        }
    }

    @Test
    public void testOffsetDateTimeForUTCTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            OffsetDateTime argOffsetTime = LocalDateTime.of(1998, Month.APRIL, 12, 18, 33, 20, 123)//
                    .atOffset(ZoneOffset.ofHours(8));
            //
            List<OffsetDateTime> dat = jdbcTemplate.query("select ?", ps -> {
                new OffsetDateTimeForUTCTypeHandler().setParameter(ps, 1, argOffsetTime, JDBCType.TIMESTAMP_WITH_TIMEZONE);
            }, (rs, rowNum) -> {
                return new OffsetDateTimeForUTCTypeHandler().getNullableResult(rs, 1);
            });
            OffsetDateTime dateTime = dat.get(0);
            ZonedDateTime testTime = argOffsetTime.atZoneSameInstant(ZoneOffset.UTC);
            //
            assert dateTime.getOffset().getId().equals(testTime.getOffset().getId());
            assert dateTime.getMonth() == testTime.getMonth();
            assert dateTime.getDayOfMonth() == testTime.getDayOfMonth();
            assert dateTime.getHour() == testTime.getHour();
            assert dateTime.getMinute() == testTime.getMinute();
            assert dateTime.getSecond() == testTime.getSecond();
            assert dateTime.getNano() == testTime.getNano();
        }
    }

    @Test
    public void testOffsetDateTimeForUTCTypeHandler_4() throws SQLException {
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
    public void testOffsetTimeForSqlTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp_z) values ('1998-04-12T18:33:20.000000123+08:00');");
            List<OffsetTime> dat = jdbcTemplate.query("select c_timestamp_z from tb_h2types where c_timestamp_z is not null limit 1;", (rs, rowNum) -> {
                return new OffsetTimeForSqlTypeHandler().getResult(rs, 1);
            });
            OffsetTime dateTime = dat.get(0);
            OffsetTime localTime = LocalTime.of(18, 33, 20, 123).atOffset(ZoneOffset.ofHours(8));
            //
            assert dateTime.getOffset().getId().equals(localTime.getOffset().getId());
            assert dateTime.getHour() == localTime.getHour();
            assert dateTime.getMinute() == localTime.getMinute();
            assert dateTime.getSecond() == localTime.getSecond();
            assert dateTime.getNano() == localTime.getNano();
        }
    }

    @Test
    public void testOffsetTimeForSqlTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp_z) values ('1998-04-12T18:33:20.000000123+08:00');");
            List<OffsetTime> dat = jdbcTemplate.query("select c_timestamp_z from tb_h2types where c_timestamp_z is not null limit 1;", (rs, rowNum) -> {
                return new OffsetTimeForSqlTypeHandler().getResult(rs, "c_timestamp_z");
            });
            OffsetTime dateTime = dat.get(0);
            OffsetTime localTime = LocalTime.of(18, 33, 20, 123).atOffset(ZoneOffset.ofHours(8));
            //
            assert dateTime.getOffset().getId().equals(localTime.getOffset().getId());
            assert dateTime.getHour() == localTime.getHour();
            assert dateTime.getMinute() == localTime.getMinute();
            assert dateTime.getSecond() == localTime.getSecond();
            assert dateTime.getNano() == localTime.getNano();
        }
    }

    @Test
    public void testOffsetTimeForSqlTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            OffsetTime localTime = LocalTime.of(18, 33, 20, 123).atOffset(ZoneOffset.ofHours(8));
            //
            List<OffsetTime> dat = jdbcTemplate.query("select ?", ps -> {
                new OffsetTimeForSqlTypeHandler().setParameter(ps, 1, localTime, JDBCType.TIMESTAMP_WITH_TIMEZONE);
            }, (rs, rowNum) -> {
                return new OffsetTimeForSqlTypeHandler().getNullableResult(rs, 1);
            });
            //
            OffsetTime dateTime = dat.get(0);
            assert dateTime.getOffset().getId().equals(localTime.getOffset().getId());
            assert dateTime.getHour() == localTime.getHour();
            assert dateTime.getMinute() == localTime.getMinute();
            assert dateTime.getSecond() == localTime.getSecond();
            assert dateTime.getNano() == localTime.getNano();
        }
    }

    @Test
    public void testOffsetTimeForSqlTypeHandler_4() throws SQLException {
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
    public void testOffsetTimeForUTCTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp_z) values ('1998-04-12T18:33:20.000000123+08:00');");
            List<OffsetTime> dat = jdbcTemplate.query("select c_timestamp_z from tb_h2types where c_timestamp_z is not null limit 1;", (rs, rowNum) -> {
                return new OffsetTimeForUTCTypeHandler().getResult(rs, 1);
            });
            OffsetTime dateTime = dat.get(0);
            OffsetTime localTime = LocalTime.of(18, 33, 20, 123)//
                    .atOffset(ZoneOffset.ofHours(8))//
                    .withOffsetSameLocal(ZoneOffset.UTC);
            //
            assert dateTime.getOffset().getId().equals(localTime.getOffset().getId());
            assert dateTime.getHour() == localTime.getHour();
            assert dateTime.getMinute() == localTime.getMinute();
            assert dateTime.getSecond() == localTime.getSecond();
            assert dateTime.getNano() == localTime.getNano();
        }
    }

    @Test
    public void testOffsetTimeForUTCTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp_z) values ('1998-04-12T18:33:20.000000123+08:00');");
            List<OffsetTime> dat = jdbcTemplate.query("select c_timestamp_z from tb_h2types where c_timestamp_z is not null limit 1;", (rs, rowNum) -> {
                return new OffsetTimeForUTCTypeHandler().getResult(rs, "c_timestamp_z");
            });
            OffsetTime dateTime = dat.get(0);
            OffsetTime localTime = LocalTime.of(18, 33, 20, 123)//
                    .atOffset(ZoneOffset.ofHours(8))//
                    .withOffsetSameLocal(ZoneOffset.UTC);
            //
            assert dateTime.getOffset().getId().equals(localTime.getOffset().getId());
            assert dateTime.getHour() == localTime.getHour();
            assert dateTime.getMinute() == localTime.getMinute();
            assert dateTime.getSecond() == localTime.getSecond();
            assert dateTime.getNano() == localTime.getNano();
        }
    }

    @Test
    public void testOffsetTimeForUTCTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            OffsetTime localTime = LocalTime.of(18, 33, 20, 123).atOffset(ZoneOffset.ofHours(8));//
            //
            List<OffsetTime> dat = jdbcTemplate.query("select ?", ps -> {
                new OffsetTimeForUTCTypeHandler().setParameter(ps, 1, localTime, JDBCType.TIMESTAMP_WITH_TIMEZONE);
            }, (rs, rowNum) -> {
                return new OffsetTimeForUTCTypeHandler().getNullableResult(rs, 1);
            });
            //
            OffsetTime dateTime = dat.get(0);
            OffsetTime testTime = localTime.withOffsetSameLocal(ZoneOffset.UTC);
            //
            assert dateTime.getOffset().getId().equals(testTime.getOffset().getId());
            assert dateTime.getHour() == testTime.getHour();
            assert dateTime.getMinute() == testTime.getMinute();
            assert dateTime.getSecond() == testTime.getSecond();
            assert dateTime.getNano() == testTime.getNano();
        }
    }

    @Test
    public void testOffsetTimeForUTCTypeHandler_4() throws SQLException {
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