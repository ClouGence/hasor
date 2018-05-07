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
package net.hasor.rsf.protocol.rsf.v1;
import io.netty.buffer.ByteBuf;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.SerializeCoder;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;
import net.hasor.rsf.protocol.rsf.CodecAdapter;
import net.hasor.rsf.protocol.rsf.Protocol;
import net.hasor.rsf.protocol.rsf.ProtocolConstants;
import net.hasor.rsf.utils.ByteStringCachelUtils;
import net.hasor.utils.ClassUtils;
import net.hasor.utils.StringUtils;

import java.io.IOException;
import java.util.List;
/**
 * Protocol Interface,for custom network protocol
 * @version : 2014年11月4日
 * @author 赵永春(zyc @ hasor.net)
 */
public class CodecAdapterForV1 implements CodecAdapter, ProtocolConstants {
    private RsfEnvironment rsfEnvironment = null;
    private ClassLoader    classLoader    = null;
    public CodecAdapterForV1(RsfEnvironment rsfEnvironment, ClassLoader classLoader) {
        this.rsfEnvironment = rsfEnvironment;
        this.classLoader = classLoader;
    }
    //
    @Override
    public RequestBlock buildRequestBlock(RequestInfo info) throws IOException {
        RequestBlock block = new RequestBlock();
        if (info.isMessage()) {
            block.setHead(RSF_MessageRequest);
        } else {
            block.setHead(RSF_InvokerRequest);
        }
        //
        //1.基本信息
        block.setRequestID(info.getRequestID());//请求ID
        block.setFlags(info.getFlags());
        block.setServiceGroup(pushString(block, info.getServiceGroup()));
        block.setServiceName(pushString(block, info.getServiceName()));
        block.setServiceVersion(pushString(block, info.getServiceVersion()));
        block.setTargetMethod(pushString(block, info.getTargetMethod()));
        block.setSerializeType(pushString(block, info.getSerializeType()));
        block.setClientTimeout(info.getClientTimeout());
        //
        //2.params
        List<String> pTypes = info.getParameterTypes();
        List<Object> pValues = info.getParameterValues();
        if ((pTypes != null && !pTypes.isEmpty()) && (pValues != null && !pValues.isEmpty())) {
            SerializeCoder coder = this.rsfEnvironment.getSerializeCoder(info.getSerializeType());
            for (int i = 0; i < pTypes.size(); i++) {
                String typeKey = pTypes.get(i);
                Object value = pValues.get(i);
                byte[] valKey = (coder != null) ? coder.encode(value) : new byte[0];
                //
                short paramType = pushString(block, typeKey);
                short paramData = pushBytes(block, valKey);
                block.addParameter(paramType, paramData);
            }
        }
        //
        //3.Opt参数
        String[] optKeys = info.getOptionKeys();
        if (optKeys.length > 0) {
            for (int i = 0; i < optKeys.length; i++) {
                short optKey = pushString(block, optKeys[i]);
                short optVal = pushString(block, info.getOption(optKeys[i]));
                block.addOption(optKey, optVal);
            }
        }
        //
        return block;
    }
    @Override
    public ResponseBlock buildResponseBlock(ResponseInfo info) throws IOException {
        ResponseBlock block = new ResponseBlock();
        //
        //1.基本信息
        block.setHead(RSF_Response);
        block.setRequestID(info.getRequestID());//请求ID
        block.setSerializeType(pushString(block, info.getSerializeType()));//序列化策略
        //
        //2.returnData
        String returnType = info.getReturnType();
        SerializeCoder serializeCoder = this.rsfEnvironment.getSerializeCoder(info.getSerializeType());
        byte[] encode = (serializeCoder != null) ? serializeCoder.encode(info.getReturnData()) : new byte[0];
        block.setReturnData(block.pushData(encode));
        block.setReturnType(pushString(block, returnType));
        block.setReturnData(block.pushData(encode));
        block.setStatus(info.getStatus());//响应状态
        //
        //3.Opt参数
        String[] optKeys = info.getOptionKeys();
        for (String optKey1 : optKeys) {
            short optKey = pushString(block, optKey1);
            short optVal = pushString(block, info.getOption(optKey1));
            block.addOption(optKey, optVal);
        }
        //
        return block;
    }
    //
    /**将字节数据放入，PoolBlock*/
    private static short pushBytes(PoolBlock socketMessage, byte[] attrData) {
        if (attrData != null) {
            return socketMessage.pushData(attrData);
        } else {
            return socketMessage.pushData(null);
        }
    }
    /**将字符串数据放入，PoolBlock*/
    private static short pushString(PoolBlock socketMessage, String attrData) {
        if (attrData != null) {
            return socketMessage.pushData(ByteStringCachelUtils.fromCache(attrData));
        } else {
            return socketMessage.pushData(null);
        }
    }
    //
    //
    private Protocol<RequestBlock>  requestProtocol  = new RpcRequestProtocolV1();
    private Protocol<ResponseBlock> responseProtocol = new RpcResponseProtocolV1();
    @Override
    public void wirteRequestBlock(RequestBlock block, ByteBuf out) throws IOException {
        this.requestProtocol.encode(block, out);
    }
    @Override
    public RequestInfo readRequestInfo(ByteBuf frame) throws Throwable {
        RequestBlock rsfBlock = this.requestProtocol.decode(frame);
        RequestInfo info = new RequestInfo();
        try {
            //
            //1.基本数据
            info.setRequestID(rsfBlock.getRequestID());
            short serializeTypeInt = rsfBlock.getSerializeType();
            String serializeType = ByteStringCachelUtils.fromCache(rsfBlock.readPool(serializeTypeInt));
            info.setSerializeType(serializeType);
            //
            //2.Message
            if (rsfBlock.getHead() == RSF_InvokerRequest) {
                info.setMessage(false);
            }
            if (rsfBlock.getHead() == RSF_MessageRequest) {
                info.setMessage(true);
            }
            //
            //3.Opt参数
            int[] optionArray = rsfBlock.getOptions();
            if (optionArray.length > 0) {
                for (int optItem : optionArray) {
                    short optKey = (short) (optItem >>> 16);
                    short optVal = (short) (optItem & PoolBlock.PoolMaxSize);
                    String optKeyStr = ByteStringCachelUtils.fromCache(rsfBlock.readPool(optKey));
                    String optValStr = ByteStringCachelUtils.fromCache(rsfBlock.readPool(optVal));
                    info.addOption(optKeyStr, optValStr);
                }
            }
            //
            //4.Request
            String serviceGroup = ByteStringCachelUtils.fromCache(rsfBlock.readPool(rsfBlock.getServiceGroup()));
            String serviceName = ByteStringCachelUtils.fromCache(rsfBlock.readPool(rsfBlock.getServiceName()));
            String serviceVersion = ByteStringCachelUtils.fromCache(rsfBlock.readPool(rsfBlock.getServiceVersion()));
            String targetMethod = ByteStringCachelUtils.fromCache(rsfBlock.readPool(rsfBlock.getTargetMethod()));
            int clientTimeout = rsfBlock.getClientTimeout();
            info.setServiceGroup(serviceGroup);
            info.setServiceName(serviceName);
            info.setServiceVersion(serviceVersion);
            info.setTargetMethod(targetMethod);
            info.setClientTimeout(clientTimeout);
            //
            int[] paramDatas = rsfBlock.getParameters();
            SerializeCoder serializeCoder = this.rsfEnvironment.getSerializeCoder(serializeType);
            if (paramDatas.length > 0) {
                for (int i = 0; i < paramDatas.length; i++) {
                    int paramItem = paramDatas[i];
                    short paramKey = (short) (paramItem >>> 16);
                    short paramVal = (short) (paramItem & PoolBlock.PoolMaxSize);
                    byte[] keyData = rsfBlock.readPool(paramKey);
                    byte[] valData = rsfBlock.readPool(paramVal);
                    //
                    String paramType = ByteStringCachelUtils.fromCache(keyData);
                    Object paramObj = null;
                    if (serializeCoder != null && StringUtils.isNotBlank(paramType)) {
                        paramObj = serializeCoder.decode(valData, ClassUtils.getClass(this.classLoader, paramType, false));
                    }
                    info.addParameter(paramType, paramObj);
                }
            }
        } finally {
            if (rsfBlock != null) {
                rsfBlock.release();
            }
        }
        return info;
    }
    @Override
    public void wirteResponseBlock(ResponseBlock block, ByteBuf out) throws IOException {
        this.responseProtocol.encode(block, out);
    }
    @Override
    public ResponseInfo readResponseInfo(ByteBuf frame) throws Throwable {
        ResponseBlock rsfBlock = this.responseProtocol.decode(frame);
        ResponseInfo info = new ResponseInfo();
        try {
            //
            //1.基本数据
            info.setRequestID(rsfBlock.getRequestID());
            short serializeTypeInt = rsfBlock.getSerializeType();
            String serializeType = ByteStringCachelUtils.fromCache(rsfBlock.readPool(serializeTypeInt));
            info.setSerializeType(serializeType);
            //
            //2.Opt参数
            int[] optionArray = rsfBlock.getOptions();
            for (int optItem : optionArray) {
                short optKey = (short) (optItem >>> 16);
                short optVal = (short) (optItem & PoolBlock.PoolMaxSize);
                String optKeyStr = ByteStringCachelUtils.fromCache(rsfBlock.readPool(optKey));
                String optValStr = ByteStringCachelUtils.fromCache(rsfBlock.readPool(optVal));
                info.addOption(optKeyStr, optValStr);
            }
            //
            //3.Response
            info.setStatus(rsfBlock.getStatus());
            SerializeCoder serializeCoder = this.rsfEnvironment.getSerializeCoder(serializeType);
            String returnType = ByteStringCachelUtils.fromCache(rsfBlock.readPool(rsfBlock.getReturnType()));
            info.setReturnType(returnType);
            byte[] returnByte = rsfBlock.readPool(rsfBlock.getReturnData());
            Object returnData = null;
            if (serializeCoder != null && StringUtils.isNotBlank(returnType)) {
                returnData = serializeCoder.decode(returnByte, ClassUtils.getClass(this.classLoader, returnType, false));
            }
            info.setReturnData(returnData);
        } finally {
            if (rsfBlock != null) {
                rsfBlock.release();
            }
        }
        return info;
    }
}
