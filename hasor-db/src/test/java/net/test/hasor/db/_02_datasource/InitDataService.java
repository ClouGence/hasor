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
import net.hasor.core.Inject;
import net.hasor.db.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.sql.SQLException;

import static net.test.hasor.db.junit.HasorUnit.newID;
/**
 * 使用多数据源例子
 * @version : 2014年7月23日
 * @author 赵永春(zyc@hasor.net)
 */
public class InitDataService {
    @Inject("mysql")
    private JdbcTemplate mysqlJDBC;
    @Inject("hsql")
    private JdbcTemplate hsqlJDBC;
    //
    public void initData() throws SQLException, IOException {
        String insertUser_1 = "insert into TB_User values(?,'默罕默德','muhammad','123','muhammad@hasor.net','2011-06-08 20:08:08');";
        String insertUser_2 = "insert into TB_User values(?,'安妮.贝隆','belon','123','belon@hasor.net','2011-06-08 20:08:08');";
        String insertUser_3 = "insert into TB_User values(?,'赵飞燕','muhammad','123','muhammad@hasor.net','2011-06-08 20:08:08');";
        //
        //1.初始化MySQL
        mysqlJDBC.execute("drop table TB_User");
        mysqlJDBC.loadSQL("TB_User.sql");
        mysqlJDBC.update(insertUser_1, newID());//执行插入语句
        mysqlJDBC.update(insertUser_2, newID());//执行插入语句
        //2.初始化HSQL
        hsqlJDBC.execute("drop table TB_User");
        hsqlJDBC.loadSQL("TB_User.sql");
        hsqlJDBC.update(insertUser_3, newID());//执行插入语句
    }
}