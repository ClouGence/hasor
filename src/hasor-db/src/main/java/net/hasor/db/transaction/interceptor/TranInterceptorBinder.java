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
import net.hasor.db.transaction.Isolation;
import net.hasor.db.transaction.Propagation;
/**
 * 
 * @version : 2014年7月17日
 * @author 赵永春(zyc@hasor.net)
 */
public class TranInterceptorBinder {
    public TranInterceptorBinder(ApiBinder apiBinder) {
        // TODO Auto-generated constructor stub
    }
    public StrategyBind matcher(String matcher) {};
    public StrategyBind matcher(Matcher<Method> matcher) {};
    // 
    public static interface StrategyBind {
        public StrategyBind withPropagation(Propagation propagation);
        public StrategyBind withIsolation(Isolation isolation);
        //
        public void done(DataSource dataSource);
    }
}