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
/**
 * 用于协助拦截器判断，方法的事务传播属性。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2013-10-30
 */
public interface MatcherInterceptor {
    /**匹配拦截的类方法*/
    public boolean matcherMethod(Method targetMethod);
    //    /**获取所使用的事务传播属性*/
    //    public Propagation matcherMethod(Method targetMethod);
}