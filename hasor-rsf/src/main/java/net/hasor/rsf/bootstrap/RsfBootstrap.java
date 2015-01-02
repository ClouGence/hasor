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
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import net.hasor.core.Hasor;
import net.hasor.core.Settings;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.remoting.transport.connection.NetworkConnection;
import net.hasor.rsf.remoting.transport.netty.RSFCodec;
import net.hasor.rsf.remoting.transport.provider.RsfProviderHandler;
import net.hasor.rsf.rpc.context.DefaultRsfContext;
import net.hasor.rsf.rpc.context.DefaultRsfSettings;
import net.hasor.rsf.rpc.executes.NameThreadFactory;
import org.more.util.StringUtils;
/**
 * Rsf启动引导程序。
 * @version : 2014年12月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfBootstrap {
    private RsfSettings settings     = null;
    private RsfStart    rsfStart     = null;
    private InetAddress localAddress = null;
    private WorkMode    workMode     = WorkMode.None;
    private int         bindSocket   = -1;
    //
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
        } else {
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
    private InetAddress finalBindAddress(RsfSettings rsfSettings) throws UnknownHostException {
        String bindAddress = rsfSettings.getBindAddress();
        return StringUtils.equalsIgnoreCase("local", bindAddress) ? InetAddress.getLocalHost() : InetAddress.getByName(bindAddress);
    }
    public RsfContext sync() throws IOException, URISyntaxException {
        if (this.rsfStart == null) {
            this.rsfStart = new RsfStart();
        }
        if (this.settings == null) {
            this.settings = new DefaultRsfSettings(new StandardContextSettings("rsf-config.xml"));
        }
        //
        //RsfContext
        final DefaultRsfContext rsfContext = new DefaultRsfContext(this.settings);
        if (this.workMode == WorkMode.Customer) {
            return rsfContext;
        }
        //
        //localAddress & bindSocket
        InetAddress localAddress = this.localAddress;
        if (localAddress == null) {
            localAddress = finalBindAddress(this.settings);
        }
        int bindSocket = (this.bindSocket < 1) ? this.settings.getBindPort() : this.bindSocket;
        //Netty
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(this.settings.getNetworkListener(), new NameThreadFactory("RSF-Listen-%s"));
        ServerBootstrap boot = new ServerBootstrap();
        boot.group(bossGroup, rsfContext.getLoopGroup());
        boot.channel(NioServerSocketChannel.class);
        boot.childHandler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                Channel channel = ch.pipeline().channel();
                NetworkConnection.initConnection(channel);
                //
                ch.pipeline().addLast(new RSFCodec(), new RsfProviderHandler(rsfContext));
            }
        }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture future = boot.bind(localAddress, bindSocket);
        Channel serverChannel = future.channel();
        Hasor.logInfo("rsf Server started at :%s:%s", localAddress, bindSocket);
        //add
        bossGroup.shutdownGracefully();s
        serverChannel.close();
        //
        return rsfContext;
    }
}