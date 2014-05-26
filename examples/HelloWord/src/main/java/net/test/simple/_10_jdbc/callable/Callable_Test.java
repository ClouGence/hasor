/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package net.test.simple._10_jdbc.callable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;
import net.hasor.jdbc.template.core.JdbcTemplate;
import net.test.simple._10_jdbc.AbstractJDBCTest;
import org.hsqldb.util.DatabaseManagerSwing;
import org.junit.Test;
/**
 * 
 * @version : 2013-12-10
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
public class Callable_Test extends AbstractJDBCTest {
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++)
            System.out.println(UUID.randomUUID().toString());
        DatabaseManagerSwing.main(args);
    }
    @Test
    public void testCallable() throws IOException, URISyntaxException, InterruptedException {
        /*≤‚ ‘ µ˜”√¥Ê¥¢π˝≥Ã°£ */
        JdbcTemplate jdbc = getJdbcTemplate();
        //
        //        int flowID = jdbc.execute(new ConnectionCallback<Integer>() {
        //            public Integer doInConnection(Connection con) throws SQLException, DataAccessException {
        //                String callSQL = "exec PR_BuildFlowTID ?,?";
        //                CallableStatement callState = con.prepareCall(callSQL);
        //                callState.setString(1, "TT");
        //                callState.registerOutParameter(2, Types.INTEGER);
        //                boolean res = callState.execute();
        //                int resData = callState.getInt(2);
        //                return resData;
        //            }
        //        });
        //System.out.println(flowID);
        //
    }
}
