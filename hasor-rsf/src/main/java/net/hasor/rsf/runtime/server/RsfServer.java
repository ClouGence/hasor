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
package net.hasor.rsf.runtime.server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import net.hasor.rsf.executes.NameThreadFactory;
import net.hasor.rsf.net.netty.RSFCodec;
import net.hasor.rsf.runtime.context.AbstractRsfContext;
/**
 * 负责维持与远程RSF服务器连接的客户端类。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfServer {
    private AbstractRsfContext rsfContext    = null;
    private Channel            serverChannel = null;
    private EventLoopGroup     bossGroup     = null;
    //
    public RsfServer(AbstractRsfContext rsfContext) {
        this.rsfContext = rsfContext;
    }
    //
    private String getBindAddress() throws UnknownHostException {
        return this.rsfContext.getSettings().getString("hasor.rsfConfig.address", InetAddress.getLocalHost().getHostAddress());
    }
    private int getBindPort() {
        return this.rsfContext.getSettings().getInteger("hasor.rsfConfig.port", 8000);
    }
    /**连接远程服务（具体的地址）*/
    public void start() throws UnknownHostException {
        this.start(this.getBindAddress(), this.getBindPort());
    }
    /**连接远程服务（具体的地址）*/
    public void start(int port) throws UnknownHostException {
        this.start(this.getBindAddress(), port);
    }
    /**连接远程服务（具体的地址）*/
    public void start(String localHost, int port) throws UnknownHostException {
        InetAddress address = null;
        if ("local".equals(localHost) == true) {
            address = InetAddress.getLocalHost();
        } else {
            address = InetAddress.getByName(localHost);
        }
        this.start(address, port);
    }
    /**连接远程服务（具体的地址）*/
    public void start(InetAddress localAddress, int port) {
        //
        int listenThread = this.rsfContext.getSettings().getInteger("hasor.rsfConfig.network.listenThread", 1);
        this.bossGroup = new NioEventLoopGroup(listenThread, new NameThreadFactory("RSF-Listen-%s"));
        ServerBootstrap boot = new ServerBootstrap();
        boot.group(this.bossGroup, this.rsfContext.getLoopGroup());
        boot.channel(NioServerSocketChannel.class);
        boot.childHandler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new RSFCodec(), new InnerServerHandler(RsfServer.this.rsfContext));
            }
        }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
        //
        ChannelFuture future = boot.bind(localAddress, port);
        this.serverChannel = future.channel();
    }
    /**停止服务*/
    public void shutdown() {
        bossGroup.shutdownGracefully();
        this.serverChannel.close();
    }
}