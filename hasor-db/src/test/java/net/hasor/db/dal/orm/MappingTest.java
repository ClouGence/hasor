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
package net.hasor.db.dal.orm;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.TypeHandlerRegistry;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.dto.TB_User;
import net.hasor.test.db.dto.TbUser;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static net.hasor.test.db.utils.TestUtils.*;

/***
 *
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class MappingTest extends AbstractDbTest {
    @Test
    public void testBigDecimalTypeHandler_1() {
        TypeHandlerRegistry registry = TypeHandlerRegistry.DEFAULT;
        MappingHandler handler = new MappingHandler(registry);
        MappingRowMapper<TbUser> resultMapper = handler.resolveMapper(TbUser.class);
        //
        assert resultMapper != null;
        assert resultMapper.getMapperClass() == TbUser.class;
        assert resultMapper.getTableName().equals("tb_user");
        assert resultMapper.isCaseInsensitive();
    }

    @Test
    public void testBeanRowMapper_1() throws SQLException {
        TypeHandlerRegistry registry = TypeHandlerRegistry.DEFAULT;
        MappingHandler handler = new MappingHandler(registry);
        MappingRowMapper<TbUser> resultMapper = handler.resolveMapper(TbUser.class);
        //
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<TbUser> resultList = jdbcTemplate.query("select * from tb_user", resultMapper);
            List<String> collect = resultList.stream().map(TbUser::getName).collect(Collectors.toList());
            //
            assert collect.size() == 3;
            assert collect.contains(beanForData1().getName());
            assert collect.contains(beanForData2().getName());
            assert collect.contains(beanForData3().getName());
        }
    }

    @Test
    public void testBeanRowMapper_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            List<TB_User> mapList = jdbcTemplate.queryForList("select * from tb_user", TB_User.class);
            //
            List<String> collect = mapList.stream().map(TB_User::getName).collect(Collectors.toList());
            //
            assert mapList.size() == 3;
            assert collect.contains(beanForData1().getName());
            assert collect.contains(beanForData2().getName());
            assert collect.contains(beanForData3().getName());
        }
    }

    @Test
    public void testBeanRowMapper_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            List<TB_User> mapList1 = jdbcTemplate.query("select * from tb_user", MappingRowMapper.newInstance(TB_User.class));
            List<TB_User> mapList2 = jdbcTemplate.query("select * from tb_user", MappingRowMapper.newInstance(TB_User.class));
            //
            List<String> collect1 = mapList1.stream().map(TB_User::getName).collect(Collectors.toList());
            List<String> collect2 = mapList2.stream().map(TB_User::getName).collect(Collectors.toList());
            //
            assert mapList1.size() == 3;
            assert collect1.contains(beanForData1().getName());
            assert collect1.contains(beanForData2().getName());
            assert collect1.contains(beanForData3().getName());
            //
            assert mapList2.size() == 3;
            assert collect2.contains(beanForData1().getName());
            assert collect2.contains(beanForData2().getName());
            assert collect2.contains(beanForData3().getName());
        }
    }
}
