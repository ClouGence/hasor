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
package net.example.nacos.config;
import net.hasor.core.ApiBinder;
import net.hasor.core.DimModule;
import net.hasor.dataql.Finder;
import net.hasor.dataql.QueryApiBinder;
import net.hasor.db.JdbcModule;
import net.hasor.db.Level;
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
public class DatawayModule implements SpringModule {
    @Resource(name = "dataDs1")
    private DataSource dataDs1 = null;
    @Resource(name = "dataDs2")
    private DataSource dataDs2 = null;

    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        QueryApiBinder queryBinder = apiBinder.tryCast(QueryApiBinder.class);
        // .check
        Objects.requireNonNull(this.dataDs1, "dataDs1 is null");
        Objects.requireNonNull(this.dataDs2, "dataDs2 is null");
        // .DataSource form Spring boot into Hasor
        queryBinder.installModule(new JdbcModule(Level.Full, "ds1", this.dataDs1));
        queryBinder.installModule(new JdbcModule(Level.Full, "ds2", this.dataDs2));
        // udf/udfSource/import 指令 的类型创建委托给 spring
        queryBinder.bindFinder(Finder.TYPE_SUPPLIER.apply(springTypeSupplier(apiBinder)));
    }
}