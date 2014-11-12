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
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.IOException;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.general.ProtocolVersion;
import net.hasor.rsf.protocol.message.RequestMsg;
import net.hasor.rsf.protocol.message.ResponseMsg;
/**
 * 
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private long sendCount        = 0;
    private long acceptedCount    = 0;
    private long chooseOtherCount = 0;
    private long okCount          = 0;
    private long start            = System.currentTimeMillis();
    //
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ResponseMsg response = (ResponseMsg) msg;
        if (response.getStatus() == ProtocolStatus.Accepted)
            acceptedCount++;
        else if (response.getStatus() == ProtocolStatus.ChooseOther)
            chooseOtherCount++;
        else if (response.getStatus() == ProtocolStatus.OK)
            okCount++;
        //
        //
        //
        long duration = System.currentTimeMillis() - start;
        if (duration % 500 == 0) {
            System.out.println("send QPS  :" + (sendCount * 1000 / duration));
            System.out.println("accept QPS:" + ((acceptedCount - chooseOtherCount) * 1000 / duration));
            System.out.println("send      :" + sendCount);
            System.out.println("accept    :" + (acceptedCount - chooseOtherCount));
            System.out.println("choose    :" + chooseOtherCount);
            System.out.println("ok(%)     :" + (float) okCount / (float) sendCount * 100);
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
    private RequestMsg getData() throws IOException {
        RequestMsg request = new RequestMsg();
        request.setVersion(ProtocolVersion.V_1_0.value());
        request.setRequestID(reqID++);
        //
        request.setServiceName("java.util.List");
        request.setServiceVersion("1.0.0");
        request.setServiceGroup("default");
        request.setTargetMethod("size");
        request.setSerializeType("json");
        //
        request.addParameter("java.lang.String", "你好...".getBytes());
        request.addParameter("java.lang.String", "你好...".getBytes());
        request.addParameter("java.lang.String", "你好...".getBytes());
        //
        request.addOption("sync", "true");
        //
        sendCount++;
        return request;
    }
}