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
package net.hasor.rsf.center.server.launcher.telnet;
import java.io.BufferedReader;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
/**
 * Simplistic telnet client.
 */
public final class TelnetClient {
    public static void execCommand(String host, int port, final BufferedReader commandReader) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b = b.group(group);
            b = b.channel(NioSocketChannel.class);
            b = b.handler(new ChannelInitializer<SocketChannel>() {
                public void initChannel(SocketChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                    pipeline.addLast(new StringDecoder());
                    pipeline.addLast(new StringEncoder());
                    pipeline.addLast(new TelnetClientHandler());
                }
            });
            Channel ch = b.connect(host, port).sync().channel();
            ChannelFuture lastWriteFuture = null;
            for (;;) {
                String line = commandReader.readLine();
                if (line == null) {
                    break;
                }
                lastWriteFuture = ch.writeAndFlush(line + "\r\n");
            }
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
        } finally {
            group.shutdownGracefully();
        }
    }
}
