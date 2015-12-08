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
package net.hasor.rsf.rpc.net;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
/**
 * 基类
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class BaseChannelInboundHandlerAdapter extends ChannelInboundHandlerAdapter {
    protected Logger           logger = LoggerFactory.getLogger(getClass());
    protected final NetChannel netChannel;
    public BaseChannelInboundHandlerAdapter(NetChannel netChannel) {
        this.netChannel = netChannel;
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        netChannel.closeChannel(channel, cause);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        netChannel.closeChannel(channel, null);
    }
    //    protected ResponseInfo buildStatus(RequestInfo requestInfo, int status, String message) {
    //        ResponseInfo info = new ResponseInfo();
    //        RsfOptionSet optMap = this.get.rsfContext.getSettings().getServerOption();
    //        info.addOptionMap(optMap);
    //        info.setRequestID(requestInfo.getRequestID());
    //        info.setStatus(ProtocolStatus.Accepted);
    //        if (StringUtils.isNotBlank(message)) {
    //            info.addOption("message", message);
    //        }
    //        return info;
    //    }
}