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
package net.hasor.rsf.transform.netty;
import static net.hasor.rsf.domain.RSFConstants.RSF_Packet_Request;
import static net.hasor.rsf.domain.RSFConstants.RSF_Packet_Response;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RSFConstants;
import net.hasor.rsf.transform.codec.ProtocolUtils;
import net.hasor.rsf.transform.protocol.RequestInfo;
import net.hasor.rsf.transform.protocol.ResponseBlock;
import net.hasor.rsf.transform.protocol.ResponseInfo;
/**
 * 解码器
 * @version : 2014年10月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class RSFProtocolDecoder extends LengthFieldBasedFrameDecoder {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    public RSFProtocolDecoder() {
        this(Integer.MAX_VALUE);
    }
    public RSFProtocolDecoder(int maxBodyLength) {
        // lengthFieldOffset   = 10
        // lengthFieldLength   = 3
        // lengthAdjustment    = 0
        // initialBytesToStrip = 0
        super(maxBodyLength, 10, 3, 0, 0);
    }
    //
    /*解码*/
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        //
        byte rsfHead = frame.getByte(0);//协议头
        short status = this.doDecode(rsfHead, ctx, frame);//协议解析
        if (status != ProtocolStatus.OK) {
            frame = frame.resetReaderIndex().skipBytes(1);
            this.fireProtocolError(ctx, rsfHead, frame.readLong(), status);
        }
        return null;
    }
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.slice(index, length);
    }
    //
    /**协议解析*/
    private short doDecode(byte rsfHead, ChannelHandlerContext ctx, ByteBuf frame) {
        if ((RSF_Packet_Request | rsfHead) == rsfHead) {
            try {
                RequestInfo info = ProtocolUtils.buildRequestInfo(rsfHead, frame);// <-1.解码二进制数据。
                info.setReceiveTime(System.currentTimeMillis());//                   <-2.设置接收时间戳
                ctx.fireChannelRead(info);
                return ProtocolStatus.OK;/*正常处理后返回*/
            } catch (IOException e) {
                logger.error("decode request error :" + e.getMessage(), e);
                return ProtocolStatus.ProtocolError;
            }
        }
        if ((RSF_Packet_Response | rsfHead) == rsfHead) {
            try {
                ResponseInfo info = ProtocolUtils.buildResponseInfo(rsfHead, frame);// <-1.解码二进制数据。
                info.setReceiveTime(System.currentTimeMillis());//                     <-2.设置接收时间戳
                ctx.fireChannelRead(info);
                return ProtocolStatus.OK;/*正常处理后返回*/
            } catch (IOException e) {
                logger.error("decode response error :" + e.getMessage(), e);
                return ProtocolStatus.ProtocolError;
            }
        }
        return ProtocolStatus.ProtocolUnknown;
    }
    //
    /**发送错误 */
    private void fireProtocolError(ChannelHandlerContext ctx, byte rsfHead, long requestID, short status) {
        ResponseBlock block = new ResponseBlock();
        block.setHead(RSFConstants.RSF_Response);
        block.setRequestID(requestID);
        block.setStatus(status);
        block.setSerializeType(block.pushData(null));
        ctx.pipeline().writeAndFlush(block);
    }
}