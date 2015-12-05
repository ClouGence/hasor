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
package test.net.hasor.rsf._04_protocol.socket;
import java.util.Date;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.serialize.SerializeCoder;
import net.hasor.rsf.serialize.SerializeFactory;
import net.hasor.rsf.transform.protocol.RequestInfo;
import net.hasor.rsf.transform.protocol.ResponseInfo;
/**
 * 100W 打印一次，证明还活着
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private SerializeCoder coder = null;
    public ClientHandler(RsfSettings rsfSetting) {
        SerializeFactory factory = SerializeFactory.createFactory(rsfSetting);
        coder = factory.getSerializeCoder("java");
    }
    //
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ResponseInfo response = (ResponseInfo) msg;
        if (response.getRequestID() % 1000000 == 0) {
            System.out.println("reqID:" + response.getRequestID());
        }
    }
    //
    //
    //一个接着一个发送，永不停息
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelFutureListener listener = new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess() == false)
                    return;
                RequestInfo info = newInfo();
                if (info == null) {
                    System.err.println("info is null, send end.");
                    return;
                }
                future.channel().writeAndFlush(info).addListener(this);
            }
        };
        //
        RequestInfo info = newInfo();
        if (info == null) {
            System.err.println("info is null, send end.");
            return;
        }
        ctx.writeAndFlush(info).addListener(listener);
    }
    //
    private static long reqID = 0;
    private RequestInfo newInfo() {
        try {
            RequestInfo request = new RequestInfo();
            request.setRequestID(reqID++);
            request.setServiceGroup("RSF");
            request.setServiceName("test.net.hasor.rsf.services.EchoService");
            request.setServiceVersion("1.0.0");
            request.setSerializeType("serializeType");
            request.setTargetMethod("targetMethod");
            request.setClientTimeout(6000);
            request.setReceiveTime(System.currentTimeMillis());
            //
            request.addParameter("java.lang.String", coder.encode("say Hello."));
            request.addParameter("java.lang.Long", coder.encode(111222333444555L));
            request.addParameter("java.util.Date", coder.encode(new Date()));
            request.addParameter("java.lang.Object", coder.encode(null));
            //
            request.addOption("auth", "yes");
            request.addOption("user", "guest");
            request.addOption("password", null);
            //
            return request;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}