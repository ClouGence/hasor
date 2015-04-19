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
package net.hasor.rsf.rpc.utils;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfOptionSet;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.rsf.constants.RsfException;
import net.hasor.rsf.protocol.protocol.ProtocolUtils;
import net.hasor.rsf.rpc.RsfRequestImpl;
import net.hasor.rsf.rpc.RsfResponseImpl;
import net.hasor.rsf.rpc.client.RsfRequestManager;
import net.hasor.rsf.rpc.component.NetworkConnection;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.rpc.message.RequestMsg;
import net.hasor.rsf.rpc.message.ResponseMsg;
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
    public static RsfRequestImpl buildRequest(RsfBindInfo<?> bindInfo, RsfRequestManager rsfClient, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) throws RsfException {
        //1.基本信息
        AbstractRsfContext rsfContext = rsfClient.getRsfContext();
        RequestMsg requestMsg = new RequestMsg();
        requestMsg.setVersion(rsfContext.getSettings().getVersion());
        requestMsg.setRequestID(requestID.getAndIncrement());
        requestMsg.setServiceName(bindInfo.getBindName());//远程服务名
        requestMsg.setServiceGroup(bindInfo.getBindGroup());//远程服务分组
        requestMsg.setServiceVersion(bindInfo.getBindVersion());//远程服务版本
        requestMsg.setTargetMethod(methodName);//远程服务方法名
        requestMsg.setSerializeType(bindInfo.getSerializeType());//序列化策略
        requestMsg.setClientTimeout(bindInfo.getClientTimeout());//远程客户端超时时间
        //2.序列化调用参数
        try {
            SerializeFactory serializeFactory = rsfContext.getSerializeFactory();
            SerializeCoder coder = serializeFactory.getSerializeCoder(requestMsg.getSerializeType());
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> paramType = parameterTypes[i];
                byte[] rawData = coder.encode(parameterObjects[i]);
                requestMsg.addParameter(ProtocolUtils.toAsmType(paramType), rawData);
            }
        } catch (Throwable e) {
            throw new RsfException(ProtocolStatus.SerializeError, e);
        }
        //3.Opt参数
        RsfOptionSet optSet = rsfClient.getRsfContext().getSettings().getClientOption();
        String[] optKeys = optSet.getOptionKeys();
        for (String optKey : optKeys) {
            String optVar = optSet.getOption(optKey);
            requestMsg.addOption(optKey, optVar);
        }
        //4.RsfRequest
        return new RsfRequestImpl(true, parameterTypes, parameterObjects, bindInfo, requestMsg, rsfContext);
    }
    //
    /**从请求数据包中恢复{@link RsfRequest}对象。*/
    public static RsfRequestImpl recoverRequest(RequestMsg requestMsg, NetworkConnection connection, AbstractRsfContext rsfContext) throws RsfException {
        //1.获取MetaData
        RsfBindInfo<?> bindInfo = rsfContext.getBindCenter().getService(//
                requestMsg.getServiceGroup(), requestMsg.getServiceName(), requestMsg.getServiceVersion());
        Object[] parameterObjects = null;//
        Class<?>[] parameterTypes = null;//
        //
        if (bindInfo == null) {
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
        return new RsfRequestImpl(false, parameterTypes, parameterObjects, bindInfo, requestMsg, rsfContext);
    }
    /**从响应数据包中恢复{@link RsfResponse}对象。*/
    public static RsfResponse recoverResponse(ResponseMsg responseMsg, RsfRequest rsfRequest, AbstractRsfContext rsfContext) throws Throwable {
        RsfBindInfo<?> metaData = rsfRequest.getBindInfo();
        Class<?> returnType = rsfRequest.getServiceMethod().getReturnType();
        Object returnObject = responseMsg.getReturnData(rsfContext.getSerializeFactory());
        return new RsfResponseImpl(metaData, responseMsg, returnObject, returnType);
    }
    /**生成BindID*/
    public static String bindID(String group, String name, String version) {
        return String.format("[%s]%s-%s", group, name, version);
    }
    /**使用指定的ClassLoader将一个asm类型转化为Class对象。*/
    public static Class<?> toJavaType(final String tType, final ClassLoader loader) throws ClassNotFoundException {
        char atChar = tType.charAt(0);
        if (/*   */'I' == atChar) {
            return int.class;
        } else if ('B' == atChar) {
            return byte.class;
        } else if ('C' == atChar) {
            return char.class;
        } else if ('D' == atChar) {
            return double.class;
        } else if ('F' == atChar) {
            return float.class;
        } else if ('J' == atChar) {
            return long.class;
        } else if ('S' == atChar) {
            return short.class;
        } else if ('Z' == atChar) {
            return boolean.class;
        } else if ('V' == atChar) {
            return void.class;
        } else if (atChar == '[') {
            int length = 0;
            while (true) {
                if (tType.charAt(length) != '[') {
                    break;
                }
                length++;
            }
            String arrayType = tType.substring(length, tType.length());
            Class<?> returnType = toJavaType(arrayType, loader);
            for (int i = 0; i < length; i++) {
                Object obj = Array.newInstance(returnType, length);
                returnType = obj.getClass();
            }
            return returnType;
        } else {
            Class<?> cache = loadClassCache.get(tType);
            if (cache == null) {
                cache = loader.loadClass(tType);
                loadClassCache.put(tType, cache);
            }
            return cache;
        }
    }
    /**将某一个类型转为asm形式的表述， int 转为 I，String转为 Ljava/lang/String。*/
    public static String toAsmType(final Class<?> classType) {
        if (classType == int.class) {
            return "I";
        } else if (classType == byte.class) {
            return "B";
        } else if (classType == char.class) {
            return "C";
        } else if (classType == double.class) {
            return "D";
        } else if (classType == float.class) {
            return "F";
        } else if (classType == long.class) {
            return "J";
        } else if (classType == short.class) {
            return "S";
        } else if (classType == boolean.class) {
            return "Z";
        } else if (classType == void.class) {
            return "V";
        } else if (classType.isArray() == true) {
            return "[" + toAsmType(classType.getComponentType());
        } else {
            return classType.getName();
        }
    }
    private static Map<String, Class<?>> loadClassCache = new java.util.concurrent.ConcurrentHashMap<String, Class<?>>();
}