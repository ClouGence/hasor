package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.ObjectTypeHandler;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ObjectTypeTest {
    @Test
    public void testObjectTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Set<String> testSet = new HashSet<>(Arrays.asList("a", "b", "c"));
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_other) values (?);", testSet);
            List<Object> dat = jdbcTemplate.query("select c_other from tb_h2_types where c_other is not null limit 1;", (rs, rowNum) -> {
                return new ObjectTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0) != testSet;
            assert dat.get(0) instanceof Set;
            assert ((Set<?>) dat.get(0)).size() == 3;
            assert ((Set<?>) dat.get(0)).contains("a");
            assert ((Set<?>) dat.get(0)).contains("b");
            assert ((Set<?>) dat.get(0)).contains("c");
        }
    }

    @Test
    public void testObjectTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Set<String> testSet = new HashSet<>(Arrays.asList("a", "b", "c"));
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_other) values (?);", testSet);
            List<Object> dat = jdbcTemplate.query("select c_other from tb_h2_types where c_other is not null limit 1;", (rs, rowNum) -> {
                return new ObjectTypeHandler().getResult(rs, "c_other");
            });
            assert dat.get(0) != testSet;
            assert dat.get(0) instanceof Set;
            assert ((Set<?>) dat.get(0)).size() == 3;
            assert ((Set<?>) dat.get(0)).contains("a");
            assert ((Set<?>) dat.get(0)).contains("b");
            assert ((Set<?>) dat.get(0)).contains("c");
        }
    }

    @Test
    public void testObjectTypeHandler_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Set<String> testSet = new HashSet<>(Arrays.asList("a", "b", "c"));
            List<Object> dat = jdbcTemplate.query("select ?", ps -> {
                new ObjectTypeHandler().setParameter(ps, 1, testSet, JDBCType.OTHER);
            }, (rs, rowNum) -> {
                return new ObjectTypeHandler().getNullableResult(rs, 1);
            });
            //
            assert dat.get(0) != testSet;
            assert dat.get(0) instanceof Set;
            assert ((Set<?>) dat.get(0)).size() == 3;
            assert ((Set<?>) dat.get(0)).contains("a");
            assert ((Set<?>) dat.get(0)).contains("b");
            assert ((Set<?>) dat.get(0)).contains("c");
        }
    }

    @Test
    public void testObjectTypeHandler_4() throws SQLException {
        //        try (Connection conn = DsUtils.localMySQL()) {
        //            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
        //            jdbcTemplate.execute("drop procedure if exists proc_double;");
        //            jdbcTemplate.execute("create procedure proc_double(out p_out double) begin set p_out=123.123; end;");
        //            //
        //            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_double(?)}",//
        //                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.DOUBLE, new DoubleTypeHandler())));
        //            //
        //            assert objectMap.size() == 2;
        //            assert objectMap.get("out") instanceof Double;
        //            assert objectMap.get("out").equals(123.123d);
        //            assert objectMap.get("#update-count-1").equals(0);
        //        }
    }
}