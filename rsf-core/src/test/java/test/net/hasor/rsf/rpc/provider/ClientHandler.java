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
package test.net.hasor.rsf.rpc.provider;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.IOException;
import java.util.List;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.constants.RSFConstants;
import net.hasor.rsf.protocol.protocol.RequestSocketBlock;
import net.hasor.rsf.protocol.protocol.ResponseSocketBlock;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.rpc.objects.socket.RsfResponseFormSocket;
import net.hasor.rsf.serialize.coder.HessianSerializeCoder;
import net.hasor.rsf.utils.ProtocolUtils;
import net.hasor.rsf.utils.RsfRuntimeUtils;
/**
 * 100W 打印一次，证明还活着
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private AbstractRsfContext rsfContext;
    private RsfBindInfo<?>     bindInfo;
    public ClientHandler(AbstractRsfContext rsfContext, RsfBindInfo<?> bindInfo) {
        this.rsfContext = rsfContext;
        this.bindInfo = bindInfo;
    }
    //
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ResponseSocketBlock socketBlock = (ResponseSocketBlock) msg;
        if (socketBlock.getStatus() == 202)
            return;
        if (socketBlock.getRequestID() % 10000000 == 0) {
            RsfResponseFormSocket rsfResponse = new RsfResponseFormSocket(rsfContext, this.bindInfo, socketBlock);
            System.out.println("reqID:" + rsfResponse.getRequestID() + "\tdata=" + rsfResponse.getResponseData());
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
        short paramType = ProtocolUtils.pushString(request, RsfRuntimeUtils.toAsmType(Integer.TYPE));
        short paramValue = request.pushData(coder.encode(1));
        //
        request.setServiceName(ProtocolUtils.pushString(request, List.class.getName()));
        request.setServiceVersion(ProtocolUtils.pushString(request, "1.0.0"));
        request.setServiceGroup(ProtocolUtils.pushString(request, "RSF"));
        request.setSerializeType(ProtocolUtils.pushString(request, "Hessian"));
        request.setTargetMethod(ProtocolUtils.pushString(request, "get"));
        request.addParameter(paramType, paramValue);
        //
        request.addOption(ProtocolUtils.pushString(request, "sync"), ProtocolUtils.pushString(request, "true"));
        //
        return request;
    }
}