/*
 * Copyright 2008-2009 the original ÕÔÓÀ´º(zyc@hasor.net).
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
package net.test.simple._10_jdbc.query;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.hasor.core.Hasor;
import net.hasor.jdbc.template.SqlRowSet;
import net.hasor.jdbc.template.core.JdbcTemplate;
import net.test.simple._10_jdbc.AbstractJDBCTest;
import org.junit.Test;
/**
 * 
 * @version : 2013-12-10
 * @author ÕÔÓÀ´º(zyc@hasor.net)
 */
public class QueryList_Test extends AbstractJDBCTest {
    @Test
    public void test_queryList_4_Object() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>test_queryList_4_Object<<--");
        JdbcTemplate jdbc = getJdbcTemplate();
        //
        List<TB_User> userList = jdbc.queryForList("select * from TB_User", TB_User.class);
        for (TB_User user : userList)
            Hasor.logInfo("user :%s.", user.name);
    }
    @Test
    public void test_queryList_4_Map() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>test_queryList_4_Map<<--");
        JdbcTemplate jdbc = getJdbcTemplate();
        //
        List<Map> mapList = jdbc.queryForList("select * from TB_User", Map.class);
        for (Map user : mapList)
            Hasor.logInfo("user :%s.", user.get("userUUID"));
    }
    @Test
    public void test_queryList_4_String() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>test_queryList_4_String<<--");
        JdbcTemplate jdbc = getJdbcTemplate();
        //
        List<String> mapList = jdbc.queryForList("select userUUID from TB_User", String.class);
        for (String user : mapList)
            Hasor.logInfo("user :%s.", user);
    }
    @Test
    public void test_queryList_4_RowSet() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>test_queryList_4_RowSet<<--");
        JdbcTemplate jdbc = getJdbcTemplate();
        //
        SqlRowSet rowSet = jdbc.queryForRowSet("select * from TB_User");
        rowSet.beforeFirst();
        while (rowSet.isLast() == false) {
            rowSet.next();
            Hasor.logInfo("user :%s.", rowSet.getString(1));
        }
    }
    // 
    //
    //
    public static class TB_User {
        public String userUUID;
        public String name;
        public String loginName;
        public String loginPassword;
        public String email;
        public Date   registerTime;
    }
}