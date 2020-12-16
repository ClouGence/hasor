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
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

/***
 * 存储过程测试
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class CallerTest extends AbstractDbTest {
    @Test
    public void insert_test_1() throws SQLException {
        String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/local_test?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&user=root&password=123258";
        try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call out_param_int_1(?)}",//
                    Arrays.asList(CallableSqlParameter.withOutput(JDBCType.INTEGER)));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("#out-1").equals(2);
            assert objectMap.get("#update-count-1").equals(0);
        }
    }
}