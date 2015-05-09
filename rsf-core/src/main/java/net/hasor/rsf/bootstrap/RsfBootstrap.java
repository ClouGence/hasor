/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.rsf.bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import net.hasor.core.EventListener;
import net.hasor.core.Settings;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.center.client.InstallCenterClient;
import net.hasor.rsf.protocol.netty.RSFCodec;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.rpc.context.DefaultRsfContext;
import net.hasor.rsf.rpc.context.DefaultRsfSettings;
import net.hasor.rsf.rpc.event.Events;
import net.hasor.rsf.rpc.provider.RsfProviderHandler;
import net.hasor.rsf.utils.NameThreadFactory;
import net.hasor.rsf.utils.NetworkUtils;
import net.hasor.rsf.utils.RsfRuntimeUtils;
import org.more.logger.LoggerHelper;
/**
 * Rsf启动引导程序。
 * @version : 2014年12月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfBootstrap {
    public static final String DEFAULT_RSF_CONFIG = "rsf-config.xml";
    private RsfSettings        settings           = null;
    private RsfStart           rsfStart           = null;
    private InetAddress        localAddress       = null;
    private WorkMode           workMode           = WorkMode.None;
    private int                bindSocket         = 0;
    private Runnable           shutdownHook       = null;
    //
    public RsfBootstrap bindSettings(Settings settings) throws IOException {
        if (settings == null)
            throw new NullPointerException();
        this.settings = new DefaultRsfSettings(settings);
        return this;
    }
    public RsfBootstrap doBinder(RsfStart rsfStart) {
        this.rsfStart = rsfStart;
        return this;
    }
    public RsfBootstrap workAt(WorkMode workMode) {
        if (workMode == null)
            throw new NullPointerException();
        this.workMode = workMode;
        return this;
    }
    public RsfBootstrap socketBind(int bindSocket) throws UnknownHostException {
        this.bindSocket = bindSocket;
        return this;
    }
    public RsfBootstrap socketBind(String localHost, int port) throws UnknownHostException {
        InetAddress address = null;
        if ("local".equals(localHost) == true) {
            address = InetAddress.getLocalHost();
        } else if (localHost != null) {
            address = InetAddress.getByName(localHost);
        }
        return this.socketBind(address, port);
    }
    public RsfBootstrap socketBind(InetAddress localAddress, int bindSocket) {
        this.localAddress = localAddress;
        this.bindSocket = bindSocket;
        return this;
    }
    //
    public RsfContext sync() throws Throwable {
        LoggerHelper.logInfo("initialize rsfBootstrap。");
        if (this.rsfStart == null) {
            LoggerHelper.logInfo("create RsfStart.");
            this.rsfStart = new InnerRsfStart();
        }
        if (this.settings == null) {
            this.settings = new DefaultRsfSettings(new StandardContextSettings(DEFAULT_RSF_CONFIG));
            this.settings.refresh();
        }
        //
        //RsfContext
        LoggerHelper.logInfo("agent shutdown method on DefaultRsfContext.", DEFAULT_RSF_CONFIG);
        AbstractRsfContext newRsfContext = null;
        if (this.rsfStart instanceof RsfContextCreater) {
            newRsfContext = ((RsfContextCreater) this.rsfStart).create(this.settings);
        }
        if (newRsfContext == null) {
            newRsfContext = new DefaultRsfContext(this.settings);
        }
        final AbstractRsfContext rsfContext = newRsfContext;
        //
        //localAddress & bindSocket
        InetAddress localAddress = this.localAddress;
        if (localAddress == null) {
            localAddress = NetworkUtils.finalBindAddress(rsfContext.getSettings().getBindAddress());
        }
        int bindSocket = (this.bindSocket < 1) ? this.settings.getBindPort() : this.bindSocket;
        LoggerHelper.logInfo("bind to address = %s , port = %s.", localAddress, bindSocket);
        //
        InterAddress centerAddress = new InterAddress(localAddress.getHostAddress(), bindSocket, "");
        InstallCenterClient.initCenter(rsfContext, centerAddress);
        //
        //Shutdown Event
        rsfContext.getEventContext().addListener(Events.Shutdown, new EventListener() {
            public void onEvent(String event, Object[] params) throws Throwable {
                LoggerHelper.logInfo("shutdown rsf.");
                if (shutdownHook != null) {
                    LoggerHelper.logInfo("shutdownHook run.");
                    shutdownHook.run();
                }
            }
        });
        if (this.workMode == WorkMode.Customer) {
            return doBinder(rsfContext);
        }
        //
        //Netty
        final InterAddress hostAddress = new InterAddress(localAddress.getHostAddress(), bindSocket, "local");
        final NioEventLoopGroup bossGroup = new NioEventLoopGroup(this.settings.getNetworkListener(), new NameThreadFactory("RSF-Listen-%s"));
        ServerBootstrap boot = new ServerBootstrap();
        boot.group(bossGroup, rsfContext.getWorkLoopGroup());
        boot.channel(NioServerSocketChannel.class);
        boot.childHandler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                Channel channel = ch.pipeline().channel();
                RsfRuntimeUtils.setAddress(hostAddress, channel);
                //
                ch.pipeline().addLast(new RSFCodec(), new RsfProviderHandler(rsfContext));
            }
        }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture future = boot.bind(localAddress, bindSocket);
        final Channel serverChannel = future.channel();
        LoggerHelper.logInfo("rsf Server started at :%s:%s", localAddress, bindSocket);
        //
        //shutdownHook
        this.shutdownHook = new Runnable() {
            public void run() {
                LoggerHelper.logInfo("shutdown rsf server.");
                bossGroup.shutdownGracefully();
                try {
                    serverChannel.close().sync();
                } catch (InterruptedException e) {
                    LoggerHelper.logSevere(e.getMessage(), e);
                }
            }
        };
        //
        //doBinder
        return doBinder(rsfContext);
    }
    private RsfContext doBinder(AbstractRsfContext rsfContext) throws Throwable {
        rsfContext.getEventContext().fireSyncEvent(Events.StartUp, rsfContext);
        //
        LoggerHelper.logInfo("do RsfBinder.");
        this.rsfStart.onBind(rsfContext.getBindCenter().getRsfBinder());
        LoggerHelper.logInfo("rsf work at %s.", this.workMode);
        //
        return rsfContext;
    }
}