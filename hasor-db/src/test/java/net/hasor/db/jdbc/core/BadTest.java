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
import net.hasor.db.jdbc.SqlParameterSource;
import net.hasor.test.db.AbstractDbTest;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

/***
 *
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class BadTest extends AbstractDbTest {
    @Test
    public void badTest_1() {
        try {
            new JdbcTemplate().loadSplitSQL(";", StandardCharsets.UTF_8, "abc");
        } catch (Exception e) {
            assert e.getMessage().equals("can't find resource 'abc'");
        }
    }

    @Test
    public void badTest_2() {
        try {
            new JdbcTemplate().executeBatch(new String[0]);
        } catch (NullPointerException | SQLException e) {
            assert e.getMessage().equals("SQL array must not be empty");
        }
    }

    @Test
    public void badTest_3() throws SQLException {
        int[] ints = new JdbcTemplate().executeBatch("insert abc(id) values (?)", new SqlParameterSource[0]);
        assert ints.length == 0;
    }
}
