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
package net.hasor.rsf.runtime.server;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.protocol.block.ResponseSocketBlock;
import net.hasor.rsf.protocol.message.RequestMsg;
import net.hasor.rsf.protocol.toos.TransferUtils;
import net.hasor.rsf.runtime.RsfContext;
import net.hasor.rsf.runtime.RsfFilterChain;
/**
 * 提供服务的Handler。
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private RsfContext rsfContext = null;
    //
    public ServerHandler(RsfContext rsfContext) {
        this.rsfContext = rsfContext;
    }
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RequestMsg == false)
            return;
        RequestMsg request = (RequestMsg) msg;
        byte version = request.getVersion();
        long requestID = request.getRequestID();
        //check 404
        String serviceName = request.getServiceName();
        ServiceMetaData serviceMetaData = this.rsfContext.getService(serviceName);
        if (serviceMetaData == null) {
            this.fireNotFound(ctx, version, requestID);//404 NotFound
            return;
        }
        //do call
        Executor exe = this.rsfContext.getCallExecute(request.getServiceName());
        try {
            exe.execute(new InvokeHandler(this.rsfContext, request, ctx));
        } catch (RejectedExecutionException e) {
            this.fireChooseOther(ctx, version, requestID);
        }
    }
    private void fireNotFound(ChannelHandlerContext ctx, byte version, long requestID) {
        ResponseSocketBlock pack = TransferUtils.buildStatus(//
                version, requestID, ProtocolStatus.NotFound);
        ctx.pipeline().writeAndFlush(pack);
    }
    private void fireChooseOther(ChannelHandlerContext ctx, byte version, long requestID) {
        ResponseSocketBlock pack = TransferUtils.buildStatus(//
                version, requestID, ProtocolStatus.ChooseOther);
        ctx.pipeline().writeAndFlush(pack);
    }
}
class InvokeHandler implements Runnable {
    private RsfContext            rsfContext     = null;
    private RequestMsg            requestMessage = null;
    private ChannelHandlerContext channelContext = null;
    //
    public InvokeHandler(RsfContext rsfContext, RequestMsg requestMessage, ChannelHandlerContext channelContext) {
        this.rsfContext = rsfContext;
        this.requestMessage = requestMessage;
        this.channelContext = channelContext;
    }
    public void run() {
        //
        //1.创建Req,Res对象
        RsfRequestImpl request = new RsfRequestImpl(requestMessage, channelContext, rsfContext);
        RsfResponseImpl response = new RsfResponseImpl(requestMessage, channelContext, rsfContext);
        //
        //2.反序列化参数
        try {
            request.unSerialize();
        } catch (Throwable e) {
            //501 SerializeError
            response.sendError(ProtocolStatus.SerializeError, e);
            return;
        }
        //
        //3.获取调用对象
        Method targetMethod = null;
        Object targetObj = this.rsfContext.getBean(serviceMetaData);
        if (targetObj == null) {
            //403 Forbidden
        }
        //
        //4.执行调用
        try {
            RsfFilterChain rsfChain = null;
            new RsfFilterChainInvocation(null, rsfChain).doFilter(request, response);
        } catch (Exception e) {
            //500 InternalServerError
            response.sendError(ProtocolStatus.InternalServerError, e);
            return;
        }
    }
    //    private static volatile long requestCount = 0;
    //    private static volatile long start        = System.currentTimeMillis();
    //    public void process(Object msgObj) {
    //        if (msgObj instanceof RequestMsg == false)
    //            return;
    //        //
    //        requestCount++;
    //        //
    //        long duration = System.currentTimeMillis() - start;
    //        if (duration % 100 == 0) {
    //            long qps = requestCount * 1000 / duration;
    //            System.out.println("QPS         :" + qps);
    //            System.out.println("requestCount:" + requestCount);
    //            System.out.println("last REQID  :" + ((RequestMsg) msgObj).getRequestID());
    //            System.out.println();
    //        }
    //    }
}