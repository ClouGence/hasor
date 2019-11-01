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
import net.hasor.core.Module;
import net.hasor.core.exts.aop.Matchers;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.db.jdbc.JdbcOperations;
import net.hasor.db.jdbc.core.JdbcConnection;
import net.hasor.db.jdbc.core.JdbcOperationsProvider;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.core.JdbcTemplateProvider;
import net.hasor.db.transaction.TransactionManager;
import net.hasor.db.transaction.TransactionTemplate;
import net.hasor.db.transaction.interceptor.TransactionInterceptor;
import net.hasor.db.transaction.interceptor.Transactional;
import net.hasor.db.transaction.provider.TransactionManagerProvider;
import net.hasor.db.transaction.provider.TransactionTemplateProvider;
import net.hasor.utils.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * DB 模块。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class JdbcModule implements Module {
    private Set<Level>           loadLevel;
    private String               dataSourceID;
    private Supplier<DataSource> dataSource;

    /** 添加数据源 */
    public JdbcModule(Level loadLevel, DataSource dataSource) {
        this(new Level[] { loadLevel }, null, new InstanceProvider<>(Objects.requireNonNull(dataSource)));
    }

    /** 添加数据源 */
    public JdbcModule(Level loadLevel, Supplier<DataSource> dataSource) {
        this(new Level[] { loadLevel }, null, dataSource);
    }

    /** 添加数据源 */
    public JdbcModule(Level loadLevel, String name, DataSource dataSource) {
        this(new Level[] { loadLevel }, name, new InstanceProvider<>(Objects.requireNonNull(dataSource)));
    }

    /** 添加数据源 */
    public JdbcModule(Level[] loadLevel, DataSource dataSource) {
        this(loadLevel, null, new InstanceProvider<>(Objects.requireNonNull(dataSource)));
    }

    /** 添加数据源 */
    public JdbcModule(Level[] loadLevel, Supplier<DataSource> dataSource) {
        this(loadLevel, null, dataSource);
    }

    /** 添加数据源 */
    public JdbcModule(Level[] loadLevel, String name, Supplier<DataSource> dataSource) {
        Objects.requireNonNull(loadLevel, "loadLevel is null.");
        Objects.requireNonNull(dataSource, "dataSource Provider is null.");
        this.loadLevel = new HashSet<>(Arrays.asList(loadLevel));
        this.dataSourceID = name;
        this.dataSource = dataSource;
    }

    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        boolean loadData = this.loadLevel.contains(Level.Full) || this.loadLevel.contains(Level.DataSource);
        boolean loadJdbc = this.loadLevel.contains(Level.Full) || this.loadLevel.contains(Level.Jdbc);
        boolean loadTran = this.loadLevel.contains(Level.Full) || this.loadLevel.contains(Level.Tran);
        //
        if (loadData) {
            if (StringUtils.isBlank(this.dataSourceID)) {
                apiBinder.bindType(DataSource.class).toProvider(this.dataSource);
            } else {
                apiBinder.bindType(DataSource.class).nameWith(this.dataSourceID).toProvider(this.dataSource);
            }
        }
        //
        if (loadJdbc) {
            JdbcTemplateProvider tempProvider = new JdbcTemplateProvider(this.dataSource);
            JdbcOperationsProvider operProvider = new JdbcOperationsProvider(this.dataSource);
            if (StringUtils.isBlank(this.dataSourceID)) {
                apiBinder.bindType(JdbcConnection.class).toProvider(tempProvider);
                apiBinder.bindType(JdbcTemplate.class).toProvider(tempProvider);
                apiBinder.bindType(JdbcOperations.class).toProvider(operProvider);
            } else {
                apiBinder.bindType(JdbcConnection.class).nameWith(this.dataSourceID).toProvider(tempProvider);
                apiBinder.bindType(JdbcTemplate.class).nameWith(this.dataSourceID).toProvider(tempProvider);
                apiBinder.bindType(JdbcOperations.class).nameWith(this.dataSourceID).toProvider(operProvider);
            }
        }
        //
        if (loadTran) {
            Supplier<TransactionManager> managerProvider = new TransactionManagerProvider(this.dataSource);
            Supplier<TransactionTemplate> templateProvider = new TransactionTemplateProvider(this.dataSource);
            if (StringUtils.isBlank(this.dataSourceID)) {
                apiBinder.bindType(TransactionManager.class).toProvider(managerProvider);
                apiBinder.bindType(TransactionTemplate.class).toProvider(templateProvider);
            } else {
                apiBinder.bindType(TransactionManager.class).nameWith(this.dataSourceID).toProvider(managerProvider);
                apiBinder.bindType(TransactionTemplate.class).nameWith(this.dataSourceID).toProvider(templateProvider);
            }
            TransactionInterceptor tranInter = new TransactionInterceptor(this.dataSource);
            Predicate<Class<?>> matcherClass = Matchers.annotatedWithClass(Transactional.class);
            Predicate<Method> matcherMethod = Matchers.annotatedWithMethod(Transactional.class);
            apiBinder.bindInterceptor(matcherClass, matcherMethod, tranInter);
        }
    }
}
