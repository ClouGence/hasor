/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package net.hasor.tconsole.client;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.hasor.tconsole.TelAttribute;
import net.hasor.tconsole.TelOptions;
import net.hasor.utils.future.BasicFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

import static net.hasor.tconsole.TelOptions.ENDCODE_OF_SILENT;

/**
 * Handles a client-side channel.
 */
@Sharable
class TelClientHandler extends SimpleChannelInboundHandler<String> {
    private static Logger                 logger       = LoggerFactory.getLogger(TelClientHandler.class);
    private        TelAttribute           telAttribute;
    private        TelClientEventListener closeFuture;
    private        BasicFuture<Object>    activeFuture;
    private        ByteBuf                receiveDataBuffer;
    private        boolean                receiveReady = false;

    TelClientHandler(TelAttribute telAttribute, BasicFuture<Object> activeFuture, TelClientEventListener closeFuture, ByteBuf receiveDataBuffer) {
        this.activeFuture = activeFuture;
        this.closeFuture = closeFuture;
        this.receiveDataBuffer = receiveDataBuffer;
        this.telAttribute = telAttribute;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 2. 先设置成静默
        ctx.channel().writeAndFlush(String.format("set %s=%s\n", TelOptions.SILENT, true));
        // 3. 命令结束符
        ctx.channel().writeAndFlush(String.format("set %s=%s\n", TelOptions.ENDCODE_OF_SILENT, this.telAttribute.getAttribute(ENDCODE_OF_SILENT)));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        if (this.receiveReady) {
            this.receiveDataBuffer.writeCharSequence(msg + "\n", StandardCharsets.UTF_8);
        }
        if (msg.equals(this.telAttribute.getAttribute(ENDCODE_OF_SILENT))) {
            this.receiveReady = true;//上面设置 ENDCODE_OF_SILENT 之后就会返回。
            this.activeFuture.completed(new Object());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(cause.getMessage(), cause);
        ctx.close();
        this.closeFuture.onEventClient();
        if (!this.activeFuture.isDone()) {
            this.activeFuture.failed(cause);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        this.closeFuture.onEventClient();
    }
}