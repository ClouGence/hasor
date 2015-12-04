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
package net.hasor.rsf.protocol;
import static net.hasor.rsf.domain.RSFConstants.RSF_Packet_Request;
import static net.hasor.rsf.domain.RSFConstants.RSF_Packet_Response;
import net.hasor.rsf.RsfOptionSet;
import net.hasor.rsf.domain.RSFConstants;
import net.hasor.rsf.protocol.protocol.PoolBlock;
import net.hasor.rsf.protocol.protocol.RequestBlock;
import net.hasor.rsf.protocol.protocol.ResponseBlock;
import net.hasor.rsf.utils.ByteStringCachelUtils;
/**
 * Protocol Interface,for custom network protocol
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class ProtocolUtils {
    /**生成指定状态的的响应包*/
    public static ResponseBlock buildResponse(RequestBlock requestBlock, short status, RsfOptionSet optMap) {
        long reqID = requestBlock.getRequestID();//请求ID
        //
        ResponseBlock block = new ResponseBlock();
        block.setHead(RSFConstants.RSF_Response);
        block.setRequestID(reqID);
        block.setStatus(status);
        block.setSerializeType(pushString(block, null));
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
    /**生成指定状态的的响应包*/
    public static ResponseBlock buildRequest(RequestBlock requestBlock, short status, RsfOptionSet optMap) {
        long reqID = requestBlock.getRequestID();//请求ID
        //
        ResponseBlock block = new ResponseBlock();
        block.setHead(RSFConstants.RSF_Response);
        block.setRequestID(reqID);
        block.setStatus(status);
        block.setSerializeType(pushString(block, null));
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
    //
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
    public static ResponseBlock buildStatus(RequestBlock requestBlock, short status, RsfOptionSet optMap) {
        long reqID = requestBlock.getRequestID();//请求ID
        //
        ResponseBlock block = new ResponseBlock();
        block.setHead(RSFConstants.RSF_Response);
        block.setRequestID(reqID);
        block.setStatus(status);
        block.setSerializeType(pushString(block, null));
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
    public static short pushString(PoolBlock socketMessage, String attrData) {
        if (attrData != null) {
            return socketMessage.pushData(ByteStringCachelUtils.fromCache(attrData));
        } else {
            return socketMessage.pushData(null);
        }
    }
}