package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.CallableSqlParameter;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.InstantTypeHandler;
import net.hasor.db.types.handler.JapaneseDateTypeHandler;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.chrono.JapaneseDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class OtherTimeTypeTest {
    @Test
    public void testInstantTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp) values (?);", testData);
            List<Instant> dat = jdbcTemplate.query("select c_timestamp from tb_h2_types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new InstantTypeHandler().getResult(rs, 1);
            });
            //
            assert testData.toInstant().toEpochMilli() == dat.get(0).toEpochMilli();
        }
    }

    @Test
    public void testInstantTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp) values (?);", testData);
            List<Instant> dat = jdbcTemplate.query("select c_timestamp from tb_h2_types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new InstantTypeHandler().getResult(rs, "c_timestamp");
            });
            //
            assert testData.toInstant().toEpochMilli() == dat.get(0).toEpochMilli();
        }
    }

    @Test
    public void testInstantTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            List<Instant> dat = jdbcTemplate.query("select ?", ps -> {
                new InstantTypeHandler().setParameter(ps, 1, testData.toInstant(), JDBCType.TIMESTAMP);
            }, (rs, rowNum) -> {
                return new InstantTypeHandler().getNullableResult(rs, 1);
            });
            //
            assert testData.toInstant().toEpochMilli() == dat.get(0).toEpochMilli();
        }
    }

    @Test
    public void testInstantTypeHandler_4() throws Exception {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_timestamp;");
            jdbcTemplate.execute("create procedure proc_timestamp(out p_out timestamp) begin set p_out= str_to_date('2008-08-09 10:11:12', '%Y-%m-%d %h:%i:%s'); end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_timestamp(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.TIMESTAMP, new InstantTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Instant;
            Date dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2008-08-09 10:11:12");
            Instant instant = (Instant) objectMap.get("out");
            assert dateString.toInstant().toEpochMilli() == instant.toEpochMilli();
        }
    }

    @Test
    public void testJapaneseDateTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp) values (?);", testData);
            List<JapaneseDate> dat = jdbcTemplate.query("select c_timestamp from tb_h2_types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new JapaneseDateTypeHandler().getResult(rs, 1);
            });
            //
            assert dat.get(0).toEpochDay() == LocalDate.now().toEpochDay();
        }
    }

    @Test
    public void testJapaneseDateTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp) values (?);", testData);
            List<JapaneseDate> dat = jdbcTemplate.query("select c_timestamp from tb_h2_types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new JapaneseDateTypeHandler().getResult(rs, "c_timestamp");
            });
            //
            assert dat.get(0).toEpochDay() == LocalDate.now().toEpochDay();
        }
    }

    @Test
    public void testJapaneseDateTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            LocalDate testData = LocalDate.of(1998, Month.APRIL, 12);
            JapaneseDate jpData = JapaneseDate.from(testData);
            List<JapaneseDate> dat = jdbcTemplate.query("select ?", ps -> {
                new JapaneseDateTypeHandler().setParameter(ps, 1, jpData, JDBCType.TIMESTAMP);
            }, (rs, rowNum) -> {
                return new JapaneseDateTypeHandler().getNullableResult(rs, 1);
            });
            //
            JapaneseDate dateTime = dat.get(0);
            assert dateTime.toEpochDay() == testData.toEpochDay();
        }
    }

    @Test
    public void testJapaneseDateTypeHandler_4() throws Exception {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_timestamp;");
            jdbcTemplate.execute("create procedure proc_timestamp(out p_out timestamp) begin set p_out= str_to_date('2008-08-09 10:11:12', '%Y-%m-%d %h:%i:%s'); end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_timestamp(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.TIMESTAMP, new JapaneseDateTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof JapaneseDate;
            Date testDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2008-08-09 10:11:12");
            JapaneseDate instant = (JapaneseDate) objectMap.get("out");
            assert JapaneseDateTypeHandler.toJapaneseDate(testDate).equals(instant);
        }
    }
}