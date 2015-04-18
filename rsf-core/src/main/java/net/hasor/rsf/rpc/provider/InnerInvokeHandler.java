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
package net.hasor.rsf.rpc.provider;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.constants.ProtocolStatus;
/**
 * 负责处理服务的调用。
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
class InnerInvokeHandler implements RsfFilterChain {
    public static RsfFilterChain Default = new InnerInvokeHandler();
    //
    //default invoke
    public void doFilter(RsfRequest request, RsfResponse response) throws Throwable {
        if (response.isResponse() == true)
            return;
        RsfBindInfo<?> bindInfo = request.getBindInfo();
        Object targetObj = request.getContext().getBean(bindInfo);
        //
        if (targetObj == null) {
            response.sendStatus(ProtocolStatus.Forbidden, "failed to get service.");
            return;
        }
        //
        try {
            Method method = request.getServiceMethod();
            Object[] pObjects = request.getParameterObject();
            Object resData = method.invoke(targetObj, pObjects);
            response.sendData(resData);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}