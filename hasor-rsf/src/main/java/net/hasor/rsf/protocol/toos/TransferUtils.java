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
package net.hasor.rsf.protocol.toos;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.general.RSFConstants;
import net.hasor.rsf.metadata.RequestMetaData;
import net.hasor.rsf.metadata.ResponseMetaData;
import net.hasor.rsf.protocol.message.RSFSocketMessage;
import net.hasor.rsf.protocol.message.RequestSocketMessage;
import net.hasor.rsf.protocol.message.ResponseSocketMessage;
/**
 * 负责{@link RequestMetaData}、{@link ResponseMetaData}到{@link RequestSocketMessage}、{@link ResponseSocketMessage}的类型转换。
 * @version : 2014年11月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class TransferUtils {
    /**将{@link RequestMetaData}转换为{@link RequestSocketMessage}消息。*/
    public static RequestSocketMessage requestTransferToSocket(RequestMetaData metaData) {
        RequestSocketMessage socketMsg = new RequestSocketMessage();
        //1.基本信息
        byte version = (byte) (RSFConstants.RSF_Request | metaData.getVersion());
        socketMsg.setVersion(version);//协议版本
        socketMsg.setRequestID(metaData.getRequestID());//请求ID
        socketMsg.setServiceName(pushString(socketMsg, metaData.getServiceName()));//服务名
        socketMsg.setServiceVersion(pushString(socketMsg, metaData.getServiceVersion()));//服务版本
        socketMsg.setServiceGroup(pushString(socketMsg, metaData.getServiceGroup()));//服务分组
        socketMsg.setTargetMethod(pushString(socketMsg, metaData.getTargetMethod()));//远程服务方法
        socketMsg.setSerializeType(pushString(socketMsg, metaData.getSerializeType()));//序列化策略
        //2.调用参数
        int pCount = metaData.getParameterCount();
        for (int i = 0; i < pCount; i++) {
            String pType = metaData.getParameterType(i);
            byte[] pData = metaData.getParameterValue(i);
            socketMsg.addParameter(pushString(socketMsg, pType), socketMsg.pushData(pData));
        }
        //3.Opt参数
        String[] optKeys = metaData.getOptionKeys();
        for (int i = 0; i < optKeys.length; i++) {
            socketMsg.addOption(//
                    pushString(socketMsg, optKeys[i]), pushString(socketMsg, metaData.getOption(optKeys[i])));
        }
        return socketMsg;
    };
    /**将{@link ResponseMetaData}转换为{@link ResponseSocketMessage}消息。*/
    public static ResponseSocketMessage responseTransferToSocket(ResponseMetaData metaData) {
        ResponseSocketMessage socketMsg = new ResponseSocketMessage();
        //1.基本信息
        byte version = (byte) (RSFConstants.RSF_Response | metaData.getVersion());
        socketMsg.setVersion(version);//协议版本
        socketMsg.setRequestID(metaData.getRequestID());//请求ID
        socketMsg.setStatus(metaData.getStatus().shortValue());//响应状态
        socketMsg.setSerializeType(pushString(socketMsg, metaData.getSerializeType()));//序列化策略
        socketMsg.setReturnType(pushString(socketMsg, metaData.getReturnType()));//返回类型
        socketMsg.setReturnData(socketMsg.pushData(metaData.getReturnData()));//返回值
        //2.Opt参数
        String[] optKeys = metaData.getOptionKeys();
        for (int i = 0; i < optKeys.length; i++) {
            socketMsg.addOption(//
                    pushString(socketMsg, optKeys[i]), pushString(socketMsg, metaData.getOption(optKeys[i])));
        }
        return socketMsg;
    };
    private static short pushString(RSFSocketMessage socketMessage, String attrData) {
        if (attrData == null)
            return socketMessage.pushData(null);
        else
            return socketMessage.pushData(attrData.getBytes());
    }
    //
    //
    //
    /**将{@link RequestSocketMessage }转换为{@link RequestMetaData}消息。*/
    public static RequestMetaData requestTransferToMetaData(RequestSocketMessage reqSocket) {
        //1.基本参数
        RequestMetaData reqMetaData = new RequestMetaData();
        reqMetaData.setVersion(reqSocket.getVersion());//协议版本
        reqMetaData.setRequestID(reqSocket.getRequestID());//请求ID
        reqMetaData.setServiceName(getString(reqSocket, reqSocket.getServiceName()));//远程服务名
        reqMetaData.setServiceGroup(getString(reqSocket, reqSocket.getServiceGroup()));//远程服务分组
        reqMetaData.setServiceVersion(getString(reqSocket, reqSocket.getServiceVersion()));//远程服务版本
        reqMetaData.setTargetMethod(getString(reqSocket, reqSocket.getTargetMethod()));//远程服务方法名
        reqMetaData.setSerializeType(getString(reqSocket, reqSocket.getSerializeType()));//序列化策略
        //2.调用参数
        short[] pTypes = reqSocket.getParameterTypes();
        short[] pValues = reqSocket.getParameterValues();
        for (int i = 0; i < pTypes.length; i++) {
            String paramType = getString(reqSocket, pTypes[i]);
            byte[] rawData = reqSocket.readPool(pValues[i]);
            //
            reqMetaData.addParameter(paramType, rawData);
        }
        //3.Opt参数
        short[] oTypes = reqSocket.getOptionKeys();
        short[] oValues = reqSocket.getOptionValues();
        for (int i = 0; i < oTypes.length; i++) {
            String optKey = getString(reqSocket, oTypes[i]);
            String optVar = getString(reqSocket, oValues[i]);
            //
            reqMetaData.addOption(optKey, optVar);
        }
        return reqMetaData;
    };
    /**将{@link ResponseSocketMessage}转换为{@link ResponseMetaData }消息。*/
    public static ResponseMetaData responseTransferToMetaData(ResponseSocketMessage resSocket) {
        //1.基本参数
        ResponseMetaData resMetaData = new ResponseMetaData();
        resMetaData.setVersion(resSocket.getVersion());//协议版本
        resMetaData.setRequestID(resSocket.getRequestID());//请求ID
        resMetaData.setStatus(ProtocolStatus.valueOf(resSocket.getStatus()));//响应状态
        resMetaData.setSerializeType(getString(resSocket, resSocket.getSerializeType()));//序列化策略
        resMetaData.setReturnType(getString(resSocket, resSocket.getSerializeType()));//返回类型
        resMetaData.setReturnData(resSocket.readPool(resSocket.getReturnData()));//返回数据
        //2.Opt参数
        short[] oTypes = resSocket.getOptionKeys();
        short[] oValues = resSocket.getOptionValues();
        for (int i = 0; i < oTypes.length; i++) {
            String optKey = getString(resSocket, oTypes[i]);
            String optVar = getString(resSocket, oValues[i]);
            //
            resMetaData.addOption(optKey, optVar);
        }
        return resMetaData;
    };
    private static String getString(RSFSocketMessage socketMessage, int attrIndex) {
        byte[] byteDatas = socketMessage.readPool(attrIndex);
        return (byteDatas == null) ? null : new String(byteDatas);
    }
}