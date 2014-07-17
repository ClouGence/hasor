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
import javax.sql.DataSource;
import net.hasor.core.AppContext;
import net.hasor.db.transaction.Isolation;
import net.hasor.db.transaction.Manager;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionManager;
import net.hasor.db.transaction.TransactionStatus;
import net.hasor.db.transaction.interceptor.support.MatcherInterceptor;
import net.hasor.db.transaction.interceptor.support.PropagationStrategy;
import net.hasor.db.transaction.interceptor.support.TranDo;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
/**
 * 某一个数据源的事务管理器
 * @author 赵永春(zyc@hasor.net)
 * @version : 2013-10-30
 */
class TranInterceptor implements MethodInterceptor {
    private TransactionManager  tranManager = null; //事务管理器
    private MatcherInterceptor  matcher     = null; //事务拦截匹配
    private PropagationStrategy strategy    = null; //策略
    private TranOperations      tranOper    = null;
    //
    /**初始化拦截器*/
    public void initInterceptor(AppContext appContext, DataSource dataSource) {
        TranOperations tranOper = appContext.findBindingBean(null, TranOperations.class);
        PropagationStrategy strategy = appContext.findBindingBean(null, PropagationStrategy.class);
        if (tranOper != null)
            this.tranOper = tranOper;
        if (strategy != null)
            this.strategy = strategy;
        //
        //3.装载事务管理器
        this.tranManager = Manager.getTransactionManager(dataSource);
    }
    //
    //
    public final Object invoke(final MethodInvocation invocation) throws Throwable {
        //1.是否排除不实用事务管理器
        Method targetMethod = invocation.getMethod();
        if (this.matcher == null || this.matcher.matcherMethod(targetMethod) == false)
            return invocation.proceed();
        //
        //3.在事务管理器的控制下进行方法调用
        String useDataSourceName = this.strategy.useDataSource(targetMethod);
        TransactionManager manager = this.tranManager.get(useDataSourceName);
        if (manager == null)
            return invocation.proceed();
        //
        TransactionStatus tranStatus = null;
        try {
            Propagation propagation = this.strategy.getPropagation(targetMethod);
            Isolation isolation = this.strategy.getIsolation(targetMethod);
            if (isolation == null)
                isolation = Isolation.DEFAULT;
            tranStatus = manager.getTransaction(propagation, isolation);
            TranDo tdo = new TranDo() {
                public Object proceed() throws Throwable {
                    return invocation.proceed();
                }
                public Method getMethod() {
                    return invocation.getMethod();
                }
                public Object[] getArgs() {
                    return invocation.getArguments();
                }
            };
            //
            return this.tranOper.execute(tranStatus, tdo);
        } catch (RollBackSQLException e) {
            tranStatus.setRollbackOnly(); /*回滚事务*/
        } catch (Throwable e) {
            manager.rollBack(tranStatus);
            throw e;
        } finally {
            if (!tranStatus.isCompleted())
                manager.commit(tranStatus);
        }
        return null;
    }
}