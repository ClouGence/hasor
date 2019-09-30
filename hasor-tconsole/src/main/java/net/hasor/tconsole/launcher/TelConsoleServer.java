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
package net.hasor.tconsole.launcher;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import net.hasor.core.container.AbstractContainer;
import net.hasor.core.spi.SpiTrigger;
import net.hasor.tconsole.TelContext;
import net.hasor.tconsole.TelExecutor;
import net.hasor.utils.NameThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Predicate;

import static net.hasor.tconsole.launcher.TelUtils.finalBindAddress;

/**
 * tConsole 服务
 * @version : 20169年09月20日
 * @author 赵永春 (zyc@hasor.net)
 */
public class TelConsoleServer extends AbstractContainer implements TelContext {
    protected static Logger                   logger        = LoggerFactory.getLogger(TelConsoleServer.class);
    private          ClassLoader              classLoader   = null;
    //
    private final    InetSocketAddress        bindAddress;
    private final    TelNettyHandler          nettyHandler;
    private          EventLoopGroup           workerGroup   = null;
    private          Channel                  telnetChannel = null;
    private          ScheduledExecutorService executor      = null;

    /**
     * 创建 tConsole 服务
     * @param bindAddress 监听的本地IP。
     * @param bindPort 监听端口
     * @param inBoundMatcher 允许联入的IP匹配器
     * @throws UnknownHostException
     */
    public TelConsoleServer(String bindAddress, int bindPort, Predicate<String> inBoundMatcher) throws UnknownHostException {
        this.bindAddress = new InetSocketAddress(finalBindAddress(bindAddress), bindPort);
        this.nettyHandler = new TelNettyHandler(this, inBoundMatcher);
    }

    public ClassLoader getClassLoader() {
        return (this.classLoader == null) ? Thread.currentThread().getContextClassLoader() : this.classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    protected void doInitialize() {
        // .执行线程池
        ClassLoader classLoader = getClassLoader();
        String shortName = "tConsole-Work";
        int workSize = 2;
        this.executor = Executors.newScheduledThreadPool(workSize, new NameThreadFactory(shortName, classLoader));
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) this.executor;
        threadPool.setCorePoolSize(workSize);
        threadPool.setMaximumPoolSize(workSize);
        logger.info("tConsole -> create TelnetHandler , threadShortName={} , workThreadSize = {}.", shortName, workSize);
        //
        // .初始化常量配置
        this.workerGroup = new NioEventLoopGroup(1, new NameThreadFactory("tConsole", classLoader));
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
                    pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
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
        logger.info("tConsole -> shutdown.");
        if (this.telnetChannel != null) {
            this.telnetChannel.close();
            this.telnetChannel = null;
        }
        if (this.workerGroup != null) {
            this.workerGroup.shutdownGracefully();
            this.workerGroup = null;
        }
        if (this.executor != null) {
            this.executor.shutdownNow();
            this.executor = null;
        }
    }

    @Override
    public TelExecutor findCommand(String cmdName) {
        return null;
    }

    @Override
    public List<String> getCommandNames() {
        return null;
    }

    @Override
    public ByteBufAllocator getByteBufAllocator() {
        return ByteBufAllocator.DEFAULT;
    }

    @Override
    public SpiTrigger getSpiTrigger() {
        return null;
    }

    @Override
    public void asyncExecute(Runnable runnable) {
        this.executor.execute(runnable);
    }
}