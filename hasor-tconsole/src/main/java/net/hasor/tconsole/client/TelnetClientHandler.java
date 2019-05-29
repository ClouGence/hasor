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
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.hasor.utils.future.BasicFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * Handles a client-side channel.
 */
@Sharable
class TelnetClientHandler extends SimpleChannelInboundHandler<String> {
    protected static Logger              logger = LoggerFactory.getLogger(TelnetClientHandler.class);
    private          BasicFuture<Object> closeFuture;
    private          AtomicBoolean       atomicBoolean;
    private          StringWriter        returnData;
    public TelnetClientHandler(BasicFuture<Object> closeFuture, AtomicBoolean atomicBoolean, StringWriter returnData) {
        this.closeFuture = closeFuture;
        this.atomicBoolean = atomicBoolean;
        this.returnData = returnData;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        logger.debug(msg);
        if (returnData != null) {
            returnData.write(msg + "\n");
        }
        if (msg.startsWith("tConsole>[SUCCEED]")) {
            this.atomicBoolean.set(true);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(cause.getMessage(), cause);
        ctx.close();
        this.closeFuture.completed(new Object());
        this.atomicBoolean.set(true);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        this.closeFuture.completed(new Object());
        this.atomicBoolean.set(true);
    }
}