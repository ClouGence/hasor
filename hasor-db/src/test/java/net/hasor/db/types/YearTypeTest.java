package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.CallableSqlParameter;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.YearOfNumberTypeHandler;
import net.hasor.db.types.handler.YearOfStringTypeHandler;
import net.hasor.db.types.handler.YearOfTimeTypeHandler;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;

public class YearTypeTest {
    @Test
    public void testYearOfNumberTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_smallint) values (2020);");
            List<Year> dat = jdbcTemplate.query("select c_smallint from tb_h2types where c_smallint is not null limit 1;", (rs, rowNum) -> {
                return new YearOfNumberTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0).getValue() == 2020;
        }
    }

    @Test
    public void testYearOfNumberTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_smallint) values (2020);");
            List<Year> dat = jdbcTemplate.query("select c_smallint from tb_h2types where c_smallint is not null limit 1;", (rs, rowNum) -> {
                return new YearOfNumberTypeHandler().getResult(rs, "c_smallint");
            });
            assert dat.get(0).getValue() == 2020;
        }
    }

    @Test
    public void testYearOfNumberTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Year dat1 = jdbcTemplate.queryForObject("select ?", Year.class, Year.of(2008));
            assert dat1.getValue() == 2008;
            //
            List<Year> dat2 = jdbcTemplate.query("select ?", ps -> {
                new YearOfNumberTypeHandler().setParameter(ps, 1, Year.of(2008), JDBCType.SMALLINT);
            }, (rs, rowNum) -> {
                return new YearOfNumberTypeHandler().getNullableResult(rs, 1);
            });
            assert dat2.get(0).getValue() == 2008;
        }
    }

    @Test
    public void testYearOfNumberTypeHandler_4() throws SQLException {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_integer;");
            jdbcTemplate.execute("create procedure proc_integer(out p_out integer) begin set p_out=2020; end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_integer(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.INTEGER, new YearOfNumberTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Year;
            Year instant = (Year) objectMap.get("out");
            assert instant.getValue() == 2020;
        }
    }

    @Test
    public void testYearOfStringTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('2008');");
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('2022');");
            List<Year> dat = jdbcTemplate.query("select c_varchar from tb_h2types where c_varchar is not null limit 2;", (rs, rowNum) -> {
                return new YearOfStringTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0).getValue() == 2008;
            assert dat.get(1).getValue() == 2022;
        }
    }

    @Test
    public void testYearOfStringTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('1986');");
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_varchar) values ('1998');");
            List<Year> dat = jdbcTemplate.query("select c_varchar from tb_h2types where c_varchar is not null limit 2;", (rs, rowNum) -> {
                return new YearOfStringTypeHandler().getResult(rs, "c_varchar");
            });
            assert dat.get(0).getValue() == 1986;
            assert dat.get(1).getValue() == 1998;
        }
    }

    @Test
    public void testYearOfStringTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Year dat1 = jdbcTemplate.queryForObject("select ?", Year.class, "0005");
            assert dat1.getValue() == 5;
            Year dat2 = jdbcTemplate.queryForObject("select ?", Year.class, "2020");
            assert dat2.getValue() == 2020;
            //
            List<Year> dat3 = jdbcTemplate.query("select ?", ps -> {
                new YearOfStringTypeHandler().setParameter(ps, 1, Year.of(1998), JDBCType.SMALLINT);
            }, (rs, rowNum) -> {
                return new YearOfStringTypeHandler().getNullableResult(rs, 1);
            });
            assert dat3.get(0).getValue() == 1998;
        }
    }

    @Test
    public void testYearOfStringTypeHandler_4() throws SQLException {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_varchar;");
            jdbcTemplate.execute("create procedure proc_varchar(out p_out varchar(10)) begin set p_out='2020'; end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_varchar(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.VARCHAR, new YearOfStringTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Year;
            Year instant = (Year) objectMap.get("out");
            assert instant.getValue() == 2020;
        }
    }

    @Test
    public void testYearOfTimeTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (CURRENT_TIMESTAMP(9));");
            List<Year> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new YearOfTimeTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0).getValue() == YearMonth.now().getYear();
        }
    }

    @Test
    public void testYearOfTimeTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (CURRENT_TIMESTAMP(9));");
            List<Year> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new YearOfTimeTypeHandler().getResult(rs, "c_timestamp");
            });
            assert dat.get(0).getValue() == YearMonth.now().getYear();
        }
    }

    @Test
    public void testYearOfTimeTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            //
            Year dat1 = jdbcTemplate.queryForObject("select ?", Year.class, new Date());
            assert dat1.getValue() == YearMonth.now().getYear();
            //
            //
            List<Year> dat2 = jdbcTemplate.query("select ?", ps -> {
                new YearOfTimeTypeHandler().setParameter(ps, 1, Year.of(2018), JDBCType.TIMESTAMP);
            }, (rs, rowNum) -> {
                return new YearOfTimeTypeHandler().getNullableResult(rs, 1);
            });
            assert dat2.get(0).getValue() == 2018;
            //
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", ps -> {
                new YearOfTimeTypeHandler().setParameter(ps, 1, Year.of(2018), JDBCType.TIMESTAMP);
            });
            Date dat = jdbcTemplate.queryForObject("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", Date.class);
            Calendar instance = Calendar.getInstance();
            instance.setTime(dat);
            int year = instance.get(Calendar.YEAR);
            assert year == 2018;
        }
    }

    @Test
    public void testYearOfTimeTypeHandler_4() throws SQLException {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_timestamp;");
            jdbcTemplate.execute("create procedure proc_timestamp(out p_out timestamp) begin set p_out= str_to_date('2008-08-09 10:11:12', '%Y-%m-%d %h:%i:%s'); end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_timestamp(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.TIMESTAMP, new YearOfTimeTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Year;
            Year instant = (Year) objectMap.get("out");
            assert instant.getValue() == 2008;
        }
    }
}