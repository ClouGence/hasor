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
package net.example.springmvc.core;
import net.hasor.core.AppContext;
import net.hasor.dataql.Finder;
import net.hasor.dataql.QueryApiBinder;
import net.hasor.dataway.dal.providers.db.InformationStorage;
import net.hasor.db.JdbcModule;
import net.hasor.db.JdbcUtils;
import net.hasor.db.Level;
import net.hasor.db.jdbc.ConnectionCallback;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.spring.SpringModule;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.io.Charsets;
import net.hasor.utils.io.IOUtils;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.DatabaseMetaData;
import java.util.Objects;

/**
 *
 * @version : 2015年12月25日
 * @author 赵永春 (zyc@hasor.net)
 */
public class DatawayModule implements WebModule, SpringModule {
    private DataSource metadataDs = null;
    private DataSource dataDs1    = null;
    private DataSource dataDs2    = null;

    public void setMetadataDs(DataSource metadataDs) {
        this.metadataDs = metadataDs;
    }

    public void setDataDs1(DataSource dataDs1) {
        this.dataDs1 = dataDs1;
    }

    public void setDataDs2(DataSource dataDs2) {
        this.dataDs2 = dataDs2;
    }

    @Override
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        apiBinder.setEncodingCharacter("UTF-8", "UTF-8");
        //
        // .check dataSource
        Objects.requireNonNull(this.metadataDs, "metadataDs is null");
        Objects.requireNonNull(this.dataDs1, "dataDs1 is null");
        Objects.requireNonNull(this.dataDs2, "dataDs2 is null");
        //
        // .isolation meta-tables using InformationStorage
        apiBinder.bindType(InformationStorage.class).toInstance(() -> {
            return this.metadataDs;
        });
        //
        // .add two data sources in to Dataway
        apiBinder.installModule(new JdbcModule(Level.Full, "ds1", this.dataDs1));
        apiBinder.installModule(new JdbcModule(Level.Full, "ds2", this.dataDs2));
        //
        // udf/udfSource/import 指令 的类型创建委托给 spring
        QueryApiBinder queryBinder = apiBinder.tryCast(QueryApiBinder.class);
        queryBinder.bindFinder(Finder.TYPE_SUPPLIER.apply(springTypeSupplier(apiBinder)));
    }

    @Override
    public void onStart(AppContext appContext) throws Throwable {
        String dbType = new JdbcTemplate(this.metadataDs).execute((ConnectionCallback<String>) con -> {
            DatabaseMetaData metaData = con.getMetaData();
            return JdbcUtils.getDbType(metaData.getURL(), metaData.getDriverName());
        });
        //
        if (JdbcUtils.H2.equalsIgnoreCase(dbType)) {
            InputStream infoStream = ResourcesUtils.getResourceAsStream("/META-INF/hasor-framework/h2/interface_info.sql");
            InputStream releaseStream = ResourcesUtils.getResourceAsStream("/META-INF/hasor-framework/h2/interface_release.sql");
            new JdbcTemplate(this.metadataDs).execute(IOUtils.toString(infoStream, Charsets.UTF_8));
            new JdbcTemplate(this.metadataDs).execute(IOUtils.toString(releaseStream, Charsets.UTF_8));
        }
    }
}