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
package test.net.hasor.rsf.protocol;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.IOException;
import net.hasor.rsf.constants.RSFConstants;
import net.hasor.rsf.protocol.protocol.RequestSocketBlock;
import net.hasor.rsf.protocol.protocol.ResponseSocketBlock;
import net.hasor.rsf.serialize.coder.HessianSerializeCoder;
import net.hasor.rsf.utils.ProtocolUtils;
/**
 * 100W 打印一次，证明还活着
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ResponseSocketBlock response = (ResponseSocketBlock) msg;
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
                future.channel().writeAndFlush(getData()).addListener(this);
            }
        };
        //
        ctx.writeAndFlush(getData()).addListener(listener);
    }
    //
    private static long reqID = 0;
    private RequestSocketBlock getData() throws IOException {
        HessianSerializeCoder coder = new HessianSerializeCoder();
        RequestSocketBlock request = new RequestSocketBlock();
        request.setHead(RSFConstants.RSF_Request);
        request.setRequestID(reqID++);
        //
        request.setServiceName(ProtocolUtils.pushString(request, "net.hasor.rsf._test.TestServices"));
        request.setServiceVersion(ProtocolUtils.pushString(request, "1.0.0"));
        request.setServiceGroup(ProtocolUtils.pushString(request, "default"));
        request.setTargetMethod(ProtocolUtils.pushString(request, "sayHello"));//String item, int index
        request.setSerializeType(ProtocolUtils.pushString(request, "Hessian"));
        //
        request.addParameter(ProtocolUtils.pushString(request, "java.lang.String"), request.pushData(coder.encode("你好...")));
        //
        request.addOption(ProtocolUtils.pushString(request, "sync"), ProtocolUtils.pushString(request, "true"));
        //
        return request;
    }
}