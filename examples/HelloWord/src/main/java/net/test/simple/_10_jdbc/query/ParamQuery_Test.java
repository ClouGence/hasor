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
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hasor.core.Hasor;
import net.hasor.jdbc.template.core.JdbcTemplate;
import net.test.simple._10_jdbc.AbstractJDBCTest;
import org.junit.Test;
/**
 * 
 * @version : 2013-12-10
 * @author ÕÔÓÀ´º(zyc@hasor.net)
 */
public class ParamQuery_Test extends AbstractJDBCTest {
    @Test
    public void test_queryList_4_Object() throws IOException, URISyntaxException, InterruptedException, SQLException {
        System.out.println("--->>test_queryList_4_Object<<--");
        JdbcTemplate jdbc = getJdbcTemplate();
        //
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("id", "76%");
        List<TB_User> userList = jdbc.queryForList("select * from TB_User where userUUID like :id", paramMap, TB_User.class);
        for (TB_User user : userList)
            Hasor.logInfo("user :%s.", user.name);
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