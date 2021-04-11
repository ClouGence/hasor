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
package net.hasor.db.lambda;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.mapping.MappingRegistry;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.dto.TbUser;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/***
 * @version : 2021-3-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class BaseLambdaTest extends AbstractDbTest {
    @Test
    public void base_1() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            LambdaTemplate lambdaTemplate = new LambdaTemplate(jdbcTemplate);
            //
            Map<String, Object> tbUser = lambdaTemplate.lambdaQuery(TbUser.class)//
                    .eq(TbUser::getAccount, "muhammad").apply("limit 1")//
                    .queryForMap();
            assert tbUser.get("name").equals("默罕默德");
            assert tbUser.get("loginName").equals("muhammad");
        }
    }

    @Test
    public void base_2() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            DataSource dataSource = appContext.getInstance(DataSource.class);
            try (Connection conn = dataSource.getConnection()) {
                LambdaTemplate lambdaTemplate = new LambdaTemplate(conn, MappingRegistry.DEFAULT);
                Map<String, Object> tbUser = lambdaTemplate.lambdaQuery(TbUser.class)//
                        .eq(TbUser::getAccount, "muhammad").apply("limit 1")//
                        .queryForMap();
                assert tbUser.get("name").equals("默罕默德");
                assert tbUser.get("loginName").equals("muhammad");
            }
            //
            try (Connection conn = dataSource.getConnection()) {
                LambdaTemplate lambdaTemplate = new LambdaTemplate(conn);
                Map<String, Object> tbUser = lambdaTemplate.lambdaQuery(TbUser.class)//
                        .eq(TbUser::getAccount, "muhammad").apply("limit 1")//
                        .queryForMap();
                assert tbUser.get("name").equals("默罕默德");
                assert tbUser.get("loginName").equals("muhammad");
            }
        }
    }

    @Test
    public void base_3() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            DataSource dataSource = appContext.getInstance(DataSource.class);
            LambdaTemplate lambdaTemplate = new LambdaTemplate(dataSource, MappingRegistry.DEFAULT);
            Map<String, Object> tbUser = lambdaTemplate.lambdaQuery(TbUser.class)//
                    .eq(TbUser::getAccount, "muhammad").apply("limit 1")//
                    .queryForMap();
            assert tbUser.get("name").equals("默罕默德");
            assert tbUser.get("loginName").equals("muhammad");
        }
    }

    @Test
    public void base_4() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            DataSource dataSource = appContext.getInstance(DataSource.class);
            LambdaTemplate lambdaTemplate = new LambdaTemplate(dataSource);
            Map<String, Object> tbUser = lambdaTemplate.lambdaQuery(TbUser.class)//
                    .eq(TbUser::getAccount, "muhammad").apply("limit 1")//
                    .queryForMap();
            assert tbUser.get("name").equals("默罕默德");
            assert tbUser.get("loginName").equals("muhammad");
        }
    }

    @Test
    public void base_5() throws SQLException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            DataSource dataSource = appContext.getInstance(DataSource.class);
            LambdaTemplate lambdaTemplate = new LambdaTemplateProvider(dataSource).get();
            Map<String, Object> tbUser = lambdaTemplate.lambdaQuery(TbUser.class)//
                    .eq(TbUser::getAccount, "muhammad").apply("limit 1")//
                    .queryForMap();
            assert tbUser.get("name").equals("默罕默德");
            assert tbUser.get("loginName").equals("muhammad");
        }
    }
}
