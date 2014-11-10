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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import net.hasor.rsf.metadata.RequestMetaData;
import net.hasor.rsf.metadata.ResponseMetaData;
import net.hasor.rsf.protocol.codec.RpcRequestProtocol;
import net.hasor.rsf.protocol.codec.RpcResponseProtocol;
import net.hasor.rsf.protocol.message.RequestSocketMessage;
import net.hasor.rsf.protocol.message.ResponseSocketMessage;
import net.hasor.rsf.protocol.toos.TransferUtils;
/**
 * 编码器
 * @version : 2014年10月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class RSFProtocolEncoder extends MessageToByteEncoder<Object> {
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        ctx.flush();
    }
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        //1.MetaData转换
        if (msg instanceof RequestMetaData) {
            msg = TransferUtils.requestTransferToSocket((RequestMetaData) msg); //request
        } else if (msg instanceof ResponseMetaData) {
            msg = TransferUtils.responseTransferToSocket((ResponseMetaData) msg);//response
        }
        //
        //2.SocketMessage转换
        if (msg instanceof RequestSocketMessage) {
            new RpcRequestProtocol().encode((RequestSocketMessage) msg, out);//request
        } else if (msg instanceof ResponseSocketMessage) {
            new RpcResponseProtocol().encode((ResponseSocketMessage) msg, out);//response
        }
    }
}