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
package net.hasor.rsf.rpc.provider;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.rsf.rpc.component.NetworkConnection;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.rpc.message.RequestMsg;
import net.hasor.rsf.rpc.message.ResponseMsg;
import net.hasor.rsf.rpc.utils.TransferUtils;
import org.more.logger.LoggerHelper;
/**
 * 负责接受 RSF 消息，并将消息转换为 request/response 对象供业务线程使用。
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfProviderHandler extends ChannelInboundHandlerAdapter {
    private AbstractRsfContext rsfContext;
    private String             serializeType;
    //
    public RsfProviderHandler(AbstractRsfContext rsfContext) {
        this.rsfContext = rsfContext;
        this.serializeType = rsfContext.getSettings().getDefaultSerializeType();
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof RequestMsg == false) {
            return;
        }
        //创建request、response
        RequestMsg requestMsg = (RequestMsg) msg;
        requestMsg.setReceiveTime(System.currentTimeMillis());
        LoggerHelper.logFinest("received request(%s) full = %s", requestMsg.getRequestID(), requestMsg);
        //放入业务线程准备执行
        try {
            Executor exe = this.rsfContext.getCallExecute(requestMsg.getServiceName());
            NetworkConnection conn = NetworkConnection.getConnection(ctx.channel());
            exe.execute(new InnerRequestHandler(this.rsfContext, requestMsg, conn));
            //
            ResponseMsg pack = TransferUtils.buildStatus(//
                    requestMsg.getVersion(), //协议版本
                    requestMsg.getRequestID(),//请求ID
                    ProtocolStatus.Accepted,//响应状态
                    this.serializeType,//序列化类型
                    this.rsfContext.getSettings().getServerOption());//选项参数
            ctx.pipeline().writeAndFlush(pack);
        } catch (RejectedExecutionException e) {
            ResponseMsg pack = TransferUtils.buildStatus(//
                    requestMsg.getVersion(), //协议版本
                    requestMsg.getRequestID(),//请求ID
                    ProtocolStatus.ChooseOther,//服务器资源紧张
                    this.serializeType,//序列化类型
                    this.rsfContext.getSettings().getServerOption());//选项参数
            ctx.pipeline().writeAndFlush(pack);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        NetworkConnection conn = NetworkConnection.getConnection(ctx.channel());
        if (conn != null) {
            LoggerHelper.logSevere("exceptionCaught, host = %s. , msg = %s.", conn.getHostAddress(), cause.getMessage());
            this.rsfContext.getRequestManager().getClientManager().unRegistered(conn.getHostAddress());
        }
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().close();
        NetworkConnection conn = NetworkConnection.getConnection(ctx.channel());
        if (conn != null) {
            LoggerHelper.logInfo("remote close, host = %s.", conn.getHostAddress());
            this.rsfContext.getRequestManager().getClientManager().unRegistered(conn.getHostAddress());
        }
    }
}