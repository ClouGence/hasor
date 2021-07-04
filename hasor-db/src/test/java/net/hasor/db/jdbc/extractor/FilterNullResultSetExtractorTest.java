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
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.hasor.test.db.utils.TestUtils.*;

/***
 * @version : 2020-11-12
 * @author 赵永春 (zyc@hasor.net)
 */
public class FilterNullResultSetExtractorTest extends AbstractDbTest {
    @Test
    public void testFilterNullResultSetExtractor_1() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            jdbcTemplate.executeUpdate(INSERT_ARRAY, arrayForData6());
            jdbcTemplate.executeUpdate(INSERT_ARRAY, arrayForData7());
            //
            ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();
            FilterResultSetExtractor<Map<String, Object>> fullExtractor = new FilterResultSetExtractor<>(rowMapper);
            FilterResultSetExtractor<Map<String, Object>> nonullExtractor = new FilterResultSetExtractor<>(rowMapper);
            nonullExtractor.setRowTester(data -> data.get("loginPassword") != null);
            //
            List<Map<String, Object>> mapList1 = jdbcTemplate.query("select * from tb_user", nonullExtractor);
            List<Map<String, Object>> mapList2 = jdbcTemplate.query("select * from tb_user", fullExtractor);
            //
            assert mapList1.size() == 3;
            assert mapList2.size() == 5;
        }
    }

    @Test
    public void testFilterNullResultSetExtractor_2() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            jdbcTemplate.executeUpdate(INSERT_ARRAY, arrayForData6());
            jdbcTemplate.executeUpdate(INSERT_ARRAY, arrayForData7());
            //
            FilterResultSetExtractor<Map<String, Object>> nonullExtractor = new FilterResultSetExtractor<>(//
                    data -> data.get("loginPassword") != null,//
                    new ColumnMapRowMapper()//
            );
            //
            List<Map<String, Object>> mapList = jdbcTemplate.query("select * from tb_user", nonullExtractor);
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
    public void testFilterNullResultSetExtractor_3() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            jdbcTemplate.executeUpdate(INSERT_ARRAY, arrayForData6()); // loginPassword is null
            jdbcTemplate.executeUpdate(INSERT_ARRAY, arrayForData7()); // loginPassword is null
            //
            FilterResultSetExtractor<Map<String, Object>> nonullExtractor = new FilterResultSetExtractor<>(//
                    new ColumnMapRowMapper(),//
                    2);
            nonullExtractor.setRowTester(data -> data.get("loginPassword") != null);
            //
            List<Map<String, Object>> mapList = jdbcTemplate.query("select * from tb_user order by index asc", nonullExtractor);
            List<String> collect = mapList.stream().map(stringObjectMap -> {
                return (String) stringObjectMap.get("name");
            }).collect(Collectors.toList());
            //
            assert mapList.size() == 2;
            assert collect.contains(beanForData1().getName());
            assert collect.contains(beanForData2().getName());
        }
    }

    @Test
    public void testFilterNullResultSetExtractor_4() {
        Predicate<Map<String, Object>> test = data -> data.get("loginPassword") != null;
        FilterResultSetExtractor<Map<String, Object>> nonullExtractor = new FilterResultSetExtractor<>(new ColumnMapRowMapper(), 2);
        nonullExtractor.setRowTester(test);
        //
        assert nonullExtractor.getRowTester() == test;
    }
}
