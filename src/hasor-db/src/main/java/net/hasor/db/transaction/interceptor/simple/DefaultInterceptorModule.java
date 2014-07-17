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
package net.hasor.db.transaction.interceptor.simple;
import java.lang.reflect.Method;
import javax.sql.DataSource;
import net.hasor.core.ApiBinder;
import net.hasor.core.MethodInvocation;
import net.hasor.core.Module;
import net.hasor.core.binder.aop.matcher.AopMatchers;
import net.hasor.db.transaction.Isolation;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionStatus;
import net.hasor.db.transaction.interceptor.TranOperations;
import net.hasor.db.transaction.interceptor.TranStrategy;
import net.hasor.db.transaction.interceptor.TransactionBinder;
/**
 * 事务策略：用于决定数据源的事务策略。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2013-10-30
 */
public class DefaultInterceptorModule implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        DataSource ds = null;
        //
        TransactionBinder it = new TransactionBinder(apiBinder);
        it.bind(ds)/*设置到数据源*/
        .aroundOperation(new TransactionOperation())/*事务执行行为*/
        .matcher(AopMatchers.annotatedWithMethod(Transactional.class))/*所有标记 @Transactional 注解的方法*/
        .withPropagation(new PropagationStrategy())/*传播属性*/
        .withIsolation(new IsolationStrategy());/*隔离级别*/
    }
}
class TransactionOperation implements TranOperations {
    public Object execute(TransactionStatus tranStatus, MethodInvocation invocation) throws Throwable {
        Method targetMethod = invocation.getMethod();
        Transactional tranAnno = targetMethod.getAnnotation(Transactional.class);
        //0.忽略
        if (tranAnno == null)
            return invocation.proceed();
        //1.只读事务
        if (tranAnno.readOnly()) {
            tranStatus.setReadOnly();
            return invocation.proceed();
        }
        //2.事务行为控制
        Object returnObj = null;
        try {
            returnObj = invocation.proceed();
        } catch (Throwable e) {
            // TODO: handle exception
        }
        return returnObj;
    }
}
/**决定传播属性*/
class PropagationStrategy implements TranStrategy<Propagation> {
    public Propagation getStrategy(Method targetMethod) {
        Transactional tranAnno = targetMethod.getAnnotation(Transactional.class);
        if (tranAnno == null)
            return Propagation.REQUIRED;//默认设置
        return tranAnno.propagation();
    }
}
/**决定隔离级别*/
class IsolationStrategy implements TranStrategy<Isolation> {
    public Isolation getStrategy(Method targetMethod) {
        Transactional tranAnno = targetMethod.getAnnotation(Transactional.class);
        if (tranAnno == null)
            return Isolation.DEFAULT;//默认设置
        return tranAnno.isolation();
    }
}