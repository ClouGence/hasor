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
import static net.hasor.rsf.constants.RSFConstants.RSF_Packet_Request;
import static net.hasor.rsf.constants.RSFConstants.RSF_Packet_Response;
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
    private static Protocol<RequestSocketBlock>[]  reqProtocolPool = new Protocol[16];
    private static Protocol<ResponseSocketBlock>[] resProtocolPool = new Protocol[16];
    //
    static {
        reqProtocolPool[1] = new RpcRequestProtocol();
        resProtocolPool[1] = new RpcResponseProtocol();
    }
    //
    public static Protocol<RequestSocketBlock> requestProtocol(byte rsfHead) {
        return reqProtocolPool[getVersion(rsfHead)];
    }
    public static Protocol<ResponseSocketBlock> responseProtocol(byte rsfHead) {
        return resProtocolPool[getVersion(rsfHead)];
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
        return (RSF_Packet_Request | version) == version;
    }
    /**是否为Response消息。*/
    public static boolean isResponse(byte version) {
        return (RSF_Packet_Response | version) == version;
    }
    /**获取协议版本。*/
    public static byte getVersion(byte rsfHead) {
        return (byte) (rsfHead & 0x0F);
    }
    /**生成指定状态的的响应包*/
    public static ResponseSocketBlock buildStatus(RequestSocketBlock requestBlock, short status, RsfOptionSet optMap) {
        long reqID = requestBlock.getRequestID();//请求ID
        String serializeType = "BlackHole";//序列化类型
        //
        ResponseSocketBlock block = new ResponseSocketBlock();
        block.setHead(RSFConstants.RSF_Response);
        block.setRequestID(reqID);
        block.setStatus(status);
        block.setSerializeType(pushString(block, serializeType));
        block.setReturnData(block.pushData(null));
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