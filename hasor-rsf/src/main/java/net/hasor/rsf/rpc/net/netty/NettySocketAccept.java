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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
/**
 * 网络接受器，负责判定网络连接是否接受。
 * @version : 2017年01月16日
 * @author 赵永春(zyc@hasor.net)
 */
class NettySocketAccept extends ChannelInboundHandlerAdapter {
    private NettyConnector connector;
    public NettySocketAccept(NettyConnector connector) {
        this.connector = connector;
    }
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!this.connector.acceptIn(ctx)) {
            ctx.close();
        }
    }
}