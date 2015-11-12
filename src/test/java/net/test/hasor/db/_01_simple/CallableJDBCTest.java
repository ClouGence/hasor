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
package net.test.hasor.db._01_simple;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.ConnectionCallback;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.test.hasor.db._07_datasource.warp.OneDataSourceWarp;
import org.junit.Test;
/**
 * 存储过程调用
 * @version : 2013-12-10
 * @author 赵永春(zyc@hasor.net)
 */
public class CallableJDBCTest {
    @Test
    public void testCallable() throws SQLException {
        System.out.println("--->>testCallable<<--");
        //
        AppContext app = Hasor.createAppContext("jdbc-config.xml", new OneDataSourceWarp());
        JdbcTemplate jdbc = app.getInstance(JdbcTemplate.class);
        //
        //
        int flowID = jdbc.execute(new ConnectionCallback<Integer>() {
            public Integer doInConnection(Connection con) throws SQLException {
                String callSQL = "exec PR_BuildFlowTID ?,?";
                CallableStatement callState = con.prepareCall(callSQL);
                callState.setString(1, "TT");
                callState.registerOutParameter(2, Types.INTEGER);
                boolean res = callState.execute();
                int resData = callState.getInt(2);
                return resData;
            }
        });
        System.out.println(flowID);
        //
    }
}
