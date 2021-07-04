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
import com.alibaba.druid.pool.DruidDataSource;
import net.hasor.db.jdbc.SqlParameterUtils;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.ByteTypeHandler;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ByteTypeTest {
    @Test
    public void testByteTypeHandler_1() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_tinyint) values (123);");
            List<Byte> dat = jdbcTemplate.query("select c_tinyint from tb_h2_types where c_tinyint is not null limit 1;", (rs, rowNum) -> {
                return new ByteTypeHandler().getResult(rs, 1);
            });
            assert dat.get(0) == 123;
        }
    }

    @Test
    public void testByteTypeHandler_2() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2_types (c_tinyint) values (123);");
            List<Byte> dat = jdbcTemplate.query("select c_tinyint from tb_h2_types where c_tinyint is not null limit 1;", (rs, rowNum) -> {
                return new ByteTypeHandler().getResult(rs, "c_tinyint");
            });
            assert dat.get(0) == 123;
        }
    }

    @Test
    public void testByteTypeHandler_3() throws Throwable {
        try (DruidDataSource dataSource = DsUtils.createDs()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //
            byte dat1 = jdbcTemplate.queryForObject("select ?", byte.class, 12);
            Byte dat2 = jdbcTemplate.queryForObject("select ?", Byte.class, 34);
            assert dat1 == 12;
            assert dat2 == 34;
            //
            List<Byte> dat = jdbcTemplate.query("select ?", ps -> {
                new ByteTypeHandler().setParameter(ps, 1, (byte) 123, JDBCType.SMALLINT);
            }, (rs, rowNum) -> {
                return new ByteTypeHandler().getNullableResult(rs, 1);
            });
            assert dat.get(0) == 123;
        }
    }

    @Test
    public void testFloatTypeHandler_4() throws SQLException {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_smallint;");
            jdbcTemplate.execute("create procedure proc_smallint(out p_out smallint) begin set p_out=123; end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_smallint(?)}",//
                    Collections.singletonList(SqlParameterUtils.withOutput("out", JDBCType.SMALLINT, new ByteTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Byte;
            assert objectMap.get("out").equals(Byte.parseByte("123"));
            assert objectMap.get("#update-count-1").equals(0);
        }
    }
}
