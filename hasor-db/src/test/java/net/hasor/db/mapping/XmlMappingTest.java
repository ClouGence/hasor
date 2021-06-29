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
package net.hasor.db.mapping;
import net.hasor.db.mapping.resolve.MappingOptions;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.sql.JDBCType;

/***
 *
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class XmlMappingTest extends AbstractDbTest {
    private String loadString(String queryConfig) throws IOException {
        return IOUtils.readToString(ResourcesUtils.getResourceAsStream(queryConfig), "UTF-8");
    }

    @Test
    public void mapperTest_01() throws Exception {
        String mapperString1 = loadString("/net_hasor_db/mapping/fragment/mapper_1_1.xml");
        TableMapping tableMapping1_1 = MappingRegistry.DEFAULT.loadReader("mapperTest_011", mapperString1, new MappingOptions()).getTableMapping();
        TableMapping tableMapping1_2 = MappingRegistry.DEFAULT.loadReader("mapperTest_011", mapperString1, new MappingOptions()).getTableMapping();
        assert tableMapping1_1 == tableMapping1_2;
        //
        String mapperString2 = loadString("/net_hasor_db/mapping/fragment/mapper_1_2.xml");
        TableMapping tableMapping2_1 = MappingRegistry.DEFAULT.loadReader("mapperTest_012", mapperString2, new MappingOptions()).getTableMapping();
        TableMapping tableMapping2_2 = MappingRegistry.DEFAULT.loadReader("mapperTest_012", mapperString2, new MappingOptions()).getTableMapping();
        assert tableMapping2_1 != tableMapping2_2;
    }

    @Test
    public void mapperTest_02() throws Exception {
        String mapperString = loadString("/net_hasor_db/mapping/fragment/mapper_2.xml");
        TableMapping tableMapping = MappingRegistry.DEFAULT.loadReader("mapperTest_02", mapperString, new MappingOptions()).getTableMapping();
        //
        assert tableMapping.getMapping("uid").getName().equals("userUUID");
        assert tableMapping.getMapping("name").getName().equals("name");
        assert tableMapping.getMapping("account").getName().equals("loginName");
        assert tableMapping.getMapping("password").getName().equals("loginPassword");
        assert tableMapping.getMapping("mail").getName().equals("email");
        assert tableMapping.getMapping("index").getName().equals("index");
        assert tableMapping.getMapping("createTime").getName().equals("registerTime");
        //
        assert tableMapping.getTable().equals("tb_user");
        assert tableMapping.getMapping("mail").getJdbcType() == JDBCType.VARCHAR;
        assert tableMapping.getMapping("index").getJdbcType() == JDBCType.INTEGER;
        assert tableMapping.getMapping("createTime").getJdbcType() == JDBCType.TIMESTAMP;
    }

    @Test
    public void mapperTest_03() throws Exception {
        String mapperString = loadString("/net_hasor_db/mapping/fragment/mapper_3.xml");
        TableMapping tableMapping = MappingRegistry.DEFAULT.loadReader("mapperTest_03", mapperString, new MappingOptions()).getTableMapping();
        //
        assert tableMapping.getMapping("uid").getName().equals("uid");
        assert tableMapping.getMapping("name").getName().equals("name");
        assert tableMapping.getMapping("account").getName().equals("account");
        assert tableMapping.getMapping("password").getName().equals("password");
        assert tableMapping.getMapping("mail").getName().equals("mail");
        assert tableMapping.getMapping("index").getName().equals("index");
        assert tableMapping.getMapping("createTime").getName().equals("createTime");
        //
        assert tableMapping.getTable().equals("TbUser2");
        assert tableMapping.getMapping("mail").getJdbcType() == JDBCType.VARCHAR;
        assert tableMapping.getMapping("index").getJdbcType() == JDBCType.INTEGER;
        assert tableMapping.getMapping("createTime").getJdbcType() == JDBCType.TIMESTAMP;
    }

    @Test
    public void mapperTest_04() throws Exception {
        String mapperString = loadString("/net_hasor_db/mapping/fragment/mapper_4.xml");
        TableMapping tableMapping = MappingRegistry.DEFAULT.loadReader("mapperTest_04", mapperString, new MappingOptions()).getTableMapping();
        //
        assert tableMapping.getMapping("uid").getName().equals("user_uuid");
        assert tableMapping.getMapping("name").getName().equals("name");
        assert tableMapping.getMapping("account").getName().equals("login_name");
        assert tableMapping.getMapping("password").getName().equals("login_password");
        assert tableMapping.getMapping("mail").getName().equals("email");
        assert tableMapping.getMapping("index").getName().equals("index");
        assert tableMapping.getMapping("createTime").getName().equals("register_time");
        //
        assert tableMapping.getTable().equals("TbUser2");
        assert tableMapping.getMapping("mail").getJdbcType() == JDBCType.VARCHAR;
        assert tableMapping.getMapping("index").getJdbcType() == JDBCType.INTEGER;
        assert tableMapping.getMapping("createTime").getJdbcType() == JDBCType.TIMESTAMP;
    }

    @Test
    public void mapperTest_05() throws Exception {
        String mapperString = loadString("/net_hasor_db/mapping/fragment/mapper_5.xml");
        TableMapping tableMapping = MappingRegistry.DEFAULT.loadReader("mapperTest_05", mapperString, new MappingOptions()).getTableMapping();
        //
        assert tableMapping.getMapping("uid").getName().equals("uid");
        assert tableMapping.getMapping("name").getName().equals("name");
        assert tableMapping.getMapping("account").getName().equals("account");
        assert tableMapping.getMapping("password").getName().equals("password");
        assert tableMapping.getMapping("mail").getName().equals("mail");
        assert tableMapping.getMapping("index").getName().equals("index");
        assert tableMapping.getMapping("createTime").getName().equals("create_time");
        //
        assert tableMapping.getTable().equals("tb_user2");
        assert tableMapping.getMapping("mail").getJdbcType() == JDBCType.VARCHAR;
        assert tableMapping.getMapping("index").getJdbcType() == JDBCType.INTEGER;
        assert tableMapping.getMapping("createTime").getJdbcType() == JDBCType.TIMESTAMP;
    }
}
