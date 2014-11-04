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
import io.netty.handler.codec.MessageToByteEncoder;
import net.hasor.rsf.protocol.codec.RpcRequestProtocol;
import net.hasor.rsf.protocol.codec.RpcResponseProtocol;
import net.hasor.rsf.protocol.socket.RSFSocketMessage;
import net.hasor.rsf.protocol.socket.RequestSocketMessage;
import net.hasor.rsf.protocol.socket.ResponseSocketMessage;
/**
 * 编码器
 * @version : 2014年10月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class RSFProtocolEncoder extends MessageToByteEncoder<RSFSocketMessage> {
    protected void encode(ChannelHandlerContext ctx, RSFSocketMessage msg, ByteBuf out) throws Exception {
        //
        if (msg instanceof RequestSocketMessage) {
            //request
            new RpcRequestProtocol().encode((RequestSocketMessage) msg, out);
        } else if (msg instanceof ResponseSocketMessage) {
            //response
            new RpcResponseProtocol().encode((ResponseSocketMessage) msg, out);
        }
        //
    }
}