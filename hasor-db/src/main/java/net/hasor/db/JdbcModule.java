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
import net.hasor.core.MethodInterceptor;
import net.hasor.core.MethodInvocation;
import net.hasor.core.Module;
import net.hasor.core.exts.aop.Matchers;
import net.hasor.db.jdbc.JdbcOperations;
import net.hasor.db.jdbc.core.JdbcAccessor;
import net.hasor.db.jdbc.core.JdbcConnection;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.core.JdbcTemplateProvider;
import net.hasor.db.transaction.*;
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
    private final Set<Level>           loadLevel;
    private final String               dataSourceID;
    private final Supplier<DataSource> dataSource;

    /** 添加数据源 */
    public JdbcModule(Level loadLevel, DataSource dataSource) {
        this(new Level[] { loadLevel }, null, of(Objects.requireNonNull(dataSource)));
    }

    /** 添加数据源 */
    public JdbcModule(Level loadLevel, Supplier<DataSource> dataSource) {
        this(new Level[] { loadLevel }, null, dataSource);
    }

    /** 添加数据源 */
    public JdbcModule(Level loadLevel, String name, DataSource dataSource) {
        this(new Level[] { loadLevel }, name, of(Objects.requireNonNull(dataSource)));
    }

    /** 添加数据源 */
    public JdbcModule(Level[] loadLevel, DataSource dataSource) {
        this(loadLevel, null, of(Objects.requireNonNull(dataSource)));
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

    private static <T> Supplier<T> of(T instance) {
        return () -> instance;
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
            if (StringUtils.isBlank(this.dataSourceID)) {
                apiBinder.bindType(JdbcAccessor.class).toProvider(tempProvider);
                apiBinder.bindType(JdbcConnection.class).toProvider(tempProvider);
                apiBinder.bindType(JdbcTemplate.class).toProvider(tempProvider);
                apiBinder.bindType(JdbcOperations.class).toProvider(tempProvider);
            } else {
                apiBinder.bindType(JdbcAccessor.class).nameWith(this.dataSourceID).toProvider(tempProvider);
                apiBinder.bindType(JdbcConnection.class).nameWith(this.dataSourceID).toProvider(tempProvider);
                apiBinder.bindType(JdbcTemplate.class).nameWith(this.dataSourceID).toProvider(tempProvider);
                apiBinder.bindType(JdbcOperations.class).nameWith(this.dataSourceID).toProvider(tempProvider);
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
            TranInterceptor tranInter = new TranInterceptor(this.dataSource);
            Predicate<Class<?>> matcherClass = Matchers.annotatedWithClass(Transactional.class);
            Predicate<Method> matcherMethod = Matchers.annotatedWithMethod(Transactional.class);
            apiBinder.bindInterceptor(matcherClass, matcherMethod, tranInter);
        }
    }

    private static class TranInterceptor implements MethodInterceptor {
        private Supplier<DataSource> dataSource = null;

        public TranInterceptor(Supplier<DataSource> dataSource) {
            this.dataSource = Objects.requireNonNull(dataSource, "dataSource Provider is null.");
        }

        /*是否不需要回滚:true表示不要回滚*/
        private boolean testNoRollBackFor(Transactional tranAnno, Throwable e) {
            //1.test Class
            Class<? extends Throwable>[] noRollBackType = tranAnno.noRollbackFor();
            for (Class<? extends Throwable> cls : noRollBackType) {
                if (cls.isInstance(e)) {
                    return true;
                }
            }
            //2.test Name
            String[] noRollBackName = tranAnno.noRollbackForClassName();
            String errorType = e.getClass().getName();
            for (String name : noRollBackName) {
                if (errorType.equals(name)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public final Object invoke(final MethodInvocation invocation) throws Throwable {
            Method targetMethod = invocation.getMethod();
            Transactional tranInfo = tranAnnotation(targetMethod);
            if (tranInfo == null) {
                return invocation.proceed();
            }
            //0.准备事务环境
            DataSource dataSource = this.dataSource.get();
            TransactionManager manager = TranManager.getManager(dataSource);
            Propagation behavior = tranInfo.propagation();
            Isolation level = tranInfo.isolation();
            TransactionStatus tranStatus = manager.getTransaction(behavior, level);
            //1.只读事务
            if (tranInfo.readOnly()) {
                tranStatus.setReadOnly();
            }
            //2.事务行为控制
            try {
                return invocation.proceed();
            } catch (Throwable e) {
                if (!this.testNoRollBackFor(tranInfo, e)) {
                    tranStatus.setRollbackOnly();
                }
                throw e;
            } finally {
                if (!tranStatus.isCompleted()) {
                    manager.commit(tranStatus);
                }
            }
        }

        /** 在方法上找 Transactional ，如果找不到在到 类上找 Transactional ，如果依然没有，那么在所处的包(包括父包)上找 Transactional。*/
        private Transactional tranAnnotation(Method targetMethod) {
            Transactional tran = targetMethod.getAnnotation(Transactional.class);
            if (tran == null) {
                Class<?> declaringClass = targetMethod.getDeclaringClass();
                tran = declaringClass.getAnnotation(Transactional.class);
            }
            return tran;
        }
    }
}
