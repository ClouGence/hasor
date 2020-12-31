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
package net.hasor.test.db;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Module;
import net.hasor.db.JdbcModule;
import net.hasor.db.Level;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.test.db.utils.DsUtils;

import javax.sql.DataSource;

import static net.hasor.test.db.utils.TestUtils.*;

/***
 * 创建JDBC环境
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class SingleDsModule implements Module {
    private final boolean initData;

    public SingleDsModule(boolean initData) {
        this.initData = initData;
    }

    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        DataSource ds = apiBinder.onShutdown(DsUtils.createDs("single"));
        apiBinder.installModule(new JdbcModule(Level.Full, ds));
    }

    @Override
    public void onStart(AppContext appContext) throws Throwable {
        // init table
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        DsUtils.initDB(jdbcTemplate);
        //
        if (this.initData) {
            jdbcTemplate.executeUpdate(INSERT_ARRAY, arrayForData1());
            jdbcTemplate.executeUpdate(INSERT_ARRAY, arrayForData2());
            jdbcTemplate.executeUpdate(INSERT_ARRAY, arrayForData3());
        }
    }
}