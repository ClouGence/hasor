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
package net.test.hasor.db._06_transaction.custom;
import java.lang.reflect.Method;
import javax.sql.DataSource;
import net.hasor.core.ApiBinder;
import net.hasor.core.Module;
import net.hasor.core.binder.aop.matcher.AopMatchers;
import net.hasor.db.transaction.Isolation;
import net.hasor.db.transaction.Propagation;
import net.hasor.plugins.db.TranStrategy;
import net.hasor.plugins.db.TransactionBinder;
import net.hasor.plugins.db.interceptor.Transactional;
/**
 * 自定义声明式事务
 * @author 赵永春(zyc@hasor.net)
 * @version : 2013-10-30
 */
public class CustomInterceptorModule implements Module {
    private DataSource dataSource = null;
    //
    public CustomInterceptorModule(final DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Override
    public void loadModule(final ApiBinder apiBinder) throws Throwable {
        TransactionBinder it = new TransactionBinder(apiBinder);
        it.bind(this.dataSource)/*设置到数据源*/
        .matcher(AopMatchers.annotatedWithMethod(Tran.class))/*所有标记 @Tran 注解的方法*/
        .withPropagation(new PropagationStrategy())/*传播属性*/
        .withIsolation(new IsolationStrategy());/*隔离级别*/
    }
}
/**决定传播属性*/
class PropagationStrategy implements TranStrategy<Propagation> {
    @Override
    public Propagation getStrategy(final Method targetMethod) {
        Transactional tranAnno = targetMethod.getAnnotation(Transactional.class);
        if (tranAnno == null) {
            return Propagation.REQUIRED;//默认设置
        }
        return tranAnno.propagation();
    }
}
/**决定隔离级别*/
class IsolationStrategy implements TranStrategy<Isolation> {
    @Override
    public Isolation getStrategy(final Method targetMethod) {
        Transactional tranAnno = targetMethod.getAnnotation(Transactional.class);
        if (tranAnno == null) {
            return Isolation.DEFAULT;//默认设置
        }
        return tranAnno.isolation();
    }
}
