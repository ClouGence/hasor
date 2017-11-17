/*
 * Copyright 2008-2009 the original author or authors.
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
package net.hasor.rsf.rpc.net.netty;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.hasor.rsf.domain.OptionInfo;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.rpc.net.LinkPool;
import net.hasor.rsf.rpc.net.RsfChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;
/**
 * RPC协议连接器，负责创建某个特定RPC协议的网络事件。
 * tips：传入的网络连接，交给{@link LinkPool}进行处理，{@link NettyConnector}本身不维护任何连接。
 * @version : 2017年01月16日
 * @author 赵永春(zyc@hasor.net)
 */
@ChannelHandler.Sharable
class NettyHandlerAdapter extends ChannelInboundHandlerAdapter {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private NettyConnector connector;
    //
    private static String converToHostProt(ChannelHandlerContext ctx) {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        return socketAddress.getAddress().getHostAddress() + ":" + socketAddress.getPort();
    }
    public NettyHandlerAdapter(NettyConnector connector) {
        this.connector = connector;
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.exceptionCaught(ctx, null);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String hostPort = converToHostProt(ctx);
        if (cause == null) {
            this.logger.warn("close socket=" + hostPort + " channel Inactive.");
        } else {
            this.logger.error("close socket=" + hostPort + " with error -> " + cause.getMessage(), cause);
        }
        ctx.close();
    }
    /** 接收解析好的 RequestInfo、ResponseInfo 对象，并将它们转发到 {@link RsfChannel}接收事件中。 */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof OptionInfo) {
            String hostPort = converToHostProt(ctx);
            Future<RsfChannel> channel = this.connector.findRsfChannelByHostPort(hostPort);
            if (channel == null || !channel.isDone()) {
                this.exceptionCaught(ctx, new RsfException(ProtocolStatus.NetworkError, "the " + hostPort + " connection is not in the pool."));
                return;
            }
            RsfChannel rsfChannel = channel.get();
            if (rsfChannel.getTarget() == null) {
                this.exceptionCaught(ctx, new RsfException(ProtocolStatus.NetworkError, "the " + hostPort + " connection is not management."));
                return;
            }
            //
            ((RsfChannelOnNetty) rsfChannel).receivedData((OptionInfo) msg);
        }
        super.channelRead(ctx, msg);
    }
}