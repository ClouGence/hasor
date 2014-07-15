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
import javax.sql.DataSource;
import net.hasor.core.ApiBinder;
import net.hasor.core.Module;
import net.hasor.core.binder.aop.matcher.AopMatchers;
/**
 * 数据库事务拦截器模块
 * @version : 2014年7月15日
 * @author 赵永春(zyc@hasor.net)
 */
public class InterceptorModule implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        DataSource dataSource = null;;
        //1.绑定Aop
        apiBinder.bindInterceptor(AopMatchers.anyClass(), AopMatchers.anyMethod(),//
                new TranInterceptor(dataSource));
        // TODO Auto-generated method stub
    }
}