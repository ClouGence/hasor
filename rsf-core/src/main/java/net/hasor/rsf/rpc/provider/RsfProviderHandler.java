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
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import net.hasor.rsf.RsfOptionSet;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.rsf.protocol.protocol.RequestSocketBlock;
import net.hasor.rsf.protocol.protocol.ResponseSocketBlock;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.utils.ProtocolUtils;
import net.hasor.rsf.utils.RsfRuntimeUtils;
import org.more.logger.LoggerHelper;
/**
 * 负责接受 RSF 消息，并将消息转换为 request/response 对象供业务线程使用。
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfProviderHandler extends ChannelInboundHandlerAdapter {
    private AbstractRsfContext rsfContext;
    //
    public RsfProviderHandler(AbstractRsfContext rsfContext) {
        this.rsfContext = rsfContext;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof RequestSocketBlock == false) {
            return;
        }
        //
        //创建request、response
        RequestSocketBlock requestBlock = (RequestSocketBlock) msg;
        RsfOptionSet optMap = this.rsfContext.getSettings().getServerOption();
        //
        //放入业务线程准备执行
        ResponseSocketBlock readyWrite = null;
        try {
            LoggerHelper.logFinest("received request(%s) full = %s", requestBlock.getRequestID(), requestBlock);
            String serviceName = new String(requestBlock.readPool(requestBlock.getServiceName()));
            Executor exe = this.rsfContext.getCallExecute(serviceName);
            Channel nettyChannel = ctx.channel();
            exe.execute(new InnerRequestHandler(this.rsfContext, requestBlock, nettyChannel));
            //
            readyWrite = ProtocolUtils.buildStatus(requestBlock, ProtocolStatus.Accepted, optMap);
        } catch (RejectedExecutionException e) {
            LoggerHelper.logWarn("task pool is full ->RejectedExecutionException.");
            readyWrite = ProtocolUtils.buildStatus(requestBlock, ProtocolStatus.ChooseOther, optMap);
        }
        //
        ctx.pipeline().writeAndFlush(readyWrite);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        InterAddress address = RsfRuntimeUtils.getAddress(ctx.channel());
        if (address != null) {
            LoggerHelper.logSevere("exceptionCaught, host = %s. , msg = %s.", address, cause.getMessage());
            this.rsfContext.getAddressPool().invalidAddress(address.toURI());
        }
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().close();
        InterAddress address = RsfRuntimeUtils.getAddress(ctx.channel());
        if (address != null) {
            LoggerHelper.logSevere("remote close, host = %s.", address);
            this.rsfContext.getAddressPool().invalidAddress(address.toURI());
        }
    }
}