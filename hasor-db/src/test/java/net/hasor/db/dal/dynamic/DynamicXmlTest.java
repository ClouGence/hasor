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
import net.hasor.test.db.dto.CharacterSensitiveEnum;
import net.hasor.test.db.dto.LicenseOfCodeEnum;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class DynamicXmlTest {
    private final DynamicParser xmlParser = new DynamicParser();

    private String loadString(String queryConfig) throws IOException {
        return IOUtils.readToString(ResourcesUtils.getResourceAsStream(queryConfig), "UTF-8");
    }

    @Test
    public void dynamicTest_01() throws Throwable {
        String queryConfig = loadString("/net_hasor_db/dal_dynamic/fragment/query_fragment_01.xml");
        DynamicSql parseXml = xmlParser.parseDynamicSql(queryConfig);
        //
        String querySql1 = loadString("/net_hasor_db/dal_dynamic/fragment/query_fragment_01.xml.sql_1");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("ownerID", "123");
        data1.put("ownerType", "SYSTEM");
        QuerySqlBuilder builder1 = parseXml.buildQuery(new TextBuilderContext(data1));
        assert builder1.getSqlString().trim().equals(querySql1.trim());
        assert builder1.getArgs()[0].equals("123");
        assert builder1.getArgs()[1].equals("SYSTEM");
        //
        //
        String querySql2 = loadString("/net_hasor_db/dal_dynamic/fragment/query_fragment_01.xml.sql_2");
        Map<String, Object> data2 = new HashMap<>();
        data1.put("ownerID", "123");
        data1.put("ownerType", null);
        QuerySqlBuilder builder2 = parseXml.buildQuery(new TextBuilderContext(data2));
        assert builder2.getSqlString().trim().equals(querySql2.trim());
        assert builder2.getArgs().length == 0;
    }

    @Test
    public void dynamicTest_02() throws Throwable {
        String queryConfig = loadString("/net_hasor_db/dal_dynamic/fragment/query_fragment_02.xml");
        DynamicSql parseXml = xmlParser.parseDynamicSql(queryConfig);
        //
        String querySql1 = loadString("/net_hasor_db/dal_dynamic/fragment/query_fragment_02.xml.sql_1");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("eventType", "123");
        QuerySqlBuilder builder1 = parseXml.buildQuery(new TextBuilderContext(data1));
        assert builder1.getSqlString().trim().equals(querySql1.trim());
        assert builder1.getArgs()[0].equals("123");
    }

    @Test
    public void dynamicTest_03() throws Throwable {
        String queryConfig = loadString("/net_hasor_db/dal_dynamic/fragment/query_fragment_03.xml");
        DynamicSql parseXml = xmlParser.parseDynamicSql(queryConfig);
        //
        String querySql1 = loadString("/net_hasor_db/dal_dynamic/fragment/query_fragment_03.xml.sql_1");
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
    public void dynamicTest_04() throws Throwable {
        String queryConfig = loadString("/net_hasor_db/dal_dynamic/fragment/query_fragment_04.xml");
        DynamicSql parseXml = xmlParser.parseDynamicSql(queryConfig);
        //
        String querySql1 = loadString("/net_hasor_db/dal_dynamic/fragment/query_fragment_04.xml.sql_1");
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
        String querySql2 = loadString("/net_hasor_db/dal_dynamic/fragment/query_fragment_04.xml.sql_2");
        Map<String, Object> data2 = new HashMap<>();
        data2.put("id", "~~~");
        data2.put("uid", "1111");
        QuerySqlBuilder builder2 = parseXml.buildQuery(new TextBuilderContext(data2));
        assert builder2.getSqlString().trim().equals(querySql2.trim());
        assert builder2.getArgs()[0].equals("~~~");
        assert builder2.getArgs()[1].equals("1111");
    }

    @Test
    public void dynamicTest_05() throws Throwable {
        String queryConfig = loadString("/net_hasor_db/dal_dynamic/fragment/query_fragment_05.xml");
        DynamicSql parseXml = xmlParser.parseDynamicSql(queryConfig);
        //
        String querySql1 = loadString("/net_hasor_db/dal_dynamic/fragment/query_fragment_05.xml.sql_1");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("startId", "33322");
        data1.put("label", new ArrayList<>(Arrays.asList(LicenseOfCodeEnum.Private, LicenseOfCodeEnum.GPLv3)));
        data1.put("state", new ArrayList<>(Collections.singletonList(CharacterSensitiveEnum.A)));
        data1.put("consoleJobId", "123");
        QuerySqlBuilder builder1 = parseXml.buildQuery(new TextBuilderContext(data1));
        assert builder1.getSqlString().trim().equals(querySql1.trim());
        assert builder1.getArgs()[0].equals("33322");
        assert builder1.getArgs()[1].equals(LicenseOfCodeEnum.Private);
        assert builder1.getArgs()[2].equals(LicenseOfCodeEnum.GPLv3);
        assert builder1.getArgs()[3].equals(CharacterSensitiveEnum.A);
        assert builder1.getArgs()[4].equals("123");
        assert builder1.getArgs()[5] == null;
    }
}
