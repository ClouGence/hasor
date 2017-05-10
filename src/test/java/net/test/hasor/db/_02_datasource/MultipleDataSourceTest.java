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
package net.test.hasor.db._02_datasource;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.data.jdbc.core.JdbcTemplate;
import net.test.hasor.db._02_datasource.warp.MultipleDataSourceWarp;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
/**
 * 使用多数据源例子
 * @version : 2014年7月23日
 * @author 赵永春(zyc@hasor.net)
 */
public class MultipleDataSourceTest {
    @Test
    public void useMoreDataSource() throws SQLException, IOException {
        //
        //1.构建AppContext
        AppContext app = Hasor.createAppContext("jdbc-config.xml", new MultipleDataSourceWarp());
        //
        //2.初始化数据
        InitDataService dataTest = app.getInstance(InitDataService.class);
        dataTest.initData();
        //
        //2.取得JDBC操作接口
        JdbcTemplate mJDBC = app.getInstance("mysql");
        JdbcTemplate hJDBC = app.getInstance("hsql");
        System.out.println("MySQL User Count :" + mJDBC.queryForInt("select count(*) from TB_User"));
        System.out.println("HSQL User Count :" + hJDBC.queryForInt("select count(*) from TB_User"));
    }
}