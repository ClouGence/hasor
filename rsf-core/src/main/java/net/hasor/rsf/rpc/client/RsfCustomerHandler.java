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
package net.hasor.rsf.rpc.client;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.hasor.rsf.RsfFuture;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.protocol.protocol.ResponseSocketBlock;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.utils.RsfRuntimeUtils;
import org.more.logger.LoggerHelper;
/**
 * 负责处理 RSF 发出请求之后的所有响应（不区分连接）
 *  -- 根据 {@link ResponseMsg}中包含的 requestID 找到对应的{@link RsfFuture}。
 *  -- 通过{@link RsfFuture}发起响应。
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfCustomerHandler extends ChannelInboundHandlerAdapter {
    private final AbstractRsfContext rsfContext;
    public RsfCustomerHandler(AbstractRsfContext rsfContext) {
        this.rsfContext = rsfContext;
    }
    //
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ResponseSocketBlock == false)
            return;
        ResponseSocketBlock block = (ResponseSocketBlock) msg;
        LoggerHelper.logFinest("received response(%s) full = %s", block.getRequestID(), block);
        //
        RsfClientRequestManager requestManager = this.rsfContext.getRequestManager();
        RsfFuture rsfFuture = requestManager.getRequest(block.getRequestID());
        if (rsfFuture == null) {
            LoggerHelper.logWarn("give up the response,requestID(%s) ,maybe because timeout! ", block.getRequestID());
            return;//或许它已经超时了。
        }
        LoggerHelper.logFine("doResponse.");
        new CustomerProcessing(block, requestManager, rsfFuture).run();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        InterAddress address = RsfRuntimeUtils.getAddress(channel);
        rsfContext.getChannelManager().closeChannel(channel);
        LoggerHelper.logSevere("exceptionCaught, host = " + address, cause);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        InterAddress address = RsfRuntimeUtils.getAddress(channel);
        rsfContext.getChannelManager().closeChannel(channel);
        LoggerHelper.logSevere("channelInactive, host = " + address);
    }
}