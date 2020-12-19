package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.CallableSqlParameter;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.ByteTypeHandler;
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

public class ByteTypeTest {
    @Test
    public void testByteTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_tinyint) values (123);");
            List<Byte> dat = jdbcTemplate.query("select c_tinyint from tb_h2types where c_tinyint is not null limit 1;", (rs, rowNum) -> {
                return new ByteTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0) == 123;
        }
    }

    @Test
    public void testByteTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_tinyint) values (123);");
            List<Byte> dat = jdbcTemplate.query("select c_tinyint from tb_h2types where c_tinyint is not null limit 1;", (rs, rowNum) -> {
                return new ByteTypeHandler().getResult(rs, "c_tinyint");
            });
            assert dat.get(0) == 123;
        }
    }

    @Test
    public void testByteTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            byte dat1 = jdbcTemplate.queryForObject("select ?", byte.class, 12);
            Byte dat2 = jdbcTemplate.queryForObject("select ?", Byte.class, 34);
            assert dat1 == 12;
            assert dat2 == 34;
            //
            List<Byte> dat = jdbcTemplate.query("select ?", ps -> {
                new ByteTypeHandler().setParameter(ps, 1, (byte) 123, JDBCType.SMALLINT);
            }, (rs, rowNum) -> {
                return new ByteTypeHandler().getNullableResult(rs, 1);
            });
            assert dat.get(0) == 123;
        }
    }

    @Test
    public void testFloatTypeHandler_4() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DsUtils.JDBC_URL)) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_smallint;");
            jdbcTemplate.execute("create procedure proc_smallint(out p_out smallint) begin set p_out=123; end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_smallint(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.SMALLINT, new ByteTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Byte;
            assert objectMap.get("out").equals(Byte.parseByte("123"));
            assert objectMap.get("#update-count-1").equals(0);
        }
    }
}