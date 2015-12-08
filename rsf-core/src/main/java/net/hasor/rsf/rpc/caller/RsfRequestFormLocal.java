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
package net.hasor.rsf.rpc.caller;
import java.lang.reflect.Method;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.transform.protocol.OptionInfo;
/**
 * RSF请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfRequestFormLocal extends OptionInfo implements RsfRequest {
    private final AbstractCaller callerManager;
    private final InterAddress   targetServer;
    private final long           requestID;
    private final RsfBindInfo<?> bindInfo;
    private final Method         targetMethod;
    private final Class<?>[]     parameterTypes;
    private final Object[]       parameterObjects;
    //
    public RsfRequestFormLocal(InterAddress target, RsfBindInfo<?> bindInfo, Method targetMethod, Object[] parameterObjects, AbstractCaller callerManager) {
        this.requestID = RsfRuntimeUtils.genRequestID();
        this.targetServer = target;
        this.bindInfo = bindInfo;
        this.targetMethod = targetMethod;
        this.parameterTypes = targetMethod.getParameterTypes();
        this.parameterObjects = parameterObjects;
        this.callerManager = callerManager;
    }
    @Override
    public String toString() {
        return "requestID:" + this.getRequestID() + " from Local," + this.bindInfo.toString();
    }
    //
    @Override
    public RsfBindInfo<?> getBindInfo() {
        return this.bindInfo;
    }
    @Override
    public long getRequestID() {
        return this.requestID;
    }
    @Override
    public String getSerializeType() {
        return this.bindInfo.getSerializeType();
    }
    @Override
    public boolean isLocal() {
        return true;
    }
    @Override
    public Method getMethod() {
        return this.targetMethod;
    }
    @Override
    public RsfContext getContext() {
        return this.callerManager.getContext();
    }
    @Override
    public long getReceiveTime() {
        return System.currentTimeMillis();
    }
    @Override
    public int getTimeout() {
        return this.bindInfo.getClientTimeout();
    }
    @Override
    public Class<?>[] getParameterTypes() {
        return this.parameterTypes.clone();
    }
    @Override
    public Object[] getParameterObject() {
        return this.parameterObjects.clone();
    }
}