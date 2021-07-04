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
import com.alibaba.druid.pool.DruidDataSource;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.dto.TbUser;
import net.hasor.test.db.utils.DsUtils;
import net.hasor.test.db.utils.TestUtils;
import org.junit.Test;

import java.util.List;

/***
 *
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class MappingRowMapperTest extends AbstractDbTest {
    @Test
    public void testBeanRowMapper_0() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            List<TbUser> tbUsers = jdbcTemplate.query("select * from tb_user", new MappingRowMapper<>(TbUser.class));
            assert tbUsers.size() == 3;
            assert TestUtils.beanForData1().getUserUUID().equals(tbUsers.get(0).getUid());
            assert TestUtils.beanForData2().getUserUUID().equals(tbUsers.get(1).getUid());
            assert TestUtils.beanForData3().getUserUUID().equals(tbUsers.get(2).getUid());
        }
    }
}
