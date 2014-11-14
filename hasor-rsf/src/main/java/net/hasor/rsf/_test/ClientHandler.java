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
import net.hasor.rsf.serialize.coder.Hessian_DecoderEncoder;
/**
 * 
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private ServerRsfContext manager          = null;
    private long             sendCount        = 0;
    private long             acceptedCount    = 0;
    private long             chooseOtherCount = 0;
    private long             serializeError   = 0;
    private long             requestTimeout   = 0;
    private long             okCount          = 0;
    private long             start            = System.currentTimeMillis();
    //
    public ClientHandler(ServerRsfContext manager) {
        this.manager = manager;
        manager.getCallExecute("aa").execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    aa();
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {}
                }
            }
        });
    }
    public void aa() {
        long duration = System.currentTimeMillis() - start;
        System.out.println("send QPS  :" + (sendCount * 1000 / duration));
        System.out.println("accept QPS:" + ((acceptedCount - chooseOtherCount) * 1000 / duration));
        System.out.println("send      :" + sendCount);
        System.out.println("accept    :" + (acceptedCount - chooseOtherCount));
        System.out.println("choose    :" + chooseOtherCount);
        System.out.println("serialize :" + serializeError);
        System.out.println("timeout   :" + requestTimeout);
        System.out.println("ok(%)     :" + okCount);
        System.out.println();
    }
    //
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ResponseMsg response = (ResponseMsg) msg;
        //
        if (response.getStatus() == ProtocolStatus.Accepted.shortValue())
            acceptedCount++;
        else if (response.getStatus() == ProtocolStatus.ChooseOther.shortValue())
            chooseOtherCount++;
        else if (response.getStatus() == ProtocolStatus.OK.shortValue())
            okCount++;
        else if (response.getStatus() == ProtocolStatus.SerializeError.shortValue())
            serializeError++;
        else if (response.getStatus() == ProtocolStatus.RequestTimeout.shortValue())
            requestTimeout++;
        else {
            int a = 0;
            a++;
        }
    }
    //1.第一个包
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelFutureListener listener = new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess() == false)
                    return;
                if (reqID < 1000000)
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
        Hessian_DecoderEncoder de = new Hessian_DecoderEncoder();
        RequestMsg request = new RequestMsg();
        request.setVersion(ProtocolVersion.V_1_0.value());
        request.setRequestID(reqID++);
        //
        request.setServiceName("net.hasor.rsf._test.TestServices");
        request.setServiceVersion("1.0.0");
        request.setServiceGroup("default");
        request.setTargetMethod("sayHello");//String item, int index
        request.setSerializeType("Hessian");
        //
        request.addParameter("java.lang.String", de.encode("你好..."));
        //
        request.addOption("sync", "true");
        //
        sendCount++;
        return request;
    }
}