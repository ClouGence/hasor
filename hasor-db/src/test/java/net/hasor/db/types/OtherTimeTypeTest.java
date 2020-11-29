package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.InstantTypeHandler;
import net.hasor.db.types.handler.JapaneseDateTypeHandler;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.chrono.JapaneseDate;
import java.util.Date;
import java.util.List;

public class OtherTimeTypeTest {
    @Test
    public void testInstantTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", testData);
            List<Instant> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
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
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", testData);
            List<Instant> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
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
    public void testInstantTypeHandler_4() throws SQLException {
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
    public void testJapaneseDateTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", testData);
            List<JapaneseDate> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
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
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", testData);
            List<JapaneseDate> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
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
    public void testJapaneseDateTypeHandler_4() throws SQLException {
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