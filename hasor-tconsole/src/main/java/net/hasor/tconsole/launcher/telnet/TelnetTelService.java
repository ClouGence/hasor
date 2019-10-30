/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.tconsole.launcher.telnet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import net.hasor.core.AppContext;
import net.hasor.tconsole.launcher.AbstractTelService;
import net.hasor.utils.NameThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.function.Predicate;

import static net.hasor.tconsole.launcher.TelUtils.finalBindAddress;

/**
 * tConsole 服务，提供 Telnet 形式的交互界面。
 * @version : 2016年09月20日
 * @author 赵永春 (zyc@hasor.net)
 */
public class TelnetTelService extends AbstractTelService {
    protected static Logger            logger        = LoggerFactory.getLogger(TelnetTelService.class);
    private final    InetSocketAddress bindAddress;
    private final    TelNettyHandler   nettyHandler;
    private          EventLoopGroup    workerGroup   = null;
    private          Channel           telnetChannel = null;

    /**
     * 创建 tConsole 服务
     * @param bindAddress 监听的本地IP。
     * @param bindPort 监听端口
     * @param inBoundMatcher 允许联入的IP匹配器
     * @throws UnknownHostException
     */
    public TelnetTelService(String bindAddress, int bindPort, Predicate<String> inBoundMatcher) throws UnknownHostException {
        this(bindAddress, bindPort, inBoundMatcher, null);
    }

    /**
     * 创建 tConsole 服务
     * @param bindAddress 监听的本地IP。
     * @param bindPort 监听端口
     * @param inBoundMatcher 允许联入的IP匹配器
     * @throws UnknownHostException
     */
    public TelnetTelService(String bindAddress, int bindPort, Predicate<String> inBoundMatcher, AppContext appContext) throws UnknownHostException {
        this(new InetSocketAddress(finalBindAddress(bindAddress), bindPort), inBoundMatcher, appContext);
    }

    /**
     * 创建 tConsole 服务
     * @param telnetSocket 监听的本地Socket
     * @param inBoundMatcher 允许联入的IP匹配器
     */
    public TelnetTelService(InetSocketAddress telnetSocket, Predicate<String> inBoundMatcher, AppContext appContext) {
        super(appContext);
        this.bindAddress = telnetSocket;
        Predicate<String> matcher = inBoundMatcher == null ? (s -> true) : inBoundMatcher;
        this.nettyHandler = new TelNettyHandler(this, matcher);
    }

    public ByteBufAllocator getByteBufAllocator() {
        return this.telnetChannel.alloc();
    }

    @Override
    protected void doInitialize() {
        super.doInitialize();
        //
        // .初始化常量配置
        this.workerGroup = new NioEventLoopGroup(1, new NameThreadFactory("tConsole", this.classLoader));
        logger.info("tConsole -> starting... at {}", this.bindAddress);
        //
        // .启动Telnet
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(this.workerGroup, this.workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.handler(new LoggingHandler(LogLevel.INFO));
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Unpooled.wrappedBuffer(new byte[] { '\n' })));
                    pipeline.addLast(new StringDecoder());
                    pipeline.addLast(new StringEncoder());
                    pipeline.addLast(nettyHandler);
                }
            });
            this.telnetChannel = b.bind(this.bindAddress).sync().channel();
        } catch (Throwable e) {
            logger.error("tConsole -> start failed, " + e.getMessage(), e);
            this.close();
        }
        logger.info("tConsole -> - bindSocket at {}", this.bindAddress);
    }

    @Override
    protected void doClose() {
        if (this.telnetChannel != null) {
            logger.info("tConsole -> telnetChannel.close");
            this.telnetChannel.close();
            this.telnetChannel = null;
        }
        if (this.workerGroup != null) {
            logger.info("tConsole -> workerGroup.shutdownGracefully");
            this.workerGroup.shutdownGracefully();
            this.workerGroup = null;
        }
        super.doClose();
    }

    @Override
    public boolean isHost() {
        return false;
    }
}