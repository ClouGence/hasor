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
import net.hasor.db.jdbc.extractor.MultipleResultSetExtractor;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/***
 * 存储过程测试
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class Caller1Test extends AbstractDbTest {
    @Before
    public void init() throws SQLException {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop table if exists proc_table;");
            jdbcTemplate.execute("create table proc_table( c_id int primary key, c_name varchar(200));");
            jdbcTemplate.execute("insert into proc_table (c_id,c_name) values (1, 'aaa');");
            jdbcTemplate.execute("insert into proc_table (c_id,c_name) values (2, 'bbb');");
            jdbcTemplate.execute("insert into proc_table (c_id,c_name) values (3, 'ccc');");
            //
            jdbcTemplate.execute("drop procedure if exists proc_select_table;");
            jdbcTemplate.execute("create procedure proc_select_table(in p_name varchar(200)) begin select * from proc_table where c_name = p_name ; end;");
        }
    }

    @Test
    public void mysqlCallResultSet_1() throws SQLException {
        try (Connection conn = DsUtils.localMySQL()) {
            Map<String, Object> objectMap = new JdbcTemplate(conn).call("{call proc_select_table(?)}",//
                    Collections.singletonList(SqlParameterUtils.withInput("aaa", JDBCType.VARCHAR)));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("#result-set-1") instanceof ArrayList;
            assert objectMap.get("#update-count-2").equals(0);
            assert ((ArrayList<?>) objectMap.get("#result-set-1")).size() == 1;
            assert ((Map) ((ArrayList<?>) objectMap.get("#result-set-1")).get(0)).get("c_name").equals("aaa");
            assert ((Map) ((ArrayList<?>) objectMap.get("#result-set-1")).get(0)).get("c_id").equals(1);
        }
    }

    @Test
    public void mysqlCallResultSet_2() throws SQLException {
        try (Connection conn = DsUtils.localMySQL()) {
            Map<String, Object> objectMap = new JdbcTemplate(conn).call("{call proc_select_multiple_table(?)}",//
                    Collections.singletonList(SqlParameterUtils.withInput("aaa", JDBCType.VARCHAR)));
            //
            assert objectMap.size() == 3;
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

    @Test
    public void mysqlCallResultSet_3() throws SQLException {
        try (Connection conn = DsUtils.localMySQL()) {
            List<Object> objectMap = new JdbcTemplate(conn).call("{call proc_select_multiple_table(?)}", cs -> {
                cs.setString(1, "aaa");
            }, new MultipleResultSetExtractor());
            //
            assert objectMap.size() == 3;
            assert objectMap.get(0) instanceof ArrayList;
            assert objectMap.get(1) instanceof ArrayList;
            assert ((ArrayList<?>) objectMap.get(0)).size() == 1;
            assert ((ArrayList<?>) objectMap.get(1)).size() == 1;
            assert ((Map) ((ArrayList<?>) objectMap.get(0)).get(0)).get("c_name").equals("aaa");
            assert ((Map) ((ArrayList<?>) objectMap.get(0)).get(0)).get("c_id").equals(1);
            assert ((Map) ((ArrayList<?>) objectMap.get(1)).get(0)).get("c_name").equals("aaa");
            assert ((Map) ((ArrayList<?>) objectMap.get(1)).get(0)).get("c_id").equals(1);
        }
    }

    @Test
    public void mysqlCallResultSet_4() throws SQLException {
        try (Connection conn = DsUtils.localMySQL()) {
            List<Object> objectMap = new JdbcTemplate(conn).call(con -> {
                return con.prepareCall("{call proc_select_multiple_table(?)}");
            }, cs -> {
                cs.setString(1, "aaa");
                return new MultipleResultSetExtractor().doInCallableStatement(cs);
            });
            //
            assert objectMap.size() == 3;
            assert objectMap.get(0) instanceof ArrayList;
            assert objectMap.get(1) instanceof ArrayList;
            assert ((ArrayList<?>) objectMap.get(0)).size() == 1;
            assert ((ArrayList<?>) objectMap.get(1)).size() == 1;
            assert ((Map) ((ArrayList<?>) objectMap.get(0)).get(0)).get("c_name").equals("aaa");
            assert ((Map) ((ArrayList<?>) objectMap.get(0)).get(0)).get("c_id").equals(1);
            assert ((Map) ((ArrayList<?>) objectMap.get(1)).get(0)).get("c_name").equals("aaa");
            assert ((Map) ((ArrayList<?>) objectMap.get(1)).get(0)).get("c_id").equals(1);
        }
    }

    @Test
    public void mysqlCallResultSet_5() throws SQLException {
        try (Connection conn = DsUtils.localMySQL()) {
            List<Object> objectMap = new JdbcTemplate(conn).call("{call proc_select_multiple_table(?)}", cs -> {
                cs.setString(1, "aaa");
                return new MultipleResultSetExtractor().doInCallableStatement(cs);
            });
            //
            assert objectMap.size() == 3;
            assert objectMap.get(0) instanceof ArrayList;
            assert objectMap.get(1) instanceof ArrayList;
            assert ((ArrayList<?>) objectMap.get(0)).size() == 1;
            assert ((ArrayList<?>) objectMap.get(1)).size() == 1;
            assert ((Map) ((ArrayList<?>) objectMap.get(0)).get(0)).get("c_name").equals("aaa");
            assert ((Map) ((ArrayList<?>) objectMap.get(0)).get(0)).get("c_id").equals(1);
            assert ((Map) ((ArrayList<?>) objectMap.get(1)).get(0)).get("c_name").equals("aaa");
            assert ((Map) ((ArrayList<?>) objectMap.get(1)).get(0)).get("c_id").equals(1);
        }
    }

    @Test
    public void mysqlCallResultSet_6() throws SQLException {
        try (Connection conn = DsUtils.localMySQL()) {
            List<Object> objectMap = new JdbcTemplate(conn).call("{call proc_select_multiple_table(?)}", cs -> {
                cs.setString(1, "aaa");
                return new MultipleResultSetExtractor().doInCallableStatement(cs);
            });
            //
            assert objectMap.size() == 3;
            assert objectMap.get(0) instanceof ArrayList;
            assert objectMap.get(1) instanceof ArrayList;
            assert ((ArrayList<?>) objectMap.get(0)).size() == 1;
            assert ((ArrayList<?>) objectMap.get(1)).size() == 1;
            assert ((Map) ((ArrayList<?>) objectMap.get(0)).get(0)).get("c_name").equals("aaa");
            assert ((Map) ((ArrayList<?>) objectMap.get(0)).get(0)).get("c_id").equals(1);
            assert ((Map) ((ArrayList<?>) objectMap.get(1)).get(0)).get("c_name").equals("aaa");
            assert ((Map) ((ArrayList<?>) objectMap.get(1)).get(0)).get("c_id").equals(1);
        }
    }
}
