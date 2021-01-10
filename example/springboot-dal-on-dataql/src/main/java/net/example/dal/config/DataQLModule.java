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
package net.example.dal.config;
import net.hasor.core.DimModule;
import net.hasor.dataql.Finder;
import net.hasor.dataql.QueryApiBinder;
import net.hasor.dataql.QueryModule;
import net.hasor.db.JdbcModule;
import net.hasor.db.Level;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.spring.SpringModule;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Objects;

/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2021-01-02
 */
@DimModule
@Component
public class DataQLModule implements QueryModule, SpringModule {
    @Resource()
    private DataSource dataSource = null;

    @Override
    public void loadModule(QueryApiBinder apiBinder) throws Throwable {
        //
        // .check dataSource
        Objects.requireNonNull(this.dataSource, "dataDs1 is null");
        //
        // .add two data sources in to Dataway
        apiBinder.installModule(new JdbcModule(Level.Full, this.dataSource));
        //
        // udf/udfSource/import 指令 的类型创建委托给 spring
        QueryApiBinder queryBinder = apiBinder.tryCast(QueryApiBinder.class);
        queryBinder.bindFinder(Finder.TYPE_SUPPLIER.apply(springTypeSupplier(apiBinder)));
        //
        new JdbcTemplate(this.dataSource).loadSQL("/initsql/my_option.sql");
        new JdbcTemplate(this.dataSource).loadSQL("/initsql/my_option_data.sql");
    }
}