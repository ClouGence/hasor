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
package net.hasor.mvc.around;
import net.hasor.core.Hasor;
import net.hasor.mvc.strategy.CallStrategy;
import net.hasor.mvc.support.Call;
/**
 * {@link CallStrategy} 接口形式的 MVC 拦截器入口。
 * @version : 2013-5-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class AroundCallStrategy implements CallStrategy {
    private final CallStrategy  callStrategy;
    private AroundInterceptor[] interceptor;
    //
    public AroundCallStrategy(CallStrategy callStrategy, AroundInterceptor[] interceptor) {
        Hasor.assertIsNotNull(callStrategy, "callStrategy is null.");
        this.callStrategy = callStrategy;
        this.interceptor = interceptor;
    }
    //
    public Object exeCall(Call call) throws Throwable {
        if (this.interceptor == null) {
            return callStrategy.exeCall(call);
        }
        //
        AroundPoint point = new AroundPoint() {
            public Object doCall(Call call) throws Throwable {
                return callStrategy.exeCall(call);
            }
        };
        return new AroundChainInvocation(this.interceptor, point).doCall(call);
    }
}