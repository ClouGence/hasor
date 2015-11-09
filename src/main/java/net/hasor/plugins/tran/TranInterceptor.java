/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.plugins.tran;
import java.lang.reflect.Method;
import net.hasor.core.MethodInterceptor;
import net.hasor.core.MethodInvocation;
import net.hasor.db.transaction.Isolation;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionManager;
import net.hasor.db.transaction.TransactionStatus;
/**
 * 某一个数据源的事务管理器
 * @author 赵永春(zyc@hasor.net)
 * @version : 2013-10-30
 */
class TranInterceptor implements MethodInterceptor {
    /*是否不需要回滚:true表示不要回滚*/
    private boolean testNoRollBackFor(Transactional tranAnno, Throwable e) {
        //1.test Class
        Class<? extends Throwable>[] noRollBackType = tranAnno.noRollbackFor();
        for (Class<? extends Throwable> cls : noRollBackType) {
            if (cls.isInstance(e) == true) {
                return true;
            }
        }
        //2.test Name
        String[] noRollBackName = tranAnno.noRollbackForClassName();
        String errorType = e.getClass().getName();
        for (String name : noRollBackName) {
            if (errorType.equals(name) == true) {
                return true;
            }
        }
        return false;
    }
    //
    @Override
    public final Object invoke(final MethodInvocation invocation) throws Throwable {
        Method targetMethod = invocation.getMethod();
        Transactional tranInfo = targetMethod.getAnnotation(Transactional.class);
        if (tranInfo == null) {
            return invocation.proceed();
        }
        //0.准备事务环境
        TransactionManager manager = atDefine.getTransactionManager();
        TransactionStatus tranStatus = null;
        Propagation behavior = tranInfo.propagation();
        Isolation level = tranInfo.isolation();
        tranStatus = manager.getTransaction(behavior, level);
        //1.只读事务
        if (tranInfo.readOnly()) {
            tranStatus.setReadOnly();
        }
        //2.事务行为控制
        Object returnObj = null;
        try {
            returnObj = invocation.proceed();
        } catch (Throwable e) {
            if (this.testNoRollBackFor(tranInfo, e) == false) {
                tranStatus.setRollbackOnly();
            }
            throw e;
        } finally {
            if (tranStatus.isCompleted() == false) {
                manager.commit(tranStatus);
            }
        }
        //
    }
}