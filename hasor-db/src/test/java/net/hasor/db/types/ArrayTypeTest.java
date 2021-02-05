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
package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.ArrayTypeHandler;
import net.hasor.test.db.SingleDsModule;
import org.junit.Test;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArrayTypeTest {
    @Test
    public void testArrayTypeHandler_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Set<String> testSet = new HashSet<>(Arrays.asList("a", "b", "c"));
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_array) values (?);", ps -> {
                new ArrayTypeHandler().setParameter(ps, 1, testSet.toArray(), JDBCType.ARRAY);
            });
            List<Object> dat = jdbcTemplate.query("select c_array from tb_h2_types where c_array is not null limit 1;", (rs, rowNum) -> {
                return new ArrayTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0) != testSet;
            assert dat.get(0) instanceof Object[];
            assert ((Object[]) dat.get(0)).length == 3;
            assert ((Object[]) dat.get(0))[0].equals("a");
            assert ((Object[]) dat.get(0))[1].equals("b");
            assert ((Object[]) dat.get(0))[2].equals("c");
        }
    }

    @Test
    public void testArrayTypeHandler_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            Set<String> testSet = new HashSet<>(Arrays.asList("a", "b", "c"));
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_array) values (?);", ps -> {
                new ArrayTypeHandler().setParameter(ps, 1, testSet.toArray(), JDBCType.ARRAY);
            });
            List<Object> dat = jdbcTemplate.query("select c_array from tb_h2_types where c_array is not null limit 1;", (rs, rowNum) -> {
                return new ArrayTypeHandler().getResult(rs, "c_array");
            });
            assert dat.get(0) != testSet;
            assert dat.get(0) instanceof Object[];
            assert ((Object[]) dat.get(0)).length == 3;
            assert ((Object[]) dat.get(0))[0].equals("a");
            assert ((Object[]) dat.get(0))[1].equals("b");
            assert ((Object[]) dat.get(0))[2].equals("c");
        }
    }

    @Test
    public void testArrayTypeHandler_4() throws SQLException {
        throw new UnsupportedOperationException("not found way to test.");
        //        try (Connection conn = DsUtils.localMySQL()) {
        //            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
        //            jdbcTemplate.execute("drop procedure if exists proc_bytes;");
        //            jdbcTemplate.execute("create procedure proc_bytes(out p_out varbinary(10)) begin set p_out= b'0111111100001111'; end;");
        //            //
        //            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_bytes(?)}",//
        //                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.ARRAY, new ArrayTypeHandler())));
        //            //
        //            assert objectMap.size() == 2;
        //            assert objectMap.get("out") instanceof Double;
        //            assert objectMap.get("out").equals(123.123d);
        //            assert objectMap.get("#update-count-1").equals(0);
        //        }
    }
}
