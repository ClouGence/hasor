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
import net.hasor.core.ApiBinder.Matcher;
import net.hasor.db.transaction.Isolation;
import net.hasor.db.transaction.Propagation;
/**
 * 
 * @version : 2014-7-19
 * @author 赵永春(zyc@hasor.net)
 */
class StrategyDefinition implements Matcher<Method> {
    private DataSource                dataSource          = null;
    private Matcher<Method>           matcher             = null;
    private TranOperations            around              = null;
    private TranStrategy<Isolation>   isolationStrategy   = new FixedValueStrategy<Isolation>(Isolation.DEFAULT);
    private TranStrategy<Propagation> propagationStrategy = new FixedValueStrategy<Propagation>(Propagation.REQUIRED);
    //
    public StrategyDefinition(DataSource dataSource, Matcher<Method> matcher) {
        this.dataSource = dataSource;
        this.matcher = matcher;
    }
    public void setTranOperations(TranOperations around) {
        this.around = around;
    }
    public void setPropagation(TranStrategy<Propagation> propagation) {
        this.propagationStrategy = propagation;
    }
    public void setIsolation(TranStrategy<Isolation> isolation) {
        this.isolationStrategy = isolation;
    }
    public DataSource getDataSource() {
        return dataSource;
    }
    public TranOperations getAround() {
        return around;
    }
    public TranStrategy<Isolation> getIsolationStrategy() {
        return isolationStrategy;
    }
    public TranStrategy<Propagation> getPropagationStrategy() {
        return propagationStrategy;
    }
    public boolean matches(Method method) {
        return this.matcher.matches(method);
    }
}