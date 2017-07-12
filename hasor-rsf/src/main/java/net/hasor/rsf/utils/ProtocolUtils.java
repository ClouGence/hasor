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
package net.hasor.rsf.utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.hasor.rsf.*;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;
import net.hasor.rsf.domain.RsfRuntimeUtils;

import java.io.IOException;
/**
 *
 * @version : 2015年3月28日
 * @author 赵永春(zyc@hasor.net)
 */
public class ProtocolUtils {
    /**将{@link RsfRequest},转换为{@link RequestInfo}。*/
    public static RequestInfo buildRequestInfo(RsfEnvironment env, RsfRequest rsfRequest) throws IOException {
        RequestInfo info = new RequestInfo();
        RsfBindInfo<?> rsfBindInfo = rsfRequest.getBindInfo();
        String serializeType = rsfRequest.getSerializeType();
        SerializeCoder coder = env.getSerializeCoder(serializeType);
        //
        //1.基本信息
        info.setRequestID(rsfRequest.getRequestID());//请求ID
        info.setServiceGroup(rsfBindInfo.getBindGroup());//序列化策略
        info.setServiceName(rsfBindInfo.getBindName());//序列化策略
        info.setServiceVersion(rsfBindInfo.getBindVersion());//序列化策略
        info.setTargetMethod(rsfRequest.getMethod().getName());//序列化策略
        info.setSerializeType(serializeType);//序列化策略
        info.setClientTimeout(rsfRequest.getTimeout());
        info.setMessage(rsfRequest.isMessage());
        //
        //2.params
        Class<?>[] pTypes = rsfRequest.getParameterTypes();
        Object[] pObjects = rsfRequest.getParameterObject();
        pTypes = (pTypes == null) ? new Class[0] : pTypes;
        pObjects = (pObjects == null) ? new Object[0] : pObjects;
        for (int i = 0; i < pTypes.length; i++) {
            String typeByte = RsfRuntimeUtils.toAsmType(pTypes[i]);
            byte[] paramByte = coder.encode(pObjects[i]);
            info.addParameter(typeByte, paramByte, pObjects[i]);
        }
        //
        //3.Opt参数
        info.addOptionMap(rsfRequest);
        //
        return info;
    }
    public static ResponseInfo buildResponseStatus(RsfEnvironment env, long requestID, short status, String errorInfo) {
        ResponseInfo info = new ResponseInfo();
        info.setRequestID(requestID);
        info.setStatus(status);
        if (StringUtils.isNotBlank(errorInfo)) {
            info.addOption("message", errorInfo);
        }
        return info;
    }
    /**将{@link RsfResponse},转换为{@link ResponseInfo}。*/
    public static ResponseInfo buildResponseInfo(RsfEnvironment env, RsfResponse rsfResponse) throws IOException {
        ResponseInfo info = new ResponseInfo();
        String serializeType = rsfResponse.getSerializeType();
        SerializeCoder coder = env.getSerializeCoder(serializeType);
        byte[] returnData = coder.encode(rsfResponse.getData());
        info.setRequestID(rsfResponse.getRequestID());
        info.setStatus(rsfResponse.getStatus());
        info.setSerializeType(serializeType);
        info.setReturnData(returnData);
        info.addOptionMap(rsfResponse);
        //
        return info;
    }
    /**创建ByteBuf*/
    public static ByteBuf newByteBuf() {
        return ByteBufAllocator.DEFAULT.heapBuffer();
    }
}