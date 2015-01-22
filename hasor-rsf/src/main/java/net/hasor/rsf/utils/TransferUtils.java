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
package net.hasor.rsf.utils;
import net.hasor.rsf.RsfOptionSet;
import net.hasor.rsf.constants.RSFConstants;
import net.hasor.rsf.remoting.transport.protocol.block.BaseSocketBlock;
import net.hasor.rsf.remoting.transport.protocol.block.RequestSocketBlock;
import net.hasor.rsf.remoting.transport.protocol.block.ResponseSocketBlock;
import net.hasor.rsf.remoting.transport.protocol.message.RequestMsg;
import net.hasor.rsf.remoting.transport.protocol.message.ResponseMsg;
/**
 * 负责{@link RequestMsg}、{@link ResponseMsg}到{@link RequestSocketBlock}、{@link ResponseSocketBlock}的类型转换。
 * @version : 2014年11月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class TransferUtils {
    /**将{@link RequestMsg}转换为{@link RequestSocketBlock}消息。*/
    public static RequestSocketBlock requestToBlock(RequestMsg msg) {
        RequestSocketBlock socketMsg = new RequestSocketBlock();
        //1.基本信息
        byte version = (byte) (RSFConstants.RSF_Request | msg.getVersion());
        socketMsg.setVersion(version);//协议版本
        socketMsg.setRequestID(msg.getRequestID());//请求ID
        socketMsg.setServiceName(pushString(socketMsg, msg.getServiceName()));//服务名
        socketMsg.setServiceVersion(pushString(socketMsg, msg.getServiceVersion()));//服务版本
        socketMsg.setServiceGroup(pushString(socketMsg, msg.getServiceGroup()));//服务分组
        socketMsg.setTargetMethod(pushString(socketMsg, msg.getTargetMethod()));//远程服务方法
        socketMsg.setSerializeType(pushString(socketMsg, msg.getSerializeType()));//序列化策略
        socketMsg.setClientTimeout(msg.getClientTimeout());//远程客户端超时时间
        //2.调用参数
        int pCount = msg.getParameterCount();
        for (int i = 0; i < pCount; i++) {
            String pType = msg.getParameterType(i);
            byte[] pData = msg.getParameterValue(i);
            socketMsg.addParameter(pushString(socketMsg, pType), socketMsg.pushData(pData));
        }
        //3.Opt参数
        String[] optKeys = msg.getOptionKeys();
        for (int i = 0; i < optKeys.length; i++) {
            socketMsg.addOption(//
                    pushString(socketMsg, optKeys[i]), pushString(socketMsg, msg.getOption(optKeys[i])));
        }
        return socketMsg;
    };
    /**将{@link ResponseMsg}转换为{@link ResponseSocketBlock}消息。*/
    public static ResponseSocketBlock responseToBlock(ResponseMsg msg) {
        ResponseSocketBlock socketMsg = new ResponseSocketBlock();
        //1.基本信息
        byte version = ProtocolUtils.finalVersionForResponse(msg.getVersion());
        socketMsg.setVersion(version);//协议版本
        socketMsg.setRequestID(msg.getRequestID());//请求ID
        socketMsg.setStatus(msg.getStatus());//响应状态
        socketMsg.setSerializeType(pushString(socketMsg, msg.getSerializeType()));//序列化策略
        socketMsg.setReturnType(pushString(socketMsg, msg.getReturnType()));//返回类型
        socketMsg.setReturnData(socketMsg.pushData(msg.getReturnData()));//返回值
        //2.Opt参数
        String[] optKeys = msg.getOptionKeys();
        for (int i = 0; i < optKeys.length; i++) {
            socketMsg.addOption(//
                    pushString(socketMsg, optKeys[i]), pushString(socketMsg, msg.getOption(optKeys[i])));
        }
        return socketMsg;
    };
    private static short pushString(BaseSocketBlock socketMessage, String attrData) {
        return socketMessage.pushData(attrData.getBytes());
    }
    //
    //
    //
    /**将{@link RequestSocketBlock}转换为{@link RequestMsg}消息。*/
    public static RequestMsg requestToMessage(RequestSocketBlock block) {
        //1.基本参数
        RequestMsg reqMetaData = new RequestMsg();
        reqMetaData.setVersion(block.getVersion());//协议版本
        reqMetaData.setRequestID(block.getRequestID());//请求ID
        reqMetaData.setServiceName(getString(block, block.getServiceName()));//远程服务名
        reqMetaData.setServiceGroup(getString(block, block.getServiceGroup()));//远程服务分组
        reqMetaData.setServiceVersion(getString(block, block.getServiceVersion()));//远程服务版本
        reqMetaData.setTargetMethod(getString(block, block.getTargetMethod()));//远程服务方法名
        reqMetaData.setSerializeType(getString(block, block.getSerializeType()));//序列化策略
        reqMetaData.setClientTimeout(block.getClientTimeout());//远程客户端超时时间
        //2.调用参数
        short[] pTypes = block.getParameterTypes();
        short[] pValues = block.getParameterValues();
        for (int i = 0; i < pTypes.length; i++) {
            String paramType = getString(block, pTypes[i]);
            byte[] rawData = block.readPool(pValues[i]);
            //
            reqMetaData.addParameter(paramType, rawData);
        }
        //3.Opt参数
        short[] oTypes = block.getOptionKeys();
        short[] oValues = block.getOptionValues();
        for (int i = 0; i < oTypes.length; i++) {
            String optKey = getString(block, oTypes[i]);
            String optVar = getString(block, oValues[i]);
            //
            reqMetaData.addOption(optKey, optVar);
        }
        return reqMetaData;
    };
    /**将{@link ResponseSocketBlock}转换为{@link ResponseMsg }消息。*/
    public static ResponseMsg responseToMessage(ResponseSocketBlock block) {
        //1.基本参数
        ResponseMsg resMetaData = new ResponseMsg();
        resMetaData.setVersion(block.getVersion());//协议版本
        resMetaData.setRequestID(block.getRequestID());//请求ID
        resMetaData.setStatus(block.getStatus());//响应状态
        resMetaData.setSerializeType(getString(block, block.getSerializeType()));//序列化策略
        resMetaData.setReturnType(getString(block, block.getReturnType()));//返回类型
        resMetaData.setReturnData(block.readPool(block.getReturnData()));//返回数据
        //2.Opt参数
        short[] oTypes = block.getOptionKeys();
        short[] oValues = block.getOptionValues();
        for (int i = 0; i < oTypes.length; i++) {
            String optKey = getString(block, oTypes[i]);
            String optVar = getString(block, oValues[i]);
            //
            resMetaData.addOption(optKey, optVar);
        }
        return resMetaData;
    };
    private static String getString(BaseSocketBlock socketMessage, int attrIndex) {
        byte[] byteDatas = socketMessage.readPool(attrIndex);
        return (byteDatas == null) ? null : new String(byteDatas);
    }
    //
    //
    //
    /**生成指定状态的的响应包*/
    public static ResponseMsg buildStatus(byte version, long requestID, short status, String serializeType, RsfOptionSet optMap) {
        ResponseMsg ack = new ResponseMsg();
        ack.setVersion(ProtocolUtils.finalVersionForResponse(version));
        ack.setRequestID(requestID);
        ack.setStatus(status);
        ack.setSerializeType(serializeType);
        //
        if (optMap != null) {
            for (String optKey : optMap.getOptionKeys())
                ack.addOption(optKey, optMap.getOption(optKey));
        }
        //
        return ack;
    }
}