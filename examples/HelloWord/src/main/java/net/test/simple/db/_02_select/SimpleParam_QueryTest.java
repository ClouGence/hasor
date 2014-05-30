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
package net.test.simple.db._02_select;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.test.simple.db.AbstractJDBCTest;
import org.junit.Test;
/**
 * 
 * @version : 2013-12-10
 * @author 赵永春(zyc@hasor.net)
 */
public class SimpleParam_QueryTest extends AbstractJDBCTest {
    @Test
    public void simpleParam_QueryTest() throws IOException, URISyntaxException, InterruptedException, SQLException {
        System.out.println("--->>simpleParam_QueryTest<<--");
        JdbcTemplate jdbc = getJdbcTemplate();
        //
        List<Map<String, Object>> userList = jdbc.queryForList("select * from TB_User where userUUID like ?", "76%");
        this.printMapList(userList);
    }
}