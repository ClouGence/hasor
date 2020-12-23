package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.CallableSqlParameter;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.NumberTypeHandler;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NumberTypeTest {
    @Test
    public void testNumberTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp) values (?);", testData);
            List<Number> dat = jdbcTemplate.query("select c_timestamp from tb_h2_types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new NumberTypeHandler().getResult(rs, 1);
            });
            //
            assert dat.get(0).longValue() == testData.getTime();
        }
    }

    @Test
    public void testNumberTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_timestamp) values (?);", testData);
            List<Number> dat = jdbcTemplate.query("select c_timestamp from tb_h2_types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
                return new NumberTypeHandler().getResult(rs, "c_timestamp");
            });
            //
            assert dat.get(0).longValue() == testData.getTime();
        }
    }

    @Test
    public void testNumberTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_char) values ('123');");
            List<Number> dat = jdbcTemplate.query("select c_char from tb_h2_types where c_char is not null limit 1;", (rs, rowNum) -> {
                return new NumberTypeHandler().getResult(rs, "c_char");
            });
            //
            assert dat.get(0).longValue() == 123;
        }
    }

    @Test
    public void testNumberTypeHandler_4() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_clob) values ('123');");
            List<Number> dat = jdbcTemplate.query("select c_clob from tb_h2_types where c_clob is not null limit 1;", (rs, rowNum) -> {
                return new NumberTypeHandler().getResult(rs, "c_clob");
            });
            //
            assert dat.get(0).longValue() == 123;
        }
    }

    @Test
    public void testNumberTypeHandler_5() throws Exception {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_varchar;");
            jdbcTemplate.execute("create procedure proc_varchar(out p_out varchar(10)) begin set p_out='123.4'; end;");
            jdbcTemplate.execute("drop procedure if exists proc_bigint;");
            jdbcTemplate.execute("create procedure proc_bigint(out p_out bigint) begin set p_out=1234; end;");
            jdbcTemplate.execute("drop procedure if exists proc_float;");
            jdbcTemplate.execute("create procedure proc_float(out p_out float) begin set p_out='123.4'; end;");
            jdbcTemplate.execute("drop procedure if exists proc_timestamp;");
            jdbcTemplate.execute("create procedure proc_timestamp(out p_out timestamp) begin set p_out= str_to_date('2008-08-09 10:11:12', '%Y-%m-%d %h:%i:%s'); end;");
            jdbcTemplate.execute("drop procedure if exists proc_data;");
            jdbcTemplate.execute("create procedure proc_data(out p_out date) begin set p_out= str_to_date('2008-08-09 10:11:12', '%Y-%m-%d %h:%i:%s'); end;");
            //
            Map<String, Object> objectMap1 = jdbcTemplate.call("{call proc_varchar(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.VARCHAR, new NumberTypeHandler())));
            Map<String, Object> objectMap2 = jdbcTemplate.call("{call proc_bigint(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.BIGINT, new NumberTypeHandler())));
            Map<String, Object> objectMap4 = jdbcTemplate.call("{call proc_float(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.FLOAT, new NumberTypeHandler())));
            Map<String, Object> objectMap5 = jdbcTemplate.call("{call proc_timestamp(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.TIMESTAMP, new NumberTypeHandler())));
            Map<String, Object> objectMap6 = jdbcTemplate.call("{call proc_data(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.DATE, new NumberTypeHandler())));
            //
            assert objectMap1.size() == 2;
            assert objectMap2.size() == 2;
            assert objectMap4.size() == 2;
            assert objectMap5.size() == 2;
            assert objectMap6.size() == 2;
            assert objectMap1.get("out") instanceof Number;
            assert objectMap2.get("out") instanceof Number;
            assert objectMap4.get("out") instanceof Number;
            assert objectMap5.get("out") instanceof Number;
            assert objectMap6.get("out") instanceof Number;
            assert objectMap1.get("out").toString().equals("123.4");
            assert objectMap2.get("out").toString().equals("1234");
            assert objectMap4.get("out").toString().startsWith("123.4"); // 有可能出现精度问题
            Date parseData1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2008-08-09 10:11:12");
            assert objectMap5.get("out").toString().equals(String.valueOf(parseData1.getTime()));
            Date parseData2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2008-08-09 00:00:00");
            assert objectMap6.get("out").toString().equals(String.valueOf(parseData2.getTime()));
        }
    }
}