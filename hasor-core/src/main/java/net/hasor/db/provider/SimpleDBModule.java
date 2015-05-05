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
package net.hasor.db.provider;
import java.util.UUID;
import javax.sql.DataSource;
import net.hasor.core.ApiBinder;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.core.JdbcTemplateProvider;
import net.hasor.db.transaction.interceptor.simple.SimpleTranInterceptorModule;
/**
 * 
 * @author 赵永春(zyc@hasor.net)
 * @version : 2013-10-30
 */
public class SimpleDBModule implements Module {
    private String     dataSourceID = UUID.randomUUID().toString();
    private DataSource dataSource   = null;
    //
    public SimpleDBModule(DataSource dataSource) {
        Hasor.assertIsNotNull(dataSource, "dataSource is null.");
        this.dataSource = dataSource;
    }
    public SimpleDBModule(String dataSourceID, DataSource dataSource) {
        Hasor.assertIsNotNull(dataSourceID, "dataSourceID is null.");
        Hasor.assertIsNotNull(dataSource, "dataSource is null.");
        this.dataSourceID = dataSourceID;
        this.dataSource = dataSource;
    }
    //
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //1.绑定DataSource接口实现
        apiBinder.bindType(DataSource.class).idWith(this.dataSourceID).toInstance(this.dataSource);
        //2.绑定JdbcTemplate接口实现
        apiBinder.bindType(JdbcTemplate.class).toProvider(new JdbcTemplateProvider(this.dataSource));
        //3.启用默认事务拦截器
        apiBinder.installModule(new SimpleTranInterceptorModule(this.dataSource));
    }
}