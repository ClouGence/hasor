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
import net.hasor.db.jdbc.SqlParameterUtils;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/***
 * 存储过程测试
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class Caller2Test extends AbstractDbTest {
    @Test
    public void mysqlCallCross_1() throws SQLException {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop table if exists proc_table;");
            jdbcTemplate.execute("create table proc_table( c_id int primary key, c_name varchar(200));");
            jdbcTemplate.execute("insert into proc_table (c_id,c_name) values (1, 'aaa');");
            jdbcTemplate.execute("insert into proc_table (c_id,c_name) values (2, 'bbb');");
            jdbcTemplate.execute("insert into proc_table (c_id,c_name) values (3, 'ccc');");
            //
            jdbcTemplate.execute("drop procedure if exists proc_select_cross_table;");
            jdbcTemplate.execute(""//
                    + "create procedure proc_select_cross_table(in p_name varchar(200), out p_out varchar(200))" //
                    + " begin " //
                    + "   select * from proc_table where c_name = p_name ;" //
                    + "   select * from proc_table where c_name = p_name ;" //
                    + "   set p_out = p_name;"//
                    + " end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_select_cross_table(?,?)}",//
                    Arrays.asList(//
                            SqlParameterUtils.withInput("aaa", JDBCType.VARCHAR),//
                            SqlParameterUtils.withOutput("bbb", JDBCType.VARCHAR)));
            //
            assert objectMap.size() == 4;
            assert objectMap.get("bbb").equals("aaa");
            assert objectMap.get("#result-set-1") instanceof ArrayList;
            assert objectMap.get("#result-set-2") instanceof ArrayList;
            assert objectMap.get("#update-count-3").equals(0);
            assert ((ArrayList<?>) objectMap.get("#result-set-1")).size() == 1;
            assert ((ArrayList<?>) objectMap.get("#result-set-2")).size() == 1;
            assert ((Map) ((ArrayList<?>) objectMap.get("#result-set-1")).get(0)).get("c_name").equals("aaa");
            assert ((Map) ((ArrayList<?>) objectMap.get("#result-set-1")).get(0)).get("c_id").equals(1);
            assert ((Map) ((ArrayList<?>) objectMap.get("#result-set-2")).get(0)).get("c_name").equals("aaa");
            assert ((Map) ((ArrayList<?>) objectMap.get("#result-set-2")).get(0)).get("c_id").equals(1);
        }
    }
}
