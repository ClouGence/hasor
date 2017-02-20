/*
 * Copyright 2008-2009 the original author or authors.
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
package net.hasor.rsf.filters.local;
import net.hasor.core.Provider;
import net.hasor.rsf.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 * 优先检查本地是否有服务提供（优先本地服务提供者的调用）。
 * 提示:如果是 p2p 调用则本地调用优先失效。
 * @version : 2014年11月30日
 * @author 赵永春(zyc@hasor.net)
 */
public class LocalPref implements RsfFilter {
    //
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        if (request.isLocal() && !request.isP2PCalls()) {
            RsfBindInfo<?> bindInfo = request.getBindInfo();
            Provider<?> provider = request.getContext().getServiceProvider(bindInfo);
            if (provider != null) {
                String method = request.getMethod().getName();
                Class<?>[] rParams = request.getParameterTypes();
                Object[] rObjects = request.getParameterObject();
                //
                Method m = provider.get().getClass().getMethod(method, rParams);
                try {
                    response.sendData(m.invoke(provider.get(), rObjects));
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
                return;
            }
        }
        //
        chain.doFilter(request, response);
    }
}