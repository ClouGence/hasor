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
package net.hasor.db.transaction.interceptor;
import java.lang.reflect.Method;
import java.util.List;
import javax.sql.DataSource;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.MethodInterceptor;
import net.hasor.core.MethodInvocation;
import net.hasor.db.transaction.Isolation;
import net.hasor.db.transaction.Manager;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionManager;
import net.hasor.db.transaction.TransactionStatus;
/**
 * 某一个数据源的事务管理器
 * @author 赵永春(zyc@hasor.net)
 * @version : 2013-10-30
 */
class TranInterceptor implements MethodInterceptor, AppContextAware {
    public void setAppContext(AppContext appContext) {
        List<StrategyDefinition> defineList = appContext.findBindingBean(StrategyDefinition.class);
        if (defineList != null && defineList.isEmpty() == false) {
            this.definitionArray = defineList.toArray(new StrategyDefinition[defineList.size()]);
        }
    }
    //
    private StrategyDefinition[] definitionArray = null;
    @Override
    public final Object invoke(final MethodInvocation invocation) throws Throwable {
        //1.排除的情况
        if (this.definitionArray == null || this.definitionArray.length == 0) {
            return invocation.proceed();
        }
        //2.找到匹配的策略
        Method targetMethod = invocation.getMethod();
        StrategyDefinition atDefine = null;
        for (StrategyDefinition define : this.definitionArray) {
            if (define.matches(targetMethod) == true) {
                atDefine = define;
                break;
            }
        }
        if (atDefine == null) {
            return invocation.proceed();
        }
        //3.准备事务
        DataSource dataSource = atDefine.getDataSource();
        Propagation propagation = atDefine.getPropagationStrategy().getStrategy(targetMethod);
        Isolation isolation = atDefine.getIsolationStrategy().getStrategy(targetMethod);
        TranOperations around = atDefine.getAround();
        //
        TransactionManager manager = Manager.getTransactionManager(dataSource);
        TransactionStatus tranStatus = null;
        try {
            tranStatus = manager.getTransaction(propagation, isolation);
            return around.execute(tranStatus, invocation);
        } catch (Throwable e) {
            if (tranStatus != null) {
                tranStatus.setRollbackOnly();
            }
            throw e;
        } finally {
            if (tranStatus != null && !tranStatus.isCompleted()) {
                manager.commit(tranStatus);
            }
        }
    }
}