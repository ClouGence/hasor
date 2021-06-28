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
import net.hasor.test.db.AbstractDbTest;
import org.junit.Before;
import org.junit.Test;

import java.sql.JDBCType;

/***
 *
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class MapperMappingTest extends AbstractDbTest {
    private MappingRegistry mappingRegistry;

    @Before
    public void beforeTest() throws Exception {
        this.mappingRegistry = MappingRegistry.newInstance();
        this.mappingRegistry.loadMapper("/net_hasor_db/mapping/mapper_1.xml");
    }

    @Test
    public void mapperTest_01() {
        TableMapping tableMapping = mappingRegistry.getMapping("resultMap_test.resultMap_1");
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
    public void mapperTest_02() {
        TableMapping tableMapping = mappingRegistry.getMapping("resultMap_test.resultMap_2");
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
    public void mapperTest_03() {
        TableMapping tableMapping = mappingRegistry.getMapping("resultMap_test.resultMap_3");
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
    public void mapperTest_04() {
        TableMapping tableMapping = mappingRegistry.getMapping("resultMap_test.resultMap_4");
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
