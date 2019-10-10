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
import net.hasor.utils.future.BasicFuture;
import net.hasor.utils.future.FutureCallback;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * simple telnet client.
 */
public class TelClient extends AbstractContainer {
    private final BasicFuture<Object> closeFuture;
    private final InetSocketAddress   remoteAddress;
    private       EventLoopGroup      loopGroup;
    private       Channel             channel;
    private final ByteBuf             receiveDataBuffer;
    private       String              endcodeOfSilent;

    public TelClient(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
        this.closeFuture = new BasicFuture<>(new FutureCallback<Object>() {
            @Override
            public void completed(Object result) {
                TelClient.this.close();
            }

            @Override
            public void failed(Throwable ex) {
                TelClient.this.close();
            }
        });
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
        int waitLength = this.endcodeOfSilent.length();
        this.channel.writeAndFlush(message.trim() + "\n");
        while (this.isInit()) {
            this.receiveDataBuffer.resetReaderIndex();
            // 滑动窗口的机制
            int loopCount = this.receiveDataBuffer.readableBytes() - waitLength;
            if (loopCount > 0) {
                for (int i = 0; i < loopCount; i++) {
                    String dat = this.receiveDataBuffer.getCharSequence(i, waitLength, StandardCharsets.UTF_8).toString();
                    if (dat.equals(this.endcodeOfSilent)) {
                        dat = this.receiveDataBuffer.getCharSequence(0, i, StandardCharsets.UTF_8).toString();
                        this.receiveDataBuffer.clear();// 完全释放
                        this.receiveDataBuffer.discardReadBytes();
                        return dat.trim();
                    }
                }
            }
            Thread.sleep(10);
        }
        //
        this.receiveDataBuffer.resetReaderIndex();
        return this.receiveDataBuffer.toString(StandardCharsets.UTF_8);
    }

    @Override
    protected void doInitialize() {
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
                pipeline.addLast(new TelClientHandler(endcodeOfSilent, closeFuture, receiveDataBuffer));
            }
        });
        this.channel = b.connect(this.remoteAddress).syncUninterruptibly().channel();
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