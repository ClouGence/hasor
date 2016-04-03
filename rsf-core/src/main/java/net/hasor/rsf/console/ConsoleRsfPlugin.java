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
package net.hasor.rsf.console;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.bootstrap.ServerBootstrap;
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
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfPlugin;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.utils.NameThreadFactory;
/**
 * RSF终端管理器插件。
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
public class ConsoleRsfPlugin implements RsfPlugin {
    protected static Logger            logger         = LoggerFactory.getLogger(ConsoleRsfPlugin.class);
    private static final StringDecoder DECODER        = new StringDecoder();
    private static final StringEncoder ENCODER        = new StringEncoder();
    private static final TelnetHandler SERVER_HANDLER = new TelnetHandler();
    private InterAddress               bindAddress;
    private EventLoopGroup             workerGroup    = null;
    @Override
    public void loadRsf(RsfContext rsfContext) throws Throwable {
        //1.初始化常量配置。
        this.workerGroup = new NioEventLoopGroup(2, new NameThreadFactory("RSF-Console-%s"));
        int consolePort = rsfContext.getSettings().getConsolePort();
        InterAddress consoleAddress = rsfContext.bindAddress();
        try {
            this.bindAddress = new InterAddress(consoleAddress.getHost(), consolePort, consoleAddress.getFormUnit());
        } catch (Throwable e) {
            throw new UnknownHostException(e.getMessage());
        }
        logger.info("console - starting... at {}", this.bindAddress);
        //
        //2.启动Telnet。
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(workerGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.handler(new LoggingHandler(LogLevel.INFO));
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                    pipeline.addLast(DECODER);
                    pipeline.addLast(ENCODER);
                    pipeline.addLast(SERVER_HANDLER);
                }
            });
            b.bind(this.bindAddress.getHost(), this.bindAddress.getPort()).sync().await();
        } catch (Throwable e) {
            logger.error("console start failed. ->" + e.getMessage(), e);
            this.shutdown();
        }
        logger.info("console - bindSocket at {}", this.bindAddress);
        //
        //3.注册shutdown事件，以保证在shutdown时可以停止Telnet。
        Hasor.addShutdownListener(rsfContext.getEnvironment(), new EventListener<AppContext>() {
            @Override
            public void onEvent(String event, AppContext eventData) throws Throwable {
                shutdown();
            }
        });
    }
    public void shutdown() {
        if (this.workerGroup != null) {
            logger.info("console shutdown.");
            this.workerGroup.shutdownGracefully();
        }
    }
}