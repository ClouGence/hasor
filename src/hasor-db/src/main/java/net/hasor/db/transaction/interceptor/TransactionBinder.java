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
import net.hasor.core.ApiBinder;
import net.hasor.core.ApiBinder.Matcher;
import net.hasor.core.Hasor;
import net.hasor.core.binder.aop.matcher.AopMatchers;
import net.hasor.db.transaction.Isolation;
import net.hasor.db.transaction.Propagation;
/**
 * 一个被事务拦截器拦截的方法，当有多个数据源可以选择时，只能控制一个数据源的事务。
 * 一个方法当调用多个小方法时，每个小方法可以有自己独立的事务。
 * 一个带有事务的方法可以调用另外一个不同数据源的事务方法。
 * @version : 2014年7月17日
 * @author 赵永春(zyc@hasor.net)
 */
public class TransactionBinder {
    private ApiBinder apiBinder = null;
    public TransactionBinder(ApiBinder apiBinder) {
        this.apiBinder = apiBinder;
    }
    //
    /*---------------------------------------------------------------------------------------Bind*/
    public TranInterceptorBindBuilder bind(DataSource dataSource) {
        TranInterceptor tranInterceptor = new TranInterceptor();
        this.apiBinder.registerAware(tranInterceptor);
        this.apiBinder.bindInterceptor(AopMatchers.anyClass(), AopMatchers.anyMethod(), tranInterceptor);
        return new TranInterceptorBindBuilder(dataSource);
    }
    //
    /*---------------------------------------------------------------------------------------Bind*/
    /**拦截器配置*/
    public class TranInterceptorBindBuilder {
        private DataSource     dataSource = null;
        private TranOperations around     = null;
        public TranInterceptorBindBuilder(DataSource dataSource) {
            this.dataSource = dataSource;
        }
        public TranInterceptorBindBuilder aroundOperation(TranOperations around) {
            this.around = around;
            return this;
        };
        public TranPropagationBindBuilder matcher(String matcherExpression) {
            return this.matcher(AopMatchers.expressionMethod(matcherExpression));
        };
        public TranPropagationBindBuilder matcher(Matcher<Method> matcher) {
            StrategyDefinition strategyDefine = new StrategyDefinition(this.dataSource, matcher);
            strategyDefine.setTranOperations(this.around);
            return new TranPropagationBindBuilder(strategyDefine);
        }
    }
    /**拦截策略配置*/
    public class TranPropagationBindBuilder {
        private StrategyDefinition strategyDefine = null;
        public TranPropagationBindBuilder(StrategyDefinition strategyDefine) {
            this.strategyDefine = strategyDefine;
        }
        public TranIsolationBindBuilder withPropagation(Propagation propagation) {
            Hasor.assertIsNotNull(propagation, "param propagation is null.");
            return this.withPropagation(new FixedValueStrategy<Propagation>(propagation));
        };
        public TranIsolationBindBuilder withPropagation(TranStrategy<Propagation> propagation) {
            Hasor.assertIsNotNull(propagation, "param propagation is null.");
            this.strategyDefine.setPropagation(propagation);
            apiBinder.bindType(StrategyDefinition.class).uniqueName().toInstance(this.strategyDefine);
            return new TranIsolationBindBuilder(this.strategyDefine);
        };
    }
    /**隔离级别设置*/
    public static class TranIsolationBindBuilder {
        private StrategyDefinition strategyDefine = null;
        public TranIsolationBindBuilder(StrategyDefinition strategyDefine) {
            this.strategyDefine = strategyDefine;
        }
        public void withIsolation(Isolation isolation) {
            Hasor.assertIsNotNull(isolation, "param isolation is null.");
            this.withIsolation(new FixedValueStrategy<Isolation>(isolation));
        };
        public void withIsolation(TranStrategy<Isolation> isolation) {
            Hasor.assertIsNotNull(isolation, "param isolation is null.");
            this.strategyDefine.setIsolation(isolation);
        };
    }
    //end
}
