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
package net.test.hasor.db._02_select;
import java.sql.SQLException;
import java.util.List;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.test.hasor.db._02_select.entity.TB_User;
import net.test.hasor.db._07_datasource.warp.OneDataSourceWarp;
import net.test.hasor.test.utils.HasorUnit;
import org.junit.Test;
/**
 * 
 * @version : 2013-12-10
 * @author 赵永春(zyc@hasor.net)
 */
public class Entity_QueryTest {
    @Test
    public void entity_QueryTest() throws SQLException {
        System.out.println("--->>entity_QueryTest<<--");
        //
        AppContext app = Hasor.createAppContext("net/test/simple/db/jdbc-config.xml", new OneDataSourceWarp());
        JdbcTemplate jdbc = app.getInstance(JdbcTemplate.class);
        //
        List<TB_User> userList = jdbc.queryForList("select * from TB_User", TB_User.class);
        HasorUnit.printObjectList(userList);
    }
}