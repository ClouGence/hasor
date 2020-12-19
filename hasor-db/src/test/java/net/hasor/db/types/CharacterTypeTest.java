package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.CallableSqlParameter;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.CharacterTypeHandler;
import net.hasor.db.types.handler.NCharacterTypeHandler;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CharacterTypeTest {
    @Test
    public void testCharacterTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_char) values ('1234567890');");
            List<Character> bigInteger = jdbcTemplate.query("select c_char from tb_h2types where c_char is not null limit 1;", (rs, rowNum) -> {
                return new CharacterTypeHandler().getResult(rs, 1);
            });
            assert bigInteger.get(0).toString().equals("1");
        }
    }

    @Test
    public void testCharacterTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_char) values ('1234567890');");
            List<Character> bigInteger = jdbcTemplate.query("select c_char from tb_h2types where c_char is not null limit 1;", (rs, rowNum) -> {
                return new CharacterTypeHandler().getResult(rs, "c_char");
            });
            assert bigInteger.get(0).toString().equals("1");
        }
    }

    @Test
    public void testCharacterTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            char dat1 = jdbcTemplate.queryForObject("select ?", char.class, "abc");
            Character dat2 = jdbcTemplate.queryForObject("select ?", Character.class, "abc");
            assert dat1 == 'a';
            assert dat2 == 'a';
            //
            List<Character> character1 = jdbcTemplate.query("select ?", ps -> {
                new CharacterTypeHandler().setParameter(ps, 1, 'a', JDBCType.CHAR);
            }, (rs, rowNum) -> {
                return new CharacterTypeHandler().getNullableResult(rs, 1);
            });
            assert character1.get(0) == 'a';
            //
            List<Character> character2 = jdbcTemplate.query("select ? as ncr", ps -> {
                new CharacterTypeHandler().setParameter(ps, 1, 'a', JDBCType.CHAR);
            }, (rs, rowNum) -> {
                return new CharacterTypeHandler().getNullableResult(rs, "ncr");
            });
            assert character2.get(0) == 'a';
        }
    }

    @Test
    public void testCharacterTypeHandler_4() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DsUtils.JDBC_URL)) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_char;");
            jdbcTemplate.execute("create procedure proc_char(out p_out char) begin set p_out='A'; end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_char(?)}",//
                    Collections.singletonList(//
                            CallableSqlParameter.withOutput("out", JDBCType.CHAR, new CharacterTypeHandler())//
                    ));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Character;
            assert objectMap.get("out").equals('A');
            assert objectMap.get("#update-count-1").equals(0);
        }
    }

    @Test
    public void testNCharacterTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_char) values ('1234567890');");
            List<Character> bigInteger = jdbcTemplate.query("select c_char from tb_h2types where c_char is not null limit 1;", (rs, rowNum) -> {
                return new NCharacterTypeHandler().getResult(rs, 1);
            });
            assert bigInteger.get(0).toString().equals("1");
        }
    }

    @Test
    public void testNCharacterTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_char) values ('1234567890');");
            List<Character> bigInteger = jdbcTemplate.query("select c_char from tb_h2types where c_char is not null limit 1;", (rs, rowNum) -> {
                return new NCharacterTypeHandler().getResult(rs, "c_char");
            });
            assert bigInteger.get(0).toString().equals("1");
        }
    }

    @Test
    public void testNCharacterTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            char dat1 = jdbcTemplate.queryForObject("select ?", char.class, "abc");
            Character dat2 = jdbcTemplate.queryForObject("select ?", Character.class, "abc");
            assert dat1 == 'a';
            assert dat2 == 'a';
            //
            List<Character> character1 = jdbcTemplate.query("select ?", ps -> {
                new NCharacterTypeHandler().setParameter(ps, 1, 'a', JDBCType.NCHAR);
            }, (rs, rowNum) -> {
                return new NCharacterTypeHandler().getNullableResult(rs, 1);
            });
            assert character1.get(0) == 'a';
            //
            List<Character> character2 = jdbcTemplate.query("select ? as ncr", ps -> {
                new NCharacterTypeHandler().setParameter(ps, 1, 'a', JDBCType.NCHAR);
            }, (rs, rowNum) -> {
                return new NCharacterTypeHandler().getNullableResult(rs, "ncr");
            });
            assert character2.get(0) == 'a';
        }
    }

    @Test
    public void testNCharacterTypeHandler_4() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DsUtils.JDBC_URL)) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_char;");
            jdbcTemplate.execute("create procedure proc_char(out p_out char) begin set p_out='A'; end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_char(?)}",//
                    Collections.singletonList(//
                            CallableSqlParameter.withOutput("out", JDBCType.NCHAR, new NCharacterTypeHandler())//
                    ));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Character;
            assert objectMap.get("out").equals('A');
            assert objectMap.get("#update-count-1").equals(0);
        }
    }
}