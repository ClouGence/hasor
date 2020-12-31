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
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.mapping.BeanRowMapper;
import net.hasor.db.jdbc.paramer.BeanSqlParameterSource;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.dto.TB_User;
import net.hasor.test.db.dto.TbUser;
import net.hasor.test.db.utils.TestUtils;
import org.junit.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * queryFor 系列方法测试
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class QueryForTest extends AbstractDbTest {
    @Test
    public void queryForList_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<TbUser> tbUsers = jdbcTemplate.queryForList("select * from tb_user", TbUser.class);
            assert tbUsers.size() == 3;
            assert TestUtils.beanForData1().getUserUUID().equals(tbUsers.get(0).getUid());
            assert TestUtils.beanForData2().getUserUUID().equals(tbUsers.get(1).getUid());
            assert TestUtils.beanForData3().getUserUUID().equals(tbUsers.get(2).getUid());
        }
    }

    @Test
    public void queryForList_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            List<TbUser> tbUsers = jdbcTemplate.queryForList("select * from tb_user where userUUID = ?", TbUser.class, tbUser.getUserUUID());
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).getUid());
        }
    }

    @Test
    public void queryForList_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            List<TbUser> tbUsers = jdbcTemplate.queryForList("select * from tb_user where userUUID = ?", new Object[] { tbUser.getUserUUID() }, TbUser.class);
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).getUid());
        }
    }

    @Test
    public void queryForList_4() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            BeanSqlParameterSource beanSqlParameterSource = new BeanSqlParameterSource(tbUser);
            List<TbUser> tbUsers = jdbcTemplate.queryForList("select * from tb_user where userUUID = :userUUID ", beanSqlParameterSource, TbUser.class);
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).getUid());
        }
    }

    @Test
    public void queryForList_5() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            Map<String, String> mapParams = new HashMap<>();
            mapParams.put("uuid", tbUser.getUserUUID());
            List<TbUser> tbUsers = jdbcTemplate.queryForList("select * from tb_user where userUUID = :uuid ", mapParams, TbUser.class);
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).getUid());
        }
    }

    @Test
    public void queryForObject_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            BeanRowMapper<TbUser> rowMapper = BeanRowMapper.newInstance(TbUser.class);
            TbUser user = jdbcTemplate.queryForObject("select * from tb_user where userUUID = '" + tbUser.getUserUUID() + "'", rowMapper);
            assert user != null;
            assert tbUser.getUserUUID().equals(user.getUid());
        }
    }

    @Test
    public void queryForObject_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            BeanRowMapper<TbUser> rowMapper = BeanRowMapper.newInstance(TbUser.class);
            TbUser user = jdbcTemplate.queryForObject("select * from tb_user where userUUID = ?", rowMapper, tbUser.getUserUUID());
            assert user != null;
            assert tbUser.getUserUUID().equals(user.getUid());
        }
    }

    @Test
    public void queryForObject_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            BeanRowMapper<TbUser> rowMapper = BeanRowMapper.newInstance(TbUser.class);
            TbUser user = jdbcTemplate.queryForObject("select * from tb_user where userUUID = ?", new Object[] { tbUser.getUserUUID() }, rowMapper);
            assert user != null;
            assert tbUser.getUserUUID().equals(user.getUid());
        }
    }

    @Test
    public void queryForObject_4() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            BeanSqlParameterSource beanSqlParameterSource = new BeanSqlParameterSource(tbUser);
            BeanRowMapper<TbUser> rowMapper = BeanRowMapper.newInstance(TbUser.class);
            TbUser user = jdbcTemplate.queryForObject("select * from tb_user where userUUID = :userUUID", beanSqlParameterSource, rowMapper);
            assert user != null;
            assert tbUser.getUserUUID().equals(user.getUid());
        }
    }

    @Test
    public void queryForObject_5() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            Map<String, String> mapParams = new HashMap<>();
            mapParams.put("uuid", tbUser.getUserUUID());
            BeanRowMapper<TbUser> rowMapper = BeanRowMapper.newInstance(TbUser.class);
            TbUser user = jdbcTemplate.queryForObject("select * from tb_user where userUUID = :uuid", mapParams, rowMapper);
            assert user != null;
            assert tbUser.getUserUUID().equals(user.getUid());
        }
    }

    @Test
    public void queryForObject_6() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            TbUser user = jdbcTemplate.queryForObject("select * from tb_user where userUUID = '" + tbUser.getUserUUID() + "'", TbUser.class);
            assert user != null;
            assert tbUser.getUserUUID().equals(user.getUid());
        }
    }

    @Test
    public void queryForObject_7() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            TbUser user = jdbcTemplate.queryForObject("select * from tb_user where userUUID = ?", TbUser.class, tbUser.getUserUUID());
            assert user != null;
            assert tbUser.getUserUUID().equals(user.getUid());
        }
    }

    @Test
    public void queryForObject_8() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            TbUser user = jdbcTemplate.queryForObject("select * from tb_user where userUUID = ?", new Object[] { tbUser.getUserUUID() }, TbUser.class);
            assert user != null;
            assert tbUser.getUserUUID().equals(user.getUid());
        }
    }

    @Test
    public void queryForObject_9() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            BeanSqlParameterSource beanSqlParameterSource = new BeanSqlParameterSource(tbUser);
            TbUser user = jdbcTemplate.queryForObject("select * from tb_user where userUUID = :userUUID", beanSqlParameterSource, TbUser.class);
            assert user != null;
            assert tbUser.getUserUUID().equals(user.getUid());
        }
    }

    @Test
    public void queryForObject_10() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            Map<String, String> mapParams = new HashMap<>();
            mapParams.put("uuid", tbUser.getUserUUID());
            TbUser user = jdbcTemplate.queryForObject("select * from tb_user where userUUID = :uuid", mapParams, TbUser.class);
            assert user != null;
            assert tbUser.getUserUUID().equals(user.getUid());
        }
    }

    @Test
    public void queryForNumber_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            long userCountLong = jdbcTemplate.queryForLong("select count(*) from tb_user");
            assert userCountLong == 3;
            long userCountInt = jdbcTemplate.queryForInt("select count(*) from tb_user");
            assert userCountInt == 3;
        }
    }

    @Test
    public void queryForNumber_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            long userCountLong = jdbcTemplate.queryForLong("select count(*) from tb_user where userUUID != ?", tbUser.getUserUUID());
            assert userCountLong == 2;
            long userCountInt = jdbcTemplate.queryForInt("select count(*) from tb_user where userUUID != ?", tbUser.getUserUUID());
            assert userCountInt == 2;
        }
    }

    @Test
    public void queryForNumber_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            BeanSqlParameterSource beanSqlParameterSource = new BeanSqlParameterSource(tbUser);
            long userCountLong = jdbcTemplate.queryForLong("select count(*) from tb_user where userUUID != :userUUID", beanSqlParameterSource);
            assert userCountLong == 2;
            long userCountInt = jdbcTemplate.queryForInt("select count(*) from tb_user where userUUID != :userUUID", beanSqlParameterSource);
            assert userCountInt == 2;
        }
    }

    @Test
    public void queryForNumber_4() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            Map<String, String> mapParams = new HashMap<>();
            mapParams.put("uuid", tbUser.getUserUUID());
            long userCountLong = jdbcTemplate.queryForLong("select count(*) from tb_user where userUUID != :uuid", mapParams);
            assert userCountLong == 2;
            long userCountInt = jdbcTemplate.queryForInt("select count(*) from tb_user where userUUID != :uuid", mapParams);
            assert userCountInt == 2;
        }
    }

    @Test
    public void queryForMap_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            Map<String, Object> mapData = jdbcTemplate.queryForMap("select * from tb_user where userUUID = '" + tbUser.getUserUUID() + "'");
            assert mapData != null;
            assert tbUser.getUserUUID().equals(mapData.get("userUUID"));
        }
    }

    @Test
    public void queryForMap_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            Map<String, Object> mapData = jdbcTemplate.queryForMap("select * from tb_user where userUUID = ?", tbUser.getUserUUID());
            assert mapData != null;
            assert tbUser.getUserUUID().equals(mapData.get("userUUID"));
        }
    }

    @Test
    public void queryForMap_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            BeanSqlParameterSource beanSqlParameterSource = new BeanSqlParameterSource(tbUser);
            Map<String, Object> mapData = jdbcTemplate.queryForMap("select * from tb_user where userUUID = :userUUID", beanSqlParameterSource);
            assert mapData != null;
            assert tbUser.getUserUUID().equals(mapData.get("userUUID"));
        }
    }

    @Test
    public void queryForMap_4() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            Map<String, String> mapParams = new HashMap<>();
            mapParams.put("uuid", tbUser.getUserUUID());
            Map<String, Object> mapData = jdbcTemplate.queryForMap("select * from tb_user where userUUID = :uuid", mapParams);
            assert mapData != null;
            assert tbUser.getUserUUID().equals(mapData.get("userUUID"));
        }
    }

    @Test
    public void queryForListMap_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<Map<String, Object>> tbUsers = jdbcTemplate.queryForList("select * from tb_user");
            assert tbUsers.size() == 3;
            assert TestUtils.beanForData1().getUserUUID().equals(tbUsers.get(0).get("userUUID"));
            assert TestUtils.beanForData2().getUserUUID().equals(tbUsers.get(1).get("userUUID"));
            assert TestUtils.beanForData3().getUserUUID().equals(tbUsers.get(2).get("userUUID"));
        }
    }

    @Test
    public void queryForListMap_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            List<Map<String, Object>> tbUsers = jdbcTemplate.queryForList("select * from tb_user where userUUID = ?", tbUser.getUserUUID());
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).get("userUUID"));
        }
    }

    @Test
    public void queryForListMap_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            BeanSqlParameterSource beanSqlParameterSource = new BeanSqlParameterSource(tbUser);
            List<Map<String, Object>> tbUsers = jdbcTemplate.queryForList("select * from tb_user where userUUID = :userUUID ", beanSqlParameterSource);
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).get("userUUID"));
        }
    }

    @Test
    public void queryForListMap_4() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            Map<String, String> mapParams = new HashMap<>();
            mapParams.put("uuid", tbUser.getUserUUID());
            List<Map<String, Object>> tbUsers = jdbcTemplate.queryForList("select * from tb_user where userUUID = :uuid ", mapParams);
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).get("userUUID"));
        }
    }

    @Test
    public void queryForListMap_5() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            List<Map<String, Object>> tbUsers = jdbcTemplate.queryForList("select * from tb_user where userUUID = ? ", ps -> {
                ps.setString(1, tbUser.getUserUUID());
            });
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).get("userUUID"));
        }
    }
}