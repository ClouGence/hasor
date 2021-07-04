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
package net.hasor.db.jdbc.extractor;
import com.alibaba.druid.pool.DruidDataSource;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.mapper.ColumnMapRowMapper;
import net.hasor.db.types.TypeHandlerRegistry;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static net.hasor.test.db.utils.TestUtils.*;

/***
 * @version : 2020-11-12
 * @author 赵永春 (zyc@hasor.net)
 */
public class ColumnMapResultSetExtractorTest extends AbstractDbTest {
    @Test
    public void testColumnMapResultSetExtractor_2() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            List<Map<String, Object>> mapList1 = jdbcTemplate.query("select * from tb_user", new ColumnMapResultSetExtractor(1));
            List<Map<String, Object>> mapList2 = jdbcTemplate.query("select * from tb_user", new ColumnMapResultSetExtractor());
            List<Map<String, Object>> mapList3 = jdbcTemplate.query("select * from tb_user", new ColumnMapResultSetExtractor(1, TypeHandlerRegistry.DEFAULT));
            List<Map<String, Object>> mapList4 = jdbcTemplate.query("select * from tb_user", new ColumnMapResultSetExtractor(false, 1));
            //
            assert mapList1.size() == 1;
            assert mapList2.size() == 3;
            assert mapList3.size() == 1;
            assert mapList4.size() == 1;
        }
    }

    @Test
    public void testRowMapperResultSetExtractor_1() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();
            List<Map<String, Object>> mapList1 = jdbcTemplate.query("select * from tb_user", new RowMapperResultSetExtractor<>(rowMapper, 1));
            List<Map<String, Object>> mapList2 = jdbcTemplate.query("select * from tb_user", new RowMapperResultSetExtractor<>(rowMapper));
            //
            assert mapList1.size() == 1;
            assert mapList2.size() == 3;
        }
    }

    @Test
    public void testColumnMapResultSetExtractor_1() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            String dataId = beanForData4().getUserUUID();
            Object[] dataArgs = arrayForData4();
            List<Map<String, Object>> mapList = null;
            //
            // before
            mapList = jdbcTemplate.query("select * from tb_user where userUUID =?", new ColumnMapResultSetExtractor(), dataId);
            assert mapList.size() == 0;
            // after
            jdbcTemplate.executeUpdate(INSERT_ARRAY, dataArgs);
            mapList = jdbcTemplate.query("select * from tb_user where userUUID =?", new ColumnMapResultSetExtractor(), dataId);
            assert mapList.size() == 1;
            assert mapList.get(0).get("name").equals(beanForData4().getName());
        }
    }
}
