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
package net.hasor.rsf.console.launcher;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import net.hasor.utils.future.BasicFuture;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * Simplistic telnet client.
 */
public final class TelnetClient {
    public static void execCommand(String host, int port, final String command, Map<String, String> envMap) throws Exception {
        StringWriter commands = new StringWriter();
        if (envMap != null) {
            for (String key : envMap.keySet()) {
                String val = envMap.get(key);
                commands.write("set " + key + " = " + val + " \n");
            }
        }
        commands.write("set SESSION_AFTERCLOSE = true \n");
        commands.write(command + "\n");
        //
        EventLoopGroup group = new NioEventLoopGroup();
        final BasicFuture<Object> closeFuture = new BasicFuture<Object>();
        final AtomicBoolean atomicBoolean = new AtomicBoolean(true);
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
                    pipeline.addLast(new TelnetClientHandler(closeFuture, atomicBoolean));
                }
            });
            Channel ch = b.connect(host, port).sync().channel();
            ChannelFuture lastWriteFuture = null;
            BufferedReader commandReader = new BufferedReader(new StringReader(commands.toString()));
            for (; ; ) {
                if (atomicBoolean.get()) {
                    String line = commandReader.readLine();
                    if (line == null) {
                        break;
                    }
                    if (ch.isActive()) {
                        atomicBoolean.set(false);
                        lastWriteFuture = ch.writeAndFlush(line + "\r\n");
                    }
                } else {
                    Thread.sleep(500);//等待指令的响应
                }
            }
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
        } finally {
            closeFuture.get();
            group.shutdownGracefully();
        }
    }
}
