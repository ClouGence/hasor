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
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.protocol.message.RequestMsg;
import net.hasor.rsf.protocol.message.ResponseMsg;
import net.hasor.rsf.protocol.toos.TransferUtils;
import net.hasor.rsf.runtime.common.NetworkConnection;
import net.hasor.rsf.runtime.context.AbstractRsfContext;
/**
 * 负责接受 RSF 消息，并将消息转换为 request/response 对象供业务线程使用。
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
class InnerServerHandler extends ChannelInboundHandlerAdapter {
    private AbstractRsfContext rsfContext;
    //
    public InnerServerHandler(AbstractRsfContext rsfContext) {
        this.rsfContext = rsfContext;
    }
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof RequestMsg == false)
            return;
        //创建request、response
        RequestMsg requestMsg = (RequestMsg) msg;
        requestMsg.setReceiveTime(System.currentTimeMillis());
        //放入业务线程准备执行
        try {
            Executor exe = this.rsfContext.getCallExecute(requestMsg.getServiceName());
            NetworkConnection conn = NetworkConnection.getConnection(ctx.channel());
            exe.execute(new InnerRequestHandler(this.rsfContext, requestMsg, conn));
            //
            ResponseMsg pack = TransferUtils.buildStatus(//
                    requestMsg.getVersion(), //协议版本
                    requestMsg.getRequestID(),//请求ID
                    ProtocolStatus.Accepted);//回应ACK
            ctx.pipeline().write(pack);
        } catch (RejectedExecutionException e) {
            ResponseMsg pack = TransferUtils.buildStatus(//
                    requestMsg.getVersion(), //协议版本
                    requestMsg.getRequestID(),//请求ID
                    ProtocolStatus.ChooseOther);//服务器资源紧张
            ctx.pipeline().write(pack);
        }
    }
}