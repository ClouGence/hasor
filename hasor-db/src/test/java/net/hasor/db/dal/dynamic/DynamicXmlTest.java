/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.db.dal.dynamic;
import net.hasor.db.types.UnknownTypeHandler;
import net.hasor.db.types.handler.StringTypeHandler;
import net.hasor.test.db.dal.dynamic.TextBuilderContext;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DynamicXmlTest {
    private final DynamicParser xmlParser = new DynamicParser();

    private String loadString(String queryConfig) throws IOException {
        return IOUtils.readToString(ResourcesUtils.getResourceAsStream(queryConfig), "UTF-8");
    }

    @Test
    public void ifTest_01() throws Throwable {
        String queryConfig = loadString("/net_hasor_db/dal_dynamic/fragment/if_01.xml");
        DynamicSql parseXml = xmlParser.parseDynamicSql(queryConfig);
        //
        String querySql1 = loadString("/net_hasor_db/dal_dynamic/fragment/if_01.xml.sql_1");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("ownerID", "123");
        data1.put("ownerType", "SYSTEM");
        QuerySqlBuilder builder1 = parseXml.buildQuery(new TextBuilderContext(data1));
        assert builder1.getSqlString().trim().equals(querySql1.trim());
        assert builder1.getArgs()[0].equals("123");
        assert builder1.getArgs()[1].equals("SYSTEM");
        //
        //
        String querySql2 = loadString("/net_hasor_db/dal_dynamic/fragment/if_01.xml.sql_2");
        Map<String, Object> data2 = new HashMap<>();
        data1.put("ownerID", "123");
        data1.put("ownerType", null);
        QuerySqlBuilder builder2 = parseXml.buildQuery(new TextBuilderContext(data2));
        assert builder2.getSqlString().trim().equals(querySql2.trim());
        assert builder2.getArgs().length == 0;
    }

    @Test
    public void includeTest_01() throws Throwable {
        String queryConfig = loadString("/net_hasor_db/dal_dynamic/fragment/include_01.xml");
        DynamicSql parseXml = xmlParser.parseDynamicSql(queryConfig);
        //
        String querySql1 = loadString("/net_hasor_db/dal_dynamic/fragment/include_01.xml.sql_1");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("eventType", "123");
        QuerySqlBuilder builder1 = parseXml.buildQuery(new TextBuilderContext(data1));
        assert builder1.getSqlString().trim().equals(querySql1.trim());
        assert builder1.getArgs()[0].equals("123");
    }

    @Test
    public void foreachTest_01() throws Throwable {
        String queryConfig = loadString("/net_hasor_db/dal_dynamic/fragment/foreach_03.xml");
        DynamicSql parseXml = xmlParser.parseDynamicSql(queryConfig);
        //
        String querySql1 = loadString("/net_hasor_db/dal_dynamic/fragment/foreach_03.xml.sql_1");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("eventTypes", Arrays.asList("a", "b", "c", "d", "e"));
        QuerySqlBuilder builder1 = parseXml.buildQuery(new TextBuilderContext(data1));
        assert builder1.getSqlString().trim().equals(querySql1.trim());
        assert builder1.getArgs()[0].equals("a");
        assert builder1.getArgs()[1].equals("b");
        assert builder1.getArgs()[2].equals("c");
        assert builder1.getArgs()[3].equals("d");
        assert builder1.getArgs()[4].equals("e");
    }

    @Test
    public void setTest_01() throws Throwable {
        String queryConfig = loadString("/net_hasor_db/dal_dynamic/fragment/set_04.xml");
        DynamicSql parseXml = xmlParser.parseDynamicSql(queryConfig);
        //
        String querySql1 = loadString("/net_hasor_db/dal_dynamic/fragment/set_04.xml.sql_1");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("phone", "1234");
        data1.put("email", "zyc@zyc");
        data1.put("expression", "ddd");
        data1.put("id", "~~~");
        data1.put("uid", "1111");
        QuerySqlBuilder builder1 = parseXml.buildQuery(new TextBuilderContext(data1));
        assert builder1.getSqlString().trim().equals(querySql1.trim());
        assert builder1.getArgs()[0].equals("1234");
        assert builder1.getArgs()[1].equals("zyc@zyc");
        assert builder1.getArgs()[2].equals("ddd");
        assert builder1.getArgs()[3].equals("~~~");
        assert builder1.getArgs()[4].equals("1111");
        //
        String querySql2 = loadString("/net_hasor_db/dal_dynamic/fragment/set_04.xml.sql_2");
        Map<String, Object> data2 = new HashMap<>();
        data2.put("id", "~~~");
        data2.put("uid", "1111");
        QuerySqlBuilder builder2 = parseXml.buildQuery(new TextBuilderContext(data2));
        assert builder2.getSqlString().trim().equals(querySql2.trim());
        assert builder2.getArgs()[0].equals("~~~");
        assert builder2.getArgs()[1].equals("1111");
    }

    @Test
    public void bindTest_01() throws Throwable {
        String queryConfig = loadString("/net_hasor_db/dal_dynamic/fragment/bind_01.xml");
        DynamicSql parseXml = xmlParser.parseDynamicSql(queryConfig);
        //
        String querySql1 = loadString("/net_hasor_db/dal_dynamic/fragment/bind_01.xml.sql_1");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("sellerId", "123");
        QuerySqlBuilder builder1 = parseXml.buildQuery(new TextBuilderContext(data1));
        assert builder1.getSqlString().trim().equals(querySql1.trim());
        assert builder1.getArgs()[0].equals("123abc");
    }

    @Test
    public void bindTest_02() throws Throwable {
        String queryConfig = loadString("/net_hasor_db/dal_dynamic/fragment/bind_01.xml");
        DynamicSql parseXml = xmlParser.parseDynamicSql(queryConfig);
        //
        Map<String, Object> data1 = new HashMap<>();
        data1.put("sellerId", "123");
        data1.put("abc", "aaa");
        try {
            parseXml.buildQuery(new TextBuilderContext(data1));
            assert false;
        } catch (Exception e) {
            assert e.getMessage().contains("duplicate key 'abc'");
        }
    }

    @Test
    public void bindTest_03() throws Throwable {
        String queryConfig = loadString("/net_hasor_db/dal_dynamic/fragment/bind_03.xml");
        DynamicSql parseXml = xmlParser.parseDynamicSql(queryConfig);
        //
        String querySql1 = loadString("/net_hasor_db/dal_dynamic/fragment/bind_03.xml.sql_1");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("sellerId", "123");
        data1.put("abc", "aaa");
        QuerySqlBuilder builder1 = parseXml.buildQuery(new TextBuilderContext(data1));
        assert builder1.getSqlString().trim().equals(querySql1.trim());
        assert builder1.getArgs()[0].equals("123abc");
    }

    @Test
    public void whereTest_01() throws Throwable {
        String queryConfig = loadString("/net_hasor_db/dal_dynamic/fragment/where_01.xml");
        DynamicSql parseXml = xmlParser.parseDynamicSql(queryConfig);
        //
        String querySql1 = loadString("/net_hasor_db/dal_dynamic/fragment/where_01.xml.sql_1");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("sellerId", "123");
        QuerySqlBuilder builder1 = parseXml.buildQuery(new TextBuilderContext(data1));
        assert builder1.getSqlString().trim().equals(querySql1.trim());
        //
        String querySql2 = loadString("/net_hasor_db/dal_dynamic/fragment/where_01.xml.sql_2");
        Map<String, Object> data2 = new HashMap<>();
        data2.put("state", "123");
        data2.put("title", "aaa");
        QuerySqlBuilder builder2 = parseXml.buildQuery(new TextBuilderContext(data2));
        assert builder2.getSqlString().trim().equals(querySql2.trim());
        assert builder2.getArgs()[0].equals("123");
        assert builder2.getArgs()[1].equals("aaa");
    }

    @Test
    public void chooseTest_01() throws Throwable {
        String queryConfig = loadString("/net_hasor_db/dal_dynamic/fragment/choose_01.xml");
        DynamicSql parseXml = xmlParser.parseDynamicSql(queryConfig);
        //
        String querySql1 = loadString("/net_hasor_db/dal_dynamic/fragment/choose_01.xml.sql_1");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("title", "123");
        data1.put("content", "aaa");
        QuerySqlBuilder builder1 = parseXml.buildQuery(new TextBuilderContext(data1));
        assert builder1.getSqlString().trim().equals(querySql1.trim());
        assert builder1.getArgs()[0].equals("123");
        assert builder1.getArgs()[1].equals("aaa");
    }

    @Test
    public void chooseTest_02() throws Throwable {
        String queryConfig = loadString("/net_hasor_db/dal_dynamic/fragment/choose_01.xml");
        DynamicSql parseXml = xmlParser.parseDynamicSql(queryConfig);
        //
        String querySql1 = loadString("/net_hasor_db/dal_dynamic/fragment/choose_01.xml.sql_2");
        Map<String, Object> data1 = new HashMap<>();
        QuerySqlBuilder builder1 = parseXml.buildQuery(new TextBuilderContext(data1));
        assert builder1.getSqlString().trim().equals(querySql1.trim());
    }

    @Test
    public void tokenTest_01() throws Throwable {
        String queryConfig = loadString("/net_hasor_db/dal_dynamic/fragment/token_01.xml");
        DynamicSql parseXml = xmlParser.parseDynamicSql(queryConfig);
        //
        String querySql1 = loadString("/net_hasor_db/dal_dynamic/fragment/token_01.xml.sql_1");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("abc", "123");
        data1.put("futures", "11");
        data1.put("orderBy", "user_name asc");
        data1.put("info", new HashMap<String, Object>() {{
            put("status", true);
        }});
        QuerySqlBuilder builder1 = parseXml.buildQuery(new TextBuilderContext(data1));
        assert builder1.getSqlString().trim().equals(querySql1.trim());
        assert builder1.getArgs()[0].equals("123");
        assert builder1.getArgs()[1].equals("11");
        assert builder1.getJavaType()[0] == String.class;
        assert builder1.getJavaType()[1] == net.hasor.test.db.dto.TB_User.class;
        assert builder1.getTypeHandlers()[0] instanceof StringTypeHandler;
        assert builder1.getTypeHandlers()[1] instanceof UnknownTypeHandler;
        assert builder1.getSqlModes()[0] == SqlMode.In;
        assert builder1.getSqlModes()[1] == SqlMode.Out;
    }
}
