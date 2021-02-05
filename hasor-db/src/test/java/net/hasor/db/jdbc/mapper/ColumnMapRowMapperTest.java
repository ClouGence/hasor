/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.db.jdbc.mapper;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.sql.Connection;
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

    @Test
    public void testColumnMapRowMapper_2() throws SQLException {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            Map<String, Object> objectMap1 = jdbcTemplate.queryForObject("select 1 as T, 2 as t", new ColumnMapRowMapper(false));
            assert objectMap1.size() == 2;
            //
            Map<String, Object> objectMap2 = jdbcTemplate.queryForObject("select 1 as T, 2 as t", new ColumnMapRowMapper(true));
            assert objectMap2.size() == 1;
        }
    }
}
