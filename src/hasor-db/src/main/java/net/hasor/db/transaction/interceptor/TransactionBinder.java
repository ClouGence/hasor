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
import net.hasor.db.transaction.Isolation;
import net.hasor.db.transaction.Propagation;
/**
 * 
 * @version : 2014年7月17日
 * @author 赵永春(zyc@hasor.net)
 */
public class TransactionBinder {
    private ApiBinder apiBinder = null;
    public TransactionBinder(ApiBinder apiBinder) {
        this.apiBinder = apiBinder;
        //格式：  <修饰符> <返回值> <类名>.<方法名>(<参数签名>)
    }
    //
    public TranBind bind(DataSource dataSource) {
        
    };
    //
    public static class TranBind {
        public TranBind aroundOperation(TranOperations around);
        public StrategyBind matcher(String matcher) {};
        public StrategyBind matcher(Matcher<Method> matcher) {};
    }
    /*策略绑定*/
    public static class StrategyBind {
        private Matcher<Method>           matcher             = null;
        private TranStrategy<Isolation>   isolationStrategy   = new FixedTranStrategy<Isolation>(Isolation.DEFAULT);
        private TranStrategy<Propagation> propagationStrategy = new FixedTranStrategy<Propagation>(Propagation.REQUIRED);
        //
        public StrategyBind withPropagation(Propagation propagation) {
            Hasor.assertIsNotNull(propagation, "param propagation is null.");
            return this.withPropagation(new FixedTranStrategy<Propagation>(propagation));
        };
        public StrategyBind withPropagation(TranStrategy<Propagation> propagation) {
            Hasor.assertIsNotNull(propagation, "param propagation is null.");
            this.propagationStrategy = propagation;
            return this;
        };
        public StrategyBind withIsolation(Isolation isolation) {
            Hasor.assertIsNotNull(isolation, "param isolation is null.");
            return this.withIsolation(new FixedTranStrategy<Isolation>(isolation));
        };
        public StrategyBind withIsolation(TranStrategy<Isolation> isolation) {
            Hasor.assertIsNotNull(isolation, "param isolation is null.");
            this.isolationStrategy = isolation;
            return this;
        };
    }
    /*策略固定值*/
    private static class FixedTranStrategy<T> implements TranStrategy<T> {
        private T value = null;
        public FixedTranStrategy(T value) {
            this.value = value;
        }
        public T getStrategy(Method targetMethod) {
            return this.value;
        }
    }
}