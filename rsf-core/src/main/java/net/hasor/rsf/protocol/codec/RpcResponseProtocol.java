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
package net.hasor.rsf.protocol.codec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.io.IOException;
import net.hasor.rsf.constants.RSFConstants;
import net.hasor.rsf.protocol.protocol.ResponseSocketBlock;
/**
 * Protocol Interface,for custom network protocol
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class RpcResponseProtocol implements Protocol<ResponseSocketBlock> {
    /**encode Message to byte & write to network framework*/
    public void encode(ResponseSocketBlock resMsg, ByteBuf buf) throws IOException {
        //
        //* --------------------------------------------------------bytes =13
        //* byte[1]  version                              RSF版本(0x81)
        buf.writeByte(RSFConstants.RSF_Response);
        //* byte[8]  requestID                            请求ID
        buf.writeLong(resMsg.getRequestID());
        //* byte[1]  keepData                             保留区
        buf.writeByte(0);
        //* byte[3]  contentLength                        内容大小(max = 16MB)
        ByteBuf responseBody = this.encodeResponse(resMsg);
        int bodyLength = responseBody.readableBytes();
        bodyLength = (bodyLength << 8) >>> 8;//左移8未，在无符号右移8位。形成最大16777215字节的限制。
        buf.writeMedium(bodyLength);
        //
        buf.writeBytes(responseBody);
        //
    }
    //
    private ByteBuf encodeResponse(ResponseSocketBlock resMsg) {
        ByteBuf bodyBuf = ByteBufAllocator.DEFAULT.heapBuffer();
        //
        //* --------------------------------------------------------bytes =8
        //* byte[2]  status                               响应状态
        bodyBuf.writeShort(resMsg.getStatus());
        //* byte[2]  serializeType-(attr-index)           序列化策略
        bodyBuf.writeShort(resMsg.getSerializeType());
        //* byte[2]  returnType-(attr-index)              返回类型
        bodyBuf.writeShort(resMsg.getReturnType());
        //* byte[2]  returnData-(attr-index)              返回数据
        bodyBuf.writeShort(resMsg.getReturnData());
        //* --------------------------------------------------------bytes =1 ~ 1021
        //* byte[1]  optionCount                          选项参数总数
        int[] optionMapping = resMsg.getOptions();
        bodyBuf.writeByte(optionMapping.length);
        for (int i = 0; i < optionMapping.length; i++) {
            //* byte[4]  ptype-0-(attr-index,attr-index)  选项参数1
            //* byte[4]  ptype-1-(attr-index,attr-index)  选项参数2
            bodyBuf.writeInt(optionMapping[i]);
        }
        //* --------------------------------------------------------bytes =n
        //* dataBody                                      数据池
        resMsg.fillTo(bodyBuf);
        return bodyBuf;
    }
    //
    //
    //
    /**decode stream to object*/
    public ResponseSocketBlock decode(ByteBuf buf) throws IOException {
        //* --------------------------------------------------------bytes =13
        //* byte[1]  version                              RSF版本
        byte version = buf.readByte();
        //* byte[8]  requestID                            包含的请求ID
        long requestID = buf.readLong();
        //* byte[1]  keepData                             保留区
        buf.skipBytes(1);
        //* byte[3]  contentLength                        内容大小
        buf.skipBytes(3);//.readUnsignedMedium()
        //
        ResponseSocketBlock res = new ResponseSocketBlock();
        res.setHead(version);
        res.setRequestID(requestID);
        //* --------------------------------------------------------bytes =8
        //* byte[2]  status                               响应状态
        res.setStatus(buf.readShort());
        //* byte[2]  serializeType-(attr-index)           序列化策略
        res.setSerializeType(buf.readShort());
        //* byte[2]  returnType-(attr-index)              返回类型
        res.setReturnType(buf.readShort());
        //* byte[2]  returnData-(attr-index)              返回数据
        res.setReturnData(buf.readShort());
        //* --------------------------------------------------------bytes =1 ~ 1021
        //* byte[1]  optionCount                          选项参数总数
        byte optionCount = buf.readByte();
        for (int i = 0; i < optionCount; i++) {
            //* byte[4]  attr-0-(attr-index,attr-index)   选项参数
            int mergeData = buf.readInt();
            res.addOption(mergeData);
        }
        //* --------------------------------------------------------bytes =n
        //* dataBody                                      数据池
        res.fillFrom(buf);
        return res;
    }
}