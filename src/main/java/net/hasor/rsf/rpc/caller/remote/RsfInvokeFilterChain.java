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
package net.hasor.rsf.rpc.caller.remote;
import net.hasor.core.Provider;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.domain.ProtocolStatus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 * 负责处理服务的调用。
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfInvokeFilterChain implements RsfFilterChain {
    public static RsfFilterChain Default = new RsfInvokeFilterChain();
    //
    //default invoke
    public void doFilter(RsfRequest request, RsfResponse response) throws Throwable {
        if (response.isResponse())
            return;
        RsfBindInfo<?> bindInfo = request.getBindInfo();
        Provider<?> targetProvider = request.getContext().getServiceProvider(bindInfo);
        Object target = targetProvider == null ? null : targetProvider.get();
        //
        if (target == null) {
            response.sendStatus(ProtocolStatus.NotFound, "service " + bindInfo.getBindID() + " not exist.");
            return;
        }
        //
        try {
            Method refMethod = request.getMethod();
            //Method targetMethod = target.getClass().getMethod(refMethod.getName(), refMethod.getParameterTypes());
            Object[] pObjects = request.getParameterObject();
            Object resData = refMethod.invoke(target, pObjects);
            response.sendData(resData);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}