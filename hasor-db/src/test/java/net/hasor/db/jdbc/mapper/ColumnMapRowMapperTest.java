package net.hasor.db.jdbc.mapper;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.hasor.test.db.utils.TestUtils.*;

public class ColumnMapRowMapperTest {
    @Test
    public void testColumnMapRowMapper_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            List<Map<String, Object>> mapList = jdbcTemplate.query("select * from tb_user", new ColumnMapRowMapper());
            //
            List<String> collect = mapList.stream().map(stringObjectMap -> {
                return (String) stringObjectMap.get("name");
            }).collect(Collectors.toList());
            //
            assert mapList.size() == 3;
            assert collect.contains(beanForData1().getName());
            assert collect.contains(beanForData2().getName());
            assert collect.contains(beanForData3().getName());
        }
    }
}