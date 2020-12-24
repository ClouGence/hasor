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
import net.hasor.db.jdbc.paramer.MapSqlParameterSource;
import net.hasor.test.db.AbstractDbTest;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/***
 *
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class UtilsTest extends AbstractDbTest {
    @Test
    public void cleanupParameter_1() {
        StatementSetterUtils.cleanupParameter(null);
        StatementSetterUtils.cleanupParameter(1);
        StatementSetterUtils.cleanupParameter(true);
        StatementSetterUtils.cleanupParameter("true");
        StatementSetterUtils.cleanupParameter(new Object());
        //
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        ParameterDisposer disposer = () -> atomicBoolean.set(true);
        assert !atomicBoolean.get();
        StatementSetterUtils.cleanupParameter(disposer);
        assert atomicBoolean.get();
    }

    @Test
    public void cleanupParameter_2() {
        StatementSetterUtils.cleanupParameters((Collection) null);
    }

    @Test
    public void buildSql_1() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("abc", "abcabc");
        SqlParameterSource source = new MapSqlParameterSource(hashMap);
        ParsedSql parsedSql = ParsedSql.getParsedSql("select :abc");
        //
        String buildSql = parsedSql.buildSql(source);
        List<String> parameterNames = parsedSql.getParameterNames();
        //
        assert buildSql.equals("select ?");
        assert parameterNames.get(0).equals("abc");
    }

    @Test
    public void buildSql_2() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("abc", "abcabc");
        SqlParameterSource source = new MapSqlParameterSource(hashMap);
        //
        String buildSql1 = ParsedSql.getParsedSql("select :abc /* :abc */").buildSql(source);
        assert buildSql1.equals("select ? /* :abc */");
        //
        String buildSql2 = ParsedSql.getParsedSql("-- select :abc /* :abc */").buildSql(source);
        assert buildSql2.equals("-- select :abc /* :abc */");
        //
        String buildSql3 = ParsedSql.getParsedSql("select :abc /* :abc */ from :abc").buildSql(source);
        assert buildSql3.equals("select ? /* :abc */ from ?");
        //
        String buildSql4 = ParsedSql.getParsedSql("select :abc /* :abc from :abc").buildSql(source);
        assert buildSql4.equals("select ? /* :abc from :abc");
    }

    @Test
    public void buildSql_3() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("abc", "abcabc");
        SqlParameterSource source = new MapSqlParameterSource(hashMap);
        //
        String buildSql1 = ParsedSql.getParsedSql("select &abc /* :abc */").buildSql(source);
        assert buildSql1.equals("select ? /* :abc */");
        //
        String buildSql2 = ParsedSql.getParsedSql("-- select :abc /* :abc */").buildSql(source);
        assert buildSql2.equals("-- select :abc /* :abc */");
        //
        String buildSql3 = ParsedSql.getParsedSql("select &abc /* :abc */ from &abc").buildSql(source);
        assert buildSql3.equals("select ? /* :abc */ from ?");
        //
        String buildSql4 = ParsedSql.getParsedSql("select &abc /* :abc from &abc").buildSql(source);
        assert buildSql4.equals("select ? /* :abc from &abc");
    }

    @Test
    public void buildSql_4() throws SQLException {
        List<String> arrays = new ArrayList<>();
        arrays.add("a");
        arrays.add("b");
        arrays.add("c");
        //
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("abc", arrays);
        SqlParameterSource source = new MapSqlParameterSource(hashMap);
        //
        String buildSql = ParsedSql.getParsedSql("select &abc").buildSql(source);
        Object[] buildValues = ParsedSql.getParsedSql("select &abc").buildValues(source);
        //
        assert buildSql.equals("select ?, ?, ?");
        assert buildValues.length == 3;
        assert buildValues[0].equals("a");
        assert buildValues[1].equals("b");
        assert buildValues[2].equals("c");
    }
}