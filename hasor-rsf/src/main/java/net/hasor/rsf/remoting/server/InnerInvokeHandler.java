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
package net.hasor.rsf.remoting.server;
import java.lang.reflect.Method;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.common.constants.ProtocolStatus;
import net.hasor.rsf.common.metadata.ServiceMetaData;
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
        ServiceMetaData<?> metaData = request.getMetaData();
        Object targetObj = request.getContext().getBindCenter().getBean(metaData);
        //
        if (targetObj == null) {
            response.sendStatus(ProtocolStatus.Forbidden, "failed to get service.");
            return;
        }
        Method method = request.getServiceMethod();
        Object[] pObjects = request.getParameterObject();
        Object resData = method.invoke(targetObj, pObjects);
        response.sendData(resData);
    }
}