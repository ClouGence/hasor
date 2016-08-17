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
package net.hasor.db;
import net.hasor.core.ApiBinder;
import net.hasor.core.ApiBinder.Matcher;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.Provider;
import net.hasor.core.binder.InstanceProvider;
import net.hasor.core.binder.aop.matcher.AopMatchers;
import net.hasor.core.scope.SingleProvider;
import net.hasor.db.jdbc.JdbcOperations;
import net.hasor.db.jdbc.core.JdbcOperationsProvider;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.core.JdbcTemplateProvider;
import net.hasor.db.transaction.TransactionManager;
import net.hasor.db.transaction.TransactionTemplate;
import org.more.util.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
/**
 * 数据库相关Module。
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2013-10-30
 */
public class DBModule implements Module {
    private String               dataSourceID = null;
    private Provider<DataSource> dataSource   = null;
    //
    public DBModule(DataSource dataSource) {
        this(null, new InstanceProvider<DataSource>(Hasor.assertIsNotNull(dataSource, "dataSource is null.")));
    }
    public DBModule(Provider<DataSource> dataSource) {
        this(null, dataSource);
    }
    public DBModule(String dataSourceID, DataSource dataSource) {
        this(dataSourceID, new InstanceProvider<DataSource>(Hasor.assertIsNotNull(dataSource, "dataSource is null.")));
    }
    public DBModule(String dataSourceID, Provider<DataSource> dataSource) {
        Hasor.assertIsNotNull(dataSource, "dataSource is null.");
        this.dataSourceID = dataSourceID;
        this.dataSource = dataSource;
    }
    //
    public void loadModule(ApiBinder apiBinder) {
        Provider<TransactionManager> managerProvider = new TransactionManagerProvider(this.dataSource);
        Provider<TransactionTemplate> templateProvider = new TransactionTemplateProvider(this.dataSource);
        //
        if (StringUtils.isBlank(this.dataSourceID)) {
            apiBinder.bindType(DataSource.class).toProvider(this.dataSource);
            apiBinder.bindType(TransactionManager.class).toProvider(new SingleProvider<TransactionManager>(managerProvider));
            apiBinder.bindType(TransactionTemplate.class).toProvider(new SingleProvider<TransactionTemplate>(templateProvider));
            apiBinder.bindType(JdbcTemplate.class).toProvider(new JdbcTemplateProvider(this.dataSource));
            apiBinder.bindType(JdbcOperations.class).toProvider(new JdbcOperationsProvider(this.dataSource));
        } else {
            apiBinder.bindType(DataSource.class).nameWith(this.dataSourceID).toProvider(this.dataSource);
            apiBinder.bindType(TransactionManager.class).nameWith(this.dataSourceID).toProvider(new SingleProvider<TransactionManager>(managerProvider));
            apiBinder.bindType(TransactionTemplate.class).nameWith(this.dataSourceID).toProvider(new SingleProvider<TransactionTemplate>(templateProvider));
            apiBinder.bindType(JdbcTemplate.class).nameWith(this.dataSourceID).toProvider(new JdbcTemplateProvider(this.dataSource));
            apiBinder.bindType(JdbcOperations.class).nameWith(this.dataSourceID).toProvider(new JdbcOperationsProvider(this.dataSource));
        }
        //
        TransactionInterceptor tranInter = new TransactionInterceptor(this.dataSource);
        Matcher<Class<?>> matcherClass = AopMatchers.annotatedWithClass(Transactional.class);
        Matcher<Method> matcherMethod = AopMatchers.annotatedWithMethod(Transactional.class);
        apiBinder.bindInterceptor(matcherClass, matcherMethod, tranInter);
    }
}