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
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.extractor.RowMapperResultSetExtractor;
import net.hasor.db.mapping.reader.TableReader;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.dto.TbUser;
import net.hasor.test.db.utils.TestUtils;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

/***
 *
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class ClassMappingTest extends AbstractDbTest {
    @Test
    public void useMappingReadTable_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TableReader<TbUser> tableReader = MappingRegistry.DEFAULT.resolveTableReader(TbUser.class);
            List<TbUser> tbUsers = jdbcTemplate.query("select * from tb_user", new RowMapperResultSetExtractor<>(tableReader::readRow));
            assert tbUsers.size() == 3;
            assert TestUtils.beanForData1().getUserUUID().equals(tbUsers.get(0).getUid());
            assert TestUtils.beanForData2().getUserUUID().equals(tbUsers.get(1).getUid());
            assert TestUtils.beanForData3().getUserUUID().equals(tbUsers.get(2).getUid());
        }
    }

    @Test
    public void testBeanRowMapper_1() throws SQLException {
        TableReader<TbUser> tableReader = MappingRegistry.DEFAULT.resolveTableReader(TbUser.class);
        //        TypeHandlerRegistry registry = TypeHandlerRegistry.DEFAULT;
        //        MappingRegistry handler = new MappingRegistry(registry);
        //        MappingRowMapper<TbUser> resultMapper = handler.resolveMapper(TbUser.class);
        //        //
        //        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
        //            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        //            //
        //            List<TbUser> resultList = jdbcTemplate.query("select * from tb_user", resultMapper);
        //            List<String> collect = resultList.stream().map(TbUser::getName).collect(Collectors.toList());
        //            //
        //            assert collect.size() == 3;
        //            assert collect.contains(beanForData1().getName());
        //            assert collect.contains(beanForData2().getName());
        //            assert collect.contains(beanForData3().getName());
        //        }
    }
    //
    //    @Test
    //    public void testBeanRowMapper_2() throws SQLException {
    //        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
    //            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
    //            List<TB_User> mapList = jdbcTemplate.queryForList("select * from tb_user", TB_User.class);
    //            //
    //            List<String> collect = mapList.stream().map(TB_User::getName).collect(Collectors.toList());
    //            //
    //            assert mapList.size() == 3;
    //            assert collect.contains(beanForData1().getName());
    //            assert collect.contains(beanForData2().getName());
    //            assert collect.contains(beanForData3().getName());
    //        }
    //    }
    //
    //    @Test
    //    public void testBeanRowMapper_3() throws SQLException {
    //        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
    //            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
    //            List<TB_User> mapList1 = jdbcTemplate.query("select * from tb_user", MappingRowMapper.newInstance(TB_User.class));
    //            List<TB_User> mapList2 = jdbcTemplate.query("select * from tb_user", MappingRowMapper.newInstance(TB_User.class));
    //            //
    //            List<String> collect1 = mapList1.stream().map(TB_User::getName).collect(Collectors.toList());
    //            List<String> collect2 = mapList2.stream().map(TB_User::getName).collect(Collectors.toList());
    //            //
    //            assert mapList1.size() == 3;
    //            assert collect1.contains(beanForData1().getName());
    //            assert collect1.contains(beanForData2().getName());
    //            assert collect1.contains(beanForData3().getName());
    //            //
    //            assert mapList2.size() == 3;
    //            assert collect2.contains(beanForData1().getName());
    //            assert collect2.contains(beanForData2().getName());
    //            assert collect2.contains(beanForData3().getName());
    //        }
    //    }
}
