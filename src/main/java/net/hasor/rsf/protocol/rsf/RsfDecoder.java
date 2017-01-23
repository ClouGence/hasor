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
package net.hasor.rsf.protocol.rsf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;
import net.hasor.rsf.utils.ProtocolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static net.hasor.rsf.domain.RsfConstants.*;
/**
 * RSF 解码器
 * @version : 2014年10月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfDecoder extends LengthFieldBasedFrameDecoder {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private RsfEnvironment rsfEnvironment;
    //
    public RsfDecoder(RsfEnvironment rsfEnvironment, int maxBodyLength) {
        // lengthFieldOffset   = 10
        // lengthFieldLength   = 3
        // lengthAdjustment    = 0
        // initialBytesToStrip = 0
        super(maxBodyLength, 10, 3, 0, 0);
        this.rsfEnvironment = rsfEnvironment;
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
            long requestID = frame.readLong();
            ResponseInfo info = ProtocolUtils.buildResponseStatus(this.rsfEnvironment, requestID, status, null);
            ctx.pipeline().writeAndFlush(info);
        }
        return null;
    }
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.slice(index, length);
    }
    //
    /**协议解析*/
    private short doDecode(byte rsfHead, ChannelHandlerContext ctx, ByteBuf frame) {
        CodecAdapter factory = CodecAdapterFactory.getCodecAdapterByVersion(this.rsfEnvironment, (byte) (rsfHead & 0x0F));
        // - RSF_InvokerRequest
        if (RSF_InvokerRequest == rsfHead) {
            try {
                RequestInfo info = factory.readRequestInfo(frame);//  <-1.解码二进制数据。
                info.setReceiveTime(System.currentTimeMillis());//    <-2.设置接收时间戳
                info.setMessage(false);
                //
                ctx.fireChannelRead(info);
                return ProtocolStatus.OK;/*正常处理后返回*/
            } catch (IOException e) {
                logger.error("decode request error :" + e.getMessage(), e);
                return ProtocolStatus.ProtocolError;
            }
        }
        // - RSF_MessageRequest
        if (RSF_MessageRequest == rsfHead) {
            try {
                RequestInfo info = factory.readRequestInfo(frame);//  <-1.解码二进制数据。
                info.setReceiveTime(System.currentTimeMillis());//    <-2.设置接收时间戳
                info.setMessage(true);
                //
                ctx.fireChannelRead(info);
                return ProtocolStatus.OK;/*正常处理后返回*/
            } catch (IOException e) {
                logger.error("decode request error :" + e.getMessage(), e);
                return ProtocolStatus.ProtocolError;
            }
        }
        // RSF_Response
        if (RSF_Response == rsfHead) {
            try {
                ResponseInfo info = factory.readResponseInfo(frame);//  <-1.解码二进制数据。
                info.setReceiveTime(System.currentTimeMillis());//      <-2.设置接收时间戳
                ctx.fireChannelRead(info);
                return ProtocolStatus.OK;/*正常处理后返回*/
            } catch (IOException e) {
                logger.error("decode response error :" + e.getMessage(), e);
                return ProtocolStatus.ProtocolError;
            }
        }
        return ProtocolStatus.ProtocolUndefined;
    }
}