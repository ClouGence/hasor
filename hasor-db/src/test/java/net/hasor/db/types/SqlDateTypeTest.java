package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.SqlDateTypeHandler;
import net.hasor.db.types.handler.SqlTimeTypeHandler;
import net.hasor.db.types.handler.SqlTimestampTypeHandler;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class SqlDateTypeTest {
    @Test
    public void testSqlTimestampTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", testData);
            List<Date> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new SqlTimestampTypeHandler().getResult(rs, 1);
            });
            //
            assert dat.get(0).getTime() == testData.getTime();
        }
    }

    @Test
    public void testSqlTimestampTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", testData);
            List<Date> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new SqlTimestampTypeHandler().getResult(rs, "c_timestamp");
            });
            //
            assert dat.get(0).getTime() == testData.getTime();
        }
    }

    @Test
    public void testSqlTimestampTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Timestamp testData = new Timestamp(new Date().getTime());
            List<Date> dat = jdbcTemplate.query("select ?", ps -> {
                new SqlTimestampTypeHandler().setParameter(ps, 1, testData, JDBCType.TIMESTAMP);
            }, (rs, rowNum) -> {
                return new SqlTimestampTypeHandler().getNullableResult(rs, 1);
            });
            //
            assert dat.get(0).getTime() == testData.getTime();
        }
    }

    @Test
    public void testSqlTimestampTypeHandler_4() throws SQLException {
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
    public void testSqlTimeTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", testData);
            List<Time> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new SqlTimeTypeHandler().getResult(rs, 1);
            });
            //
            assert testData.getTime() != dat.get(0).getTime();
            LocalTime t1Time = LocalDateTime.ofInstant(testData.toInstant(), ZoneId.systemDefault()).toLocalTime();
            LocalTime t2Time = dat.get(0).toLocalTime();
            assert t1Time.toNanoOfDay() != t2Time.toNanoOfDay();
            assert t1Time.toSecondOfDay() == t2Time.toSecondOfDay();
        }
    }

    @Test
    public void testSqlTimeTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", testData);
            List<Time> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new SqlTimeTypeHandler().getResult(rs, "c_timestamp");
            });
            //
            assert testData.getTime() != dat.get(0).getTime();
            LocalTime t1Time = LocalDateTime.ofInstant(testData.toInstant(), ZoneId.systemDefault()).toLocalTime();
            LocalTime t2Time = dat.get(0).toLocalTime();
            assert t1Time.toNanoOfDay() != t2Time.toNanoOfDay();
            assert t1Time.toSecondOfDay() == t2Time.toSecondOfDay();
        }
    }

    @Test
    public void testSqlTimeTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Time testTime = new Time(new Date().getTime());
            List<Time> dat = jdbcTemplate.query("select ?", ps -> {
                new SqlTimeTypeHandler().setParameter(ps, 1, testTime, JDBCType.TIMESTAMP);
            }, (rs, rowNum) -> {
                return new SqlTimeTypeHandler().getNullableResult(rs, 1);
            });
            //
            assert testTime.getTime() != dat.get(0).getTime();
            LocalTime t1Time = testTime.toLocalTime();
            LocalTime t2Time = dat.get(0).toLocalTime();
            assert t1Time.toNanoOfDay() == t2Time.toNanoOfDay();
        }
    }

    @Test
    public void testSqlTimeTypeHandler_4() throws SQLException {
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
    public void testSqlDateTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", testData);
            List<java.sql.Date> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new SqlDateTypeHandler().getResult(rs, 1);
            });
            //
            assert testData.getTime() != dat.get(0).getTime();
            LocalDate t1Date = LocalDateTime.ofInstant(testData.toInstant(), ZoneId.systemDefault()).toLocalDate();
            LocalDate t2Date = dat.get(0).toLocalDate();
            assert t1Date.equals(t2Date);
        }
    }

    @Test
    public void testSqlDateTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", testData);
            List<java.sql.Date> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new SqlDateTypeHandler().getResult(rs, "c_timestamp");
            });
            //
            assert testData.getTime() != dat.get(0).getTime();
            LocalDate t1Date = LocalDateTime.ofInstant(testData.toInstant(), ZoneId.systemDefault()).toLocalDate();
            LocalDate t2Date = dat.get(0).toLocalDate();
            assert t1Date.equals(t2Date);
        }
    }

    @Test
    public void testSqlDateTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            java.sql.Date testData = new java.sql.Date(new Date().getTime());
            List<java.sql.Date> dat = jdbcTemplate.query("select ?", ps -> {
                new SqlDateTypeHandler().setParameter(ps, 1, testData, JDBCType.TIMESTAMP);
            }, (rs, rowNum) -> {
                return new SqlDateTypeHandler().getNullableResult(rs, 1);
            });
            //
            assert testData.getTime() != dat.get(0).getTime();
            LocalDate t1Data = testData.toLocalDate();
            LocalDate t2Data = dat.get(0).toLocalDate();
            assert t1Data.toEpochDay() == t2Data.toEpochDay();
        }
    }

    @Test
    public void testSqlDateTypeHandler_4() throws SQLException {
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