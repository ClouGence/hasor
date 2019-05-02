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
package net.hasor.db.transaction.interceptor;
import net.hasor.core.Hasor;
import net.hasor.core.MethodInterceptor;
import net.hasor.core.MethodInvocation;
import net.hasor.db.transaction.*;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.function.Supplier;
/**
 * 某一个数据源的事务管理器
 * @version : 2015年11月10日
 * @author 赵永春 (zyc@hasor.net)
 */
public class TransactionInterceptor implements MethodInterceptor {
    private Supplier<DataSource> dataSource = null;
    public TransactionInterceptor(Supplier<DataSource> dataSource) {
        this.dataSource = Hasor.assertIsNotNull(dataSource, "dataSource Provider is null.");
    }
    //
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
    //
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