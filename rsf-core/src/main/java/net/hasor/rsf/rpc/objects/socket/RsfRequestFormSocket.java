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
package net.hasor.rsf.rpc.objects.socket;
import java.lang.reflect.Method;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfOptionSet;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.protocol.protocol.PoolSocketBlock;
import net.hasor.rsf.protocol.protocol.RequestSocketBlock;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.rpc.objects.local.RsfResponseFormLocal;
import net.hasor.rsf.serialize.SerializeCoder;
import net.hasor.rsf.serialize.SerializeFactory;
import net.hasor.rsf.utils.ByteStringCachelUtils;
import net.hasor.rsf.utils.RsfRuntimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * RSF请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfRequestFormSocket extends RsfBaseFormSocket<AbstractRsfContext, RequestSocketBlock> implements RsfRequest {
    protected Logger           logger = LoggerFactory.getLogger(getClass());
    private AbstractRsfContext rsfContext;
    private RsfBindInfo<?>     bindInfo;
    private String             targetMethodName;
    private Method             targetMethod;
    private Class<?>[]         parameterTypes;
    private Object[]           parameterObjects;
    //
    public RsfRequestFormSocket(AbstractRsfContext rsfContext, RequestSocketBlock rsfBlock) {
        super(rsfContext, rsfBlock);
        this.rsfContext = rsfContext;
    }
    @Override
    public void recovery(AbstractRsfContext context, RequestSocketBlock rsfBlock) {
        super.recovery(context, rsfBlock);
        //
        this.targetMethodName = ByteStringCachelUtils.fromCache(rsfBlock.readPool(rsfBlock.getTargetMethod()));
        String group = ByteStringCachelUtils.fromCache(rsfBlock.readPool(rsfBlock.getServiceGroup()));
        String name = ByteStringCachelUtils.fromCache(rsfBlock.readPool(rsfBlock.getServiceName()));
        String version = ByteStringCachelUtils.fromCache(rsfBlock.readPool(rsfBlock.getServiceVersion()));
        this.bindInfo = context.getBindCenter().getService(group, name, version);
        if (bindInfo == null) {
            throw new RsfException(ProtocolStatus.NotFound, "service was not found.");
        }
        //
        SerializeFactory serializeFactory = context.getSerializeFactory();
        SerializeCoder coder = serializeFactory.getSerializeCoder(this.getSerializeType());
        //
        int[] paramDatas = rsfBlock.getParameters();
        this.parameterTypes = new Class<?>[paramDatas.length];
        this.parameterObjects = new Object[paramDatas.length];
        for (int i = 0; i < paramDatas.length; i++) {
            int paramItem = paramDatas[i];
            short paramKey = (short) (paramItem >>> 16);
            short paramVal = (short) (paramItem & PoolSocketBlock.PoolMaxSize);
            byte[] keyData = rsfBlock.readPool(paramKey);
            byte[] valData = rsfBlock.readPool(paramVal);
            //
            try {
                String keyName = ByteStringCachelUtils.fromCache(keyData);
                this.parameterTypes[i] = RsfRuntimeUtils.getType(keyName, context.getClassLoader());
                this.parameterObjects[i] = coder.decode(valData);
            } catch (Throwable e) {
                logger.error("recovery form Socket > " + e.getMessage(), e);
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new RsfException(e.getMessage(), e);
                }
            }
        }
    }
    @Override
    public String toString() {
        return "requestID:" + this.getRequestID() + " from Socket," + this.bindInfo.toString();
    }
    //
    //
    //
    @Override
    public RsfBindInfo<?> getBindInfo() {
        return this.bindInfo;
    }
    @Override
    public boolean isLocal() {
        return false;
    }
    @Override
    public Method getServiceMethod() {
        if (this.targetMethod == null) {
            Class<?> targetType = this.bindInfo.getBindType();
            this.targetMethod = RsfRuntimeUtils.getServiceMethod(targetType, this.targetMethodName, this.parameterTypes);
        }
        return this.targetMethod;
    }
    @Override
    public String getMethod() {
        return this.targetMethodName;
    }
    @Override
    public RsfContext getContext() {
        return this.rsfContext;
    }
    @Override
    public long getReceiveTime() {
        return this.getRsfBlock().getReceiveTime();
    }
    @Override
    public int getTimeout() {
        return this.getRsfBlock().getClientTimeout();
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
}