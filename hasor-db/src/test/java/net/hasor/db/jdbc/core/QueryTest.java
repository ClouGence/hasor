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
import net.hasor.db.jdbc.extractor.RowMapperResultSetExtractor;
import net.hasor.db.jdbc.paramer.BeanSqlParameterSource;
import net.hasor.db.lambda.mapping.MappingRowMapper;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.dto.TB_User;
import net.hasor.test.db.dto.TbUser;
import net.hasor.test.db.utils.TestUtils;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * query 系列方法测试
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class QueryTest extends AbstractDbTest {
    @Test
    public void query_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<TbUser> tbUsers = jdbcTemplate.query(con -> {
                return con.prepareStatement("select * from tb_user");
            }, rs -> {
                return new RowMapperResultSetExtractor<>(new MappingRowMapper<>(TbUser.class)).extractData(rs);
            });
            assert tbUsers.size() == 3;
            assert TestUtils.beanForData1().getUserUUID().equals(tbUsers.get(0).getUid());
            assert TestUtils.beanForData2().getUserUUID().equals(tbUsers.get(1).getUid());
            assert TestUtils.beanForData3().getUserUUID().equals(tbUsers.get(2).getUid());
        }
    }

    @Test
    public void query_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<TbUser> tbUsers = jdbcTemplate.query("select * from tb_user", rs -> {
                return new RowMapperResultSetExtractor<>(new MappingRowMapper<>(TbUser.class)).extractData(rs);
            });
            assert tbUsers.size() == 3;
            assert TestUtils.beanForData1().getUserUUID().equals(tbUsers.get(0).getUid());
            assert TestUtils.beanForData2().getUserUUID().equals(tbUsers.get(1).getUid());
            assert TestUtils.beanForData3().getUserUUID().equals(tbUsers.get(2).getUid());
        }
    }

    @Test
    public void query_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            List<TbUser> tbUsers = jdbcTemplate.query("select * from tb_user where userUUID = ?", ps -> {
                ps.setString(1, tbUser.getUserUUID());
            }, rs -> {
                return new RowMapperResultSetExtractor<>(new MappingRowMapper<>(TbUser.class)).extractData(rs);
            });
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).getUid());
        }
    }

    @Test
    public void query_4() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            List<TbUser> tbUsers = jdbcTemplate.query("select * from tb_user where userUUID = ?", rs -> {
                return new RowMapperResultSetExtractor<>(new MappingRowMapper<>(TbUser.class)).extractData(rs);
            }, tbUser.getUserUUID());
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).getUid());
        }
    }

    @Test
    public void query_5() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            List<TbUser> tbUsers = jdbcTemplate.query("select * from tb_user where userUUID = ?", new Object[] { tbUser.getUserUUID() }, rs -> {
                return new RowMapperResultSetExtractor<>(new MappingRowMapper<>(TbUser.class)).extractData(rs);
            });
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).getUid());
        }
    }

    @Test
    public void query_6() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            Map<String, String> mapParams = new HashMap<>();
            mapParams.put("uuid", tbUser.getUserUUID());
            List<TbUser> tbUsers = jdbcTemplate.query("select * from tb_user where userUUID = :uuid", mapParams, rs -> {
                return new RowMapperResultSetExtractor<>(new MappingRowMapper<>(TbUser.class)).extractData(rs);
            });
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).getUid());
        }
    }

    @Test
    public void query_7() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            BeanSqlParameterSource beanSqlParameterSource = new BeanSqlParameterSource(tbUser);
            List<TbUser> tbUsers = jdbcTemplate.query("select * from tb_user where userUUID = :userUUID", beanSqlParameterSource, rs -> {
                return new RowMapperResultSetExtractor<>(new MappingRowMapper<>(TbUser.class)).extractData(rs);
            });
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).getUid());
        }
    }

    @Test
    public void queryVoid_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<TbUser> tbUsers = new ArrayList<>();
            jdbcTemplate.query(con -> {
                return con.prepareStatement("select * from tb_user");
            }, (rs, rowNum) -> {
                tbUsers.add(new MappingRowMapper<>(TbUser.class).mapRow(rs, rowNum));
            });
            assert tbUsers.size() == 3;
            assert TestUtils.beanForData1().getUserUUID().equals(tbUsers.get(0).getUid());
            assert TestUtils.beanForData2().getUserUUID().equals(tbUsers.get(1).getUid());
            assert TestUtils.beanForData3().getUserUUID().equals(tbUsers.get(2).getUid());
        }
    }

    @Test
    public void queryVoid2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<TbUser> tbUsers = new ArrayList<>();
            jdbcTemplate.query("select * from tb_user", (rs, rowNum) -> {
                tbUsers.add(new MappingRowMapper<>(TbUser.class).mapRow(rs, rowNum));
            });
            assert tbUsers.size() == 3;
            assert TestUtils.beanForData1().getUserUUID().equals(tbUsers.get(0).getUid());
            assert TestUtils.beanForData2().getUserUUID().equals(tbUsers.get(1).getUid());
            assert TestUtils.beanForData3().getUserUUID().equals(tbUsers.get(2).getUid());
        }
    }

    @Test
    public void queryVoid3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            List<TbUser> tbUsers = new ArrayList<>();
            jdbcTemplate.query("select * from tb_user where userUUID = ?", ps -> {
                ps.setString(1, tbUser.getUserUUID());
            }, (rs, rowNum) -> {
                tbUsers.add(new MappingRowMapper<>(TbUser.class).mapRow(rs, rowNum));
            });
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).getUid());
        }
    }

    @Test
    public void queryVoid4() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            List<TbUser> tbUsers = new ArrayList<>();
            jdbcTemplate.query("select * from tb_user where userUUID = ?", (rs, rowNum) -> {
                tbUsers.add(new MappingRowMapper<>(TbUser.class).mapRow(rs, rowNum));
            }, tbUser.getUserUUID());
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).getUid());
        }
    }

    @Test
    public void queryVoid5() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            List<TbUser> tbUsers = new ArrayList<>();
            jdbcTemplate.query("select * from tb_user where userUUID = ?", new Object[] { tbUser.getUserUUID() }, (rs, rowNum) -> {
                tbUsers.add(new MappingRowMapper<>(TbUser.class).mapRow(rs, rowNum));
            });
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).getUid());
        }
    }

    @Test
    public void queryVoid6() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            Map<String, String> mapParams = new HashMap<>();
            mapParams.put("uuid", tbUser.getUserUUID());
            List<TbUser> tbUsers = new ArrayList<>();
            jdbcTemplate.query("select * from tb_user where userUUID = :uuid", mapParams, (rs, rowNum) -> {
                tbUsers.add(new MappingRowMapper<>(TbUser.class).mapRow(rs, rowNum));
            });
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).getUid());
        }
    }

    @Test
    public void queryVoid7() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            BeanSqlParameterSource beanSqlParameterSource = new BeanSqlParameterSource(tbUser);
            List<TbUser> tbUsers = new ArrayList<>();
            jdbcTemplate.query("select * from tb_user where userUUID = :userUUID", beanSqlParameterSource, (rs, rowNum) -> {
                tbUsers.add(new MappingRowMapper<>(TbUser.class).mapRow(rs, rowNum));
            });
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).getUid());
        }
    }

    @Test
    public void queryList_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<TbUser> tbUsers = jdbcTemplate.query(con -> {
                return con.prepareStatement("select * from tb_user");
            }, new MappingRowMapper<>(TbUser.class));
            assert tbUsers.size() == 3;
            assert TestUtils.beanForData1().getUserUUID().equals(tbUsers.get(0).getUid());
            assert TestUtils.beanForData2().getUserUUID().equals(tbUsers.get(1).getUid());
            assert TestUtils.beanForData3().getUserUUID().equals(tbUsers.get(2).getUid());
        }
    }

    @Test
    public void queryList_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<TbUser> tbUsers = jdbcTemplate.query("select * from tb_user", new MappingRowMapper<>(TbUser.class));
            assert tbUsers.size() == 3;
            assert TestUtils.beanForData1().getUserUUID().equals(tbUsers.get(0).getUid());
            assert TestUtils.beanForData2().getUserUUID().equals(tbUsers.get(1).getUid());
            assert TestUtils.beanForData3().getUserUUID().equals(tbUsers.get(2).getUid());
        }
    }

    @Test
    public void queryList_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            List<TbUser> tbUsers = jdbcTemplate.query("select * from tb_user where userUUID = ?", ps -> {
                ps.setString(1, tbUser.getUserUUID());
            }, new MappingRowMapper<>(TbUser.class));
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).getUid());
        }
    }

    @Test
    public void queryList_4() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            List<TbUser> tbUsers = jdbcTemplate.query("select * from tb_user where userUUID = ?", new MappingRowMapper<>(TbUser.class), tbUser.getUserUUID());
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).getUid());
        }
    }

    @Test
    public void queryList_5() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            List<TbUser> tbUsers = jdbcTemplate.query("select * from tb_user where userUUID = ?", new Object[] { tbUser.getUserUUID() }, new MappingRowMapper<>(TbUser.class));
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).getUid());
        }
    }

    @Test
    public void queryList_6() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            Map<String, String> mapParams = new HashMap<>();
            mapParams.put("uuid", tbUser.getUserUUID());
            List<TbUser> tbUsers = jdbcTemplate.query("select * from tb_user where userUUID = :uuid", mapParams, new MappingRowMapper<>(TbUser.class));
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).getUid());
        }
    }

    @Test
    public void queryList_7() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            TB_User tbUser = TestUtils.beanForData1();
            BeanSqlParameterSource beanSqlParameterSource = new BeanSqlParameterSource(tbUser);
            List<TbUser> tbUsers = jdbcTemplate.query("select * from tb_user where userUUID = :userUUID", beanSqlParameterSource, new MappingRowMapper<>(TbUser.class));
            assert tbUsers.size() == 1;
            assert tbUser.getUserUUID().equals(tbUsers.get(0).getUid());
        }
    }
}
