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
package net.hasor.rsf.rpc.client;
import io.netty.channel.Channel;
import java.net.MalformedURLException;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.constants.RSFConstants;
import net.hasor.rsf.protocol.protocol.RequestSocketBlock;
import net.hasor.rsf.protocol.protocol.ResponseSocketBlock;
/**
 * 负责{@link RequestMsg}、{@link ResponseMsg}到{@link RequestSocketBlock}、{@link ResponseSocketBlock}的类型转换。
 * @version : 2014年11月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class TransferUtils {

    //    private static AtomicLong requestID = new AtomicLong(1);
    //    //
    //    /**根据元信息创建一个{@link RsfRequest}对象。*/
    //    public static RsfRequestFormLocal buildRequest(RsfBindInfo<?> bindInfo, RsfRequestManager rsfClient, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) throws RsfException {
    //        //1.基本信息
    //        AbstractRsfContext rsfContext = rsfClient.getRsfContext();
    //        RequestMsg requestMsg = new RequestMsg();
    //        requestMsg.setVersion(rsfContext.getSettings().getVersion());
    //        requestMsg.setRequestID(requestID.getAndIncrement());
    //        requestMsg.setServiceName(bindInfo.getBindName());//远程服务名
    //        requestMsg.setServiceGroup(bindInfo.getBindGroup());//远程服务分组
    //        requestMsg.setServiceVersion(bindInfo.getBindVersion());//远程服务版本
    //        requestMsg.setTargetMethod(methodName);//远程服务方法名
    //        requestMsg.setSerializeType(bindInfo.getSerializeType());//序列化策略
    //        requestMsg.setClientTimeout(bindInfo.getClientTimeout());//远程客户端超时时间
    //        //2.序列化调用参数
    //        try {
    //            SerializeFactory serializeFactory = rsfContext.getSerializeFactory();
    //            SerializeCoder coder = serializeFactory.getSerializeCoder(requestMsg.getSerializeType());
    //            for (int i = 0; i < parameterTypes.length; i++) {
    //                Class<?> paramType = parameterTypes[i];
    //                byte[] rawData = coder.encode(parameterObjects[i]);
    //                requestMsg.addParameter(ProtocolUtils.toAsmType(paramType), rawData);
    //            }
    //        } catch (Throwable e) {
    //            throw new RsfException(ProtocolStatus.SerializeError, e);
    //        }
    //        //3.Opt参数
    //        RsfOptionSet optSet = rsfClient.getRsfContext().getSettings().getClientOption();
    //        String[] optKeys = optSet.getOptionKeys();
    //        for (String optKey : optKeys) {
    //            String optVar = optSet.getOption(optKey);
    //            requestMsg.addOption(optKey, optVar);
    //        }
    //        //4.RsfRequest
    //        return new RsfRequestFormLocal(true, parameterTypes, parameterObjects, bindInfo, requestMsg, rsfContext);
    //    }
    //    //
    //    /**从请求数据包中恢复{@link RsfRequest}对象。*/
    //    public static RsfRequestFormLocal recoverRequest(RequestMsg requestMsg, NetworkConnection connection, AbstractRsfContext rsfContext) throws RsfException {
    //        //1.获取MetaData
    //        RsfBindInfo<?> bindInfo = rsfContext.getBindCenter().getService(//
    //                requestMsg.getServiceGroup(), requestMsg.getServiceName(), requestMsg.getServiceVersion());
    //        Object[] parameterObjects = null;//
    //        Class<?>[] parameterTypes = null;//
    //        //
    //        if (bindInfo == null) {
    //            throw new RsfException(ProtocolStatus.NotFound, "service was not found.");
    //        }
    //        //2.反序列化
    //        try {
    //            SerializeFactory serializeFactory = rsfContext.getSerializeFactory();
    //            parameterObjects = requestMsg.toParameters(serializeFactory);
    //            List<String> pTypes = requestMsg.getParameterTypes();
    //            parameterTypes = new Class<?>[pTypes.size()];
    //            for (int i = 0; i < pTypes.size(); i++) {
    //                parameterTypes[i] = ProtocolUtils.toJavaType(pTypes.get(i), Thread.currentThread().getContextClassLoader());
    //            }
    //        } catch (RsfException e) {
    //            throw e;
    //        } catch (Throwable e) {
    //            throw new RsfException(ProtocolStatus.SerializeError, e);
    //        }
    //        //4.RsfRequest and check Forbidden
    //        return new RsfRequestFormLocal(false, parameterTypes, parameterObjects, bindInfo, requestMsg, rsfContext);
    //    }
    //    /**从响应数据包中恢复{@link RsfResponse}对象。*/
    //    public static RsfResponse recoverResponse(ResponseMsg responseMsg, RsfRequest rsfRequest, AbstractRsfContext rsfContext) throws Throwable {
    //        RsfBindInfo<?> metaData = rsfRequest.getBindInfo();
    //        Class<?> returnType = rsfRequest.getServiceMethod().getReturnType();
    //        Object returnObject = responseMsg.getReturnData(rsfContext.getSerializeFactory());
    //        return new RsfResponseFormLocal(metaData, responseMsg, returnObject, returnType);
    //    }
    //    /**生成BindID*/
    //    public static String bindID(String group, String name, String version) {
    //        return String.format("[%s]%s-%s", group, name, version);
    //    }
    //    /**使用指定的ClassLoader将一个asm类型转化为Class对象。*/
    //    public static Class<?> toJavaType(final String tType, final ClassLoader loader) throws ClassNotFoundException {
    //        char atChar = tType.charAt(0);
    //        if (/*   */'I' == atChar) {
    //            return int.class;
    //        } else if ('B' == atChar) {
    //            return byte.class;
    //        } else if ('C' == atChar) {
    //            return char.class;
    //        } else if ('D' == atChar) {
    //            return double.class;
    //        } else if ('F' == atChar) {
    //            return float.class;
    //        } else if ('J' == atChar) {
    //            return long.class;
    //        } else if ('S' == atChar) {
    //            return short.class;
    //        } else if ('Z' == atChar) {
    //            return boolean.class;
    //        } else if ('V' == atChar) {
    //            return void.class;
    //        } else if (atChar == '[') {
    //            int length = 0;
    //            while (true) {
    //                if (tType.charAt(length) != '[') {
    //                    break;
    //                }
    //                length++;
    //            }
    //            String arrayType = tType.substring(length, tType.length());
    //            Class<?> returnType = toJavaType(arrayType, loader);
    //            for (int i = 0; i < length; i++) {
    //                Object obj = Array.newInstance(returnType, length);
    //                returnType = obj.getClass();
    //            }
    //            return returnType;
    //        } else {
    //            Class<?> cache = loadClassCache.get(tType);
    //            if (cache == null) {
    //                cache = loader.loadClass(tType);
    //                loadClassCache.put(tType, cache);
    //            }
    //            return cache;
    //        }
    //    }
    //    /**将某一个类型转为asm形式的表述， int 转为 I，String转为 Ljava/lang/String。*/
    //    public static String toAsmType(final Class<?> classType) {
    //        if (classType == int.class) {
    //            return "I";
    //        } else if (classType == byte.class) {
    //            return "B";
    //        } else if (classType == char.class) {
    //            return "C";
    //        } else if (classType == double.class) {
    //            return "D";
    //        } else if (classType == float.class) {
    //            return "F";
    //        } else if (classType == long.class) {
    //            return "J";
    //        } else if (classType == short.class) {
    //            return "S";
    //        } else if (classType == boolean.class) {
    //            return "Z";
    //        } else if (classType == void.class) {
    //            return "V";
    //        } else if (classType.isArray() == true) {
    //            return "[" + toAsmType(classType.getComponentType());
    //        } else {
    //            return classType.getName();
    //        }
    //    }
    //    private static Map<String, Class<?>> loadClassCache = new java.util.concurrent.ConcurrentHashMap<String, Class<?>>();
    //    /**将请求参数转换为对象。*/
    //    public static Object toParameter(String serializeType, byte[] dataBytes, SerializeFactory serializeFactory) throws Throwable {
    //        SerializeCoder coder = serializeFactory.getSerializeCoder(serializeType);
    //        return coder.decode(dataBytes);
    //    }
    //    //
    //    /**将{@link RequestMsg}转换为{@link RequestSocketBlock}消息。*/
    //    public static RequestSocketBlock requestToBlock(RequestMsg msg) {
    //        RequestSocketBlock socketMsg = new RequestSocketBlock();
    //        //1.基本信息
    //        byte version = (byte) (RSFConstants.RSF_Request | msg.getVersion());
    //        socketMsg.setVersion(version);//协议版本
    //        socketMsg.setRequestID(msg.getRequestID());//请求ID
    //        socketMsg.setServiceName(pushString(socketMsg, msg.getServiceName()));//服务名
    //        socketMsg.setServiceVersion(pushString(socketMsg, msg.getServiceVersion()));//服务版本
    //        socketMsg.setServiceGroup(pushString(socketMsg, msg.getServiceGroup()));//服务分组
    //        socketMsg.setTargetMethod(pushString(socketMsg, msg.getTargetMethod()));//远程服务方法
    //        socketMsg.setSerializeType(pushString(socketMsg, msg.getSerializeType()));//序列化策略
    //        socketMsg.setClientTimeout(msg.getClientTimeout());//远程客户端超时时间
    //        //2.调用参数
    //        int pCount = msg.getParameterCount();
    //        for (int i = 0; i < pCount; i++) {
    //            String pType = msg.getParameterType(i);
    //            byte[] pData = msg.getParameterValue(i);
    //            socketMsg.addParameter(pushString(socketMsg, pType), socketMsg.pushData(pData));
    //        }
    //        //3.Opt参数
    //        String[] optKeys = msg.getOptionKeys();
    //        for (int i = 0; i < optKeys.length; i++) {
    //            socketMsg.addOption(//
    //                    pushString(socketMsg, optKeys[i]), pushString(socketMsg, msg.getOption(optKeys[i])));
    //        }
    //        return socketMsg;
    //    };
    //    /**将{@link ResponseMsg}转换为{@link ResponseSocketBlock}消息。*/
    //    public static ResponseSocketBlock responseToBlock(ResponseMsg msg) {
    //        ResponseSocketBlock socketMsg = new ResponseSocketBlock();
    //        //1.基本信息
    //        byte version = ProtocolUtils.finalVersionForResponse(msg.getVersion());
    //        socketMsg.setVersion(version);//协议版本
    //        socketMsg.setRequestID(msg.getRequestID());//请求ID
    //        socketMsg.setStatus(msg.getStatus());//响应状态
    //        socketMsg.setSerializeType(pushString(socketMsg, msg.getSerializeType()));//序列化策略
    //        socketMsg.setReturnType(pushString(socketMsg, msg.getReturnType()));//返回类型
    //        socketMsg.setReturnData(socketMsg.pushData(msg.getReturnData()));//返回值
    //        //2.Opt参数
    //        String[] optKeys = msg.getOptionKeys();
    //        for (int i = 0; i < optKeys.length; i++) {
    //            socketMsg.addOption(pushString(socketMsg, optKeys[i]), pushString(socketMsg, msg.getOption(optKeys[i])));
    //        }
    //        return socketMsg;
    //    };
    //    private static short pushString(PoolSocketBlock socketMessage, String attrData) {
    //        return socketMessage.pushData(attrData.getBytes());
    //    }
    //    //
    //    //
    //    //
    //    /**将{@link RequestSocketBlock}转换为{@link RequestMsg}消息。*/
    //    public static RequestMsg requestToMessage(RequestSocketBlock block) {
    //        //1.基本参数
    //        RequestMsg reqMetaData = new RequestMsg();
    //        reqMetaData.setVersion(block.getVersion());//协议版本
    //        reqMetaData.setRequestID(block.getRequestID());//请求ID
    //        reqMetaData.setServiceName(getString(block, block.getServiceName()));//远程服务名
    //        reqMetaData.setServiceGroup(getString(block, block.getServiceGroup()));//远程服务分组
    //        reqMetaData.setServiceVersion(getString(block, block.getServiceVersion()));//远程服务版本
    //        reqMetaData.setTargetMethod(getString(block, block.getTargetMethod()));//远程服务方法名
    //        reqMetaData.setSerializeType(getString(block, block.getSerializeType()));//序列化策略
    //        reqMetaData.setClientTimeout(block.getClientTimeout());//远程客户端超时时间
    //        //2.调用参数
    //        short[] pTypes = block.getParameterTypes();
    //        short[] pValues = block.getParameterValues();
    //        for (int i = 0; i < pTypes.length; i++) {
    //            String paramType = getString(block, pTypes[i]);
    //            byte[] rawData = block.readPool(pValues[i]);
    //            //
    //            reqMetaData.addParameter(paramType, rawData);
    //        }
    //        //3.Opt参数
    //        short[] oTypes = block.getOptionKeys();
    //        short[] oValues = block.getOptionValues();
    //        for (int i = 0; i < oTypes.length; i++) {
    //            String optKey = getString(block, oTypes[i]);
    //            String optVar = getString(block, oValues[i]);
    //            //
    //            reqMetaData.addOption(optKey, optVar);
    //        }
    //        return reqMetaData;
    //    };
    //    /**将{@link ResponseSocketBlock}转换为{@link ResponseMsg }消息。*/
    //    public static ResponseMsg responseToMessage(ResponseSocketBlock block) {
    //        //1.基本参数
    //        ResponseMsg resMetaData = new ResponseMsg();
    //        resMetaData.setVersion(block.getVersion());//协议版本
    //        resMetaData.setRequestID(block.getRequestID());//请求ID
    //        resMetaData.setStatus(block.getStatus());//响应状态
    //        resMetaData.setSerializeType(getString(block, block.getSerializeType()));//序列化策略
    //        resMetaData.setReturnType(getString(block, block.getReturnType()));//返回类型
    //        resMetaData.setReturnData(block.readPool(block.getReturnData()));//返回数据
    //        //2.Opt参数
    //        short[] oTypes = block.getOptionKeys();
    //        short[] oValues = block.getOptionValues();
    //        for (int i = 0; i < oTypes.length; i++) {
    //            String optKey = getString(block, oTypes[i]);
    //            String optVar = getString(block, oValues[i]);
    //            //
    //            resMetaData.addOption(optKey, optVar);
    //        }
    //        return resMetaData;
    //    };
    //    private static String getString(PoolSocketBlock socketMessage, short attrIndex) {
    //        byte[] byteDatas = socketMessage.readPool(attrIndex);
    //        return (byteDatas == null) ? null : new String(byteDatas);
    //    }
    //
    //
    //
}