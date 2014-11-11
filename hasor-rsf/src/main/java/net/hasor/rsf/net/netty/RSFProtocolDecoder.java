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
package net.hasor.rsf.net.netty;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.general.ProtocolType;
import net.hasor.rsf.general.ProtocolVersion;
import net.hasor.rsf.general.RSFConstants;
import net.hasor.rsf.metadata.RequestMetaData;
import net.hasor.rsf.metadata.ResponseMetaData;
import net.hasor.rsf.protocol.codec.Protocol;
import net.hasor.rsf.protocol.message.RequestSocketMessage;
import net.hasor.rsf.protocol.message.ResponseSocketMessage;
import net.hasor.rsf.protocol.toos.ProtocolUtils;
import net.hasor.rsf.protocol.toos.TransferUtils;
/**
 * 解码器
 * @version : 2014年10月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class RSFProtocolDecoder extends LengthFieldBasedFrameDecoder {
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
        //* byte[1]  version                              RSF版本(0xC1)
        byte version = frame.getByte(0);
        //decode
        ProtocolType pType = ProtocolType.valueOf(version);
        if (pType == ProtocolType.Request) {
            //request
            Protocol<RequestSocketMessage> requestProtocol = ProtocolUtils.requestProtocol(version);
            if (requestProtocol != null) {
                RequestSocketMessage reqSocket = requestProtocol.decode(frame);
                RequestMetaData reqMetaData = TransferUtils.requestTransferToMetaData(reqSocket);
                this.fireAck(ctx, reqMetaData);
                return null;/*正常处理后返回*/
            }
        }
        if (pType == ProtocolType.Response) {
            //response
            Protocol<ResponseSocketMessage> responseProtocol = ProtocolUtils.responseProtocol(version);
            if (responseProtocol != null) {
                ResponseSocketMessage resSocket = responseProtocol.decode(frame);
                ResponseMetaData resMetaData = TransferUtils.responseTransferToMetaData(resSocket);
                ctx.fireChannelRead(resMetaData);
                return null;/*正常处理后返回*/
            }
        }
        /*                    错误情况*/
        frame.skipBytes(1);
        this.fireProtocolError(ctx, frame.readLong());
        return null;
    }
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.slice(index, length);
    }
    //
    //
    /**发送协议错误*/
    private void fireProtocolError(ChannelHandlerContext ctx, long reqID) {
        //1.发送Error包
        ResponseSocketMessage ack = new ResponseSocketMessage();
        ack.setVersion((byte) (RSFConstants.RSF_Response | ProtocolVersion.V_1_0.value()));
        ack.setRequestID(reqID);
        ack.setStatus(ProtocolStatus.ProtocolError.shortValue());
        ctx.pipeline().writeAndFlush(ack);
    }
    /**发送ACK*/
    private void fireAck(ChannelHandlerContext ctx, RequestMetaData reqMetaData) {
        //1.发送ACK包
        ResponseSocketMessage ack = new ResponseSocketMessage();
        ack.setVersion((byte) (RSFConstants.RSF_Response | ProtocolVersion.V_1_0.value()));
        ack.setRequestID(reqMetaData.getRequestID());
        ack.setStatus(ProtocolStatus.Accepted.shortValue());
        //2.当ACK，发送成功之后继续传递msg
        ctx.pipeline().writeAndFlush(ack).addListener(new FireChannel(ctx, reqMetaData));
    }
}
/***/
class FireChannel implements ChannelFutureListener {
    private ChannelHandlerContext ctx     = null;
    private Object                message = null;
    //
    public FireChannel(ChannelHandlerContext ctx, Object message) {
        this.ctx = ctx;
        this.message = message;
    }
    public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess() == false)
            return;
        this.ctx.fireChannelRead(this.message);
    }
}