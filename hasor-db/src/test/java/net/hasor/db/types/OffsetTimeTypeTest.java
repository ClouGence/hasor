package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.CallableSqlParameter;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.*;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.time.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OffsetTimeTypeTest {
    @Test
    public void testOffsetDateTimeForSqlTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp_z) values ('1998-04-12T18:33:20.000000123+08:00');");
            List<OffsetDateTime> dat = jdbcTemplate.query("select c_timestamp_z from tb_h2_types where c_timestamp_z is not null limit 1;", (rs, rowNum) -> {
                return new OffsetDateTimeForSqlTypeHandler().getResult(rs, 1);
            });
            //
            OffsetDateTime dateTime = dat.get(0);
            OffsetDateTime testData = LocalDateTime.of(1998, Month.APRIL, 12, 18, 33, 20, 123)//
                    .atOffset(ZoneOffset.ofHours(8));
            //
            assert dateTime.getOffset().getId().equals(testData.getOffset().getId());
            assert dateTime.getYear() == testData.getYear();
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
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp_z) values ('1998-04-12T18:33:20.000000123+08:00');");
            List<OffsetDateTime> dat = jdbcTemplate.query("select c_timestamp_z from tb_h2_types where c_timestamp_z is not null limit 1;", (rs, rowNum) -> {
                return new OffsetDateTimeForSqlTypeHandler().getResult(rs, "c_timestamp_z");
            });
            //
            OffsetDateTime dateTime = dat.get(0);
            OffsetDateTime testData = LocalDateTime.of(1998, Month.APRIL, 12, 18, 33, 20, 123)//
                    .atOffset(ZoneOffset.ofHours(8));
            //
            assert dateTime.getOffset().getId().equals(testData.getOffset().getId());
            assert dateTime.getYear() == testData.getYear();
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
            assert dateTime.getYear() == testData.getYear();
            assert dateTime.getMonth() == testData.getMonth();
            assert dateTime.getDayOfMonth() == testData.getDayOfMonth();
            assert dateTime.getHour() == testData.getHour();
            assert dateTime.getMinute() == testData.getMinute();
            assert dateTime.getSecond() == testData.getSecond();
            assert dateTime.getNano() == testData.getNano();
        }
    }

    @Test
    public void testOffsetDateTimeForSqlTypeHandler_4() throws Exception {
        try (Connection conn = DsUtils.localOracle()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute(""//
                    + "create or replace procedure proc_timestamptz(p_out out timestamp with time zone)\n" //
                    + "AS\n" //
                    + "BEGIN\n"//
                    + "  p_out := to_timestamp_tz('2013-10-15T17:18:28-06:00','YYYY-MM-DD\"T\"HH24:MI:SSTZH:TZM');\n" //
                    + "END;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_timestamptz(?)}",//
                    Collections.singletonList(CallableSqlParameter.withInOut("out", null, JDBCType.TIMESTAMP_WITH_TIMEZONE, new OffsetDateTimeForSqlTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof OffsetDateTime;
            assert objectMap.get("out").toString().equals("2013-10-15T17:18:28-06:00");
        }
    }

    @Test
    public void testOffsetDateTimeForUTCTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp_z) values ('1998-04-12T18:33:20.000000123+08:00');");
            List<OffsetDateTime> dat = jdbcTemplate.query("select c_timestamp_z from tb_h2_types where c_timestamp_z is not null limit 1;", (rs, rowNum) -> {
                return new OffsetDateTimeForUTCTypeHandler().getResult(rs, 1);
            });
            OffsetDateTime dateTime = dat.get(0);
            ZonedDateTime utcTime = LocalDateTime.of(1998, Month.APRIL, 12, 18, 33, 20, 123)//
                    .atOffset(ZoneOffset.ofHours(8))//
                    .atZoneSameInstant(ZoneOffset.UTC);
            //
            assert dateTime.getOffset().getId().equals(utcTime.getOffset().getId());
            assert dateTime.getYear() == utcTime.getYear();
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
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp_z) values ('1998-04-12T18:33:20.000000123+08:00');");
            List<OffsetDateTime> dat = jdbcTemplate.query("select c_timestamp_z from tb_h2_types where c_timestamp_z is not null limit 1;", (rs, rowNum) -> {
                return new OffsetDateTimeForUTCTypeHandler().getResult(rs, "c_timestamp_z");
            });
            OffsetDateTime dateTime = dat.get(0);
            ZonedDateTime utcTime = LocalDateTime.of(1998, Month.APRIL, 12, 18, 33, 20, 123)//
                    .atOffset(ZoneOffset.ofHours(8))//
                    .atZoneSameInstant(ZoneOffset.UTC);
            //
            assert dateTime.getOffset().getId().equals(utcTime.getOffset().getId());
            assert dateTime.getYear() == utcTime.getYear();
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
            assert dateTime.getYear() == testTime.getYear();
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
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_timestamp;");
            jdbcTemplate.execute("create procedure proc_timestamp(out p_out timestamp) begin set p_out= str_to_date('2008-08-09 08:09:30', '%Y-%m-%d %h:%i:%s'); end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_timestamp(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.TIMESTAMP, new OffsetDateTimeForUTCTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof OffsetDateTime;
            ZonedDateTime testTime = LocalDateTime.of(2008, Month.AUGUST, 9, 8, 9, 30)//
                    .atOffset(ZoneOffset.ofHours(8))//
                    .atZoneSameInstant(ZoneOffset.UTC);
            OffsetDateTime dateTime = (OffsetDateTime) objectMap.get("out");
            assert dateTime.getOffset().getId().equals(testTime.getOffset().getId());
            assert dateTime.getYear() == testTime.getYear();
            assert dateTime.getMonth() == testTime.getMonth();
            assert dateTime.getDayOfMonth() == testTime.getDayOfMonth();
            assert dateTime.getHour() == testTime.getHour();
            assert dateTime.getMinute() == testTime.getMinute();
            assert dateTime.getSecond() == testTime.getSecond();
            assert dateTime.getNano() == testTime.getNano();
        }
    }

    @Test
    public void testOffsetTimeForSqlTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp_z) values ('1998-04-12T18:33:20.000000123+08:00');");
            List<OffsetTime> dat = jdbcTemplate.query("select c_timestamp_z from tb_h2_types where c_timestamp_z is not null limit 1;", (rs, rowNum) -> {
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
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp_z) values ('1998-04-12T18:33:20.000000123+08:00');");
            List<OffsetTime> dat = jdbcTemplate.query("select c_timestamp_z from tb_h2_types where c_timestamp_z is not null limit 1;", (rs, rowNum) -> {
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
        try (Connection conn = DsUtils.localOracle()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute(""//
                    + "create or replace procedure proc_timestamptz(p_out out timestamp with time zone)\n" //
                    + "AS\n" //
                    + "BEGIN\n"//
                    + "  p_out := to_timestamp_tz('2013-10-15T17:18:28-06:00','YYYY-MM-DD\"T\"HH24:MI:SSTZH:TZM');\n" //
                    + "END;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_timestamptz(?)}",//
                    Collections.singletonList(CallableSqlParameter.withInOut("out", null, JDBCType.TIMESTAMP_WITH_TIMEZONE, new OffsetTimeForSqlTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof OffsetTime;
            assert objectMap.get("out").toString().equals("17:18:28-06:00");
        }
    }

    @Test
    public void testOffsetTimeForUTCTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp_z) values ('1998-04-12T18:33:20.000000123+08:00');");
            List<OffsetTime> dat = jdbcTemplate.query("select c_timestamp_z from tb_h2_types where c_timestamp_z is not null limit 1;", (rs, rowNum) -> {
                return new OffsetTimeForUTCTypeHandler().getResult(rs, 1);
            });
            OffsetTime dateTime = dat.get(0);
            OffsetTime localTime = LocalTime.of(18, 33, 20, 123)//
                    .atOffset(ZoneOffset.ofHours(8))    //
                    .atDate(LocalDate.ofEpochDay(0))    //
                    .atZoneSameInstant(ZoneOffset.UTC)  //
                    .toOffsetDateTime()                 //
                    .toOffsetTime();
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
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp_z) values ('1998-04-12T18:33:20.000000123+08:00');");
            List<OffsetTime> dat = jdbcTemplate.query("select c_timestamp_z from tb_h2_types where c_timestamp_z is not null limit 1;", (rs, rowNum) -> {
                return new OffsetTimeForUTCTypeHandler().getResult(rs, "c_timestamp_z");
            });
            OffsetTime dateTime = dat.get(0);
            OffsetTime localTime = LocalTime.of(18, 33, 20, 123)//
                    .atOffset(ZoneOffset.ofHours(8))    //
                    .atDate(LocalDate.ofEpochDay(0))    //
                    .atZoneSameInstant(ZoneOffset.UTC)  //
                    .toOffsetDateTime()                 //
                    .toOffsetTime();
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
            OffsetTime testTime = localTime             //
                    .atDate(LocalDate.ofEpochDay(0))    //
                    .atZoneSameInstant(ZoneOffset.UTC)  //
                    .toOffsetDateTime()                 //
                    .toOffsetTime();
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
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_timestamp;");
            jdbcTemplate.execute("create procedure proc_timestamp(out p_out timestamp) begin set p_out= str_to_date('2008-08-09 08:09:30', '%Y-%m-%d %h:%i:%s'); end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_timestamp(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.TIMESTAMP, new OffsetTimeForUTCTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof OffsetTime;
            ZonedDateTime testTime = LocalDateTime.of(2008, Month.AUGUST, 9, 8, 9, 30)//
                    .atOffset(ZoneOffset.ofHours(8))//
                    .atZoneSameInstant(ZoneOffset.UTC);
            OffsetTime dateTime = (OffsetTime) objectMap.get("out");
            assert dateTime.getOffset().getId().equals(testTime.getOffset().getId());
            assert dateTime.getHour() == testTime.getHour();
            assert dateTime.getMinute() == testTime.getMinute();
            assert dateTime.getSecond() == testTime.getSecond();
            assert dateTime.getNano() == testTime.getNano();
        }
    }

    @Test
    public void testZonedDateTimeTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp_z) values ('1998-04-12T18:33:20.000000123+08:00');");
            List<ZonedDateTime> dat = jdbcTemplate.query("select c_timestamp_z from tb_h2_types where c_timestamp_z is not null limit 1;", (rs, rowNum) -> {
                return new ZonedDateTimeTypeHandler().getResult(rs, 1);
            });
            ZonedDateTime dateTime = dat.get(0);
            OffsetDateTime localTime = LocalDateTime.of(1998, 4, 12, 18, 33, 20, 123)//
                    .atOffset(ZoneOffset.ofHours(8));
            //
            assert dateTime.getOffset().getId().equals(localTime.getOffset().getId());
            assert dateTime.getHour() == localTime.getHour();
            assert dateTime.getMinute() == localTime.getMinute();
            assert dateTime.getSecond() == localTime.getSecond();
            assert dateTime.getNano() == localTime.getNano();
        }
    }

    @Test
    public void testZonedDateTimeTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp_z) values ('1998-04-12T18:33:20.000000123+08:00');");
            List<ZonedDateTime> dat = jdbcTemplate.query("select c_timestamp_z from tb_h2_types where c_timestamp_z is not null limit 1;", (rs, rowNum) -> {
                return new ZonedDateTimeTypeHandler().getResult(rs, "c_timestamp_z");
            });
            ZonedDateTime dateTime = dat.get(0);
            OffsetDateTime localTime = LocalDateTime.of(1998, 4, 12, 18, 33, 20, 123)//
                    .atOffset(ZoneOffset.ofHours(8));
            //
            assert dateTime.getOffset().getId().equals(localTime.getOffset().getId());
            assert dateTime.getHour() == localTime.getHour();
            assert dateTime.getMinute() == localTime.getMinute();
            assert dateTime.getSecond() == localTime.getSecond();
            assert dateTime.getNano() == localTime.getNano();
        }
    }

    @Test
    public void testZonedDateTimeTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            ZonedDateTime localTime = ZonedDateTime.of(//
                    LocalDate.of(1998, 4, 12),//
                    LocalTime.of(18, 33, 20, 123),//
                    ZoneOffset.ofHours(8));
            //
            List<ZonedDateTime> dat = jdbcTemplate.query("select ?", ps -> {
                new ZonedDateTimeTypeHandler().setParameter(ps, 1, localTime, JDBCType.TIMESTAMP_WITH_TIMEZONE);
            }, (rs, rowNum) -> {
                return new ZonedDateTimeTypeHandler().getNullableResult(rs, 1);
            });
            //
            ZonedDateTime dateTime = dat.get(0);
            //
            assert dateTime.getOffset().getId().equals(localTime.getOffset().getId());
            assert dateTime.getHour() == localTime.getHour();
            assert dateTime.getMinute() == localTime.getMinute();
            assert dateTime.getSecond() == localTime.getSecond();
            assert dateTime.getNano() == localTime.getNano();
        }
    }

    @Test
    public void testZonedDateTimeTypeHandler_4() throws SQLException {
        try (Connection conn = DsUtils.localOracle()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute(""//
                    + "create or replace procedure proc_timestamptz(p_out out timestamp with time zone)\n" //
                    + "AS\n" //
                    + "BEGIN\n"//
                    + "  p_out := to_timestamp_tz('2013-10-15T17:18:28-06:00','YYYY-MM-DD\"T\"HH24:MI:SSTZH:TZM');\n" //
                    + "END;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_timestamptz(?)}",//
                    Collections.singletonList(CallableSqlParameter.withInOut("out", null, JDBCType.TIMESTAMP_WITH_TIMEZONE, new ZonedDateTimeTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof ZonedDateTime;
            assert objectMap.get("out").toString().equals("2013-10-15T17:18:28-06:00");
        }
    }
}