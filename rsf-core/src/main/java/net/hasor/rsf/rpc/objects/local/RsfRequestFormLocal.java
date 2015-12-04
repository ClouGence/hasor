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
package net.hasor.rsf.rpc.objects.local;
import java.lang.reflect.Method;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfOptionSet;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.domain.RSFConstants;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.protocol.protocol.OptionManager;
import net.hasor.rsf.protocol.protocol.RequestBlock;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.serialize.SerializeCoder;
import net.hasor.rsf.serialize.SerializeFactory;
import net.hasor.rsf.utils.ByteStringCachelUtils;
import net.hasor.rsf.utils.ProtocolUtils;
import net.hasor.rsf.utils.RsfRuntimeUtils;
/**
 * RSF请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfRequestFormLocal extends OptionManager implements RsfRequest {
    private final AbstractRsfContext rsfContext;
    private final long               requestID;
    private final RsfBindInfo<?>     bindInfo;
    private final Method             targetMethod;
    private final Class<?>[]         parameterTypes;
    private final Object[]           parameterObjects;
    private final long               receiveTime;
    //
    public RsfRequestFormLocal(RsfBindInfo<?> bindInfo, Method targetMethod, Object[] parameterObjects, AbstractRsfContext rsfContext) throws RsfException {
        this.requestID = RsfRuntimeUtils.genRequestID();
        this.bindInfo = bindInfo;
        this.targetMethod = targetMethod;
        this.parameterTypes = targetMethod.getParameterTypes();
        this.parameterObjects = parameterObjects;
        this.rsfContext = rsfContext;
        this.receiveTime = System.currentTimeMillis();
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
    public byte getVersion() {
        return RSFConstants.Version_1;
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
    public Method getServiceMethod() {
        return this.targetMethod;
    }
    @Override
    public RsfContext getContext() {
        return this.rsfContext;
    }
    @Override
    public long getReceiveTime() {
        return this.receiveTime;
    }
    @Override
    public int getTimeout() {
        return this.bindInfo.getClientTimeout();
    }
    @Override
    public String getMethod() {
        return this.targetMethod.getName();
    }
    @Override
    public Class<?>[] getParameterTypes() {
        return this.parameterTypes;
    }
    @Override
    public Object[] getParameterObject() {
        return this.parameterObjects;
    }
    //
    public RsfResponseFormLocal buildResponse() {
        RsfResponseFormLocal rsfResponse = new RsfResponseFormLocal(this);
        RsfOptionSet optMap = this.rsfContext.getSettings().getServerOption();
        for (String optKey : optMap.getOptionKeys()) {
            rsfResponse.addOption(optKey, optMap.getOption(optKey));
        }
        return rsfResponse;
    }
    public RequestBlock buildSocketBlock(SerializeFactory serializeFactory) throws Throwable {
        SerializeCoder coder = serializeFactory.getSerializeCoder(getSerializeType());
        RsfBindInfo<?> rsfBindInfo = this.getBindInfo();
        RequestBlock block = new RequestBlock();
        //
        //1.基本信息
        block.setHead(RSFConstants.RSF_Request);
        block.setRequestID(getRequestID());//请求ID
        block.setServiceGroup(ProtocolUtils.pushString(block, rsfBindInfo.getBindGroup()));//序列化策略
        block.setServiceName(ProtocolUtils.pushString(block, rsfBindInfo.getBindName()));//序列化策略
        block.setServiceVersion(ProtocolUtils.pushString(block, rsfBindInfo.getBindVersion()));//序列化策略
        block.setTargetMethod(ProtocolUtils.pushString(block, getMethod()));//序列化策略
        block.setSerializeType(ProtocolUtils.pushString(block, getSerializeType()));//序列化策略
        block.setClientTimeout(getTimeout());
        //
        //2.params
        Class<?>[] pTypes = getParameterTypes();
        Object[] pObjects = getParameterObject();
        for (int i = 0; i < pTypes.length; i++) {
            byte[] typeByte = ByteStringCachelUtils.fromCache(RsfRuntimeUtils.toAsmType(pTypes[i]));
            byte[] paramByte = coder.encode(pObjects[i]);
            //
            short paramType = block.pushData(typeByte);
            short paramData = block.pushData(paramByte);
            //
            block.addParameter(paramType, paramData);
        }
        //
        //3.Opt参数
        String[] optKeys = getOptionKeys();
        for (int i = 0; i < optKeys.length; i++) {
            short optKey = ProtocolUtils.pushString(block, optKeys[i]);
            short optVal = ProtocolUtils.pushString(block, getOption(optKeys[i]));
            block.addOption(optKey, optVal);
        }
        //
        return block;
    }
}