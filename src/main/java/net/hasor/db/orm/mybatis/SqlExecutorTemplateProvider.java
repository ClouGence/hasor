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
package net.hasor.db.orm.mybatis;
import net.hasor.core.Provider;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.sql.DataSource;
/**
 * @version : 2014年7月17日
 * @author 赵永春(zyc@hasor.net)
 */
public class SqlExecutorTemplateProvider implements Provider<SqlExecutorTemplate> {
    private Provider<DataSource>        dataSource;
    private Provider<SqlSessionFactory> sessionFactory;
    public SqlExecutorTemplateProvider(Provider<SqlSessionFactory> sessionFactory, Provider<DataSource> dataSource) {
        this.dataSource = dataSource;
        this.sessionFactory = sessionFactory;
    }
    public SqlExecutorTemplate get() {
        return new SqlExecutorTemplate(this.sessionFactory.get(), this.dataSource.get());
    }
}