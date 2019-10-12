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
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import net.hasor.core.container.AbstractContainer;
import net.hasor.tconsole.launcher.TelUtils;
import net.hasor.utils.future.BasicFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * simple telnet client.
 */
public class TelClient extends AbstractContainer {
    private static Logger            logger = LoggerFactory.getLogger(TelClient.class);
    private final  InetSocketAddress remoteAddress;
    private        EventLoopGroup    loopGroup;
    private        Channel           channel;
    private final  ByteBuf           receiveDataBuffer;
    private        String            endcodeOfSilent;

    public TelClient(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
        this.receiveDataBuffer = ByteBufAllocator.DEFAULT.heapBuffer();
    }

    /** 远程地址 */
    public InetSocketAddress remoteAddress() {
        return this.remoteAddress;
    }

    /** 发送命令 */
    public String sendCommand(String message) throws InterruptedException {
        if (!this.isInit()) {
            throw new IllegalStateException("the Container has been inited.");
        }
        this.channel.writeAndFlush(message.trim() + "\n");
        while (this.isInit()) {
            this.receiveDataBuffer.resetReaderIndex();
            // 滑动窗口的机制
            int readLength = TelUtils.waitString(this.receiveDataBuffer, this.endcodeOfSilent);
            if (readLength > -1) {
                this.receiveDataBuffer.skipBytes(readLength + endcodeOfSilent.length());
                String dat = this.receiveDataBuffer.getCharSequence(0, readLength, StandardCharsets.UTF_8).toString();
                this.receiveDataBuffer.clear();// 完全释放
                this.receiveDataBuffer.discardReadBytes();
                return dat.trim();
            }
            Thread.sleep(10);
        }
        //
        this.receiveDataBuffer.resetReaderIndex();
        return this.receiveDataBuffer.toString(StandardCharsets.UTF_8);
    }

    @Override
    protected void doInitialize() {
        final BasicFuture<Object> activeFuture = new BasicFuture<>();
        this.endcodeOfSilent = "----" + UUID.randomUUID().toString().replace("-", "") + "----";
        this.loopGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b = b.group(this.loopGroup);
        b = b.channel(NioSocketChannel.class);
        b = b.handler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Unpooled.wrappedBuffer(new byte[] { '\n' })));
                pipeline.addLast(new StringDecoder());
                pipeline.addLast(new StringEncoder());
                pipeline.addLast(new TelClientHandler(endcodeOfSilent, activeFuture, TelClient.this::close, receiveDataBuffer));
            }
        });
        this.channel = b.connect(this.remoteAddress).channel();
        try {
            activeFuture.get();
            logger.info("TelClient initialize ok.");
        } catch (Throwable e) {
            if (e instanceof ExecutionException) {
                e = e.getCause();
            }
            logger.error("TelClient initialize failed -> " + e.getMessage(), e);
            this.close();
        }
    }

    @Override
    protected void doClose() {
        if (this.channel != null) {
            try {
                this.channel.close().sync();
            } catch (Exception e) { /**/ }
        }
        if (this.loopGroup != null) {
            this.loopGroup.shutdownGracefully();
        }
    }
}