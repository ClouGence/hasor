package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.NumberTypeHandler;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class NumberTypeTest {
    @Test
    public void testNumberTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Date testData = new Date();
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", testData);
            List<Number> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
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
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_timestamp) values (?);", testData);
            List<Number> dat = jdbcTemplate.query("select c_timestamp from tb_h2types where c_timestamp is not null limit 1;", (rs, rowNum) -> {
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
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_char) values ('123');");
            List<Number> dat = jdbcTemplate.query("select c_char from tb_h2types where c_char is not null limit 1;", (rs, rowNum) -> {
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
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_clob) values ('123');");
            List<Number> dat = jdbcTemplate.query("select c_clob from tb_h2types where c_clob is not null limit 1;", (rs, rowNum) -> {
                return new NumberTypeHandler().getResult(rs, "c_clob");
            });
            //
            assert dat.get(0).longValue() == 123;
        }
    }
}