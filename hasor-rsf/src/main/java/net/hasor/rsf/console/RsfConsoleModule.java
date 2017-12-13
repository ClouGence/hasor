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
import io.netty.bootstrap.ServerBootstrap;
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
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.Hasor;
import net.hasor.core.LifeModule;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfApiBinder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfModule;
import net.hasor.rsf.domain.RsfConstants;
import net.hasor.utils.NameThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
/**
 * RSF终端管理器插件。
 * @version : 2016年2月18日
 * @author 赵永春 (zyc@hasor.net)
 */
public class RsfConsoleModule extends RsfModule implements LifeModule {
    protected static Logger         logger        = LoggerFactory.getLogger(RsfConstants.LoggerName_Console);
    private          Channel        telnetChannel = null;
    private          EventLoopGroup workerGroup   = null;
    //
    @Override
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        logger.info("rsfConsole -> init consoleModule.");
        //
        final Set<Class<?>> rsfCommandSet = new HashSet<Class<?>>(apiBinder.getEnvironment().findClass(RsfCommand.class));
        rsfCommandSet.remove(RsfCommand.class);
        if (rsfCommandSet.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("event -> init failed , not found any @RsfCommand.");
            }
            return;
        }
        for (final Class<?> commandClass : rsfCommandSet) {
            if (commandClass == RsfCommand.class || !RsfInstruct.class.isAssignableFrom(commandClass)) {
                continue;
            }
            if (!commandClass.getPackage().isAnnotationPresent(RsfSearchInclude.class)) {
                continue;
            }
            logger.info("rsfConsole -> new order {}.", commandClass);
            apiBinder.bindType(RsfInstruct.class).uniqueName().to((Class<? extends RsfInstruct>) commandClass);
        }
        //
    }
    @Override
    public void onStart(AppContext appContext) {
        final RsfContext rsfContext = appContext.getInstance(RsfContext.class);
        if (rsfContext == null) {
            logger.error("rsfConsole -> RsfContext is null.");
            return;
        }
        //1.初始化常量配置。
        this.workerGroup = new NioEventLoopGroup(1, new NameThreadFactory("RSF-Console", appContext.getClassLoader()));
        int consolePort = rsfContext.getSettings().getConsolePort();
        String consoleAddress = rsfContext.getSettings().getBindAddress();
        String formUnit = rsfContext.getSettings().getUnitName();
        InterAddress bindAddress = new InterAddress(consoleAddress, consolePort, formUnit);
        logger.info("rsfConsole -> starting... at {}", bindAddress);
        //
        //2.启动Telnet。
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
                    pipeline.addLast(new TelnetHandler(rsfContext));
                }
            });
            this.telnetChannel = b.bind(bindAddress.getHost(), bindAddress.getPort()).sync().channel();
        } catch (Throwable e) {
            logger.error("rsfConsole -> start failed, " + e.getMessage(), e);
            this.shutdown();
        }
        logger.info("rsfConsole -> - bindSocket at {}", bindAddress);
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
        if (this.telnetChannel == null && this.workerGroup == null) {
            return;
        }
        logger.info("rsfConsole -> shutdown.");
        if (this.telnetChannel != null) {
            this.telnetChannel.close();
        }
        if (this.workerGroup != null) {
            this.workerGroup.shutdownGracefully();
        }
    }
    @Override
    public void onStop(AppContext appContext) {
    }
}