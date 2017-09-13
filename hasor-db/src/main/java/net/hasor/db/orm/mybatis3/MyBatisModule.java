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
package net.hasor.db.orm.mybatis3;
import net.hasor.core.*;
import net.hasor.core.provider.InfoAwareProvider;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.core.provider.SingleProvider;
import net.hasor.utils.StringUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Reader;
/**
 * mybatis 插件
 * @version : 2016年1月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class MyBatisModule implements Module {
    protected Logger                      logger         = LoggerFactory.getLogger(getClass());
    private   String                      dataSourceID   = null;
    private   Provider<SqlSessionFactory> sessionFactory = null;
    //
    public MyBatisModule(String sqlmapConfig) throws IOException {
        this(null, defaultSessionFactory(sqlmapConfig));
    }
    public MyBatisModule(SqlSessionFactory sessionFactory) {
        this(null, new InstanceProvider<SqlSessionFactory>(Hasor.assertIsNotNull(sessionFactory, "sessionFactory is null.")));
    }
    public MyBatisModule(Provider<SqlSessionFactory> sessionFactory) {
        this(null, sessionFactory);
    }
    public MyBatisModule(String dataSourceID, String sqlmapConfig) throws IOException {
        this(dataSourceID, defaultSessionFactory(sqlmapConfig));
    }
    public MyBatisModule(String dataSourceID, SqlSessionFactory sessionFactory) {
        this(dataSourceID, new InstanceProvider<SqlSessionFactory>(Hasor.assertIsNotNull(sessionFactory, "sessionFactory is null.")));
    }
    public MyBatisModule(String dataSourceID, Provider<SqlSessionFactory> sessionFactory) {
        this.dataSourceID = dataSourceID;
        this.sessionFactory = Hasor.assertIsNotNull(sessionFactory, "sessionFactory is null.");
    }
    //
    private static SingleProvider<SqlSessionFactory> defaultSessionFactory(final String sqlmapConfig) throws IOException {
        Hasor.assertIsNotNull(sqlmapConfig, "sqlmapConfig is null.");
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final Reader reader = Hasor.assertIsNotNull(Resources.getResourceAsReader(loader, sqlmapConfig), "could not find resource '" + sqlmapConfig + "'");
        return new SingleProvider<SqlSessionFactory>(new Provider<SqlSessionFactory>() {
            public SqlSessionFactory get() {
                return new SqlSessionFactoryBuilder().build(reader);
            }
        });
    }
    //
    public void loadModule(ApiBinder apiBinder) {
        if (StringUtils.isBlank(this.dataSourceID)) {
            // .检测依赖
            BindInfo<DataSource> bindInfo = apiBinder.getBindInfo(DataSource.class);
            if (bindInfo == null) {
                throw new IllegalStateException("need DataSource.");
            }
            // .初始化MyBatis绑定
            Provider<DataSource> dataSource = Hasor.autoAware(apiBinder.getEnvironment(), new InfoAwareProvider<DataSource>(bindInfo));
            final SqlExecutorTemplateProvider templateProvider = new SqlExecutorTemplateProvider(this.sessionFactory, dataSource);
            apiBinder.bindType(SqlExecutorTemplate.class).toProvider(templateProvider);
            apiBinder.bindType(SqlExecutorOperations.class).toProvider(new Provider<SqlExecutorOperations>() {
                public SqlExecutorOperations get() {
                    return templateProvider.get();
                }
            });
        } else {
            // .检测依赖
            BindInfo<DataSource> bindInfo = apiBinder.findBindingRegister(this.dataSourceID, DataSource.class);
            if (bindInfo == null) {
                throw new IllegalStateException("need DataSource.");
            }
            // .初始化MyBatis绑定
            Provider<DataSource> dataSource = Hasor.autoAware(apiBinder.getEnvironment(), new InfoAwareProvider<DataSource>(bindInfo));
            final SqlExecutorTemplateProvider templateProvider = new SqlExecutorTemplateProvider(this.sessionFactory, dataSource);
            apiBinder.bindType(SqlExecutorTemplate.class).nameWith(this.dataSourceID).toProvider(templateProvider);
            apiBinder.bindType(SqlExecutorOperations.class).nameWith(this.dataSourceID).toProvider(new Provider<SqlExecutorOperations>() {
                public SqlExecutorOperations get() {
                    return templateProvider.get();
                }
            });
        }
    }
}