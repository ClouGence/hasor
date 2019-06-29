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
package net.hasor.db.mybatis3;
import net.hasor.core.*;
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
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
/**
 * mybatis 插件
 * @version : 2016年1月10日
 * @author 赵永春 (zyc@hasor.net)
 */
public class MyBatisModule implements Module {
    protected Logger                      logger         = LoggerFactory.getLogger(getClass());
    private   String                      dataSourceID   = null;
    private   Supplier<SqlSessionFactory> sessionFactory = null;
    //
    public MyBatisModule(String sqlmapConfig) throws IOException {
        this(null, defaultSessionFactory(sqlmapConfig));
    }
    public MyBatisModule(SqlSessionFactory sessionFactory) {
        this(null, InstanceProvider.of(Objects.requireNonNull(sessionFactory, "sessionFactory is null.")));
    }
    public MyBatisModule(Supplier<SqlSessionFactory> sessionFactory) {
        this(null, sessionFactory);
    }
    public MyBatisModule(String dataSourceID, String sqlmapConfig) throws IOException {
        this(dataSourceID, defaultSessionFactory(sqlmapConfig));
    }
    public MyBatisModule(String dataSourceID, SqlSessionFactory sessionFactory) {
        this(dataSourceID, InstanceProvider.of(Objects.requireNonNull(sessionFactory, "sessionFactory is null.")));
    }
    public MyBatisModule(String dataSourceID, Supplier<SqlSessionFactory> sessionFactory) {
        this.dataSourceID = dataSourceID;
        this.sessionFactory = Objects.requireNonNull(sessionFactory, "sessionFactory is null.");
    }
    //
    private static SingleProvider<SqlSessionFactory> defaultSessionFactory(final String sqlmapConfig) throws IOException {
        Objects.requireNonNull(sqlmapConfig, "sqlmapConfig is null.");
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Reader resourceAsReader = Resources.getResourceAsReader(loader, sqlmapConfig);
        final Reader reader = Objects.requireNonNull(resourceAsReader, "could not find resource '" + sqlmapConfig + "'");
        return new SingleProvider<>(() -> new SqlSessionFactoryBuilder().build(reader));
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
            Supplier<DataSource> dataSource = apiBinder.getProvider(bindInfo);
            final SqlExecutorTemplateProvider templateProvider = new SqlExecutorTemplateProvider(this.sessionFactory, dataSource);
            apiBinder.bindType(SqlExecutorTemplate.class).toProvider(templateProvider);
            apiBinder.bindType(SqlExecutorOperations.class).toProvider(templateProvider);
        } else {
            // .检测依赖
            BindInfo<DataSource> bindInfo = apiBinder.findBindingRegister(this.dataSourceID, DataSource.class);
            if (bindInfo == null) {
                throw new IllegalStateException("need DataSource.");
            }
            // .初始化MyBatis绑定
            Supplier<DataSource> dataSource = apiBinder.getProvider(bindInfo);
            final SqlExecutorTemplateProvider templateProvider = new SqlExecutorTemplateProvider(this.sessionFactory, dataSource);
            apiBinder.bindType(SqlExecutorTemplate.class).nameWith(this.dataSourceID).toProvider(templateProvider);
            apiBinder.bindType(SqlExecutorOperations.class).nameWith(this.dataSourceID).toProvider(templateProvider);
        }
        //
        Supplier<AppContext> contextSupplier = apiBinder.getProvider(AppContext.class);
        Collection<Class<?>> mappers = this.sessionFactory.get().getConfiguration().getMapperRegistry().getMappers();
        mappers.forEach(mapper -> {
            apiBinder.bindType(mapper).toProvider(new InnerSqlMapperProxy<>(mapper, contextSupplier));
            logger.info("Registering Ibatis Mapper: {}", mapper.getClass().getName());
        });
        logger.info("mysql mybatis init ok.");
    }
}