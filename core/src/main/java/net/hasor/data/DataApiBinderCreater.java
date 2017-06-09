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
package net.hasor.data;
import net.hasor.core.*;
import net.hasor.core.binder.ApiBinderCreater;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.core.classcode.matcher.AopMatchers;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.core.provider.SingleProvider;
import net.hasor.core.utils.StringUtils;
import net.hasor.data.jdbc.JdbcOperations;
import net.hasor.data.jdbc.core.JdbcOperationsProvider;
import net.hasor.data.jdbc.core.JdbcTemplate;
import net.hasor.data.jdbc.core.JdbcTemplateProvider;
import net.hasor.data.ql.UDF;
import net.hasor.data.ql.ctx.UDFDefine;
import net.hasor.data.transaction.TransactionManager;
import net.hasor.data.transaction.TransactionTemplate;
import net.hasor.data.transaction.interceptor.TransactionInterceptor;
import net.hasor.data.transaction.interceptor.Transactional;
import net.hasor.data.transaction.provider.TransactionManagerProvider;
import net.hasor.data.transaction.provider.TransactionTemplateProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Method;
/**
 * DataQL 扩展接口。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class DataApiBinderCreater implements ApiBinderCreater {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public ApiBinder createBinder(final ApiBinder apiBinder) {
        return new DataApiBinderImpl(apiBinder);
    }
    //
    private static class DataApiBinderImpl extends ApiBinderWrap implements DataApiBinder {
        protected Logger logger = LoggerFactory.getLogger(getClass());
        public DataApiBinderImpl(ApiBinder apiBinder) {
            super(apiBinder);
        }
        //
        @Override
        public void addUDF(String name, Class<? extends UDF> udfType) {
            this.addUDF(name, bindType(UDF.class).uniqueName().to(udfType).toInfo());
        }
        @Override
        public void addUDF(String name, UDF dataUDF) {
            this.addUDF(name, bindType(UDF.class).uniqueName().toInstance(dataUDF).toInfo());
        }
        @Override
        public void addUDF(String name, Provider<? extends UDF> udfProvider) {
            this.addUDF(name, bindType(UDF.class).uniqueName().toProvider(udfProvider).toInfo());
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
        public void addUDF(String name, BindInfo<? extends UDF> udfInfo) {
            UDFDefine define = Hasor.autoAware(getEnvironment(), new UDFDefine(name, udfInfo));
            this.bindType(UDFDefine.class).uniqueName().toInstance(define);
        }
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
