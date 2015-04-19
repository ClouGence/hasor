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
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.rsf.constants.RSFConstants;
import net.hasor.rsf.protocol.codec.Protocol;
import net.hasor.rsf.protocol.codec.RpcRequestProtocol;
import net.hasor.rsf.protocol.codec.RpcResponseProtocol;
import net.hasor.rsf.protocol.protocol.PoolSocketBlock;
import net.hasor.rsf.protocol.protocol.RequestSocketBlock;
import net.hasor.rsf.protocol.protocol.ResponseSocketBlock;
/**
 * Protocol Interface,for custom network protocol
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class ProtocolUtils {
    private static Protocol<RequestSocketBlock>[]  reqProtocolPool = new Protocol[63];
    private static Protocol<ResponseSocketBlock>[] resProtocolPool = new Protocol[63];
    //
    static {
        reqProtocolPool[0] = new RpcRequestProtocol();
        resProtocolPool[0] = new RpcResponseProtocol();
    }
    /**获取协议版本。*/
    private static byte version(byte version) {
        return (byte) (0x3F & version);
    }
    //
    public static Protocol<RequestSocketBlock> requestProtocol(byte ver) {
        return reqProtocolPool[version(ver) - 1];
    }
    public static Protocol<ResponseSocketBlock> responseProtocol(byte ver) {
        return resProtocolPool[version(ver) - 1];
    }
    //
    //
    //
    /**判断 response 是否为一个ACK包。*/
    public static boolean isACK(ResponseSocketBlock socketMessage) {
        return socketMessage.getStatus() == ProtocolStatus.Accepted;
    }
    /**是否为Request消息。*/
    public static boolean isRequest(byte version) {
        return (0xC1 | version) == version;
    }
    /**是否为Response消息。*/
    public static boolean isResponse(byte version) {
        return (0x81 | version) == version;
    }
    /**获取协议版本。*/
    public static byte getVersion(byte version) {
        return (byte) (0x3F & version);
    }
    /**生成 request 的 version 信息。*/
    public static byte finalVersionForRequest(byte version) {
        return (byte) (RSFConstants.RSF_Request | version);
    }
    /**生成 response 的 version 信息。*/
    public static byte finalVersionForResponse(byte version) {
        return (byte) (RSFConstants.RSF_Response | version);
    }
    /**生成指定状态的的响应包*/
    public static ResponseSocketBlock buildStatus(byte version, long requestID, short status, String serializeType, RsfOptionSet optMap) {
        ResponseSocketBlock block = new ResponseSocketBlock();
        block.setVersion(ProtocolUtils.finalVersionForResponse(version));
        block.setRequestID(requestID);
        block.setStatus(status);
        block.setSerializeType(pushString(block, serializeType));
        //
        if (optMap != null) {
            for (String optKey : optMap.getOptionKeys()) {
                short key = pushString(block, optKey);
                short val = pushString(block, optMap.getOption(optKey));
                block.addOption(key, val);
            }
        }
        //
        return block;
    }
    public static short pushString(PoolSocketBlock socketMessage, String attrData) {
        return socketMessage.pushData(attrData.getBytes());
    }
}