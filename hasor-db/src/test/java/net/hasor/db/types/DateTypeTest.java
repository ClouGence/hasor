package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.SqlParameterUtils;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.DateOnlyTypeHandler;
import net.hasor.db.types.handler.DateTypeHandler;
import net.hasor.db.types.handler.TimeOnlyTypeHandler;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DateTypeTest {
    @Test
    public void testDateTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp) values (?);", testData);
            List<Date> dat = jdbcTemplate.query("select c_timestamp from tb_h2_types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
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
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp) values (?);", testData);
            List<Date> dat = jdbcTemplate.query("select c_timestamp from tb_h2_types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
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
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_timestamp;");
            jdbcTemplate.execute("create procedure proc_timestamp(out p_out timestamp) begin set p_out= str_to_date('2008-08-09 08:09:30', '%Y-%m-%d %h:%i:%s'); end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_timestamp(?)}",//
                    Collections.singletonList(SqlParameterUtils.withOutput("out", JDBCType.TIMESTAMP, new DateTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Date;
            String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(objectMap.get("out"));
            assert dateString.equals("2008-08-09 08:09:30");
            assert objectMap.get("#update-count-1").equals(0);
        }
    }

    @Test
    public void testTimeOnlyTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp) values (?);", testData);
            List<Date> dat = jdbcTemplate.query("select c_timestamp from tb_h2_types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
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
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp) values (?);", testData);
            List<Date> dat = jdbcTemplate.query("select c_timestamp from tb_h2_types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
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
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_timestamp;");
            jdbcTemplate.execute("create procedure proc_timestamp(out p_out timestamp) begin set p_out= str_to_date('2008-08-09 08:09:30', '%Y-%m-%d %h:%i:%s'); end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_timestamp(?)}",//
                    Collections.singletonList(SqlParameterUtils.withOutput("out", JDBCType.TIMESTAMP, new TimeOnlyTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Date;
            String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(objectMap.get("out"));
            assert dateString.equals("1970-01-01 08:09:30");
            assert objectMap.get("#update-count-1").equals(0);
        }
    }

    @Test
    public void testDateOnlyTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp) values (?);", testData);
            List<Date> dat = jdbcTemplate.query("select c_timestamp from tb_h2_types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
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
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp) values (?);", testData);
            List<Date> dat = jdbcTemplate.query("select c_timestamp from tb_h2_types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
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
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_timestamp;");
            jdbcTemplate.execute("create procedure proc_timestamp(out p_out timestamp) begin set p_out= str_to_date('2008-08-09 08:09:30', '%Y-%m-%d %h:%i:%s'); end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_timestamp(?)}",//
                    Collections.singletonList(SqlParameterUtils.withOutput("out", JDBCType.TIMESTAMP, new DateOnlyTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Date;
            String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(objectMap.get("out"));
            assert dateString.equals("2008-08-09 00:00:00");
            assert objectMap.get("#update-count-1").equals(0);
        }
    }
}