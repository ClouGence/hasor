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
package net.hasor.rsf.rpc;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.utils.RsfRuntimeUtils;
import org.more.logger.LoggerHelper;
/**
 * 基类
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class BaseChannelInboundHandlerAdapter extends ChannelInboundHandlerAdapter {
    protected final AbstractRsfContext rsfContext;
    public BaseChannelInboundHandlerAdapter(AbstractRsfContext rsfContext) {
        this.rsfContext = rsfContext;
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
        LoggerHelper.logWarn("channelInactive, host = " + address);
    }
}