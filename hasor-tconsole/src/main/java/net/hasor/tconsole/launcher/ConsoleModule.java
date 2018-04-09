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
package net.hasor.tconsole.launcher;
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
import net.hasor.core.*;
import net.hasor.tconsole.CommandFinder;
import net.hasor.tconsole.ConsoleApiBinder;
import net.hasor.tconsole.commands.GetSetExecutor;
import net.hasor.tconsole.commands.HelpExecutor;
import net.hasor.tconsole.commands.QuitExecutor;
import net.hasor.utils.NameThreadFactory;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
/**
 * RSF终端管理器插件。
 * @version : 2016年2月18日
 * @author 赵永春 (zyc@hasor.net)
 */
public class ConsoleModule implements LifeModule {
    protected static Logger            logger         = LoggerFactory.getLogger(ConsoleModule.class);
    private          boolean           enable         = true;
    private          Channel           telnetChannel  = null;
    private          EventLoopGroup    workerGroup    = null;
    private          InetSocketAddress bindAddress    = null;
    private          String[]          consoleInBound = null;
    //
    private static InetAddress finalBindAddress(String hostString) throws UnknownHostException {
        return "local".equalsIgnoreCase(hostString) ? InetAddress.getLocalHost() : InetAddress.getByName(hostString);
    }
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        this.enable = apiBinder.tryCast(ConsoleApiBinder.class).isEnable();
        if (!this.enable) {
            logger.info("tConsole is disable.");
            return;
        }
        //
        Settings settings = apiBinder.getEnvironment().getSettings();
        int consolePort = settings.getInteger("hasor.tConsole.bindPort", 2180);
        String hostString = settings.getString("hasor.tConsole.bindAddress", "0.0.0.0");
        this.bindAddress = new InetSocketAddress(finalBindAddress(hostString), consolePort);
        //
        String consoleInBoundStr = settings.getString("hasor.tConsole.inBound", "local,127.0.0.1");
        ArrayList<String> addressList = new ArrayList<String>();
        if (StringUtils.isNotBlank(consoleInBoundStr)) {
            for (String item : consoleInBoundStr.split(",")) {
                String itemTrim = item.trim();
                if (StringUtils.isNotBlank(itemTrim)) {
                    try {
                        if ("local".equalsIgnoreCase(itemTrim)) {
                            addressList.add(finalBindAddress("local").getHostAddress());
                        } else {
                            addressList.add(itemTrim);
                        }
                    } catch (Exception e) {
                        logger.error("tConsole - inBound address " + itemTrim + " error " + e.getMessage(), e);
                    }
                }
            }
        }
        if (addressList.isEmpty()) {
            try {
                addressList.add(finalBindAddress("local").getHostAddress());
            } catch (Exception e) {
                addressList.add("127.0.0.1");
            }
        }
        this.consoleInBound = addressList.toArray(new String[addressList.size()]);
        //
        apiBinder.bindType(CommandFinder.class).toInstance(Hasor.autoAware(apiBinder.getEnvironment(), new Manager()));
        ConsoleApiBinder consoleBinder = apiBinder.tryCast(ConsoleApiBinder.class);
        consoleBinder.addCommand(new String[] { "set", "get" }, new GetSetExecutor());
        consoleBinder.addCommand(new String[] { "help", "man" }, new HelpExecutor());
        consoleBinder.addCommand(new String[] { "quit", "exit" }, new QuitExecutor());
    }
    @Override
    public void onStart(final AppContext appContext) {
        if (!this.enable) {
            return;
        }
        //
        if (appContext == null) {
            logger.error("tConsole -> AppContext is null.");
            return;
        }
        //
        // .初始化常量配置。
        this.workerGroup = new NioEventLoopGroup(1, new NameThreadFactory("tConsole", appContext.getClassLoader()));
        logger.info("tConsole -> starting... at {}", this.bindAddress);
        //
        // .启动Telnet。
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
                    pipeline.addLast(new TelnetHandler(appContext.getInstance(CommandFinder.class), consoleInBound));
                }
            });
            this.telnetChannel = b.bind(this.bindAddress).sync().channel();
        } catch (Throwable e) {
            logger.error("tConsole -> start failed, " + e.getMessage(), e);
            this.onStop(appContext);
        }
        logger.info("tConsole -> - bindSocket at {}", this.bindAddress);
        //
        // .注册shutdown事件，以保证在shutdown时可以停止Telnet。
        Hasor.addShutdownListener(appContext.getEnvironment(), new EventListener<AppContext>() {
            @Override
            public void onEvent(String event, AppContext eventData) throws Throwable {
                onStop(appContext);
            }
        });
    }
    @Override
    public void onStop(AppContext appContext) {
        if (this.telnetChannel == null && this.workerGroup == null) {
            return;
        }
        logger.info("tConsole -> shutdown.");
        if (this.telnetChannel != null) {
            this.telnetChannel.close();
        }
        if (this.workerGroup != null) {
            this.workerGroup.shutdownGracefully();
        }
    }
}