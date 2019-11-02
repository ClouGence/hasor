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
package net.hasor.test.db.single;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Module;
import net.hasor.db.JdbcModule;
import net.hasor.db.Level;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.test.db.DataSourceUtils;

/***
 * 创建JDBC环境
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class NoDataSingleDataSourceModule implements Module {
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        apiBinder.installModule(new JdbcModule(Level.Full, DataSourceUtils.loadDB("demo1", apiBinder.getEnvironment().getSettings())));
    }

    @Override
    public void onStart(AppContext appContext) throws Throwable {
        // .初始化MySQL
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        if (jdbcTemplate.queryForInt("SELECT count(1) FROM information_schema.system_tables where TABLE_NAME ='TB_USER';") > 0) {
            jdbcTemplate.executeUpdate("drop table TB_USER");
        }
        jdbcTemplate.loadSQL("net_hasor_db/TB_User.sql");
    }
}