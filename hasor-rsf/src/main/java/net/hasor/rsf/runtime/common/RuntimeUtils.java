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
package net.hasor.rsf.runtime.common;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.general.RsfException;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.protocol.message.RequestMsg;
import net.hasor.rsf.protocol.message.ResponseMsg;
import net.hasor.rsf.protocol.toos.ProtocolUtils;
import net.hasor.rsf.runtime.RsfContext;
import net.hasor.rsf.runtime.RsfRequest;
import net.hasor.rsf.runtime.RsfResponse;
import net.hasor.rsf.runtime.client.RsfClient;
import net.hasor.rsf.runtime.context.AbstractRsfContext;
import net.hasor.rsf.serialize.SerializeCoder;
import net.hasor.rsf.serialize.SerializeFactory;
/**
 * 
 * @version : 2014年11月17日
 * @author 赵永春(zyc@hasor.net)
 */
public class RuntimeUtils {
    private static AtomicLong requestID = new AtomicLong(1);
    //
    /**根据元信息创建一个{@link RsfRequest}对象。*/
    public static RsfRequestImpl buildRequest(ServiceMetaData metaData, NetworkConnection connection, RsfClient rsfClient, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) throws RsfException {
        //1.基本信息
        RsfContext rsfContext = rsfClient.getRsfContext();
        RequestMsg requestMsg = new RequestMsg();
        requestMsg.setVersion(rsfContext.getSettings().getVersion());
        requestMsg.setRequestID(requestID.getAndIncrement());
        requestMsg.setServiceName(metaData.getServiceName());//远程服务名
        requestMsg.setServiceGroup(metaData.getServiceGroup());//远程服务分组
        requestMsg.setServiceVersion(metaData.getServiceVersion());//远程服务版本
        requestMsg.setTargetMethod(methodName);//远程服务方法名
        requestMsg.setSerializeType(metaData.getSerializeType());//序列化策略
        requestMsg.setClientTimeout(metaData.getClientTimeout());//远程客户端超时时间
        //2.序列化调用参数
        try {
            SerializeFactory serializeFactory = rsfContext.getSerializeFactory();
            SerializeCoder coder = serializeFactory.getSerializeCoder(requestMsg.getSerializeType());
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> paramType = parameterTypes[i];
                byte[] rawData = coder.encode(parameterObjects[i]);
                requestMsg.addParameter(paramType.getName(), rawData);
            }
        } catch (Throwable e) {
            throw new RsfException(ProtocolStatus.SerializeError, e);
        }
        //3.Opt参数
        String[] optKeys = rsfClient.getOptionKeys();
        for (String optKey : optKeys) {
            String optVar = rsfClient.getOption(optKey);
            requestMsg.addOption(optKey, optVar);
        }
        //4.RsfRequest
        return new RsfRequestImpl(true, parameterTypes, parameterObjects, metaData, requestMsg, connection, rsfContext);
    }
    //
    /**从请求数据包中恢复{@link RsfRequest}对象。*/
    public static RsfRequestImpl recoverRequest(RequestMsg requestMsg, NetworkConnection connection, AbstractRsfContext rsfContext) throws RsfException {
        //1.获取MetaData
        ServiceMetaData metaData = rsfContext.getService(//
                requestMsg.getServiceName(), requestMsg.getServiceGroup(), requestMsg.getServiceVersion());
        Object[] parameterObjects = null;//
        Class<?>[] parameterTypes = null;//
        //
        if (metaData == null) {
            throw new RsfException(ProtocolStatus.NotFound, "service was not found.");
        }
        //2.反序列化
        try {
            SerializeFactory serializeFactory = rsfContext.getSerializeFactory();
            parameterObjects = requestMsg.toParameters(serializeFactory);
            List<String> pTypes = requestMsg.getParameterTypes();
            parameterTypes = new Class<?>[pTypes.size()];
            for (int i = 0; i < pTypes.size(); i++) {
                parameterTypes[i] = ProtocolUtils.toJavaType(pTypes.get(i), Thread.currentThread().getContextClassLoader());
            }
        } catch (RsfException e) {
            throw e;
        } catch (Throwable e) {
            throw new RsfException(ProtocolStatus.SerializeError, e);
        }
        //4.RsfRequest and check Forbidden
        return new RsfRequestImpl(false, parameterTypes, parameterObjects, metaData, requestMsg, connection, rsfContext);
    }
    /**从响应数据包中恢复{@link RsfResponse}对象。*/
    public static RsfResponse recoverResponse(ResponseMsg responseMsg, RsfRequest rsfRequest, AbstractRsfContext rsfContext) throws Throwable {
        ServiceMetaData metaData = rsfRequest.getMetaData();
        Class<?> returnType = rsfRequest.getServiceMethod().getReturnType();
        Object returnObject = responseMsg.getReturnData(rsfContext.getSerializeFactory());
        return new RsfResponseImpl(metaData, responseMsg, returnObject, returnType);
    }
}