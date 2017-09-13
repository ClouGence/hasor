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
package net.test.hasor.db._02_datasource.warp;
import net.hasor.core.ApiBinder;
import net.hasor.core.Module;
import net.hasor.core.Settings;
import net.hasor.db.DBModule;

import javax.sql.DataSource;
/***
 * 创建JDBC环境
 * @version : 2014-1-13
 * @author 赵永春(zyc@hasor.net)
 */
public class SingleDataSourceWarp implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //1.获取数据库连接配置信息
        Settings settings = apiBinder.getEnvironment().getSettings();
        String driverString = settings.getString("demo-jdbc-mysql.driver");
        String urlString = settings.getString("demo-jdbc-mysql.url");
        String userString = settings.getString("demo-jdbc-mysql.user");
        String pwdString = settings.getString("demo-jdbc-mysql.password");
        //2.创建数据库连接池
        DataSource dataSource = C3p0DataSourceFactory.createDataSource(driverString, urlString, userString, pwdString);
        //5.启用默认事务拦截器
        apiBinder.installModule(new DBModule(dataSource));
    }
}