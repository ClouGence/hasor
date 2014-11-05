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
package net.hasor.rsf._test.socket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.IOException;
import net.hasor.rsf.general.RSFConstants;
import net.hasor.rsf.protocol.codec.RpcRequestProtocol;
import net.hasor.rsf.protocol.socket.RequestSocketMessage;
import net.hasor.rsf.protocol.socket.ResponseSocketMessage;
import net.hasor.rsf.protocol.toos.SocketUtils;
/**
 * 
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private long sendCount     = 0;
    private long acceptedCount = 0;
    private long start         = System.currentTimeMillis();
    //
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (SocketUtils.isACK((ResponseSocketMessage) msg))
            acceptedCount++;
        //
        //
        //
        long duration = System.currentTimeMillis() - start;
        if (duration % 5000 == 0) {
            long qps = sendCount * 1000 / duration;
            System.out.println("QPS:" + qps);
            System.out.println("send:" + sendCount);
            System.out.println("accepted:" + acceptedCount);
            System.out.println("ok(%):" + (float) acceptedCount / (float) sendCount * 100);
            System.out.println();
        }
    }
    //1.第一个包
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelFutureListener listener = new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess() == false)
                    return;
                future.channel().writeAndFlush(getData()).addListener(this);
            }
        };
        //
        ctx.writeAndFlush(getData()).addListener(listener);
    }
    //
    //
    private static int reqID = 0;
    private ByteBuf getData() throws IOException {
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
        sendCount++;
        return out;
    }
}