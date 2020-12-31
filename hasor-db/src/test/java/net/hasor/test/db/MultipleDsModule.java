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
 * 多数剧源
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class MultipleDsModule implements Module {
    public static final String  DS_A = "multiple_a";
    public static final String  DS_B = "multiple_b";
    private final       boolean initData;

    public MultipleDsModule(boolean initData) {
        this.initData = initData;
    }

    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        DataSource ds1 = apiBinder.onShutdown(DsUtils.createDs(DS_A));
        DataSource ds2 = apiBinder.onShutdown(DsUtils.createDs(DS_B));
        apiBinder.installModule(new JdbcModule(Level.Full, DS_A, ds1));
        apiBinder.installModule(new JdbcModule(Level.Full, DS_B, ds2));
    }

    @Override
    public void onStart(AppContext appContext) throws Throwable {
        // init table
        DsUtils.initDB(appContext.findBindingBean(DS_A, JdbcTemplate.class));
        DsUtils.initDB(appContext.findBindingBean(DS_B, JdbcTemplate.class));
        //
        if (!this.initData) {
            return;
        }
        //
        JdbcTemplate jdbcTemplate1 = appContext.findBindingBean(DS_A, JdbcTemplate.class);
        jdbcTemplate1.executeUpdate(INSERT_ARRAY, arrayForData1());
        jdbcTemplate1.executeUpdate(INSERT_ARRAY, arrayForData2());
        //
        JdbcTemplate jdbcTemplate2 = appContext.findBindingBean(DS_B, JdbcTemplate.class);
        jdbcTemplate2.executeUpdate(INSERT_ARRAY, arrayForData3());
    }
}