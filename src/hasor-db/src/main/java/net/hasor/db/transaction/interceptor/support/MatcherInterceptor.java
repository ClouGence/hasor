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
package net.hasor.db.transaction.interceptor.support;
import java.lang.reflect.Method;
/**
 * 匹配器：用于协助拦截器判断哪些方法会被事务拦截器拦截。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2013-10-30
 */
public interface MatcherInterceptor {
    /**获取用于目标方法的传播属性。*/
    protected Propagation getPropagation(Method method) {
        String descName = ClassUtils.getDescName(method);s
        //
        //格式：  <修饰符> <返回值> <类名>.<方法名>(<参数签名>)
        for (TranStrategy strategy : this.strategyArrays) {
            //
        }
        return this.defaultStrategy;
    }
    /**匹配拦截的类方法*/
    public boolean matcherMethod(Method targetMethod);
}