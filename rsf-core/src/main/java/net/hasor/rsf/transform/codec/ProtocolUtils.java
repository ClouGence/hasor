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
package net.hasor.rsf.transform.codec;
import java.io.IOException;
import java.util.List;
import io.netty.buffer.ByteBuf;
import net.hasor.rsf.domain.RSFConstants;
import net.hasor.rsf.transform.protocol.PoolBlock;
import net.hasor.rsf.transform.protocol.RequestBlock;
import net.hasor.rsf.transform.protocol.RequestInfo;
import net.hasor.rsf.transform.protocol.ResponseBlock;
import net.hasor.rsf.transform.protocol.ResponseInfo;
/**
 * Protocol Interface,for custom network protocol
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class ProtocolUtils {
    private static Protocol<RequestBlock>[]  reqProtocolPool = new Protocol[16];
    private static Protocol<ResponseBlock>[] resProtocolPool = new Protocol[16];
    //
    static {
        reqProtocolPool[1] = new RpcRequestProtocol();
        resProtocolPool[1] = new RpcResponseProtocol();
    }
    //
    /**
     * 从RSF头中获取协议版本。
     * @param rsfHead RSF数据包头信息。
     */
    public static byte getVersion(byte rsfHead) {
        return (byte) (rsfHead & 0x0F);
    }
    /*将字节数据放入，PoolBlock*/
    private static short pushBytes(PoolBlock socketMessage, byte[] attrData) {
        if (attrData != null) {
            return socketMessage.pushData(attrData);
        } else {
            return socketMessage.pushData(null);
        }
    }
    /*将字符串数据放入，PoolBlock*/
    private static short pushString(PoolBlock socketMessage, String attrData) {
        if (attrData != null) {
            return socketMessage.pushData(ByteStringCachelUtils.fromCache(attrData));
        } else {
            return socketMessage.pushData(null);
        }
    }
    //
    //
    //
    /**根据RSF头数据获取对应的Request编码解码器。*/
    public static Protocol<RequestBlock> getRquestProtocol(byte rsfHead) {
        return reqProtocolPool[getVersion(rsfHead)];
    }
    /**根据RSF头中描述的协议版本，从二进制数据流中创建{@link RequestInfo}对象。
     * @see #buildRequestInfo(RequestBlock)*/
    public static RequestInfo buildRequestInfo(byte rsfHead, ByteBuf dataBuf) throws IOException {
        Protocol<RequestBlock> protocol = getRquestProtocol(rsfHead);
        RequestBlock block = protocol.decode(dataBuf);
        return buildRequestInfo(block);
    }
    /**解析RSF协议数据包{@link RequestBlock}，将其转换为{@link RequestInfo}。*/
    public static RequestInfo buildRequestInfo(RequestBlock rsfBlock) {
        RequestInfo info = new RequestInfo();
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
        //3.Request
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
        for (int i = 0; i < paramDatas.length; i++) {
            int paramItem = paramDatas[i];
            short paramKey = (short) (paramItem >>> 16);
            short paramVal = (short) (paramItem & PoolBlock.PoolMaxSize);
            byte[] keyData = rsfBlock.readPool(paramKey);
            byte[] valData = rsfBlock.readPool(paramVal);
            //
            String paramType = ByteStringCachelUtils.fromCache(keyData);
            info.addParameter(paramType, valData);
        }
        //
        return info;
    }
    /**编码{@link RequestInfo}为协议数据包{@link RequestBlock}。*/
    public static RequestBlock buildRequestBlock(RequestInfo info) {
        RequestBlock block = new RequestBlock();
        //
        //1.基本信息
        block.setHead(RSFConstants.RSF_Request);
        block.setRequestID(info.getRequestID());//请求ID
        block.setServiceGroup(ProtocolUtils.pushString(block, info.getServiceGroup()));
        block.setServiceName(ProtocolUtils.pushString(block, info.getServiceName()));
        block.setServiceVersion(ProtocolUtils.pushString(block, info.getServiceVersion()));
        block.setTargetMethod(ProtocolUtils.pushString(block, info.getTargetMethod()));
        block.setSerializeType(ProtocolUtils.pushString(block, info.getSerializeType()));
        block.setClientTimeout(info.getClientTimeout());
        //
        //2.params
        List<String> pTypes = info.getParameterTypes();
        List<byte[]> pValues = info.getParameterValues();
        for (int i = 0; i < pTypes.size(); i++) {
            String typeKey = pTypes.get(i);
            byte[] valKey = pValues.get(i);
            //
            short paramType = ProtocolUtils.pushString(block, typeKey);
            short paramData = ProtocolUtils.pushBytes(block, valKey);
            block.addParameter(paramType, paramData);
        }
        //
        //3.Opt参数
        String[] optKeys = info.getOptionKeys();
        for (int i = 0; i < optKeys.length; i++) {
            short optKey = ProtocolUtils.pushString(block, optKeys[i]);
            short optVal = ProtocolUtils.pushString(block, info.getOption(optKeys[i]));
            block.addOption(optKey, optVal);
        }
        //
        return block;
    }
    /**
     * 自动编码{@link RequestInfo}，并将编码的协议数据写入到{@link ByteBuf}。
     * @see #wirteRequestBlock(byte, RequestBlock, ByteBuf)
     * @param rsfHead 使用的协议版本号（0～15）或是携带了协议信息的RSF数据传输协议头。
     * @param info 需要编码的request。
     * @param dataBuf 数据输出到这里。
     */
    public static void wirteRequestInfo(byte rsfHead, RequestInfo info, ByteBuf dataBuf) throws IOException {
        RequestBlock block = buildRequestBlock(info);
        wirteRequestBlock(rsfHead, block, dataBuf);
    }
    /**
     * 将协议数据{@link RequestBlock}，写入到{@link ByteBuf}。
     * @param rsfHead 使用的协议版本号（0～15）或是携带了协议信息的RSF数据传输协议头。
     * @param block 协议数据包。
     * @param dataBuf 数据输出到这里。
     */
    public static void wirteRequestBlock(byte rsfHead, RequestBlock block, ByteBuf dataBuf) throws IOException {
        Protocol<RequestBlock> protocol = getRquestProtocol(rsfHead);
        protocol.encode(block, dataBuf);
    }
    //
    //
    //
    /**根据RSF头数据获取对应的Response编码解码器。*/
    public static Protocol<ResponseBlock> getResponseProtocol(byte rsfHead) {
        return resProtocolPool[getVersion(rsfHead)];
    }
    /**根据RSF头中描述的协议版本，从二进制数据流中创建{@link ResponseInfo}对象。
     * @see #buildResponseInfo(ResponseBlock)*/
    public static ResponseInfo buildResponseInfo(byte rsfHead, ByteBuf dataBuf) throws IOException {
        Protocol<ResponseBlock> protocol = getResponseProtocol(rsfHead);
        ResponseBlock block = protocol.decode(dataBuf);
        return buildResponseInfo(block);
    }
    /**解析RSF协议数据包{@link ResponseBlock}，将其转换为{@link ResponseInfo}。*/
    public static ResponseInfo buildResponseInfo(ResponseBlock rsfBlock) {
        ResponseInfo info = new ResponseInfo();
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
        byte[] returnData = rsfBlock.readPool(rsfBlock.getReturnData());
        info.setReturnData(returnData);
        return info;
    }
    /**编码{@link ResponseInfo}为协议数据包{@link ResponseBlock}。*/
    public static ResponseBlock buildResponseBlock(ResponseInfo info) {
        ResponseBlock block = new ResponseBlock();
        //
        //1.基本信息
        block.setHead(RSFConstants.RSF_Response);
        block.setRequestID(info.getRequestID());//请求ID
        block.setSerializeType(ProtocolUtils.pushString(block, info.getSerializeType()));//序列化策略
        //
        //2.returnData
        block.setReturnData(block.pushData(info.getReturnData()));
        block.setStatus(info.getStatus());//响应状态
        //
        //3.Opt参数
        String[] optKeys = info.getOptionKeys();
        for (int i = 0; i < optKeys.length; i++) {
            short optKey = ProtocolUtils.pushString(block, optKeys[i]);
            short optVal = ProtocolUtils.pushString(block, info.getOption(optKeys[i]));
            block.addOption(optKey, optVal);
        }
        //
        return block;
    }
    /**
     * 自动编码{@link ResponseInfo}，并将编码的协议数据写入到{@link ByteBuf}。
     * @see #wirteResponseBlock(byte, ResponseBlock, ByteBuf)
     * @param rsfHead 使用的协议版本号（0～15）或是携带了协议信息的RSF数据传输协议头。
     * @param info 需要编码的request。
     * @param dataBuf 数据输出到这里。
     */
    public static void wirteResponseInfo(byte rsfHead, ResponseInfo info, ByteBuf dataBuf) throws IOException {
        ResponseBlock block = buildResponseBlock(info);
        wirteResponseBlock(rsfHead, block, dataBuf);
    }
    /**
     * 将协议数据{@link ResponseBlock}，写入到{@link ByteBuf}。
     * @param rsfHead 使用的协议版本号（0～15）或是携带了协议信息的RSF数据传输协议头。
     * @param block 协议数据包。
     * @param dataBuf 数据输出到这里。
     */
    public static void wirteResponseBlock(byte rsfHead, ResponseBlock block, ByteBuf dataBuf) throws IOException {
        Protocol<ResponseBlock> protocol = getResponseProtocol(rsfHead);
        protocol.encode(block, dataBuf);
    }
}