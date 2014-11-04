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
package net.hasor.rsf._test;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.hasor.rsf.general.RSFConstants;
import net.hasor.rsf.protocol.codec.RpcRequestProtocol;
import net.hasor.rsf.protocol.socket.RequestSocketMessage;
/**
 * 
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private static int reqID = 1230;
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 9000000; i++) {
            this._channelActive(ctx);
        }
        ctx.close();
    }
    // 连接成功后，向server发送消息
    public void _channelActive(ChannelHandlerContext ctx) throws Exception {
        RequestSocketMessage req = new RequestSocketMessage();
        req.setVersion(RSFConstants.RSF_V_1_0_Req);
        req.setRequestID(reqID++);
        //
        req.setServiceName(req.pushData("java.util.List".getBytes()));
        req.setServiceVersion(req.pushData("1.0.0".getBytes()));
        req.setServiceGroup(req.pushData("default".getBytes()));
        req.setTargetMethod(req.pushData("size".getBytes()));
        req.setSerializeType(req.pushData("json".getBytes()));
        //
        req.addParameter(//
                req.pushData("java.lang.String".getBytes()),//
                req.pushData("你好...".getBytes()));
        req.addParameter(//
                req.pushData("java.lang.String".getBytes()),//
                req.pushData("你好...".getBytes()));
        req.addParameter(//
                req.pushData("java.lang.String".getBytes()),//
                req.pushData(null));
        //
        //
        req.addOption(//
                req.pushData("sync".getBytes()),//
                req.pushData("true".getBytes()));
        //
        //
        ByteBuf out = ByteBufAllocator.DEFAULT.heapBuffer();
        new RpcRequestProtocol().encode(req, out);
        //        System.out.println("pack Size=" + out.readableBytes());
        ctx.writeAndFlush(out);
    }
}