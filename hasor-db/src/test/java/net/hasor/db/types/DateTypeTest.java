package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.DateOnlyTypeHandler;
import net.hasor.db.types.handler.DateTypeHandler;
import net.hasor.db.types.handler.TimeOnlyTypeHandler;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class DateTypeTest {
    @Test
    public void testDateTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", testData);
            List<Date> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new DateTypeHandler().getResult(rs, 1);
            });
            //
            assert testData.getTime() == dat.get(0).getTime();
        }
    }

    @Test
    public void testDateTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", testData);
            List<Date> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new DateTypeHandler().getResult(rs, "c_timestamp");
            });
            //
            assert testData.getTime() == dat.get(0).getTime();
        }
    }

    @Test
    public void testDateTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            List<Date> dat = jdbcTemplate.query("select ?", ps -> {
                new DateTypeHandler().setParameter(ps, 1, testData, JDBCType.TIMESTAMP);
            }, (rs, rowNum) -> {
                return new DateTypeHandler().getNullableResult(rs, 1);
            });
            //
            Date dateTime = dat.get(0);
            assert dateTime.getTime() == testData.getTime();
        }
    }

    @Test
    public void testDateTypeHandler_4() throws SQLException {
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
    public void testTimeOnlyTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", testData);
            List<Date> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new TimeOnlyTypeHandler().getResult(rs, 1);
            });
            //
            assert testData.getTime() != dat.get(0).getTime();
            LocalDate t1Data = LocalDateTime.ofInstant(testData.toInstant(), ZoneId.systemDefault()).toLocalDate();
            LocalDate t2Data = LocalDateTime.ofInstant(dat.get(0).toInstant(), ZoneId.systemDefault()).toLocalDate();
            assert t1Data.toEpochDay() != t2Data.toEpochDay();
            //
            LocalTime t1Time = LocalDateTime.ofInstant(testData.toInstant(), ZoneId.systemDefault()).toLocalTime();
            LocalTime t2Time = LocalDateTime.ofInstant(dat.get(0).toInstant(), ZoneId.systemDefault()).toLocalTime();
            assert t1Time.toNanoOfDay() == t2Time.toNanoOfDay();
        }
    }

    @Test
    public void testTimeOnlyTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", testData);
            List<Date> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new TimeOnlyTypeHandler().getResult(rs, "c_timestamp");
            });
            //
            assert testData.getTime() != dat.get(0).getTime();
            LocalDate t1Data = LocalDateTime.ofInstant(testData.toInstant(), ZoneId.systemDefault()).toLocalDate();
            LocalDate t2Data = LocalDateTime.ofInstant(dat.get(0).toInstant(), ZoneId.systemDefault()).toLocalDate();
            assert t1Data.toEpochDay() != t2Data.toEpochDay();
            //
            LocalTime t1Time = LocalDateTime.ofInstant(testData.toInstant(), ZoneId.systemDefault()).toLocalTime();
            LocalTime t2Time = LocalDateTime.ofInstant(dat.get(0).toInstant(), ZoneId.systemDefault()).toLocalTime();
            assert t1Time.toNanoOfDay() == t2Time.toNanoOfDay();
        }
    }

    @Test
    public void testTimeOnlyTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            List<Date> dat = jdbcTemplate.query("select ?", ps -> {
                new TimeOnlyTypeHandler().setParameter(ps, 1, testData, JDBCType.TIMESTAMP);
            }, (rs, rowNum) -> {
                return new TimeOnlyTypeHandler().getNullableResult(rs, 1);
            });
            //
            assert testData.getTime() != dat.get(0).getTime();
            LocalDate t1Data = LocalDateTime.ofInstant(testData.toInstant(), ZoneId.systemDefault()).toLocalDate();
            LocalDate t2Data = LocalDateTime.ofInstant(dat.get(0).toInstant(), ZoneId.systemDefault()).toLocalDate();
            assert t1Data.toEpochDay() != t2Data.toEpochDay();
            //
            LocalTime t1Time = LocalDateTime.ofInstant(testData.toInstant(), ZoneId.systemDefault()).toLocalTime();
            LocalTime t2Time = LocalDateTime.ofInstant(dat.get(0).toInstant(), ZoneId.systemDefault()).toLocalTime();
            assert t1Time.toNanoOfDay() == t2Time.toNanoOfDay();
        }
    }

    @Test
    public void testTimeOnlyTypeHandler_4() throws SQLException {
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
    public void testDateOnlyTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", testData);
            List<Date> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new DateOnlyTypeHandler().getResult(rs, 1);
            });
            //
            assert testData.getTime() != dat.get(0).getTime();
            LocalDate t1Data = LocalDateTime.ofInstant(testData.toInstant(), ZoneId.systemDefault()).toLocalDate();
            LocalDate t2Data = LocalDateTime.ofInstant(dat.get(0).toInstant(), ZoneId.systemDefault()).toLocalDate();
            assert t1Data.toEpochDay() == t2Data.toEpochDay();
            //
            LocalTime t1Time = LocalDateTime.ofInstant(testData.toInstant(), ZoneId.systemDefault()).toLocalTime();
            LocalTime t2Time = LocalDateTime.ofInstant(dat.get(0).toInstant(), ZoneId.systemDefault()).toLocalTime();
            assert t1Time.toNanoOfDay() != t2Time.toNanoOfDay();
        }
    }

    @Test
    public void testDateOnlyTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", testData);
            List<Date> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new DateOnlyTypeHandler().getResult(rs, "c_timestamp");
            });
            //
            assert testData.getTime() != dat.get(0).getTime();
            LocalDate t1Data = LocalDateTime.ofInstant(testData.toInstant(), ZoneId.systemDefault()).toLocalDate();
            LocalDate t2Data = LocalDateTime.ofInstant(dat.get(0).toInstant(), ZoneId.systemDefault()).toLocalDate();
            assert t1Data.toEpochDay() == t2Data.toEpochDay();
            //
            LocalTime t1Time = LocalDateTime.ofInstant(testData.toInstant(), ZoneId.systemDefault()).toLocalTime();
            LocalTime t2Time = LocalDateTime.ofInstant(dat.get(0).toInstant(), ZoneId.systemDefault()).toLocalTime();
            assert t1Time.toNanoOfDay() != t2Time.toNanoOfDay();
        }
    }

    @Test
    public void testDateOnlyTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            List<Date> dat = jdbcTemplate.query("select ?", ps -> {
                new DateOnlyTypeHandler().setParameter(ps, 1, testData, JDBCType.TIMESTAMP);
            }, (rs, rowNum) -> {
                return new DateOnlyTypeHandler().getNullableResult(rs, 1);
            });
            //
            assert testData.getTime() != dat.get(0).getTime();
            LocalDate t1Data = LocalDateTime.ofInstant(testData.toInstant(), ZoneId.systemDefault()).toLocalDate();
            LocalDate t2Data = LocalDateTime.ofInstant(dat.get(0).toInstant(), ZoneId.systemDefault()).toLocalDate();
            assert t1Data.toEpochDay() == t2Data.toEpochDay();
            //
            LocalTime t1Time = LocalDateTime.ofInstant(testData.toInstant(), ZoneId.systemDefault()).toLocalTime();
            LocalTime t2Time = LocalDateTime.ofInstant(dat.get(0).toInstant(), ZoneId.systemDefault()).toLocalTime();
            assert t1Time.toNanoOfDay() != t2Time.toNanoOfDay();
        }
    }

    @Test
    public void testDateOnlyTypeHandler_4() throws SQLException {
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