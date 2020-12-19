package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.CallableSqlParameter;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.MonthOfNumberTypeHandler;
import net.hasor.db.types.handler.MonthOfStringTypeHandler;
import net.hasor.db.types.handler.MonthOfTimeTypeHandler;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;

public class MonthTypeTest {
    @Test
    public void testMonthOfNumberTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_tinyint) values (05);");
            List<Month> dat = jdbcTemplate.query("select c_tinyint from tb_h2types where c_tinyint is not null limit 1;", (rs, rowNum) -> {
                return new MonthOfNumberTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0) == Month.MAY;
        }
    }

    @Test
    public void testMonthOfNumberTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_tinyint) values (05);");
            List<Month> dat = jdbcTemplate.query("select c_tinyint from tb_h2types where c_tinyint is not null limit 1;", (rs, rowNum) -> {
                return new MonthOfNumberTypeHandler().getResult(rs, "c_tinyint");
            });
            assert dat.get(0) == Month.MAY;
        }
    }

    @Test
    public void testMonthOfNumberTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Month dat1 = jdbcTemplate.queryForObject("select ?", Month.class, 5);
            assert dat1 == Month.MAY;
            //
            List<Month> dat2 = jdbcTemplate.query("select ?", ps -> {
                new MonthOfNumberTypeHandler().setParameter(ps, 1, Month.MAY, JDBCType.SMALLINT);
            }, (rs, rowNum) -> {
                return new MonthOfNumberTypeHandler().getNullableResult(rs, 1);
            });
            assert dat2.get(0) == Month.MAY;
        }
    }

    @Test
    public void testMonthOfNumberTypeHandler_4() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DsUtils.JDBC_URL)) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_smallint;");
            jdbcTemplate.execute("create procedure proc_smallint(out p_out smallint) begin set p_out=1; end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_smallint(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.SMALLINT, new MonthOfNumberTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Month;
            assert objectMap.get("out") == Month.JANUARY;
            assert objectMap.get("#update-count-1").equals(0);
        }
    }

    @Test
    public void testMonthOfStringTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('05');");
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('may');");
            List<Month> dat = jdbcTemplate.query("select c_varchar from tb_h2types where c_varchar is not null limit 2;", (rs, rowNum) -> {
                return new MonthOfStringTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0) == Month.MAY;
            assert dat.get(1) == Month.MAY;
        }
    }

    @Test
    public void testMonthOfStringTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('05');");
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('may');");
            List<Month> dat = jdbcTemplate.query("select c_varchar from tb_h2types where c_varchar is not null limit 2;", (rs, rowNum) -> {
                return new MonthOfStringTypeHandler().getResult(rs, "c_varchar");
            });
            assert dat.get(0) == Month.MAY;
            assert dat.get(1) == Month.MAY;
        }
    }

    @Test
    public void testMonthOfStringTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Month dat1 = jdbcTemplate.queryForObject("select ?", Month.class, "5");
            assert dat1 == Month.MAY;
            Month dat2 = jdbcTemplate.queryForObject("select ?", Month.class, "may");
            assert dat2 == Month.MAY;
            //
            List<Month> dat3 = jdbcTemplate.query("select ?", ps -> {
                new MonthOfStringTypeHandler().setParameter(ps, 1, Month.MAY, JDBCType.SMALLINT);
            }, (rs, rowNum) -> {
                return new MonthOfStringTypeHandler().getNullableResult(rs, 1);
            });
            assert dat3.get(0) == Month.MAY;
        }
    }

    @Test
    public void testMonthOfStringTypeHandler_4() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DsUtils.JDBC_URL)) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_varchar;");
            jdbcTemplate.execute("create procedure proc_varchar(out p_out varchar(10)) begin set p_out='may'; end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_varchar(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.VARCHAR, new MonthOfStringTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Month;
            assert objectMap.get("out") == Month.MAY;
            assert objectMap.get("#update-count-1").equals(0);
        }
    }

    @Test
    public void testMonthOfTimeTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (CURRENT_TIMESTAMP(9));");
            List<Month> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new MonthOfTimeTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0) == YearMonth.now().getMonth();
        }
    }

    @Test
    public void testMonthOfTimeTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (CURRENT_TIMESTAMP(9));");
            List<Month> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new MonthOfTimeTypeHandler().getResult(rs, "c_timestamp");
            });
            assert dat.get(0) == YearMonth.now().getMonth();
        }
    }

    @Test
    public void testMonthOfTimeTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            //
            Month dat1 = jdbcTemplate.queryForObject("select ?", Month.class, new Date());
            assert dat1 == YearMonth.now().getMonth();
            //
            //
            List<Month> dat2 = jdbcTemplate.query("select ?", ps -> {
                new MonthOfTimeTypeHandler().setParameter(ps, 1, Month.MAY, JDBCType.TIMESTAMP);
            }, (rs, rowNum) -> {
                return new MonthOfTimeTypeHandler().getNullableResult(rs, 1);
            });
            assert dat2.get(0) == Month.MAY;
            //
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", ps -> {
                new MonthOfTimeTypeHandler().setParameter(ps, 1, Month.MAY, JDBCType.TIMESTAMP);
            });
            Date dat = jdbcTemplate.queryForObject("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", Date.class);
            Calendar instance = Calendar.getInstance();
            instance.setTime(dat);
            int month = instance.get(Calendar.MONTH);
            assert month == Calendar.MAY;
        }
    }

    @Test
    public void testMonthOfTimeTypeHandler_4() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DsUtils.JDBC_URL)) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_timestamp;");
            jdbcTemplate.execute("create procedure proc_timestamp(out p_out timestamp) begin set p_out= str_to_date('2008-08-09 10:11:12', '%Y-%m-%d %h:%i:%s'); end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_timestamp(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.TIMESTAMP, new MonthOfTimeTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Month;
            assert objectMap.get("out") == Month.AUGUST;
            assert objectMap.get("#update-count-1").equals(0);
        }
    }
}