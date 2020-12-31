package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.SqlParameterUtils;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.YearMonthOfNumberTypeHandler;
import net.hasor.db.types.handler.YearMonthOfStringTypeHandler;
import net.hasor.db.types.handler.YearMonthOfTimeTypeHandler;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.time.Month;
import java.time.YearMonth;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class YearMonthTypeTest {
    @Test
    public void testYearMonthOfNumberTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_integer) values (202001);");
            List<YearMonth> dat = jdbcTemplate.query("select c_integer from tb_h2_types where c_integer is not null limit 1;", (rs, rowNum) -> {
                return new YearMonthOfNumberTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0).getYear() == 2020;
            assert dat.get(0).getMonth() == Month.JANUARY;
        }
    }

    @Test
    public void testYearMonthOfNumberTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_integer) values (202001);");
            List<YearMonth> dat = jdbcTemplate.query("select c_integer from tb_h2_types where c_integer is not null limit 1;", (rs, rowNum) -> {
                return new YearMonthOfNumberTypeHandler().getResult(rs, "c_integer");
            });
            assert dat.get(0).getYear() == 2020;
            assert dat.get(0).getMonth() == Month.JANUARY;
        }
    }

    @Test
    public void testYearMonthOfNumberTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            YearMonth dat1 = jdbcTemplate.queryForObject("select ?", YearMonth.class, YearMonth.of(2008, 2));
            assert dat1.getYear() == 2008;
            assert dat1.getMonth() == Month.FEBRUARY;
            //
            List<YearMonth> dat2 = jdbcTemplate.query("select ?", ps -> {
                new YearMonthOfNumberTypeHandler().setParameter(ps, 1, YearMonth.of(2008, 2), JDBCType.SMALLINT);
            }, (rs, rowNum) -> {
                return new YearMonthOfNumberTypeHandler().getNullableResult(rs, 1);
            });
            assert dat2.get(0).getYear() == 2008;
            assert dat2.get(0).getMonth() == Month.FEBRUARY;
        }
    }

    @Test
    public void testYearMonthOfNumberTypeHandler_4() throws SQLException {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_integer;");
            jdbcTemplate.execute("create procedure proc_integer(out p_out integer) begin set p_out=202001; end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_integer(?)}",//
                    Collections.singletonList(SqlParameterUtils.withOutput("out", JDBCType.INTEGER, new YearMonthOfNumberTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof YearMonth;
            YearMonth instant = (YearMonth) objectMap.get("out");
            assert instant.getYear() == 2020;
            assert instant.getMonth() == Month.JANUARY;
        }
    }

    @Test
    public void testYearMonthOfStringTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_varchar) values ('2008-01');");
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_varchar) values ('2022-03');");
            List<YearMonth> dat = jdbcTemplate.query("select c_varchar from tb_h2_types where c_varchar is not null limit 2;", (rs, rowNum) -> {
                return new YearMonthOfStringTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0).getYear() == 2008;
            assert dat.get(0).getMonth() == Month.JANUARY;
            assert dat.get(1).getYear() == 2022;
            assert dat.get(1).getMonth() == Month.MARCH;
        }
    }

    @Test
    public void testYearMonthOfStringTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_varchar) values ('1986-01');");
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_varchar) values ('1998-03');");
            List<YearMonth> dat = jdbcTemplate.query("select c_varchar from tb_h2_types where c_varchar is not null limit 2;", (rs, rowNum) -> {
                return new YearMonthOfStringTypeHandler().getResult(rs, "c_varchar");
            });
            assert dat.get(0).getYear() == 1986;
            assert dat.get(0).getMonth() == Month.JANUARY;
            assert dat.get(1).getYear() == 1998;
            assert dat.get(1).getMonth() == Month.MARCH;
        }
    }

    @Test
    public void testYearMonthOfStringTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            YearMonth dat1 = jdbcTemplate.queryForObject("select ?", YearMonth.class, "0005-01");
            assert dat1.getYear() == 5;
            assert dat1.getMonth() == Month.JANUARY;
            YearMonth dat2 = jdbcTemplate.queryForObject("select ?", YearMonth.class, "2020-01");
            assert dat2.getYear() == 2020;
            assert dat2.getMonth() == Month.JANUARY;
            //
            List<YearMonth> dat3 = jdbcTemplate.query("select ?", ps -> {
                new YearMonthOfStringTypeHandler().setParameter(ps, 1, YearMonth.of(1998, 2), JDBCType.VARCHAR);
            }, (rs, rowNum) -> {
                return new YearMonthOfStringTypeHandler().getNullableResult(rs, 1);
            });
            assert dat3.get(0).getYear() == 1998;
            assert dat3.get(0).getMonth() == Month.FEBRUARY;
        }
    }

    @Test
    public void testYearMonthOfStringTypeHandler_4() throws SQLException {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_varchar;");
            jdbcTemplate.execute("create procedure proc_varchar(out p_out varchar(10)) begin set p_out='2020-01'; end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_varchar(?)}",//
                    Collections.singletonList(SqlParameterUtils.withOutput("out", JDBCType.VARCHAR, new YearMonthOfStringTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof YearMonth;
            YearMonth instant = (YearMonth) objectMap.get("out");
            assert instant.getYear() == 2020;
            assert instant.getMonth() == Month.JANUARY;
        }
    }

    @Test
    public void testYearMonthOfTimeTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp) values (CURRENT_TIMESTAMP(9));");
            List<YearMonth> dat = jdbcTemplate.query("select c_timestamp from tb_h2_types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new YearMonthOfTimeTypeHandler().getResult(rs, 1);
            });
            YearMonth yearMonth = YearMonth.now();
            assert dat.get(0).getYear() == yearMonth.getYear();
            assert dat.get(0).getMonth() == yearMonth.getMonth();
        }
    }

    @Test
    public void testYearMonthOfTimeTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp) values (CURRENT_TIMESTAMP(9));");
            List<YearMonth> dat = jdbcTemplate.query("select c_timestamp from tb_h2_types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new YearMonthOfTimeTypeHandler().getResult(rs, "c_timestamp");
            });
            YearMonth yearMonth = YearMonth.now();
            assert dat.get(0).getYear() == yearMonth.getYear();
            assert dat.get(0).getMonth() == yearMonth.getMonth();
        }
    }

    @Test
    public void testYearMonthOfTimeTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            //
            YearMonth dat1 = jdbcTemplate.queryForObject("select ?", YearMonth.class, new Date());
            YearMonth yearMonth = YearMonth.now();
            assert dat1.getYear() == yearMonth.getYear();
            assert dat1.getMonth() == yearMonth.getMonth();
            //
            //
            List<YearMonth> dat2 = jdbcTemplate.query("select ?", ps -> {
                new YearMonthOfTimeTypeHandler().setParameter(ps, 1, YearMonth.of(2018, 4), JDBCType.TIMESTAMP);
            }, (rs, rowNum) -> {
                return new YearMonthOfTimeTypeHandler().getNullableResult(rs, 1);
            });
            assert dat2.get(0).getYear() == 2018;
            assert dat2.get(0).getMonth() == Month.APRIL;
            //
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp) values (?);", ps -> {
                new YearMonthOfTimeTypeHandler().setParameter(ps, 1, YearMonth.of(2018, 4), JDBCType.TIMESTAMP);
            });
            YearMonth dat3 = jdbcTemplate.queryForObject("select c_timestamp from tb_h2_types where c_timestamp is not null limit 1;", YearMonth.class);
            assert dat3.getYear() == 2018;
            assert dat3.getMonth() == Month.APRIL;
        }
    }

    @Test
    public void testYearMonthOfTimeTypeHandler_4() throws Exception {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_timestamp;");
            jdbcTemplate.execute("create procedure proc_timestamp(out p_out timestamp) begin set p_out= str_to_date('2008-08-09 10:11:12', '%Y-%m-%d %h:%i:%s'); end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_timestamp(?)}",//
                    Collections.singletonList(SqlParameterUtils.withOutput("out", JDBCType.TIMESTAMP, new YearMonthOfTimeTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof YearMonth;
            YearMonth instant = (YearMonth) objectMap.get("out");
            assert instant.getYear() == 2008;
            assert instant.getMonth() == Month.AUGUST;
        }
    }
}