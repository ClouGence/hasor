package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.CallableSqlParameter;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.LongTypeHandler;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LongTypeTest {
    @Test
    public void testLongTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_bigint) values (123);");
            List<Long> dat = jdbcTemplate.query("select c_bigint from tb_h2_types where c_bigint is not null limit 1;", (rs, rowNum) -> {
                return new LongTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0) == 123l;
        }
    }

    @Test
    public void testLongTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_bigint) values (123);");
            List<Long> dat = jdbcTemplate.query("select c_bigint from tb_h2_types where c_bigint is not null limit 1;", (rs, rowNum) -> {
                return new LongTypeHandler().getResult(rs, "c_bigint");
            });
            assert dat.get(0) == 123l;
        }
    }

    @Test
    public void testLongTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            long dat1 = jdbcTemplate.queryForObject("select ?", long.class, 123l);
            Long dat2 = jdbcTemplate.queryForObject("select ?", Long.class, 123l);
            assert dat1 == 123l;
            assert dat2 == 123l;
            //
            List<Long> dat = jdbcTemplate.query("select ?", ps -> {
                new LongTypeHandler().setParameter(ps, 1, 123l, JDBCType.BIGINT);
            }, (rs, rowNum) -> {
                return new LongTypeHandler().getNullableResult(rs, 1);
            });
            assert dat.get(0) == 123l;
        }
    }

    @Test
    public void testLongTypeHandler_4() throws SQLException {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_bigint;");
            jdbcTemplate.execute("create procedure proc_bigint(out p_out bigint) begin set p_out=123123; end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_bigint(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.BIGINT, new LongTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Long;
            assert objectMap.get("out").equals(Long.parseLong("123123"));
            assert objectMap.get("#update-count-1").equals(0);
        }
    }
}