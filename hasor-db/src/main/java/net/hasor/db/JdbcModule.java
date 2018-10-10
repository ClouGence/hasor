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
import net.hasor.core.*;
import net.hasor.core.classcode.matcher.Matchers;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.core.provider.SingleProvider;
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
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
/**
 * DB 模块。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class JdbcModule implements Module {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private Set<Level>           loadLevel;
    private String               dataSourceID;
    private Provider<DataSource> dataSource;
    //
    /** 添加数据源 */
    public JdbcModule(Level loadLevel, DataSource dataSource) {
        this(new Level[] { loadLevel }, null, new InstanceProvider<DataSource>(Hasor.assertIsNotNull(dataSource)));
    }
    /** 添加数据源 */
    public JdbcModule(Level loadLevel, Provider<DataSource> dataSource) {
        this(new Level[] { loadLevel }, null, dataSource);
    }
    /** 添加数据源 */
    public JdbcModule(Level loadLevel, String name, DataSource dataSource) {
        this(new Level[] { loadLevel }, name, new InstanceProvider<DataSource>(Hasor.assertIsNotNull(dataSource)));
    }
    //
    /** 添加数据源 */
    public JdbcModule(Level[] loadLevel, DataSource dataSource) {
        this(loadLevel, null, new InstanceProvider<DataSource>(Hasor.assertIsNotNull(dataSource)));
    }
    /** 添加数据源 */
    public JdbcModule(Level[] loadLevel, Provider<DataSource> dataSource) {
        this(loadLevel, null, dataSource);
    }
    /** 添加数据源 */
    public JdbcModule(Level[] loadLevel, String name, Provider<DataSource> dataSource) {
        Hasor.assertIsNotNull(loadLevel, "loadLevel is null.");
        Hasor.assertIsNotNull(dataSource, "dataSource Provider is null.");
        this.loadLevel = new HashSet<Level>(Arrays.asList(loadLevel));
        this.dataSourceID = name;
        this.dataSource = dataSource;
    }
    //
    //
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
            if (StringUtils.isBlank(this.dataSourceID)) {
                apiBinder.bindType(JdbcTemplate.class).toProvider(new JdbcTemplateProvider(this.dataSource));
                apiBinder.bindType(JdbcOperations.class).toProvider(new JdbcOperationsProvider(this.dataSource));
            } else {
                apiBinder.bindType(JdbcTemplate.class).nameWith(this.dataSourceID).toProvider(new JdbcTemplateProvider(this.dataSource));
                apiBinder.bindType(JdbcOperations.class).nameWith(this.dataSourceID).toProvider(new JdbcOperationsProvider(this.dataSource));
            }
        }
        //
        if (loadTran) {
            Provider<TransactionManager> managerProvider = new TransactionManagerProvider(this.dataSource);
            Provider<TransactionTemplate> templateProvider = new TransactionTemplateProvider(this.dataSource);
            if (StringUtils.isBlank(this.dataSourceID)) {
                apiBinder.bindType(TransactionManager.class).toProvider(new SingleProvider<TransactionManager>(managerProvider));
                apiBinder.bindType(TransactionTemplate.class).toProvider(new SingleProvider<TransactionTemplate>(templateProvider));
            } else {
                apiBinder.bindType(TransactionManager.class).nameWith(this.dataSourceID).toProvider(new SingleProvider<TransactionManager>(managerProvider));
                apiBinder.bindType(TransactionTemplate.class).nameWith(this.dataSourceID).toProvider(new SingleProvider<TransactionTemplate>(templateProvider));
            }
            TransactionInterceptor tranInter = new TransactionInterceptor(this.dataSource);
            Matcher<Class<?>> matcherClass = Matchers.annotatedWithClass(Transactional.class);
            Matcher<Method> matcherMethod = Matchers.annotatedWithMethod(Transactional.class);
            apiBinder.bindInterceptor(matcherClass, matcherMethod, tranInter);
        }
    }
}
