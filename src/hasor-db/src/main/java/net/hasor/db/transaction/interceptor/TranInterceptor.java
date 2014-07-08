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
import net.hasor.core.binder.InstanceProvider;
import net.hasor.db.transaction.Manager;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionManager;
import net.hasor.db.transaction.TransactionStatus;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.more.util.ClassUtils;
/**
 * 某一个数据源的事务管理器
 * @author 赵永春(zyc@hasor.net)
 * @version : 2013-10-30
 */
public class TranInterceptor implements MethodInterceptor {
    private Provider<DataSource> dataSourceProvider = null; //数据源
    private TransactionManager   transactionManager = null; //事务管理器
    private TranStrategy[]       strategyArrays     = null; //事务策略
    private Propagation          defaultStrategy    = null; //默认事务策略
    //
    public static void main(String[] args) {
        System.out.println(TransactionManager.class.getMethods()[1]);
    }
    //
    /**创建TranInterceptor对象。*/
    public TranInterceptor(DataSource dataSource) {
        this(new InstanceProvider<DataSource>(dataSource));
    }
    /**创建TranInterceptor对象。*/
    public TranInterceptor(Provider<DataSource> dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
        this.strategyArrays = new TranStrategy[0];
        this.defaultStrategy = Propagation.REQUIRED;/*默认策略，加入已有事务*/
    }
    //
    /**获取当前的数据源*/
    protected TransactionManager getTransactionManager() {
        if (this.transactionManager == null) {
            DataSource ds = this.dataSourceProvider.get();
            this.transactionManager = Manager.getTransactionManager(ds);
        }
        return this.transactionManager;
    }
    /**获取用于目标方法的传播属性。*/
    protected Propagation getPropagation(Method method) {
        String descName = ClassUtils.getDescName(method);
        //
        //格式：  <修饰符> <返回值> <类名>.<方法名>(<参数签名>)
        for (TranStrategy strategy : this.strategyArrays) {
            //
        }
        return this.defaultStrategy;
    }
    //
    public final Object invoke(MethodInvocation invocation) throws Throwable {
        //1.是否排除不实用事务管理器
        Method targetMethod = invocation.getMethod();
        Class<?> targetClass = targetMethod.getDeclaringClass();
        //
        if (!this.matcherClass(targetClass))
            return invocation.proceed();
        if (!this.matcherMethod(targetMethod))
            return invocation.proceed();
        //2.在事务管理器的控制下进行方法调用
        TransactionStatus tranStatus = null;
        TransactionManager tranManager = this.getTransactionManager();
        try {
            Propagation propagation = getPropagation(targetMethod);
            tranStatus = tranManager.getTransaction(propagation);
            return invocation.proceed();
        } catch (Throwable e) {
            tranManager.rollBack(tranStatus);
            throw e;
        } finally {
            if (!tranStatus.isCompleted())
                tranManager.commit(tranStatus);
        }
    }
    /**匹配拦截的类*/
    protected boolean matcherClass(Class<?> targetClass) {
        return true;
    }
    /**匹配拦截的类方法*/
    protected boolean matcherMethod(Method targetMethod) {
        return true;
    }
    //
    private static class TranStrategy {
        private String      key         = null;
        private Propagation propagation = null;
        public String getKey() {
            return key;
        }
        public void setKey(String key) {
            this.key = key;
        }
        public Propagation getPropagation() {
            return propagation;
        }
        public void setPropagation(Propagation propagation) {
            this.propagation = propagation;
        }
    }
}