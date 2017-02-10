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
package net.hasor.rsf.rpc.caller;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.domain.AttributeSet;
import net.hasor.rsf.domain.RsfRuntimeUtils;
import net.hasor.rsf.domain.provider.AddressProvider;

import java.lang.reflect.Method;
/**
 * RSF请求(本地发起调用)
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfRequestFormLocal extends AttributeSet implements RsfRequest {
    private final RsfCaller       rsfCaller;
    private final AddressProvider target;
    private final long            requestID;
    private final RsfBindInfo<?>  bindInfo;
    private final Method          targetMethod;
    private final Class<?>[]      parameterTypes;
    private final Object[]        parameterObjects;
    //
    public RsfRequestFormLocal(AddressProvider target, RsfBindInfo<?> bindInfo, Method targetMethod, Object[] parameterObjects, RsfCaller rsfCaller) {
        this.requestID = RsfRuntimeUtils.genRequestID();
        this.target = target;
        this.bindInfo = bindInfo;
        this.targetMethod = targetMethod;
        this.parameterTypes = targetMethod.getParameterTypes();
        this.parameterObjects = parameterObjects;
        this.rsfCaller = rsfCaller;
    }
    @Override
    public String toString() {
        return "requestID:" + this.getRequestID() + " from Local," + this.bindInfo.toString();
    }
    /**获取最终要调用的远程服务地址。*/
    public AddressProvider getTarget() {
        return this.target;
    }
    @Override
    public boolean isP2PCalls() {
        return !this.target.isDistributed();
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
    public boolean isMessage() {
        return this.bindInfo.isMessage();
    }
    @Override
    public Method getMethod() {
        return this.targetMethod;
    }
    @Override
    public RsfContext getContext() {
        return this.rsfCaller.getContext();
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
        return this.parameterTypes == null ? new Class[0] : this.parameterTypes.clone();
    }
    @Override
    public Object[] getParameterObject() {
        return this.parameterObjects == null ? new Object[0] : this.parameterObjects.clone();
    }
    @Override
    public InterAddress getRemoteAddress() {
        RsfContext rsfContext = this.rsfCaller.getContext();
        String protocol = null;
        if (this.getTargetAddress() == null) {
            protocol = rsfContext.getSettings().getDefaultProtocol();
        } else {
            protocol = getTargetAddress().getSechma();
        }
        return rsfContext.publishAddress(protocol);
    }
    @Override
    public InterAddress getTargetAddress() {
        return !this.target.isDistributed() ? this.target.get(null, null, null) : null;
    }
}