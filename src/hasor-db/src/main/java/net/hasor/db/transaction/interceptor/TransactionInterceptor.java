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
import net.hasor.core.Provider;
import net.hasor.core.binder.aop.AopMatcherMethodInterceptor;
import net.hasor.db.transaction.TransactionBehavior;
import net.hasor.db.transaction.TransactionManager;
import net.hasor.db.transaction.TransactionStatus;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
/**
 * 某一个数据源的事务管理器
 * @author 赵永春(zyc@hasor.net)
 * @version : 2013-10-30
 */
public class TransactionInterceptor implements MethodInterceptor {
    private Provider<DataSource>        dataSourceProvider;
    private TransactionBehavior         behavior;
    private TransactionManager          transactionManager;
    private AopMatcherMethodInterceptor interceptorMatcher;
    //
    //
    public final Object invoke(MethodInvocation invocation) throws Throwable {
        //1.是否排除不实用事务管理器
        Class<?> targetC = invocation.getThis().getClass();
        if (!this.matcherClass(targetC))
            return invocation.proceed();
        Method targetM = invocation.getMethod();
        if (!this.matcherMethod(targetM))
            return invocation.proceed();
        //2.在事务管理器的控制下进行方法调用
        TransactionStatus tranStatus = null;
        try {
            tranStatus = this.transactionManager.getTransaction(this.behavior);
            return invocation.proceed();
        } catch (Throwable e) {
            this.transactionManager.rollBack(tranStatus);
            throw e;
        } finally {
            if (!tranStatus.isCompleted())
                this.transactionManager.commit(tranStatus);
        }
    }
    /**匹配拦截的类*/
    protected boolean matcherClass(Class<?> targetClass) {
        if (this.interceptorMatcher != null)
            return this.interceptorMatcher.matcher(targetClass);
        return true;
    }
    /**匹配拦截的类方法*/
    protected boolean matcherMethod(Method targetMethod) {
        if (this.interceptorMatcher != null)
            return this.interceptorMatcher.matcher(targetMethod);
        return true;
    }s
}