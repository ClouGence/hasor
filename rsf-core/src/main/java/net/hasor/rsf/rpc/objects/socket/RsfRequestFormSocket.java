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
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.rsf.constants.RsfException;
import net.hasor.rsf.protocol.protocol.PoolSocketBlock;
import net.hasor.rsf.protocol.protocol.RequestSocketBlock;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.serialize.SerializeCoder;
import net.hasor.rsf.serialize.SerializeFactory;
import net.hasor.rsf.utils.RsfRuntimeUtils;
import org.more.logger.LoggerHelper;
/**
 * RSF请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfRequestFormSocket extends RsfBaseFormSocket<AbstractRsfContext, RequestSocketBlock> implements RsfRequest {
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
    public void recovery(RequestSocketBlock rsfBlock) {
        super.recovery(rsfBlock);
        //
        this.targetMethodName = new String(rsfBlock.readPool(rsfBlock.getTargetMethod()));
        String group = new String(rsfBlock.readPool(rsfBlock.getServiceGroup()));
        String name = new String(rsfBlock.readPool(rsfBlock.getServiceName()));
        String version = new String(rsfBlock.readPool(rsfBlock.getServiceVersion()));
        this.bindInfo = this.rsfContext.getBindCenter().getService(group, name, version);
        if (bindInfo == null) {
            throw new RsfException(ProtocolStatus.NotFound, "service was not found.");
        }
        //
        SerializeFactory serializeFactory = this.rsfContext.getSerializeFactory();
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
                this.parameterTypes[i] = RsfRuntimeUtils.getType(keyData, this.rsfContext.getClassLoader());
                this.parameterObjects[i] = coder.decode(valData);
            } catch (Throwable e) {
                LoggerHelper.logSevere(e.getMessage(), e);
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
        // TODO Auto-generated method stub
        return 0;
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
}