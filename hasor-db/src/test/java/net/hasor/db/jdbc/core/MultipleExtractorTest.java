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
package net.hasor.db.jdbc.core;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.utils.DsUtils;
import net.hasor.test.db.utils.TestUtils;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.hasor.test.db.utils.TestUtils.*;

/***
 * @version : 2020-11-12
 * @author 赵永春 (zyc@hasor.net)
 */
public class MultipleExtractorTest extends AbstractDbTest {
    @Test
    public void testMultipleResultExtractor_1() throws SQLException, IOException {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop table if exists tb_user;");
            jdbcTemplate.loadSQL("net_hasor_db/tb_user_for_mysql.sql");
            String insertSql = INSERT_ARRAY.replace("index", "`index`");
            jdbcTemplate.executeUpdate(insertSql, arrayForData1());
            jdbcTemplate.executeUpdate(insertSql, arrayForData2());
            jdbcTemplate.executeUpdate(insertSql, arrayForData3());
            //
            String multipleSql = ""//
                    + "select * from tb_user where loginName = 'muhammad';\n"//
                    + "select * from tb_user where loginName = 'belon';";
            List<Object> objectList = jdbcTemplate.multipleExecute(multipleSql);
            //
            assert objectList.size() == 2;
            assert objectList.get(0) instanceof ArrayList;
            assert objectList.get(1) instanceof ArrayList;
            assert ((ArrayList<?>) objectList.get(0)).size() == 1;
            assert ((ArrayList<?>) objectList.get(1)).size() == 1;
            assert ((ArrayList<?>) objectList.get(0)).get(0) instanceof Map;
            assert ((ArrayList<?>) objectList.get(1)).get(0) instanceof Map;
            assert ((Map) ((ArrayList<?>) objectList.get(0)).get(0)).get("userUUID").equals(TestUtils.beanForData1().getUserUUID());
            assert ((Map) ((ArrayList<?>) objectList.get(1)).get(0)).get("userUUID").equals(TestUtils.beanForData2().getUserUUID());
        }
    }
}