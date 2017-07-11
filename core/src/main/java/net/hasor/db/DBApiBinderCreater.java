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
import net.hasor.core.Hasor;
import net.hasor.core.Matcher;
import net.hasor.core.Provider;
import net.hasor.core.binder.ApiBinderCreater;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.core.classcode.matcher.AopMatchers;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.core.provider.SingleProvider;
import net.hasor.core.utils.StringUtils;
import net.hasor.db.jdbc.JdbcOperations;
import net.hasor.db.jdbc.core.JdbcOperationsProvider;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.core.JdbcTemplateProvider;
import net.hasor.db.transaction.TransactionManager;
import net.hasor.db.transaction.TransactionTemplate;
import net.hasor.db.transaction.interceptor.TransactionInterceptor;
import net.hasor.db.transaction.interceptor.Transactional;
import net.hasor.db.transaction.provider.TransactionManagerProvider;
import net.hasor.db.transaction.provider.TransactionTemplateProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Method;
/**
 * DB 扩展接口。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class DBApiBinderCreater implements ApiBinderCreater {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public ApiBinder createBinder(final ApiBinder apiBinder) {
        return new DBApiBinderImpl(apiBinder);
    }
    //
    private static class DBApiBinderImpl extends ApiBinderWrap implements DBApiBinder {
        protected Logger logger = LoggerFactory.getLogger(getClass());
        public DBApiBinderImpl(ApiBinder apiBinder) {
            super(apiBinder);
        }
        //
        @Override
        public void addDataSource(DataSource dataSource) {
            this.addDataSource(null, dataSource);
        }
        @Override
        public void addDataSource(Provider<DataSource> dataSource) {
            this.addDataSource(null, dataSource);
        }
        @Override
        public void addDataSource(String dataSourceID, DataSource dataSource) {
            this.addDataSource(dataSourceID, new InstanceProvider<DataSource>(dataSource));
        }
        //
        //
        @Override
        public void addDataSource(String dataSourceID, Provider<DataSource> dataSource) {
            Hasor.assertIsNotNull(dataSource, "dataSource is null.");
            Provider<TransactionManager> managerProvider = new TransactionManagerProvider(dataSource);
            Provider<TransactionTemplate> templateProvider = new TransactionTemplateProvider(dataSource);
            //
            if (StringUtils.isBlank(dataSourceID)) {
                this.bindType(DataSource.class).toProvider(dataSource);
                this.bindType(TransactionManager.class).toProvider(new SingleProvider<TransactionManager>(managerProvider));
                this.bindType(TransactionTemplate.class).toProvider(new SingleProvider<TransactionTemplate>(templateProvider));
                this.bindType(JdbcTemplate.class).toProvider(new JdbcTemplateProvider(dataSource));
                this.bindType(JdbcOperations.class).toProvider(new JdbcOperationsProvider(dataSource));
            } else {
                this.bindType(DataSource.class).nameWith(dataSourceID).toProvider(dataSource);
                this.bindType(TransactionManager.class).nameWith(dataSourceID).toProvider(new SingleProvider<TransactionManager>(managerProvider));
                this.bindType(TransactionTemplate.class).nameWith(dataSourceID).toProvider(new SingleProvider<TransactionTemplate>(templateProvider));
                this.bindType(JdbcTemplate.class).nameWith(dataSourceID).toProvider(new JdbcTemplateProvider(dataSource));
                this.bindType(JdbcOperations.class).nameWith(dataSourceID).toProvider(new JdbcOperationsProvider(dataSource));
            }
            //
            TransactionInterceptor tranInter = new TransactionInterceptor(dataSource);
            Matcher<Class<?>> matcherClass = AopMatchers.annotatedWithClass(Transactional.class);
            Matcher<Method> matcherMethod = AopMatchers.annotatedWithMethod(Transactional.class);
            this.bindInterceptor(matcherClass, matcherMethod, tranInter);
        }
    }
}
