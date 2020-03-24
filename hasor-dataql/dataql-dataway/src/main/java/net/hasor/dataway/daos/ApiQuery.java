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
package net.hasor.dataway.daos;
public class ApiQuery {
    public String queryApi(String requestUrl) {
        return null;
    }
}
//        jdbcTemplate.execute(new ConnectionCallback<Object>() {
//            @Override
//            public Object doInConnection(Connection con) throws SQLException {
//                con.getMetaData().getTables().getMetaData().
//                con.getMetaData().
//                return null;
//            }
//        });
//        //
//        // .初始化MySQL
//        if (jdbcTemplate.queryForInt("SELECT count(1) FROM information_schema.system_tables where TABLE_NAME ='TB_USER';") > 0) {
//            jdbcTemplate.executeUpdate("drop table TB_USER");
//        }
//        jdbcTemplate.loadSQL("net_hasor_db/TB_User.sql");
//        //
//