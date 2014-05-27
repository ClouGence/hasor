/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.test.simple._10_jdbc.curd;
import net.hasor.jdbc.template.core.JdbcTemplate;
import net.test.simple._10_jdbc.AbstractJDBCTest;
import org.junit.Test;
/***
 * 基本增删改查测试
 * @version : 2014-1-13
 * @author 赵永春(zyc@hasor.net)
 */
public class CURD_Test extends AbstractJDBCTest {
    /*使用 insert 插入数据*/
    @Test
    public void insert() throws Exception {
        /*获取 JDBC 操作接口。*/
        JdbcTemplate jdbc = getJdbcTemplate();
        //
        System.out.println(jdbc.queryForInt("select count(*) from TB_User where userUUID='deb4f4c8-5ba1-4f76-8b4a-c2be028bf57b'"));
        //
        String insertUser = "insert into TB_User values('deb4f4c8-5ba1-4f76-8b4a-c2be028bf57b','安妮.贝隆','belon','123','belon@hasor.net','2011-06-08 20:08:08');";
        jdbc.execute(insertUser);//执行插入语句
        //
        System.out.println(jdbc.queryForInt("select count(*) from TB_User where userUUID='deb4f4c8-5ba1-4f76-8b4a-c2be028bf57b'"));
    }
    /*使用 update 更新数据*/
    @Test
    public void update() throws Exception {
        /*获取 JDBC 操作接口。*/
        JdbcTemplate jdbc = getJdbcTemplate();
    }
    /*使用 delete 删除数据*/
    @Test
    public void delete() throws Exception {
        /*获取 JDBC 操作接口。*/
        JdbcTemplate jdbc = getJdbcTemplate();
        //
    }
    /*使用 select 查询数据*/
    @Test
    public void select() throws Exception {
        /*获取 JDBC 操作接口。*/
        JdbcTemplate jdbc = getJdbcTemplate();
        //
    }
}