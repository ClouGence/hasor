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
import io.netty.buffer.ByteBufProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.List;
import net.hasor.rsf.general.ProtocolType;
import net.hasor.rsf.general.RSFConstants;
import net.hasor.rsf.net.netty.RSFProtocolDecoder.State;
import net.hasor.rsf.protocol.ProtocolRequest;
/**
 * 
 * @version : 2014年10月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class RSFProtocolDecoder extends ReplayingDecoder<State> {
    public RSFProtocolDecoder() {
        super(State.Read_Head);
    }
    /** The internal state of {@link RSFProtocolDecoder}. <em>Internal use only</em>. */
    enum State {
        /*RSF协议头*/
        Read_Head,
        /*调用请求*/
        Read_Request,
        /*调用响应*/
        Read_Response,
        /*RSF消息*/
        Read_Message,
        /*Ping指令*/
        Read_Ping,
        /*坏的消息*/
        Bad_Message
    }
    //
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        switch (state()) {
        /*RSF协议头*/
        case Read_Head: {
            byte rsfVersion = in.readByte();
            ProtocolType rsfProtocolType = ProtocolType.valueOf(in.readByte());
            //1.检查协议版本
            if (rsfVersion != RSFConstants.RSF_Version || rsfProtocolType == ProtocolType.Unknown) {
                this.checkpoint(State.Bad_Message);
                return;
            }
            //2.处理各种类型消息
            switch (rsfProtocolType) {
            default:
                this.checkpoint(State.Bad_Message);
                return;
            case Request:
                this.checkpoint(State.Read_Request);
                break;
            case Response:
                this.checkpoint(State.Read_Response);
                break;
            case Message:
                this.checkpoint(State.Read_Message);
                break;
            case Ping:
                this.checkpoint(State.Read_Ping);
                break;
            }
            in.resetReaderIndex();
            return;
        }
        /*调用请求*/
        case Read_Request: {
            ProtocolRequest request = new ProtocolRequest();
            request.decode(in);
            out.add(request);
            return;
        }
        /*调用响应*/
        case Read_Response: {
            return;
        }
        /*RSF消息*/
        case Read_Message: {
            return;
        }
        /*Ping指令*/
        case Read_Ping: {
            return;
        }
        /*Ping指令*/
        case Bad_Message: {
            in.skipBytes(actualReadableBytes());
            return;
        }
        }
    }
    //
    //    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    //        try {
    //            ByteBuf result = (ByteBuf) msg;
    //            //
    //            ProtocolRequest request = new ProtocolRequest();
    //            request.decode(result);
    //            //
    //            System.out.println();
    //            //            result.
    //            //            byte[] result1 = new byte[result.readableBytes()];
    //            //            // msg中存储的是ByteBuf类型的数据，把数据读取到byte[]中
    //            //            result.readBytes(result1);
    //            //            String resultStr = new String(result1);
    //            //            // 接收并打印客户端的信息
    //            //            System.out.println("Client said:" + resultStr);
    //            //            // 释放资源，这行很关键
    //            //            result.release();
    //            //            // 向客户端发送消息
    //            //            String response = "I am ok!";
    //            //            // 在当前场景下，发送的数据必须转换成ByteBuf数组
    //            //            ByteBuf encoded = ctx.alloc().buffer(4 * response.length());
    //            //            encoded.writeBytes(response.getBytes());
    //            //            ctx.write(encoded);
    //            //            ctx.flush();
    //        } finally {
    //            ReferenceCountUtil.release(msg);
    //        }
    //    }
    private final class HeaderParser implements ByteBufProcessor {
        @Override
        public boolean process(byte value) throws Exception {
            // TODO Auto-generated method stub
            return false;
        }
    }
}